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
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.SimpleService;
import io.ybrid.api.metadata.Metadata;
import io.ybrid.api.metadata.SimpleMetadata;
import io.ybrid.api.session.Request;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

public class Driver extends io.ybrid.api.driver.common.Driver {
    private final @NotNull Bouquet bouquet;
    private final @NotNull PlayoutInfo playoutInfo = new PlayoutInfo();
    private final @NotNull Metadata metadata;

    protected Driver(Session session) {
        super(session);
        this.bouquet = new Bouquet(new SimpleService());
        this.currentService = bouquet.getDefaultService();
        metadata = new SimpleMetadata(new Item(), null, this.currentService, TemporalValidity.INDEFINITELY_VALID);
        capabilities.add(Capability.AUDIO_TRANSPORT);
        setChanged(SubInfo.CAPABILITIES);
        setChanged(SubInfo.BOUQUET);
        setChanged(SubInfo.PLAYOUT);
        setChanged(SubInfo.METADATA);
    }

    @Override
    public void executeRequest(@NotNull Request request) throws Exception {
        switch (request.getCommand()) {
            case CONNECT:
                connected = true;
                break;
            case REFRESH:
                // no-op.
                break;
            default:
                super.executeRequest(request);
        }
    }

    @Override
    public URI getStreamURI() throws MalformedURLException, URISyntaxException {
        Server server = session.getServer();

        assertConnected();

        return new URI(server.getProtocol(), null, server.getHostname(), server.getPort(), getMountpoint(), null, null);
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
}
