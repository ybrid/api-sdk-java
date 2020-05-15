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

package io.ybrid.api;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * This interface is implemented by objects representing an Item. A item roughly corresponds to a track.
 */
public interface Item extends hasIdentifier, hasDisplayName {
    /**
     * This allows access to the items Metadata.
     * @return Returns the map of metadata.
     */
    @NotNull
    Map<String, String> getMetadata();

    /**
     * Returns the type of the item.
     * The item type can be used by players to switch between audio profiles.
     * This can be useful to for example provide different settings for traffic announcements.
     * @return Returns the type of the item.
     */
    ItemType getType();

    /**
     * Return the total playback time of the item.
     *
     * @return Returns the playback time in [ms].
     * @deprecated Use {@link #getPlaybackLength()} instead.
     */
    @Deprecated
    default long getDuration() {
        return getPlaybackLength().toMillis();
    }

    /**
     * Return the total playback time of the item.
     * @return Returns the playback time.
     */
    Duration getPlaybackLength();

    /**
     * Returns the list of Companions as to be displayed while this item is played.
     * @return Returns the list of Companions.
     */
    @NotNull
    List<Companion> getCompanions();
}
