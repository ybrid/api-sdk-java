/*
 * Copyright 2019 nacamar GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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