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

package io.ybrid.api.driver.v1;

import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

final class Companion extends io.ybrid.api.driver.common.Companion {
    private static String getString(JSONObject json, String key) {
        String ret;

        if (json.isNull(key))
            return null;

        ret = json.getString(key);
        if (ret.isEmpty())
            return null;

        return ret;
    }

    private static URL getURL(JSONObject json, String key) throws MalformedURLException {
        String string = getString(json, key);
        if (string == null)
            return null;

        return new URL(string);
    }

    Companion(JSONObject json) throws MalformedURLException {
        alternativeText = getString(json, "altText");
        height = json.getInt("height");
        width = json.getInt("width");
        sequenceNumber = json.getInt("sequenceNumber");
        staticResource = getURL(json, "staticResourceURL");
        onClick = getURL(json, "onClickThroughURL");
        onView = getURL(json, "onCreativeViewURL");
    }
}
