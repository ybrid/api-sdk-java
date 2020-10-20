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

package io.ybrid.api.transport;

/**
 * This enum lists all states a connection of a transport can have.
 */
public enum TransportConnectionState {
    /**
     * The transport is currently disconnected or idle.
     */
    DISCONNECTED,
    /**
     * The transport is being connected but the connection has not yet reached the stage of
     * data flow.
     */
    CONNECTING,
    /**
     * The transport's connection has been established. Data can flow.
     */
    CONNECTED,
    /**
     * The transport is still connected, however the transport's connection received
     * End-Of-File from the peer.
     */
    RECEIVED_EOF,
    /**
     * The transport's connection is being disconnected. Data may no longer flow but new data
     * is still accepted (e.g. from draining buffers).
     */
    DISCONNECTING,
    /**
     * The transport's connection could not be established or was lost at any non-disconnected state.
     * After a transport's connection reached this state no attempt must be made to reconnect.
     * Rather a new {@link TransportDescription} must be requested.
     */
    ERROR;
}
