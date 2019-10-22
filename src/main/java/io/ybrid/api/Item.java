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

package io.ybrid.api;

import java.util.List;
import java.util.Map;

/**
 * This interface is implemented by objects representing an Item. A item roughly corresponds to a track.
 */
public interface Item extends hasIdentifier, hasDisplayName {
    /**
     * This allows access to the items Metadata.
     * @return Returns the map of metadata.
     */
    Map<String, String> getMetadata();

    /**
     * Returns the type of the item.
     * The item type can be used by players to switch between audio profiles.
     * This can be useful to for example provide different settings for traffic announcements.
     * @return Returns the type of the item.
     */
    ItemType getType();

    /**
     * Return the total playback time of the item.
     * @return Returns the playback time in [ms].
     */
    long getDuration();

    /**
     * Returns the list of Companions as to be displayed while this item is played.
     * @return Returns the list of Companions.
     */
    List<Companion> getCompanions();
}
