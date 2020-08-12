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

import java.io.IOException;
import java.net.URL;

/**
 * This interface is implemented by Companions.
 *
 * Companions are visual elements to be displayed alongside Items that are currently playing.
 */
public interface Companion {
    /**
     * This should be called once the Companion was rendered.
     * @throws IOException Thrown if there are any I/O-Errors.
     */
    void onView() throws IOException;

    /**
     * Returns an text that should be used as alternative to the image. It may be displayed while the image
     * is still loading of after loading failed. It can also be displayed if a graphical representation is not
     * possible for some reason.
     * @return Returns an alternative text for the Companion.
     */
    String getAlternativeText();

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
     * Returns the URL of the Companion that should be rendered.
     * The resource can be pre-fetched and cached.
     * @return Returns the URL of the Companion.
     */
    URL getStaticResource();

    /**
     * This returns the URL to navigate the user to when the Companion is clicked.
     * @return Returns the URL to send the user to.
     */
    URL getOnClick();

    /**
     * Returns the URL that should be contacted once the Companion is viewed.
     *
     * See also {@link #onView()}.
     *
     * @return Returns the URL to be called when the companion is viewed.
     */
    URL getOnView();
}
