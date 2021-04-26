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

package io.ybrid.api;

import io.ybrid.api.util.Connectable;
import io.ybrid.api.util.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class represents the connection to a specific Ybrid server.
 *
 * Objects of this class can be reused for several sessions.
 *
 * @deprecated This class should no longer be used directly by the user.
 */
@Deprecated
public final class Server implements Connectable, ApiUser {
    private final @NotNull WorkaroundMap workarounds = new WorkaroundMap();
    private final boolean secure;
    private @Nullable ApiVersion apiVersion = null;

    /**
     * Creates a new Server object.
     *
     * @param baseURL The base URL to use for this server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Server(@NotNull URL baseURL) throws MalformedURLException {
        Utils.assertValidHostname(baseURL.getHost());
        switch (baseURL.getProtocol()) {
            case "http":
                this.secure = false;
                break;
            case "https":
                this.secure = true;
                break;
            default:
                throw new MalformedURLException("Unsupported protocol: " + baseURL.getProtocol());
        }
    }

    /**
     * Get whether the connection should be established securely.
     * @return Whether contacting the server securely.
     * @deprecated This class is deprecated. For a replacement see {@link MediaEndpoint#getURI()}.
     */
    @Deprecated
    public boolean isSecure() {
        return secure;
    }

    @Override
    public void connect() {
        /* NOOP */
    }

    @Override
    public void disconnect() {
        /* NOOP */
    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public void forceApiVersion(@Nullable ApiVersion version) throws IllegalArgumentException, IllegalStateException {
        this.apiVersion = version;
    }

    @Override
    public @Nullable ApiVersion getForcedApiVersion() {
        return apiVersion;
    }

    @Override
    public @NotNull WorkaroundMap getWorkarounds() {
        return workarounds;
    }
}
