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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * This class represents the connection to a specific Ybrid server.
 *
 * Objects of this class can be reused for several sessions.
 */
public class Server implements Connectable, ApiUser {
    /**
     * The default port used for Ybrid servers.
     */
    public static final int DEFAULT_PORT = 80;
    /**
     * The default security setting for Ybrid servers.
     */
    public static final boolean DEFAULT_SECURE = false;

    private final @NotNull WorkaroundMap workarounds = new WorkaroundMap();
    private final @NotNull String hostname;
    private int port = DEFAULT_PORT;
    private boolean secure = DEFAULT_SECURE;
    private final @NotNull Logger logger;
    private @Nullable ApiVersion apiVersion = null;

    private void assertValidHostname(@Nullable String hostname) throws MalformedURLException {
        if (hostname == null)
            throw new MalformedURLException("Bad hostname: null");
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
     * @param baseURL The base URL to use for this server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     */
    public Server(@NotNull URL baseURL) throws MalformedURLException {
        int newPort;

        this.logger = Logger.getLogger(Server.class.getName());

        newPort = baseURL.getPort();
        if (newPort < 0)
            newPort = baseURL.getDefaultPort();

        assertValidHostname(baseURL.getHost());
        assertValidPort(newPort);
        this.hostname = baseURL.getHost();
        this.port = newPort;
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
     * Creates a new Server object.
     *
     * @param hostname The name of the host used to access the server.
     * @param port The port to access the server.
     * @param secure Whether to use a secure connection to the server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     * @deprecated Use {@link #Server(URL)} instead.
     */
    @Deprecated
    public Server(@NotNull String hostname, int port, boolean secure) throws MalformedURLException {
        this.logger = Logger.getLogger(Server.class.getName());
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
     * @param port The port to access the server.
     * @param secure Whether to use a secure connection to the server.
     * @throws MalformedURLException Thrown if there is any problem found with the parameters.
     * @deprecated Use {@link #Server(String, int, boolean)} instead.
     */
    @Deprecated
    public Server(@NotNull Logger logger, @NotNull String hostname, int port, boolean secure) throws MalformedURLException {
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
     * @deprecated Use {@link #Server(String, int, boolean)} instead.
     */
    @Deprecated
    public Server(@NotNull Logger logger, @NotNull String hostname) throws MalformedURLException {
        this.logger = logger;
        assertValidHostname(hostname);
        this.hostname = hostname;
    }

    /**
     * Get the name of the host used to contact the server.
     * @return Returns the hostname.
     */
    public @NotNull String getHostname() {
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
    public @NotNull String getProtocol() {
        return isSecure() ? "https" : "http";
    }

    /**
     * Create a new unconnected {@link Session} for the given {@link Alias}.
     *
     * This may connect if needed. See {@link #connect()}.
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
     * @deprecated Callers should use their own instance of a {@link Logger}.
     */
    @Deprecated
    public @NotNull Logger getLogger() {
        return logger;
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
