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

import java.util.Collection;

/**
 * The CapabilitySet contains a set of {@link Capability Capabilities} supported by a {@link SessionClient}.
 */
public interface CapabilitySet extends Iterable<Capability> {
    /**
     * Returns the size of the set. This should be avoided in favor of {@link #isEmpty()}.
     * @return the size of the set.
     */
    int size();

    /**
     * Returns whether the set is empty. In an empty set no {@link Capability Capabilities} are set.
     * @return whether the set is empty.
     */
    boolean isEmpty();

    /**
     * Checks for a specific {@link Capability} to be present in the set.
     * @param o the {@link Capability} toi check.
     * @return whether the {@link Capability} is present.
     */
    boolean contains(Capability o);

    /**
     * Checks whether all of a {@link Collection} of {@link Capability Capabilities} are present in the set.
     * @param c the {@link Collection} to check.
     * @return wheather all of the {@link Capability Capabilities} are present.
     */
    boolean containsAll(Collection<Capability> c);

    /**
     * This is a helper method that will create a new set suitable for player implementations.
     * The new set will be linked to the old set in that it will see updates.
     * The difference is that the returned set will contain {@link Capability#PLAYBACK} if
     * {@link Capability#PLAYBACK_URL} is present in the parent set.
     * @return the new set.
     */
    CapabilitySet makePlayerSet();
}
