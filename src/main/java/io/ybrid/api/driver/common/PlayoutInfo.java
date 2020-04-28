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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import io.ybrid.api.SwapInfo;

import java.time.Duration;

public class PlayoutInfo implements io.ybrid.api.PlayoutInfo {
    @NotNull
    protected SwapInfo swapInfo;
    @Nullable
    protected Duration timeToNextItem;
    private final long buildTimestamp = System.currentTimeMillis();

    public PlayoutInfo(@NotNull SwapInfo swapInfo, @Nullable Duration timeToNextItem) {
        this.swapInfo = swapInfo;
        this.timeToNextItem = timeToNextItem;
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
        return timeToNextItem.plusMillis(buildTimestamp).minusMillis(System.currentTimeMillis());
    }
}
