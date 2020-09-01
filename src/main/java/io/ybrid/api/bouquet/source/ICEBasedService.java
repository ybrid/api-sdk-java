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

package io.ybrid.api.bouquet.source;

import io.ybrid.api.metadata.source.Source;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * This class implements a {@link SourceServiceMetadata} that is based on
 * ice-*-headers. icy-*-headers are also supported.
 */
public class ICEBasedService implements SourceServiceMetadata {
    private static final @NotNull String PREFIX_ICE = "ice-";
    private static final @NotNull String PREFIX_ICY = "icy-";

    private static final @NotNull String KEY_AUDIO_INFO = "audio-info";
    private static final @NotNull String KEY_BR = "br";
    private static final @NotNull String KEY_PUB = "pub";
    private static final @NotNull String KEY_DESCRIPTION = "description";
    private static final @NotNull String KEY_GENRE = "genre";
    private static final @NotNull String KEY_NAME = "name";
    private static final @NotNull String KEY_URL = "url";

    private final @NotNull Source source;
    private final @NotNull String identifier;
    private final @NotNull Map<String, String> values = new HashMap<>();

    /**
     * Main constructor.
     * @param source The source this service is based on.
     * @param identifier The server identifier.
     * @param headers A map of the headers to include. Non-supported headers are automatically stripped.
     *                This allows to pass all headers unfiltered.
     */
    public ICEBasedService(@NotNull Source source, @NotNull String identifier, @NotNull Map<String, String> headers) {
        this.source = source;
        this.identifier = identifier;

        for (final @NotNull Map.Entry<@NotNull String, @NotNull String> entry : headers.entrySet())
            add(entry.getKey(), entry.getValue());
    }

    private void add(@NotNull String key, @NotNull String value) {
        key = key.toLowerCase(Locale.ROOT);

        if (!(key.startsWith(PREFIX_ICE) || key.startsWith(PREFIX_ICY)))
            return;

        values.put(key, value);
    }

    private @Nullable String get(@NotNull String key) {
        if (values.containsKey(PREFIX_ICE + key))
            return values.get(PREFIX_ICE + key);
        return values.get(PREFIX_ICY + key);
    }

    @Override
    public @Nullable URL getIcon() {
        return null;
    }

    @Override
    public @Nullable String getGenre() {
        return get(KEY_GENRE);
    }

    @Override
    public @Nullable String getDescription() {
        return get(KEY_DESCRIPTION);
    }

    @Override
    public @Nullable URI getInfoURI() {
        return null;
    }

    @Override
    public String getDisplayName() {
        String ret = get(KEY_NAME);

        if (ret != null)
            return ret;

        return getIdentifier();
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @NotNull Source getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "ICEBasedService{" +
                "source=" + source +
                ", identifier='" + identifier + '\'' +
                ", values=" + values +
                '}';
    }
}
