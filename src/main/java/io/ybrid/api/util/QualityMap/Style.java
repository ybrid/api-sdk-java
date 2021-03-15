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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

/**
 * This interface is used to implement different styles of {@link QualityMap}s.
 * @param <T> The base type used by the {@link QualityMap}.
 */
public interface Style<T> {
    /**
     * Gets wildcards for the given value. Ordered from most exact to least exact match.
     *
     * @param value The base value to use.
     * @return An array of wildcards for this value.
     */
    @NotNull T[] getWildcards(@NotNull T value);

    /**
     * Creates a Style instance that has no wildcards defined.
     *
     * @param <K> The base type used by the {@link QualityMap}.
     * @return The new instance.
     */
    @Contract(pure = true)
    static <K> @NotNull Style<K> createNoWildcards() {
        return value -> {
            //noinspection unchecked
            return (K[]) Array.newInstance(value.getClass(), 0);
        };
    }
}
