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

package io.ybrid.api.driver;


import io.ybrid.api.MediaEndpoint;
import io.ybrid.api.MediaProtocol;
import io.ybrid.api.session.Session;
import io.ybrid.api.Workaround;
import io.ybrid.api.util.TriState;
import io.ybrid.api.util.Utils;
import io.ybrid.api.util.uri.Builder;
import io.ybrid.api.util.uri.Path;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class selects a {@link Driver} based on a given {@link MediaEndpoint} (via {@link Session}).
 *
 * This should not be used directly.
 */
@ApiStatus.Internal
public final class DriverSelector {
    private static final Logger LOGGER = Logger.getLogger(DriverSelector.class.getName());

    @ApiStatus.Internal
    private static final class Result {
        final public @NotNull EnumSet<MediaProtocol> set;
        final public @NotNull String method;
        final public boolean autoDetected;
        final public @Nullable URI baseURI;

        public Result(@NotNull EnumSet<MediaProtocol> set, @NotNull String method, boolean autoDetected, @Nullable URI baseURI) {
            this.set = set;
            this.method = method;
            this.autoDetected = autoDetected;
            this.baseURI = baseURI;
        }

        private boolean can(@NotNull MediaProtocol version) {
            return set.contains(version);
        }
    }

    /**
     * Gets a {@link Driver} based on the parameters.
     * This method may access the network.
     *
     * @param session The {@link Session} to use.
     * @return The instance of the {@link Driver} to use.
     */
    public static @NotNull Driver getFactory(@NotNull Session session) throws MalformedURLException {
        @NotNull MediaEndpoint mediaEndpoint = session.getMediaEndpoint();
        final @NotNull Result result = getSupportedVersions(mediaEndpoint);
        final @NotNull URI baseURI = Objects.requireNonNull(Utils.firstOf(result.baseURI, mediaEndpoint.getURI()));

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Supported versions for " + mediaEndpoint.getURI() +
                    " = " + result.set + " by " + result.method + " with base URI " + baseURI);
        }

        if (result.can(MediaProtocol.YBRID_V2_BETA) && !result.autoDetected)
            return new io.ybrid.api.driver.ybrid.v2.Driver(session, baseURI);

        if (result.can(MediaProtocol.YBRID_V1))
            return new io.ybrid.api.driver.ybrid.v1.Driver(session, baseURI);

        if (result.can(MediaProtocol.ICY) || result.can(MediaProtocol.ICECAST_V2_4) || result.can(MediaProtocol.ICECAST_V2_5_BETA))
            return new io.ybrid.api.driver.icy.Driver(session, baseURI);

        if (result.can(MediaProtocol.PLAIN))
            return new io.ybrid.api.driver.plain.Driver(session, baseURI);

        throw new UnsupportedOperationException("Server and client do not share a common supported version.");
    }

    private static Result getSupportedVersions(@NotNull MediaEndpoint mediaEndpoint) {
        if (mediaEndpoint.getForcedMediaProtocol() != null) {
            return new Result(EnumSet.of(mediaEndpoint.getForcedMediaProtocol()), "force on MediaEndpoint", false, null);
        }

        try {
            return getSupportedVersionsFromOptions(mediaEndpoint);
        } catch (Exception ignored) {
        }

        try {
            return getSupportedVersionsFromYbridV2Server(mediaEndpoint);
        } catch (Exception ignored) {
        }

        if (mediaEndpoint.getWorkarounds().get(Workaround.WORKAROUND_GUESS_ICY).equals(TriState.TRUE))
            return new Result(EnumSet.of(MediaProtocol.ICY), "using default", true, null);

        // Best guess:
        return new Result(EnumSet.of(MediaProtocol.PLAIN), "using default", true, null);
    }

    @Contract("_ -> new")
    private static @NotNull Result getSupportedVersionsFromOptions(@NotNull MediaEndpoint mediaEndpoint) throws IOException, URISyntaxException {
        return getSupportedVersionsFromYbridV2Server(mediaEndpoint, null, "OPTIONS", "OPTIONS");
    }

    @Contract("_ -> new")
    private static @NotNull Result getSupportedVersionsFromYbridV2Server(@NotNull MediaEndpoint mediaEndpoint) throws IOException, URISyntaxException {
        return getSupportedVersionsFromYbridV2Server(mediaEndpoint, new Path("/ctrl/v2/session/info"), "GET", "Ybrid v2 request");
    }

    @Contract("_, _, _, _ -> new")
    private static @NotNull Result getSupportedVersionsFromYbridV2Server(@NotNull MediaEndpoint mediaEndpoint, @Nullable Path pathSuffix, @NotNull String method, @NotNull String resultMethod) throws IOException, URISyntaxException {
        final EnumSet<MediaProtocol> ret = EnumSet.noneOf(MediaProtocol.class);
        final @NotNull Builder builder = new Builder(mediaEndpoint.getURI());
        final @NotNull JSONRequest request;
        final @NotNull JSONArray supportedVersions;
        @Nullable URI baseURI = null;

        if (pathSuffix != null)
            builder.appendPath(pathSuffix);

        request = new JSONRequest(builder.toURL(), method);
        request.perform();

        supportedVersions = Objects.requireNonNull(request.getResponseBody()).getJSONObject("__responseHeader").getJSONArray("supportedVersions");
        for (int i = 0; i < supportedVersions.length(); i++) {
            ret.add(MediaProtocol.fromWire(supportedVersions.getString(i)));
        }

        try {
            baseURI = URI.create(getBaseURI(request.getResponseBody().getJSONObject("__responseObject")));
        } catch (Throwable ignored) {
        }

        return new Result(ret, resultMethod, true, baseURI);
    }

    private static @NotNull String getBaseURI(@NotNull JSONObject responseObject) {
        if (responseObject.has("baseURI"))
            return responseObject.getString("baseURI");

        if (responseObject.has("baseURL"))
            return responseObject.getString("baseURL");

        if (responseObject.has("playout")) {
            final @NotNull JSONObject playoutInfo = responseObject.getJSONObject("playout");

            if (playoutInfo.has("baseURI"))
                return playoutInfo.getString("baseURI");

            if (playoutInfo.has("baseURL"))
                return playoutInfo.getString("baseURL");
        }

        throw new IllegalArgumentException();
    }
}
