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

package io.ybrid.api.session;

import io.ybrid.api.metadata.ItemType;
import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertThrows;

public class CommandTest {
    @Test
    public void makeRequestNoArg() {
        Command.CONNECT.makeRequest();
        assertThrows(IllegalArgumentException.class, Command.SKIP_FORWARD::makeRequest);
    }

    @Test
    public void makeRequestOneArg() {
        assertThrows(IllegalArgumentException.class, () -> Command.CONNECT.makeRequest((Serializable) null));
        Command.SKIP_FORWARD.makeRequest((Serializable) null);

        assertThrows(IllegalArgumentException.class, () -> Command.CONNECT.makeRequest(ItemType.COMEDY));
        Command.SKIP_FORWARD.makeRequest(ItemType.COMEDY);
    }
}