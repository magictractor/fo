/**
 * Copyright 2024 Ken Dobson
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
package uk.co.magictractor.fo.handler.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;

/**
 *
 */
public class TextSubstitutionFilterTest {

    private static final String TITLE = "Title";

    @Test
    public void testCharacters_noSub() throws SAXException {
        checkSubstitution("Mary had a little lamb");
    }

    @Test
    public void testCharacters_noSubSingleChar() throws SAXException {
        checkSubstitution("1");
    }

    @Test
    public void testCharacters_noSubEmptyString() throws SAXException {
        checkSubstitution("", true);
    }

    @Test
    public void testCharacters_subVarOnly() throws SAXException {
        checkSubstitution("${metadata.title}");
    }

    @Test
    public void testCharacters_subMiddle() throws SAXException {
        checkSubstitution("abc${metadata.title}xyz");
    }

    @Test
    public void testCharacters_subStart() throws SAXException {
        checkSubstitution("${metadata.title}xyz");
    }

    @Test
    public void testCharacters_subEnd() throws SAXException {
        checkSubstitution("abc${metadata.title}");
    }

    @Test
    public void testCharacters_subMulti() throws SAXException {
        checkSubstitution("${metadata.title}pqr${metadata.title}");
    }

    @Test
    public void testCharacters_subMultiAdjacent() throws SAXException {
        checkSubstitution("${metadata.title}${metadata.title}${metadata.title}");
    }

    @Test
    public void testCharacters_noSuchVar() throws SAXException {
        checkSubstitution("abc${no.such.var}xyz");
    }

    @Test
    public void testCharacters_noSuchVarThenVar() throws SAXException {
        checkSubstitution("abc${no.such.var}${metadata.title}xyz");
    }

    public void checkSubstitution(String s) throws SAXException {
        checkSubstitution(s, false);
    }

    public void checkSubstitution(String s, boolean allowEmpty) throws SAXException {
        CaptureCharactersHandler capture = new CaptureCharactersHandler();
        if (allowEmpty) {
            capture.allowEmpty();
        }
        TextSubstitutionFilter filter = new TextSubstitutionFilter(createDocument(), capture);

        char[] ch = s.toCharArray();
        filter.characters(ch, 0, ch.length);

        String expected = s.replace("${metadata.title}", TITLE)
                .replace("${no.such.var}", "");
        assertThat(capture.capturedCharacters()).isEqualTo(expected);
    }

    private FoDocument createDocument() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataTitle(TITLE)
                .build();
        return doc;
    }

}
