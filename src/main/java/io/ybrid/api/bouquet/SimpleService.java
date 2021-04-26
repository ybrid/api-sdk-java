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

package io.ybrid.api.bouquet;

import io.ybrid.api.util.Identifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public class SimpleService implements Service {
    final @NotNull String displayName;
    final @NotNull Identifier identifier;
    final @Nullable URI icon;
    final @Nullable String genre;

    public SimpleService(@NotNull String displayName, @NotNull Identifier identifier, @Nullable URI icon, @Nullable String genre) {
        this.displayName = displayName;
        this.identifier = identifier.toType(SimpleService.class);
        this.icon = icon;
        this.genre = genre;
    }

    public SimpleService(@NotNull String displayName, @NotNull Identifier identifier) {
        this(displayName, identifier, (URI)null, null);
    }

    public SimpleService() {
        this("default", new Identifier(SimpleService.class));
    }

    @Override
    public @Nullable URI getIconURI() {
        return icon;
    }

    @Override
    public @Nullable String getGenre() {
        return genre;
    }

    @Override
    public @NotNull String getDisplayName() {
        return displayName;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Contract(value = "null -> false", pure = true)
    @Override
    public boolean equals(Object o) {
        return Service.equals(this, (Service)o);
    }

    @Contract(pure = true)
    @Override
    public int hashCode() {
        return Service.hashCode(this);
    }

    @Override
    public String toString() {
        return "SimpleService{" +
                "displayName='" + displayName + '\'' +
                ", identifier='" + identifier + '\'' +
                ", icon=" + icon +
                ", genre='" + genre + '\'' +
                '}';
    }
}
