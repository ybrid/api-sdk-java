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
import org.jetbrains.annotations.NotNull;
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
public final class FactorySelector {
    private static final Logger LOGGER = Logger.getLogger(FactorySelector.class.getName());

    /**
     * Gets a {@link Factory} based on the parameters.
     * This method may access the network.
     *
     * @param server The {@link Server} to use.
     * @param mediaEndpoint The {@link MediaEndpoint} to use.
     * @return The instance of the {@link Factory} to use.
     */
    public static @NotNull Factory getFactory(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) throws MalformedURLException {
        EnumSet<ApiVersion> set = getSupportedVersions(server, mediaEndpoint);

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Supported versions for " + mediaEndpoint.getURI().toString() +
                    " on " + server.getProtocol() + "://" + server.getHostname() + ":" + server.getPort() +
                    " = " + set);
        }

        if (set.contains(ApiVersion.YBRID_V2_BETA))
            return new io.ybrid.api.driver.ybrid.v2.Factory();

        if (set.contains(ApiVersion.YBRID_V1))
            return new io.ybrid.api.driver.ybrid.v1.Factory();

        if (set.contains(ApiVersion.ICY))
            return new io.ybrid.api.driver.icy.Factory();

        if (set.contains(ApiVersion.PLAIN))
            return new io.ybrid.api.driver.plain.Factory();

        throw new UnsupportedOperationException("Server and client do not share a common supported version.");
    }

    private static EnumSet<ApiVersion> getSupportedVersions(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) {
        if (mediaEndpoint.getForcedApiVersion() != null) {
            return EnumSet.of(mediaEndpoint.getForcedApiVersion());
        }

        if (server.getForcedApiVersion() != null) {
            return EnumSet.of(server.getForcedApiVersion());
        }

        try {
            return getSupportedVersionsFromYbridV2Server(server, mediaEndpoint);
        } catch (Exception ignored) {
        }

        // Best guess:
        return EnumSet.of(ApiVersion.PLAIN);
    }

    private static EnumSet<ApiVersion> getSupportedVersionsFromYbridV2Server(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) throws IOException, URISyntaxException {
        final EnumSet<ApiVersion> ret = EnumSet.noneOf(ApiVersion.class);
        final @NotNull Builder builder = new Builder(mediaEndpoint.getURI());
        final @NotNull JSONRequest request;
        JSONArray supportedVersions;

        builder.setServer(server);
        builder.appendPath(new Path("/ctrl/v2/session/info"));

        request = new JSONRequest(builder.toURL(), "GET");
        request.perform();

        supportedVersions = Objects.requireNonNull(request.getResponseBody()).getJSONObject("__responseHeader").getJSONArray("supportedVersions");
        for (int i = 0; i < supportedVersions.length(); i++) {
            ret.add(ApiVersion.fromWire(supportedVersions.getString(i)));
        }

        return ret;
    }
}
