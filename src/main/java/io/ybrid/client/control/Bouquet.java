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

package io.ybrid.client.control;

import java.util.Collection;

public class Bouquet {
    private Service defaultService;
    private Collection<Service> services;

    public Bouquet(Service defaultService, Collection<Service> services) {
        if (!services.contains(defaultService))
            throw new IllegalArgumentException("Default Service not part of Services");

        this.defaultService = defaultService;
        this.services = services;
    }

    public Service getDefaultService() {
        return defaultService;
    }

    public Collection<Service> getServices() {
        return services;
    }
}
