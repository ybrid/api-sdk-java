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

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;

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
    default CapabilitySet makePlayerSet() {
        return new CapabilitySet() {
            private final CapabilitySet parent = CapabilitySet.this;

            @Override
            @NotNull
            public Iterator<Capability> iterator() {
                if (contains(Capability.PLAYBACK)) {
                    EnumSet<Capability> n = parent.toSet();
                    n.add(Capability.PLAYBACK);
                    return Collections.unmodifiableSet(n).iterator();
                } else {
                    return parent.iterator();
                }
            }

            @Override
            public int size() {
                int size = parent.size();

                if (contains(Capability.PLAYBACK_URL))
                    size++;

                return size;
            }

            @Override
            public boolean isEmpty() {
                return parent.isEmpty();
            }

            @Override
            public boolean contains(Capability o) {
                if (o == Capability.PLAYBACK && contains(Capability.PLAYBACK_URL))
                    return true;
                return parent.contains(o);
            }

            @Override
            public boolean containsAll(Collection<Capability> c) {
                if (!c.contains(Capability.PLAYBACK)) {
                    return parent.containsAll(c);
                }

                for (Capability cap : c) {
                    if (!contains(cap))
                        return false;
                }

                return true;
            }

            @Override
            public CapabilitySet makePlayerSet() {
                return parent.makePlayerSet();
            }

            @Override
            public EnumSet<Capability> toSet() {
                return parent.toSet();
            }
        };
    }

    /**
     * This builds a {@link EnumSet} that corresponds to the current state of the set.
     * The returned {@link EnumSet} is a copy and can be copied by the caller at will.
     * It will not be updated by changes of the object it was obtained.
     *
     * @return Returns a {@link EnumSet} representing the current state.
     */
    EnumSet<Capability> toSet();

    /**
     * This builds a new {@code CapabilitySet} using given {@code inputSet}.
     * A copy of the set is made so modifications to it will not propagate.
     *
     * @param inputSet The source {@link EnumSet} to use.
     * @return A new {@code CapabilitySet} build from the {@code inputSet}.
     */
    static CapabilitySet fromSet(EnumSet<Capability> inputSet) {
        final EnumSet<Capability> set = inputSet.clone();

        return new CapabilitySet() {
            @Override
            public int size() {
                return set.size();
            }

            @Override
            public boolean isEmpty() {
                return set.isEmpty();
            }

            @Override
            public boolean contains(Capability o) {
                return set.contains(o);
            }

            @Override
            public boolean containsAll(Collection<Capability> c) {
                return set.containsAll(c);
            }

            @Override
            public EnumSet<Capability> toSet() {
                return set.clone();
            }

            @Override
            @NotNull
            public Iterator<Capability> iterator() {
                return Collections.unmodifiableSet(set).iterator();
            }
        };
    }
}
