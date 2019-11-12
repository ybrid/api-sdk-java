/*
 * Copyright (c) 2019 nacamar GmbH - YBRID®, a Hybrid Dynamic Live Audio Technology
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

import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

public class ServerTest extends TestCase {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public void testCreateSimplePositive() throws MalformedURLException {
        String[] hostnames = {"localhost"};

        for (String hostname : hostnames) {
            Server server = new Server(LOGGER, hostname);

            assertNotNull(server);
            assertEquals(server.getHostname(), hostname);
        }
    }

    public void testCreateSimpleNegative() {
        String[] hostnames = {null, ":", ""};

        for (String hostname : hostnames) {
            try {
                new Server(LOGGER, hostname);
                fail("Object creation successful.");
            } catch (Exception e) {
                /* NOOP */
            }
        }
    }

    public void testGetStreamSessionPositive() throws MalformedURLException {
        String hostname = "localhost";
        String[] mountpoints = {"/test", "/a/b"};

        for (String mountpoint : mountpoints) {
            Alias alias = new Alias(LOGGER, new URL("http://" + hostname + mountpoint));
            Session session = alias.createSession();

            assertNotNull(session);
        }
    }
}