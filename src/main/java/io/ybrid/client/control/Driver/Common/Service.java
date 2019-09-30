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

package io.ybrid.client.control.Driver.Common;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

abstract public class Service implements io.ybrid.client.control.Service {
    protected String identifier;
    protected URL icon;

    protected Service(JSONObject json) throws MalformedURLException {
        String value;

        identifier = json.getString("id");

        value = json.getString("iconURL");
        if (value != null && !value.isEmpty())
            icon = new URL(value);
    }

    protected Service() {
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public URL getIcon() {
        return icon;
    }
}
