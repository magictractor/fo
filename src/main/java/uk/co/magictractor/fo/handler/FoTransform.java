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
package uk.co.magictractor.fo.handler;

import java.io.OutputStream;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.FopFactory;
import org.xml.sax.ContentHandler;

/**
 *
 */
public interface FoTransform {

    // UserAgent may contain metadata such as the document title.
    ContentHandler createHandler(OutputStream out, FopFactory fopFactory, FOUserAgent userAgent);

    /**
     * <p>
     * A file extension typically associated with transformed output. Users may
     * chose to use a different extension.
     * </p>
     * <p>
     * The extension must include a leading dot.
     * </p>
     */
    String fileExtension();

    /**
     * <p>
     * A mime type associated with transformed output.
     * </p>
     */
    // Note: text/asciidoc
    // see https://discuss.asciidoctor.org/Mimetype-for-Asciidoc-td211.html
    // and https://docs.asciidoctor.org/asciidoc/latest/faq/#whats-the-media-type-aka-mime-type-for-asciidoc
    String mimeType();

}
