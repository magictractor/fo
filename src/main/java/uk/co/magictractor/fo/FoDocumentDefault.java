/**
 * Copyright 2023 Ken Dobson
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

import org.w3c.dom.Document;

/**
 */
//https://xmlgraphics.apache.org/fop/2.9/embedding.html
//XSL-FO spec https://www.w3.org/TR/xsl11/#fo-section
//Examples
//https://www.data2type.de/en/xml-xslt-xslfo/xsl-fo/xsl-fo-introduction/blocks#c430
//
// Instances should be created using FoDocumentBuilder
/* default */ class FoDocumentDefault implements FoDocument {

    // https://xmlgraphics.apache.org/fop/trunk/embedding.html

    private final Document domDocument;
    private FoMetadata foMetadata;

    public FoDocumentDefault(Document domDocument) {
        this.domDocument = domDocument;
    }

    @Override
    public Document getDomDocument() {
        return domDocument;
    }

    @Override
    public FoMetadata getMetadata() {
        if (foMetadata == null) {
            foMetadata = new FoMetadataDom(domDocument);
        }
        return foMetadata;
    }

}
