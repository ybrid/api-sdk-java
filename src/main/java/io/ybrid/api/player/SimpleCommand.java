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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * This enum implements basic player control commands.
 */
public enum SimpleCommand implements Command<SimpleCommand> {
    /**
     * Prepares the player as if calling a standard {@code prepare()}-method.
     */
    PREPARE(false),
    /**
     * Starts playback.
     */
    PLAY(true),
    /**
     * Stops playback.
     */
    STOP(true);

    private final boolean hasAudioAction;

    SimpleCommand(boolean hasAudioAction) {
        this.hasAudioAction = hasAudioAction;
    }

    @ApiStatus.Experimental
    @Override
    public boolean hasAudioAction() {
        return hasAudioAction;
    }

    @Override
    public void assertArgumentListValid(@Nullable Serializable[] arguments) throws IllegalArgumentException {
        if (arguments.length != 0)
            throw new IllegalArgumentException("Command does not accept parameters");
    }
}
