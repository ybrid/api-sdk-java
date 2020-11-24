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

package io.ybrid.api.driver;

import io.ybrid.api.util.Utils;
import io.ybrid.api.util.XWWWFormUrlEncodedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This helper class allows to make request to an {@link URL} expecting a JSON document in return.
 * This class takes care of handling transformation and parsing as well as all network activity.
 *
 * This class only performs I/O operations in {@link #perform()}.
 */
public final class JSONRequest {
    static final Logger LOGGER = Logger.getLogger(JSONRequest.class.getName());

    private final @NotNull URL url;
    private final @NotNull String requestMethod;
    private final @Nullable String requestBodyContentType;
    private final @Nullable byte[] requestBody;
    private @Nullable JSONObject responseBody = null;

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isAcceptable(@NotNull HttpURLConnection connection) {
        String contentType = connection.getContentType();

        if (contentType == null)
            return false;

        contentType = contentType.toLowerCase(Locale.ROOT);

        // No charset given, so we guess it's UTF-8.
        if (contentType.equals("application/json"))
            return true;

        if (contentType.equals("application/json; charset=utf-8"))
            return true;

        return false;
    }

    /**
     * Creates a new JSON request.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     * @param requestBodyContentType Content-type of the request body or null.
     * @param requestBody Request body or null.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod, @Nullable String requestBodyContentType, @Nullable byte[] requestBody) throws IllegalArgumentException {
        this.url = url;
        this.requestMethod = requestMethod;

        if ((requestBodyContentType == null) != (requestBody == null))
            throw new IllegalArgumentException("requestBodyContentType, and requestBody must both be null or none-null");

        this.requestBodyContentType = requestBodyContentType;
        this.requestBody = requestBody;
    }

    /**
     * Creates a new JSON request using a {@link String} as request body.
     * The String is sent encoded as UTF-8. The {@code requestBodyContentType} parameter must reflect this.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     * @param requestBodyContentType Content-type of the request body or null.
     * @param requestBody Request body or null.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod, @Nullable String requestBodyContentType, @Nullable String requestBody) throws IllegalArgumentException {
        this(url, requestMethod, requestBodyContentType, requestBody == null ? null : requestBody.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Creates a new JSON request without a request body.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod) throws IllegalArgumentException {
        this(url, requestMethod, null, (byte[])null);
    }

    /**
     * Creates a new JSON request using a {@link Map} of {@link String}s as request body.
     * The Strings are sent encoded as UTF-8 in "POST parameters" format.
     * The content type for the body is set to "application/x-www-form-urlencoded; charset=utf-8".
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod, @Nullable Map<String, String> requestBody) throws IllegalArgumentException, UnsupportedEncodingException {
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
     * Performs the actual request.
     *
     * This call can be re-called safely to re-send the same request.
     *
     * @return Returns whether the request was successful or not.
     */
    public synchronized boolean perform() throws IOException {
        HttpURLConnection connection;
        InputStream inputStream;
        final boolean success;
        final boolean acceptable;

        // We set this to null early, so we can just throw an exception in this method at will.
        responseBody = null;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("Accept", "application/json, *; q=0");
        connection.setRequestProperty("Accept-Charset", "utf-8, *; q=0");
        connection.setDoInput(true);
        connection.setDoOutput(requestBody != null);

        if (requestBody != null) {
            connection.setRequestProperty("Content-Type", requestBodyContentType);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody);
            outputStream.close();
        }

        success = connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        acceptable = isAcceptable(connection);

        if (success) {
            LOGGER.info("JSONRequest to " + url + " returned " + connection.getResponseCode() + " " + connection.getResponseMessage() + " [" + connection.getContentType() + "]");
        } else {
            LOGGER.warning("JSONRequest to " + url + " failed with " + connection.getResponseCode() + " " + connection.getResponseMessage() + " [" + connection.getContentType() + "]");
        }

        if (acceptable) {
            if (success) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            responseBody = Utils.slurpToJSONObject(inputStream);
            inputStream.close();
        }

        connection.disconnect();

        return success && acceptable;
    }

    /**
     * Returns the response from the last request.
     * @return The response or null.
     */
    public synchronized @Nullable JSONObject getResponseBody() {
        return responseBody;
    }

    /**
     * Gets the {@link URL} used for the request.
     * @return The URL used.
     */
    public @NotNull URL getUrl() {
        return url;
    }
}
