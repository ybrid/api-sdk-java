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
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class SimpleItem implements Item {
    protected final @NotNull HashMap<@NotNull String, @NotNull String> metadata = new HashMap<>();
    protected final @NotNull ArrayList<Companion> companions = new ArrayList<>();
    protected final @NotNull String identifier;
    protected @Nullable ItemType type;
    protected @Nullable Duration playbackLength;

    public SimpleItem(@NotNull String identifier) {
        this.identifier = identifier;
    }

    public SimpleItem(@NotNull String identifier, @Nullable String artist, @Nullable String title) {
        this(identifier);
        if (artist != null)
            metadata.put(METADATA_ARTIST, artist);
        if (title != null)
            metadata.put(METADATA_TITLE, title);
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @Nullable ItemType getType() {
        return type;
    }

    @Override
    public @Nullable Duration getPlaybackLength() {
        return playbackLength;
    }

    @Override
    public @NotNull Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public @NotNull List<Companion> getCompanions() {
        return Collections.unmodifiableList(companions);
    }

    @Override
    public String getDisplayName() {
        String artist = metadata.get(METADATA_ARTIST);
        String title = metadata.get(METADATA_TITLE);

        if (artist != null && title != null) {
            return artist + " - " + title;
        }

        return title;
    }

    @Override
    public String toString() {
        return "Item{" +
                "identifier='" + identifier + '\'' +
                ", metadata=" + metadata +
                ", type=" + type +
                ", playbackLength=" + playbackLength +
                ", companions=" + companions +
                '}';
    }
}
