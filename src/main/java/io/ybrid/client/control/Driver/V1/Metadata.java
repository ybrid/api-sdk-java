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

package io.ybrid.client.control.Driver.V1;

import io.ybrid.client.control.Item;
import org.json.JSONObject;

import java.net.MalformedURLException;

class Metadata extends io.ybrid.client.control.Driver.Common.Metadata {
    private Metadata(Service service, JSONObject json, long requestTime) throws MalformedURLException {
        this.service = service;
        this.requestTime = requestTime;

        currentBitRate = json.getInt("currentBitRate");
        currentItem = new Item(json.getJSONObject("currentItem"));
        nextItem = new Item(json.getJSONObject("nextItem"));
        service.updateStation(json.getJSONObject("station"));
        swapInfo = new SwapInfo(json.getJSONObject("swapInfo"));
        timeToNextItem = json.getLong("timeToNextItemMillis");
    }

    Metadata(Service service, JSONObject json) throws MalformedURLException {
        this(service, json, System.currentTimeMillis());
    }

}
