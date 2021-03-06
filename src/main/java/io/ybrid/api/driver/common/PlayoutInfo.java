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

package io.ybrid.api.driver.common;

import io.ybrid.api.SwapInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public class PlayoutInfo implements io.ybrid.api.PlayoutInfo {
    private static final long serialVersionUID = -9060307971835366582L;

    @NotNull
    protected final SwapInfo swapInfo;
    @Nullable
    protected final Duration timeToNextItem;
    @Nullable
    protected final Duration behindLive;
    private final Instant buildTimestamp = Instant.now();

    public PlayoutInfo(@NotNull SwapInfo swapInfo, @Nullable Duration timeToNextItem, @Nullable Duration behindLive) {
        this.swapInfo = swapInfo;
        this.timeToNextItem = timeToNextItem;
        this.behindLive = behindLive;
    }

    @NotNull
    @Override
    public SwapInfo getSwapInfo() {
        return swapInfo;
    }

    @Nullable
    @Override
    public Duration getTimeToNextItem() {
        if (timeToNextItem == null)
            return null;
        return timeToNextItem.minus(Duration.between(buildTimestamp, Instant.now()));
    }

    @Nullable
    @Override
    public Duration getBehindLive() {
        return behindLive;
    }

    @Override
    public String toString() {
        return "PlayoutInfo{" +
                "swapInfo=" + swapInfo +
                ", getTimeToNextItem()=" + getTimeToNextItem() +
                ", getBehindLive()=" + getBehindLive() +
                ", buildTimestamp=" + buildTimestamp +
                '}';
    }
}
