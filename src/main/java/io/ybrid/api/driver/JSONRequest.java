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

import io.ybrid.api.util.MediaType;
import io.ybrid.api.util.QualityMap.MediaTypeMap;
import io.ybrid.api.util.QualityMap.Quality;
import io.ybrid.api.util.Utils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This helper class allows to make request to an {@link URL} expecting a JSON document in return.
 * This class takes care of handling transformation and parsing as well as all network activity.
 *
 * This class only performs I/O operations in {@link #perform()}.
 */
@ApiStatus.Internal
public final class JSONRequest extends Request {
    static private final long serialVersionUID = -7211568165710281552L;
    static final Logger LOGGER = Logger.getLogger(JSONRequest.class.getName());
    static private final @NotNull MediaTypeMap acceptableMediaTypes = new MediaTypeMap();

    static {
        acceptableMediaTypes.put(new MediaType("application/vnd.nacamar.ybrid+json; version=v2"), Quality.MOST_ACCEPTABLE);
        acceptableMediaTypes.put(new MediaType("application/json"), Quality.LEAST_ACCEPTABLE);
        acceptableMediaTypes.put(MediaType.MEDIA_TYPE_ANY, Quality.NOT_ACCEPTABLE);
    }

    private @Nullable String responseBody = null;

    @SuppressWarnings("RedundantIfStatement")
    private static boolean isAcceptable(@NotNull HttpURLConnection connection) {
        String contentType = connection.getContentType();

        if (contentType == null)
            return false;

        contentType = contentType.toLowerCase(Locale.ROOT);

        if (contentType.equals("application/vnd.nacamar.ybrid+json; version=v2"))
            return true;

        if (contentType.equals("application/vnd.nacamar.ybrid+json"))
            return true;

        // No charset given, so we guess it's UTF-8.
        if (contentType.equals("application/json"))
            return true;

        if (contentType.equals("application/json; charset=utf-8"))
            return true;

        return false;
    }

    @SuppressWarnings("unused")
    public JSONRequest(@NotNull Request request) {
        super(request);
    }

    /**
     * Creates a new JSON request without a request body.
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod) throws IllegalArgumentException {
        super(url, requestMethod);
    }

    /**
     * Creates a new JSON request using a {@link Map} of {@link String}s as request body.
     * The Strings are sent encoded as UTF-8 in "POST parameters" format.
     * The content type for the body is set to "application/x-www-form-urlencoded; charset=utf-8".
     *
     * @param url URL to request.
     * @param requestMethod Request method to be used if protocol uses HTTP-style methods.
     */
    public JSONRequest(@NotNull URL url, @NotNull String requestMethod, @Nullable Map<String, String> requestBody) throws IllegalArgumentException {
        super(url, requestMethod, requestBody);
    }

    @Override
    public synchronized boolean perform() throws IOException {
        final @NotNull HttpURLConnection connection;
        final boolean success;
        final boolean acceptable;

        // We set this to null early, so we can just throw an exception in this method at will.
        responseBody = null;

        connection = (HttpURLConnection) createRequest(acceptableMediaTypes);

        success = connection.getResponseCode() == HttpURLConnection.HTTP_OK;
        acceptable = isAcceptable(connection);

        if (success) {
            LOGGER.info("JSONRequest to " + url + " returned " + connection.getResponseCode() + " " + connection.getResponseMessage() + " [" + connection.getContentType() + "]");
        } else {
            LOGGER.warning("JSONRequest to " + url + " failed with " + connection.getResponseCode() + " " + connection.getResponseMessage() + " [" + connection.getContentType() + "]");
        }

        if (acceptable) {
            final @NotNull InputStream inputStream;

            if (success) {
                inputStream = connection.getInputStream();
            } else {
                inputStream = connection.getErrorStream();
            }

            responseBody = Utils.slurpToString(inputStream);
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
        return Utils.transform(responseBody, JSONObject::new);
    }
}
