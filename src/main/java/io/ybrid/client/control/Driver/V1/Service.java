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

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Service extends io.ybrid.client.control.Driver.Common.Service {
    private static final String STATION_NAME = "name";
    private static final String STATION_GENERE = "genre";
    private static final String[] STATION_METADATA_LIST = {STATION_NAME, STATION_GENERE};

    private HashMap<String, String> station = new HashMap<>();

    public Service(JSONObject json) throws MalformedURLException {
        super(json);
    }

    public Service() {
    }

    public Map<String, String> getStation() {
        return Collections.unmodifiableMap(station);
    }

    @Override
    public void updateStation(JSONObject json) {
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
        return station.get(STATION_GENERE);
    }

    @Override
    public String getDisplayName() {
        String value = station.get(STATION_NAME);

        if (value == null)
            value = identifier;

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
