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

import org.jetbrains.annotations.Nullable;

/**
 * This interface is implemented by classes providing track metadata with a common structure.
 */
public interface BasicTrackMetadata extends TrackMetadata {
    /**
     * Gets the title of the track.
     * The title must not include other information such as the artist or the version.
     *
     * @return The title of the track or {@code null}.
     * @see #getVersion()
     */
    @Nullable String getTitle();

    /**
     * Gets the version of this track.
     * Common examples are "{@code Radio edit}", and "{@code Extended remix}".
     *
     * @return The version of the track or {@code null}.
     * @see #getTitle()
     */
    @Nullable String getVersion();

    /**
     * Gets the artist of the track.
     *
     * @return The artist of the track or {@code null}.
     */
    @Nullable String getArtist();

    /**
     * Gets the album the track belongs to.
     *
     * @return The album the track belongs to or {@code null}.
     */
    @Nullable String getAlbum();

    @Override
    default @Nullable String getDisplayTitle() {
        final @Nullable String title = getTitle();
        final @Nullable String version = getVersion();
        final @Nullable String artist = getArtist();
        final @Nullable String album = getAlbum();
        String ret;

        if (title == null) {
            return null;
        } else {
            if (version == null) {
                ret = title;
            } else {
                ret = title + " (" + version + ")";
            }
        }

        if (artist != null) {
            ret = artist + " - " + ret;
        }

        if (album != null) {
            ret = album + " - " + ret;
        }

        return ret;
    }
}
