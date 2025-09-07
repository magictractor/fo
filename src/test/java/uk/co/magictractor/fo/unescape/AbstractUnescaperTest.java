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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;

/**
 * Tests that determine the supported features of {@code Unescaper}
 * implementations.
 */
public abstract class AbstractUnescaperTest {

    private static final String REPLACEMENT_CHARACTER = "\ufffd";

    private final Unescaper unescaper;

    protected AbstractUnescaperTest(Unescaper unescaper) {
        this.unescaper = unescaper;
    }

    @Test
    public void testUnescape_emptyString() {
        checkUnchanged("");
    }

    @Test
    public void testUnescape_whiteSpace() {
        checkUnchanged("  \t  \t");
    }

    /**
     * XML has five predefined entities: {@code &amp;}, {@code &lt;},
     * {@code &gt;}, {@code &apos;}, and {@code &quot;}.
     *
     * @see https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references#Standard_public_entity_sets_for_characters
     */
    @Test
    public void testUnescape_predefined_amp() {
        check("&amp;", "&");
    }

    @Test
    public void testUnescape_predefined_lt() {
        check("&lt;", "<");
    }

    @Test
    public void testUnescape_predefined_gt() {
        check("&gt;", ">");
    }

    /**
     * {@code &apos;} is a little unusual because it is one the the five
     * predefined names for XML, but is not included in HTML 4.
     *
     * @see https://www.w3.org/TR/html4/sgml/entities.html
     */
    @Test
    public void testUnescape_predefined_apos() {
        check("&apos;", "'", htmlVersion() == 5);
    }

    @Test
    public void testUnescape_predefined_quot() {
        check("&quot;", "\"");
    }

    @Test
    public void testUnescape_hexadecimal_lowercase() {
        check("&#xabcd;", "\uabcd");
    }

    @Test
    public void testUnescape_hexadecimal_uppercase() {
        check("&#xBCDE;", "\ubcde");
    }

    @Test
    public void testUnescape_hexadecimal_uppercaseX() {
        check("&#X1234;", "\u1234");
    }

    @Test
    public void testUnescape_hexadecimal_mixed_case() {
        check("&#xcDeF;", "\ucdef");
    }

    @Test
    public void testUnescape_decimal() {
        check("&#233;", "\u00e9");
    }

    @Test
    public void testUnescape_decimalNegative() {
        if (!issueNegativeDecimal()) {
            checkUnchanged("&#-4;");
        }
        else {
            // Bad behaviour.
            check("&#-4;", "\ufffc");
        }
    }

    /**
     * <p>
     * A comment on stackoverflow.com suggested that Apache Text has an issue
     * here. I wasn't able to replicate. And I couldn't find a resolved issue.
     * <ul>
     * <li>Commons Text 1.0 and Commons Lang 3.8.1, 3.0 passed the test suite,
     * including this test.</li>
     * <li>Commons Lang 2.0 throws NumberFormatExceptions for many tests because
     * it doesn't handle hexadecimal. But this test passes.</li>
     * </p>
     * <p>
     * Note that 2.0 does not have {@code unescapeHtml4()}, I tested with
     * {@code unescapeHtml()}.
     * </p>
     *
     * @see Nickkk comment on https://stackoverflow.com/a/994339
     */
    @Test
    public void testUnescape_preserveTags() {
        check("<p>&uuml;&egrave;</p>", "<p>\u00fc\u00e8</p>");
    }

    /**
     * If using something like JSoup's parseFragment, then whitespace might not
     * be preserved.
     */
    @Test
    public void testUnescape_preserveTagsWithWhiteSpace() {
        check("<p >&uuml;&egrave;<  /p > ", "<p >\u00fc\u00e8<  /p > ");
    }

    @Test
    public void testUnescape_preserveTagsWithAttributesAndWhiteSpace() {
        check("<p   a=\"b\">&uuml;&egrave;<  /p > ", "<p   a=\"b\">\u00fc\u00e8<  /p > ");
    }

    /**
     * And check single and double quotation marks are preserved (see comment on
     * testUnescape_preserve_tags()).
     */
    @Test
    public void testUnescape_preserveQuotationMarks() {
        check("\"Let's visit the caf&eacute;!\", she exclaimed", "\"Let's visit the caf\u00e9!\", she exclaimed");
    }

