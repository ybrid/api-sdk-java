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

import io.ybrid.api.util.ClockManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

/**
 * This class is used to store temporal validity.
 */
public final class TemporalValidity {
    /**
     * Represents a indefinite validity.
     */
    public static final @NotNull TemporalValidity INDEFINITELY_VALID = new TemporalValidity(null, null);
    /**
     * Represents a invalid validity.
     */
    public static final @NotNull TemporalValidity INVALID = new TemporalValidity(Instant.EPOCH, Instant.EPOCH);

    private final @Nullable Instant notBefore;
    private final @Nullable Instant notAfter;

    private TemporalValidity(@Nullable Instant notBefore, @Nullable Instant notAfter) {
        this.notBefore = notBefore;
        this.notAfter = notAfter;
    }

    /**
     * Create a new TemporalValidity with a start and end time.
     * The Instances must be obtained directly or indirectly from the {@link ClockManager}.
     * @param start The start time.
     * @param end The end time.
     * @return The new instance.
     * @see ClockManager
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TemporalValidity make(@NotNull Instant start, @NotNull Instant end) {
        return new TemporalValidity(start, end);
    }

    /**
     * Create a new TemporalValidity with a start and duration.
     * The Instances must be obtained directly or indirectly from the {@link ClockManager}.
     * @param start The start time.
     * @param duration The duration.
     * @return The new instance.
     * @see ClockManager
     */
    @Contract(value = "_, _ -> new", pure = true)
    public static @NotNull TemporalValidity make(@NotNull Instant start, @NotNull Duration duration) {
        return make(start, start.plus(duration));
    }

    /**
     * Create a new TemporalValidity with a duration from now.
     * @param duration The duration.
     * @return The new instance.
     */
    @Contract(value = "_ -> new", pure = true)
    public static @NotNull TemporalValidity makeFromNow(@NotNull Duration duration) {
        return make(ClockManager.now(), duration);
    }

    /**
     * Creates a new temporal translated validity.
     * @param translation The translation, positive to translate into the future.
     * @return The new validity.
     */
    @Contract(value = "_ -> new", pure = true)
    public @NotNull TemporalValidity translate(@NotNull Duration translation) {
        return new TemporalValidity(
                getNotBefore() != null ? getNotBefore().plus(translation) : null,
                getNotAfter() != null ? getNotAfter().plus(translation) : null);
    }

    /**
     * Gets the start time of this validity.
     * @return The start time or {@code null}.
     */
    @Contract(pure = true)
    public @Nullable Instant getNotBefore() {
        return notBefore;
    }

    /**
     * Gets the end time of this validity.
     * @return The end time or {@code null}.
     */
    @Contract(pure = true)
    public @Nullable Instant getNotAfter() {
        return notAfter;
    }

    /**
     * Returns whether this validity is still in the valid range.
     * @return The current validity.
     */
    @Contract(pure = true)
    public boolean isValid() {
        if (notBefore == null && notAfter == null) {
            return true;
        } else {
            final @NotNull Instant now = ClockManager.now();
            if (notBefore != null && Duration.between(notBefore, now).isNegative()) {
                return false;
            }

            if (notAfter != null && Duration.between(now, notAfter).isNegative()) {
                return false;
            }

            return true;
        }
    }
}
