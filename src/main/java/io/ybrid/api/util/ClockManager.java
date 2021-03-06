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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.logging.Logger;

/**
 * This class provides a monotonous clock for use with session timings.
 * The clock is initially synchronized to the system time.
 */
public final class ClockManager {
    static final Logger LOGGER = Logger.getLogger(ClockManager.class.getName());

    private static final @NotNull Clock clock = new Clock() {
        private final long offset = System.currentTimeMillis() - System.nanoTime() / 1_000_000;
        private long last = Long.MIN_VALUE;

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
            long now = System.nanoTime() / 1_000_000 + offset;
            if (now < last) {
                LOGGER.info("Clock jumped backwards: past was " + last + " and now is " + now + " jump by " + (now - last));
                now = last;
            }
            last = now;
            return Instant.ofEpochMilli(now);
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
