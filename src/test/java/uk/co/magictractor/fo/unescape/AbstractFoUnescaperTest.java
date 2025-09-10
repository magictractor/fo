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

import org.junit.jupiter.api.Test;

public abstract class AbstractFoUnescaperTest extends AbstractUnescaperTest {

    protected AbstractFoUnescaperTest(Unescaper unescaper) {
        super(unescaper);
    }

    @Test
    public void testUnescape_emptyString() {
        checkUnchanged("");
    }

    @Test
    public void testUnescape_referenceWithTextBefore() {
        check("abc&amp;", "abc&");
    }

    @Test
    public void testUnescape_referenceWithTextAfter() {
        check("&amp;xyz", "&xyz");
    }

    @Test
    public void testUnescape_referenceWithTextBeforeAndAfter() {
        check("1&lt;2", "1<2");
    }

    @Test
    public void testUnescape_multipleReferencesAdjacent() {
        check("&quot;&amp;&quot;", "\"&\"");
    }

    @Test
    public void testUnescape_multipleReferencesSeparated() {
        check("&lt; &amp; &gt;", "< & >");
    }

    @Test
    public void testUnescape_namedReferenceUnknown() {
        checkUnchanged("&unknown;");
    }

    @Test
    public void testUnescape_namedReferenceKnownThenUnknown() {
        check("&lt;&unknown;", "<&unknown;");
    }

    @Test
    public void testUnescape_namedReferenceUnknownThenKnown() {
        check("&unknown;&gt;", "&unknown;>");
    }

    @Test
    public void testUnescape_ampersandOnly() {
        checkUnchanged("&");
    }

    @Test
    public void testUnescape_ampersandAndSemicolon() {
        checkUnchanged("&;");
    }

    @Test
    public void testUnescape_numericReferenceStart() {
        checkUnchanged("&#");
    }

    @Test
    public void testUnescape_hexadecimalReferenceStart() {
        checkUnchanged("&#x");
    }

    @Test
    public void testUnescape_hexadecimalReferenceEmpty() {
        checkUnchanged("&#x;");
    }

    @Test
    public void testUnescape_hexadecimalReference2chars() {
        check("&#xe9;", "\u00e9");
    }

    @Test
    public void testUnescape_heaxadecimalReference3chars() {
        check("&#xabc;", "\u0abc");
    }

    @Test
    public void testUnescape_heaxadecimalReferenceUppercase() {
        check("&#xCDEF;", "\ucdef");
    }

    @Test
    public void testUnescape_heaxadecimalReferenceUppercaseX() {
        check("&#X1234;", "\u1234");
    }

    @Test
    public void testUnescape_heaxadecimalReferenceInvalid() {
        checkUnchanged("&#xpqr;");
    }

    @Test
    public void testUnescape_decimalReferenceEmpty() {
        checkUnchanged("&#;");
    }

    @Test
    public void testUnescape_decimalReferenceInvalid() {
        checkUnchanged("&#abcd");
    }

    @Test
    public void testUnescape_decimalReference1chars() {
        // tab char
        check("&#9;", "\t");
    }

    @Test
    public void testUnescape_decimalReference2chars() {
        check("&#90;", "Z");
    }

    @Test
    public void testUnescape_decimalReference3chars() {
        check("&#122;", "z");
    }

}
