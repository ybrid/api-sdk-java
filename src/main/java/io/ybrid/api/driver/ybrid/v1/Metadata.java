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

package io.ybrid.api.driver.ybrid.v1;

import io.ybrid.api.TemporalValidity;
import io.ybrid.api.driver.common.Service;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.time.Duration;

public final class Metadata extends io.ybrid.api.metadata.SimpleMetadata {
    public Metadata(@NotNull Service service, @NotNull JSONObject json) throws MalformedURLException {
        super(new Item(json.getJSONObject("currentItem")), new Item(json.getJSONObject("nextItem")), service,
                json.has("timeToNextItemMillis") ? TemporalValidity.makeFromNow(Duration.ofMillis(json.getLong("timeToNextItemMillis"))) : TemporalValidity.INDEFINITELY_VALID
                );

        if (service instanceof io.ybrid.api.driver.ybrid.v1.Service)
            ((io.ybrid.api.driver.ybrid.v1.Service)service).updateStation(json.getJSONObject("station"));
    }
}
