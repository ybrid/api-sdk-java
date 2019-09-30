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

import io.ybrid.client.control.Driver.Common.Driver;
import io.ybrid.client.control.Driver.FactorySelector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Session implements Connectable {
    private Driver driver;
    private Server server;
    private Alias alias;

    Session(Server server, Alias alias) throws MalformedURLException {
        this.driver = FactorySelector.getFactory(server, alias).getDriver(this);
        this.server = server;
        this.alias = alias;
    }

    public Alias getAlias() {
        return alias;
    }

    public Server getServer() {
        return server;
    }

    public void swap(SwapMode mode) throws IOException {
        driver.swap(mode);
    }

    public Metadata getMetadata() throws IOException {
        return driver.getMetadata();
    }

    public URL getStreamURL() throws MalformedURLException {
        return driver.getStreamURL();
    }

    public StreamInputStream getInputStream() {
        return driver.getInputStream();
    }

    public Service getDefaultService() {
        return driver.getDefaultService();
    }

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
