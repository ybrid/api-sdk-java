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

/**
 * This interface is implemented by objects that control a session.
 */
public interface SessionClient {
    /**
     * Get the current Bouquet of active Services.
     * The Bouquet may be displayed to the user to select the Service to listen to.
     * @return Returns the current Bouquet.
     */
    Bouquet getBouquet();

    /**
     * This call requests the session to be brought back to the live portion of the current service.
     * @throws IOException Thrown on any I/O-Error.
     */
    void windToLive() throws IOException;

    /**
     * This call requests the session to be brought to the given time within the current service.
     * @param timestamp The timestamp to jump to.
     * @throws IOException Thrown on any I/O-Error.
     */
    void WindTo(Instant timestamp) throws IOException;

    /**
     * This call allows to move in the stream by a relative time.
     * The time can be positive to move into the future or negative to move into the past
     * relative to the current position.
     * @param duration The duration to wind in [ms].
     * @throws IOException Thrown on any I/O-Error.
     */
    void Wind(long duration) throws IOException;

    /**
     * Skip to the next Item of the given type.
     * @param itemType The ItemType to skip to.
     * @throws IOException Thrown on any I/O-Error.
     */
    void skipForwards(ItemType itemType) throws IOException;

    /**
     * Skip to the previous Item of the given type.
     * @param itemType The ItemType to skip to.
     * @throws IOException Thrown on any I/O-Error.
     */
    void skipBackwards(ItemType itemType) throws IOException;

    /**
     * Swap the current Item with a different one.
     * @param mode The mode for the swap. See {@link SwapMode} for details.
     * @throws IOException Thrown on any I/O-Error.
     */
    void swapItem(SwapMode mode) throws IOException;

    /**
     * Swap to a different Service.
     * @param service The new service to listen to.
     */
    void swapService(Service service);

    /**
     * Get the current Metadata for the session.
     * @return Returns the current Metadata.
     * @throws IOException Thrown on any I/O-Error.
     */
    Metadata getMetadata() throws IOException;

    /**
     * Returns the current Service the session is connected to.
     * @return This returns the current Service.
     */
    Service getCurrentService();
}
