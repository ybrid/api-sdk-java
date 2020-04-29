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

package io.ybrid.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public interface PlayoutInfo {
    /**
     * Returns the information on the current swap state.
     * @return Returns the current SwapInfo.
     */
    @NotNull
    SwapInfo getSwapInfo();

    /**
     * Returns the time to the next Item.
     * This is measured with the current system clock so that every call to this method will give an updated result.
     * may return a negative number if the start of the next Item is in the past.
     * @return Returns the time to the next item or null if unknown.
     */
    @Nullable
    Duration getTimeToNextItem();

    /**
     * This returns time the the server side clock for the current client is behind
     * live playback. This is zero for live playback, positive for delayed playback,
     * and can be negative if the current client is ahead of live playback.
     * @return Returns the time playback is behind live or null if unknown.
     */
    @Nullable
    default Duration getBehindLive() {
        return null;
    }
}
