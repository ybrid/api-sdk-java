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

package io.ybrid.api.util;

import io.ybrid.api.util.QualityMap.QualityMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

/**
 * This is a utility class used internally by the Ybrid client.
 * It should not be used outside the Ybrid ecosystem.
 */
public final class Utils {
    private static ByteArrayOutputStream slurp(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
            result.write(buffer, 0, length);
        }
        return result;
    }

    /**
     * Slurps a {@link InputStream} into a {@link String}.
     * The encoding is expected to be UTF-8.
     *
     * @param inputStream The input stream to slurp.
     * @return The content of the input stream as {@link String}.
     * @throws IOException Thrown on I/O-Error on the {@code inputStream}.
     */
    public static String slurpToString(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = slurp(inputStream);
        return result.toString(StandardCharsets.UTF_8.name());
    }

    /**
     * Slurps a {@link InputStream} into {@code byte[]}.
     * The encoding is expected to be UTF-8.
     *
     * @param inputStream The input stream to slurp.
     * @return The content of the input stream as {@code byte[]}.
     * @throws IOException Thrown on I/O-Error on the {@code inputStream}.
     */
    public static byte[] slurpToByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = slurp(inputStream);
        return result.toByteArray();
    }

    /**
     * Slurps a {@link InputStream} into {@link JSONObject}.
     * The encoding is expected to be UTF-8.
     *
     * @param inputStream The input stream to slurp.
     * @return The content of the input stream as {@link JSONObject}.
     * @throws IOException Thrown on I/O-Error on the {@code inputStream}.
     */
    public static JSONObject slurpToJSONObject(InputStream inputStream) throws IOException {
        return new JSONObject(slurpToString(inputStream));
    }

    /**
     * Asserts a Accept:-style list is valid.
     * @param list The list to check or {@code null}.
     * @throws IllegalArgumentException Thrown if the list is not valid.
     * @deprecated No longer needed as internally ensured by {@link QualityMap}.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public static void assertValidAcceptList(@Nullable Map<String, Double> list) throws IllegalArgumentException {
        if (list == null)
            return;

        for (double weight : list.values())
            if (weight < 0 || weight > 1)
                throw new IllegalArgumentException("Invalid weight=" + weight + ", must be in range [0,1]");
    }

    /**
     * Asserts the given hostname is valid.
     * @param hostname The hostname to check.
     * @throws MalformedURLException Thrown if the hostname is invalid.
     */
    @Contract(value = "null -> fail", pure = true)
    public static void assertValidHostname(@Nullable String hostname) throws MalformedURLException {
        if (hostname == null)
            throw new MalformedURLException("Bad hostname: null");
        if (!hostname.matches("^[a-zA-Z0-9:.-]+$"))
            throw new MalformedURLException("Bad hostname: \"" + hostname + "\"");
    }

    /**
     * Asserts the given port is valid.
     * @param port The port to check.
     * @throws MalformedURLException Thrown if the port is invalid.
     */
    @Contract(pure = true)
    public static void assertValidPort(int port) throws MalformedURLException {
        if (port < 0 || port > 65535)
            throw new MalformedURLException("Bad port: " + port);
    }

    /**
     * Checks the given FQDN for validity.
     * {@code localhost} is not considered valid by this function.
     * @param fqdn The FQDN to test.
     * @return Whether the argument is a valid FQDN.
     */
    public static boolean isValidFQDN(@NotNull String fqdn) {
        if (fqdn.equals("localhost") || fqdn.equals("localhost.localdomain") || fqdn.equals("127.0.0.1") || fqdn.equals("::1"))
            return false;

        try {
            assertValidHostname(fqdn);
        } catch (MalformedURLException e) {
            return false;
        }

        return fqdn.contains(".");
    }

    /**
     * This converts a {@link URI} to an {@link URL}.
     * All exceptions are converted to {@link RuntimeException}s.
     * If {@code null} is given as input {@code null} is also returned.
     *
     * @param uri The URI to convert.
     * @return The resulting URL.
     */
    @Contract(value = "null -> null", pure = true)
    public static @Nullable URL toURL(@Nullable URI uri) {
        if (uri == null)
            return null;
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Transforms a object into another in a {@code null}-aware way.
     * @param input The input.
     * @param transformer The {@link Function} used to do the transformation if the object is non-{@code null}.
     * @param <I> The type of the input.
     * @param <O> The type of the output.
     * @return The result of the transformation or {@code null}.
     */
    @Contract("null, _ -> null")
    public static <I,O> @Nullable O transform(@Nullable I input, @NotNull Function<I,O> transformer) {
        if (input == null)
            return null;

        return transformer.apply(input);
    }

    /**
     * This returns the first non-{@code null} value passed or {@code null} if no non-{@code null} values are passed.
     * @param inputs The values to check.
     * @param <K> The common type of all values.
     * @return The result of the operation or {@code null}.
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <K> @Nullable K firstOf(@Nullable K... inputs) {
        for (final @Nullable K current : inputs) {
            if (current != null)
                return current;
        }

        return null;
    }
}
