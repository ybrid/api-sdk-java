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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This is the base class for all commands.
 * This should not be used directly rather should command classes be implemented using Enums.
 * @param <C> The command type.
 */
public interface Command<C extends Command<C>> {
    /**
     * Gets the number of arguments requests for this command requires.
     * @return The number of required arguments.
     */
    @Contract(pure = true)
    int numberOfArguments();

    /**
     * Checks whether a object is valid as argument for the given Command.
     * This includes checks for type, and nullability.
     *
     * @param index The index of the argument starting with 0 for the first argument.
     * @param argument The object to check.
     * @return Whether the object is valid as argument.
     */
    @Contract(pure = true)
    boolean isArgumentValid(int index, @Nullable Object argument);

    /**
     * Builds a new {@link Request} for this Command with no arguments.
     * @return The newly created request.
     */
    @Contract(" -> new")
    default @NotNull Request<C> makeRequest() throws IllegalArgumentException {
        if (numberOfArguments() != 0)
            throw new IllegalArgumentException("Invalid number of arguments for request command " + this + ", expected " + numberOfArguments() + " but got 0");

        //noinspection unchecked
        return new Request<>((C) this, null);
    }

    /**
     * Builds a new {@link Request} for this Command with one arguments.
     * @param argument The argument to pass as part of the request.
     * @return The newly created request.
     */
    @Contract("_ -> new")
    default @NotNull Request<C> makeRequest(@Nullable Object argument) throws IllegalArgumentException {
        if (numberOfArguments() != 1)
            throw new IllegalArgumentException("Invalid number of arguments for request command " + this + ", expected " + numberOfArguments() + " but got 1");

        if (!isArgumentValid(0, argument))
            throw new IllegalArgumentException("Invalid type of argument 0 for request command " + this);

        //noinspection unchecked
        return new Request<>((C) this, new Object[]{argument});
    }
}
