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

import io.ybrid.api.Service;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.time.Instant;

abstract public class Metadata implements io.ybrid.api.Metadata {
    protected Item currentItem;
    protected Item nextItem;
    protected Service service;
    protected Duration timeToNextItem;
    protected Instant requestTime;

    private Duration getTimeToNextItemAsDuration() {
        return timeToNextItem.minus(Duration.between(requestTime, Instant.now()));
    }

    @Override
    public Item getCurrentItem() {
        return currentItem;
    }

    @Override
    public Item getNextItem() {
        return nextItem;
    }

    @Override
    public @NotNull Service getService() {
        return service;
    }

    @Override
    public boolean isValid() {
        if (timeToNextItem == null)
            return true;
        return !getTimeToNextItemAsDuration().isNegative();
    }

    @Override
    public String toString() {
        return "Metadata{" +
                "currentItem=" + currentItem +
                ", nextItem=" + nextItem +
                ", service=" + service +
                ", timeToNextItem=" + timeToNextItem +
                ", requestTime=" + requestTime +
                '}';
    }
}
