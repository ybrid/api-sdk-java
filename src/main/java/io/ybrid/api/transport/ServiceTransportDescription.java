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

package io.ybrid.api.transport;

import io.ybrid.api.MediaEndpoint;
import io.ybrid.api.WorkaroundMap;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.MetadataMixer;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.transaction.Transaction;
import io.ybrid.api.util.QualityMap.LanguageMap;
import io.ybrid.api.util.hasAcceptedLanguages;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is the base class for all transport descriptions.
 */
public abstract class ServiceTransportDescription implements hasAcceptedLanguages {
    protected @NotNull final Source source;
    protected @NotNull final Service initialService;
    protected @NotNull final MetadataMixer metadataMixer;
    protected @Nullable final Map<String, Double> acceptedMediaFormats;
    protected @Nullable final LanguageMap acceptedLanguages;
    protected @NotNull final Transaction transaction;
    protected final @NotNull WorkaroundMap activeWorkarounds;

    /**
     * Main constructor.
     *
     * @param source The source that shall represent the new transport.
     * @param initialService The initial service to connect to.
     * @param metadataMixer The Metadata mixer the new transport should send updates to.
     * @param acceptedMediaFormats List of accepted media formats or {@code null}.
     * @param acceptedLanguages List of accepted languages or {@code null}.
     * @param transaction The {@link Transaction} causing the creation of this transport description.
     * @param activeWorkarounds The set of active workarounds for this transport.
     * @see #getAcceptedMediaFormats()
     * @see #getAcceptedLanguagesMap()
     */
    @ApiStatus.Internal
    protected ServiceTransportDescription(@NotNull Source source,
                                          @NotNull Service initialService,
                                          @NotNull MetadataMixer metadataMixer,
                                          @Nullable Map<String, Double> acceptedMediaFormats,
                                          @Nullable LanguageMap acceptedLanguages,
                                          @NotNull Transaction transaction,
                                          @NotNull WorkaroundMap activeWorkarounds) {
        this.source = source;
        this.initialService = initialService;
        this.metadataMixer = metadataMixer;
        this.acceptedMediaFormats = acceptedMediaFormats;
        this.acceptedLanguages = acceptedLanguages;
        this.transaction = transaction;
        this.activeWorkarounds = activeWorkarounds;
    }

    /**
     * Gets the {@link Source} that shall be represent the new transport.
     * @return The representing source.
     */
    public @NotNull Source getSource() {
        return source;
    }

    /**
     * Gets the service the new transport should initially connect to.
     * @return The service to connect to.
     */
    public @NotNull Service getInitialService() {
        return initialService;
    }

    /**
     * Gets the {@link MetadataMixer} the new transport should send metadata updates to.
     * @return The mixer.
     */
    public @NotNull MetadataMixer getMetadataMixer() {
        return metadataMixer;
    }

    /**
     * Get the list of media formats accepted for this transport.
     * <P>
     * For HTTP based protocols:
     * If this returns {@code null} no {@code Accept:}-header should be generated.
     *
     * @return List of accepted formats or {@code null}.
     */
    public @Nullable Map<String, Double> getAcceptedMediaFormats() {
        return acceptedMediaFormats;
    }

    @Override
    public @Nullable LanguageMap getAcceptedLanguagesMap() {
        return acceptedLanguages;
    }

    /**
     * Gets the {@link Transaction} that caused the creation of this transport description.
     *
     * @return The Corresponding {@link Transaction}.
     */
    public @NotNull Transaction getTransaction() {
        return transaction;
    }

    /**
     * Gets the map of currently active workarounds.
     * This map can be updated by the module implementing support for this transport if needed.
     * However special care must be taken when doing so to avoid data corruption.
     * Applications must not update the map.
     * Generally applications that wish to communicate workaround settings
     * should use {@link MediaEndpoint#getWorkarounds()}.
     * Those methods are always safe to use.
     *
     * @return The map of active workarounds.
     * @see MediaEndpoint#getWorkarounds()
     */
    public @NotNull WorkaroundMap getActiveWorkarounds() {
        return activeWorkarounds;
    }

    /**
     * This function must be called to signal any state changes of the transport's connection
     * back to the description provider.
     *
     * @param state The new state of transport.
     * @see TransportConnectionState
     */
    public void signalConnectionState(@NotNull TransportConnectionState state) {
        switch (state) {
            case DISCONNECTED:
            case DISCONNECTING:
            case ERROR:
                metadataMixer.remove(getSource());
                break;
            case CONNECTING:
            case CONNECTED:
            case RECEIVED_EOF:
                metadataMixer.add(getSource());
                break;
        }
    }
}