    /**
     * <p>
     * Zero is special, it gets mapped to {@code U+FFFD REPLACEMENT CHAR}, but
     * other control characters do not. With HTML 5, many C1 control characters
     * are mapped using the Windows-1252 encoding.
     * </p>
     * <p>
     * "If the number is 0x00, then this is a null-character-reference parse
     * error. Set the character reference code to 0xFFFD."
     * </p>
     */
    @Test
    public void testUnescape_zero() {
        String expected = htmlVersion() == 5 && !issueNoReplacementCharacterForZero() ? REPLACEMENT_CHARACTER : "\u0000";
        check("&#x00;", expected);
    }

    // Quirk seen when using a JSoup Parser.
    // See https://github.com/jhy/jsoup/discussions/2394
    @Test
    public void testUnescape_zeroZero() {
        String expected = htmlVersion() == 5 && !issueNoReplacementCharacterForZero() ? REPLACEMENT_CHARACTER : "\u0000";
        check("&#0;&#x00;", expected + expected);
    }

    // JSoup fails if using parseFragmentInput variant (for parser errors).
    // Fails with any null in the input.
    // See https://github.com/jhy/jsoup/issues/2395
    @Test
    public void testUnescape_inputZero() {
        checkUnchanged("\u0000");
    }

    @Test
    public void testUnescape_inputZeroZero() {
        checkUnchanged("\u0000\u0000");
    }

    @Test
    public void testUnescape_inputControlCharacters() {
        checkUnchanged("\u0001");
        checkUnchanged("\u0031");
        checkUnchanged("\u0080");
        checkUnchanged("\u009f");
    }

    @Test
    public void testUnescape_inputSurrogateCharacters() {
        checkUnchanged(Character.toString(Character.MIN_SURROGATE));
        checkUnchanged(Character.toString(Character.MAX_SURROGATE));
    }

    @Test
    public void testUnescape_controlCodesC0() {
        // Not 0x00, that has a distinct test (above).
        for (int i = 0x01; i <= 0x1f; i++) {
            checkControl(i);
        }
    }

    //   https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-state
    @Test
    public void testUnescape_controlCodesC1() {
        // TODO! check why 0x79 isn't a control char
        // checkControl(0x79);
        checkControl(0x80, '\u20ac');
        checkControl(0x81);
        checkControl(0x82, '\u201a');
        checkControl(0x83, '\u0192');
        checkControl(0x84, '\u201e');
        checkControl(0x85, '\u2026');
        checkControl(0x86, '\u2020');
        checkControl(0x87, '\u2021');
        checkControl(0x88, '\u02c6');
        checkControl(0x89, '\u2030');
        checkControl(0x8a, '\u0160');
        checkControl(0x8b, '\u2039');
        checkControl(0x8c, '\u0152');
        checkControl(0x8d);
        checkControl(0x8e, '\u017d');
        checkControl(0x8f);
        checkControl(0x90);
        checkControl(0x91, '\u2018');
        checkControl(0x92, '\u2019');
        checkControl(0x93, '\u201c');
        checkControl(0x94, '\u201d');
        checkControl(0x95, '\u2022');
        checkControl(0x96, '\u2013');
        checkControl(0x97, '\u2014');
        checkControl(0x98, '\u02dc');
        checkControl(0x99, '\u2122');
        checkControl(0x9a, '\u0161');
        checkControl(0x9b, '\u203a');
        checkControl(0x9c, '\u0153');
        checkControl(0x9d);
        checkControl(0x9e, '\u017e');
        checkControl(0x9f, '\u0178');
    }

    private void checkControl(int controlCodePoint) {
        if (Character.getType(controlCodePoint) != Character.CONTROL) {
            throw new IllegalArgumentException(controlCodePoint + " is not the code point of a control character");
        }

        String entity = "&#" + controlCodePoint + ";";
        String expected = new String(new char[] { (char) controlCodePoint });
        check(entity, expected);
    }

