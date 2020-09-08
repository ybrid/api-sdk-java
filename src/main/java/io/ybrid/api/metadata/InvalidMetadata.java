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

package io.ybrid.api.metadata;

import io.ybrid.api.Identifier;
import io.ybrid.api.bouquet.Service;
import io.ybrid.api.bouquet.SimpleService;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This implements a invalid metadata class.
 * This can be used by classes that must provide
 * metadata by their contracts but do not (yet) have the information
 * to do so.
 * <P>
 * Any two instances of this class will not be equal to allow to signal
 * changes of metadata even if the change is from one invalid state to another.
 */
public final class InvalidMetadata implements Metadata {
    private final @NotNull Service service;
    private final @NotNull Item currentItem = new SimpleItem(new Identifier());

    /**
     * Main constructor.
     *
     * @param service The service to use for this metadata.
     */
    public InvalidMetadata(@NotNull Service service) {
        this.service = service;
    }

    /**
     * This constructs a new instance with a random service.
     *
     * @see #InvalidMetadata(Service)
     */
    public InvalidMetadata() {
        this(new SimpleService());
    }

    @Contract(pure = true)
    @Override
    public @NotNull Item getCurrentItem() {
        return currentItem;
    }

    @Contract(pure = true)
    @Override
    public @Nullable Item getNextItem() {
        return null;
    }

    @Contract(pure = true)
    @Override
    public @NotNull Service getService() {
        return service;
    }

    @Contract(pure = true)
    @Override
    public boolean isValid() {
        return false;
    }
}
