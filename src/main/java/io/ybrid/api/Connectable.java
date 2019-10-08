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

package io.ybrid.api;

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
