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

import java.net.URI;

/**
 * This is the base interface for interfaces and classes implementing metadata for tracks.
 */
public interface TrackMetadata {
    /**
     * Gets a generic title to display along the track.
     * This is a last-resort fallback that should only be used if
     * no structured higher level metadata is available.
     *
     * @return A title for the track or {@code null}.
     */
    @Nullable String getDisplayTitle();

    /**
     * Gets the genre for the track as a string.
     * This string might not be fully normalized but should be suitable for the user to recognise
     * the genre in question.
     *
     * @return A genre or {@code null}.
     */
    default @Nullable String getGenre() {
        return null;
    }

    /**
     * Gets a generic comment that can be displayed alongside the track.
     * The comment is in no way processed or normalised and may be in any language.
     * The comment must be rendered as text without processing of any meta characters.
     *
     * @return A comment or {@code null}.
     */
    default @Nullable String getComment() {
        return null;
    }

    /**
     * A {@link URI} that the user can navigate to in order to find more information
     * about the track. This can include URIs of database services or online stores.
     * This must not be used for linking advertisements or shopping related services
     * that are not related to the track.
     *
     * @return A URI to more information about the track or {@code null}.
     */
    default @Nullable URI getInfoURI() {
        return null;
    }
}
