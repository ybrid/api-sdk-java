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

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * This class abstracts identifiers as used in metadata.
 */
public final class Identifier implements Serializable {
    private static final long serialVersionUID = -3059154610234338954L;

    private final @NotNull String identifier;
    private final @NotNull Class<?> type;

    /**
     * Main constructor.
     * @param identifier The identifier to use as string.
     * @param type Type of the identifier.
     */
    public Identifier(@NotNull String identifier, @NotNull Class<?> type) {
        if (identifier.isEmpty())
            throw new IllegalArgumentException("Empty string passed as identifier");
        this.identifier = identifier;
        this.type = type;
    }

    /**
     * Constructs a new random identifier.
     * @param type Type of the identifier.
     */
    public Identifier(@NotNull Class<?> type) {
        this(UUID.randomUUID().toString(), type);
    }

    /**
     * Main constructor.
     * @param identifier The identifier to use as string.
     * @deprecated Use {@link #Identifier(String, Class)}
     */
    @Deprecated
    public Identifier(@NotNull String identifier) {
        this(identifier, Object.class);
    }

    /**
     * Constructs a new random identifier.
     * @deprecated Use {@link #Identifier(Class)}
     */
    @Deprecated
    public Identifier() {
        this(UUID.randomUUID().toString(), Object.class);
    }

    /**
     * Gets the type of this identifier.
     * @return The type.
     */
    @Contract(pure = true)
    public @NotNull Class<?> getType() {
        return type;
    }

    /**
     * Checks if the type of this identifier is a sub-class of the given {@code superClass}.
     * @param superClass The super-class to check for.
     * @return Whether this identifiers type is a sub-class of the given super-class.
     */
    @Contract(pure = true)
    public boolean typeIsA(@NotNull Class<?> superClass) {
        try {
            type.asSubclass(superClass);
            return true;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier that = (Identifier) o;
        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
