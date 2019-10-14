/*
 * Copyright 2019 nacamar GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.ybrid.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * This is a utility class used internally by the ybrid client.
 * It should not be used outside the ybrid ecosystem.
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
}
