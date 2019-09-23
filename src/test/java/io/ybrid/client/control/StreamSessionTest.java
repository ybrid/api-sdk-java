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

import junit.framework.TestCase;

import java.net.MalformedURLException;
import java.net.URL;

public class StreamSessionTest extends TestCase {
    public void testGetStreamURLPositive() throws MalformedURLException {
        String hostname = "localhost";
        String[] mountpoints = {"/test", "/a/b"};

        for (String mountpoint : mountpoints) {
            ServerSession serverSession = new ServerSession(hostname);
            StreamSession streamSession = serverSession.getStreamSession(mountpoint);
            URL url;

/*
            streamSession.connect();

            url = streamSession.getStreamURL();

            assertNotNull(url);
*/
        }
    }
}