    /**
     * <pre>
    0x80    0x20AC  EURO SIGN (€)
    0x82    0x201A  SINGLE LOW-9 QUOTATION MARK (‚)
    0x83    0x0192  LATIN SMALL LETTER F WITH HOOK (ƒ)
    0x84    0x201E  DOUBLE LOW-9 QUOTATION MARK („)
    0x85    0x2026  HORIZONTAL ELLIPSIS (…)
    0x86    0x2020  DAGGER (†)
    0x87    0x2021  DOUBLE DAGGER (‡)
    0x88    0x02C6  MODIFIER LETTER CIRCUMFLEX ACCENT (ˆ)
    0x89    0x2030  PER MILLE SIGN (‰)
    0x8A    0x0160  LATIN CAPITAL LETTER S WITH CARON (Š)
    0x8B    0x2039  SINGLE LEFT-POINTING ANGLE QUOTATION MARK (‹)
    0x8C    0x0152  LATIN CAPITAL LIGATURE OE (Œ)
    0x8E    0x017D  LATIN CAPITAL LETTER Z WITH CARON (Ž)
    0x91    0x2018  LEFT SINGLE QUOTATION MARK (‘)
    0x92    0x2019  RIGHT SINGLE QUOTATION MARK (’)
    0x93    0x201C  LEFT DOUBLE QUOTATION MARK (“)
    0x94    0x201D  RIGHT DOUBLE QUOTATION MARK (”)
    0x95    0x2022  BULLET (•)
    0x96    0x2013  EN DASH (–)
    0x97    0x2014  EM DASH (—)
    0x98    0x02DC  SMALL TILDE (˜)
    0x99    0x2122  TRADE MARK SIGN (™)
    0x9A    0x0161  LATIN SMALL LETTER S WITH CARON (š)
    0x9B    0x203A  SINGLE RIGHT-POINTING ANGLE QUOTATION MARK (›)
    0x9C    0x0153  LATIN SMALL LIGATURE OE (œ)
    0x9E    0x017E  LATIN SMALL LETTER Z WITH CARON (ž)
    0x9F    0x0178  LATIN CAPITAL LETTER Y WITH DIAERESIS (Ÿ)
    </pre>
     */
    private void checkControl(int controlCodePoint, char windows1252Mapping) {
        if (Character.getType(controlCodePoint) != Character.CONTROL) {
            throw new IllegalArgumentException(controlCodePoint + " is not the code point of a control character");
        }

        String entity = "&#" + controlCodePoint + ";";
        char expectedChar = htmlVersion() == 5 ? windows1252Mapping : (char) controlCodePoint;
        String expected = new String(new char[] { expectedChar });
        check(entity, expected);
    }

    @Test
    public void testUnescape_minLeadingSurrogateMinusOne() {
        check("&#xd7ff;", "\ud7ff");
    }

    @Test
    public void testUnescape_leadingSurrogate() {
        String expected = htmlVersion() == 5 && !issueNoReplacementCharacterForSurrogate() ? REPLACEMENT_CHARACTER : "\ud800";
        check("&#xd800;", expected);
    }

    @Test
    public void testUnescape_trailingSurrogate() {
        String expected = htmlVersion() == 5 && !issueNoReplacementCharacterForSurrogate() ? REPLACEMENT_CHARACTER : "\udfff";
        check("&#xdfff;", expected);
    }

    @Test
    public void testUnescape_maxTrailingSurrogatePlusOne() {
        check("&#xe000;", "\ue000");
    }

    @Test
    public void testUnescape_maxChar() {
        check("&#xffff;", "\uffff");
    }

    @Test
    public void testUnescape_maxCharPlusOne() {
        if (!issueSingleChar()) {
            check("&#x10000;", "\ud800\udc00");
        }
        else {
            // Bad behaviour.
            check("&#x10000;", "\u0000");
        }
    }

    // https://html.spec.whatwg.org/multipage/parsing.html#numeric-character-reference-state
    // describes big number behaviour for HTML 5.
    // Unicode maximum is 1,114,112.
    // https://docs.oracle.com/cd/E19253-01/817-2521/overview-207/index.html
    @Test
    public void testUnescape_hexadecimalMax() {
        if (!issueSingleChar()) {
            check("&#x10ffff;", "\udbff\udfff");
        }
        else {
            // Bad behaviour.
            check("&#x10ffff;", "\uffff");
        }
    }

    @Test
    public void testUnescape_hexadecimalLargerThanMaxCodePoint() {
        // Maximum code point is 0x10FFFF.
        checkTooBig("&#x110000;");
    }

    @Test
    public void testUnescape_hexadecimalLargerThanMaxInt() {
        // Int is 4 bytes, so test with 5.
        checkTooBig("&#x1234567890;");
    }

    @Test
    public void testUnescape_decimalMax() {
        if (!issueSingleChar()) {
            check("&#1114111;", "\udbff\udfff");
        }
        else {
            // Bad behaviour.
            // 0x10ffff cast to char.
            check("&#1114111;", "\uffff");
        }
    }

    @Test
    public void testUnescape_decimalLargerThanMaxCodePoint() {
        // Maximum code point is 0x10FFFF = 1114111.
        checkTooBig("&#1114112;");
    }

    @Test
    public void testUnescape_decimalLargerThanMaxInt() {
        // Maximum code point is 0x10FFFF = 1114111.
        checkTooBig("&#9" + Integer.MAX_VALUE + "9;");
    }

