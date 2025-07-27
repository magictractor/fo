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
package uk.co.magictractor.fo.intent;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.ReaderInputStream;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.co.magictractor.fo.indent.FoIndent;

/**
 *
 */
public class FoIndentTest {

    @Test
    public void testOfString_space() throws Exception {
        FoIndent actual = FoIndent.of("    ");

        assertThat(actual.indentChar()).isEqualTo(' ');
        assertThat(actual.indentSize()).isEqualTo(4);
    }

    @Test
    public void testOfString_tab() throws Exception {
        FoIndent actual = FoIndent.of("\t");

        assertThat(actual.indentChar()).isEqualTo('\t');
        assertThat(actual.indentSize()).isEqualTo(1);
    }

    @Test
    public void testInfer_child4spaces() throws Exception {
        String xml = "<parent>\n" +
                "    <child/>\n" +
                "</parent>\n";

        checkInfer(xml, ' ', 4);
    }

    @Test
    public void testInfer_child2spaces() throws Exception {
        String xml = "<parent>\n" +
                "  <child/>\n" +
                "</parent>\n";

        checkInfer(xml, ' ', 2);
    }

    @Test
    public void testInfer_child1tab() throws Exception {
        String xml = "<parent>\n" +
                "\t<child/>\n" +
                "</parent>\n";

        checkInfer(xml, '\t', 1);
    }

    private void checkInfer(String xml, char expectedIndentChar, int expectedIndentSize) throws Exception {
        FoIndent actual = FoIndent.infer(parse(xml));

        assertThat(actual.indentChar()).isEqualTo(expectedIndentChar);
        assertThat(actual.indentSize()).isEqualTo(expectedIndentSize);
    }

    private Document parse(String xml) throws SAXException, IOException, ParserConfigurationException {
        try (InputStream in = new ReaderInputStream(new StringReader(xml), StandardCharsets.US_ASCII)) {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            // return DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(in);
        }
    }

}
