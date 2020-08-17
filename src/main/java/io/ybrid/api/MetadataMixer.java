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
import io.ybrid.api.metadata.Item;
import io.ybrid.api.metadata.Metadata;
import io.ybrid.api.metadata.SimpleMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class MetadataMixer {

    private static class ItemInfo {
        private final @NotNull Item item;
        private final @Nullable Duration timeToNextItem;
        private final @NotNull Instant requestTime;

        public ItemInfo(@NotNull Item item, @Nullable Duration timeToNextItem, @NotNull Instant requestTime) {
            this.item = item;
            this.timeToNextItem = timeToNextItem;
            this.requestTime = requestTime;
        }

        public @NotNull Item getItem() {
            return item;
        }

        public @Nullable Duration getTimeToNextItem() {
            return timeToNextItem;
        }

        public @NotNull Instant getRequestTime() {
            return requestTime;
        }
    }

    public enum Source {
        SESSION,
        TRANSPORT,
        FORMAT;
    }

    public enum Position {
        PREVIOUS,
        CURRENT,
        NEXT;
    }

    private final @NotNull Map<Position, ItemInfo> items = new HashMap<>();
    private final @NotNull Map<Position, Service> services = new HashMap<>();

    MetadataMixer() {
    }

    public void add(@NotNull Item item, @NotNull Source source, @NotNull Position position, @Nullable Duration timeToNextItem, @NotNull Instant requestTime) {
        items.put(position, new ItemInfo(item, timeToNextItem, requestTime));
    }

    public void add(@NotNull Service service, @NotNull Source source, @NotNull Position position, @Nullable Duration timeToNextItem, @NotNull Instant requestTime) {
        services.put(position, service);
    }

    public void add(@NotNull Metadata metadata, @NotNull Source source, @Nullable Duration timeToNextItem, @NotNull Instant requestTime) {
        add(metadata.getCurrentItem(), source, Position.CURRENT, timeToNextItem, requestTime);
        if (metadata.getNextItem() != null)
            add(metadata.getNextItem(), source, Position.NEXT, timeToNextItem, requestTime);
        add(metadata.getService(), source, Position.CURRENT, timeToNextItem, requestTime);
    }

    public @NotNull Metadata getMetadata() {
        final @NotNull ItemInfo current = items.get(Position.CURRENT);
        final @Nullable ItemInfo next = items.get(Position.CURRENT);
        return new SimpleMetadata(current.getItem(), next != null ? next.getItem() : null, services.get(Position.CURRENT), current.getTimeToNextItem(), current.getRequestTime());
    }

    public void removeNext() {
        items.remove(Position.NEXT);
        services.remove(Position.NEXT);
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
    }
}
