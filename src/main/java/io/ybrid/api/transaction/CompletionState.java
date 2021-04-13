/*
 * Copyright (c) 2021 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.transaction;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum CompletionState {
    /**
     * The action has not yet completed.
     */
    INCOMPLETE,
    /**
     * The completion state was reached as the underlying action has completed.
     */
    DONE,
    /**
     * The completion state was reached as there was no action taken.
     */
    NO_ACTION,
    /**
     * The completion state was reached by an estimation (e.g. using a typical response delay).
     * @see #TIMEOUT
     */
    @ApiStatus.Experimental
    ESTIMATED,
    /**
     * The completion state was reached by a guess (e.g. a related statechange that makes the system guess
     * completion was reached).
     */
    @ApiStatus.Experimental
    GUESSED,
    /**
     * The completion state was reached by a timeout.
     * @see #ESTIMATED
     */
    @ApiStatus.Experimental
    TIMEOUT;

    /**
     * Tries to upgrade from a current to a new state.
     * @param next The new state to upgrade to.
     * @return The resulting new state.
     * @throws IllegalArgumentException Thrown if the upgrade is not possible.
     */
    @Contract(value = "_ -> param1", pure = true)
    @ApiStatus.Internal
    @ApiStatus.Experimental
    @NotNull CompletionState upgrade(@NotNull CompletionState next) throws IllegalArgumentException {
        if (this.equals(next))
            return next;

        if (this.equals(INCOMPLETE)) {
            return next;
        } else if (this.equals(ESTIMATED) || this.equals(GUESSED) || this.equals(TIMEOUT)) {
            if (next.equals(DONE)) {
                return next;
            }
        }

        throw new IllegalArgumentException("Can not upgrade from " + this + " to " + next);
    }
}
