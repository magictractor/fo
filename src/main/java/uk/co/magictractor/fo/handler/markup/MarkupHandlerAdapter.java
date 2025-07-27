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

import org.xml.sax.SAXException;

import uk.co.magictractor.fo.handler.BlankContentHandler;

/**
 * Adapter for SAX events to markup events. MarkupHandlers can then create
 * output for different types of markup (Asciidoc, Discord etc).
 */
public class MarkupHandlerAdapter implements BlankContentHandler {

    private final MarkupHandler markupHandler;

    public MarkupHandlerAdapter(MarkupHandler markupHandler) {
        this.markupHandler = markupHandler;
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {
        markupHandler.addText(new String(ch, start, length));
    }

}
