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

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.magictractor.fo.entityset.EntitySets;
import uk.co.magictractor.fo.entityset.EntityTree;

public class FoHtml5Unescaper extends AbstractFoUnescaper {

    private static final Log LOG = LogFactory.getLog(FoHtml5Unescaper.class);

    // int forms of char values
    private static final int MIN_SURROGATE = Character.MIN_SURROGATE;
    private static final int MAX_SURROGATE = Character.MAX_SURROGATE;

    // 0x80 to 0x9f are mapped to Windows-1252 characters rather than the ANSI control codes.
    private static final String CONTROL_CODE_C1_MAPPINGS = "" +
            "\u20ac\u0081\u201a\u0192\u201e\u2026\u2020\u2021" +
            "\u02c6\u2030\u0160\u2039\u0152\u008d\u017d\u008f" +
            "\u0090\u2018\u2019\u201c\u201d\u2022\u2013\u2014" +
            "\u02dc\u2122\u0161\u203a\u0153\u009d\u017e\u0178";

    private static final String REPLACEMENT_CHARACTER = "\ufffd";

    private final EntityTree entityTree;

    public FoHtml5Unescaper() {
        this(EntitySets.html5());
    }

    public FoHtml5Unescaper(EntityTree entityTree) {
        this.entityTree = entityTree;
    }

    public FoHtml5Unescaper(Map<String, String> entitySet) {
        this(new EntityTree(entitySet));
    }

    @Override
    public String unescape(String text) {
        // Do this up front so that the common case of no substitution required is satisfied quickly.
        int ampersandIndex = text.indexOf("&");
        if (ampersandIndex == -1) {
            return text;
        }

        Context context = new Context(text, ampersandIndex);

        boolean hasPossibleCharacterReference;
        do {
            parseCharacterReference(context);

            hasPossibleCharacterReference = context.copyToNextAmpersand();
        } while (hasPossibleCharacterReference);

        return context.out.toString();
    }

    // https://html.spec.whatwg.org/multipage/parsing.html#character-reference-state
    private void parseCharacterReference(Context context) {
        LOG.trace("parseCharacterReference() " + context);
        if (!context.hasMore()) {
            // The ampersand is at the end of the input.
            context.copySelectedText();
        }
        else if (context.nextChar() == '#') {
            parseNumericCharacterReference(context);
        }
        else {
            context.revertChar();
            parseNamedCharacterReference(context);
        }
    }

    private void parseNamedCharacterReference(Context context) {
        EntityTree subtree = entityTree;
        EntityTree bestTree = null;
        char c = ';';
        while (context.hasMore()) {
            LOG.trace(context);
            c = context.nextChar();
            if (c == ';') {
                // set flag?
                break;
            }
            subtree = subtree.getSubtree(c);
            if (subtree == null) {
                break;
            }
            if (subtree.getValue() != null) {
                bestTree = subtree;
                context.mark();
            }
        }

        if (bestTree == null) {
            context.copySelectedText();
        }
        else {
            context.write(bestTree.getValue());
            if (subtree != bestTree) {
                context.reset();
            }
            else if (c != ';') {
                context.revertChar();
            }
        }
    }

    // https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-state
    private void parseNumericCharacterReference(Context context) {
        if (!context.hasMore()) {
            // The hash is at the end of the input.
            context.copySelectedText();
            return;
        }

        int codePoint;
        char c = context.nextChar();
        if (c == 'x' || c == 'X') {
            codePoint = parseHexadecimalCharacterReference(context);
        }
        else {
            context.revertChar();
            codePoint = parseDecimalCharacterReference(context);
        }

        if (codePoint == -1) {
            LOG.trace("nah");
            context.copySelectedText();
        }
        else if (codePoint > Character.MAX_CODE_POINT) {
            LOG.trace("too big");
            // Too big.
            context.write(REPLACEMENT_CHARACTER);
        }
        else {
            LOG.trace("codePoint=" + codePoint);
            context.write(convertCodePoint(codePoint));
        }
    }

    private int parseDecimalCharacterReference(Context context) {
        int codePoint = 0;
        boolean tooBig = false;
        int digit = -1;
        while (context.hasMore()) {
            char c = context.nextChar();

            if (c >= '0' && c <= '9') {
                digit = c - '0';
            }
            else {
                if (c != ';') {
                    // missing-semicolon-after-character-reference
                    LOG.trace("missing semicolon in decimal");
                    context.revertChar();
                }
                if (digit == -1) {
                    // No digits seen.
                    // absence-of-digits-in-numeric-character-reference
                    codePoint = -1;
                }
                break;
            }

            if (!tooBig) {
                codePoint = codePoint * 10 + digit;
                if (codePoint > Character.MAX_CODE_POINT) {
                    // Too big, but there could be an invalid character before the end,
                    // so continue to check remaining characters.
                    tooBig = true;
                }
            }
        }

        return codePoint;
    }

