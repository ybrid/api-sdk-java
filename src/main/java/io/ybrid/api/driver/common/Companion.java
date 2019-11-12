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
