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
import io.ybrid.api.TemporalValidity;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.hasIdentifier;
import io.ybrid.api.metadata.source.Source;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Sync implements hasIdentifier {
    private final @NotNull Identifier identifier = new Identifier();
    private final @NotNull Source source;
    private final @Nullable Sync parent;
    private final @Nullable Identifier predecessor;
    private final @Nullable Object sessionSpecific;
    private final @Nullable Service currentService;
    private final @Nullable TrackMetadata currentTrack;
    private final @Nullable TrackMetadata nextTrack;
    private final @Nullable TemporalValidity temporalValidity;

    public final static class Builder {
        private final @NotNull Source source;
        private final @Nullable Sync parent;
        private final @Nullable Sync predecessor;
        private @Nullable Object sessionSpecific = null;
        private @Nullable Service currentService = null;
        private @Nullable TrackMetadata currentTrack = null;
        private @Nullable TrackMetadata nextTrack = null;
        private @Nullable TemporalValidity temporalValidity = null;

        private Builder(@NotNull Source source, @Nullable Sync parent, @Nullable Sync predecessor) {
            this.source = source;
            this.parent = parent;
            this.predecessor = predecessor;
        }

        public Builder(@NotNull Source source) {
            this(source, null, null);
        }

        public Builder(@NotNull Source source, @Nullable Sync parent) {
            this(source, parent, null);
        }

        public Builder(@NotNull Sync predecessor) {
            this((Sync)null, predecessor);
        }

        public Builder(@Nullable Sync parent, @NotNull Sync predecessor) {
            this(predecessor.getSource(), parent, predecessor);
        }

        public void setSessionSpecific(@Nullable Object sessionSpecific) {
            this.sessionSpecific = sessionSpecific;
        }

        public void setCurrentService(@Nullable Service currentService) {
            this.currentService = currentService;
        }

        public void setCurrentTrack(@Nullable TrackMetadata currentTrack) {
            this.currentTrack = currentTrack;
        }

        public void setNextTrack(@Nullable TrackMetadata nextTrack) {
            this.nextTrack = nextTrack;
        }

        public void setTemporalValidity(@Nullable TemporalValidity temporalValidity) {
            this.temporalValidity = temporalValidity;
        }

        public void autoFill() {
            if (predecessor != null) {
                loadDefaults(predecessor);
            }
        }

        public void loadDefaults(@NotNull Sync defaults) {
            if (sessionSpecific == null)
                setSessionSpecific(defaults.getSessionSpecific());
            if (currentService == null)
                setCurrentService(defaults.getCurrentService());
            if (currentTrack == null)
                setCurrentTrack(defaults.getCurrentTrack());
            if (nextTrack == null)
                setNextTrack(defaults.getNextTrack());
            if (temporalValidity == null)
                setTemporalValidity(defaults.getTemporalValidity());
        }

        @Contract(value = " -> new", pure = true)
        public @NotNull Sync build() {
            return new Sync(source, parent, predecessor != null ? predecessor.getIdentifier() : null, sessionSpecific, currentService, currentTrack, nextTrack, temporalValidity);
        }

        @Contract(value = "_ -> new", pure = true)
        public static @NotNull Sync buildEmpty(@NotNull Source source) {
            return new Builder(source).build();
        }
    }

    private Sync(@NotNull Source source, @Nullable Sync parent, @Nullable Identifier predecessor, @Nullable Object sessionSpecific, @Nullable Service currentService, @Nullable TrackMetadata currentTrack, @Nullable TrackMetadata nextTrack, @Nullable TemporalValidity temporalValidity) {
        this.source = source;
        this.parent = parent;
        this.predecessor = predecessor;
        this.sessionSpecific = sessionSpecific;
        this.currentService = currentService;
        this.currentTrack = currentTrack;
        this.nextTrack = nextTrack;
        this.temporalValidity = temporalValidity;
    }

    @Contract(pure = true)
    @Override
    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    @Contract(pure = true)
    public @NotNull Source getSource() {
        return source;
    }

    @Contract(pure = true)
    public @Nullable Sync getParent() {
        return parent;
    }

    @Contract(pure = true)
    public @Nullable Identifier getPredecessorIdentifier() {
        return predecessor;
    }

    @Contract(pure = true)
    public @Nullable Object getSessionSpecific() {
        return sessionSpecific;
    }

    @Contract(pure = true)
    public @Nullable Service getCurrentService() {
        return currentService;
    }

    @Contract(pure = true)
    public @Nullable TrackMetadata getCurrentTrack() {
        return currentTrack;
    }

    @Contract(pure = true)
    public @Nullable TrackMetadata getNextTrack() {
        return nextTrack;
    }

    @Contract(pure = true)
    public @Nullable TemporalValidity getTemporalValidity() {
        return temporalValidity;
    }

    public boolean isSuccessorOf(@NotNull Identifier identifier) {
        @Nullable Sync cur = this;

        while (cur != null) {
            if (cur.getIdentifier().equals(identifier))
                return true;
            if (cur.getPredecessorIdentifier() != null && cur.getPredecessorIdentifier().equals(identifier))
                return true;

            cur = cur.getParent();
        }

        return false;
    }

    public boolean isSuccessorOf(@NotNull Sync sync) {
        return isSuccessorOf(sync.getIdentifier());
    }


    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, 248281328693201417L);
    }
}
