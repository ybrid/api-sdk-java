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
import io.ybrid.api.message.MessageBody;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Map;

/**
 * This class implements {@link URI} based {@link TransportDescription}s.
 * In addition to the pure URI this class can also store a {@link MessageBody} to be send alongside
 * the request to the given URI.
 */
public class URITransportDescription extends TransportDescription {
    private final @NotNull URI uri;
    private final @Nullable MessageBody requestBody;

    /**
     * Main constructor.
     * @param source The source that shall represent the new transport.
     * @param initialService The initial service to connect to.
     * @param metadataMixer The Metadata mixer the new transport should send updates to.
     * @param acceptedMediaFormats List of accepted media formats or {@code null}.
     * @param acceptedLanguages List of accepted languages or {@code null}.
     * @param transaction The {@link Transaction} causing the creation of this transport description.
     * @param uri The URI to connect to.
     * @param requestBody The {@link MessageBody} to send alongside the request or {@code null}.
     */
    public URITransportDescription(@NotNull Source source, @NotNull Service initialService, @NotNull MetadataMixer metadataMixer, @Nullable Map<String, Double> acceptedMediaFormats, @Nullable Map<String, Double> acceptedLanguages, @NotNull Transaction transaction, @NotNull URI uri, @Nullable MessageBody requestBody) {
        super(source, initialService, metadataMixer, acceptedMediaFormats, acceptedLanguages, transaction);
        this.uri = uri;
        this.requestBody = requestBody;
    }

    /**
     * Creates a new URITransportDescription based using only q {@link URI}.
     * @param source The source that shall represent the new transport.
     * @param initialService The initial service to connect to.
     * @param transaction The {@link Transaction} causing the creation of this transport description.
     * @param uri The URI to connect to.
     * @see #URITransportDescription(Source, Service, MetadataMixer, Map, Map, Transaction, URI, MessageBody)
     */
    public URITransportDescription(@NotNull Source source, @NotNull Service initialService, @NotNull MetadataMixer metadataMixer, @NotNull Transaction transaction, @NotNull URI uri) {
        this(source, initialService, metadataMixer, null, null, transaction, uri, null);
    }

    /**
     * Gets the {@link URI} associated with this description.
     * @return The URI of this description.
     */
    public @NotNull URI getURI() {
        return uri;
    }

    /**
     * Gets the request body associated with this description.
     * @return The request body of this description.
     */
    public @Nullable MessageBody getRequestBody() {
        return requestBody;
    }
}
