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

package io.ybrid.api.driver.common;

import io.ybrid.api.Metadata;
import io.ybrid.api.Service;
import io.ybrid.api.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;

public abstract class Driver implements Connectable, SessionClient {
    protected Session session;
    protected boolean connected = false;
    protected String hostname;
    protected String token;
    protected Service currentService;

    abstract public void swapItem(SwapMode mode) throws IOException;
    abstract public Metadata getMetadata() throws IOException;
    abstract public URL getStreamURL() throws MalformedURLException;
    abstract public Bouquet getBouquet();
    abstract protected JSONObject request(String command, String parameters) throws IOException;

    protected Driver(Session session) {
        this.session = session;
    }

    protected static void assertValidMountpoint(String mountpoint) throws MalformedURLException {
        if (!mountpoint.startsWith("/"))
            throw new MalformedURLException();
    }

    protected final String getMountpoint() throws MalformedURLException {
        String mountpoint = session.getAlias().getUrl().getPath();
        assertValidMountpoint(mountpoint);
        return mountpoint;
    }

    @Override
    public Service getCurrentService() {
        return currentService;
    }

    public void swapService(Service service) {
        if (service.equals(getCurrentService()))
            return;

        throw new UnsupportedOperationException("Can not swap to given Service");
    }

    protected void assertConnected() {
        if (!isConnected())
            throw new IllegalStateException("Not connected");
    }

    protected JSONObject request(URL url, String body) throws IOException {
        HttpURLConnection connection;
        InputStream inputStream;
        OutputStream outputStream;
        String data;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoInput(true);
        connection.setDoOutput(body != null);

        if (body != null) {
            outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.close();
        }

        inputStream = connection.getInputStream();
        data = Utils.slurpToString(inputStream);
        inputStream.close();
        connection.disconnect();

        return new JSONObject(data);
    }

    protected JSONObject request(String command) throws IOException {
        return request(command, null);
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }


    @Override
    public void windToLive() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void WindTo(Instant timestamp) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void Wind(long duration) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        throw new UnsupportedOperationException();
    }
}
