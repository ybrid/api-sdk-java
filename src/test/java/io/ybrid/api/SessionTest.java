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
import io.ybrid.api.transport.TransportDescription;
import junit.framework.TestCase;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class SessionTest extends TestCase {
    public void testGetStreamURLPositive() throws IOException, URISyntaxException {
        for (URI mediaEndpointURI : NetworkHelper.getAliases()) {
            final MediaEndpoint alias;
            final Session session;
            final TransportDescription[] transportDescription = new TransportDescription[1];

            alias = new MediaEndpoint(mediaEndpointURI);
            assertNotNull(alias);

            session = alias.createSession();
            assertNotNull(session);

            session.connect();

            session.attachPlayer(newTransportDescription -> transportDescription[0] = newTransportDescription);
            session.createTransaction(Command.CONNECT_INITIAL_TRANSPORT.makeRequest()).run();
            assertNotNull(transportDescription[0]);

            session.close();
        }
    }
}
