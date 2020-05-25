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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
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

        metadata = new Metadata((Service) getCurrentService(), json);
        setChanged(SubInfo.METADATA);

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

    @Override
    public io.ybrid.api.@NotNull Metadata getMetadata() throws IOException {
        updateMetadata();
        return metadata;
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() throws IOException {
        updateMetadata();
        return playoutInfo;
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
                LOGGER.log(Level.SEVERE, "Invalid hostname from server: " + hostname);
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
