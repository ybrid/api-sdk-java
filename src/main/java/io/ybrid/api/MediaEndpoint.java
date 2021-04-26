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

import io.ybrid.api.util.QualityMap.LanguageMap;
import io.ybrid.api.util.hasAcceptedLanguages;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Locale;

/**
 * An MediaEndpoint represents a entry point on a {@link Server}.
 * The MediaEndpoint can be used to open a {@link Session} and a stream.
 */
public final class MediaEndpoint implements ApiUser, hasAcceptedLanguages {
    private final @NotNull WorkaroundMap workarounds = new WorkaroundMap();
    private final @NotNull URI uri;
    private @Nullable ApiVersion apiVersion = null;
    private @Nullable LanguageMap acceptedLanguages = null;

    /**
     * Create a new MediaEndpoint using the given {@link URI}.
     *
     * @param uri The {@link URI} of the MediaEndpoint.
     */
    public MediaEndpoint(@NotNull URI uri) throws MalformedURLException {
        this.uri = uri;
    }

    /**
     * Get the {@link URI} of the MediaEndpoint.
     * @return Returns the {@link URI} of the MediaEndpoint.
     */
    public @NotNull URI getURI() {
        return uri;
    }

    /**
     * Returns the {@link Server} used by this MediaEndpoint.
     * If no {@link Server} has been passed to the Constructor it is automatically created.
     *
     * @return Returns the {@link Server} object of this MediaEndpoint.
     * @deprecated Deprecated as {@link Server} was deprecated.
     */
    @ApiStatus.ScheduledForRemoval
    @Deprecated
    public @NotNull Server getServer() {
        try {
            return new Server(uri.toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a {@link Session} using this MediaEndpoint.
     *
     * This may will connect the used {@link Server} if needed.
     *
     * @return Returns a newly created and unconnected {@link Session}.
     * @throws MalformedURLException Thrown if any error is found in the MediaEndpoint' URL.
     */
    public @NotNull Session createSession() throws MalformedURLException {
        return new Session(this);
    }

    @Override
    public @Nullable LanguageMap getAcceptedLanguagesMap() {
        return acceptedLanguages;
    }

    /**
     * Sets the list of languages requested by the user and their corresponding weights.
     * @param list The list of languages or null.
     */
    public void setAcceptedLanguages(@Nullable List<Locale.LanguageRange> list) {
        if (list == null) {
            this.acceptedLanguages = null;
            return;
        }
        this.acceptedLanguages = new LanguageMap(list);
    }

    /**
     * Sets the list of languages requested by the user and their corresponding weights.
     * @param list The list of languages or null.
     */
    public void setAcceptedLanguages(@Nullable LanguageMap list) {
        this.acceptedLanguages = list;
    }

    @Override
    public void forceApiVersion(@Nullable ApiVersion version) throws IllegalArgumentException, IllegalStateException {
        this.apiVersion = version;
    }

    @Override
    public @Nullable ApiVersion getForcedApiVersion() {
        return apiVersion;
    }

    @Override
    public @NotNull WorkaroundMap getWorkarounds() {
        return workarounds;
    }
}
