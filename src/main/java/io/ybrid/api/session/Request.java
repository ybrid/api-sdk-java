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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * This class represents a single request to the {@link io.ybrid.api.Session} or it's driver.
 * It provides a uniform way to make API requests.
 * <P>
 * Instances can be created using {@link Command#makeRequest()}, and {@link Command#makeRequest(Object)}.
 */
public final class Request {
    private final @NotNull Command command;
    private final @Nullable Object[] arguments;

    /**
     * Internal constructor.
     * @param command The command to request.
     * @param arguments The argument list for the request or {@code null}.
     * @see Command#makeRequest()
     * @see Command#makeRequest(Object)
     */
    protected Request(@NotNull Command command, @Nullable Object[] arguments) {
        this.command = command;
        this.arguments = arguments;
    }

    /**
     * Gets the {@link Command} of this request.
     * @return The command.
     */
    @Contract(pure = true)
    public @NotNull Command getCommand() {
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
