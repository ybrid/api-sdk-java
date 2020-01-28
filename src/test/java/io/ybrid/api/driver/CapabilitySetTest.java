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

package io.ybrid.api.driver;

import io.ybrid.api.Capability;
import io.ybrid.api.CapabilitySet;
import junit.framework.TestCase;

public class CapabilitySetTest extends TestCase {
    public void testEmptySet() {
        CapabilitySet set = new io.ybrid.api.driver.CapabilitySet();
        CapabilitySet playerSet;

        assertTrue(set.isEmpty());
        assertEquals(set.size(), 0);

        playerSet = set.makePlayerSet();
        assertNotNull(playerSet);
        assertTrue(playerSet.isEmpty());
        assertEquals(playerSet.size(), 0);
    }

    public void testSwapOnlySet() {
        io.ybrid.api.driver.CapabilitySet set = new io.ybrid.api.driver.CapabilitySet();
        CapabilitySet playerSet;

        set.add(Capability.SWAP_ITEM);

        assertFalse(set.isEmpty());
        assertEquals(set.size(), 1);
        assertTrue(set.contains(Capability.SWAP_ITEM));
        assertFalse(set.contains(Capability.PLAYBACK));

        playerSet = set.makePlayerSet();
        assertNotNull(playerSet);
        assertFalse(playerSet.isEmpty());
        assertEquals(playerSet.size(), 1);
        assertTrue(playerSet.contains(Capability.SWAP_ITEM));
        assertFalse(playerSet.contains(Capability.PLAYBACK));
    }

    public void testPlaybackURLSet() {
        io.ybrid.api.driver.CapabilitySet set = new io.ybrid.api.driver.CapabilitySet();
        CapabilitySet playerSet;

        set.add(Capability.PLAYBACK_URL);

        assertFalse(set.isEmpty());
        assertEquals(set.size(), 1);
        assertFalse(set.contains(Capability.SWAP_ITEM));
        assertFalse(set.contains(Capability.PLAYBACK));
        assertTrue(set.contains(Capability.PLAYBACK_URL));

        playerSet = set.makePlayerSet();
        assertNotNull(playerSet);
        assertFalse(playerSet.isEmpty());
        assertEquals(playerSet.size(), 2);
        assertFalse(playerSet.contains(Capability.SWAP_ITEM));
        assertTrue(playerSet.contains(Capability.PLAYBACK));
        assertTrue(playerSet.contains(Capability.PLAYBACK_URL));
    }
}