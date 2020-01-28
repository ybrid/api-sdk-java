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

import java.util.*;

/**
 * This implements {@link io.ybrid.api.CapabilitySet} for internal use only.
 */
public class CapabilitySet implements io.ybrid.api.CapabilitySet {
    private final EnumSet<Capability> set = EnumSet.noneOf(Capability.class);

    @Override
    public Iterator<Capability> iterator() {
        return Collections.unmodifiableSet(set).iterator();
    }

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

    void add(Capability o) {
        set.add(o);
    }

    void add(Capability[] o) {
        set.addAll(Arrays.asList(o));
    }

    @Override
    public io.ybrid.api.CapabilitySet makePlayerSet() {
        return new io.ybrid.api.CapabilitySet() {
            private CapabilitySet parent = CapabilitySet.this;

            @Override
            public Iterator<Capability> iterator() {
                if (contains(Capability.PLAYBACK)) {
                    EnumSet<Capability> n = parent.set.clone();
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
            public io.ybrid.api.CapabilitySet makePlayerSet() {
                return parent.makePlayerSet();
            }
        };
    }
}
