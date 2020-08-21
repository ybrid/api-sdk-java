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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Driver extends io.ybrid.api.driver.common.Driver {
    static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    private static final Capability[] initialCapabilities = {Capability.PLAYBACK_URL};
    private final Bouquet bouquet = new Factory().getBouquet(session.getServer(), session.getAlias());
    private Metadata metadata;
    private PlayoutInfo playoutInfo;

    public Driver(Session session) {
        super(session);

        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_POST_BODY_AS_QUERY_STRING);

        this.currentService = bouquet.getDefaultService();

        capabilities.add(initialCapabilities);

        setChanged(SubInfo.BOUQUET);
    }

    protected JSONObject request(@NotNull String command, @Nullable Map<String, String> parameters) throws IOException {
        Server server = session.getServer();
        String hostname = this.hostname;
        String path = getMountpoint() + "/ctrl/" + command;

        if (token != null) {
            if (parameters == null) {
                parameters = new HashMap<>();
            } else {
                parameters = new HashMap<>(parameters);
            }
            parameters.put("sessionId", token);
        }

        if (hostname == null)
            hostname = server.getHostname();

        final URL url = new URL(server.getProtocol(), hostname, server.getPort(), path);
        return request(url, parameters);
    }

    protected JSONObject request(@NotNull String command) throws IOException {
        return request(command, null);
    }


    @Override
    public @NotNull Bouquet getBouquet() {
        return bouquet;
    }

    @Override
    public void swapItem(@NotNull SwapMode mode) throws IOException {
        final Map<String, String> parameters;

        assertConnected();

        parameters = new HashMap<>();
        parameters.put("mode", mode.getOnWire());

        request("swap", parameters);
    }

    private void updateMetadata() throws IOException {
        final JSONObject json;

        assertConnected();

        json = request("show-meta");
        if (json == null)
            throw new IOException("No valid reply from server");

        metadata = new Metadata((Service) getCurrentService(), json);
        setChanged(SubInfo.METADATA);
        setChanged(SubInfo.BOUQUET);

        if (json.has("swapInfo")) {
            final SwapInfo swapInfo = new SwapInfo(json.getJSONObject("swapInfo"));
            final long timeToNextItem;

            if (json.has("timeToNextItemMillis")) {
                timeToNextItem = json.getLong("timeToNextItemMillis");
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
    public void refresh(@NotNull SubInfo what) throws IOException {
        if (what == SubInfo.VALIDITY) {
            updateValidity();
        } else {
            updateMetadata();
        }
    }

    @Override
    public void refresh(@NotNull EnumSet<SubInfo> what) throws IOException {
        if (what.isEmpty())
            return;

        if (what.contains(SubInfo.VALIDITY)) {
            updateValidity();
            if (what.size() > 1) {
                updateMetadata();
            }
        } else {
            updateMetadata();
        }
    }

    @Override
    public io.ybrid.api.metadata.@NotNull Metadata getMetadata() {
        return metadata;
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() {
        return playoutInfo;
    }

    @Override
    public URI getStreamURI() throws MalformedURLException, URISyntaxException {
        Server server = session.getServer();

        assertConnected();

        //noinspection SpellCheckingInspection
        return new URI(server.isSecure() ? "icyxs" : "icyx", null, hostname, server.getPort(), getMountpoint(), "sessionId=" + token, null);
    }

    @Override
    public void connect() throws IOException {
        final @NotNull WorkaroundMap workarounds = session.getActiveWorkarounds();
        JSONObject response;
        String hostname;
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

        if (workarounds.get(Workaround.WORKAROUND_BAD_FQDN) == TriState.TRUE) {
            hostname = null;
        } else {
            hostname = response.getString("host");

            if (hostname != null) {
                if (workarounds.get(Workaround.WORKAROUND_BAD_FQDN) == TriState.AUTOMATIC) {
                    if (!isValidFQDN(hostname)) {
                        LOGGER.log(Level.SEVERE, "Invalid hostname from server: " + hostname);
                        hostname = null;
                        workarounds.enable(Workaround.WORKAROUND_BAD_FQDN);
                    }
                }
            }
        }

        /* We did not get anything useful from the server */
        if (hostname == null)
            hostname = session.getServer().getHostname();

        this.hostname = hostname;

        connected = true;
    }

}
