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

package io.ybrid.api.util.QualityMap;

import io.ybrid.api.util.MediaType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This implements a basic {@link QualityMap} for {@link MediaType}s.
 */
public class MediaTypeMap extends QualityMap<MediaType> {
    /**
     * Creates an empty map.
     */
    public MediaTypeMap() {
        super(MediaType.STYLE);
    }

    /**
     * Creates an map with an initial set of {@link MediaType}s.
     */
    public MediaTypeMap(@NotNull QualityMap<? extends MediaType> initialValues) {
        super(MediaType.STYLE, initialValues);
    }

    /**
     * Creates an map with an initial set of {@link MediaType}s.
     */
    public MediaTypeMap(@NotNull Map<? extends MediaType, ?> initialValues) {
        super(MediaType.STYLE, initialValues);
    }

    @ApiStatus.Internal
    public static @Nullable MediaTypeMap createMap(@Nullable Map<?, ?> values) {
        final @NotNull MediaTypeMap ret;

        if (values == null)
            return null;

        ret = new MediaTypeMap();
        for (final @NotNull Map.Entry<?, ?> entry : values.entrySet()) {
            final @NotNull Object key = entry.getKey();
            final @NotNull Object value = entry.getKey();

            if (key instanceof MediaType) {
                ret.put((MediaType) key, value);
            } else if (key instanceof String) {
                ret.put(new MediaType((String) key), value);
            } else {
                throw new IllegalArgumentException("Bad type for key: " + key);
            }
        }

        return ret;
    }
}
