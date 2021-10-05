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

import io.ybrid.api.metadata.BasicTrackMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class implements Vorbis Comment based track metadata.
 * This kind of metadata is common to all Xiph.Org Foundation codecs.
 * See https://www.xiph.org/ for more information and
 * https://xiph.org/vorbis/doc/v-comment.html for a complete specification
 * on Vorbis Comments.
 */
public class VorbisCommentBasedMetadata implements SourceTrackMetadata, BasicTrackMetadata {
    /* From the official list at https://xiph.org/vorbis/doc/v-comment.html */
    public static final @NotNull String KEY_TITLE = "TITLE";
    public static final @NotNull String KEY_VERSION = "VERSION";
    public static final @NotNull String KEY_ALBUM = "ALBUM";
    public static final @NotNull String KEY_TRACKNUMBER = "TRACKNUMBER";
    public static final @NotNull String KEY_ARTIST = "ARTIST";
    public static final @NotNull String KEY_PERFORMER = "PERFORMER";
    public static final @NotNull String KEY_COPYRIGHT = "COPYRIGHT";
    public static final @NotNull String KEY_LICENSE = "LICENSE";
    public static final @NotNull String KEY_ORGANIZATION = "ORGANIZATION";
    public static final @NotNull String KEY_DESCRIPTION = "DESCRIPTION";
    public static final @NotNull String KEY_GENRE = "GENRE";
    public static final @NotNull String KEY_DATE = "DATE";
    public static final @NotNull String KEY_LOCATION = "LOCATION";
    public static final @NotNull String KEY_CONTACT = "CONTACT";
    public static final @NotNull String KEY_ISRC = "ISRC";


    protected final @NotNull Map<String, List<String>> comments = new HashMap<>();
    protected final @NotNull String vendor;
    protected final @NotNull Source source;

    private static void assertValidKey(String key) {
        /* A case-insensitive field name that may consist of ASCII 0x20 through 0x7D, 0x3D ('=') excluded.
         * ASCII 0x41 through 0x5A inclusive (A-Z) is to be considered equivalent to ASCII 0x61 through 0x7A inclusive (a-z).
         */

        for (final byte b : key.getBytes(StandardCharsets.UTF_8)) {
            if (b < 0x20 || b > 0x7D || b == 0x3D)
                throw new IllegalArgumentException("Invalid byte in key: " + b);
        }
    }

    /**
     * Main constructor.
     *
     * @param source The {@link Source} this metadata originates from.
     * @param vendor The vendor string.
     * @param comments A map of comments.
     */
    public VorbisCommentBasedMetadata(@NotNull Source source, @NotNull String vendor, @NotNull Map<String, ? extends Collection<String>> comments) {
        this.vendor = vendor;
        this.source = source;

        for (final @NotNull Map.Entry<@NotNull String, ? extends Collection<String>> entry : comments.entrySet()) {
            final @NotNull String key = entry.getKey().toUpperCase(Locale.ROOT);
            assertValidKey(key);
            this.comments.put(key, new ArrayList<>(entry.getValue()));
        }
    }

    /**
     * Gets the {@link Set} of all keys contained in this comments block.
     * @return The set of all keys in this block.
     */
    public @NotNull @UnmodifiableView Set<String> keySet() {
        return Collections.unmodifiableSet(comments.keySet());
    }

    /**
     * Gets a a collection of values for a given key.
     * If a no entries are found for a given key a empty collection is returned.
     *
     * @param key The key to query for.
     * @return The collection of values.
     */
    public @NotNull @UnmodifiableView Collection<String> getValue(@NotNull String key) {
        final @Nullable List<String> res;

        key = key.toUpperCase(Locale.ROOT);

        assertValidKey(key);

        res = comments.get(key);

        if (res == null)
            return Collections.emptyList();

        return Collections.unmodifiableCollection(res);
    }

    /**
     * Gets a value for a key as a single string.
     * <ul>
     *     <li>If there are no entries for the given key {@code null} is returned.</li>
     *     <li>If there is exactly one entry for the given key that entry is returned.</li>
     *     <li>If there are more than one entries for the given key they are joined in an unspecified manner and returned.</li>
     * </ul>
     *
     * This should be avoided as the result is undefined if more than one entry is present for a given key.
     *
     * @param key The key to query.
     * @return The collected string or {@code null}.
     */
    public @Nullable String getSingleValue(@NotNull String key) {
        final @NotNull Collection<String> values = getValue(key);
        final int size = values.size();

        if (size == 0) {
            return null;
        } else if (size == 1) {
            return values.iterator().next();
        } else {
            final @NotNull StringBuilder builder = new StringBuilder();
            for (final @NotNull String value : values) {
                if (builder.length() > 0)
                    builder.append(", ");
                builder.append(value);
            }
            return builder.toString();
        }
    }

    @Override
    public @Nullable String getTitle() {
        return getSingleValue(KEY_TITLE);
    }

    @Override
    public @Nullable String getVersion() {
        return getSingleValue(KEY_VERSION);
    }

    @Override
    public @Nullable String getArtist() {
        return getSingleValue(KEY_ARTIST);
    }

    @Override
    public @Nullable String getAlbum() {
        return getSingleValue(KEY_ALBUM);
    }

    @Override
    public @Nullable String getGenre() {
        return getSingleValue(KEY_GENRE);
    }

    @Override
    public @NotNull Source getSource() {
        return source;
    }
}
