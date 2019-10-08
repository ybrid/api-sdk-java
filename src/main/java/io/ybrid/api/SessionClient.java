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

import java.io.IOException;
import java.time.Instant;

public interface SessionClient {
    Bouquet getBouquet();
    void windToLive() throws IOException;
    void WindTo(Instant timestamp) throws IOException;
    void Wind(long duration) throws IOException;
    void skipForwards(ItemType itemType) throws IOException;
    void skipBackwards(ItemType itemType) throws IOException;
    void swapItem(SwapMode mode) throws IOException;
    void swapService(Service service);
    Metadata getMetadata() throws IOException;
    Service getCurrentService();
}
