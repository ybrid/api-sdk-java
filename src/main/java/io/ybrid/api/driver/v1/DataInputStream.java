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

package io.ybrid.api.driver.v1;

import io.ybrid.api.Session;
import io.ybrid.api.StreamInputStream;
import io.ybrid.api.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

class DataInputStream extends StreamInputStream {
    private static final int SLEEP_TIME = 179; /* [ms] */
    private static final int SERVER_TIMEOUT = 6000; /* [ms] */

    private final Session session;
    private final LinkedList<byte[]> bufferQueue = new LinkedList<>();
    private String contentType = null;
    private InputStream current;
    private FetcherThread thread = new FetcherThread(this);

    private static class FetcherThread extends Thread {
        private static final int MAX_FILL = 20;

        private final DataInputStream parent;

        FetcherThread(DataInputStream parent) {
            this.parent = parent;
        }

        private void fetch() throws IOException {
            URL url = parent.session.getStreamURL();
            URLConnection connection = url.openConnection();
            InputStream inputStream;
            String contentType;
            byte[] data;

            parent.session.getServer().getLogger().finer("FetcherThread.fetch: fetching...");

            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.setRequestProperty("Accept", "application/x-ybrid-discrete");

            connection.connect();

            contentType = connection.getContentType();

            if (parent.contentType == null)
                parent.contentType = contentType;

            if (!parent.contentType.equals(contentType))
                throw new IOException("Server sent illegal response");

            inputStream = connection.getInputStream();
            data = Utils.slurpToByteArray(inputStream);
            inputStream.close();

            synchronized (parent) {
                parent.bufferQueue.push(data);
            }

            parent.session.getServer().getLogger().finer("FetcherThread.fetch: fetched " + data.length + " bytes");
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                boolean needSleep = false;

                synchronized (parent) {
                    if (parent.bufferQueue.size() >= MAX_FILL) {
                        needSleep = true;
                    }
                }

                if (needSleep) {
                    try {
                        sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        return;
                    }
                    continue;
                }

                try {
                    fetch();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    public DataInputStream(Session session) {
        this.session = session;
        thread.start();
    }

    private void assertInputStream() throws IOException {
        if (current != null)
            return;

        for (int i = 0; i < (SERVER_TIMEOUT / SLEEP_TIME); i++) {
            if (bufferQueue.size() > 0)
                break;

            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException ignored) {
                break;
            }
        }

        if (bufferQueue.size() > 0) {
            current = new ByteArrayInputStream(bufferQueue.remove());
            return;
        }

        throw new IOException("Can not talk to server anymore. Retry later.");
    }

    private void requestNext() throws IOException {
        current = null;
        assertInputStream();
    }

    public String getContentType() throws IOException {
        assertInputStream();
        return contentType;
    }

    @Override
    public int read() throws IOException {
        int ret;

        assertInputStream();

        ret = current.read();

        if (ret == -1) {
            requestNext();
            ret = current.read();
        }

        return ret;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int ret;

        assertInputStream();

        ret = current.read(b);

        if (ret == -1) {
            requestNext();
            ret = current.read(b);
        }

        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret;

        assertInputStream();

        ret = current.read(b, off, len);

        if (ret == -1) {
            requestNext();
            ret = current.read(b, off, len);
        }

        return ret;
    }

    @Override
    public long skip(long n) throws IOException {
        assertInputStream();
        return current.skip(n);
    }

    @Override
    public void close() throws IOException {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }

        if (current != null) {
            current.close();
            current = null;
        }
    }
}
