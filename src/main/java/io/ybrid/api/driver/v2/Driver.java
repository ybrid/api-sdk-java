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

package io.ybrid.api.driver.v2;

import io.ybrid.api.Service;
import io.ybrid.api.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
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
        state = new State(session, session.getAlias().getUrl());

        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_POST_BODY_AS_QUERY_STRING);
        session.getActiveWorkarounds().enableIfAutomatic(Workaround.WORKAROUND_BAD_PACKED_RESPONSE);
    }

    private URL getUrl(@Nullable String suffix) throws MalformedURLException {
        URL baseUrl = state.getBaseUrl();
        if (suffix == null || suffix.isEmpty())
            return baseUrl;

        return new URL(baseUrl.getProtocol(), baseUrl.getHost(), baseUrl.getPort(), baseUrl.getFile() + suffix);
    }

    @Override
    public URL getStreamURL() throws MalformedURLException {
        assertConnected();
        return getUrl("?session-id=" + token);
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
            response = new Response(Objects.requireNonNull(request(getUrl("/ctrl/v2/" + command), parameters)));
            state.accept(response);

            try {
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

    protected boolean shouldRequestSessionInfo(@NotNull SubInfo what) throws IOException {
        Instant lastUpdate;

        assertConnected();

        lastUpdate = state.getLastUpdated(what);
        return lastUpdate == null || !lastUpdate.plus(MINIMUM_BETWEEN_SESSION_INFO).isAfter(Instant.now());
    }

    @Override
    public void disconnect() {
        capabilities.remove(Capability.SKIP_BACKWARDS);
        capabilities.remove(Capability.PLAYBACK_URL);
        setChanged(SubInfo.CAPABILITIES);

        try {
            v2request(COMMAND_SESSION_CLOSE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.disconnect();
    }

    @Override
    public void connect() throws IOException {
        Response response;

        if (isConnected())
            return;

        if (!isValid())
            throw new IOException("Session is not valid.");

        response = v2request(COMMAND_SESSION_CREATE);
        if (response == null)
            throw new IOException("No valid response from server. BAD.");

        token = response.getToken();

        connected = true;

        capabilities.add(Capability.PLAYBACK_URL);
        capabilities.add(Capability.SKIP_BACKWARDS);
        setChanged(SubInfo.CAPABILITIES);
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return state.getMetadata();
    }

    @Override
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
    public void refresh(@NotNull SubInfo what) throws IOException {
        if (shouldRequestSessionInfo(what))
            v2request(COMMAND_SESSION_INFO);
    }

    @Override
    public void refresh(@NotNull EnumSet<SubInfo> what) throws IOException {
        for (SubInfo subInfo : what) {
            if (shouldRequestSessionInfo(subInfo)) {
                v2request(COMMAND_SESSION_INFO);
                return;
            }
        }
    }

    @Override
    public void swapItem(@NotNull SwapMode mode) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("mode", mode.getOnWire());
        v2request(COMMAND_PLAYOUT_SWAP_ITEM, parameters);
    }

    @Override
    public void swapService(@NotNull Service service) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("service-id", service.getIdentifier());
        v2request(COMMAND_PLAYOUT_SWAP_SERVICE, parameters);
    }

    @Override
    public void windToLive() throws IOException {
        v2request(COMMAND_PLAYOUT_WIND_BACK_TO_LIVE);
    }

    @Override
    public void windTo(@NotNull Instant timestamp) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("ts", String.valueOf(timestamp.toEpochMilli()));
        v2request(COMMAND_PLAYOUT_WIND, parameters);
    }

    @Override
    public void wind(@NotNull Duration duration) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("duration", String.valueOf(duration.toMillis()));
        v2request(COMMAND_PLAYOUT_WIND, parameters);
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        if (itemType != null) {
            parameters.put("item-type", itemType.name());
        }
        v2request(COMMAND_PLAYOUT_SKIP_FORWARDS, parameters);
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        if (itemType != null) {
            parameters.put("item-type", itemType.name());
        }
        v2request(COMMAND_PLAYOUT_SKIP_BACKWARDS, parameters);
    }

    @Override
    public void swapToMain() throws IOException {
        v2request(COMMAND_PLAYOUT_BACK_TO_MAIN);
    }
}
