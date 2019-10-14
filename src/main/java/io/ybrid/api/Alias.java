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

package io.ybrid.api;

import io.ybrid.api.driver.FactorySelector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * An Alias represents a entry point on a {@link Server}.
 * The alias can be used to open a {@link Session} and a stream.
 */
public class Alias {
    private final Logger logger;
    private URL url;
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
     * @return Returns a newly created and unconnected {@link Session}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Session createSession() throws MalformedURLException {
        return getServer().createSession(this);
    }

    /**
     * Get the current {@link Bouquet} from the {@link Server}.
     * @param server The {@link Server} to use.
     * @return Returns the current {@link Bouquet}.
     */
    public Bouquet getBouquet(Server server) {
        return FactorySelector.getFactory(server, this).getBouquet(server, this);
    }

    /**
     * Get the current {@link Bouquet} from the default {@link Server}.
     * @return Returns the current {@link Bouquet}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Bouquet getBouquet() throws MalformedURLException {
        return getBouquet(getServer());
    }
}
