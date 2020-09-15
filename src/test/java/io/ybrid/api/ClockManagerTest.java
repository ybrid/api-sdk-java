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
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;

import static org.junit.Assert.*;

public class ClockManagerTest {
    static private void assertEquals(final @NotNull Instant expected, final @NotNull Instant actual) {
        final long al = expected.toEpochMilli();
        final long bl = actual.toEpochMilli();

        if (al < (bl - 10) || al > (bl + 10))
            org.junit.Assert.assertEquals(expected, actual);
    }

    @Test
    public void getClock() {
        final Clock clock = ClockManager.getClock();
        final @NotNull Instant ref;
        final @NotNull Instant now;

        assertNotNull(clock);

        ref = Instant.now();
        now = clock.instant();
        assertEquals(ref, now);

        org.junit.Assert.assertEquals("UTC", clock.getZone().getId());
    }

    @Test
    public void now() {
        final @NotNull Instant ref = Instant.now();
        final Instant now = ClockManager.now();

        assertNotNull(now);

        System.out.println("now = " + now);

        assertEquals(ref, now);
    }

    @Test
    public void testTicking() throws InterruptedException {
        for (int i = 0; i < 16; i++) {
            final @NotNull Instant ref = Instant.now();
            final @NotNull Instant got = ClockManager.now();

            System.out.println("i = " + i);
            assertEquals(ref, got);

            Thread.sleep(374);
        }
    }
}