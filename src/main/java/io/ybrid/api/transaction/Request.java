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

import io.ybrid.api.util.Utils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

/**
 * This class represents a single request to the {@link io.ybrid.api.Session} or related objects.
 * It provides a uniform way to make API requests.
 * <P>
 * Instances can be created using {@link Command#makeRequest()}, and {@link Command#makeRequest(Object)}.
 */
public class Request<C extends Command<C>> implements Serializable {
    private final @NotNull C command;
    private final @Nullable Serializable[] arguments;

    private static Serializable[] cloneArrayTypeCorrect(final Object[] input) {
        final Serializable[] ret = new Serializable[input.length];

        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(input, 0, ret, 0, input.length);

        return ret;
    }

    /**
     * Internal constructor.
     * @param command The command to request.
     * @param arguments The argument list for the request or {@code null}.
     * @see Command#makeRequest()
     * @see Command#makeRequest(Object)
     */
    protected Request(@NotNull C command, @Nullable Serializable[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * Internal constructor.
     * @param command The command to request.
     * @param arguments The argument list for the request or {@code null}.
     * @see Command#makeRequest()
     * @see Command#makeRequest(Object)
     * @deprecated Use {@link #Request(Command, Serializable[])}
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    protected Request(@NotNull C command, @Nullable Object[] arguments) {
        this.command = command;
        this.arguments = Utils.transform(arguments, Request::cloneArrayTypeCorrect);
    }

    /**
     * Gets the {@link Command} of this request.
     * @return The command.
     */
    @Contract(pure = true)
    public @NotNull C getCommand() {
        return command;
    }

    /**
     * Gets the list of arguments for the request.
     * @return The argument list or {@code null}.
     */
    @Contract(pure = true)
    public @Nullable Object[] getArguments() {
        return arguments;
    }

    /**
     * Gets the n-th argument.
     * @param index The index of the argument starting with 0.
     * @return The argument.
     */
    @Contract(pure = true)
    public @Nullable Object getArgumentNullable(int index) {
        return getArguments()[index];
    }

    /**
     * Gets the n-th argument and requires it to be non-null.
     * @param index The index of the argument starting with 0.
     * @return The argument.
     */
    @Contract(pure = true)
    public @NotNull Object getArgumentNotNull(int index) {
        return Objects.requireNonNull(getArguments()[index]);
    }
}
