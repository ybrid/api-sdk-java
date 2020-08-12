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
import io.ybrid.api.driver.FactorySelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * An Alias represents a entry point on a {@link Server}.
 * The alias can be used to open a {@link Session} and a stream.
 */
public class Alias implements ApiUser {
    private final @NotNull WorkaroundMap workarounds = new WorkaroundMap();
    private final @NotNull URL url;
    private final @NotNull Server server;
    private @Nullable ApiVersion apiVersion = null;

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
     * Get the current {@link Bouquet} from the default {@link Server}.
     * @return Returns the current {@link Bouquet}.
     * @throws MalformedURLException Thrown if any error is found in the Alias' URL.
     */
    public Bouquet getBouquet() throws IOException {
        return FactorySelector.getFactory(getServer(), this).getBouquet(getServer(), this);
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
