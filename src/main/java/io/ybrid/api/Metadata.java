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
