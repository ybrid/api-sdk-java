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

final class Response {
    private final JSONObject root;
    private final JSONObject responseHeader;
    private final JSONObject responseObject;

    public Response(@NotNull JSONObject root) {
        this.root = root;
        this.responseHeader = root.getJSONObject("__responseHeader");
        this.responseObject = root.getJSONObject("__responseObject");
    }

    public String getToken() {
        final String sessionId = responseObject.getString("sessionId");
        if (sessionId == null)
            throw new NullPointerException();
        return sessionId;
    }

    public boolean getValid() {
        return responseObject.getBoolean("valid");
    }

    @Nullable
    protected JSONObject getRawBouquet() {
        if (!responseObject.has("bouquet"))
            return null;
        return responseObject.getJSONObject("bouquet");
    }

    @Nullable
    protected JSONObject getRawMetadata() {
        if (!responseObject.has("metadata"))
            return null;
        return responseObject.getJSONObject("metadata");
    }

    @Nullable
    protected JSONObject getRawPlayout() {
        if (!responseObject.has("playout"))
            return null;
        return responseObject.getJSONObject("playout");
    }

    @Nullable
    protected JSONObject getRawSwapInfo() {
        if (!responseObject.has("swapInfo"))
            return null;
        return responseObject.getJSONObject("swapInfo");
    }
}
