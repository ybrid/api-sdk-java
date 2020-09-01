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

package io.ybrid.api;

import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.*;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.metadata.source.SourceMetadata;
import io.ybrid.api.metadata.source.SourceTrackMetadata;
import io.ybrid.api.metadata.source.SourceType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MetadataMixer implements KnowsSubInfoState {

    private static class ItemInfo {
        private final @NotNull Item item;
        private final @NotNull TemporalValidity temporalValidity;

        public ItemInfo(@NotNull Item item, @NotNull TemporalValidity temporalValidity) {
            this.item = item;
            this.temporalValidity = temporalValidity;
        }

        public @NotNull Item getItem() {
            return item;
        }

        public @NotNull TemporalValidity getTemporalValidity() {
            return temporalValidity;
        }
    }

    public enum Position {
        PREVIOUS,
        CURRENT,
        NEXT;
    }

    private final @NotNull EnumSet<SubInfo> changed = EnumSet.noneOf(SubInfo.class);
    private final @NotNull Map<Position, ItemInfo> items = new HashMap<>();
    private final @NotNull Map<Position, Service> services = new HashMap<>();
    private final @NotNull Map<@NotNull String, Service> bouquetContent = new HashMap<>();
    private Service bouquetDefaultService;

    MetadataMixer() {
        final @NotNull Metadata metadata = new InvalidMetadata();
        final @NotNull Source source = new Source(SourceType.SESSION);
        final @NotNull List<Service> services = new ArrayList<>(1);
        services.add(metadata.getService());
        add(new Bouquet(metadata.getService(), services), source);
        add(metadata, source, TemporalValidity.INDEFINITELY_VALID);
    }

    private void updatedCurrentService() {
        final @NotNull Service currentService = services.get(Position.CURRENT);

        if (!bouquetContent.containsKey(currentService.getIdentifier())) {
            try {
                throw new IllegalArgumentException("Service not part of bouquet: " + currentService + ", " + bouquetContent.values());
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }

        // No-op if we are on the same object still.
        if (currentService == bouquetContent.get(currentService.getIdentifier()))
            return;

        bouquetContent.put(currentService.getIdentifier(), currentService);

        if (bouquetDefaultService.getIdentifier().equals(currentService.getIdentifier()))
            bouquetDefaultService = currentService;

        changed.add(SubInfo.BOUQUET);
    }

    public synchronized void add(@NotNull Bouquet bouquet, @NotNull Source source) {
        bouquetContent.clear();
        for (final @NotNull Service service : bouquet.getServices()) {
            bouquetContent.put(service.getIdentifier(), service);
        }

        bouquetDefaultService = bouquet.getDefaultService();
        changed.add(SubInfo.BOUQUET);
    }

    public void add(@NotNull Item item, @NotNull Source source, @NotNull Position position, @NotNull TemporalValidity temporalValidity) {
        items.put(position, new ItemInfo(item, temporalValidity));
        changed.add(SubInfo.METADATA);
    }

    public void add(@NotNull Service service, @NotNull Source source, @NotNull Position position, @NotNull TemporalValidity temporalValidity) {
        services.put(position, service);
        changed.add(SubInfo.METADATA);
        if (position.equals(Position.CURRENT))
            updatedCurrentService();
    }

    public void add(@NotNull Metadata metadata, @NotNull Source source, @NotNull TemporalValidity temporalValidity) {
        add(metadata.getCurrentItem(), source, Position.CURRENT, temporalValidity);
        if (metadata.getNextItem() != null)
            add(metadata.getNextItem(), source, Position.NEXT, temporalValidity);
        add(metadata.getService(), source, Position.CURRENT, temporalValidity);
    }

    public void add(@NotNull SourceMetadata metadata, @NotNull Position position, @NotNull TemporalValidity temporalValidity) {
        if (metadata instanceof SourceTrackMetadata) {
            final @NotNull SourceTrackMetadata track = (SourceTrackMetadata) metadata;
            final @NotNull Item item = new SimpleItem(UUID.randomUUID().toString(), track);
            add(item, metadata.getSource(), position, temporalValidity);
        }
    }

    public synchronized @NotNull Bouquet getBouquet() {
        changed.remove(SubInfo.BOUQUET);
        return new Bouquet(bouquetDefaultService, bouquetContent.values());
    }

    public @NotNull Metadata getMetadata() {
        final @NotNull ItemInfo current = items.get(Position.CURRENT);
        final @Nullable ItemInfo next = items.get(Position.CURRENT);
        changed.remove(SubInfo.METADATA);
        return new SimpleMetadata(current.getItem(), next != null ? next.getItem() : null, services.get(Position.CURRENT), current.getTemporalValidity());
    }

    public void removeNext() {
        items.remove(Position.NEXT);
        services.remove(Position.NEXT);
        changed.add(SubInfo.METADATA);
    }

    public void forwardToNextItem() {
        final @NotNull ItemInfo nextItem = items.get(Position.NEXT);
        final @NotNull Service nextService = services.get(Position.NEXT);

        items.put(Position.PREVIOUS, items.get(Position.CURRENT));
        if (nextItem != null)
            items.put(Position.CURRENT, nextItem);

        services.put(Position.PREVIOUS, services.get(Position.CURRENT));
        if (nextService != null) {
            services.put(Position.CURRENT, nextService);
            updatedCurrentService();
        }

        removeNext();
        changed.add(SubInfo.METADATA);
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        return changed.contains(what);
    }


}
