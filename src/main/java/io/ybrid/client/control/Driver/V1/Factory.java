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

package io.ybrid.client.control.Driver.V1;

import io.ybrid.client.control.Driver.Common.Driver;
import io.ybrid.client.control.Session;

public class Factory extends io.ybrid.client.control.Driver.Common.Factory {
    @Override
    public Driver getDriver(Session session) {
        return new io.ybrid.client.control.Driver.V1.Driver(session);
    }
}
