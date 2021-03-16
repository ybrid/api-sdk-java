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

import org.jetbrains.annotations.ApiStatus;

import java.io.Serializable;

/**
 * This interface is implemented by objects returning a swap state.
 */
public interface SwapInfo extends Serializable {
    /**
     * This returns the state of the next swap.
     * @return Returns whether the next swap will return to the main program.
     */
    boolean isNextSwapReturnToMain();

    /**
     * Returns the number of swaps the client is expected to be allowed before the server refuses them.
     * @return Returns the number of swaps the user can do.
     * @deprecated This should no longer be used.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    int getSwapsLeft();

    /**
     * Returns whether the object expects the next swap to be successful.
     *
     * This can be used to update the user interface to provide a swap button only when expected to work.
     * @return Whether the next swap is expected to be successful.
     * @deprecated Use {@link CapabilitySet#contains(Capability)} with {@link Capability#SWAP_ITEM}.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    default boolean canSwap() {
        return getSwapsLeft() != 0;
    }
}
