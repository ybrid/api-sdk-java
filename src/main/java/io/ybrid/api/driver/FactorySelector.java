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


import io.ybrid.api.ApiVersion;
import io.ybrid.api.MediaEndpoint;
import io.ybrid.api.Server;
import io.ybrid.api.util.uri.Builder;
import io.ybrid.api.util.uri.Path;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class selects a {@link Factory} based on a given {@link Server} and {@link MediaEndpoint}.
 *
 * This should not be used directly.
 */
@ApiStatus.Internal
public final class FactorySelector {
    private static final Logger LOGGER = Logger.getLogger(FactorySelector.class.getName());

    @ApiStatus.Internal
    private static final class Result {
        final public @NotNull EnumSet<ApiVersion> set;
        final public @NotNull String method;

        public Result(@NotNull EnumSet<ApiVersion> set, @NotNull String method) {
            this.set = set;
            this.method = method;
        }

        private boolean can(@NotNull ApiVersion version) {
            return set.contains(version);
        }
    }

    /**
     * Gets a {@link Factory} based on the parameters.
     * This method may access the network.
     *
     * @param server The {@link Server} to use.
     * @param mediaEndpoint The {@link MediaEndpoint} to use.
     * @return The instance of the {@link Factory} to use.
     */
    public static @NotNull Factory getFactory(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) throws MalformedURLException {
        final @NotNull Result result = getSupportedVersions(server, mediaEndpoint);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Supported versions for " + mediaEndpoint.getURI() +
                    " on " + server.getProtocol() + "://" + server.getHostname() + ":" + server.getPort() +
                    " = " + result.set + " by " + result.method);
        }

        if (result.can(ApiVersion.YBRID_V2_BETA))
            return new io.ybrid.api.driver.ybrid.v2.Factory();

        if (result.can(ApiVersion.YBRID_V1))
            return new io.ybrid.api.driver.ybrid.v1.Factory();

        if (result.can(ApiVersion.ICY) || result.can(ApiVersion.ICECAST_V2_4) || result.can(ApiVersion.ICECAST_V2_5_BETA))
            return new io.ybrid.api.driver.icy.Factory();

        if (result.can(ApiVersion.PLAIN))
            return new io.ybrid.api.driver.plain.Factory();

        throw new UnsupportedOperationException("Server and client do not share a common supported version.");
    }

    private static Result getSupportedVersions(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) {
        if (mediaEndpoint.getForcedApiVersion() != null) {
            return new Result(EnumSet.of(mediaEndpoint.getForcedApiVersion()), "force on MediaEndpoint");
        }

        if (server.getForcedApiVersion() != null) {
            return new Result(EnumSet.of(server.getForcedApiVersion()), "force on Server");
        }

        try {
            return new Result(getSupportedVersionsFromOptions(server, mediaEndpoint), "OPTIONS");
        } catch (Exception ignored) {
        }

        try {
            return new Result(getSupportedVersionsFromYbridV2Server(server, mediaEndpoint), "Ybrid v2 request");
        } catch (Exception ignored) {
        }

        // Best guess:
        return new Result(EnumSet.of(ApiVersion.PLAIN), "using default");
    }

    private static EnumSet<ApiVersion> getSupportedVersionsFromOptions(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) throws IOException, URISyntaxException {
        return getSupportedVersionsFromYbridV2Server(server, mediaEndpoint, null, "OPTIONS");
    }

    private static EnumSet<ApiVersion> getSupportedVersionsFromYbridV2Server(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) throws IOException, URISyntaxException {
        return getSupportedVersionsFromYbridV2Server(server, mediaEndpoint, new Path("/ctrl/v2/session/info"), "GET");
    }

    private static EnumSet<ApiVersion> getSupportedVersionsFromYbridV2Server(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint, @Nullable Path pathSuffix, @NotNull String method) throws IOException, URISyntaxException {
        final EnumSet<ApiVersion> ret = EnumSet.noneOf(ApiVersion.class);
        final @NotNull Builder builder = new Builder(mediaEndpoint.getURI());
        final @NotNull JSONRequest request;
        JSONArray supportedVersions;

        builder.setServer(server);
        if (pathSuffix != null)
            builder.appendPath(pathSuffix);

        request = new JSONRequest(builder.toURL(), method);
        request.perform();

        supportedVersions = Objects.requireNonNull(request.getResponseBody()).getJSONObject("__responseHeader").getJSONArray("supportedVersions");
        for (int i = 0; i < supportedVersions.length(); i++) {
            ret.add(ApiVersion.fromWire(supportedVersions.getString(i)));
        }

        return ret;
    }
}
