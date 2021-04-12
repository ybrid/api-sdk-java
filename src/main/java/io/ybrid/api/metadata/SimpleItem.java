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

package io.ybrid.api.metadata;

import io.ybrid.api.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class SimpleItem implements Item {
    protected final @NotNull HashMap<@NotNull String, @NotNull String> metadata = new HashMap<>();
    protected final @NotNull ArrayList<Companion> companions = new ArrayList<>();
    protected final @NotNull Identifier identifier;
    protected @Nullable ItemType type;
    protected @Nullable Duration playbackLength;

    public SimpleItem(@NotNull Identifier identifier) {
        this.identifier = identifier.toType(SimpleItem.class);
    }

    public SimpleItem(@NotNull Identifier identifier, @Nullable String artist, @Nullable String title) {
        this(identifier);
        if (artist != null)
            metadata.put(METADATA_ARTIST, artist);
        if (title != null)
            metadata.put(METADATA_TITLE, title);
    }

    private void addMetadata(final @NotNull String key, final @Nullable String value) {
        if (value == null)
            return;
        metadata.put(key, value);
    }

    public SimpleItem(@NotNull Identifier identifier, @NotNull TrackMetadata trackMetadata) {
        this(identifier);
        addMetadata(METADATA_DESCRIPTION, trackMetadata.getComment());
        if (trackMetadata instanceof BasicTrackMetadata) {
            addMetadata(METADATA_TITLE, ((BasicTrackMetadata) trackMetadata).getTitle());
            addMetadata(METADATA_VERSION, ((BasicTrackMetadata) trackMetadata).getVersion());
            addMetadata(METADATA_ARTIST, ((BasicTrackMetadata) trackMetadata).getArtist());
            addMetadata(METADATA_ALBUM, ((BasicTrackMetadata) trackMetadata).getAlbum());
        } else {
            addMetadata(METADATA_TITLE, trackMetadata.getDisplayTitle());
        }
    }

    @Override
    public @NotNull Identifier getIdentifier() {
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
        return getDisplayTitle();
    }

    @Override
    public @Nullable String getComment() {
        return metadata.get(METADATA_DESCRIPTION);
    }

    @Override
    public @Nullable String getTitle() {
        return metadata.get(METADATA_TITLE);
    }

    @Override
    public @Nullable String getVersion() {
        return metadata.get(METADATA_VERSION);
    }

    @Override
    public @Nullable String getArtist() {
        return metadata.get(METADATA_ARTIST);
    }

    @Override
    public @Nullable String getAlbum() {
        return metadata.get(METADATA_ALBUM);
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
