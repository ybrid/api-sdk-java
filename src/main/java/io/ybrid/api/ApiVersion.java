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

package io.ybrid.api;

/**
 * This enum is used to identify a specific version of the Ybrid® API.
 */
public enum ApiVersion {
    /**
     * Version 1 of the Ybrid® API.
     */
    V1,
    /**
     * Beta of version 2 of the Ybrid® API.
     */
    V2_BETA;

    /** Returns the enum constant of this type with the specified on-wire representation.
     *
     * This is like {@link #valueOf(String)} except that it uses the on-wire representation
     * as input.
     * @param input The on-wire representation to convert.
     * @return The corresponding enum value.
     */
    public static ApiVersion fromWire(String input) {
        switch (input) {
            case "v1":
                return V1;
            case "v2":
                return V2_BETA;
            default:
                throw new IllegalArgumentException("No enum constant for string \"" + input +"\"");
        }
    }
}
