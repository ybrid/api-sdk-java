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

package io.ybrid.api.driver.common;

import io.ybrid.api.ItemType;

import java.util.*;

abstract public class Item implements io.ybrid.api.Item {
    public static final String METADATA_TITLE = "title";
    public static final String METADATA_ARTIST = "artist";
    public static final String METADATA_DESCRIPTION = "description";

    protected static final String[] metadataList = {METADATA_ARTIST, METADATA_DESCRIPTION, METADATA_TITLE};

    protected String identifier;
    protected HashMap<String, String> metadata = new HashMap<>();
    protected ItemType type;
    protected long duration;
    protected ArrayList<Companion> companions = new ArrayList<>();

    @Override
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

    @Override
    public Map<String, String> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public ItemType getType() {
        return type;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    @Override
    public List<io.ybrid.api.Companion> getCompanions() {
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
