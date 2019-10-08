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

import io.ybrid.api.driver.common.Companion;

import java.util.List;
import java.util.Map;

public interface Item extends hasIdentifier, hasDisplayName {
    Map<String, String> getMetadata();
    ItemType getType();
    long getDuration();
    List<Companion> getCompanions();
}
