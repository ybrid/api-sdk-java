/*
 * Copyright (c) 2019 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This is a utility class used internally by the Ybrid client.
 * It should not be used outside the Ybrid ecosystem.
 */
public class Utils {
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
}
