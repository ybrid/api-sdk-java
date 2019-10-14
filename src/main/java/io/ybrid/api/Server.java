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

import java.net.MalformedURLException;
import java.util.logging.Logger;

/**
 * This class represents the connection to a specific ybrid server.
 *
 * Objects of this class can be reused for several sessions.
 */
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

    /**
     * Creates a new Server object.
     *
     * @param logger The {@link Logger} to use.
     * @param hostname The name of the host used to access the server.
     * @param port The port to access the server.
     * @param secure Whether to use a secure connection to the server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Server(Logger logger, String hostname, int port, boolean secure) throws MalformedURLException {
        this.logger = logger;
        assertValidHostname(hostname);
        assertValidPort(port);
        this.hostname = hostname;
        this.port = port;
        this.secure = secure;
    }

    /**
     * Creates a new Server object.
     *
     * @param logger The {@link Logger} to use.
     * @param hostname The name of the host used to access the server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Server(Logger logger, String hostname) throws MalformedURLException {
        this.logger = logger;
        assertValidHostname(hostname);
        this.hostname = hostname;
    }

    /**
     * Get the name of the host used to contact the server.
     * @return Returns the hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Get the port used to contact the server.
     * @return Returns the port number.
     */
    public int getPort() {
        return port;
    }

    /**
     * Get whether the connection should be established securely.
     * @return Whether contacting the server securely.
     */
    public boolean isSecure() {
        return secure;
    }

    /**
     * Gets the transport protocol used.
     * @return Returns the name of the protocol.
     */
    public String getProtocol() {
        return isSecure() ? "https" : "http";
    }

    /**
     * Create a new unconnected {@link Session} for the given {@link Alias}.
     *
     * @param alias The {@link Alias} to connect to.
     * @return Returns the newly created {@link Session}.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Session createSession(Alias alias) throws MalformedURLException {
        return new Session(this, alias);
    }

    /**
     * Get the {@link Logger} used by this Server object.
     * @return Returns the logger.
     */
    public Logger getLogger() {
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
