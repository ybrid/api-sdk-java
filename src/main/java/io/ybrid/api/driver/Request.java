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

package io.ybrid.api.driver;

import io.ybrid.api.util.QualityMap.MediaTypeMap;
import io.ybrid.api.util.XWWWFormUrlEncodedBuilder;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

@ApiStatus.Internal
abstract public class Request {
    static private final Logger LOGGER = Logger.getLogger(Request.class.getName());

    protected final @NotNull URL url;
    protected final @NotNull String requestMethod;
    protected final @Nullable String requestBodyContentType;
    protected final byte[] requestBody;

    abstract public boolean perform() throws IOException;

    /**
     * Creates a new Request.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     * @param requestBodyContentType Content-type of the request body or null.
     * @param requestBody Request body or null.
     */
    public Request(@NotNull URL url, @NotNull String requestMethod, @Nullable String requestBodyContentType, byte[] requestBody) throws IllegalArgumentException {
        this.url = url;
        this.requestMethod = requestMethod;

        if ((requestBodyContentType == null) != (requestBody == null))
            throw new IllegalArgumentException("requestBodyContentType, and requestBody must both be null or none-null");

        this.requestBodyContentType = requestBodyContentType;
        this.requestBody = requestBody;
    }

    /**
     * Creates a new Request using a {@link String} as request body.
     * The String is sent encoded as UTF-8. The {@code requestBodyContentType} parameter must reflect this.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     * @param requestBodyContentType Content-type of the request body or null.
     * @param requestBody Request body or null.
     */
    public Request(@NotNull URL url, @NotNull String requestMethod, @Nullable String requestBodyContentType, @Nullable String requestBody) throws IllegalArgumentException {
        this(url, requestMethod, requestBodyContentType, requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a new Request without a request body.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public Request(@NotNull URL url, @NotNull String requestMethod) throws IllegalArgumentException {
        this(url, requestMethod, null, (byte[])null);
    }

    /**
     * Creates a new Request using a {@link Map} of {@link String}s as request body.
     * The Strings are sent encoded as UTF-8 in "POST parameters" format.
     * The content type for the body is set to "application/x-www-form-urlencoded; charset=utf-8".
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public Request(@NotNull URL url, @NotNull String requestMethod, @Nullable Map<String, String> requestBody) throws IllegalArgumentException {
        this.url = url;
        this.requestMethod = requestMethod;

        if (requestBody == null) {
            this.requestBodyContentType = null;
            this.requestBody = null;
        } else {
            final XWWWFormUrlEncodedBuilder builder = new XWWWFormUrlEncodedBuilder();

            builder.append(requestBody);

            this.requestBodyContentType = builder.getMediaType();

            this.requestBody = builder.getBytes();
        }
    }

    /**
     * Gets the {@link URL} used for the request.
     * @return The URL used.
     */
    public @NotNull URL getUrl() {
        return url;
    }

    protected @NotNull URLConnection createRequest(@Nullable MediaTypeMap accepted) throws IOException {
        final @NotNull URLConnection connection = url.openConnection();

        if (connection instanceof HttpURLConnection) {
            ((HttpURLConnection) connection).setRequestMethod(requestMethod);
        }

        if (accepted != null)
            connection.setRequestProperty("Accept", accepted.toHTTPHeaderLikeString());

        connection.setRequestProperty("Accept-Charset", "utf-8, *; q=0");
        connection.setDoInput(true);
        connection.setDoOutput(requestBody != null);

        if (requestBody != null) {
            connection.setRequestProperty("Content-Type", requestBodyContentType);

            final @NotNull OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody);
            outputStream.close();
        }

        return connection;
    }
}
