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

package io.ybrid.api;

import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.driver.FactorySelector;
import io.ybrid.api.driver.common.Driver;
import io.ybrid.api.metadata.ItemType;
import io.ybrid.api.metadata.Metadata;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.metadata.source.SourceType;
import io.ybrid.api.transport.TransportDescription;
import io.ybrid.api.transport.URITransportDescription;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.logging.Logger;

/**
 * This class implements an actual session with a Ybrid server.
 *
 * The session can be used to request an audio stream from the server.
 * It is also used to control the stream.
 */
public class Session implements Connectable, SessionClient {
    static final Logger LOGGER = Logger.getLogger(Session.class.getName());

    private final @NotNull Source source = new Source(SourceType.SESSION);
    private final @NotNull WorkaroundMap activeWorkarounds = new WorkaroundMap();
    private final @NotNull MetadataMixer metadataMixer;
    private final @NotNull Driver driver;
    private final @NotNull Server server;
    private final @NotNull Alias alias;
    private Map<String, Double> acceptedMediaFormats = null;
    private Map<String, Double> acceptedLanguages = null;

    private static void assertValidAcceptList(@Nullable Map<String, Double> list) throws IllegalArgumentException {
        if (list == null)
            return;

        for (double weight : list.values())
            if (weight < 0 || weight > 1)
                throw new IllegalArgumentException("Invalid weight=" + weight + ", must be in range [0,1]");
    }

    private void loadSessionToMixer() {
        try {
            // get initial metadata if any.
            if (driver.hasChanged(SubInfo.BOUQUET)) {
                metadataMixer.add(driver.getBouquet(), source);
                metadataMixer.add(driver.getMetadata().getService(), source, MetadataMixer.Position.CURRENT, TemporalValidity.INDEFINITELY_VALID);
                LOGGER.info("Loaded initial bouquet");
            }
            if (driver.hasChanged(SubInfo.METADATA)) {
                driver.clearChanged(SubInfo.METADATA);
                metadataMixer.add(driver.getMetadata(), source, getPlayoutInfo().getTemporalValidity());
                LOGGER.info("Loaded initial metadata");
            }
        } catch (Exception ignored) {
        }
    }

    Session(@NotNull Server server, @NotNull Alias alias) throws MalformedURLException {
        this.server = server;
        this.alias = alias;
        this.driver = FactorySelector.getFactory(server, alias).getDriver(this);
        this.metadataMixer = new MetadataMixer(this.driver::acceptSessionSpecific);

        activeWorkarounds.merge(alias.getWorkarounds());
        activeWorkarounds.merge(server.getWorkarounds());

        loadSessionToMixer();
    }

    /**
     * Gets the {@link MetadataMixer} for this session.
     * @return Gets the current {@link MetadataMixer}.
     */
    public @NotNull MetadataMixer getMetadataMixer() {
        return metadataMixer;
    }

    /**
     * Gets the {@link Alias} used for this session.
     * @return Returns the {@link Alias}.
     */
    public @NotNull Alias getAlias() {
        return alias;
    }

    /**
     * Gets the {@link Server} object used to communicate with the server.
     * @return Returns the {@link Server} object.
     */
    public @NotNull Server getServer() {
        return server;
    }

    @Override
    public @NotNull CapabilitySet getCapabilities() {
        return driver.getCapabilities();
    }

    @Override
    public @NotNull Bouquet getBouquet() {
        if (driver.hasChanged(SubInfo.BOUQUET)) {
            driver.clearChanged(SubInfo.BOUQUET);
            metadataMixer.add(driver.getBouquet(), source);
        }

        return metadataMixer.getBouquet();
    }

    @Override
    public void windToLive() throws IOException {
        driver.windToLive();
    }

    @Override
    public void windTo(@NotNull Instant timestamp) throws IOException {
        driver.windTo(timestamp);
    }

    @Override
    public void wind(@NotNull Duration duration) throws IOException {
        driver.wind(duration);
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        driver.skipForwards(itemType);
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        driver.skipBackwards(itemType);
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        driver.swapItem(mode);
    }

    @Override
    public void swapService(@NotNull Service service) throws IOException {
        driver.swapService(service);
    }

    @Override
    public void swapToMain() throws IOException {
        driver.swapToMain();
    }

