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

package io.ybrid.api.metadata;

import io.ybrid.api.transaction.GenericCommand;
import io.ybrid.api.transaction.Request;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * This interface is implemented by Companions.
 *
 * Companions are visual elements to be displayed alongside Items that are currently playing.
 */
public interface Companion {
    /**
     * Returns an text that should be used as alternative to the image. It may be displayed while the image
     * is still loading of after loading failed. It can also be displayed if a graphical representation is not
     * possible for some reason.
     * @return Returns an alternative text for the Companion.
     */
    @Nullable String getAlternativeText();

    /**
     * Returns the expected Height of the Companion.
     * @return Returns the height in [px].
     */
    int getHeight();

    /**
     * Returns the expected Width of the Companion.
     * @return Returns the width in [px].
     */
    int getWidth();

    /**
     * Returns the number of this Companion as part of the set of all Companions on the Item.
     * @return Returns the index of the Companion.
     */
    int getSequenceNumber();

    /**
     * Returns the URI of the Companion that should be rendered.
     * The resource can be pre-fetched and cached.
     * @return Returns the URI of the Companion.
     */
    @Nullable URI getStaticResourceURI();

    /**
     * This returns the URI to navigate the user to when the Companion is clicked.
     * @return Returns the URI to send the user to.
     */
    @Nullable URI getOnClickURI();

    /**
     * Returns the URI that should be contacted once the Companion is viewed.
     *
     * @return Returns the URI to be called when the companion is viewed.
     * @see #createOnViewRequest()
     */
    @Nullable URI getOnViewURI();

    /**
     * This creates a {@link Request} to be ran once the Companion was rendered.
     * This "pings" the {@link URI} as it would be returned by {@link #getOnViewURI()}.
     * This will do nothing if no URI is set. Therefore it is safe to always call this.
     *
     * @see #getOnViewURI()
     */
    default Request<?> createOnViewRequest() {
        final @Nullable URI uri = getOnViewURI();
        if (uri == null) {
            return GenericCommand.NOOP.makeRequest();
        } else {
            return GenericCommand.PING_REQUEST.makeRequest(uri);
        }
    }
}
