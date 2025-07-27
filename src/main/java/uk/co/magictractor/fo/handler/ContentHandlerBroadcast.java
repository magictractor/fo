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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.MoreObjects;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

/**
 * <p>
 * A {@code ContentHandler} that delegates to a collection of other
 * {@code ContentHandler}s.
 * </p>
 * <p>
 * This is used to allow multiple outputs (PDF, text etc) to be created from a
 * single transform of a XSL-FO document.
 * </p>
 */
public class ContentHandlerBroadcast implements ContentHandler, HasLexicalHandler {

    private final List<ContentHandler> handlers = new ArrayList<>();

    // If any children implement HasLexicalHandler then getLexicalHandler()
    // can change if children are modified after they have been added.
    private boolean buildLexicalHandlerOnDemand;
    private LexicalHandler lexicalHandler;

    public void addHandler(ContentHandler handler) {
        handlers.add(handler);
        if (!buildLexicalHandlerOnDemand) {
            if (handler instanceof LexicalHandler) {
                // TODO! verify does not implement both??
                lexicalHandler = LexicalHandlers.add(lexicalHandler, (LexicalHandler) handler);
            }
            else if (handler instanceof HasLexicalHandler) {
                buildLexicalHandlerOnDemand = true;
                lexicalHandler = null;
            }
        }
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        if (buildLexicalHandlerOnDemand) {
            return buildLexicalHandler();
        }
        return lexicalHandler;
    }

    private LexicalHandler buildLexicalHandler() {
        LexicalHandler lexicalHandler = null;
        for (ContentHandler handler : handlers) {
            if (handler instanceof LexicalHandler) {
                lexicalHandler = LexicalHandlers.add(lexicalHandler, (LexicalHandler) handler);
            }
            else if (handler instanceof HasLexicalHandler) {
                lexicalHandler = LexicalHandlers.add(lexicalHandler, (HasLexicalHandler) handler);
            }
        }

        return lexicalHandler;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        for (ContentHandler handler : handlers) {
            handler.setDocumentLocator(locator);
        }
    }

    @Override
    public void startDocument()
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.startDocument();
        }
    }

    @Override
    public void endDocument()
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.endDocument();
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.startPrefixMapping(prefix, uri);
        }
    }

    @Override
    public void endPrefixMapping(String prefix)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.endPrefixMapping(prefix);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.startElement(uri, localName, qName, attributes);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.endElement(uri, localName, qName);
        }
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.characters(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char ch[], int start, int length)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.ignorableWhitespace(ch, start, length);
        }
    }

    @Override
    public void processingInstruction(String target, String data)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.processingInstruction(target, data);
        }
    }

    @Override
    public void skippedEntity(String name)
            throws SAXException {
        for (ContentHandler handler : handlers) {
            handler.skippedEntity(name);
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("handlers", handlers)
                .toString();
    }

}
