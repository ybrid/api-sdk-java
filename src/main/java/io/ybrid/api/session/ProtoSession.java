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

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.ItemType;
import io.ybrid.api.transaction.SessionTransaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;

/**
 * This class is used internally by {@link Session} and must not be used otherwise.
 */
public abstract class ProtoSession implements Connectable, SessionClient {
    public abstract @NotNull SessionTransaction createTransaction(@NotNull Request request);

    private void executeRequestAsTransaction(@NotNull Request request) throws IOException {
        final @NotNull SessionTransaction transaction = createTransaction(request);
        final @Nullable Throwable error;

        transaction.run();
        error = transaction.getError();

        if (error == null)
            return;

        if (error instanceof IOException)
            throw (IOException)error;

        throw new IOException(error);
    }

    @Override
    public void disconnect() {
        try {
            executeRequestAsTransaction(Command.DISCONNECT.makeRequest());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connect() throws IOException {
        executeRequestAsTransaction(Command.CONNECT.makeRequest());
    }


    @Override
    public void refresh(@NotNull SubInfo what) throws IOException {
        refresh(EnumSet.of(what));
    }

    @Override
    public void refresh(@NotNull EnumSet<SubInfo> what) throws IOException {
        executeRequestAsTransaction(Command.REFRESH.makeRequest(what));
    }

    @Override
    public void windToLive() throws IOException {
        executeRequestAsTransaction(Command.WIND_TO_LIVE.makeRequest());
    }

    @Override
    public void windTo(@NotNull Instant timestamp) throws IOException {
        executeRequestAsTransaction(Command.WIND_TO.makeRequest(timestamp));
    }

    @Override
    public void wind(@NotNull Duration duration) throws IOException {
        executeRequestAsTransaction(Command.WIND_BY.makeRequest(duration));
    }

    @Override
    public void skipForwards(ItemType itemType) throws IOException {
        executeRequestAsTransaction(Command.SKIP_FORWARD.makeRequest(itemType));
    }

    @Override
    public void skipBackwards(ItemType itemType) throws IOException {
        executeRequestAsTransaction(Command.SKIP_BACKWARD.makeRequest(itemType));
    }

    @Override
    public void swapItem(SwapMode mode) throws IOException {
        executeRequestAsTransaction(Command.SWAP_ITEM.makeRequest(mode));
    }

    @Override
    public void swapService(@NotNull Service service) throws IOException {
        executeRequestAsTransaction(Command.SWAP_SERVICE.makeRequest(service));
    }

    @Override
    public void swapToMain() throws IOException {
        executeRequestAsTransaction(Command.SWAP_TO_MAIN_SERVICE.makeRequest());
    }
}