    // Test to ensure there's no shortcut to U+FFFD when parsing a big number.
    @Test
    public void testUnescape_decimalTooBigThenInvalid() {
        check("&#1234567890z;", REPLACEMENT_CHARACTER + "z;", supportsEntityNameWithoutSemicolon());
    }

    // Test to ensure there's no shortcut to U+FFFD when parsing a big number.
    @Test
    public void testUnescape_hexadecimalTooBigThenInvalid() {
        check("&#x1234567890abcdefz;", REPLACEMENT_CHARACTER + "z;", supportsEntityNameWithoutSemicolon());
    }

    private void checkTooBig(String entity) {
        if (!issueBigNumberCrash() && !issueSingleChar()) {
            check(entity, REPLACEMENT_CHARACTER, htmlVersion() == 5);
        }
        else {
            checkBrokenTooBig(entity);
        }
    }

    private void checkBrokenTooBig(String entity) {
        BigInteger value;
        if (entity.startsWith("&#")) {
            if (entity.charAt(2) == 'x') {
                value = new BigInteger(entity.substring(3, entity.length() - 1), 16);
            }
            else {
                value = new BigInteger(entity.substring(2, entity.length() - 1));
            }
        }
        else {
            throw new IllegalArgumentException();
        }
        boolean isBiggerThanMaxInt = value.bitLength() > 32;

        // Apache crashes for values between 0x110000 and max int.
        if (issueBigNumberCrash()) {
            if (isBiggerThanMaxInt) {
                checkUnchanged(entity);
            }
            else {
                // Apache Text throws an exception.
                assertThatThrownBy(() -> unescaper.unescape(entity))
                        .isExactlyInstanceOf(IllegalArgumentException.class);
            }
        }

        // Spring only returns a single character.
        if (issueSingleChar()) {
            char singleCharValue = (char) value.intValue();
            if (value.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0) {
                check(entity, new String(new char[] { singleCharValue }));
            }
            else {
                // Spring uses Integer.parseInt() and does no substitution if the parse fails.
                checkUnchanged(entity);
            }
        }
    }

    @Test
    public void testUnescape_badlyFormedNumber() {
        // Missing "x", so badly formed and cannot be replaced.
        checkUnchanged("&#abcd;");
    }

    // https://www.w3.org/TR/html4/sgml/entities.html
    @Test
    public void testUnescape_name_html4() {
        check("&copy;", "\u00a9");
        check("&eacute;", "\u00e9");
        check("&bull;", "\u2022");
        check("&otimes;", "\u2297");
        check("&spades;", "\u2660");
    }

    // https://html.spec.whatwg.org/multipage/named-characters.html
    @Test
    public void testUnescape_name_html5() {
        check("&rarrtl;", "\u21a3", htmlVersion() == 5);
        check("&boxUL;", "\u255d", htmlVersion() == 5);
        check("1&lesg;2", "1\u22da\ufe002", htmlVersion() == 5);
    }

    @Test
    public void testUnescape_hexadecimalWithoutSemicolon() {
        check("&#x12abg", "\u12abg", supportsEntityNameWithoutSemicolon());
    }

    @Test
    public void testUnescape_decimalWithoutSemicolon() {
        check("&#123a", "\u007ba", supportsEntityNameWithoutSemicolon());
    }

    @Test
    public void testUnescape_nameWithoutSemicolon() {
        check("&#123a", "\u007ba", supportsEntityNameWithoutSemicolon());
    }

    /**
     * <p>
     * Spaces should have been removed from values of some entities according to
     * the spec, but are still present in the W3C entity declarations.
     * </p>
     * <blockquote>Earlier versions of this specification defined these entities
     * with the replacement text starting with a space, to avoid the possibility
     * that the expansion of the entity combined with preceding text. However
     * for various reasons the entities as incorporated in HTML do not have a
     * space here, and so the definitions now consist just of the combining
     * character so that HTML and XHTML are consistent with any specifications
     * using these definitions.</blockquote>
     * <p>
     * This test is to guard against implementations still having the spaces.
     * </p>
     *
     * @see https://www.w3.org/TR/xml-entity-names/#chars_math-combining-tables
     */
    @Test
    public void testUnescape_noLeadingSpace() {
        check("&DownBreve;", "\u0311", htmlVersion() == 5);
        check("&tdot;", "\u20db", htmlVersion() == 5);
        check("&TripleDot;", "\u20db", htmlVersion() == 5);
        check("&DotDot;", "\u20dc", htmlVersion() == 5);
    }

