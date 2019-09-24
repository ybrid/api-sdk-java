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

import org.json.JSONObject;

public class SwapInfo {
    private boolean nextSwapReturnsToMain;
    private int swapsLeft;

    SwapInfo(JSONObject json) {
        nextSwapReturnsToMain = json.getBoolean("nextSwapReturnsToMain");
        swapsLeft = json.getInt("swapsLeft");
    }

    public boolean isNextSwapReturnToMain() {
        return nextSwapReturnsToMain;
    }

    public int getSwapsLeft() {
        return swapsLeft;
    }

    private boolean canSwap() {
        return swapsLeft != 0;
    }
}
