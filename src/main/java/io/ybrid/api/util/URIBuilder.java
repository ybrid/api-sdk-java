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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class URIBuilder {
    private @NotNull String scheme;
    private @Nullable String hostname;
    private int port;
    private @NotNull String path;
    private @Nullable String query;
    private @Nullable String fragment;

    @Contract(value = "_, _, _ -> new", pure = true)
    private static @NotNull String[] split(@NotNull String in, @NotNull String delimiter, int max) {
        final @NotNull ArrayList<String> list = new ArrayList<>(max);
        int pos = 0;

        for (; max > 0; max--) {
            final int nextPos = in.indexOf(delimiter, pos);
            if (max == 1 || nextPos < 0) {
                list.add(in.substring(pos));
                break;
            }

            list.add(in.substring(pos, nextPos));
            pos = nextPos + delimiter.length();
        }

        return list.toArray(new String[0]);
    }

    static private void assertValidScheme(@NotNull String uri, @NotNull String scheme) throws URISyntaxException {
        if (!scheme.matches("^[a-zA-Z0-9.+-]+$"))
            throw new URISyntaxException(uri, "Invalid scheme: " + scheme);
    }

    public URIBuilder(@NotNull String uri) throws URISyntaxException {
        @NotNull String[] res;
        @NotNull String rest;

        res = split(uri, ":", 2);
        if (res.length != 2)
            throw new URISyntaxException(uri, "No scheme given");

        assertValidScheme(uri, res[0]);
        this.scheme = res[0];

        if (!res[1].startsWith("//"))
            throw new URISyntaxException(uri, "Invalid syntax");

        res = split(res[1].substring(2), "/", 2);
        if (res.length != 2)
            throw new URISyntaxException(uri, "No path given");

        rest = "/" + res[1];

        if (res[0].isEmpty()) {
            this.hostname = null;
            this.port = 0;
        } else {
            if (res[0].startsWith("[")) {
                res = split(res[0].substring(1), "]:", 2);
                if (res.length != 2) {
                    if (res[0].substring(1).endsWith("]")) {
                        res = new String[]{res[0].substring(1, res[0].length() - 1)};
                    } else {
                        throw new URISyntaxException(uri, "Invalid hostname");
                    }
                }
            } else {
                res = split(res[0], ":", 2);
            }
            try {
                Utils.assertValidHostname(res[0]);
            } catch (MalformedURLException e) {
                throw new URISyntaxException(uri, "Invalid hostname: " + res[0]);
            }
            this.hostname = res[0];
            if (res.length == 2) {
                try {
                    this.port = Integer.parseInt(res[1]);
                } catch (Throwable e) {
                    throw new URISyntaxException(uri, "Invalid port: " + res[1]);
                }
            } else {
                this.port = 0;
            }
        }

        res = split(rest, "#", 2);
        if (res.length == 2) {
            this.fragment = res[1];
        } else {
            this.fragment = null;
        }

        res = split(res[0], "?", 2);
        if (res.length == 2) {
            this.query = res[1];
        } else {
            this.query = null;
        }
        this.path = res[0];
    }

    private static @NotNull String javaURItoString(@NotNull URI uri) {
        final @NotNull String javasIdea = uri.toASCIIString();
        System.out.println("javasIdea = " + javasIdea);
        if (javasIdea.startsWith("file:/") && !javasIdea.startsWith("file://"))
            return "file://" + javasIdea.substring(5); // RFC's idea.
        return javasIdea;
    }
    public URIBuilder(@NotNull URI uri) throws URISyntaxException {
        this(javaURItoString(uri));
    }

    public URIBuilder(@NotNull URL url) throws URISyntaxException {
        this(url.toURI());
    }

    @Contract(pure = true)
    public @NotNull String getRawScheme() {
        return scheme;
    }

    @Contract(pure = true)
    public @Nullable String getRawHostname() {
        return hostname;
    }

    @Contract(pure = true)
    public int getRawPort() {
        return port;
    }

    @Contract(pure = true)
    public @NotNull String getRawPath() {
        return path;
    }

    @Contract(pure = true)
    public @Nullable String getRawQuery() {
        return query;
    }

    @Contract(pure = true)
    public @Nullable String getRawFragment() {
        return fragment;
    }

    public @NotNull String toURIString() {
        @NotNull String ret = scheme + "://";

        if (hostname != null) {
            if (hostname.contains(":")) {
                ret += "[" + hostname + "]";
            } else {
                ret += hostname;
            }
            if (port > 0)
                ret += ":" + port;
        }

        ret += path;

        if (query != null)
            ret += "?" + query;

        if (fragment != null)
            ret += "#" + fragment;

        return ret;
    }

    public @NotNull URI toURI() {
        return URI.create(toURIString());
    }

    public @NotNull URL toURL() throws MalformedURLException {
        return new URL(toURIString());
    }

    @Override
    public String toString() {
        return "URIBuilder{" +
                "scheme='" + scheme + '\'' +
                ", hostname='" + hostname + '\'' +
                ", port=" + port +
                ", path='" + path + '\'' +
                ", query='" + query + '\'' +
                ", fragment='" + fragment + '\'' +
                '}';
    }
}
