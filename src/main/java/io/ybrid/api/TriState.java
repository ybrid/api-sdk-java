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

package io.ybrid.api;

import org.jetbrains.annotations.NotNull;

/**
 * Generic tri-state class.
 */
public enum TriState {
    /**
     * Third state.
     */
    TRI,
    /**
     * False or negative state.
     */
    FALSE,
    /**
     * True or positive state.
     */
    TRUE;

    /**
     * Alias for the third state.
     */
    public static final @NotNull TriState NULL = TRI;
    /**
     * Alias for the third state.
     */
    public static final @NotNull TriState AUTOMATIC = TRI;

    /**
     * Conversion of a TriState value to a boolean.
     * @param def The value used for the third state.
     * @return The corresponding boolean value.
     */
    public boolean toBool(boolean def) {
        switch (this) {
            case FALSE:
                return false;
            case TRUE:
                return true;
        }

        return def;
    }
}
