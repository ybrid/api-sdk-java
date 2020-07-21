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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * This class implements a renderer for the {@code application/x-www-form-urlencoded} media type.
 */
public class XWWWFormUrlEncodedBuilder {
    private static final @NotNull String UTF_8_NAME = StandardCharsets.UTF_8.name();

    private final @NotNull List<Map.Entry<@NotNull String, @Nullable String>> list = new ArrayList<>();
    private @NotNull String prefix = "";

    /**
     * Gets the prefix for rendering the provided values.
     * @return The prefix.
     * @see #setPrefix(String)
     */
    public @NotNull String getPrefix() {
        return prefix;
    }

    /**
     * This set the prefix for rendering the provided values.
     * The prefix should generally end with something like "{@code .}" or "{@code -}" to make it a proper namespace.
     * @param prefix The prefix to use or {@code null} for no prefix. Defaults to {@code null}.
     */
    public void setPrefix(@Nullable String prefix) {
        if (prefix == null) {
            this.prefix = "";
        } else {
            this.prefix = prefix;
        }
    }

    /**
     * Appends a map {@link java.util.Map.Entry}.
     * The key of the map must not be {@code null}.
     * @param entry The entry to append.
     * @see #append(String, Object)
     */
    public void append(Map.Entry<@NotNull String, @Nullable ?> entry) {
        append(entry.getKey(), entry.getValue());
    }

    /**
     * Appends a key-value-pair.
     * @param key The key to use.
     * @param value The value to use or {@code null} if no value is used for this key.
     */
    public void append(@NotNull String key, @Nullable Object value) {
        final @Nullable String stringValue;

        if (value == null) {
            list.add(new AbstractMap.SimpleEntry<>(key, null));
        } else if (value instanceof Collection) {
            for (Object item : (Collection<?>)value) {
                append(key, item);
            }
        } else {
            list.add(new AbstractMap.SimpleEntry<>(key, value.toString()));
        }
    }

    /**
     * Appends the content of a {@link Map}.
     * This is the same as calling {@link #append(Map.Entry)} for each entry.
     * @param map The map to add.
     */
    public void append(@NotNull Map<@NotNull String, @Nullable ?> map) {
        for (Map.Entry<@NotNull String, @Nullable ?> entry : map.entrySet())
            append(entry);
    }

    /**
     * Returns the full media type for the rendered result.
     * This includes the MIME-Type as well as parameters like the charset.
     * @return The full media type.
     */
    public @NotNull String getMediaType() {
        return "application/x-www-form-urlencoded; charset=utf-8";
    }

    /**
     * Renders to result into a array of bytes suitable for over-the-wire transmission.
     * @return The result as a byte array.
     */
    public @NotNull byte[] getBytes() {
        return toString().getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Renders the result into a {@link String}.
     * @return The result as String.
     * @see #getBytes()
     */
    @Override
    public String toString() {
        final @NotNull StringBuilder rendered = new StringBuilder();

        try {
            for (Map.Entry<@NotNull String, @Nullable String> entry : list) {
                @NotNull String key = entry.getKey();
                @Nullable String value = entry.getValue();

                if (rendered.length() > 0)
                    rendered.append('&');

                rendered.append(URLEncoder.encode(prefix + key, UTF_8_NAME));

                if (value != null) {
                    rendered.append('=');
                    rendered.append(URLEncoder.encode(value, UTF_8_NAME));
                }
            };
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        return rendered.toString();
    }
}
