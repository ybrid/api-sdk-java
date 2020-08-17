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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

/**
 * This class provides a monotonous clock for use with session timings.
 * The clock is initially synchronized to the system time.
 */
public final class ClockManager {
    private static final @NotNull Clock clock = new Clock() {
        private final long offset = System.currentTimeMillis() - System.nanoTime() / 1_000_000;

        @Override
        public ZoneId getZone() {
            return ZoneId.of("UTC");
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return null;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(System.nanoTime() / 1_000_000 + offset);
        }
    };

    private ClockManager() {
    }

    /**
     * Gets a {@link Clock} object for this clock.
     * @return The Clock object.
     */
    @Contract(pure = true)
    public static @NotNull Clock getClock() {
        return clock;
    }

    /**
     * Queries the current time.
     * @return The current time.
     */
    public static @NotNull Instant now() {
        return clock.instant();
    }
}
