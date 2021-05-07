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

import io.ybrid.api.session.Command;
import io.ybrid.api.transaction.Request;
import io.ybrid.api.transaction.Transaction;
import io.ybrid.api.transport.ServiceTransportDescription;
import io.ybrid.api.transport.ServiceURITransportDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.*;

public class SessionTest {
    @Test
    public void GetStreamURLPositive() throws IOException {
        for (URI mediaEndpointURI : NetworkHelper.getEndpoints()) {
            final MediaEndpoint mediaEndpoint;
            final Session session;
            final ServiceTransportDescription[] transportDescription = new ServiceTransportDescription[1];

            mediaEndpoint = new MediaEndpoint(mediaEndpointURI);
            assertNotNull(mediaEndpoint);

            session = mediaEndpoint.createSession();
            assertNotNull(session);

            session.connect();

            session.attachPlayer(newTransportDescription -> transportDescription[0] = newTransportDescription);
            session.createTransaction((Request<?>) Command.CONNECT_INITIAL_TRANSPORT.makeRequest()).run();
            assertNotNull(transportDescription[0]);

            session.close();
        }
    }

    @Test
    public void offlineSession() throws IOException {
        final @NotNull URI streamURI = URI.create("http://localhost:8000/");
        final @NotNull MediaEndpoint mediaEndpoint = new MediaEndpoint(streamURI);
        final @NotNull Session session;
        final @NotNull Transaction transaction;
        final @Nullable Throwable error;

        mediaEndpoint.forceMediaProtocol(MediaProtocol.PLAIN);

        session = mediaEndpoint.createSession();
        session.connect();

        assertTrue(session.isConnected());

        session.attachPlayer(transportDescription -> {
            final @NotNull ServiceURITransportDescription transport = (ServiceURITransportDescription) transportDescription;
            assertEquals(streamURI, transport.getURI());
        });

        transaction = session.createTransaction(Command.CONNECT_INITIAL_TRANSPORT.makeRequest());
        transaction.run();
        error = transaction.getError();
        if (error != null)
            error.printStackTrace();
        assertNull(error);
        session.close();
    }
}
