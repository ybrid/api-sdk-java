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

package io.ybrid.api;

/**
 * This enum lists all known workarounds.
 */
public enum Workaround {
    /**
     * Workaround used for servers sending invalid FQDNs.
     */
    WORKAROUND_BAD_FQDN,
    /**
     * Workaround used for servers that require POST bodies to be send as query string.
     */
    WORKAROUND_POST_BODY_AS_QUERY_STRING,
    /**
     * Workaround used for servers that send invalidly packed response to some commands.
     */
    WORKAROUND_BAD_PACKED_RESPONSE,
    /**
     * Workaround used for server sending negative time to next item.
     */
    WORKAROUND_NEGATIVE_TIME_TO_NEXT_ITEM,
    /**
     * Workaround used for servers that start audio transport with silence.
     * <P>
     * Note MP3 streams always start with silence. For servers only supporting this legacy codec
     * this workaround should be in the automatic or enabled state.
     */
    WORKAROUND_SKIP_SILENCE,
    /**
     * Workaround used for servers that provide services without a {@code DisplayName}.
     */
    WORKAROUND_SERVICE_WITH_NO_DISPLAY_NAME,
    /**
     * Workaround used for servers that provide an invalid playback URI.
     */
    WORKAROUND_INVALID_PLAYBACK_URI,
    /**
     * Workaround used to force failed {@link MediaProtocol} detection to assume {@link MediaProtocol#ICY}.
     */
    WORKAROUND_GUESS_ICY,
    /**
     * Workaround used for servers invalidly skipping {@link io.ybrid.api.bouquet.Bouquet}s.
     */
    WORKAROUND_NO_BOUQUET,
    /**
     * Workaround used for servers sending a default service that is not part of the bouquet.
     */
    WORKAROUND_DEFAULT_SERVICE_NOT_IN_BOUQUET;
}
