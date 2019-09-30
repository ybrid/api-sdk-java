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

import io.ybrid.client.control.Driver.FactorySelector;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

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

    public Alias(Logger logger, URL url, Server server) {
        this.logger = logger;
        this.url = url;
        this.server = server;
    }

    public Alias(Logger logger, URL url) {
        this.logger = logger;
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    public Server getServer() throws MalformedURLException {
        assertServer();
        return server;
    }

    public Session getSession() throws MalformedURLException {
        return getServer().getSession(this);
    }

    public Bouquet getBouquet(Server server) {
        return FactorySelector.getFactory(server, this).getBouquet(server, this);
    }

    public Bouquet getBouquet() {
        return getBouquet(server);
    }
}
