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

package io.ybrid.api;

import java.util.Collection;

/**
 * This class represents a Bouquet. A Bouquet is a collection of {@link Service Services} provided
 * to the user by the server.
 */
public class Bouquet {
    private Service defaultService;
    private Collection<Service> services;

    /**
     * Creates a new Bouquet object.
     * @param defaultService The default {@link Service} of the Bouquet. Must be part of {@code services}.
     * @param services The collection of all {@link Service Services} in this Bouquet.
     */
    public Bouquet(Service defaultService, Collection<Service> services) {
        if (!services.contains(defaultService))
            throw new IllegalArgumentException("Default Service not part of Services");

        this.defaultService = defaultService;
        this.services = services;
    }

    /**
     * Gets the default {@link Service} from this Bouquet.
     * @return Returns the default {@link Service}.
     */
    public Service getDefaultService() {
        return defaultService;
    }

    /**
     * Returns the collection of {@link Service Services} known in this Bouquet.
     * @return Returns the collection of {@link Service Services}.
     */
    public Collection<Service> getServices() {
        return services;
    }
}
