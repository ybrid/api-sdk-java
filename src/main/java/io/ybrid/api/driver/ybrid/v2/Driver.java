/*
 * Copyright (c) 2020 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.driver.ybrid.v2;

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.ItemType;
import io.ybrid.api.metadata.Sync;
import io.ybrid.api.session.Request;
import io.ybrid.api.util.ClockManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

final class Driver extends io.ybrid.api.driver.common.Driver {
    private static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    private static final String COMMAND_SESSION_CREATE = "session/create";
    private static final String COMMAND_SESSION_CLOSE = "session/close";
    private static final String COMMAND_SESSION_INFO = "session/info";
    private static final String COMMAND_PLAYOUT_SWAP_ITEM = "playout/swap/item";
    private static final String COMMAND_PLAYOUT_BACK_TO_MAIN = "playout/back-to-main";
    private static final String COMMAND_PLAYOUT_SWAP_SERVICE = "playout/swap/service";
    private static final String COMMAND_PLAYOUT_WIND = "playout/wind";
    private static final String COMMAND_PLAYOUT_WIND_BACK_TO_LIVE = "playout/wind/back-to-live";
    private static final String COMMAND_PLAYOUT_SKIP_FORWARDS = "playout/skip/forwards";
    private static final String COMMAND_PLAYOUT_SKIP_BACKWARDS = "playout/skip/backwards";

    private static final Duration MINIMUM_BETWEEN_SESSION_INFO = Duration.ofMillis(300);

    private final State state;

    public Driver(@NotNull Session session) {
        super(session);
        state = new State(session, session.getMediaEndpoint().getURI());

        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_POST_BODY_AS_QUERY_STRING);
        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_BAD_PACKED_RESPONSE);
    }

    private URL getUrl(@Nullable String suffix) throws MalformedURLException {
        final @NotNull URL baseUrl = state.getBaseURI().toURL();

        if (suffix == null || suffix.isEmpty())
            return baseUrl;

        return new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), baseUrl.getFile() + suffix);
    }

    @Override
    public URI getStreamURI() {
        return state.getPlaybackURI();
    }

    private void handleUpdates() {
        if (state.hasChanged(SubInfo.BOUQUET)) {
            Bouquet bouquet = state.getBouquet();
            if (bouquet.getServices().size() > 1) {
                capabilities.add(Capability.SWAP_SERVICE);
            } else {
                capabilities.remove(Capability.SWAP_SERVICE);
            }
            setChanged(SubInfo.CAPABILITIES);
            setChanged(SubInfo.BOUQUET);
        }

        if (state.hasChanged(SubInfo.PLAYOUT)) {
            PlayoutInfo playoutInfo = state.getPlayoutInfo();
            if (playoutInfo.getSwapInfo().canSwap()) {
                capabilities.add(Capability.SWAP_ITEM);
            } else {
                capabilities.remove(Capability.SWAP_ITEM);
            }

            if (!Objects.requireNonNull(playoutInfo.getBehindLive()).isZero()) {
                capabilities.add(Capability.WIND_TO_LIVE);
                capabilities.add(Capability.SKIP_FORWARDS);
            } else {
                capabilities.remove(Capability.WIND_TO_LIVE);
                capabilities.remove(Capability.SKIP_FORWARDS);
            }
            setChanged(SubInfo.CAPABILITIES);
            setChanged(SubInfo.PLAYOUT);
        }

        if (state.hasChanged(SubInfo.METADATA))
            setChanged(SubInfo.METADATA);
    }

    @Nullable
    protected Response v2request(@NotNull String command, @Nullable Map<String, String> parameters) throws IOException {
        Response response = null;

        if (token != null) {
            if (parameters == null) {
                parameters = new HashMap<>();
            } else {
                parameters = new HashMap<>(parameters);
            }
            parameters.put("session-id", token);
        }


        try {
            final @NotNull URL url;

            if (command.startsWith("http://") || command.startsWith("https://")) {
                url = new URL(command);
            } else {
                url = getUrl("/ctrl/v2/" + command);
            }

            response = new Response(Objects.requireNonNull(request(url, parameters)));

            try {
                state.accept(response);

                if (!response.getValid())
                    setInvalid();
            } catch (Exception e) {
                if (session.getActiveWorkarounds().get(Workaround.WORKAROUND_BAD_PACKED_RESPONSE).toBool(false)) {
                    LOGGER.warning("Invalid response from server but ignored by enabled WORKAROUND_BAD_PACKED_RESPONSE");
                } else {
                    LOGGER.severe("Invalid response from server.");
                    throw e;
                }
            }
        } catch (NullPointerException ignored) {
        }

        handleUpdates();
        return response;
    }

    @Nullable
    protected Response v2request(@NotNull String command) throws IOException {
        return v2request(command, null);
    }

    protected boolean shouldRequestSessionInfo(@NotNull SubInfo what) {
        Instant lastUpdate;

        assertConnected();

        lastUpdate = state.getLastUpdated(what);
        return lastUpdate == null || !lastUpdate.plus(MINIMUM_BETWEEN_SESSION_INFO).isAfter(ClockManager.now());
    }

    public void connect() throws IOException {
        final @Nullable Response response;

        if (isConnected())
            return;

        if (!isValid())
            throw new IOException("Session is not valid.");

        if (state.getBaseURI().getPath().contains("/ctrl/v2/session/info")) {
            response = v2request(state.getBaseURI().toString());
        } else {
            response = v2request(COMMAND_SESSION_CREATE);
        }

        if (response == null)
            throw new IOException("No valid response from server. BAD.");

        token = response.getToken();

        connected = true;

        capabilities.add(Capability.AUDIO_TRANSPORT);
        capabilities.add(Capability.SKIP_BACKWARDS);
        setChanged(SubInfo.CAPABILITIES);
    }

    @Override
    @Contract(pure = true)
    public @NotNull Service getCurrentService() {
        return state.getCurrentService();
    }

    @Override
    public @NotNull Bouquet getBouquet() {
        return state.getBouquet();
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() {
        return state.getPlayoutInfo();
    }

    @Override
    public void executeRequest(@NotNull Request request) throws Exception {
        switch (request.getCommand()) {
            case CONNECT:
                connect();
                break;
            case DISCONNECT:
                capabilities.remove(Capability.SKIP_BACKWARDS);
                capabilities.remove(Capability.AUDIO_TRANSPORT);
                setChanged(SubInfo.CAPABILITIES);

                try {
                    v2request(COMMAND_SESSION_CLOSE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case REFRESH: {
                final @NotNull Object arg = request.getArgumentNotNull(0);
                final @NotNull EnumSet<SubInfo> infos;

                if (arg instanceof Sync) {
                    infos = EnumSet.of(SubInfo.METADATA, SubInfo.PLAYOUT);
                } else {
                    //noinspection unchecked
                    infos = (EnumSet<SubInfo>) arg;
                }

                for (SubInfo subInfo : infos) {
                    if (shouldRequestSessionInfo(subInfo)) {
                        v2request(COMMAND_SESSION_INFO);
                    }
                }

                if (arg instanceof Sync)
                    state.refresh((Sync) arg);
                break;
            }
            case WIND_TO_LIVE:
                v2request(COMMAND_PLAYOUT_WIND_BACK_TO_LIVE);
                break;
            case WIND_TO: {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("ts", String.valueOf(((Instant)request.getArgumentNotNull(0)).toEpochMilli()));
                v2request(COMMAND_PLAYOUT_WIND, parameters);
                break;
            }
            case WIND_BY: {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("duration", String.valueOf(((Duration)request.getArgumentNotNull(0)).toMillis()));
                v2request(COMMAND_PLAYOUT_WIND, parameters);
                break;
            }
            case SKIP_FORWARD: {
                HashMap<String, String> parameters = new HashMap<>();
                final @Nullable ItemType itemType = (ItemType) request.getArgumentNullable(0);

                if (itemType != null) {
                    parameters.put("item-type", itemType.name());
                }
                v2request(COMMAND_PLAYOUT_SKIP_FORWARDS, parameters);
                break;
            }
            case SKIP_BACKWARD: {
                HashMap<String, String> parameters = new HashMap<>();
                final @Nullable ItemType itemType = (ItemType) request.getArgumentNullable(0);

                if (itemType != null) {
                    parameters.put("item-type", itemType.name());
                }
                v2request(COMMAND_PLAYOUT_SKIP_BACKWARDS, parameters);
                break;
            }
            case SWAP_ITEM: {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("mode", ((SwapMode)request.getArgumentNotNull(0)).getOnWire());
                v2request(COMMAND_PLAYOUT_SWAP_ITEM, parameters);
                break;
            }
            case SWAP_SERVICE: {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("service-id", ((Service)request.getArgumentNotNull(0)).getIdentifier().toString());
                v2request(COMMAND_PLAYOUT_SWAP_SERVICE, parameters);
                break;
            }
            case SWAP_TO_MAIN_SERVICE:
                v2request(COMMAND_PLAYOUT_BACK_TO_MAIN);
                break;
            default:
                super.executeRequest(request);
        }
    }
}
