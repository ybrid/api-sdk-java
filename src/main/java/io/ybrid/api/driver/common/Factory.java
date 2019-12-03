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

package io.ybrid.api.driver.common;

import io.ybrid.api.Alias;
import io.ybrid.api.Bouquet;
import io.ybrid.api.Server;
import io.ybrid.api.Session;

/**
 * This implements a Factory for drivers. This should not be used directly.
 */
abstract public class Factory {
    /**
     * Gets a driver instance.
     * @param session The {@link Session} to return a driver for.
     * @return Returns the new instance of the driver.
     */
    public abstract Driver getDriver(Session session);

    /**
     * Get the current {@link Bouquet} from the server.
     * @param server The {@link Server} to use.
     * @param alias The {@link Alias} to use.
     * @return Returns the {@link Bouquet} as returned by the server.
     */
    public abstract Bouquet getBouquet(Server server, Alias alias);
}
