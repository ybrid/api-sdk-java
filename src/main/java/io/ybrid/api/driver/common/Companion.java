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

package io.ybrid.api.driver.common;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;

public class Companion implements io.ybrid.api.metadata.Companion {
    protected String alternativeText;
    protected int height;
    protected int width;
    protected int sequenceNumber;
    protected @Nullable URI staticResource;
    protected @Nullable URI onClick;
    protected @Nullable URI onView;

    @Override
    public void onView() throws IOException {
        if (onView != null) {
            final @NotNull URLConnection connection = onView.toURL().openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            connection.getInputStream().close();
        }
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

    @Contract(pure = true)
    @Override
    public @Nullable URI getStaticResourceURI() {
        return staticResource;
    }

    @Contract(pure = true)
    @Override
    public @Nullable URI getOnClickURI() {
        return onClick;
    }

    @Contract(pure = true)
    @Override
    public @Nullable URI getOnViewURI() {
        return onView;
    }
}
