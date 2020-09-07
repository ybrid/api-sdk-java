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

package io.ybrid.api.driver.ybrid.v2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

final class Service implements io.ybrid.api.bouquet.Service {
    private final @NotNull String identifier;
    private final @Nullable URL icon;

    public Service(@NotNull JSONObject json) throws MalformedURLException {
        String value;

        identifier = json.getString("id");

        value = json.getString("iconURL");
        if (value != null && !value.isEmpty()) {
            icon = new URL(value);
        } else {
            icon = null;
        }
    }

    @Override
    public @NotNull String getIdentifier() {
        return identifier;
    }

    @Override
    public @Nullable URL getIcon() {
        return icon;
    }

    @Override
    public @NotNull String getDisplayName() {
        return identifier;
    }

    @Override
    public String getGenre() {
        return null;
    }
}
