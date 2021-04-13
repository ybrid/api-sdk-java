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

import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class TransactionTest {
    private @NotNull Transaction createTransaction(@NotNull Runnable runnable) {
        return new SimpleTransaction() {
            @Override
            protected void execute() throws Throwable {
                runnable.run();
            }
        };
    }

    private static class WaiterThread extends Thread {
        final @NotNull Transaction transaction;
        final @NotNull Waiter waiter;

        public interface Waiter {
            void waitForComplete() throws InterruptedException;
        }

        public WaiterThread(@NotNull Transaction transaction, @NotNull Waiter waiter, @NotNull String name) {
            super(name);
            this.transaction = transaction;
            this.waiter = waiter;
        }

        @Override
        public void run() {
            System.out.println("before transaction " + getName());
            try {
                waiter.waitForComplete();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after transaction " + getName());
        }
    }

    @Test
    public void assertSuccess() {
        @NotNull Transaction transaction;

        transaction = createTransaction(() -> {});
        transaction.run();
        transaction.assertSuccess();

        transaction = createTransaction(() -> {throw new RuntimeException();});
        transaction.run();
        assertThrows(TransactionExecutionException.class, transaction::assertSuccess);
    }

    @Test
    public void waitControlComplete() throws InterruptedException {
        final @NotNull Transaction transaction = createTransaction(() -> {
            System.out.println("before sleep");
            try {
                Thread.sleep(128);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after sleep");
        });

        new WaiterThread(transaction, transaction::waitControlComplete, "Thread A").start();
        new WaiterThread(transaction, transaction::waitAudioComplete, "Thread B").start();

        System.out.println("before transaction thread main");
        transaction.runInBackground();
        System.out.println("after transaction scheduling thread main");
        transaction.waitControlComplete();
        System.out.println("after transaction thread main");
    }

    @Test
    public void waitAudioComplete() throws InterruptedException {
        final @NotNull Transaction transaction = createTransaction(() -> {
            System.out.println("before sleep");
            try {
                Thread.sleep(128);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("after sleep");
        });

        new WaiterThread(transaction, transaction::waitAudioComplete, "Thread A").start();
        new WaiterThread(transaction, transaction::waitAudioComplete, "Thread B").start();

        System.out.println("before transaction thread main");
        transaction.runInBackground();
        System.out.println("after transaction scheduling thread main");
        transaction.waitControlComplete();
        System.out.println("after transaction thread main");
        transaction.setAudioComplete(CompletionState.DONE);
    }

    @Test
    public void testAsync() {
        final @NotNull Transaction transaction = createTransaction(() -> {});
        final boolean[] status = new boolean[1];

        transaction.run();
        transaction.setAudioComplete(CompletionState.DONE);

        transaction.onControlComplete(() -> status[0] = true);
        assertTrue(status[0]);

        status[0] = false;

        transaction.onAudioComplete(() -> status[0] = true);
        assertTrue(status[0]);
    }
}