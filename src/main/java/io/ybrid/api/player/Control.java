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

package io.ybrid.api.player;

import io.ybrid.api.session.Session;
import io.ybrid.api.transaction.Request;
import io.ybrid.api.transaction.RequestBasedTransaction;
import io.ybrid.api.transaction.Transaction;
import io.ybrid.api.transport.ServiceTransportDescription;
import io.ybrid.api.util.MediaType;
import io.ybrid.api.util.QualityMap.MediaTypeMap;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface defines the communication channel from the {@link Session} to the player.
 */
public interface Control {
    /**
     * This is called when the {@link Session} attaches a player.
     *
     * @param session The {@link Session} doing the attach.
     */
    default void onAttach(@NotNull Session session) {
        // no-op
    }

    /**
     * This is called when the {@link Session} detaches a player.
     * The player must not use the {@link Session} any longer.
     *
     * @param session The {@link Session} doing the detach.
     */
    default void onDetach(@NotNull Session session) {
        // no-op
    }

    /**
     * Get the list of {@link MediaType}s supported by the player.
     *
     * If this returns null no {@code Accept:}-header should be generated.
     * @return List of supported formats or null.
     */
    default @Nullable MediaTypeMap getAcceptedMediaTypes() {
        return null;
    }

    /**
     * Requests the player to connect a new transport.
     *
     * @param transportDescription The transport to connect.
     * @throws Exception Any exception thrown while connecting the new transport.
     */
    void connectTransport(@NotNull ServiceTransportDescription transportDescription) throws Exception;

    /**
     * Executes the given transaction's {@link Request} in the player context.
     * <P>
     * <B>Note</B>:
     * This is called from within {@link Transaction#run()}.
     * Implementations must call {@link RequestBasedTransaction#getRequest()} to find the {@link Request}
     * and handle this request.
     * <P>
     * The default implementation throws {@link UnsupportedOperationException}.
     *
     * @param transaction The transaction to execute.
     * @throws Throwable Any exception thrown while executing the request.
     */
    default <C extends Command<C>> void executeTransaction(@NotNull RequestBasedTransaction<Request<C>> transaction) throws Throwable {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new Transaction for the given {@link Request}.
     * The resulting transaction will be executed on this Control.
     * <P>
     * <B>Note:</B> Most implementations should not override this.
     *
     * @param request The request to create a transaction for.
     * @param <C> The Command type.
     * @return The resulting transaction.
     */
    @ApiStatus.NonExtendable
    default <C extends Command<C>> @NotNull Transaction createTransaction(@NotNull Request<C> request) {
        return new RequestBasedTransaction<Request <C>>(request) {
            @Override
            protected void execute() throws Throwable {
                Control.this.executeTransaction(this);
            }
        };
    }
}
