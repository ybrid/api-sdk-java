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

import io.ybrid.api.Identifier;
import io.ybrid.api.hasIdentifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

abstract class Transaction implements hasIdentifier, Runnable {
    private final @NotNull Identifier identifier = new Identifier();
    private final @NotNull Set<@NotNull Runnable> onControlComplete = new HashSet<>();
    private final @NotNull Set<@NotNull Runnable> onAudioComplete = new HashSet<>();
    private boolean running = false;
    private boolean controlComplete = false;
    private boolean audioComplete = false;
    private @Nullable Throwable error = null;

    /**
     * The method executed as the transaction's task.
     * This is an internal method and must never be called directly.
     *
     * @throws Exception Any excepting thrown while executing.
     */
    @ApiStatus.Internal
    protected abstract void execute() throws Exception;

    private void signal(@NotNull Collection<@NotNull Runnable> callbacks) {
        for (@NotNull Runnable runnable : callbacks) {
            try {
                runnable.run();
            } catch (Throwable ignored) {
            }
        }
    }

    /**
     * Internal method used to signal that the control phase is complete.
     */
    @ApiStatus.Internal
    protected void signalControlComplete() {
        signal(onControlComplete);
    }

    /**
     * Internal method used to signal that the transaction is complete and now audible.
     */
    @ApiStatus.Internal
    protected void signalAudioComplete() {
        signal(onAudioComplete);
    }

    /**
     * Adds a callback to be notified once the transaction has completed the control phase.
     * @param runnable The callback.
     */
    public void onControlComplete(@NotNull Runnable runnable) {
        onControlComplete.add(runnable);
    }

    /**
     * Adds a callback to be notified once the transaction has completed and the result is audible.
     * @param runnable The callback.
     */
    public void onAudioComplete(@NotNull Runnable runnable) {
        onAudioComplete.add(runnable);
    }

    /**
     * Queries whether the transaction has completed the control phase.
     * @return Whether the transaction completed the control phase.
     */
    public boolean isControlComplete() {
        return controlComplete;
    }

    /**
     * Queries whether the transaction has completed and the result is audible.
     * @return Whether the transaction completed and is audible.
     */
    public boolean isAudioComplete() {
        return audioComplete;
    }

    /**
     * Informs the transaction that the change is now audible.
     * This is to be called by the player when the audio reached the point the result of of this transaction is audible.
     */
    public void setAudioComplete() {
        audioComplete = true;
        signalControlComplete();
    }

    /**
     * Queries whether the current transaction is running.
     * @return Whether the transaction is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the the error thrown by running the transaction.
     * This returns {@code null} if no error has been thrown yet.
     * @return The error or {@code null}.
     */
    public @Nullable Throwable getError() {
        return error;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    /**
     * This runs the actual transaction.
     * This method will block as long as the transaction is running.
     *
     * @see #runInBackground()
     */
    @Override
    public void run() {
        synchronized (this) {
            if (controlComplete || running || error != null)
                return;

            running = true;
            try {
                execute();
            } catch (Throwable e) {
                error = e;
            }
            controlComplete = true;
            running = false;
        }
        signalControlComplete();
    }

    /**
     * This is a helper method. It runs {@link #run()} in a thread.
     * This method returns once the thread has been started and does not block
     * until the transaction has been completed.
     */
    public synchronized void runInBackground() {
        if (controlComplete || running || error != null)
            return;
        new Thread(this, "Transaction " + identifier.toString()).start();
    }
}
