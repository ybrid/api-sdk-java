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

package io.ybrid.api.driver.v2;

import io.ybrid.api.Bouquet;
import io.ybrid.api.KnowsSubInfoState;
import io.ybrid.api.Metadata;
import io.ybrid.api.SubInfo;
import io.ybrid.api.driver.v1.SwapInfo;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

public class State implements KnowsSubInfoState {
    private final Map<String, Service> services = new HashMap<>();
    private final EnumSet<SubInfo> changed = EnumSet.noneOf(SubInfo.class);
    private final EnumMap<SubInfo, Instant> lastUpdated = new EnumMap<>(SubInfo.class);
    private Service defaultService;
    private Service currentService;
    private Metadata currentMetadata;
    private SwapInfo swapInfo;
    private URL baseUrl;

    public State(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    @Override
    public boolean hasChanged(SubInfo what) {
        return changed.contains(what);
    }

    @Nullable
    public Instant getLastUpdated(SubInfo what) {
        return lastUpdated.get(what);
    }

    private void clearChanged(SubInfo what) {
        changed.remove(what);
    }

    private void setChanged(SubInfo what) {
        changed.add(what);
        lastUpdated.put(what, Instant.now());
    }

    public Metadata getMetadata() {
        clearChanged(SubInfo.METADATA);
        return currentMetadata;
    }

    public SwapInfo getSwapInfo() {
        clearChanged(SubInfo.SWAP_INFO);
        return swapInfo;
    }

    public Bouquet getBouquet() {
        clearChanged(SubInfo.BOUQUET);
        return new Bouquet(defaultService, new ArrayList<>(services.values()));
    }

    private void updateMetadata(JSONObject raw) {
        if (raw == null)
            return;

        try {
            currentMetadata = new io.ybrid.api.driver.v1.Metadata(currentService, raw);
        } catch (MalformedURLException ignored) {
        }

        setChanged(SubInfo.METADATA);
    }

    private void updateBouquet(JSONObject raw) {
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

        primary = raw.getString("primaryServiceId");
        if (primary == null) {
            defaultService = null;
        } else {
            defaultService = services.get(primary);
        }

        active = raw.getString("activeServiceId");
        if (active == null) {
            currentService = null;
        } else {
            currentService = services.get(active);
        }

        setChanged(SubInfo.BOUQUET);
    }

    private void updatePlayout(JSONObject raw) {
        if (raw == null)
            return;

        try {
            baseUrl = new URL(raw.getString("baseURL"));
        } catch (MalformedURLException ignored) {
        }
    }

    private void updateSwapInfo(JSONObject raw) {
        SwapInfo newSwapInfo;
        if (raw == null)
            return;

        newSwapInfo = new SwapInfo(raw);
        if (!Objects.equals(swapInfo, newSwapInfo))
            setChanged(SubInfo.SWAP_INFO);
        lastUpdated.put(SubInfo.SWAP_INFO, Instant.now());
        swapInfo = newSwapInfo;
    }

    void accept(Response response) {
        updateBouquet(response.getRawBouquet());
        updateMetadata(response.getRawMetadata()); // This must be after updateBouquet() has been called.
        updatePlayout(response.getRawPlayout());
        updateSwapInfo(response.getRawSwapInfo());
        // TODO
    }
}
