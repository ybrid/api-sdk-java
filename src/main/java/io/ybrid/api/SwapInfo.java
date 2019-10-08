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

/**
 * This interface is implemented by objects returning a swap state.
 */
public interface SwapInfo {
    /**
     * This returns the state of the next swap.
     * @return Returns whether the next swap will return to the main program.
     */
    boolean isNextSwapReturnToMain();

    /**
     * Returns the number of swaps the client is expected to be allowed before the server refuses them.
     * @return Returns the number of swaps the user can do.
     */
    int getSwapsLeft();

    /**
     * Returns whether the object expects the next swap to be successful.
     *
     * This can be used to update the user interface to provide a swap button only when expected to work.
     * @return Whether the next swap is expected to be successful.
     */
    default boolean canSwap() {
        return getSwapsLeft() != 0;
    }
}
