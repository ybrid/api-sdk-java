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

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.*;

public class Item implements hasIdentifier {
    public static final String METADATA_TITLE = "title";
    public static final String METADATA_ARTIST = "artist";
    public static final String METADATA_DESCRIPTION = "description";

    private static final String[] metadataList = {METADATA_ARTIST, METADATA_DESCRIPTION, METADATA_TITLE};

    public enum Type {
        ADVERTISEMENT, COMEDY, JINGLE, MUSIC, NEWS, VOICE;
    }

    private String identifier;
    private HashMap<String, String> metadata = new HashMap<>();
    private Type type;
    private long duration;
    private ArrayList<Companion> companions = new ArrayList<>();

    public Item(JSONObject json) throws MalformedURLException {
        identifier = json.getString("id");
        JSONArray array;

        for (String key : metadataList) {
            String value = json.getString(key);
            if (value != null && !value.isEmpty())
                metadata.put(key, value);
        }

        type = Type.valueOf(json.getString("type"));

        duration = json.getLong("durationMillis");

        array = json.getJSONArray("companions");
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                companions.add(new Companion(array.getJSONObject(i)));
            }
        }
    }

    public String getDisplayName() {
        String artist = metadata.get(METADATA_ARTIST);
        String title = metadata.get(METADATA_TITLE);

        if (artist != null && title != null) {
            return artist + " - " + title;
        }

        return title;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    public Type getType() {
        return type;
    }

    public long getDuration() {
        return duration;
    }

    public List<Companion> getCompanions() {
        return Collections.unmodifiableList(companions);
    }


    @Override
    public String toString() {
        return "Item{" +
                "identifier='" + identifier + '\'' +
                ", metadata=" + metadata +
                ", type=" + type +
                ", duration=" + duration +
                ", companions=" + companions +
                '}';
    }
}
