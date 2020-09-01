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

import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.*;
import io.ybrid.api.metadata.source.SourceType;
import io.ybrid.api.metadata.source.SourceMetadata;
import io.ybrid.api.metadata.source.SourceTrackMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    private boolean hasChanged = false;

    MetadataMixer() {
        add(new InvalidMetadata(), SourceType.SESSION, TemporalValidity.INDEFINITELY_VALID);
    }

    public void add(@NotNull Item item, @NotNull SourceType source, @NotNull Position position, @NotNull TemporalValidity temporalValidity) {
        items.put(position, new ItemInfo(item, temporalValidity));
        changed.add(SubInfo.METADATA);
    }

    public void add(@NotNull Service service, @NotNull SourceType source, @NotNull Position position, @NotNull TemporalValidity temporalValidity) {
        services.put(position, service);
        changed.add(SubInfo.METADATA);
    }

    public void add(@NotNull Metadata metadata, @NotNull SourceType source, @NotNull TemporalValidity temporalValidity) {
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
        if (nextService != null)
            services.put(Position.CURRENT, nextService);

        removeNext();
        changed.add(SubInfo.METADATA);
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        return changed.contains(what);
    }


}
