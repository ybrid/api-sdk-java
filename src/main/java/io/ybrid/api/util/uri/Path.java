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

package io.ybrid.api.util.uri;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.BitSet;

@ApiStatus.Internal
public final class Path extends ArrayList<String> {
    private static final long serialVersionUID = 8210345850586753737L;
    private static final @NotNull String UTF_8_NAME = StandardCharsets.UTF_8.name();
    private static final @NotNull BitSet safeness;

    static {
        safeness = new BitSet(256);
        for (byte c = '0'; c <= '9'; c++)
            safeness.set(c);
        for (byte c = 'a'; c <= 'z'; c++)
            safeness.set(c);
        for (byte c = 'A'; c <= 'Z'; c++)
            safeness.set(c);
        for (byte c : "-._~!$&'()*+,;=:@".getBytes(StandardCharsets.UTF_8))
            safeness.set(c);
    }

    static int hexToInt(byte in) {
        if (in >= '0' && in <= '9')
            return in - '0';
        if (in >= 'a' && in <= 'f')
            return in - 'a' + 10;
        if (in >= 'A' && in <= 'F')
            return in - 'A' + 10;
        throw new IllegalArgumentException("Not a hex char: " + in);
    }

    static char intToHex(int in) {
        //noinspection SpellCheckingInspection
        return "0123456789ABCDEF".charAt(in);
    }

    static @NotNull String decode(@NotNull String string) {
        final @NotNull ByteArrayOutputStream stream = new ByteArrayOutputStream();
        final byte[] in = string.getBytes(StandardCharsets.UTF_8);

        for (int i = 0, inLength = in.length; i < inLength; i++) {
            byte c = in[i];
            if (c != '%') {
                stream.write(c);
            } else {
                stream.write(hexToInt(in[i+1]) * 16 + hexToInt(in[i + 2]));
                i += 2;
            }
        }

        try {
            return stream.toString(UTF_8_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static @NotNull String encode(@NotNull String string) {
        final @NotNull StringBuilder builder = new StringBuilder();
        final byte[] in = string.getBytes(StandardCharsets.UTF_8);

        for (final byte c : in) {
            if (safeness.get(c)) {
                builder.append((char)c);
            } else {
                builder.append('%');
                builder.append(intToHex((c >> 4) & 0xF));
                builder.append(intToHex(c & 0xF));
            }
        }

        return builder.toString();
    }

    public Path() {
    }

    public Path(@NotNull String rawPath) throws URISyntaxException {
        final @NotNull String[] segments;

        if (rawPath.equals("/"))
            return;

        if (!rawPath.startsWith("/")) {
            throw new URISyntaxException(rawPath, "Invalid path");
        }

        segments = rawPath.substring(1).split("/", -1);

        for (final @NotNull String segment : segments) {
            add(decode(segment));
        }
    }

    public static @NotNull Path create(@NotNull String rawPath) {
        try {
            return new Path(rawPath);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull String toRawPath() {
        if (isEmpty()) {
            return "/";
        } else {
            StringBuilder ret = new StringBuilder();

            for (final @NotNull String segment : this) {
                ret.append("/").append(encode(segment));
            }

            return ret.toString();
        }
    }

    public void normalize() {
        final @NotNull ArrayList<String> updated = new ArrayList<>(size());

        for (final @NotNull String segment : this) {
            if (segment.equals("."))
                continue;

            if (segment.equals("..")) {
                if (!updated.isEmpty())
                    updated.remove(updated.size() - 1);
                continue;
            }

            updated.add(segment);
        }

        clear();
        addAll(updated);
    }

    public void append(@NotNull Path path) {
        if (!isEmpty() && get(size() - 1).equals("")) {
            remove(size() - 1);
        }
        addAll(path);
    }
}
