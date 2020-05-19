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


import io.ybrid.api.Alias;
import io.ybrid.api.ApiVersion;
import io.ybrid.api.Server;
import io.ybrid.api.Utils;
import io.ybrid.api.driver.common.Factory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EnumSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class selects a {@link Factory} based on a given {@link Server} and {@link Alias}.
 *
 * This should not be used directly.
 */
public final class FactorySelector {
    /**
     * Gets a {@link Factory} based on the parameters.
     * This method may access the network.
     *
     * @param server The {@link Server} to use.
     * @param alias The {@link Alias} to use.
     * @return The instance of the {@link Factory} to use.
     */
    public static Factory getFactory(Server server, Alias alias) throws MalformedURLException {
        EnumSet<ApiVersion> set = getSupportedVersions(server, alias);
        final Logger logger;

        if (server == null)
            server = alias.getServer();

        logger = server.getLogger();

        if (logger.isLoggable(Level.INFO)) {
            logger.info("Supported versions for " + alias.getUrl().toString() +
                    " on " + server.getProtocol() + "://" + server.getHostname() + ":" + server.getPort() +
                    " = " + set);
        }

        if (set.contains(ApiVersion.V2_BETA))
            return new io.ybrid.api.driver.v2.Factory();

        if (set.contains(ApiVersion.V1))
            return new io.ybrid.api.driver.v1.Factory();

        throw new UnsupportedOperationException("Server and client do not share a common supported version.");
    }

    private static EnumSet<ApiVersion> getSupportedVersions(Server server, Alias alias) throws MalformedURLException {
        EnumSet<ApiVersion> ret = EnumSet.noneOf(ApiVersion.class);

        if (alias.getForcedApiVersion() != null) {
            ret.add(alias.getForcedApiVersion());
            return ret;
        }

        if (server == null)
            server = alias.getServer();

        if (server.getForcedApiVersion() != null) {
            ret.add(server.getForcedApiVersion());
            return ret;
        }

        try {
            String path = alias.getUrl().getPath() + "/ctrl/v2/session/info";
            JSONObject response;
            JSONArray supportedVersions;
            URL url;
            HttpURLConnection connection;

            url = new URL(server.getProtocol(), server.getHostname(), server.getPort(), path);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Charset", "utf-8, *; q=0");
            connection.setDoOutput(false);
            connection.setDoInput(true);

            connection.connect();

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                response = Utils.slurpToJSONObject(connection.getInputStream());
            } else {
                response = Utils.slurpToJSONObject(connection.getErrorStream());
            }

            supportedVersions = response.getJSONObject("__responseHeader").getJSONArray("supportedVersions");
            for (int i = 0; i < supportedVersions.length(); i++) {
                ret.add(ApiVersion.fromWire(supportedVersions.getString(i)));
            }
        } catch (Exception e) {
            // Best guess:
            ret.clear();
            ret.add(ApiVersion.V1);
        }
        return ret;
    }
}
