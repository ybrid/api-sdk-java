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
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.bouquet.SimpleService;
import io.ybrid.api.driver.ybrid.common.SwapInfo;
import io.ybrid.api.metadata.InvalidMetadata;
import io.ybrid.api.metadata.Metadata;
import io.ybrid.api.metadata.Sync;
import io.ybrid.api.util.ClockManager;
import io.ybrid.api.util.Identifier;
import io.ybrid.api.util.Utils;
import io.ybrid.api.util.uri.Builder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

final class State implements KnowsSubInfoState {
    private static final Logger LOGGER = Logger.getLogger(State.class.getName());

    private final Map<Identifier, Service> services = new HashMap<>();
    private final EnumSet<SubInfo> changed = EnumSet.noneOf(SubInfo.class);
    private final EnumMap<SubInfo, Instant> lastUpdated = new EnumMap<>(SubInfo.class);
    private final @NotNull Session session;
    private Service defaultService;
    private Service currentService;
    private Metadata currentMetadata;
    private SwapInfo swapInfo;
    private Duration behindLive;
    private URI baseURI;
    private URI playbackURI;
    private String token;

    public State(@NotNull Session session, @NotNull URI baseURI) {
        this.session = session;
        this.baseURI = baseURI;
    }

    public URI getBaseURI() {
        return baseURI;
    }

