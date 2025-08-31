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

import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.unescape.FoUnescaper;

public class FoUnescaperTest {

    @Test
    public void testUnescape_emptyString() {
        check("", "");
    }

    @Test
    public void testUnescape_entityOnly() {
        check("&amp;", "&");
    }

    @Test
    public void testUnescape_entityWithTextBefore() {
        check("abc&amp;", "abc&");
    }

    @Test
    public void testUnescape_entityWithTextAfter() {
        check("&amp;xyz", "&xyz");
    }

    @Test
    public void testUnescape_entityWithTextBeforeAndAfter() {
        check("1&lt;2", "1<2");
    }

    @Test
    public void testUnescape_multipleEntitiesAdjacent() {
        check("&quot;&amp;&quot;", "\"&\"");
    }

    @Test
    public void testUnescape_multipleEntitiesSeparated() {
        check("&apos; &amp; &apos;", "' & '");
    }

    @Test
    public void testUnescape_entityStructureOnly() {
        checkUnchanged("&unknown;");
    }

    @Test
    public void testUnescape_entityThenEntityStructure() {
        check("&lt;&unknown;", "<&unknown;");
    }

    @Test
    public void testUnescape_entityStructureThenEntity() {
        check("&unknown;&gt;", "&unknown;>");
    }

    @Test
    public void testUnescape_unclosedEntity() {
        checkUnchanged("&apos");
    }

    @Test
    public void testUnescape_unclosedSecondEntity() {
        check("&lt;boo&gt", "<boo&gt");
    }

    @Test
    public void testUnescape_empty() {
        checkUnchanged("&;");
    }

    @Test
    public void testUnescape_hexadecimalEmpty() {
        checkUnchanged("&#x;");
    }

    @Test
    public void testUnescape_hexadecimal2chars() {
        check("&#xe9;", "\u00e9");
    }

    @Test
    public void testUnescape_heaxadecimal3chars() {
        check("&#xabc;", "\u0abc");
    }

    @Test
    public void testUnescape_heaxadecimalUppercase() {
        check("&#xCDEF;", "\ucdef");
    }

    @Test
    public void testUnescape_heaxadecimalUppercaseX() {
        check("&#X1234;", "\u1234");
    }

    @Test
    public void testUnescape_heaxadecimalInvalid() {
        checkUnchanged("&#xpqr;");
    }

    @Test
    public void testUnescape_decimalEmpty() {
        checkUnchanged("&#;");
    }

    @Test
    public void testUnescape_decimalInvalid() {
        checkUnchanged("&#abcd");
    }

    @Test
    public void testUnescape_decimal1chars() {
        // tab char
        check("&#9;", "\t");
    }

    @Test
    public void testUnescape_decimal2chars() {
        check("&#90;", "Z");
    }

    @Test
    public void testUnescape_decimal3chars() {
        check("&#122;", "z");
    }

    private void checkUnchanged(String in) {
        check(in, in);
    }

    private void check(String in, String expectedOut) {
        String actualOut = new FoUnescaper().unescape(in);
        assertThat(actualOut).isEqualTo(expectedOut);
    }

}
