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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class represents a single request to the {@link io.ybrid.api.Session} or it's driver.
 * It provides a uniform way to make API requests.
 * <P>
 * Instances can be created using {@link Command#makeRequest()}, and {@link Command#makeRequest(Object)}.
 * @deprecated Use {@link io.ybrid.api.transaction.Request} instead.
 */
@Deprecated
@ApiStatus.ScheduledForRemoval
public final class Request extends io.ybrid.api.transaction.Request<Command> {
    /**
     * Internal constructor.
     *
     * @param command   The command to request.
     * @param arguments The argument list for the request or {@code null}.
     * @see Command#makeRequest()
     * @see Command#makeRequest(Object)
     */
    protected Request(@NotNull Command command, @Nullable Object[] arguments) {
        super(command, arguments);
    }

    @Deprecated
    @ApiStatus.Internal
    @ApiStatus.ScheduledForRemoval
    public Request(@NotNull io.ybrid.api.transaction.Request<Command> request) {
        this(request.getCommand(), request.getArguments());
    }
}
