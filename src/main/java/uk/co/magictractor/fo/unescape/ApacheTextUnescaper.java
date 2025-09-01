/**
 * Copyright 2025 Ken Dobson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.magictractor.fo.unescape;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.CharSequenceTranslator;

/**
 * Known issue: throws an {@code IllegalArgumentException} for numeric entities
 * with values greater than {@code Character.MAX_CODE_POINT} and less than or
 * equal to {Integer.MAX_VALUE}. See
 * <a href="https://issues.apache.org/jira/browse/TEXT-44" target=
 * "_blank">https://issues.apache.org/jira/browse/TEXT&#x2011;44</a>.
 */
public class ApacheTextUnescaper implements Unescaper {

    private final CharSequenceTranslator translator;

    /**
     * <p>
     * Calls {@link #ApacheTextUnescaper(CharSequenceTranslator)} with
     * {@link StringEscapeUtils#UNESCAPE_HTML4}. This gives the same behaviour
     * as {link StringEscapeUtils#unescapeHtml4()}.
     * </p>
     * <p>
     * Semicolons are required to terminate entities. A alternative
     * {@code CharSequenceTranslator} could be created permitting missing
     * semicolons on a {@code NumericEntityUnescaper}, but semicolons would
     * still be required if using the provided {@code LookupTranslator}s for
     * named entities. It might be possible to work around this by creating new
     * translators, but if tolerating missing semicolons is desired, it might be
     * better to use one of the other {@link Unescaper} implementations.
     * </p>
     * <p>
     * Additional named entities could easily be added by created a new
     * {@code AggregateTranslator} based on
     * {@code StringEscapeUtils.UNESCAPE_HTML4} containing an additional (or
     * replacement) {@code LookupTranslator}.
     * </p>
     */
    public ApacheTextUnescaper() {
        this(StringEscapeUtils.UNESCAPE_HTML4);
    }

    /**
     * <p>
     * Creates an instance using the given {@code CharSequenceTranslator}. This
     * could be one of the constants in {@link StringEscapeUtils}, or it could
     * be a custom {@CharSequenceTranslator}.
     * </p>
     * <p>
     * Additional entity names can be mapped by creating a new
     * {@link AggregateTranslator} containing another {@link LookupTranslator}.
     * Refer to the provided translators in {@link StringEscapeUtils} if
     * considering this.
     * </p>
     */
    // TODO! create a unit test confirming that its easy to map other names. Do that after work on EntitySets.
    public ApacheTextUnescaper(CharSequenceTranslator translator) {
        this.translator = translator;
    }

    @Override
    public String unescape(String text) {
        return translator.translate(text);
    }

}
