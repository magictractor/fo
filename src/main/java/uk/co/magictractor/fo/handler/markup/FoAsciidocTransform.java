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
package uk.co.magictractor.fo.handler.markup;

import java.io.OutputStream;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.ContentHandler;

import uk.co.magictractor.fo.handler.FoTransform;

/**
 *
 */
public class FoAsciidocTransform implements FoTransform {

    @Override
    public ContentHandler createHandler(OutputStream out, FopFactory fopFactory, FOUserAgent userAgent) {
        return new MarkupHandlerAdapter(new FoAsciidocHandler(out));
    }

    // See https://docs.asciidoctor.org/asciidoc/latest/faq/#whats-the-media-type-aka-mime-type-for-asciidoc
    @Override
    public String fileExtension() {
        return ".adoc";
    }

    @Override
    public String mimeType() {
        return "text/asciidoc";
    }

}
