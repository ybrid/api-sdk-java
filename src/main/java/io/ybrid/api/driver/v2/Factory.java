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

package io.ybrid.api.driver.v2;

import io.ybrid.api.*;
import io.ybrid.api.bouquet.Bouquet;
import io.ybrid.api.driver.common.Driver;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public final class Factory extends io.ybrid.api.driver.common.Factory {
    @Override
    public @NotNull Driver getDriver(@NotNull Session session) {
        return new io.ybrid.api.driver.v2.Driver(session);
    }

    @Override
    public Bouquet getBouquet(@NotNull Server server, @NotNull Alias alias) throws IOException {
        final Driver driver = getDriver(server.createSession(alias));
        Bouquet bouquet = null;
        IOException thrown = null;

        driver.connect();
        try {
            if (!driver.hasChanged(SubInfo.BOUQUET))
                driver.refresh(SubInfo.BOUQUET);

            bouquet = driver.getBouquet();
        } catch (IOException e) {
            thrown = e;
        }
        driver.disconnect();

        if (thrown != null)
            throw thrown;

        return bouquet;
    }
}
