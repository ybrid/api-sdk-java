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

import java.net.MalformedURLException;
import java.util.logging.Logger;

/**
 * This class represents the connection to a specific Ybrid server.
 *
 * Objects of this class can be reused for several sessions.
 */
public class Server implements Connectable {
    /**
     * The default port used for Ybrid servers.
     */
    public static final int DEFAULT_PORT = 80;
    /**
     * The default security setting for Ybrid servers.
     */
    public static final boolean DEFAULT_SECURE = false;

    private final String hostname;
    private int port = DEFAULT_PORT;
    private boolean secure = DEFAULT_SECURE;
    private final Logger logger;

    private void assertValidHostname(String hostname) throws MalformedURLException {
        if (!hostname.matches("^[a-zA-Z0-9.-]+$"))
            throw new MalformedURLException("Bad hostname: \"" + hostname + "\"");
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
     * This is like {@link #Server(Logger, String, int, boolean)} With {@code port} set to {@link #DEFAULT_PORT},
     * and {@code secure} set to {@link #DEFAULT_SECURE}.
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
     * This may will connect the used {@link Server} if needed.
     *
     * @param alias The {@link Alias} to connect to.
     * @return Returns the newly created {@link Session}.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Session createSession(Alias alias) throws MalformedURLException {
        connect();
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
