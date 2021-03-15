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

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

public class LanguageMap extends QualityMap<String> {
    private static final @NotNull Style<String> STYLE = Style.createNoWildcards();

    /**
     * Creates an empty map.
     */
    public LanguageMap() {
        super(STYLE);
    }

    /**
     * Creates an map with an initial set of languages.
     */
    public LanguageMap(@NotNull QualityMap<? extends String> initialValues) {
        super(STYLE, initialValues);
    }

    /**
     * Creates an map with an initial set of languages.
     */
    public LanguageMap(@NotNull Map<? extends String, ?> initialValues) {
        super(STYLE, initialValues);
    }

    /**
     * Creates an map with an initial set of languages.
     */
    public LanguageMap(@NotNull Collection<Locale.@NotNull LanguageRange> initialValues) {
        super(STYLE);
        putAll(initialValues);
    }

    /**
     * Adds a {@link Locale.LanguageRange} to this map.
     * @param range The range to add.
     */
    public void put(@NotNull Locale.LanguageRange range) {
        put(range.getRange(), range.getWeight());
    }

    /**
     * Adds a {@link Collection} of {@link Locale.LanguageRange} to this map.
     * @param values The ranges to add.
     */
    public void putAll(@NotNull Collection<Locale.@NotNull LanguageRange> values) {
        for (final @NotNull Locale.LanguageRange range : values) {
            put(range);
        }
    }
}
