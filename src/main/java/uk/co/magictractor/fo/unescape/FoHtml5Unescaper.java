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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.magictractor.fo.entityset.EntitySets;

public class FoHtml5Unescaper extends AbstractFoUnescaper {

    private static final Log LOG = LogFactory.getLog(FoHtml5Unescaper.class);

    private static final String REPLACEMENT_CHARACTER = "\ufffd";

    public FoHtml5Unescaper() {
        this(EntitySets.html5());
    }

    public FoHtml5Unescaper(Map<String, String> entitySet) {
        super(entitySet);
    }

    @Override
    public String unescape(String text) {
        // Do these up front so that the common case of no substitution required is satisfied quickly.
        int ampersandIndex = text.indexOf("&");
        if (ampersandIndex == -1) {
            return text;
        }

        int semicolonIndex = text.indexOf(";", ampersandIndex + 1);
        if (semicolonIndex == -1) {
            return text;
        }

        StringBuilder sb = null;
        int copyFromIndex = 0;
        while (true) {
            String entityName = text.substring(ampersandIndex + 1, semicolonIndex);
            String replacement = unescapeEntity(entityName);
            if (replacement != null) {
                if (copyFromIndex == 0) {
                    sb = new StringBuilder();
                }
                sb.append(text.substring(copyFromIndex, ampersandIndex));
                sb.append(replacement);
                copyFromIndex = semicolonIndex + 1;
            }

            ampersandIndex = text.indexOf("&", semicolonIndex + 1);
            if (ampersandIndex == -1) {
                break;
            }

            semicolonIndex = text.indexOf(";", ampersandIndex + 1);
            if (semicolonIndex == -1) {
                break;
            }
        }

        if (copyFromIndex == 0) {
            return text;
        }

        sb.append(text.substring(copyFromIndex));

        return sb.toString();
    }

    protected String unescapeEntity(String entityName) {
        if (entityName.isEmpty()) {
            return null;
        }

        if (entityName.charAt(0) == '#') {
            if (entityName.length() > 1 && (entityName.charAt(1) == 'x' || entityName.charAt(1) == 'X')) {
                return unescapeHexadecimal(entityName.substring(2));
            }
            return unescapeDecimal(entityName.substring(1));
        }
        return unescapeName(entityName);
    }

    // If too big then return U+FFFD REPLACEMENT CHARACTER
    // https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-state
    protected String unescapeHexadecimal(String hex) {
        if (hex.isEmpty()) {
            // Entity was "&#x;"
            return null;
        }

        int codePoint = 0;
        boolean tooBig = false;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            int nibble;
            if (c >= '0' && c <= '9') {
                nibble = c - '0';
            }
            else if (c >= 'a' && c <= 'f') {
                nibble = c - 0x57;
            }
            else if (c >= 'A' && c <= 'F') {
                nibble = c - 0x37;
            }
            else {
                // Invalid.
                return null;
            }

            if (!tooBig) {
                codePoint = codePoint << 4 | nibble;
                if (codePoint > Character.MAX_CODE_POINT) {
                    // Too big, but there could be an invalid character before the end,
                    // so continue to check remaining characters.
                    tooBig = true;
                }
            }
        }

        return tooBig ? REPLACEMENT_CHARACTER : new String(Character.toChars(codePoint));
    }

    // If too big then return U+FFFD REPLACEMENT CHARACTER
    // https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-state
    protected String unescapeDecimal(String decimal) {
        if (decimal.isEmpty()) {
            // Entity was "&#;"
            return null;
        }

        int codePoint = 0;
        boolean tooBig = false;
        for (int i = 0; i < decimal.length(); i++) {
            char c = decimal.charAt(i);
            if (c < '0' || c > '9') {
                // Invalid.
                return null;
            }

            if (!tooBig) {
                codePoint = codePoint * 10 + (c - '0');
                if (codePoint > Character.MAX_CODE_POINT) {
                    // Too big, but there could be an invalid character before the end,
                    // so continue to check remaining characters.
                    tooBig = true;
                }
            }
        }

        return tooBig ? REPLACEMENT_CHARACTER : convertCodePoint(codePoint);
    }

    /**
     * If the HTML 5 specification were followed strictly, then some code points
     * should be mapped to {@code U+FFFD REPLACEMENT CHARACTER}, including
     * {@code 0x00}, surrogates and some control characters.
     *
     * @see https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-end-state
     */
    protected String convertCodePoint(int codePoint) {
        // Character.isSurrogate(0); or use constants

        return new String(Character.toChars(codePoint));
    }

}
