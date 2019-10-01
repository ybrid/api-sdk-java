/*
 * Copyright 2019 nacamar GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ybrid.client.control.Driver.V1;

import io.ybrid.client.control.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Driver extends io.ybrid.client.control.Driver.Common.Driver {
    private Bouquet bouquet = new Factory().getBouquet(session.getServer(), session.getAlias());
    public Driver(Session session) {
        super(session);

        this.currentService = bouquet.getDefaultService();
    }

    @Override
    protected JSONObject request(String command, String parameters) throws IOException {
        Server server = session.getServer();
        String hostname = this.hostname;
        String path = getMountpoint() + "/ctrl/" + command;
        String body = null;
        URL url;

        server.getLogger().finer("Request: command=" + command + ", parameters=" + parameters + ", token=" + token);

        if (parameters != null) {
            body = parameters;
            if (token != null)
                body += "&sessionId=" + token;
        } else if (token != null) {
            body = "sessionId=" + token;
        }

        if (hostname == null)
            hostname = server.getHostname();

        if (body != null) {
            path += "?" + body;
            body = null;
        }

        url = new URL(server.getProtocol(), hostname, server.getPort(), path);
        return request(url, body);
    }

    @Override
    public Bouquet getBouquet() {
        return bouquet;
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        assertConnected();

        request("swap", "mode=" + mode.getOnWire());
    }

    @Override
    public Metadata getMetadata() throws IOException {
        assertConnected();
        return new Metadata((Service) getCurrentService(), request("show-meta"));
    }

    @Override
    public URL getStreamURL() throws MalformedURLException {
        Server server = session.getServer();
        String path = getMountpoint();

        assertConnected();

        if (token != null)
            path += "?sessionId=" + token;

        return new URL(server.getProtocol(), hostname, server.getPort(), path);
    }

    @Override
    public StreamInputStream getInputStream() {
        return new DataInputStream(session);
    }

    @Override
    public void connect() throws IOException {
        JSONObject response;
        String hostname;
        String token;

        if (isConnected())
            return;

        response = request("create-session");
        token = response.getString("sessionId");
        if (token == null)
            throw new IOException("No SessionID from server. BAD.");

        this.token = token;

        hostname = response.getString("host");
        if (hostname != null)
            this.hostname = hostname;

        connected = true;
    }

}
