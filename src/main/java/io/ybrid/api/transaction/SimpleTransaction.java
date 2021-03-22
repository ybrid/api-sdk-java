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

import io.ybrid.api.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

abstract class SimpleTransaction implements Transaction {
    static final Logger LOGGER = Logger.getLogger(SimpleTransaction.class.getName());

    private final @NotNull Identifier identifier = new Identifier();
    private final @NotNull Set<@NotNull Runnable> onControlComplete = new HashSet<>();
    private final @NotNull Set<@NotNull Runnable> onAudioComplete = new HashSet<>();
    private boolean signaledControlComplete = false;
    private boolean signaledAudioComplete = false;
    private boolean running = false;
    private boolean controlComplete = false;
    private boolean audioComplete = false;
    private @Nullable Throwable error = null;

    private void log(@NotNull String message) {
        LOGGER.fine("[Transaction " + getIdentifier().toString() + "] " + message);
    }

    /**
     * The method executed as the transaction's task.
     * This is an internal method and must never be called directly.
     *
     * @throws Exception Any excepting thrown while executing.
     */
    @ApiStatus.Internal
    @ApiStatus.OverrideOnly
    protected abstract void execute() throws Exception;

    private void signal(@NotNull Collection<@NotNull Runnable> callbacks) {
        //noinspection SynchronizationOnLocalVariableOrMethodParameter
        synchronized (callbacks) {
            for (@NotNull Runnable runnable : callbacks) {
                try {
                    runnable.run();
                } catch (Throwable ignored) {
                }
            }
        }
    }

    /**
     * Internal method used to signal that the control phase is complete.
     */
    @ApiStatus.Internal
    protected void signalControlComplete() {
        log("control is complete");
        synchronized (this) {
            if (signaledControlComplete)
                return;
            signaledControlComplete = true;
        }
        log("signaling control complete");
        signal(onControlComplete);
        log("signaled control complete");
    }

    /**
     * Internal method used to signal that the transaction is complete and now audible.
     */
    @ApiStatus.Internal
    protected void signalAudioComplete() {
        log("audio is complete");
        synchronized (this) {
            if (signaledAudioComplete)
                return;
            signaledAudioComplete = true;
        }
        log("signaling audio complete");
        signal(onAudioComplete);
        log("signaled audio complete");
    }

    @Override
    public void onControlComplete(@NotNull Runnable runnable) {
        synchronized (onControlComplete) {
            onControlComplete.add(runnable);
        }
    }

    @Override
    public void onAudioComplete(@NotNull Runnable runnable) {
        synchronized (onAudioComplete) {
            onAudioComplete.add(runnable);
        }
    }

    @Override
    public boolean isControlComplete() {
        return controlComplete;
    }

    @Override
    public boolean isAudioComplete() {
        return audioComplete;
    }

    @Override
    public synchronized void setAudioComplete() {
        if (audioComplete)
            return;
        audioComplete = true;
        signalAudioComplete();
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public @Nullable Throwable getError() {
        return error;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return identifier;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (controlComplete || running || error != null)
                return;

            running = true;
            log("Running...");
            try {
                execute();
            } catch (Throwable e) {
                error = e;
            }
            log("... control completed");
            controlComplete = true;
            running = false;
        }
        signalControlComplete();
    }

    @Override
    public synchronized void runInBackground() {
        if (controlComplete || running || error != null)
            return;
        new Thread(this, "Transaction " + identifier.toString()).start();
    }

    private void waitForComplete(@NotNull Supplier<Boolean> isComplete, @NotNull Consumer<Runnable> callbackConsumer) throws InterruptedException {
        final @NotNull Object bus = new Object();

        // this is just for speed up:
        if (isComplete.get())
            return;

        // now the real code:
        synchronized (this) {
            callbackConsumer.accept(() -> {
                synchronized (bus) {
                    bus.notify();
                }
                log("sent notify");
            });

            if (isComplete.get())
                return;
        }
        log("registered callback");

        synchronized (bus) {
            bus.wait();
        }
    }

    @Override
    public void waitControlComplete() throws InterruptedException {
        waitForComplete(this::isControlComplete, this::onControlComplete);
    }

    @Override
    public void waitAudioComplete() throws InterruptedException {
        waitForComplete(this::isAudioComplete, this::onAudioComplete);
    }
}
