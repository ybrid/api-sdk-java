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
import io.ybrid.api.driver.common.Driver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

/**
 * This class implements an actual session with a ybrid server.
 *
 * The session can be used to request an audio stream from the server.
 * It is also used to control the stream.
 */
public class Session implements Connectable, SessionClient {
    private Driver driver;
    private Server server;
    private Alias alias;

    Session(Server server, Alias alias) throws MalformedURLException {
        this.driver = FactorySelector.getFactory(server, alias).getDriver(this);
        this.server = server;
        this.alias = alias;
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
    public Bouquet getBouquet() {
        return driver.getBouquet();
    }

    @Override
    public void windToLive() throws IOException {
        driver.windToLive();
    }

    @Override
    public void WindTo(Instant timestamp) throws IOException {
        driver.WindTo(timestamp);
    }

    @Override
    public void Wind(long duration) throws IOException {
        driver.Wind(duration);
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
    public void swapService(Service service) {
        driver.swapService(service);
    }

    @Override
    public Metadata getMetadata() throws IOException {
        return driver.getMetadata();
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
    public Service getCurrentService() {
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
