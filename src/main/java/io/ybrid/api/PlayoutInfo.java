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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Duration;

public interface PlayoutInfo extends Serializable {
    /**
     * Returns the information on the current swap state.
     * @return Returns the current SwapInfo.
     * @deprecated Access via {@link CapabilitySet}-API.
     */
    @NotNull
    @Deprecated
    SwapInfo getSwapInfo();

    /**
     * Returns the time to the next Item.
     * This is measured with the current system clock so that every call to this method will give an updated result.
     * may return a negative number if the start of the next Item is in the past.
     * @return Returns the time to the next item or null if unknown.
     */
    @Nullable
    Duration getTimeToNextItem();

    /**
     * Returns the temporal validity of the current Item.
     * This is based on {@link #getTimeToNextItem()}.
     *
     * @return The temporal validity.
     * @see #getTimeToNextItem()
     */
    default @NotNull TemporalValidity getTemporalValidity() {
        final @Nullable Duration timeToNextItem = getTimeToNextItem();
        if (timeToNextItem == null) {
            return TemporalValidity.INDEFINITELY_VALID;
        } else {
            return TemporalValidity.makeFromNow(timeToNextItem);
        }
    }

    /**
     * This returns time the the server side clock for the current client is behind
     * live playback. This is zero for live playback, positive for delayed playback,
     * and can be negative if the current client is ahead of live playback.
     * @return Returns the time playback is behind live or null if unknown.
     */
    @Nullable
    default Duration getBehindLive() {
        return null;
    }

    /**
     * This method adjusts in objects understanding of passed time by adjusting the passed time
     * with a constant offset.
     * @param adjustment The offset to use.
     * @return The new instance of PlayoutInfo with the adjustment made.
     */
    @NotNull
    @Contract("_ -> new")
    default PlayoutInfo adjustTimeToNextItem(Duration adjustment) {
        @NotNull final PlayoutInfo aliasThis = this;
        @NotNull final Duration aliasAdjustment = adjustment;

        return new PlayoutInfo() {
            private static final long serialVersionUID = -1720896005620054208L;

            @NotNull private final PlayoutInfo parent = aliasThis;
            @NotNull private final Duration adjustment = aliasAdjustment;

            @Override
            @NotNull
            @Deprecated
            public SwapInfo getSwapInfo() {
                return parent.getSwapInfo();
            }

            @Override
            @Nullable
            public Duration getTimeToNextItem() {
                Duration timeToNextItem = parent.getTimeToNextItem();

                if (timeToNextItem == null)
                    return null;

                return parent.getTimeToNextItem().plus(adjustment);
            }

            @Override
            @Nullable
            public Duration getBehindLive() {
                return parent.getBehindLive();
            }

            @Override
            @Contract("_ -> new")
            @NotNull
            public PlayoutInfo adjustTimeToNextItem(Duration adjustment) {
                return parent.adjustTimeToNextItem(this.adjustment.plus(adjustment));
            }
        };
    }
}
