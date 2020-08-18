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

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Service extends io.ybrid.api.driver.common.Service {
    private static final String STATION_NAME = "name";
    private static final String STATION_GENRE = "genre";
    private static final String[] STATION_METADATA_LIST = {STATION_NAME, STATION_GENRE};

    private HashMap<String, String> station = new HashMap<>();

    Service() {
    }

    @NotNull
    public Map<String, String> getStation() {
        return Collections.unmodifiableMap(station);
    }

    void updateStation(@NotNull JSONObject json) {
        HashMap<String, String> station = new HashMap<>();

        for (String key : STATION_METADATA_LIST) {
            String value = json.getString(key);
            if (value != null && !value.isEmpty())
                station.put(key, value);
        }

        this.station = station;
    }

    @Override
    public String getGenre() {
        return station.get(STATION_GENRE);
    }

    @Override
    public @NotNull String getDisplayName() {
        String value = station.get(STATION_NAME);

        if (value == null)
            value = super.getDisplayName();

        return value;
    }

    @Override
    public String toString() {
        return "Service{" +
                "identifier='" + identifier + '\'' +
                ", icon=" + icon +
                ", station=" + station +
                '}';
    }
}
