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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.time.Duration;

/**
 * This enum implements generic commands.
 */
public enum GenericCommand implements Command<GenericCommand> {
    /**
     * Sleeps for a given {@link Duration}.
     */
    SLEEP(new Class[][]{new Class[]{Duration.class}});

    private final @NotNull Class<? extends Serializable>[][] validArguments;

    GenericCommand(@NotNull Class<? extends Serializable>[][] validArguments) {
        this.validArguments = validArguments;
    }

    @Override
    public boolean hasAudioAction() {
        return false;
    }

    @Override
    public void assertArgumentListValid(@Nullable Serializable[] arguments) throws IllegalArgumentException {
        outer:
        for (final @Nullable Class<? extends Serializable>[] args : validArguments) {
            if (arguments == null && args == null)
                return;

            if (arguments == null)
                continue;

            if (arguments.length != args.length)
                continue;

            for (int i = 0; i < arguments.length; i++) {
                if (args[i] == null)
                    continue outer;

                if (!args[i].isInstance(arguments[i]))
                    continue outer;
            }

            return;
        }

        throw new IllegalArgumentException();
    }

    @ApiStatus.Internal
    public static Transaction createTransaction(@NotNull Request<GenericCommand> request) {
        switch (request.getCommand()) {
            case SLEEP:
                return new RequestBasedTransaction<Request<GenericCommand>>(request) {
                    @Override
                    protected void execute() throws Throwable {
                        Thread.sleep(((Duration)getRequest().getArgumentNotNull(0)).toMillis());
                    }
                };
            default:
                throw new IllegalStateException("Unexpected value: " + request.getCommand());
        }
    }
}
