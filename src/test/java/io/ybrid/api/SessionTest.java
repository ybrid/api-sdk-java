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

import junit.framework.TestCase;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;

public class SessionTest extends TestCase {
    public void testGetStreamURLPositive() throws IOException {
        for (URL aliasUrl : NetworkHelper.getAliases()) {
            Alias alias;
            Session session;
            URL url;

            alias = new Alias(aliasUrl);
            assertNotNull(alias);

            session = alias.createSession();
            assertNotNull(session);

            session.connect();

            url = session.getStreamURL();
            assertNotNull(url);

            session.close();
        }
    }

    public void testSessionInfo() throws IOException, InterruptedException {
        for (URL aliasUrl : NetworkHelper.getAliases()) {
            Session session = new Alias(aliasUrl).createSession();
            Metadata oldMetadata = null;
            Metadata newMetadata;

            assertNotNull(session);

            session.connect();

            assertEquals(200, NetworkHelper.pingURL(session.getStreamURL()));

            for (int i = 0; i < 10; i++) {
                Instant start;
                Instant end;

                start = Instant.now();
                newMetadata = session.getMetadata();
                end = Instant.now();

                assertNotNull(newMetadata);

                System.out.println("i = " + i + ", end - start = " + Duration.between(start, end).toMillis() + "ms, (oldMetadata == newMetadata) = " + (oldMetadata == newMetadata));

                oldMetadata = newMetadata;
                Thread.sleep(100);
            }

            session.close();
        }
    }
}
