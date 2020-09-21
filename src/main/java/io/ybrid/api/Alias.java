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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * An Alias represents a entry point on a {@link Server}.
 * The alias can be used to open a {@link Session} and a stream.
 */
public final class Alias implements ApiUser {
    private final @NotNull WorkaroundMap workarounds = new WorkaroundMap();
    private final @NotNull URL url;
    private final @NotNull Server server;
    private @Nullable ApiVersion apiVersion = null;
    private @Nullable Map<String, Double> acceptedLanguages = null;

    /**
     * Create a new Alias using the given {@link Server}.
     *
     * @param url The {@link URL} of the Alias.
     * @param server The {@link Server} to use for contacting the Alias.
     */
    public Alias(@NotNull URL url, @Nullable Server server) throws MalformedURLException {
        this.url = url;
        if (server != null) {
            this.server = server;
        } else {
            this.server = new Server(url);
        }
    }

    /**
     * Create a new Alias using the given {@link Server}.
     *
     * @param url The {@link URL} of the Alias.
     */
    public Alias(@NotNull URL url) throws MalformedURLException {
        this(url, null);
    }

    /**
     * Get the {@link URL} of the Alias.
     * @return Returns the {@link URL} of the Alias.
     */
    public @NotNull URL getUrl() {
        return url;
    }

    /**
     * Returns the {@link Server} used by this Alias.
     * If no {@link Server} has been passed to the Constructor it is automatically created.
     *
     * @return Returns the {@link Server} object of this Alias.
     */
    public @NotNull Server getServer() {
        return server;
    }

    /**
     * Create a {@link Session} using this Alias.
     *
     * This may will connect the used {@link Server} if needed.
     *
     * @return Returns a newly created and unconnected {@link Session}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public @NotNull Session createSession() throws MalformedURLException {
        return getServer().createSession(this);
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
        Utils.assertValidAcceptList(acceptedLanguages);
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

        Utils.assertValidAcceptList(newList);
        this.acceptedLanguages = newList;
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
