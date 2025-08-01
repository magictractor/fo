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

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public interface BlankContentHandler extends ContentHandler {

    @Override
    default void setDocumentLocator(Locator locator) {
    }

    @Override
    default void startDocument()
            throws SAXException {
    }

    @Override
    default void endDocument()
            throws SAXException {
    }

    @Override
    default void startPrefixMapping(String prefix, String uri)
            throws SAXException {
    }

    @Override
    default void endPrefixMapping(String prefix)
            throws SAXException {
    }

    @Override
    default void startElement(String uri, String localName, String qName, Attributes atts)
            throws SAXException {
    }

    @Override
    default void endElement(String uri, String localName, String qName)
            throws SAXException {
    }

    @Override
    default void characters(char ch[], int start, int length)
            throws SAXException {
    }

    @Override
    default void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
    }

    @Override
    default void processingInstruction(String target, String data)
            throws SAXException {
    }

    @Override
    default void skippedEntity(String name)
            throws SAXException {
    }

}
