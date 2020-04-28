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

package io.ybrid.api.driver.v1;

import io.ybrid.api.*;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.logging.Level;

public final class Driver extends io.ybrid.api.driver.common.Driver {
    private static final Capability[] initialCapabilities = {Capability.PLAYBACK_URL};
    private final Bouquet bouquet = new Factory().getBouquet(session.getServer(), session.getAlias());
    public Driver(Session session) {
        super(session);

        this.currentService = bouquet.getDefaultService();

        capabilities.add(initialCapabilities);
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
        Metadata ret;

        assertConnected();

        ret = new Metadata((Service) getCurrentService(), request("show-meta"));

        if (ret.getSwapInfo().canSwap()) {
            capabilities.add(Capability.SWAP_ITEM);
        } else {
            capabilities.remove(Capability.SWAP_ITEM);
        }
        haveCapabilitiesChanged = true;

        return ret;
    }

    @Override
    public PlayoutInfo getPlayoutInfo() throws IOException {
        Metadata metadata = getMetadata();
        return new io.ybrid.api.driver.common.PlayoutInfo(metadata.getSwapInfo(), Duration.ofMillis(metadata.getTimeToNextItem()));
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

        if (hostname != null) {
            if (hostname.equals("localhost") || hostname.equals("localhost.localdomain")) {
                session.getServer().getLogger().log(Level.SEVERE, "Invalid hostname from server: " + hostname);
                hostname = null;
            }
        }

        /* We did not get anything useful from the server */
        if (hostname == null)
            hostname = session.getServer().getHostname();

        this.hostname = hostname;

        connected = true;
    }

}
