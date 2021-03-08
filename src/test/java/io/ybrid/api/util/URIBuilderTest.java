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

package io.ybrid.api.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class URIBuilderTest {
    private static abstract class Vector {
        public final @NotNull String uri;

        private Vector(@NotNull String uri) {
            this.uri = uri;
        }
    }

    private static final class InvalidVector extends Vector {
        private InvalidVector(@NotNull String uri) {
            super(uri);
        }
    }

    private static final class ValidVector extends Vector {
        public final @NotNull String scheme;
        public final @Nullable String hostname;
        public final int port;
        public final @NotNull String path;
        public final @Nullable String query;
        public final @Nullable String fragment;

        public ValidVector(@NotNull String uri, @NotNull String scheme, @Nullable String hostname, int port, @NotNull String path, @Nullable String query, @Nullable String fragment) {
            super(uri);
            this.scheme = scheme;
            this.hostname = hostname;
            this.port = port;
            this.path = path;
            this.query = query;
            this.fragment = fragment;
        }
    }

    private static interface Constructor {
        @NotNull URIBuilder construct(@NotNull String uri) throws URISyntaxException, MalformedURLException;
    }

    private static final Vector[] vectors = new Vector[]{
            new ValidVector("http://example.org/bla", "http", "example.org", 0, "/bla", null, null),
            new ValidVector("http://example.org/bla?a=b", "http", "example.org", 0, "/bla", "a=b", null),
            new ValidVector("http://example.org/bla#c", "http", "example.org", 0, "/bla", null, "c"),
            new ValidVector("http://example.org/bla?a=b#c", "http", "example.org", 0, "/bla", "a=b", "c"),
            new ValidVector("http://example.org:1234/bla?a=b#c", "http", "example.org", 1234, "/bla", "a=b", "c"),
            new ValidVector("http://127.0.0.1:1234/bla?a=b#c", "http", "127.0.0.1", 1234, "/bla", "a=b", "c"),
            new ValidVector("http://[::1]:1234/bla?a=b#c", "http", "::1", 1234, "/bla", "a=b", "c"),
            new ValidVector("file:///bla", "file", null, 0, "/bla", null, null),
            new InvalidVector("xxx"),
            new InvalidVector("http://abc"),
            new InvalidVector("http://::1/"),
            new InvalidVector("http://::1:1234/"),
            new InvalidVector("http://[::1:1234/"),
            new InvalidVector("file:/bla//a/b"),
            new InvalidVector("file://bla"),
            new InvalidVector("file:/bla")
    };

    private static final Constructor[] constructors = new Constructor[]{
            URIBuilder::new,
            uri -> new URIBuilder(new URI(uri)),
            uri -> new URIBuilder(new URL(uri))
    };

    @Test
    public void testValid() throws URISyntaxException, MalformedURLException {
        for (final @NotNull Vector vector : vectors) {
            final @NotNull ValidVector validVector;

            if (!(vector instanceof ValidVector))
                continue;

            validVector = (ValidVector) vector;

            for (final @NotNull Constructor constructor : constructors) {
                final @NotNull URIBuilder builder;

                try {
                    builder = constructor.construct(vector.uri);
                } catch (Throwable e) {
                    System.out.println("vector.uri = " + vector.uri);
                    throw e;
                }

                System.out.println("builder.toString() = " + builder.toString());
                System.out.println("builder.toURIString() = " + builder.toURIString());

                assertEquals(validVector.scheme, builder.getRawScheme());
                assertEquals(validVector.hostname, builder.getRawHostname());
                assertEquals(validVector.port, builder.getRawPort());
                assertEquals(validVector.path, builder.getRawPath());
                assertEquals(validVector.query, builder.getRawQuery());
                assertEquals(validVector.fragment, builder.getRawFragment());
                assertEquals(vector.uri, builder.toURIString());
            }
        }
    }

    @Test
    public void testInvalid() {
        for (final @NotNull Vector vector : vectors) {
            if (vector instanceof ValidVector)
                continue;

            for (final @NotNull Constructor constructor : constructors) {
                boolean failed = false;

                try {
                    constructor.construct(vector.uri);
                } catch (URISyntaxException | MalformedURLException e) {
                    failed = true;
                }

                if (!failed)
                    fail("Constructor did not not fail for: " + vector.uri);
            }
        }
    }
}