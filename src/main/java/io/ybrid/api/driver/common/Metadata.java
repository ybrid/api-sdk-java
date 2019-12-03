/*
 * Copyright (c) 2019 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

abstract public class Metadata implements io.ybrid.api.Metadata {
    protected int currentBitRate;
    protected Item currentItem;
    protected Item nextItem;
    protected Service service;
    protected SwapInfo swapInfo;
    protected long timeToNextItem;
    protected long requestTime;

    @Override
    public Item getCurrentItem() {
        return currentItem;
    }

    @Override
    public Item getNextItem() {
        return nextItem;
    }

    @Override
    public int getCurrentBitRate() {
        return currentBitRate;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public SwapInfo getSwapInfo() {
        return swapInfo;
    }

    @Override
    public long getTimeToNextItem() {
        return timeToNextItem - (System.currentTimeMillis() - requestTime);
    }

    @Override
    public boolean isValid() {
        return getTimeToNextItem() >= 0;
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "currentBitRate=" + currentBitRate +
                ", currentItem=" + currentItem +
                ", nextItem=" + nextItem +
                ", service=" + service +
                ", swapInfo=" + swapInfo +
                ", timeToNextItem=" + timeToNextItem +
                ", requestTime=" + requestTime +
                '}';
    }
}
