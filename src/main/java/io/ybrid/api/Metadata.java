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

package io.ybrid.api;

/**
 * This Interface is implemented by Metadata objects.
 *
 * Metadata objects contain information for a section of a stream.
 */
public interface Metadata {
    /**
     * Returns the currently playing Item.
     * The result can be used to access metadata for the item for a display of what is currently played.
     *
     * @return Retruns the current item.
     */
    Item getCurrentItem();

    /**
     * Get the Item that is expected to be played next.
     * @return Returns the next item or null.
     */
    Item getNextItem();

    /**
     * Get the currently selected bitrate.
     * The exact bitrate can only be obtained from the audio decoder.
     * @return Returns the current bitrate in [Bit/s].
     */
    int getCurrentBitRate();

    /**
     * Returns the current service the listener is attached to.
     * @return Returns the current service.
     */
    Service getService();

    /**
     * Returns the information on the current swap state.
     * @return Returns the current SwapInfo.
     */
    SwapInfo getSwapInfo();

    /**
     * Returns the time to the next Item.
     * This is measured with the current system clock so that every call to this method will give an updated result.
     * may return a negative number if the start of the next Item is in the past.
     * @return Returns the time to the next item in [ms].
     */
    long getTimeToNextItem();

    /**
     * Returns whether this Metadata is valid.
     * Metadata may become invalid after the current item finished playback or any other event.
     * If the Metadata is invalid the client must no longer use it and refresh it's Metadata state.
     * @return Returns validity of this Metadata.
     */
    boolean isValid();
}
