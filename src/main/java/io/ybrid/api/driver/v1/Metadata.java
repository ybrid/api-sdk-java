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

package io.ybrid.api.driver.v1;

import io.ybrid.api.driver.common.Service;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.Instant;

public final class Metadata extends io.ybrid.api.driver.common.Metadata {
    private Metadata(@NotNull Service service, @NotNull JSONObject json, Instant requestTime) throws MalformedURLException {
        this.service = service;
        this.requestTime = requestTime;

        currentItem = new Item(json.getJSONObject("currentItem"));
        nextItem = new Item(json.getJSONObject("nextItem"));
        if (json.has("timeToNextItemMillis")) {
            timeToNextItem = Duration.ofMillis(json.getLong("timeToNextItemMillis"));
        } else {
            timeToNextItem = null;
        }

        if (service instanceof io.ybrid.api.driver.v1.Service)
            ((io.ybrid.api.driver.v1.Service)service).updateStation(json.getJSONObject("station"));
    }

    public Metadata(@NotNull Service service, @NotNull JSONObject json) throws MalformedURLException {
        this(service, json, Instant.now());
    }

    @Override
    public boolean isValid() {
        if (timeToNextItem == null)
            return true;
        return super.isValid();
    }
}
