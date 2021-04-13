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
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * This is the base class for all commands.
 * This should not be used directly rather should command classes be implemented using Enums.
 * @param <C> The command type.
 */
public interface Command<C extends Command<C>> extends Serializable {
    /**
     * Gets the number of arguments requests for this command requires.
     * @return The number of required arguments.
     * @deprecated Implementations should only use {@link #assertArgumentListValid(Serializable[])}
     */
    @Deprecated
    @Contract(pure = true)
    @ApiStatus.ScheduledForRemoval
    @ApiStatus.OverrideOnly
    default int numberOfArguments() {
        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether a object is valid as argument for the given Command.
     * This includes checks for type, and nullability.
     *
     * @param index    The index of the argument starting with 0 for the first argument.
     * @param argument The object to check.
     * @return Whether the object is valid as argument.
     * @deprecated Implementations should only use {@link #assertArgumentListValid(Serializable[])}
     */
    @Deprecated
    @Contract(pure = true)
    @ApiStatus.ScheduledForRemoval
    @ApiStatus.OverrideOnly
    default boolean isArgumentValid(int index, @Nullable Object argument) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns whether the command has a audio related action.
     * <P>
     * For commands with audio actions the user can hear the action.
     * Examples include the user hearing the the audio jump to the new position on seek.
     * For commands with no such action the user can not tell the command to be performed.
     * Examples for this include pure control messages such as keep-alive messages or metadata
     * requests.
     * <P>
     * The default implementation returns {@code false}.
     *
     * @return Whether this command has a audio action.
     */
    @ApiStatus.Experimental
    default boolean hasAudioAction() {
        return true;
    }

    /**
     * Checks whether a list of arguments is valid for the given Command.
     * This must check both the number of arguments as well as their type,
     * and value (including nullability).
     *
     * @param arguments The list of arguments.
     * @throws IllegalArgumentException Thrown when the argument list is invalid
     */
    @Contract(value = "null -> fail; !null -> _", pure = true)
    default void assertArgumentListValid(@Nullable Serializable[] arguments) throws IllegalArgumentException {
        if (numberOfArguments() != arguments.length)
            throw new IllegalArgumentException("Invalid number of arguments for request command " + this + ", expected " + numberOfArguments() + " but got 1");

        for (int i = 0; i < arguments.length; i++) {
            if (!isArgumentValid(i, arguments[i]))
                throw new IllegalArgumentException("Invalid type of argument " + i + " for request command " + this);
        }
    }

    /**
     * Builds a new {@link Request} for this Command with no arguments.
     * @return The newly created request.
     */
    @Contract(" -> new")
    default @NotNull Request<C> makeRequest() throws IllegalArgumentException {
        assertArgumentListValid(new Serializable[0]);

        //noinspection unchecked
        return new Request<>((C) this, null);
    }

    /**
     * Builds a new {@link Request} for this Command with one arguments.
     * @param argument The argument to pass as part of the request.
     * @return The newly created request.
     */
    @Contract("_ -> new")
    default @NotNull Request<C> makeRequest(@Nullable Serializable argument) throws IllegalArgumentException {
        final @Nullable Serializable[] arguments = new Serializable[]{argument};

        assertArgumentListValid(arguments);

        //noinspection unchecked
        return new Request<>((C) this, arguments);
    }

    /**
     * Builds a new {@link Request} for this Command with one arguments.
     * @param argument The argument to pass as part of the request.
     * @return The newly created request.
     * @deprecated Use {@link #makeRequest(Serializable)}.
     */
    @Deprecated
    @Contract("_ -> new")
    @ApiStatus.ScheduledForRemoval
    default @NotNull Request<C> makeRequest(@Nullable Object argument) throws IllegalArgumentException {
        return makeRequest((Serializable) argument);
    }
}
