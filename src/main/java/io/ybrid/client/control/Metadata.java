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

package io.ybrid.client.control;

import io.ybrid.client.control.Driver.Common.Service;
import org.json.JSONObject;

import java.net.MalformedURLException;

public class Metadata {
    private int currentBitRate;
    private Item currentItem;
    private Item nextItem;
    private Service service;
    private SwapInfo swapInfo;
    private long timeToNextItem;
    private long requestTime;

    public Metadata(Service service, JSONObject json, long requestTime) throws MalformedURLException {
        this.service = service;
        this.requestTime = requestTime;

        currentBitRate = json.getInt("currentBitRate");
        currentItem = new Item(json.getJSONObject("currentItem"));
        nextItem = new Item(json.getJSONObject("nextItem"));
        service.updateStation(json.getJSONObject("station"));
        swapInfo = new SwapInfo(json.getJSONObject("swapInfo"));
        timeToNextItem = json.getLong("timeToNextItemMillis");
    }

    public Metadata(Service service, JSONObject json) throws MalformedURLException {
        this(service, json, System.currentTimeMillis());
    }

    public Item getCurrentItem() {
        return currentItem;
    }

    public Item getNextItem() {
        return nextItem;
    }

    public int getCurrentBitRate() {
        return currentBitRate;
    }

    public Service getService() {
        return service;
    }

    public SwapInfo getSwapInfo() {
        return swapInfo;
    }

    public long getTimeToNextItem() {
        return timeToNextItem - (System.currentTimeMillis() - requestTime);
    }

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
