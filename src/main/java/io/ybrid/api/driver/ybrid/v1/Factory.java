/*
 * Copyright (c) 2019 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.driver.ybrid.v1;

import io.ybrid.api.Alias;
import io.ybrid.api.Server;
import io.ybrid.api.Session;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.bouquet.SimpleService;
import io.ybrid.api.driver.common.Driver;
import org.jetbrains.annotations.NotNull;

/**
 * This implements the {@link io.ybrid.api.driver.common.Factory} for version 1 API.
 */
public final class Factory extends io.ybrid.api.driver.common.Factory {
    @Override
    public @NotNull Driver getDriver(@NotNull Session session) {
        return new io.ybrid.api.driver.ybrid.v1.Driver(session);
    }

    @Override
    public @NotNull Bouquet getBouquet(@NotNull Server server, @NotNull Alias alias) {
        return new Bouquet(new SimpleService());
    }
}