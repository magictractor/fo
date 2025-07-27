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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

import uk.co.magictractor.fo.handler.HasLexicalHandler;

/**
 * Base class for filter implementations that delegates all methods to another
 * handler. Filter implementations will typically extend this and override a
 * small number of methods.
 */
public class BaseContentHandlerFilter implements ContentHandler, HasLexicalHandler {

    private final ContentHandler wrapped;
    private final LexicalHandler wrappedLexical;

    public BaseContentHandlerFilter(ContentHandler wrapped) {
        this.wrapped = wrapped;
        // Hmm... this could change after the filter has been created. See BroadcastContentHandler.
        // Do something more robust??
        this.wrappedLexical = HasLexicalHandler.getLexicalHandler(wrapped);
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        return wrappedLexical;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        wrapped.setDocumentLocator(locator);
    }

    @Override
    public void startDocument()
            throws SAXException {
        wrapped.startDocument();
    }

    @Override
    public void endDocument()
            throws SAXException {
        wrapped.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        wrapped.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix)
            throws SAXException {
        wrapped.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName,
            String qName, Attributes attributes)
            throws SAXException {
        wrapped.startElement(uri, localName, qName, attributes);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        wrapped.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        wrapped.characters(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        wrapped.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        wrapped.processingInstruction(target, data);
    }

    @Override
    public void skippedEntity(String name)
            throws SAXException {
        wrapped.skippedEntity(name);
    }

    // LexicalHandler methods below.
    // To use these, implements should implement LexicalHandler.

    public void startCDATA()
            throws SAXException {
        wrappedLexical.startCDATA();
    }

    public void endCDATA()
            throws SAXException {
        wrappedLexical.endCDATA();
    }

    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
        wrappedLexical.startDTD(name, publicId, systemId);
    }

    public void endDTD()
            throws SAXException {
        wrappedLexical.endDTD();
    }

    public void startEntity(String name)
            throws SAXException {
        wrappedLexical.startEntity(name);
    }

    public void endEntity(String name)
            throws SAXException {
        wrappedLexical.endEntity(name);
    }

    public void comment(char ch[], int start, int length)
            throws SAXException {
        wrappedLexical.comment(ch, start, length);
    }

}
