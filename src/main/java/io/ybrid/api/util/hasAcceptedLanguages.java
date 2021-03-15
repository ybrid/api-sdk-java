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

package io.ybrid.api.util;

import io.ybrid.api.util.QualityMap.LanguageMap;
import io.ybrid.api.util.QualityMap.QualityMap;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Interface implemented by classes providing a list of accepted languages.
 */
public interface hasAcceptedLanguages {
    /**
     * Get list of accepted languages.
     * <p>
     * For HTTP based protocols:
     * If this returns {@code null} no {@code Accept-Language:}-header should be generated.
     *
     * @return List of languages accepted or {@code null}.
     * @deprecated Use {@link #getAcceptedLanguagesMap()}
     */
    @Deprecated
    @Nullable
    default Map<String, Double> getAcceptedLanguages() {
        return Utils.transform(getAcceptedLanguagesMap(), QualityMap::toDoubleMap);
    }

    /**
     * Get list of accepted languages.
     * <P>
     * For HTTP based protocols:
     * If this returns {@code null} no {@code Accept-Language:}-header should be generated.
     *
     * @return List of languages accepted or {@code null}.
     */
    @Nullable LanguageMap getAcceptedLanguagesMap();
}
