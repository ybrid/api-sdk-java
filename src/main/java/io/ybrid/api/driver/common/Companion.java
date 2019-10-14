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

    @Override
    public void onView() throws IOException {
        URLConnection connection = onView.openConnection();
        connection.setDoInput(false);
        connection.setDoOutput(false);
        connection.connect();
    }

    @Override
    public String getAlternativeText() {
        return alternativeText;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public URL getStaticResource() {
        return staticResource;
    }

    @Override
    public URL getOnClick() {
        return onClick;
    }

    @Override
    public URL getOnView() {
        return onView;
    }
}
