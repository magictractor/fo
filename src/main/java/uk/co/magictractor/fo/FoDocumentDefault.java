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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    // TODO! will likely initially be held in a template.
    // template also contains defaults for "h1" etc.
    // should be able to add Fonts for a single document.
    @Override
    public List<URL> getFontUrls() {
        List<URL> fontUrls = new ArrayList<>();
        try {
            // TODO! URI constructors (see deprecation in Java 20)
            // fontUrls.add(new URL("file:pdf/fonts/OpenSans-Regular.ttf"));
            fontUrls.add(new URI("file:pdf/fonts/OpenSans-Regular.ttf").toURL());
            fontUrls.add(new URL("file:pdf/fonts/OpenSans-Bold.ttf"));
            fontUrls.add(new URL("file:pdf/fonts/OpenSans-Italic.ttf"));
            // temp typo
            fontUrls.add(new URL("file:pdf/fonts/OpenSans-BoldItalicsTypo.ttf"));
            // temp duplicate
            fontUrls.add(new URL("file:pdf/fonts/OpenSans-Bold.ttf"));
            // error??
            fontUrls.add(new URL("file:////pdf/fonts/OpenSans-Bold.ttf"));
            // temp .TTC (multiple fonts
            fontUrls.add(new URL("file:/c:/windows/fonts/msgothic.ttc"));
            fontUrls.add(new URL("file:/c:/windows/fonts/cambria.ttc"));
            fontUrls.add(new URL("file:/c:/windows/fonts/nirmala.ttc"));
        }
        catch (URISyntaxException | MalformedURLException e) {
            throw new IllegalStateException(e);
        }
        return fontUrls;
    }

}