    @Override
    public @NotNull Metadata getMetadata() {
        if (driver.hasChanged(SubInfo.METADATA)) {
            driver.clearChanged(SubInfo.METADATA);
            metadataMixer.add(driver.getMetadata(), source, getPlayoutInfo().getTemporalValidity());
        }
        return metadataMixer.getMetadata();
    }

    @Override
    public @NotNull PlayoutInfo getPlayoutInfo() {
        driver.clearChanged(SubInfo.PLAYOUT);
        return driver.getPlayoutInfo();
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        return metadataMixer.hasChanged(what) || driver.hasChanged(what);
    }

    @Override
    public void refresh(@NotNull SubInfo what) throws IOException {
        driver.refresh(what);
    }

    @Override
    public void refresh(@NotNull EnumSet<SubInfo> what) throws IOException {
        driver.refresh(what);
    }

    /**
     * Get the list of media formats supported by the player.
     *
     * If this returns null no {@code Accept:}-header should be generated.
     * @return List of supported formats or null.
     */
    @Nullable
    public Map<String, Double> getAcceptedMediaFormats() {
        return acceptedMediaFormats;
    }

    /**
     * Set the list of formats supported by the player and their corresponding weights.
     * @param acceptedMediaFormats List of supported formats or null.
     */
    public void setAcceptedMediaFormats(@Nullable Map<String, Double> acceptedMediaFormats) {
        assertValidAcceptList(acceptedMediaFormats);
        this.acceptedMediaFormats = acceptedMediaFormats;
    }

    /**
     * Get list of languages requested by the user.
     *
     * If this returns null no {@code Accept-Language:}-header should be generated.
     * @return List of languages requested by the user or null.
     */
    @Nullable
    public Map<String, Double> getAcceptedLanguages() {
        return acceptedLanguages;
    }

    /**
     * Sets the list of languages requested by the user.
     *
     * This function is only still included for older versions of Android
     * (before {@code android.os.Build.VERSION_CODES.O})
     * and might be removed at any time.
     *
     * @param acceptedLanguages List of languages to set or null.
     * @deprecated Use {@link #setAcceptedLanguages(List)} instead.
     */
    @Deprecated
    public void setAcceptedLanguages(@Nullable Map<String, Double> acceptedLanguages) {
        assertValidAcceptList(acceptedLanguages);
        this.acceptedLanguages = acceptedLanguages;
    }

    /**
     * Sets the list of languages requested by the user and their corresponding weights.
     * @param list The list of languages or null.
     */
    public void setAcceptedLanguages(@Nullable List<Locale.LanguageRange> list) {
        Map<String, Double> newList;

        if (list == null) {
            this.acceptedLanguages = null;
            return;
        }

        newList = new HashMap<>();

        for (Locale.LanguageRange range : list)
            newList.put(range.getRange(), range.getWeight());

        assertValidAcceptList(newList);
        this.acceptedLanguages = newList;
    }

    /**
     * Gets the map of currently active workarounds.
     * This map can be updated by the caller if needed.
     * However special care must be taken when doing so to avoid data corruption.
     * Generally applications that wish to communicate workaround settings
     * should use {@link Alias#getWorkarounds()} or {@link Server#getWorkarounds()}.
     * Those methods are always safe to use.
     *
     * @return The map of active workarounds.
     * @see Alias#getWorkarounds()
     * @see Server#getWorkarounds()
     */
    public @NotNull WorkaroundMap getActiveWorkarounds() {
        return activeWorkarounds;
    }

    /**
     * gets the {@link URL} of the audio stream.
     * @return The {@link URL} of the stream.
     * @deprecated Use {@link #getStreamTransportDescription()}.
     */
    @Deprecated
    public URI getStreamURI() {
        try {
            return driver.getStreamURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the {@link TransportDescription} that can be used to access the audio stream.
     * @return The transport description for the audio stream.
     */
    public TransportDescription getStreamTransportDescription() {
        try {
            return new URITransportDescription(new Source(SourceType.TRANSPORT), metadataMixer.getCurrentService(), metadataMixer, getAcceptedMediaFormats(), getAcceptedLanguages(), driver.getStreamURI(), null);
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        driver.disconnect();
    }

    @Override
    public boolean isConnected() {
        return driver.isConnected();
    }

    @Override
    public void connect() throws IOException {
        driver.connect();
        loadSessionToMixer();
    }

    @Override
    public boolean isValid() {
        driver.clearChanged(SubInfo.VALIDITY);
        return driver.isValid();
    }

    @Override
    public void close() throws IOException {
        driver.close();
    }
}
