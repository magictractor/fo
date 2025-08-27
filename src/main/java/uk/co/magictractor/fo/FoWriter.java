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

import java.net.URL;
import java.util.List;
import java.util.function.Function;

import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;

import org.xml.sax.ContentHandler;

import uk.co.magictractor.fo.config.FoConfig;
import uk.co.magictractor.fo.config.FoWriterFontDetector;
import uk.co.magictractor.fo.handler.HasLexicalHandler;

/**
 * Instances of {@code FoWriter} are usually created using
 * {@code FoWriterBuilder} but the constructors are public so that the use of
 * builders is not mandatory.
 */
public class FoWriter {

    private final FoConfig foConfig;
    private final Function<FoDocument, ContentHandler> contentHandlerFunction;

    public FoWriter(FoConfig foConfig, Function<FoDocument, ContentHandler> contentHandlerFunction) {
        this.foConfig = foConfig;
        this.contentHandlerFunction = contentHandlerFunction;
    }

    // TODO! move to writer package
    // TODO! rename this method to write()
    public void dump(FoDocument foDocument) {
        List<URL> fontUrls = foDocument.getFontUrls();
        if (fontUrls == null || fontUrls.isEmpty()) {
            write0(foDocument);
        }
        else {
            try {
                FoWriterFontDetector.setFontUrls(fontUrls);
                write0(foDocument);
            }
            finally {
                FoWriterFontDetector.reset();
            }
        }
    }

    private void write0(FoDocument foDocument) {
        ContentHandler handler = contentHandlerFunction.apply(foDocument);

        SAXResult result = new SAXResult(handler);
        // LexicalHandler can be null.
        // It is typically used if capturing intermediate XSL-FO with comments.
        result.setLexicalHandler(HasLexicalHandler.getLexicalHandler(handler));

        try {
            foConfig.getTransformer().transform(new DOMSource(foDocument.getDomDocument()), result);
        }
        catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

}
