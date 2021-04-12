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

package io.ybrid.api.metadata.source;

import io.ybrid.api.util.Identifier;
import io.ybrid.api.util.hasIdentifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * This class is used to identify a specific source.
 */
public final class Source implements hasIdentifier {
    private final @NotNull SourceType type;
    private final @NotNull Identifier identifier;

    /**
     * Main constructor.
     *
     * @param type The type of the Source.
     * @param identifier The identifier of the Source.
     */
    public Source(@NotNull SourceType type, @NotNull Identifier identifier) {
        this.type = type;
        this.identifier = identifier.toType(Source.class);
    }

    /**
     * Create a new Source with a random identifier.
     *
     * @param type The type of the Source.
     */
    public Source(@NotNull SourceType type) {
        this(type, new Identifier(Source.class));
    }

    /**
     * Gets the type of this source.
     *
     * @return The type.
     */
    @NotNull SourceType getType() {
        return type;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return identifier.equals(source.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public @NotNull String toString() {
        return "Source{" +
                "type=" + type +
                ", identifier=" + identifier +
                '}';
    }
}
