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

import io.ybrid.api.driver.FactorySelector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * An Alias represents a entry point on a {@link Server}.
 * The alias can be used to open a {@link Session} and a stream.
 */
public class Alias {
    private final Logger logger;
    private final URL url;
    private Server server;

    private void assertServer() throws MalformedURLException {
        boolean secure;
        int port;

        if (server != null)
            return;

        switch (url.getProtocol()) {
            case "http":
                secure = false;
                break;
            case "https":
                secure = true;
                break;
            default:
                throw new MalformedURLException("Invalid protocol");
        }

        port = url.getPort();
        if (port < 0)
            port = url.getDefaultPort();

        server = new Server(logger, url.getHost(), port, secure);
    }

    /**
     * Create a new Alias using the given {@link Server}.
     *
     * @param logger The logger to use for the Alias.
     * @param url The {@link URL} of the Alias.
     * @param server The {@link Server} to use for contacting the Alias.
     */
    public Alias(Logger logger, URL url, Server server) {
        this.logger = logger;
        this.url = url;
        this.server = server;
    }

    /**
     * Create a Alias without a {@link Server} object. A {@link Server} object is created automatically if needed.
     *
     * @param logger The logger to use for the Alias.
     * @param url The {@link URL} of the Alias.
     */
    public Alias(Logger logger, URL url) {
        this.logger = logger;
        this.url = url;
    }

    /**
     * Get the {@link URL} of the Alias.
     * @return Returns the {@link URL} of the Alias.
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Returns the {@link Server} used by this Alias.
     * If no {@link Server} has been passed to the Constructor it is automatically created.
     *
     * @return Returns the {@link Server} object of this Alias.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Server getServer() throws MalformedURLException {
        assertServer();
        return server;
    }

    /**
     * Create a {@link Session} using this Alias.
     *
     * This may will connect the used {@link Server} if needed.
     *
     * @return Returns a newly created and unconnected {@link Session}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Session createSession() throws MalformedURLException {
        return getServer().createSession(this);
    }

    /**
     * Get the current {@link Bouquet} from the default {@link Server}.
     * @return Returns the current {@link Bouquet}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Bouquet getBouquet() throws IOException {
        return FactorySelector.getFactory(getServer(), this).getBouquet(getServer(), this);
    }
}
