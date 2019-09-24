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

import java.net.MalformedURLException;
import java.util.logging.Logger;

public class Server implements Connectable {
    private String hostname;
    private int port = 80;
    private boolean secure = false;
    private final Logger logger;

    private void assertValidHostname(String hostname) throws MalformedURLException {
        if (!hostname.matches("^[a-zA-Z0-9.]+$"))
            throw new MalformedURLException("Bad hostname");
    }

    private void assertValidPort(int port) throws MalformedURLException {
        if (port < 0 || port > 65535)
            throw new MalformedURLException("Bad port");
    }

    public Server(Logger logger, String hostname, int port, boolean secure) throws MalformedURLException {
        this.logger = logger;
        assertValidHostname(hostname);
        assertValidPort(port);
        this.hostname = hostname;
        this.port = port;
        this.secure = secure;
    }

    public Server(Logger logger, String hostname) throws MalformedURLException {
        this.logger = logger;
        assertValidHostname(hostname);
        this.hostname = hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public boolean isSecure() {
        return secure;
    }

    String getProtocol() {
        return isSecure() ? "https" : "http";
    }

    public Session getStreamSession(String mountpoint) throws MalformedURLException {
        return new Session(this, mountpoint);
    }

    Logger getLogger() {
        return logger;
    }

    void finer(String msg) {
        if (logger != null)
            logger.finer(msg);
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
}
