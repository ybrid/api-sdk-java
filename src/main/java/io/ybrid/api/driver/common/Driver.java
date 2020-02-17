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

package io.ybrid.api.driver.common;

import io.ybrid.api.Metadata;
import io.ybrid.api.Service;
import io.ybrid.api.*;
import io.ybrid.api.driver.CapabilitySet;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Driver implements Connectable, SessionClient {
    protected final Session session;
    protected final CapabilitySet capabilities = new CapabilitySet();
    protected boolean haveCapabilitiesChanged = true;
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
    public io.ybrid.api.CapabilitySet getCapabilities() {
        haveCapabilitiesChanged = false;
        return capabilities;
    }

    @Override
    public boolean haveCapabilitiesChanged() {
        return haveCapabilitiesChanged;
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
        final Logger logger = session.getServer().getLogger();
        HttpURLConnection connection;
        InputStream inputStream;
        OutputStream outputStream;
        final JSONObject jsonObject;

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
        jsonObject = Utils.slurpToJSONObject(inputStream);
        inputStream.close();
        connection.disconnect();

        if (logger.isLoggable(Level.FINE))
            logger.fine("request: url=" + url + ", jsonObject=" + jsonObject);
        return jsonObject;
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
