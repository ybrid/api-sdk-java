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

package io.ybrid.api.bouquet;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents a Bouquet. A Bouquet is a collection of {@link Service Services} provided
 * to the user by the server.
 */
public final class Bouquet {
    private final @NotNull Service defaultService;
    private final @NotNull Set<Service> services;

    /**
     * Creates a new Bouquet object.
     * @param defaultService The default {@link Service} of the Bouquet. Must be part of {@code services}.
     * @param services The collection of all {@link Service Services} in this Bouquet.
     */
    public Bouquet(@NotNull Service defaultService, @NotNull Collection<Service> services) {
        if (!services.contains(defaultService))
            throw new IllegalArgumentException("Default Service not part of Services");

        this.defaultService = defaultService;
        this.services = new HashSet<>(services);
    }

    /**
     * Creates a new Bouquet object that contains only a single {@link Service}.
     * The given service is also used as default service.
     *
     * @param service The service to include.
     */
    public Bouquet(@NotNull Service service) {
        final @NotNull Set<Service> services = new HashSet<>();
        services.add(service);
        this.services = services;
        this.defaultService = service;
    }

    /**
     * Gets the default {@link Service} from this Bouquet.
     * @return Returns the default {@link Service}.
     */
    public @NotNull Service getDefaultService() {
        return defaultService;
    }

    /**
     * Returns the collection of {@link Service Services} known in this Bouquet.
     * @return Returns the collection of {@link Service Services}.
     */
    public @NotNull Collection<Service> getServices() {
        return services;
    }
}
