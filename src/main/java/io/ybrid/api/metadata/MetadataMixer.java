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

package io.ybrid.api.metadata;

import io.ybrid.api.Identifier;
import io.ybrid.api.Session;
import io.ybrid.api.TemporalValidity;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.hasIdentifier;
import io.ybrid.api.metadata.source.Source;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public final class MetadataMixer implements Consumer<@NotNull Sync> {
    private final @NotNull Set<Source> sources = new HashSet<>();
    private final @NotNull Session session;
    private final @NotNull Map<@NotNull Identifier, @NotNull Service> services = new HashMap<>();
    private final @NotNull Map<@NotNull Identifier, @NotNull Service> serviceUpdates = new HashMap<>();
    private Service defaultService = null;

    public MetadataMixer(@NotNull Session session) {
        this.session = session;
        add(session.getSource());
    }

    public synchronized void accept(@NotNull Bouquet bouquet) {
        final @NotNull Map<@NotNull Identifier, @NotNull Service> fromBouquet = new HashMap<>();

        for (final @NotNull Service service : bouquet.getServices()) {
            fromBouquet.put(service.getIdentifier(), service);
        }

        for (Iterator<@NotNull Service> iterator = serviceUpdates.values().iterator(); iterator.hasNext(); ) {
            final @NotNull Identifier identifier = iterator.next().getIdentifier();
            if (fromBouquet.get(identifier) != services.get(identifier)) {
                iterator.remove();
            }
        }

        services.clear();
        services.putAll(fromBouquet);

        defaultService = serviceUpdates.get(bouquet.getDefaultService().getIdentifier());

        if (defaultService == null)
            defaultService = bouquet.getDefaultService();
    }

    @Override
    public synchronized void accept(@NotNull Sync sync) {
        if (!sources.contains(sync.getSource()))
            return;

        if (sync.getCurrentService() != null)
            serviceUpdates.put(sync.getCurrentService().getIdentifier(), sync.getCurrentService());
    }

    public synchronized void add(@NotNull Source source) {
        sources.add(source);
    }

    public synchronized void remove(@NotNull Source source) {
        sources.remove(source);
    }

    @Contract("null -> null; !null -> !null")
    private @Nullable Item trackToItem(@Nullable TrackMetadata track) {
        if (track == null)
            return null;

        if (track instanceof Item)
            return (Item) track;

        if (track instanceof hasIdentifier)
            return new SimpleItem(((hasIdentifier) track).getIdentifier(), track);

        return new SimpleItem(new Identifier(), track);
    }

    public @NotNull Metadata resolveMetadata(@NotNull Sync sync) {
        final @NotNull Sync upgraded = sync.getUpgraded();

        return new SimpleMetadata(Objects.requireNonNull(trackToItem(upgraded.getCurrentTrack())),
                trackToItem(upgraded.getNextTrack()),
                Objects.requireNonNull(upgraded.getCurrentService()),
                upgraded.getTemporalValidity() != null ? upgraded.getTemporalValidity() : TemporalValidity.INDEFINITELY_VALID);
    }

    public @NotNull Service resolveService(@NotNull Sync sync) {
        return Objects.requireNonNull(sync.getUpgraded().getCurrentService());
    }

    public @NotNull Bouquet getBouquet() {
        final @NotNull Set<Service> newServices = new HashSet<>();

        for (final @NotNull Service service : services.values()) {
            //noinspection Java8MapApi
            if (serviceUpdates.containsKey(service.getIdentifier())) {
                newServices.add(serviceUpdates.get(service.getIdentifier()));
            } else {
                newServices.add(service);
            }
        }

        return new Bouquet(defaultService, newServices);
    }
}