    /**
     * {@code &lang;} and {@code &rang;} are the only entities that have
     * different values in HTML4 and HTML5.
     */
    @Test
    public void testUnescape_langAndRang() {
        // Value mismatch for lang, Html4 has 〈 (9001), Html5 has ⟨ (10216)
        // Value mismatch for rang, Html4 has 〉 (9002), Html5 has ⟩ (10217)
        if (htmlVersion() == 5) {
            check("&lang;", "\u27e8");
            check("&rang;", "\u27e9");
        }
        else {
            check("&lang;", "\u2329");
            check("&rang;", "\u232a");
        }
    }

    /**
     * <p>
     * The HTML version determines the entity names that are expected to be
     * supported and some expected behaviour.
     * </p>
     * <p>
     * HTML 5 behaviours are more clearly defined, although some libraries do
     * not implement everything in the specification, such as mappings to
     * {@code U+FFFD REPLACEMENT CHARACTER}.
     * </p>
     *
     * @return 4 or 5
     */
    abstract int htmlVersion();

    // TODO! check expected entity set

    /**
     * The HTML 5 specification allows the entity names to be inferred without a
     * semicolon.
     */
    protected boolean supportsEntityNameWithoutSemicolon() {
        return htmlVersion() == 5;
    }

    /**
     * <p>
     * Numbers that are too big should be tolerated. The HTML 5 spec says they
     * should be mapped to {@code U+FFFD REPLACEMENT CHARACTER}.
     * </p>
     * <p>
     * However, Apache throws an IllegalArgumentException when using
     * {@code StringEscapeUtils.unescapeHtml4()}.
     * </p>
     * <p>
     * See code in {@code NumericEntityUnescaper.translate()}.
     */
    // Integer.parseInt() is used and the value passed to Character.toChars(codePoint)
    // but that throws an exception if the code point passed is larger than the maximum 0x10ffff.
    protected boolean issueBigNumberCrash() {
        return false;
    }

    /**
     * <p>
     * A Unicode code point has a value up to 0x10ffff and can be mapped to two
     * characters (a surrogate pair).
     * </p>
     * <p>
     * Spring does not do this, it simply casts the value of a numeric entity to
     * a char. It should use {@code Character.toChars(codePoint)}.
     */
    protected boolean issueSingleChar() {
        return false;
    }

    /**
     * <p>
     * Negative decimals should not be treated as numbers, so {@code &#-2;}
     * should remain unchanged.
     * <p>
     * <p>
     * However, Spring reads the int and then casts it to a char.
     * </p>
     */
    protected boolean issueNegativeDecimal() {
        return false;
    }

    /**
     * <p>
     * HTML5 parsers should return {@code U+FFFD} for zero values numbered
     * character references such as {@code &#x00;}.
     * </p>
     * <p>
     * JSoup does not do this (also not for surrogate characters).
     * </p>
     */
    protected boolean issueNoReplacementCharacterForZero() {
        return false;
    }

    /**
     * <p>
     * HTML5 parsers should return {@code U+FFFD} for values that correspond to
     * surrogate characters in numbered character references such as
     * {@code &#x0d800;}.
     * </p>
     * <p>
     * JSoup does not do this (also not for zero).
     * </p>
     */
    protected boolean issueNoReplacementCharacterForSurrogate() {
        return false;
    }

    @Test
    public void testUnescape_name_unknown() {
        checkUnchanged("&unknown;");
    }

    private void check(String entity, String expectedReplacement, boolean isSupported) {
        check0(entity, isSupported ? expectedReplacement : entity);
    }

    private void checkUnchanged(String entity) {
        check0(entity, entity);
    }

    private void check(String entity, String expectedReplacement) {
        if (entity.equals(expectedReplacement)) {
            throw new IllegalArgumentException("Args are equal, use checkUnchanged() instead");
        }
        check0(entity, expectedReplacement);
    }

    private void check0(String entity, String expectedReplacement) {
        String actual = unescaper.unescape(entity);
        // assertThat(actual).isEqualTo(expectedReplacement);
        if (!actual.equals(expectedReplacement)) {
            // Assertions.fail();
            assertThat(uString(actual)).isEqualTo(uString(expectedReplacement));
            // Fallback in case uString() makes them look equal (such as returning "BIG").
            assertThat(actual).isEqualTo(expectedReplacement);
        }
    }

    private String uString(String str) {
        if (str.length() == 1) {
            return uString(str.charAt(0));
        }
        else if (str.length() == 2) {
            return uString(str.charAt(0)) + uString(str.charAt(1));
        }
        //throw new IllegalStateException("Code needs modification to handle string of length " + str.length());
        // return "BIG";
        return str;
    }

    private String uString(char c) {
        return String.format("\\u%04x", (int) c);
    }

}
