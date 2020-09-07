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

package io.ybrid.api.driver.ybrid.v2;

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.driver.ybrid.v1.SwapInfo;
import io.ybrid.api.metadata.InvalidMetadata;
import io.ybrid.api.metadata.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

final class State implements KnowsSubInfoState {
    private final Map<String, Service> services = new HashMap<>();
    private final EnumSet<SubInfo> changed = EnumSet.noneOf(SubInfo.class);
    private final EnumMap<SubInfo, Instant> lastUpdated = new EnumMap<>(SubInfo.class);
    private final @NotNull Session session;
    private Service defaultService;
    private Service currentService;
    private Metadata currentMetadata;
    private SwapInfo swapInfo;
    private Duration behindLive;
    private URL baseUrl;

    public State(@NotNull Session session, @NotNull URL baseUrl) {
        this.session = session;
        this.baseUrl = baseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        return changed.contains(what);
    }

    @Nullable
    public Instant getLastUpdated(@NotNull SubInfo what) {
        return lastUpdated.get(what);
    }

    private void clearChanged(@NotNull SubInfo what) {
        changed.remove(what);
    }

    private void setChanged(@NotNull SubInfo what) {
        changed.add(what);
        lastUpdated.put(what, ClockManager.now());
    }

    public Metadata getMetadata() {
        clearChanged(SubInfo.METADATA);
        return currentMetadata;
    }

    public Bouquet getBouquet() {
        clearChanged(SubInfo.BOUQUET);
        return new Bouquet(defaultService, new ArrayList<>(services.values()));
    }

    public PlayoutInfo getPlayoutInfo() {
        clearChanged(SubInfo.PLAYOUT);
        return new io.ybrid.api.driver.common.PlayoutInfo(swapInfo, null, behindLive);
    }

    private void updateMetadata(@Nullable JSONObject raw) {
        if (raw == null)
            return;

        try {
            currentMetadata = new io.ybrid.api.driver.ybrid.v1.Metadata(currentService, raw);
        } catch (MalformedURLException ignored) {
        }

        setChanged(SubInfo.METADATA);
    }

    private void updateBaseURL(@Nullable String raw) {
        final @NotNull WorkaroundMap workarounds = session.getActiveWorkarounds();
        @Nullable URL newURL = null;

        if (raw == null)
            return;

        try {
            switch (workarounds.get(Workaround.WORKAROUND_BAD_FQDN)) {
                case TRUE:
                    newURL = null;
                    break;
                case FALSE:
                    newURL = new URL(raw);
                    break;
                case TRI:
                    newURL = new URL(raw);
                    if (!io.ybrid.api.driver.common.Driver.isValidFQDN(newURL.getHost())) {
                        newURL = null;
                        workarounds.enable(Workaround.WORKAROUND_BAD_FQDN);
                    }
                    break;
            }
        } catch (MalformedURLException ignored) {
        }


        if (newURL != null)
            baseUrl = newURL;
    }

    private void updateBouquet(@Nullable JSONObject raw) {
        JSONArray list;
        String primary;
        String active;

        if (raw == null)
            return;

        list = raw.getJSONArray("availableServices");
        if (list == null)
            return;

        services.clear();

        for (int i = 0; i < list.length(); i++) {
            try {
                Service service = new Service(list.optJSONObject(i));
                services.put(service.getIdentifier(), service);
            } catch (MalformedURLException ignored) {
            }
        }

        /*
         * Check if the server provided a primary service.
         * There is a bug in V2_BETA servers where they do not not
         * provide the info on swaps. We try to work around this.
         */
        if (raw.has("primaryServiceId")) {
            primary = raw.getString("primaryServiceId");
            if (primary == null) {
                defaultService = null;
            } else {
                defaultService = services.get(primary);
            }
        } else {
            // No default service provided. Try to reuse the old one.

            if (defaultService == null)
                throw new IllegalStateException("Server did not provide any default service");

            defaultService = services.get(defaultService.getIdentifier());

            if (defaultService == null)
                throw new IllegalStateException("Server did not provide any default service and old default service is gone");
        }

        active = raw.getString("activeServiceId");
        if (active == null) {
            currentService = null;
        } else {
            currentService = services.get(active);
            if (currentMetadata == null) {
                currentMetadata = new InvalidMetadata(currentService);
            }
        }

        setChanged(SubInfo.BOUQUET);
    }

    private void updatePlayout(@Nullable JSONObject raw) {
        if (raw == null)
            return;

        updateBaseURL(raw.getString("baseURL"));

        behindLive = Duration.ofMillis(raw.getLong("offsetToLive"));
        setChanged(SubInfo.PLAYOUT);
    }

    private void updateSwapInfo(@Nullable JSONObject raw) {
        SwapInfo newSwapInfo;
        if (raw == null)
            return;

        newSwapInfo = new SwapInfo(raw);
        swapInfo = newSwapInfo;
        setChanged(SubInfo.PLAYOUT);
    }

    void accept(Response response) {
        updateBouquet(response.getRawBouquet());
        updateMetadata(response.getRawMetadata()); // This must be after updateBouquet() has been called.
        updatePlayout(response.getRawPlayout());
        updateSwapInfo(response.getRawSwapInfo());
        // TODO
    }
}
