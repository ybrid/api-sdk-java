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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This implements a map used to store the state of workarounds.
 * Each workaround can have a state of {@link TriState#TRUE} when enabled,
 * {@link TriState#FALSE} when disabled, or {@link TriState#AUTOMATIC} when
 * it should be auto-detected.
 * <P>
 * This map ensures all workarounds have a defined state at all times.
 * Class that set the value such as {@link #put(Workaround, TriState)} will check the validity of the value set.
 * Calls that remove items such as {@link #remove(Object)} instead reset them to the default state of
 * {@link TriState#AUTOMATIC}.
 * <P>
 * All values are initialised to {@link TriState#AUTOMATIC} after object creation.
 */
public class WorkaroundMap extends EnumMap<Workaround, TriState> {
    /**
     * The main constructor.
     */
    public WorkaroundMap() {
        super(Workaround.class);
        clear();
    }

    @Override
    public @NotNull TriState get(Object key) {
        return super.get(key);
    }

    @Override
    public TriState put(Workaround key, @NotNull TriState value) {
        return super.put(key, value);
    }

    @Override
    public TriState remove(Object key) {
        return super.put((Workaround)key, TriState.AUTOMATIC);
    }

    @Override
    public void putAll(Map<? extends Workaround, ? extends TriState> m) {
        for (final @NotNull Entry<? extends  Workaround, ? extends TriState> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        for (final @NotNull Workaround workaround : Workaround.values())
            put(workaround, TriState.AUTOMATIC);
    }

    /**
     * Enables a workaround.
     * @param workaround The workaround to enable.
     */
    public void enable(@NotNull Workaround workaround) {
        put(workaround, TriState.TRUE);
    }

    /**
     * Disabled a workaround.
     * @param workaround The workaround to disable.
     */
    public void disable(@NotNull Workaround workaround) {
        put(workaround, TriState.FALSE);
    }

    /**
     * Merge the given map into this one. By merging all enabled and disabled
     * states are copied but no automatic states.
     * @param map The map to merge.
     */
    public void merge(@NotNull WorkaroundMap map) {
        for (final @NotNull Entry<Workaround, TriState> entry : map.entrySet()) {
            if (entry.getValue() != TriState.AUTOMATIC)
                put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public String toString() {
        final @NotNull List<String> enabled = new ArrayList<>();
        final @NotNull List<String> disabled = new ArrayList<>();
        final @NotNull List<String> automatic = new ArrayList<>();

        for (final @NotNull Entry<Workaround, TriState> entry : entrySet()) {
            final @NotNull List<String> list;

            switch (entry.getValue()) {
                case TRI:
                    list = automatic;
                    break;
                case FALSE:
                    list = disabled;
                    break;
                case TRUE:
                    list = enabled;
                    break;
                default:
                    throw new RuntimeException("Unreachable code reached. VERY BAD.");
            }

            list.add(entry.getKey().toString().substring("WORKAROUND_".length()));
        }

        return "{Enabled: " + enabled +
                ", Disabled: " + disabled +
                ", Automatic: " + automatic +
                "}";
    }
}
