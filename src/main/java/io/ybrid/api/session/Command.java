/*
 * Copyright (c) 2020 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.session;

import io.ybrid.api.Session;
import io.ybrid.api.SubInfo;
import io.ybrid.api.SwapMode;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.ItemType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;

public enum Command {
    /**
     * Connects the {@link Session}.
     */
    CONNECT,
    /**
     * Disconnects the {@link Session}.
     */
    DISCONNECT,
    /**
     * Requests the refresh of a set of {@link SubInfo}.
     */
    REFRESH(true, EnumSet.class),
    /**
     * Winds back to the live position of the stream.
     */
    WIND_TO_LIVE,
    /**
     * Winds to a specific {@link Instant} on the stream.
     */
    WIND_TO(true, Instant.class),
    /**
     * Winds by a specific {@link Duration} on the stream.
     */
    WIND_BY(true, Duration.class),
    /**
     * Skips forward to next item of a given {@link ItemType} or the next item if {@code null}.
     */
    SKIP_FORWARD(false, ItemType.class),
    /**
     * Skips backwards to previous item of a given {@link ItemType} or the previous item if {@code null}.
     */
    SKIP_BACKWARD(false, ItemType.class),
    /**
     * Swaps the item using the given {@link SwapMode}.
     */
    SWAP_ITEM(true, SwapMode.class),
    /**
     * Swaps back to the main item.
     */
    SWAP_TO_MAIN_ITEM,
    /**
     * Swaps to the given {@link Service}.
     */
    SWAP_SERVICE(true, Service.class),
    /**
     * Swaps back to the main {@link Service}.
     */
    SWAP_TO_MAIN_SERVICE;

    private final boolean argumentNotNull;
    private final @Nullable Class<?>[] argumentTypes;

    Command(boolean argumentNotNull, @NotNull Class<?> argumentType) {
        this.argumentNotNull = argumentNotNull;
        this.argumentTypes = new Class[]{argumentType};
    }

    Command() {
        this.argumentNotNull = false;
        this.argumentTypes = null;
    }

    /**
     * Gets the number of arguments requests for this command requires.
     * @return The number of required arguments.
     */
    @Contract(pure = true)
    public int numberOfArguments() {
        return argumentTypes == null ? 0 : 1;
    }

    /**
     * Checks whether a object is valid as argument for the given Command.
     * This includes checks for type, and nullability.
     *
     * @param index The index of the argument starting with 0 for the first argument.
     * @param argument The object to check.
     * @return Whether the object is valid as argument.
     */
    @Contract(pure = true)
    public boolean isArgumentValid(int index, @Nullable Object argument) {
        if (index < 0 || index >= numberOfArguments())
            throw new IllegalArgumentException("Argument index is out of range for request command " + this + ", command takes " + numberOfArguments() + " arguments but index " + index + " was passed");

        if (argument == null) {
            return !argumentNotNull;
        } else {
            //noinspection NullableProblems
            for (final @NotNull Class<?> type : argumentTypes) {
                if (type.isInstance(argument))
                    return true;
            }
            return false;
        }
    }

    /**
     * Builds a new {@link Request} for this Command with no arguments.
     * @return The newly created request.
     */
    @Contract(" -> new")
    public @NotNull Request makeRequest() throws IllegalArgumentException {
        if (numberOfArguments() != 0)
            throw new IllegalArgumentException("Invalid number of arguments for request command " + this + ", expected " + numberOfArguments() + " but got 0");

        return new Request(this, null);
    }

    /**
     * Builds a new {@link Request} for this Command with one arguments.
     * @param argument The argument to pass as part of the request.
     * @return The newly created request.
     */
    @Contract(" -> new")
    public @NotNull Request makeRequest(@Nullable Object argument) throws IllegalArgumentException {
        if (numberOfArguments() != 1)
            throw new IllegalArgumentException("Invalid number of arguments for request command " + this + ", expected " + numberOfArguments() + " but got 1");

        if (!isArgumentValid(0, argument))
            throw new IllegalArgumentException("Invalid type of argument 0 for request command " + this);

        return new Request(this, new Object[]{argument});
    }
}
