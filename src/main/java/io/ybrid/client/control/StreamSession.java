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

import java.net.MalformedURLException;
import java.net.URL;

public class StreamSession implements Connectable {
    private boolean connected = false;
    private ServerSession serverSession;
    private String mountpoint;

    private static void assertValidMountpoint(String mountpoint) throws MalformedURLException {
        if (!mountpoint.startsWith("/"))
            throw new MalformedURLException();
    }

    private void assertConnected() {
        if (!isConnected())
            throw new IllegalStateException("Not connected");
    }

    StreamSession(ServerSession serverSession, String mountpoint) throws MalformedURLException {
        assertValidMountpoint(mountpoint);
        this.serverSession = serverSession;
        this.mountpoint = mountpoint;
    }

    public URL getStreamURL() throws MalformedURLException {
        assertConnected();
        return new URL(serverSession.getProtocol(), serverSession.getHostname(), serverSession.getPort(), mountpoint);
    }

    @Override
    public void connect() {
        /* TODO: Obtain a valid session. */
        connected = true;
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }
}
