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

import io.ybrid.api.*;
import io.ybrid.api.Service;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class Driver extends io.ybrid.api.driver.common.Driver {
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

    private static final long MINIMUM_BETWEEN_SESSION_INFO = 300; // [ms]

    private final State state;
    private long timestampLastSessionInfoRequest = 0;

    public Driver(Session session) {
        super(session);
        state = new State(session.getAlias().getUrl());
    }

    private URL getUrl(String suffix) throws MalformedURLException {
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
        if (state.hasChanged(SubInfo.SWAP_INFO)) {
            SwapInfo swapInfo = state.getSwapInfo();
            if (swapInfo.canSwap()) {
                capabilities.add(Capability.SWAP_ITEM);
            } else {
                capabilities.remove(Capability.SWAP_ITEM);
            }
            haveCapabilitiesChanged = true;
        }
    }

    @Override
    protected JSONObject request(String command, String parameters) throws IOException {
        String body = null;

        if (parameters != null) {
            body = parameters;
            if (token != null)
                body += "&session-id=" + token;
        } else if (token != null) {
            body = "session-id=" + token;
        }

        if (body == null)
            body = "";
        return request(getUrl("/ctrl/v2/" + command + "?" + body), null);
    }

    protected Response v2request(String command, Map<String, String> parameters) throws IOException {
        String renderedParameters = null;

        if (parameters != null) {
            StringBuilder rendered = new StringBuilder();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (rendered.length() > 0)
                    rendered.append('&');
                rendered.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.name()));
                rendered.append('=');
                rendered.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
            }
            renderedParameters = rendered.toString();
        }

        Response response = new Response(request(command, renderedParameters));
        state.accept(response);
        handleUpdates();
        return response;
    }

    protected Response v2request(String command) throws IOException {
        return v2request(command, null);
    }

    protected void requestSessionInfo() throws IOException {
        final long now = System.currentTimeMillis();

        if (now < (timestampLastSessionInfoRequest + MINIMUM_BETWEEN_SESSION_INFO))
            return;

        v2request(COMMAND_SESSION_INFO);
        timestampLastSessionInfoRequest = now;
    }

    @Override
    public void disconnect() {
        capabilities.remove(Capability.PLAYBACK_URL);
        haveCapabilitiesChanged = true;

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

        response = v2request(COMMAND_SESSION_CREATE);
        if (response == null)
            throw new IOException("No Session from server. BAD.");
        token = response.getToken();

        connected = true;

        capabilities.add(Capability.PLAYBACK_URL);
        haveCapabilitiesChanged = true;
    }

    @Override
    public Metadata getMetadata() throws IOException {
        requestSessionInfo();
        return state.getMetadata();
    }

    @Override
    public Bouquet getBouquet() throws IOException {
        requestSessionInfo();
        return state.getBouquet();
    }

    @Override
    public PlayoutInfo getPlayoutInfo() throws IOException {
        requestSessionInfo();
        return new io.ybrid.api.driver.common.PlayoutInfo(state.getSwapInfo(), null);
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("mode", mode.getOnWire());
        v2request(COMMAND_PLAYOUT_SWAP_ITEM, parameters);
    }

    @Override
    public void swapService(Service service) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("service-id", service.getIdentifier());
        v2request(COMMAND_PLAYOUT_SWAP_SERVICE, parameters);
    }

    @Override
    public void windToLive() throws IOException {
        v2request(COMMAND_PLAYOUT_WIND_BACK_TO_LIVE);
    }

    @Override
    public void windTo(Instant timestamp) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("ts", String.valueOf(timestamp.toEpochMilli()));
        v2request(COMMAND_PLAYOUT_WIND, parameters);
    }

    @Override
    public void wind(Duration duration) throws IOException {
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