    public URI getPlaybackURI() {
        return playbackURI;
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

    @Contract(pure = true)
    public Service getCurrentService() {
        return currentService;
    }

    public void refresh(@NotNull Sync sync) {
        final @NotNull Sync.Builder builder = new Sync.Builder(session.getSource(), sync);

        builder.setCurrentTrack(currentMetadata.getCurrentItem());
        builder.setNextTrack(currentMetadata.getNextItem());
        builder.setCurrentService(currentService);
        builder.setTemporalValidity(getPlayoutInfo().getTemporalValidity());

        session.getMetadataMixer().accept(builder.build());
    }

    public Bouquet getBouquet() {
        clearChanged(SubInfo.BOUQUET);
        return new Bouquet(defaultService, services.values());
    }

    public PlayoutInfo getPlayoutInfo() {
        clearChanged(SubInfo.PLAYOUT);
        return new io.ybrid.api.driver.common.PlayoutInfo(swapInfo, null, behindLive);
    }

    public @Nullable SwapInfo getSwapInfo() {
        return swapInfo;
    }

    private void updateMetadata(@Nullable JSONObject raw) {
        if (raw == null)
            return;

        try {
            currentMetadata = new io.ybrid.api.driver.ybrid.common.Metadata(currentService, raw);
        } catch (URISyntaxException ignored) {
        }

        setChanged(SubInfo.METADATA);
    }

    private void updateBaseURL(@Nullable String raw) {
        final @NotNull WorkaroundMap workarounds = session.getActiveWorkarounds();
        @Nullable URI newURI = null;

        if (raw == null)
            return;

        try {
            switch (workarounds.get(Workaround.WORKAROUND_BAD_FQDN)) {
                case TRUE:
                    newURI = null;
                    break;
                case FALSE:
                    newURI = new URI(raw);
                    break;
                case TRI:
                    newURI = new URI(raw);
                    if (!Utils.isValidFQDN(newURI.getHost())) {
                        newURI = null;
                        workarounds.enable(Workaround.WORKAROUND_BAD_FQDN);
                    }
                    break;
            }
        } catch (URISyntaxException ignored) {
        }


        if (newURI != null)
            baseURI = newURI;
    }

    private URI guessPlaybackURI() throws URISyntaxException, UnsupportedEncodingException, MalformedURLException {
        final @NotNull Builder builder = new Builder(baseURI);

        builder.setPort();

        if (builder.getRawScheme().equals("https")) {
            builder.setRawScheme("icyxs");
        } else {
            builder.setRawScheme("icyx");
        }

        builder.setQuery("session-id", token);

        LOGGER.warning("Was asked to guess playbackURI, guessed: " + builder.toURIString());
        return builder.toURI();
    }

    private void updatePlaybackURI(@Nullable String raw) {
        final @NotNull WorkaroundMap workarounds = session.getActiveWorkarounds();

        LOGGER.info("Request to set playbackURI to: " + raw);

        if (workarounds.get(Workaround.WORKAROUND_INVALID_PLAYBACK_URI).toBool(false)) {
            try {
                playbackURI = guessPlaybackURI();
            } catch (URISyntaxException | UnsupportedEncodingException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        } else {
            if (raw == null)
                return;

            try {
                switch (workarounds.get(Workaround.WORKAROUND_BAD_FQDN)) {
                    case FALSE:
                        playbackURI = new URI(raw);
                        break;
                    case TRUE:
                        if (playbackURI == null)
                            playbackURI = guessPlaybackURI();
                        break;
                    case TRI: {
                        final @NotNull URI uri = new URI(raw);
                        if (Utils.isValidFQDN(uri.getHost())) {
                            playbackURI = uri;
                        } else {
                            if (playbackURI == null)
                                playbackURI = guessPlaybackURI();
                            workarounds.enable(Workaround.WORKAROUND_BAD_FQDN);
                        }
                        break;
                    }
                }

                LOGGER.info("playbackURI set to: " + playbackURI);
            } catch (URISyntaxException | UnsupportedEncodingException | MalformedURLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private @Nullable URI jsonToURI(@NotNull JSONObject json, @NotNull String key) throws URISyntaxException {
        final @Nullable String value = json.getString(key);
        if (value != null && !value.isEmpty()) {
            return new URI(value);
        } else {
            return null;
        }
    }

    private void updateBouquet(@Nullable JSONObject raw) {
        final @NotNull WorkaroundMap workaroundMap = session.getActiveWorkarounds();
        JSONArray list;
        String primary;
        String active;

        LOGGER.info("raw = " + raw + ", workaroundMap = " + workaroundMap + ", services = " + services);

        if (raw == null) {
            final @NotNull Service service;

            if (!workaroundMap.get(Workaround.WORKAROUND_NO_BOUQUET).toBool(true) || !services.isEmpty()) {
                return;
            }

            LOGGER.severe("Server sent invalid reply (required bouquet is missing)");
            workaroundMap.enableIfAutomatic(Workaround.WORKAROUND_NO_BOUQUET);

            service = new SimpleService();
            services.put(service.getIdentifier(), service);
            defaultService = service;
            currentService = service;
            currentMetadata = new InvalidMetadata(currentService);
            setChanged(SubInfo.BOUQUET);
            return;
        }

        list = raw.getJSONArray("availableServices");
        if (list == null)
            return;

        services.clear();

        for (int i = 0; i < list.length(); i++) {
            final @NotNull JSONObject json = list.optJSONObject(i);
            final @NotNull String identifier = json.getString("id");
            @NotNull String displayName;
            final @NotNull Service service;
            @Nullable URI iconURI;

            try {
                displayName = json.getString("displayName");
            } catch (Throwable e) {
                if (workaroundMap.get(Workaround.WORKAROUND_SERVICE_WITH_NO_DISPLAY_NAME).toBool(true)) {
                    displayName = identifier;
                    workaroundMap.enableIfAutomatic(Workaround.WORKAROUND_SERVICE_WITH_NO_DISPLAY_NAME);
                } else {
                    throw new RuntimeException("Service \"" + identifier + "\" has no displayName. Consider to enabling " + Workaround.WORKAROUND_SERVICE_WITH_NO_DISPLAY_NAME, e);
                }
            }

            try {
                iconURI = jsonToURI(json, "iconURL");
            } catch (Throwable e) {
                iconURI = null;
            }

            service = new SimpleService(displayName, new Identifier(identifier, SimpleService.class), iconURI, null);

            services.put(service.getIdentifier(), service);
        }

        active = raw.getString("activeServiceId");
        if (active == null) {
            currentService = null;
        } else {
            currentService = services.get(new Identifier(active, Service.class));
            if (currentMetadata == null) {
                currentMetadata = new InvalidMetadata(currentService);
            }
        }

        /*
         * Check if the server provided a primary service.
         * There is a bug in V2_BETA servers of them not
         * providing the info on swaps. We try to work around this.
         */
        if (raw.has("primaryServiceId")) {
            primary = raw.getString("primaryServiceId");
            if (primary == null) {
                defaultService = null;
            } else {
                defaultService = services.get(new Identifier(primary, Service.class));

                if (defaultService == null) {
                    LOGGER.severe("Server provided invalid default service. Service not part of bouquet: " + primary);

                    if (!workaroundMap.get(Workaround.WORKAROUND_DEFAULT_SERVICE_NOT_IN_BOUQUET).toBool(true)) {
                        throw new IllegalStateException("Server provided invalid default service. Service not part of bouquet: " + primary);
                    }

                    if (currentService != null) {
                        workaroundMap.enableIfAutomatic(Workaround.WORKAROUND_DEFAULT_SERVICE_NOT_IN_BOUQUET);
                        LOGGER.warning("Assuming current service as default service: " + currentService);
                        defaultService = currentService;
                    }
                }
            }
        } else {
            // No default service provided. Try to reuse the old one.

            if (defaultService == null)
                throw new IllegalStateException("Server did not provide any default service");

            defaultService = services.get(defaultService.getIdentifier());

            if (defaultService == null)
                throw new IllegalStateException("Server did not provide any default service and old default service is gone");
        }

        setChanged(SubInfo.BOUQUET);
    }

    private void updatePlayout(@Nullable JSONObject raw) {
        if (raw == null)
            return;

        updateBaseURL(raw.getString("baseURL"));
        updatePlaybackURI(raw.getString("playbackURI"));

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

    void accept(@NotNull Response response) {
        token = response.getToken();
        updateBouquet(response.getRawBouquet());
        updateMetadata(response.getRawMetadata()); // This must be after updateBouquet() has been called.
        updatePlayout(response.getRawPlayout());
        updateSwapInfo(response.getRawSwapInfo());
        // TODO
    }
}
