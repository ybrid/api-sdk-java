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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.UUID;

public class SimpleService implements Service {
    final @NotNull String displayName;
    final @NotNull String identifier;
    final @Nullable URL icon;
    final @Nullable String genre;

    public SimpleService(@NotNull String displayName, @NotNull String identifier, @Nullable URL icon, @Nullable String genre) {
        this.displayName = displayName;
        this.identifier = identifier;
        this.icon = icon;
        this.genre = genre;
    }

    public SimpleService(@NotNull String displayName, @NotNull String identifier) {
        this(displayName, identifier, null, null);
    }

    public SimpleService() {
        this("default", UUID.randomUUID().toString());
    }

    @Override
    public @Nullable URL getIcon() {
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
    public @NotNull String getIdentifier() {
        return identifier;
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
