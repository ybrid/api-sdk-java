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

package io.ybrid.api.transaction;

import io.ybrid.api.Session;
import io.ybrid.api.session.Command;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * This class implements transactions on {@link Session}s.
 */
public final class SessionTransaction extends SimpleTransaction {
    private final @NotNull Session session;
    private final @NotNull Request<Command> request;
    private final @NotNull Executor executor;

    public interface Executor {
        void execute(@NotNull Transaction transaction) throws Exception;
    }

    /**
     * Internal main constructor.
     * Use {@link Session#createTransaction(Request)} instead.
     *
     * @param session The session to execute the request on.
     * @param request The request to execute.
     * @param executor The method to call for execution.
     * @see Session#createTransaction(Request)
     */
    @ApiStatus.Internal
    public SessionTransaction(@NotNull Session session, @NotNull Request<Command> request, @NotNull Executor executor) {
        this.session = session;
        this.request = request;
        this.executor = executor;
    }

    /**
     * Gets the {@link Request} of this transaction.
     * @return The request.
     */
    @Contract(pure = true)
    public @NotNull io.ybrid.api.session.Request getRequest() {
        return new io.ybrid.api.session.Request(request);
    }

    /**
     * Gets the {@link Session} of this transaction.
     * @return The session.
     */
    @Contract(pure = true)
    public @NotNull Session getSession() {
        return session;
    }

    @Override
    @ApiStatus.Internal
    protected void execute() throws Exception {
        executor.execute(this);
    }
}
