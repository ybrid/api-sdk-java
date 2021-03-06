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

package io.ybrid.api.metadata;

import io.ybrid.api.bouquet.Service;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * @return Returns the current item.
     */
    Item getCurrentItem();

    /**
     * Get the Item that is expected to be played next.
     * @return Returns the next item or null.
     */
    @Nullable
    Item getNextItem();

    /**
     * Returns the current service the listener is attached to.
     * @return Returns the current service.
     */
    @NotNull
    Service getService();

    /**
     * Returns whether this Metadata is valid.
     * Metadata may become invalid after the current item finished playback or any other event.
     * If the Metadata is invalid the client must no longer use it and refresh it's Metadata state.
     * @return Returns validity of this Metadata.
     */
    boolean isValid();
}
