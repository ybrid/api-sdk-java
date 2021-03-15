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
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The QualityMap implements a map used to store items with a given {@link Quality} assigned.
 * It supports a subset of the functions of the {@link Map} interface.
 * @param <T> The base type of the items in this map.
 */
public class QualityMap<T> {
    private final @NotNull Map<@NotNull T, @NotNull Quality> map = new HashMap<>();
    private final @NotNull Style<T> style;

    /**
     * Creates a new instance with initially no items.
     *
     * @param style The {@link Style} to use.
     */
    public QualityMap(@NotNull Style<T> style) {
        this.style = style;
    }

    /**
     * Creates a new instance with a set of initial items.
     *
     * @param style The {@link Style} to use.
     * @param initialValues The initial items.
     */
    public QualityMap(@NotNull Style<T> style, @NotNull QualityMap<? extends T> initialValues) {
        this(style);
        putAll(initialValues);
    }

    /**
     * Creates a new instance with a set of initial items.
     * This uses the same rules as {@link #putAll(Map)}.
     *
     * @param style The {@link Style} to use.
     * @param initialValues The initial items.
     */
    public QualityMap(@NotNull Style<T> style, @NotNull Map<? extends T, ?> initialValues) {
        this(style);
        putAll(initialValues);
    }

    /**
     * This creates a copy of this map with the qualities represented as {@code double}.
     * @return The copy.
     * @see Quality#toDouble()
     */
    @Contract(pure = true)
    public @NotNull Map<T, Double> toDoubleMap() {
        final @NotNull Map<T, Double> ret = new HashMap<>(size());

        for (final @NotNull Map.Entry<@NotNull T, @NotNull Quality> entry : map.entrySet()) {
            ret.put(entry.getKey(), entry.getValue().toDouble());
        }

        return ret;
    }

    /**
     * This creates a copy of this map with the qualities represented as {@link String}.
     * @return The copy.
     * @see Quality#toString()
     */
    @Contract(pure = true)
    public @NotNull Map<T, String> toStringMap() {
        final @NotNull Map<T, String> ret = new HashMap<>(size());

        for (final @NotNull Map.Entry<@NotNull T, @NotNull Quality> entry : map.entrySet()) {
            ret.put(entry.getKey(), entry.getValue().toString());
        }

        return ret;
    }

    /**
     * This adds a item to this map.
     * <P>
     * If the {@code value} is not a {@link Quality} it is automatically converted to one.
     * The following conversions are currently supported:
     * <ul>
     *     <li>From {@link Quality} without conversion.</li>
     *     <li>From {@code double} by calling {@link Quality#valueOf(double)}.</li>
     *     <li>From {@link String} by calling {@link Quality#valueOf(String)}.</li>
     * </ul>
     *
     * @param key The item to add.
     * @param value The quality.
     */
    public void put(@NotNull T key, @NotNull Object value) {
        if (value instanceof Quality) {
            put(key, (Quality) value);
        } else if (value instanceof Double) {
            put(key, Quality.valueOf((Double)value));
        } else if (value instanceof String) {
            put(key, Quality.valueOf((String) value));
        } else {
            throw new IllegalArgumentException("Invalid type for key: " + key + ": " + value.getClass().getName());
        }
    }

    /**
     * This adds a item to this map.
     *
     * @param key The item to add.
     * @param value The quality.
     */
    public void put(@NotNull T key, @NotNull Quality value) {
        map.put(key, value);
    }

    /**
     * Adds all values from the given map to this map.
     * @param values The map to add the values from.
     */
    public void putAll(@NotNull QualityMap<? extends T> values) {
        for (final @NotNull Map.Entry<@NotNull ? extends T, @NotNull Quality> entry : values.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Adds all values from the given map to this map.
     * <P>
     * This allows adding {@link Map}s with values not being a {@link Quality}.
     * The same rules are applied as for {@link #put(Object, Object)}
     *
     * @param values The map to add the values from.
     */
    public void putAll(@NotNull Map<? extends T, ?> values) {
        for (final @NotNull Map.Entry<@NotNull ? extends T, @NotNull ?> entry : values.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets the size of this map.
     *
     * @return The size in items.
     */
    @Contract(pure = true)
    public int size() {
        return map.size();
    }

    /**
     * Checks whether this map is empty.
     *
     * @return Whether this map is empty.
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns whether the given item is part of this map.
     *
     * @param key The key to check with.
     * @return Whether the given item is part of this map.
     */
    @Contract(pure = true)
    public boolean containsKey(@NotNull T key) {
        return map.containsKey(key);
    }

    /**
     * Gets the {@link Quality} of a given item.
     * This includes resolving of wildcards as per the map's {@link Style}.
     * If a item is not found {@link Quality#NOT_ACCEPTABLE} is returned.
     *
     * @param key The item to check for.
     * @return The {@link Quality} for the given item or {@link Quality#NOT_ACCEPTABLE}.
     */
    @Contract(pure = true)
    public @NotNull Quality get(@NotNull T key) {
        @Nullable Quality quality = map.get(key);

        if (quality != null)
            return quality;

        for (final @NotNull T wildcard : style.getWildcards(key)) {
            quality = map.get(wildcard);
            if (quality != null)
                return quality;
        }

        return Quality.NOT_ACCEPTABLE;
    }

    /**
     * Removes the given item from the map.
     * @param key The item to remove.
     */
    public void remove(@NotNull T key) {
        map.remove(key);
    }

    /**
     * Clears this map by removing all items from it.
     */
    public void clear() {
        map.clear();
    }

    /**
     * Returns a {@link Set} of all items in this map.
     *
     * @return The {@link Set} of all items.
     */
    @Contract(pure = true)
    public @NotNull Set<@NotNull T> keySet() {
        return map.keySet();
    }

    /**
     * Gets a {@link Set} of all entries in this map.
     * @return The {@link Set} of all entries.
     */
    @Contract(pure = true)
    public @NotNull Set<Map.Entry<@NotNull T, @NotNull Quality>> entrySet() {
        return map.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QualityMap<?> that = (QualityMap<?>) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}