    // https://html.spec.whatwg.org/multipage/parsing.html#hexadecimal-character-reference-start-state
    // https://html.spec.whatwg.org/multipage/parsing.html#hexadecimal-character-reference-state
    private int parseHexadecimalCharacterReference(Context context) {
        int codePoint = 0;
        boolean tooBig = false;
        int nibble = -1;
        while (context.hasMore()) {
            char c = context.nextChar();
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
                if (c != ';') {
                    // missing-semicolon-after-character-reference
                    context.revertChar();
                }
                break;
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

        if (nibble == -1) {
            // No digits seen.
            // absence-of-digits-in-numeric-character-reference
            codePoint = -1;
        }

        return codePoint;
    }

    /**
     * If the HTML 5 specification were followed strictly, then some code points
     * should be mapped to {@code U+FFFD REPLACEMENT CHARACTER}, including
     * {@code 0x00}, surrogates and some control characters.
     *
     * @see https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-end-state
     */
    @Override
    protected String convertCodePoint(int codePoint) {
        if (codePoint == 0x00) {
            return REPLACEMENT_CHARACTER;
        }
        if (codePoint >= MIN_SURROGATE && codePoint <= MAX_SURROGATE) {
            return REPLACEMENT_CHARACTER;
        }
        if (codePoint >= 0x80 && codePoint <= 0x9f) {
            int index = codePoint - 0x80;
            return CONTROL_CODE_C1_MAPPINGS.substring(index, index + 1);
        }

        return super.convertCodePoint(codePoint);
    }

    private static final class Context {
        private final String in;
        private final int inLength;
        private final StringBuilder out;

        private int fromIndex;
        private int toIndex;
        // Used with mark() and reset() like InputStreams.
        private int markIndex = -1;

        Context(String in, int firstAmpersandIndex) {
            this.in = in;
            this.inLength = in.length();
            // Output is likely to be slightly smaller than the input,
            // so use in.length() as the capacity.
            this.out = new StringBuilder(in.length());

            this.fromIndex = firstAmpersandIndex;
            this.toIndex = firstAmpersandIndex + 1;

            if (firstAmpersandIndex > 0) {
                write(in.substring(0, firstAmpersandIndex));
            }
        }

        boolean hasMore() {
            return toIndex < inLength;
        }

        char nextChar() {
            LOG.trace("context.nextChar() -> '" + in.charAt(toIndex) + "'");
            return in.charAt(toIndex++);
        }

        void revertChar() {
            toIndex--;
        }

        void write(String text) {
            LOG.trace("context.write() text=\"" + text + "\"");
            out.append(text);
            fromIndex = toIndex;
            // Not setting toIndex, copyToNextAmpersand() will be used next.
        }

        void copySelectedText() {
            if (toIndex == inLength) {
                out.append(in.substring(fromIndex));
                if (LOG.isTraceEnabled()) {
                    LOG.trace("context.copyPeekedText() text=\"" + in.substring(fromIndex) + "\"");
                }
            }
            else {
                // TODO! use more efficient append (avoid substring) (and above)
                // out.append(in.substring(fromIndex, toIndex + 1));
                out.append(in, fromIndex, toIndex + 1);
                if (LOG.isTraceEnabled()) {
                    LOG.trace("context.copyPeekedText() text=\"" + in.substring(fromIndex, toIndex + 1) + "\"");
                }
            }
            fromIndex = toIndex + 1;
        }

        boolean copyToNextAmpersand() {
            if (fromIndex == inLength + 1) {
                LOG.trace("context.copyToNextAmpersand() already at end, nothing to copy");
                return false;
            }
            int ampersandIndex = in.indexOf('&', fromIndex);
            if (ampersandIndex == -1) {
                LOG.trace("context.copyToNextAmpersand() no ampersand");
                out.append(in.substring(fromIndex));
                return false;
            }
            else {
                LOG.trace("context.copyToNextAmpersand() copying \"" + in.substring(fromIndex, ampersandIndex) + "\"");
                out.append(in.substring(fromIndex, ampersandIndex));
                fromIndex = ampersandIndex;
                toIndex = ampersandIndex + 1;
                return true;
            }
        }

        void mark() {
            markIndex = toIndex;
        }

        void reset() {
            fromIndex = markIndex;
        }

        @Override
        public String toString() {
            ToStringHelper helper = MoreObjects.toStringHelper(this)
                    .add("in", in)
                    .add("fromIndex", toStringForIndex(fromIndex))
                    .add("toIndex", toStringForIndex(toIndex));
            if (markIndex != -1) {
                helper.add("markIndex", toStringForIndex(markIndex));
            }
            return helper.toString();
        }

        private String toStringForIndex(int index) {
            return index + "[" + (index < inLength ? in.charAt(index) : "EOF") + "]";
        }
    }

}
