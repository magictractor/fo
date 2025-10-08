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
package uk.co.magictractor.fo;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.co.magictractor.fo.namespace.DefaultNamespaces;

public class DomUtilTest {

    @Test
    public void t() {
        Document doc = createMetadataDocument();
        //  DomUtil.findOrCreateChild(null, null, null, null, null);
        Element root = (Element) doc.getFirstChild();
        // TODO! build Namespaces from the DOM.
        DomUtil.findChild(root, DefaultNamespaces.get().fo().qName("declarations"));
    }

    private Document createMetadataDocument() {
        return DomUtil.parseXml(
            "<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">" +
                    "<fo:declarations xmlns:x=\"adobe:ns:meta/\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"  xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:xmp=\"http://ns.adobe.com/xap/1.0/\"  xmlns:pdf=\"http://ns.adobe.com/pdf/1.3/\">"
                    +
                    "<x:xmpmeta>" +
                    "<rdf:RDF>" +
                    "<rdf:Description>" +
                    "<dc:title>TITLE</dc:title>" +
                    "<xmp:Keywords>KEYWORDS</xmp:Keywords>" +
                    "</rdf:Description>" +
                    "</rdf:RDF>" +
                    "</x:xmpmeta>" +
                    "</fo:declarations>" +
                    "</fo:root>");
    }

}
