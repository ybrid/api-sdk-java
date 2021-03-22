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

import java.util.Objects;

/**
 * This {@link RuntimeException} is used to signal exceptions occurred while executing a transaction.
 */
public class TransactionExecutionException extends RuntimeException {
    private static final long serialVersionUID = -5026503491845225503L;

    private final @NotNull Transaction transaction;

    /**
     * Creates a new exception with the given transaction.
     * @param transaction The transaction.
     */
    public TransactionExecutionException(@NotNull Transaction transaction) {
        super(Objects.requireNonNull(transaction.getError()));
        this.transaction = transaction;
    }

    /**
     * Creates a new exception with the given transaction.
     * @param transaction The transaction.
     * @param message The message to use.
     */
    public TransactionExecutionException(@NotNull Transaction transaction, @NotNull String message) {
        super(message, Objects.requireNonNull(transaction.getError()));
        this.transaction = transaction;
    }

    /**
     * Gets the transaction the exception happened on.
     * @return The transaction the exception happened on.
     */
    public @NotNull Transaction getTransaction() {
        return transaction;
    }
}
