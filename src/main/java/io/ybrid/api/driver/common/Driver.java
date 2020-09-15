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

import io.ybrid.api.PlayoutInfo;
import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.driver.CapabilitySet;
import io.ybrid.api.driver.JSONRequest;
import io.ybrid.api.metadata.Metadata;
import io.ybrid.api.metadata.source.SourceMetadata;
import io.ybrid.api.session.Command;
import io.ybrid.api.session.Request;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.EnumSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Driver implements Closeable, KnowsSubInfoState {
    static final Logger LOGGER = Logger.getLogger(Driver.class.getName());

    protected final Session session;
    protected final CapabilitySet capabilities = new CapabilitySet();
    private final EnumSet<SubInfo> changed = EnumSet.noneOf(SubInfo.class);
    protected boolean connected = false;
    private boolean valid = true;
    protected String hostname;
    protected String token;
    protected Service currentService;

    abstract public @NotNull Metadata getMetadata();
    abstract public @NotNull Bouquet getBouquet();
    abstract public @NotNull PlayoutInfo getPlayoutInfo();
    abstract public URI getStreamURI() throws MalformedURLException, URISyntaxException;

    @Override
    public void close() throws IOException {
        try {
            executeRequest(Command.DISCONNECT.makeRequest());
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    protected Driver(Session session) {
        this.session = session;
    }

    protected static void assertValidMountpoint(@NotNull String mountpoint) throws MalformedURLException {
        if (!mountpoint.startsWith("/"))
            throw new MalformedURLException();
    }

    /**
     * Checks the given FQDN for validity.
     * {@code localhost} is not considered valid by this function.
     * @param fqdn The FQDN to test.
     * @return Whether the argument is a valid FQDN.
     */
    public static boolean isValidFQDN(@NotNull String fqdn) {
        if (fqdn.equals("localhost") || fqdn.equals("localhost.localdomain") || fqdn.equals("127.0.0.1") || fqdn.equals("::1"))
            return false;

        return fqdn.contains(".");
    }

    protected final String getMountpoint() throws MalformedURLException {
        String mountpoint = session.getAlias().getUrl().getPath();
        assertValidMountpoint(mountpoint);
        return mountpoint;
    }

    public @NotNull io.ybrid.api.CapabilitySet getCapabilities() {
        clearChanged(SubInfo.CAPABILITIES);
        return capabilities;
    }

    public void clearChanged(@NotNull SubInfo what) {
        changed.remove(what);
    }

    protected void setChanged(@NotNull SubInfo what) {
        changed.add(what);
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        return changed.contains(what);
    }

    protected void assertConnected() {
        if (!isConnected())
            throw new IllegalStateException("Not connected");
    }

    // TODO: Remove this once the servers no longer require it.
    private static URL workaroundNoPostBody(@NotNull URL url, @NotNull Map<String, String> body) {
        final XWWWFormUrlEncodedBuilder builder = new XWWWFormUrlEncodedBuilder();
        builder.append(body);
        try {
            return new URL(url.getProtocol(), url.getHost(), url.getPort(), url.getFile() + "?" + builder.toString());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    protected JSONObject request(@NotNull URL url, @Nullable Map<String, String> body) throws IOException {
        final JSONObject jsonObject;
        final JSONRequest request;

        if (body != null) {
            if (session.getActiveWorkarounds().get(Workaround.WORKAROUND_POST_BODY_AS_QUERY_STRING).toBool(false)) {
                request = new JSONRequest(workaroundNoPostBody(url, body), "POST");
            } else {
                request = new JSONRequest(url, "POST", body);
            }
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

    public void executeRequest(@NotNull Request request) throws Exception {
        switch (request.getCommand()) {
            case DISCONNECT:
                connected = false;
                break;
            case SWAP_SERVICE:
                if (request.getArgumentNotNull(0).equals(session.getMetadataMixer().getCurrentService()))
                    return;

                throw new UnsupportedOperationException("Can not swap to given Service");
            default:
                throw new UnsupportedOperationException();
        }
    }

    public void acceptSessionSpecific(@NotNull SourceMetadata sourceMetadata) {
        // no-op
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isValid() {
        return valid;
    }

    protected void setInvalid() {
        if (valid)
            setChanged(SubInfo.VALIDITY);
        valid = false;
    }
}
