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

import java.util.Locale;
import java.util.Objects;

/**
 * Instances of this class represent specific qualities.
 * Qualities are based on RFC 7231 section 5.3.
 * The range of a quality value is 0.000 (not acceptable) to 1.000 (best quality)
 * with a resolution of 0.001.
 * <p>
 * Working with qualities using floating points such as double should be avoided
 * to avoid floating point errors. This implementation uses a fixed-point implementation internally.
 */
public final class Quality implements Comparable<Quality> {
    /**
     * Constant for a not acceptable quality (0.000).
     */
    public static final Quality NOT_ACCEPTABLE = new Quality(0);
    /**
     * Constant for the most acceptable quality (1.000).
     */
    public static final Quality MOST_ACCEPTABLE = new Quality(1000);
    /**
     * Constant for the least acceptable quality (0.001).
     * This is the smallest quality that is still acceptable.
     */
    public static final Quality LEAST_ACCEPTABLE = new Quality(1);

    private static final double SCALE_DOUBLE = 1000.;

    private final int quality;

    private static void assertValue(int value) {
        if (value < 0 || value > 1000)
            throw new IllegalArgumentException("Quality value is out of range");
    }

    private Quality(int quality) {
        assertValue(quality);
        this.quality = quality;
    }

    /**
     * Constructs quality value from a {@link String}.
     * @param str The string to use.
     * @return The quality value.
     */
    public static @NotNull Quality valueOf(@NotNull String str) {
        return valueOf(Double.parseDouble(str));
    }

    /**
     * Constructs quality value from a double.
     * @param d The double to use.
     * @return The quality value.
     */
    public static @NotNull Quality valueOf(double d) {
        return new Quality((int) (d * SCALE_DOUBLE));
    }

    /**
     * Returns whether the quality is still acceptable ({@code quality > 0.000}).
     * @return Whether the quality is acceptable.
     */
    @Contract(pure = true)
    public boolean isAcceptable() {
        return !equals(NOT_ACCEPTABLE);
    }

    /**
     * This converts the quality to a double.
     * @return The double representing the quality.
     */
    @Contract(pure = true)
    public double toDouble() {
        return quality / SCALE_DOUBLE;
    }

    /**
     * This converts the quality to a {@link String}.
     * @return The String representing the quality.
     */
    @SuppressWarnings("HardCodedStringLiteral")
    @Contract(pure = true)
    @Override
    public @NotNull String toString() {
        if (equals(MOST_ACCEPTABLE)) {
            return "1";
        } else if (equals(NOT_ACCEPTABLE)) {
            return "0";
        } else {
            if ((quality % 100) == 0) {
                return String.format(Locale.ROOT, "0.%01d", quality / 100);
            } else if ((quality % 10) == 0) {
                return String.format(Locale.ROOT, "0.%02d", quality / 10);
            } else {
                return String.format(Locale.ROOT, "0.%03d", quality);
            }
        }
    }

    @Contract(pure = true)
    @Override
    public int compareTo(@NotNull Quality o) {
        return this.quality - o.quality;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quality quality1 = (Quality) o;
        return quality == quality1.quality;
    }

    @Override
    public int hashCode() {
        return Objects.hash(quality);
    }
}
