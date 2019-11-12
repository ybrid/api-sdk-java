/*
 * Copyright (c) 2019 nacamar GmbH - YBRID®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.driver.v1;

import io.ybrid.api.Service;
import io.ybrid.api.*;
import io.ybrid.api.driver.common.Driver;

import java.util.ArrayList;

/**
 * This implements the {@link io.ybrid.api.driver.common.Factory} for version 1 API.
 */
public class Factory extends io.ybrid.api.driver.common.Factory {
    @Override
    public Driver getDriver(Session session) {
        return new io.ybrid.api.driver.v1.Driver(session);
    }

    @Override
    public Bouquet getBouquet(Server server, Alias alias) {
        Service service = new io.ybrid.api.driver.v1.Service();
        ArrayList<Service> services = new ArrayList<>();
        services.add(service);
        return new Bouquet(service, services);
    }
}
