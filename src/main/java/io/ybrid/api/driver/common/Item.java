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
