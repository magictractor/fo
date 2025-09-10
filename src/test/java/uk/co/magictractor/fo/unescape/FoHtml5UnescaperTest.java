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

public class FoHtml5UnescaperTest extends AbstractFoUnescaperTest {

    protected FoHtml5UnescaperTest() {
        super(new FoHtml5Unescaper());
    }

    @Test
    public void testUnescape_namedReferenceUnclosed() {
        check("&lt", "<");
    }

    @Test
    public void testUnescape_namedReferenceEmptyClosedAndUnclosed() {
        check("&lt;boo&gt", "<boo>");
    }

    @Test
    public void testUnescape_namedReferencePartialNotionUnclosed() {
        // Not sign.
        // https://www.compart.com/en/unicode/U+00AC
        check("&notion", "\u00acion");
    }

    @Test
    public void testUnescape_namedReferencePartialNotingUnclosed() {
        // Should use "&notin;" rather than the substring "&not;"
        // https://www.compart.com/en/unicode/U+2209
        check("&noting", "\u2209g");
    }

    @Test
    public void testUnescape_namedReferencePartialNoti() {
        // Will have a tree heading towards "&notin", but not get there.
        check("&noti;", "\u00aci;");
    }

    @Test
    public void testUnescape_decimalReferenceUnclosed() {
        check("&#87", "W");
    }

    @Test
    public void testUnescape_decimalReferenceClosedByNonDigit() {
        check("&#87a", "Wa");
    }

    @Test
    public void testUnescape_hexadecimalReferenceUnclosed() {
        check("&#xef", "\u00ef");
    }

    @Test
    public void testUnescape_hexadecimalReferenceClosedByNonDigit() {
        check("&#xefg", "\u00efg");
    }

    @Test
    public void testUnescape_numericReferenceBiggerThanMaxCodePoint() {
        check("&#1234567890;", REPLACEMENT_CHARACTER);
    }

    @Test
    public void testUnescape_numericReferenceBiggerThanMaxInt() {
        check("&#xabcdef0123;", REPLACEMENT_CHARACTER);
    }

    @Test
    public void testUnescape_numericReferenceZero() {
        check("&#0;", REPLACEMENT_CHARACTER);
    }

    @Test
    public void testUnescape_numericReferenceSurrogates() {
        check("&#xd799;", "\ud799");
        check("&#xd800;", REPLACEMENT_CHARACTER);
        check("&#xdfff;", REPLACEMENT_CHARACTER);
        check("&#xe000;", "\ue000");
    }

    @Test
    public void testUnescape_numericReferenceControlC1() {
        // Windows-1252 encoding is used for control C1 code points 0x80 to 0x9f.
        check("&#x80;", "\u20ac");
    }

}
