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

import io.ybrid.api.MetadataMixer;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This is the base class for all transport descriptions.
 */
public abstract class TransportDescription {
    protected @NotNull final Source source;
    protected @NotNull final Service initialService;
    protected @NotNull final MetadataMixer metadataMixer;
    protected @Nullable Map<String, Double> acceptedMediaFormats;
    protected @Nullable Map<String, Double> acceptedLanguages;
    protected @NotNull final Transaction transaction;

    /**
     * Main constructor.
     *
     * @param source The source that shall represent the new transport.
     * @param initialService The initial service to connect to.
     * @param metadataMixer The Metadata mixer the new transport should send updates to.
     * @param acceptedMediaFormats List of accepted media formats or {@code null}.
     * @param acceptedLanguages List of accepted languages or {@code null}.
     * @param transaction The {@link Transaction} causing the creation of this transport description.
     * @see #getAcceptedMediaFormats()
     * @see #getAcceptedLanguages()
     */
    protected TransportDescription(@NotNull Source source, @NotNull Service initialService, @NotNull MetadataMixer metadataMixer, @Nullable Map<String, Double> acceptedMediaFormats, @Nullable Map<String, Double> acceptedLanguages, @NotNull Transaction transaction) {
        this.source = source;
        this.initialService = initialService;
        this.metadataMixer = metadataMixer;
        this.acceptedMediaFormats = acceptedMediaFormats;
        this.acceptedLanguages = acceptedLanguages;
        this.transaction = transaction;
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

    /**
     * Get list of languages accepted for this transport.
     * <P>
     * For HTTP based protocols:
     * If this returns {@code null} no {@code Accept-Language:}-header should be generated.
     *
     * @return List of languages accepted or {@code null}.
     */
    public @Nullable Map<String, Double> getAcceptedLanguages() {
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
}
