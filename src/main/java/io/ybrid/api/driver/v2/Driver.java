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

import io.ybrid.api.Bouquet;
import io.ybrid.api.Metadata;
import io.ybrid.api.Session;
import io.ybrid.api.SwapMode;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Driver extends io.ybrid.api.driver.common.Driver {
    private static final String COMMAND_SESSION_CREATE = "session/create";
    private static final String COMMAND_SESSION_CLOSE = "session/close";
    private static final String COMMAND_SESSION_INFO = "session/info";
    private static final String COMMAND_PLAYOUT_SWAP_ITEM = "playout/swap/item";

    private final State state;

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
        return response;
    }

    protected Response v2request(String command) throws IOException {
        return v2request(command, null);
    }

    @Override
    public void disconnect() {
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
    }

    @Override
    public Metadata getMetadata() throws IOException {
        v2request(COMMAND_SESSION_INFO);
        return state.getMetadata();
    }

    @Override
    public Bouquet getBouquet() throws IOException {
        v2request(COMMAND_SESSION_INFO);
        return state.getBouquet();
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("mode", mode.getOnWire());
        v2request(COMMAND_PLAYOUT_SWAP_ITEM, parameters);
    }
}
