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

import io.ybrid.api.ItemType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;

final class Item extends io.ybrid.api.driver.common.Item {
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
