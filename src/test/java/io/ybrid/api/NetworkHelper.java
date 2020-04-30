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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@SuppressWarnings("ConstantConditions")
public class NetworkHelper {
    private static final URL[] aliases = {
    };

    /**
     * This is a helper method allowing creation of URL objects in static
     * initialisation of this class.
     * This helper only transforms any {@link MalformedURLException} into a {@link IllegalArgumentException}.
     * @param url The URL as {@link String}.
     * @return The URL as {@link URL}.
     */
    @Contract("_ -> new")
    private static @NotNull URL newURL(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Checks whether tests can access the network.
     *
     * @return Whether checks can access the network.
     */
    @Contract(pure = true)
    public static boolean isOnline() {
        return aliases.length > 0;
    }

    /**
     * Get a {@link URL} of the default alias to use.
     * @return The {@link URL} of the alias or null if no online tests should be preformed.
     */
    @Contract(pure = true)
    public static @Nullable URL getDefaultAlias() {
        if (!isOnline())
            return null;
        return aliases[0];
    }

    /**
     * Returns a list of alias {@link URL URLs} to perform tests with.
     * If {@link #isOnline()} returns false this method will return an empty non-null array.
     * @return The array of Aliases used for tests.
     */
    @Contract(pure = true)
    public static URL[] getAliases() {
        return aliases;
    }

    /**
     * Make a HTTP GET request and discard returned document.
     *
     * @param url The URL to ping
     * @return The HTTP status code
     * @throws IOException Thrown on any I/O-Error
     */
    public static int pingURL(URL url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setDoOutput(false);
        connection.setDoInput(true);
        connection.connect();
        connection.getInputStream().close();
        connection.disconnect();
        return connection.getResponseCode();
    }
}
