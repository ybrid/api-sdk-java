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

package io.ybrid.api;

import java.io.IOException;
import java.time.Instant;

/**
 * This interface is implemented by objects that control a session.
 */
public interface SessionClient {
    /**
     * Get the current set of {@link Capability Capabilities} supported.
     * This can be used to display different options to the user.
     *
     * Calling this resets the flag returned by {@link #haveCapabilitiesChanged()}
     * @return Returns the set of current {@link Capability Capabilities}.
     */
    CapabilitySet getCapabilities();

    /**
     * Checks whether the {@link CapabilitySet} has changed since last call to {@link #getCapabilities()}.
     * @return Whether {@link #getCapabilities()} needs to be called again.
     */
    boolean haveCapabilitiesChanged();

    /**
     * Get the current Bouquet of active Services.
     * The Bouquet may be displayed to the user to select the Service to listen to.
     * @return Returns the current Bouquet.
     */
    Bouquet getBouquet() throws IOException;

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

    /**
     * Get the current {@link PlayoutInfo} for the session.
     * @return Returns the current {@link PlayoutInfo}.
     * @throws IOException Thrown on any I/O-Error.
     */
    PlayoutInfo getPlayoutInfo() throws IOException;
}
