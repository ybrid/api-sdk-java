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

import io.ybrid.api.Session;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * This interface defines the communication channel from the {@link Session} to the player.
 */
public interface PlayerControl {
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
     * Get the list of media formats supported by the player.
     *
     * If this returns null no {@code Accept:}-header should be generated.
     * @return List of supported formats or null.
     */
    default @Nullable Map<String, Double> getAcceptedMediaFormats() {
        return null;
    }
}
