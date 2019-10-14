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
