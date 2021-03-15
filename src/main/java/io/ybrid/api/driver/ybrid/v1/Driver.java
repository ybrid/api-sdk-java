/*
 * Copyright (c) 2019 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ybrid.api.driver.ybrid.v1;

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.SimpleService;
import io.ybrid.api.driver.ybrid.common.Metadata;
import io.ybrid.api.driver.ybrid.common.SwapInfo;
import io.ybrid.api.metadata.InvalidMetadata;
import io.ybrid.api.metadata.Sync;
import io.ybrid.api.session.Command;
import io.ybrid.api.transaction.Request;
import io.ybrid.api.util.TriState;
import io.ybrid.api.util.Utils;
import io.ybrid.api.util.uri.Builder;
import io.ybrid.api.util.uri.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Driver extends io.ybrid.api.driver.common.Driver {
    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    private io.ybrid.api.metadata.Metadata metadata;
    private PlayoutInfo playoutInfo;
    private @NotNull Builder baseURI;

    public Driver(Session session) {
        super(session);

        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_POST_BODY_AS_QUERY_STRING);

        try {
            this.baseURI = new Builder(session.getMediaEndpoint().getURI());
            this.baseURI.setServer(session.getServer());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        this.currentService = new SimpleService();
        metadata = new InvalidMetadata(this.currentService);

        setChanged(SubInfo.BOUQUET);
        setChanged(SubInfo.METADATA);
    }

    protected JSONObject request(@NotNull String command, @Nullable Map<String, String> parameters) throws IOException {
        final @NotNull Builder builder = baseURI.clone();

        try {
            builder.appendPath(new Path("/ctrl"));
            builder.appendPath(new Path("/" + command));
        } catch (URISyntaxException e) {
            throw new IOException(e);
        }

        if (token != null) {
            if (parameters == null) {
                parameters = new HashMap<>();
            } else {
                parameters = new HashMap<>(parameters);
            }
            parameters.put("sessionId", token);
        }

        return request(builder.toURL(), parameters);
    }

    protected JSONObject request(@NotNull String command) throws IOException {
        return request(command, null);
    }


    private void updateMetadata() throws IOException, URISyntaxException {
        final JSONObject json;

        assertConnected();

        json = request("show-meta");
        if (json == null)
            throw new IOException("No valid reply from server");

        metadata = new Metadata(getCurrentService(), json);
        setChanged(SubInfo.METADATA);
        setChanged(SubInfo.BOUQUET);

        if (json.has("swapInfo")) {
            final SwapInfo swapInfo = new SwapInfo(json.getJSONObject("swapInfo"));
            long timeToNextItem;

            if (json.has("timeToNextItemMillis")) {
                timeToNextItem = json.getLong("timeToNextItemMillis");
                if (timeToNextItem < -1) {
                    if (session.getActiveWorkarounds().get(Workaround.WORKAROUND_NEGATIVE_TIME_TO_NEXT_ITEM).toBool(true)) {
                        LOGGER.warning("Invalid \"timeToNextItemMillis\" from server: " + timeToNextItem + ", working around by arbitrarily assuming 512ms");
                        session.getActiveWorkarounds().enable(Workaround.WORKAROUND_NEGATIVE_TIME_TO_NEXT_ITEM);
                        timeToNextItem = 512;
                    } else {
                        LOGGER.warning("Invalid \"timeToNextItemMillis\" from server: " + timeToNextItem + ", workaround disabled");
                    }
                }
            } else {
                timeToNextItem = -1;
            }

            if (swapInfo.canSwap()) {
                capabilities.add(Capability.SWAP_ITEM);
            } else {
                capabilities.remove(Capability.SWAP_ITEM);
            }
            setChanged(SubInfo.CAPABILITIES);

            playoutInfo = new io.ybrid.api.driver.common.PlayoutInfo(swapInfo, Duration.ofMillis(timeToNextItem), null);
            setChanged(SubInfo.PLAYOUT);
        }
    }

    private void updateValidity() throws IOException {
        final JSONObject json;
        final @NotNull Map<String, String> parameters = new HashMap<>();

        assertConnected();

        parameters.put("sessionToCheckId", token);
        json = request("is-session-valid", parameters);
        if (json == null)
            throw new IOException("No valid reply from server");

        if (!json.getBoolean("valid"))
            setInvalid();
    }

    @Override
    public void executeRequest(@NotNull Request<Command> request) throws Exception {
        switch (request.getCommand()) {
            case CONNECT:
                connect();
                break;
            case REFRESH: {
                final @NotNull Object arg = request.getArgumentNotNull(0);
                final @NotNull EnumSet<SubInfo> infos;

                if (arg instanceof Sync) {
                    infos = EnumSet.of(SubInfo.METADATA, SubInfo.PLAYOUT);
                } else {
                    //noinspection unchecked
                    infos = EnumSet.copyOf((EnumSet<SubInfo>) arg);

                    infos.remove(SubInfo.BOUQUET);

                    if (infos.isEmpty())
                        return;

                }

                if (infos.contains(SubInfo.VALIDITY)) {
                    updateValidity();
                    if (infos.size() > 1) {
                        updateMetadata();
                    }
                } else {
                    updateMetadata();
                }

                if (arg instanceof Sync) {
                    final @NotNull Sync.Builder builder = new Sync.Builder(session.getSource(), (Sync)arg);

                    builder.autoFill();
                    builder.setCurrentTrack(metadata.getCurrentItem());
                    builder.setNextTrack(metadata.getNextItem());
                    builder.setTemporalValidity(getPlayoutInfo().getTemporalValidity());

                    session.getMetadataMixer().accept(builder.build());
                }
                break;
            }
            case SWAP_ITEM:
                final Map<String, String> parameters;

                assertConnected();

                parameters = new HashMap<>();
                parameters.put("mode", ((SwapMode)request.getArgumentNotNull(0)).getOnWire());

                request("swap", parameters);
            break;
            default:
                super.executeRequest(request);
        }
    }

    @Override
    public @NotNull Bouquet getBouquet() {
        return new Bouquet(getCurrentService());
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() {
        return playoutInfo;
    }

    @Override
    public @NotNull URI getStreamURI() throws MalformedURLException, URISyntaxException {
        final @NotNull Builder builder = baseURI.clone();
        builder.setRawScheme(session.getServer().isSecure() ? "icyxs" : "icyx");
        builder.setQuery("sessionId", token);
        return builder.toURI();
    }

    public void connect() throws IOException {
        final @NotNull WorkaroundMap workarounds = session.getActiveWorkarounds();
        JSONObject response;
        String token;

        if (isConnected())
            return;

        response = request("create-session");
        if (response == null)
            throw new IOException("No valid response from server. BAD.");

        token = response.getString("sessionId");
        if (token == null)
            throw new IOException("No SessionID from server. BAD.");

        this.token = token;

        if (workarounds.get(Workaround.WORKAROUND_BAD_FQDN) != TriState.TRUE) {
            try {
                @NotNull Builder base;
                @NotNull String key;

                try {
                    key = "baseURI";
                    base = new Builder(response.getString(key));
                } catch (JSONException | NullPointerException e) {
                    key = "baseURL";
                    base = new Builder(response.getString(key));
                }

                if (workarounds.get(Workaround.WORKAROUND_BAD_FQDN) == TriState.AUTOMATIC) {
                    if (!Utils.isValidFQDN(Objects.requireNonNull(base.getRawHostname()))) {
                        LOGGER.log(Level.SEVERE, "Invalid hostname from server (" + key + "): " + base.getRawHostname());
                        throw new RuntimeException();
                    }
                }

                this.baseURI = base;
                LOGGER.info("Got new baseURI from server (" + key + "): " + this.baseURI.toURIString());
            } catch (Throwable k) {
                String hostname = response.getString("host");

                if (hostname != null) {
                    if (workarounds.get(Workaround.WORKAROUND_BAD_FQDN) == TriState.AUTOMATIC) {
                        if (!Utils.isValidFQDN(hostname)) {
                            LOGGER.log(Level.SEVERE, "Invalid hostname from server: " + hostname);
                            hostname = null;
                            workarounds.enable(Workaround.WORKAROUND_BAD_FQDN);
                        }
                    }
                }

                /* We did not get anything useful from the server */
                if (hostname != null) {
                    try {
                        baseURI.setRawHostname(hostname);
                        LOGGER.info("Got new baseURI from server (host): " + this.baseURI.toURIString());
                    } catch (URISyntaxException e) {
                        throw new IOException(e);
                    }
                }
            }
        }

        connected = true;
        capabilities.add(Capability.AUDIO_TRANSPORT);
    }

}
