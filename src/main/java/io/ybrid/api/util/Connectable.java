/*
 * Copyright (c) 2020 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Interface that is implemented by objects that can be connected to a remote resource.
 */
public interface Connectable extends Closeable {
    /**
     * Connect to the remote resource.
     * @throws IOException Thrown when a connection can not be established.
     */
    void connect() throws IOException;

    /**
     * Disconnects from a the remote resource.
     */
    void disconnect();

    /**
     * Returns whether the object is connected.
     * @return Whether the object is connected.
     */
    boolean isConnected();

    /**
     * Closes resources open by this object.
     * By default disconnect from the remote resource.
     * @throws IOException Thrown in case of I/O-Errors.
     */
    @Override
    default void close() throws IOException {
        disconnect();
    }
}
