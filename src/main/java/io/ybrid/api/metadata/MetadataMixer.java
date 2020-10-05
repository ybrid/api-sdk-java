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

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.session.Command;
import io.ybrid.api.transaction.SimpleTransaction;
import io.ybrid.api.transaction.TransactionWithResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

public class MetadataMixer implements Consumer<@NotNull Sync> {
    private static class RefreshTransaction extends SimpleTransaction implements TransactionWithResult<@NotNull Sync> {
        private final @NotNull Sync request;
        private final @NotNull Session session;
        private final @NotNull Consumer<@NotNull Sync> consumer;
        private @Nullable Sync result = null;

        private RefreshTransaction(@NotNull Sync request, @NotNull Session session, @NotNull Consumer<@NotNull Sync> consumer) {
            this.request = request;
            this.session = session;
            this.consumer = consumer;
        }

        @Override
        protected void execute() throws Exception {
            final @NotNull Sync.Builder builder = new Sync.Builder(session.getSource(), request);
            final @NotNull Metadata metadata;

            session.createTransaction(Command.REFRESH.makeRequest(EnumSet.of(SubInfo.METADATA, SubInfo.PLAYOUT))).run();

            metadata = session.getMetadata();
            builder.setCurrentTrack(metadata.getCurrentItem());
            builder.setNextTrack(metadata.getNextItem());
            builder.setCurrentService(metadata.getService());
            builder.setTemporalValidity(session.getPlayoutInfo().getTemporalValidity());

            result = builder.build();
            consumer.accept(result);
        }

        @Override
        public @NotNull Sync getResult() {
            if (result == null)
                throw new IllegalStateException("Transaction must be run before the result can be fetched");
            return result;
        }
    }

    private final @NotNull Set<Source> sources = new HashSet<>();
    private final @NotNull Set<Sync> syncs = new HashSet<>();
    private final @NotNull Session session;

    public MetadataMixer(@NotNull Session session) {
        this.session = session;
        add(session.getSource());
    }

    private @NotNull Sync upgrade(@NotNull Sync sync) {
        final @NotNull Sync.Builder builder = new Sync.Builder(sync.getSource(), sync);
        @Nullable Sync cur = sync;

        accept(sync);

        for (final @NotNull Sync candidate : syncs) {
            if (candidate.isSuccessorOf(cur))
                cur = candidate;
        }

        while (cur != null) {
            builder.loadDefaults(cur);
            cur = cur.getParent();
        }

        return builder.build();
    }

    @Override
    public synchronized void accept(@NotNull Sync sync) {
        if (syncs.contains(sync))
            return;

        if (!sources.contains(sync.getSource()))
            return;

        for (Iterator<Sync> iterator = syncs.iterator(); iterator.hasNext(); ) {
            final @NotNull Sync cur = iterator.next();
            if (cur.isSuccessorOf(sync))
                return;
            if (sync.isSuccessorOf(cur))
                iterator.remove();
        }

        syncs.add(sync);
    }

    public synchronized void add(@NotNull Source source) {
        sources.add(source);
    }

    public synchronized void remove(@NotNull Source source) {
        sources.remove(source);
        //noinspection Java8CollectionRemoveIf
        for (Iterator<Sync> iterator = syncs.iterator(); iterator.hasNext(); ) {
            final @NotNull Sync sync = iterator.next();
            if (sync.getSource().equals(source))
                iterator.remove();
        }
    }

    public @NotNull Bouquet resolveBouquet(@NotNull Sync sync) {
        final @NotNull Service currentService = resolveService(sync);
        final @NotNull Bouquet fromSession = session.getBouquet();
        final @NotNull Set<Service> services = new HashSet<>(fromSession.getServices());

        // Force refresh of the service within the set.
        // (just calling add() would not overwrite the Service if one of it's incarnations is already present in the set.)
        services.remove(currentService);
        services.add(currentService);

        // Force to use our copy of the current service if it is also the default service
        if (fromSession.getDefaultService().equals(currentService)) {
            return new Bouquet(currentService, services);
        } else {
            return new Bouquet(fromSession.getDefaultService(), services);
        }
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
        final @NotNull Sync upgraded = upgrade(sync);

        return new SimpleMetadata(Objects.requireNonNull(trackToItem(upgraded.getCurrentTrack())),
                trackToItem(upgraded.getNextTrack()),
                Objects.requireNonNull(upgraded.getCurrentService()),
                upgraded.getTemporalValidity() != null ? upgraded.getTemporalValidity() : TemporalValidity.INDEFINITELY_VALID);
    }

    public @NotNull Service resolveService(@NotNull Sync sync) {
        return Objects.requireNonNull(upgrade(sync).getCurrentService());
    }

    public @NotNull TransactionWithResult<@NotNull Sync> refreshSession(@NotNull Sync sync) {
        return new RefreshTransaction(sync, session, this);
    }
}
