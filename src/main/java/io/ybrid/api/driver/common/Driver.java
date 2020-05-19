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

package io.ybrid.api.driver.common;

import io.ybrid.api.Metadata;
import io.ybrid.api.Service;
import io.ybrid.api.*;
import io.ybrid.api.driver.CapabilitySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Driver implements Connectable, SessionClient {
    static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    protected final Session session;
    protected final CapabilitySet capabilities = new CapabilitySet();
    protected boolean haveCapabilitiesChanged = true;
    protected boolean connected = false;
    protected String hostname;
    protected String token;
    protected Service currentService;

    abstract public void swapItem(SwapMode mode) throws IOException;
    abstract public @NotNull Metadata getMetadata() throws IOException;
    abstract public URL getStreamURL() throws MalformedURLException;
    abstract public @NotNull Bouquet getBouquet() throws IOException;

    protected Driver(Session session) {
        this.session = session;
    }

    protected static void assertValidMountpoint(@NotNull String mountpoint) throws MalformedURLException {
        if (!mountpoint.startsWith("/"))
            throw new MalformedURLException();
    }

    protected final String getMountpoint() throws MalformedURLException {
        String mountpoint = session.getAlias().getUrl().getPath();
        assertValidMountpoint(mountpoint);
        return mountpoint;
    }

    @Override
    public @NotNull io.ybrid.api.CapabilitySet getCapabilities() {
        haveCapabilitiesChanged = false;
        return capabilities;
    }

    @Override
    public boolean haveCapabilitiesChanged() {
        return haveCapabilitiesChanged;
    }

    @Override
    public @NotNull Service getCurrentService() throws IOException {
        assertConnected();

        return currentService;
    }

    public void swapService(@NotNull Service service) throws IOException {
        if (service.equals(getCurrentService()))
            return;

        throw new UnsupportedOperationException("Can not swap to given Service");
    }

    protected void assertConnected() {
        if (!isConnected())
            throw new IllegalStateException("Not connected");
    }

    // TODO: Remove this once the servers no longer require it.
    private static URL workaroundNoPostBody(@NotNull URL url, @NotNull Map<String, String> body) {
        try {
            final StringBuilder rendered = new StringBuilder();
            final String utf8Name = StandardCharsets.UTF_8.name();

            for (Map.Entry<String, String> entry : body.entrySet()) {
                if (rendered.length() > 0)
                    rendered.append('&');
                rendered.append(URLEncoder.encode(entry.getKey(), utf8Name));
                rendered.append('=');
                rendered.append(URLEncoder.encode(entry.getValue(), utf8Name));
            }

            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + rendered.toString());
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    protected JSONObject request(URL url, @Nullable Map<String, String> body) throws IOException {
        final JSONObject jsonObject;
        final JSONRequest request;

        if (body != null) {
            request = new JSONRequest(workaroundNoPostBody(url, body), "POST");
        } else {
            request = new JSONRequest(url, "POST");
        }

        if (request.perform()) {
            jsonObject = request.getResponseBody();
        } else {
            jsonObject = null;
        }

        if (LOGGER.isLoggable(Level.FINE))
            LOGGER.fine("request: url=" + request.getUrl() + ", jsonObject=" + jsonObject);
        return jsonObject;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public void windToLive() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void windTo(@NotNull Instant timestamp) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void wind(@NotNull Duration duration) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        throw new UnsupportedOperationException();
    }
}
