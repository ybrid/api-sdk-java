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

import io.ybrid.api.SubInfo;
import io.ybrid.api.SwapMode;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.metadata.ItemType;
import io.ybrid.api.transaction.Request;
import io.ybrid.api.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;
import java.util.Objects;

public enum Command implements io.ybrid.api.transaction.Command<Command> {
    /**
     * Connects the {@link Session}.
     */
    CONNECT(false),
    /**
     * Disconnects the {@link Session}.
     */
    DISCONNECT(false),
    /**
     * Requests a initial transport being connected.
     */
    CONNECT_INITIAL_TRANSPORT(true),
    /**
     * Requests a new transport to be connected. This can be used
     * e.g. when the connection to the current transport was lost.
     */
    RECONNECT_TRANSPORT(true),
    /**
     * Requests the refresh of a set of {@link SubInfo}.
     */
    REFRESH(false, true, new Class[]{EnumSet.class, Identifier.class}),
    /**
     * Winds back to the live position of the stream.
     */
    WIND_TO_LIVE(true),
    /**
     * Winds to a specific {@link Instant} on the stream.
     */
    WIND_TO(true, true, Instant.class),
    /**
     * Winds by a specific {@link Duration} on the stream.
     */
    WIND_BY(true, true, Duration.class),
    /**
     * Skips forward to next item of a given {@link ItemType} or the next item if {@code null}.
     */
    SKIP_FORWARD(true, false, ItemType.class),
    /**
     * Skips backwards to previous item of a given {@link ItemType} or the previous item if {@code null}.
     */
    SKIP_BACKWARD(true, false, ItemType.class),
    /**
     * Swaps the item using the given {@link SwapMode}.
     */
    SWAP_ITEM(true, true, SwapMode.class),
    /**
     * Swaps back to the main item.
     */
    SWAP_TO_MAIN_ITEM(true),
    /**
     * Swaps to the given {@link Service}.
     */
    SWAP_SERVICE(true, true, new Class[]{Service.class, Identifier.class}),
    /**
     * Swaps back to the main {@link Service}.
     */
    SWAP_TO_MAIN_SERVICE(true);

    private final boolean hasAudioAction;
    private final boolean argumentNotNull;
    private final @Nullable Class<?>[] argumentTypes;

    Command(boolean hasAudioAction, boolean argumentNotNull, @NotNull Class<?>[] argumentTypes) {
        this.hasAudioAction = hasAudioAction;
        this.argumentNotNull = argumentNotNull;
        this.argumentTypes = argumentTypes;
    }

    Command(boolean hasAudioAction, boolean argumentNotNull, @NotNull Class<?> argumentType) {
        this(true, argumentNotNull, new Class[]{argumentType});
    }

    Command(boolean hasAudioAction) {
        this.hasAudioAction = hasAudioAction;
        this.argumentNotNull = false;
        this.argumentTypes = null;
    }

    private void assertArgumentCount(int got) {
        if (argumentTypes == null) {
            if (got != 0)
                throw new IllegalArgumentException("No arguments expected");

            return;
        }

        if (got != 1)
            throw new IllegalArgumentException("Unexpected number of arguments for command " + this + ": got " + got + " but expected 1");
    }

    @ApiStatus.Experimental
    @Override
    public boolean hasAudioAction() {
        return hasAudioAction;
    }

    @Override
    public void assertArgumentListValid(@Nullable Serializable[] arguments) throws IllegalArgumentException {
        assertArgumentCount(arguments.length);

        if (arguments.length == 0)
            return;

        if (arguments[0] == null) {
            if (argumentNotNull) {
                throw new IllegalArgumentException("Invalid null argument: " + this + " does not accept null as argument");
            } else {
                return;
            }
        }

        //noinspection NullableProblems
        for (final @NotNull Class<?> type : Objects.requireNonNull(argumentTypes)) {
            if (type.isInstance(arguments[0]))
                return;
        }
        throw new IllegalArgumentException("Invalid type passed for command " + this);
    }


    @Override
    public @NotNull Request<Command> makeRequest(@Nullable Serializable argument) throws IllegalArgumentException {
        if (argument instanceof Service)
            argument = ((Service) argument).getIdentifier();
        return io.ybrid.api.transaction.Command.super.makeRequest(argument);
    }
}
