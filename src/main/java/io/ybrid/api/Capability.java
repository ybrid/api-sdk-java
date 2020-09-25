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

import io.ybrid.api.metadata.Item;

/**
 * This enum contains all possible capabilities which might be supported by a {@link Session}.
 */
public enum Capability {
    /**
     * The {@link Session} supports accessing an transport for audio playback.
     */
    AUDIO_TRANSPORT,
    /**
     * The {@link Session} supports playback.
     * This is not a feature of the Session itself but may be used by classes that implement {@link Session}.
     */
    PLAYBACK,
    /**
     * The {@link Session} supports swapping items.
     */
    SWAP_ITEM,
    /**
     * The {@link Session} supports swapping services.
     */
    SWAP_SERVICE,
    /**
     * The {@link Session} supports winding back to the live portion of the content.
     */
    WIND_TO_LIVE,
    /**
     * The {@link Session} supports winding to a specific point in time.
     */
    WIND_TO,
    /**
     * The {@link Session} supports winding relative to the current position.
     */
    WIND,
    /**
     * The {@link Session} supports skipping forward to the next {@link Item}.
     */
    SKIP_FORWARDS,
    /**
     * The {@link Session} supports skipping backwards to the previous {@link Item}.
     */
    SKIP_BACKWARDS;
}
