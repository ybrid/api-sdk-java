/*
 * Copyright (c) 2020 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.driver.plain;

import io.ybrid.api.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Driver extends io.ybrid.api.driver.common.Driver {
    private final @NotNull Bouquet bouquet;
    private final @NotNull PlayoutInfo playoutInfo = new PlayoutInfo();
    private final @NotNull Metadata metadata;

    protected Driver(Session session) {
        super(session);
        try {
            this.bouquet = new Factory().getBouquet(session.getServer(), session.getAlias());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.currentService = bouquet.getDefaultService();
        metadata = new Metadata(this.currentService, new Item());
        capabilities.add(Capability.PLAYBACK_URL);
        setChanged(SubInfo.CAPABILITIES);
        setChanged(SubInfo.BOUQUET);
        setChanged(SubInfo.PLAYOUT);
        setChanged(SubInfo.METADATA);
    }

    @Override
    public void swapItem(@NotNull SwapMode mode) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void refresh(@NotNull SubInfo what) throws IOException {
        setChanged(what);
        // no-op
    }

    @Override
    public URL getStreamURL() throws MalformedURLException {
        Server server = session.getServer();

        assertConnected();

        return new URL(server.getProtocol(), server.getHostname(), server.getPort(), getMountpoint());
    }

    @Override
    public @NotNull Metadata getMetadata() {
        return metadata;
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() {
        return playoutInfo;
    }

    @Override
    public @NotNull Bouquet getBouquet() {
        return bouquet;
    }

    @Override
    public void connect() throws IOException {
        connected = true;
    }
}
