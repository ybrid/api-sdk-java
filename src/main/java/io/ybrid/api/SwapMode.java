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

/**
 * This enum is used to identify the mode of a swap.
 */
public enum SwapMode {
    /**
     * Beginning of alternative content will be skipped to fit to the left main items duration.
     */
    END2END("end2end"),
    /**
     * Alternative content starts from the beginning and will become faded out at the end.
     */
    FADE2END("fade2end");

    private final String onWire;

    SwapMode(String onWire) {
        this.onWire = onWire;
    }

    public String getOnWire() {
        return onWire;
    }
}
