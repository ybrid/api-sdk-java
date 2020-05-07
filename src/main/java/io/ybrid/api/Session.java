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
import io.ybrid.api.driver.common.Driver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

/**
 * This class implements an actual session with a Ybrid server.
 *
 * The session can be used to request an audio stream from the server.
 * It is also used to control the stream.
 */
public class Session implements Connectable, SessionClient {
    private final Driver driver;
    private final Server server;
    private final Alias alias;

    Session(Server server, Alias alias) throws MalformedURLException {
        this.server = server;
        this.alias = alias;
        this.driver = FactorySelector.getFactory(server, alias).getDriver(this);
    }

    /**
     * Gets the {@link Alias} used for this session.
     * @return Returns the {@link Alias}.
     */
    public Alias getAlias() {
        return alias;
    }

    /**
     * Gets the {@link Server} object used to communicate with the server.
     * @return Returns the {@link Server} object.
     */
    public Server getServer() {
        return server;
    }

    @Override
    public @NotNull CapabilitySet getCapabilities() {
        return driver.getCapabilities();
    }

    @Override
    public boolean haveCapabilitiesChanged() {
        return driver.haveCapabilitiesChanged();
    }

    @Override
    public @NotNull Bouquet getBouquet() throws IOException {
        return driver.getBouquet();
    }

    @Override
    public void windToLive() throws IOException {
        driver.windToLive();
    }

    @Override
    public void windTo(@NotNull Instant timestamp) throws IOException {
        driver.windTo(timestamp);
    }

    @Override
    public void wind(@NotNull Duration duration) throws IOException {
        driver.wind(duration);
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        driver.skipForwards(itemType);
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        driver.skipBackwards(itemType);
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        driver.swapItem(mode);
    }

    @Override
    public void swapService(@NotNull Service service) throws IOException {
        driver.swapService(service);
    }

    @Override
    public @NotNull Metadata getMetadata() throws IOException {
        return driver.getMetadata();
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() throws IOException {
        return driver.getPlayoutInfo();
    }

    /**
     * gets the {@link URL} of the audio stream.
     * @return The {@link URL} of the stream.
     * @throws MalformedURLException Thrown in case the stream can not be represented by an URL.
     */
    public URL getStreamURL() throws MalformedURLException {
        return driver.getStreamURL();
    }

    @Override
    public @NotNull Service getCurrentService() throws IOException {
        return driver.getCurrentService();
    }

    @Override
    public void disconnect() {
        driver.disconnect();
    }

    @Override
    public boolean isConnected() {
        return driver.isConnected();
    }

    @Override
    public void connect() throws IOException {
        driver.connect();
    }

    @Override
    public void close() throws IOException {
        driver.close();
    }
}
