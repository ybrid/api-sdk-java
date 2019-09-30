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

import io.ybrid.client.control.ItemType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;

class Item extends io.ybrid.client.control.Driver.Common.Item {
    Item(JSONObject json) throws MalformedURLException {
        identifier = json.getString("id");
        JSONArray array;

        for (String key : metadataList) {
            String value = json.getString(key);
            if (value != null && !value.isEmpty())
                metadata.put(key, value);
        }

        type = ItemType.valueOf(json.getString("type"));

        duration = json.getLong("durationMillis");

        array = json.getJSONArray("companions");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                companions.add(new Companion(array.getJSONObject(i)));
            }
        }
    }
}
