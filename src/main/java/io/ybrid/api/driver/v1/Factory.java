/*
 * Copyright 2019 nacamar GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
