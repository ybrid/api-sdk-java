/*
 * Copyright (c) 2021 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
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

package io.ybrid.api.driver.ybrid.common;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

final class Companion extends io.ybrid.api.driver.common.Companion {
    private static String getString(@NotNull JSONObject json, @NotNull String key) {
        String ret;

        if (json.isNull(key))
            return null;

        ret = json.getString(key);
        if (ret.isEmpty())
            return null;

        return ret;
    }

    private static URI getURI(@NotNull JSONObject json, @NotNull String key) throws URISyntaxException {
        String string = getString(json, key);
        if (string == null)
            return null;

        return new URI(string);
    }

    Companion(@NotNull JSONObject json) throws URISyntaxException {
        alternativeText = getString(json, "altText");
        height = json.getInt("height");
        width = json.getInt("width");
        sequenceNumber = json.getInt("sequenceNumber");
        staticResource = getURI(json, "staticResourceURL");
        onClick = getURI(json, "onClickThroughURL");
        onView = getURI(json, "onCreativeViewURL");
    }
}
