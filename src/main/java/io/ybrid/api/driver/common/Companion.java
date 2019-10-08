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

package io.ybrid.api.driver.common;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class Companion implements io.ybrid.api.Companion {
    protected String alternativeText;
    protected int height;
    protected int width;
    protected int sequenceNumber;
    protected URL staticResource;
    protected URL onClick;
    protected URL onView;

    private void ping(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setDoInput(false);
        connection.setDoOutput(false);
        connection.connect();
    }

    public void onClick() throws IOException {
        ping(onClick);
    }

    public void onView() throws IOException {
        ping(onView);
    }

    public String getAlternativeText() {
        return alternativeText;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public URL getStaticResource() {
        return staticResource;
    }

    public URL getOnClick() {
        return onClick;
    }

    public URL getOnView() {
        return onView;
    }
}
