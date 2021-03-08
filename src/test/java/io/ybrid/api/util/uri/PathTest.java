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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.net.URISyntaxException;

import static org.junit.Assert.*;

public class PathTest {
    private static final @NotNull String[] good_paths = new String[]{"/", "/test", "/test/bla", "/test/bla/", "/te%20st", "/te%2Fst/"};
    private static final @NotNull String[] bad_paths = new String[]{""};

    @Test
    public void parseGood() throws URISyntaxException {
        for (final @NotNull String rawPath : good_paths) {
            final @NotNull Path path = new Path(rawPath);
            System.out.println("rawPath = " + rawPath);
            System.out.println("path.toRawPath() = " + path.toRawPath());
            System.out.println("path = " + path);
            assertEquals(rawPath, path.toRawPath());
        }
    }

    @Test
    public void parseBad() {
        for (final @NotNull String rawPath : bad_paths) {
            boolean failed = false;
            try {
                new Path(rawPath);
            } catch (URISyntaxException e) {
                failed = true;
            }

            if (!failed)
                fail("Constructor did not fail for: " + rawPath);
        }
    }

    @Test
    public void toRawPath() {
        final @NotNull Path path = new Path();
        path.add("test");
        path.add("te st");
        path.add("te/st");
        path.add("te+st");

        assertEquals("/test/te%20st/te%2Fst/te+st", path.toRawPath());
    }

    @Test
    public void normalize() throws URISyntaxException {
        final @NotNull Path path = new Path("/test/./../b");
        path.normalize();
        assertEquals("/b", path.toRawPath());
    }

    @Test
    public void append() throws URISyntaxException {
        final @NotNull Path pathA = new Path("/a/b");
        final @NotNull Path pathB = new Path("/a/b/");
        final @NotNull Path pathToAppend = new Path("/c/d");

        pathA.append(pathToAppend);
        pathB.append(pathToAppend);

        assertEquals("/a/b/c/d", pathA.toRawPath());
        assertEquals("/a/b/c/d", pathB.toRawPath());
    }
}