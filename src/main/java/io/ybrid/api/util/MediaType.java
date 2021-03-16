/*
 * Copyright (c) 2021 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.util;

import io.ybrid.api.util.QualityMap.QualityMap;
import io.ybrid.api.util.QualityMap.Style;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a Media Type (such as used by the HTTP Content-Type header).
 */
public final class MediaType implements Serializable {
    private static final long serialVersionUID = -2955670942461535881L;

    /* --------[ Special Media Types ]-------- */
    /**
     * Any Media type, used for {@code Accept:}-Headers.
     */
    public static final @NotNull MediaType MEDIA_TYPE_ANY = new MediaType("*/*");
    /* --------[ Official Media Types ]-------- */
    /**
     * Any stream of octets. Often used as fallback.
     */
    public static final @NotNull MediaType MEDIA_TYPE_APPLICATION_OCTET_STREAM = new MediaType("application/octet-stream");
    /**
     * Ogg with any content.
     */
    public static final @NotNull MediaType MEDIA_TYPE_APPLICATION_OGG = new MediaType("application/ogg");
    /**
     * Ogg with audio content.
     */
    public static final @NotNull MediaType MEDIA_TYPE_AUDIO_OGG = new MediaType("audio/ogg");
    /**
     * MP3.
     */
    public static final @NotNull MediaType MEDIA_TYPE_AUDIO_MPEG = new MediaType("audio/mpeg");

    /* --------[ Other things ]-------- */
    /**
     * The {@link Style} used for {@link QualityMap}.
     */
    public static final @NotNull Style<MediaType> STYLE = value -> {
        final @NotNull Set<MediaType> ret = new HashSet<>();
        final @NotNull String v = value.toString();

        if (value.hasParameters()) {
            ret.add(new MediaType(v.substring(0, v.indexOf(';'))));
        }

        ret.add(new MediaType(v.substring(0, v.indexOf('/')) + "/*"));
        ret.add(MEDIA_TYPE_ANY);

        return ret.toArray(new MediaType[0]);
    };

    private final @NotNull String mediaType;

    /**
     * Main constructor.
     * @param mediaType The string representing the Media Type.
     */
    @Contract(pure = true)
    public MediaType(@NotNull String mediaType) {
        this.mediaType = mediaType.trim();
    }

    /**
     * Returns whether this Media Type has parameters.
     * @return Whether this Media Type has parameters.
     */
    @Contract(pure = true)
    public boolean hasParameters() {
        return mediaType.contains(";");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MediaType mediaType1 = (MediaType) o;
        return mediaType.equals(mediaType1.mediaType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(serialVersionUID, mediaType);
    }

    @Override
    public String toString() {
        return mediaType;
    }
}
