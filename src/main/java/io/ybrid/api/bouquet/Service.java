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

package io.ybrid.api.bouquet;

import io.ybrid.api.util.hasDisplayName;
import io.ybrid.api.util.hasIdentifier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * This interface is implemented by objects representing a service or "program".
 */
public interface Service extends hasIdentifier, hasDisplayName {
    /**
     * Get an icon that can be displayed by the user interface next to the display of the service.
     * @return Returns an URL to a icon for the service or null.
     */
    @Nullable
    URL getIcon();

    /**
     * This returns the genre of the service as a string that can be presented to the user.
     * @return Returns the genre of the service.
     */
    @Nullable
    String getGenre();

    /**
     * Gets a description of the service and it's program.
     *
     * @return The description or {@code null}.
     */
    default @Nullable String getDescription() {
        return null;
    }

    /**
     * A {@link URI} that the user can navigate to in order to find more information
     * about the service. This is normally the service's webpage.
     *
     * @return A URI to more information about the service or {@code null}.
     */
    default @Nullable URI getInfoURI() {
        return null;
    }


    /**
     * This implements a equality check for services to be used for overriding {@link Object#equals(Object)} in
     * implementing classes. This method works the same as {@link Objects#equals(Object, Object)}.
     *
     * @param a First service for equality check.
     * @param b Second services for equality check.
     * @return Whether the services are equal.
     * @see #hashCode(Service)
     */
    @Contract(value = "null, null -> true; null, !null -> false; !null, null -> false; _, _ -> _", pure = true)
    static boolean equals(@Nullable Service a, @Nullable Service b) {
        if (a == b)
            return true;
        if (a == null || b == null)
            return false;
        return a.getIdentifier().equals(b.getIdentifier());
    }

    /**
     * This implements a hashcode generation for services to be used for overriding {@link Object#hashCode()}
     * in implementing classes.
     *
     * @param o The service to obtain the hashcode of.
     * @return The hashcode of the services.
     * @see #equals(Service, Service)
     */
    @Contract(pure = true)
    static int hashCode(@NotNull Service o) {
        return Objects.hash(2053, o.getIdentifier());
    }
}
