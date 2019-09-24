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

package io.ybrid.client.control;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Session implements Connectable {
    private boolean connected = false;
    private Server server;
    private String hostname;
    private String mountpoint;
    private String token;
    private Service defaultService = new Service();

    private static void assertValidMountpoint(String mountpoint) throws MalformedURLException {
        if (!mountpoint.startsWith("/"))
            throw new MalformedURLException();
    }

    private void assertConnected() {
        if (!isConnected())
            throw new IllegalStateException("Not connected");
    }

    Session(Server server, Alias alias) throws MalformedURLException {
        String mountpoint = alias.getUrl().getPath();

        assertValidMountpoint(mountpoint);
        this.server = server;
        this.mountpoint = mountpoint;
    }

    public URL getStreamURL() throws MalformedURLException {
        String path = mountpoint;

        assertConnected();

        if (token != null)
            path += "?sessionId=" + token;

        return new URL(server.getProtocol(), hostname, server.getPort(), path);
    }

    public DataInputStream getStreamInputStream() {
        return new DataInputStream(this);
    }

    private JSONObject request(String command, String parameters) throws IOException {
        String hostname = this.hostname;
        String path = mountpoint + "/ctrl/" + command;
        URL url;

        server.finer("Request: command=" + command + ", parameters=" + parameters + ", token=" + token);

        if (parameters != null) {
            path += "?" + parameters;
            if (token != null)
                path += "&sessionId=" + token;
        } else if (token != null) {
            path += "?sessionId=" + token;
        }

        if (hostname == null)
            hostname = server.getHostname();

        url = new URL(server.getProtocol(), hostname, server.getPort(), path);
        return request(url);
    }

    private JSONObject request(URL url) throws IOException {
        HttpURLConnection connection;
        InputStream inputStream;
        String data;

        server.finer("Request: url = " + url);
        connection = (HttpURLConnection) url.openConnection();
        inputStream = connection.getInputStream();
        data = Utils.slurpToString(inputStream);
        inputStream.close();
        connection.disconnect();

        return new JSONObject(data);
    }

    private JSONObject request(String command) throws IOException {
        return request(command, null);
    }

    public void swap(SwapMode mode) throws IOException {
        assertConnected();

        request("swap", "mode=" + mode.getOnWire());
    }

    public JSONObject getMetadata() throws IOException {
        assertConnected();
        return request("show-meta");
    }

    public JSONObject getMetadata(URL url) throws IOException {
        assertConnected();
        return request(url);
    }

    public Server getServer() {
        return server;
    }

    public Service getDefaultService() {
        return defaultService;
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

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
