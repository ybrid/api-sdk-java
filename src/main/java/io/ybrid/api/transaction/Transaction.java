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

import io.ybrid.api.util.hasIdentifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This interface is common to all transactions.
 * It provides the basic outline for interaction with transactions.
 */
public interface Transaction extends hasIdentifier, Runnable {
    /**
     * Adds a callback to be notified once the transaction has completed the control phase.
     * @param runnable The callback.
     */
    void onControlComplete(@NotNull Runnable runnable);

    /**
     * Adds a callback to be notified once the transaction has completed and the result is audible.
     * @param runnable The callback.
     */
    void onAudioComplete(@NotNull Runnable runnable);

    /**
     * Queries whether the transaction has completed the control phase.
     * @return Whether the transaction completed the control phase.
     */
    boolean isControlComplete();

    /**
     * Queries whether the transaction has completed and the result is audible.
     * @return Whether the transaction completed and is audible.
     */
    boolean isAudioComplete();

    /**
     * Informs the transaction that the change is now audible.
     * This is to be called by the player when the audio reached the point the result of of this transaction is audible.
     */
    void setAudioComplete();

    /**
     * Queries whether the current transaction is running.
     * @return Whether the transaction is running.
     */
    boolean isRunning();

    /**
     * Gets the the error thrown by running the transaction.
     * This returns {@code null} if no error has been thrown yet.
     * @return The error or {@code null}.
     */
    @Nullable Throwable getError();

    /**
     * This runs the actual transaction.
     * This method will block as long as the transaction is running.
     *
     * @see #runInBackground()
     */
    @Override
    void run();

    /**
     * This is a helper method. It runs {@link #run()} in a thread.
     * This method returns once the thread has been started and does not block
     * until the transaction has been completed.
     */
    void runInBackground();

    /**
     * Asserts that this transaction has been completed successfully.
     * If the transaction has not completed at all (not been started yet or still running)
     * this will also throw an exception.
     * @throws TransactionExecutionException Thrown if the transaction has not completed successfully.
     */
    @ApiStatus.NonExtendable
    default void assertSuccess() throws TransactionExecutionException {
        if (!isControlComplete())
            throw new TransactionExecutionException(this, "The transaction has not been completed.");
        if (getError() != null)
            throw new TransactionExecutionException(this, "The transaction has failed.");
    }

    /**
     * Waits for the transaction to reach the control complete state.
     * @throws InterruptedException Thrown as by {@link Object#wait()}
     */
    void waitControlComplete() throws InterruptedException;

    /**
     * Waits for the transaction to reach the audio complete state.
     * @throws InterruptedException Thrown as by {@link Object#wait()}
     */
    void waitAudioComplete() throws InterruptedException;
}
