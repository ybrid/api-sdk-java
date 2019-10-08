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
