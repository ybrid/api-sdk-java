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
import java.io.InputStream;

/**
 * This class is an abstract parent for classes that implement {@link InputStream}
 * with the extension of support for query for the Content-Type.
 */
abstract public class StreamInputStream extends InputStream {
    /**
     * Get the current Content-Type for the stream,
     * @return Returns the Content-type of the stream as {@link String}.
     * @throws IOException Thrown on any I/O-Error.
     */
    public abstract String getContentType() throws IOException;
}
