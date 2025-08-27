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

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class LexicalHandlerBroadcaster implements LexicalHandler {

    private final List<LexicalHandler> handlers = new ArrayList<>();

    public LexicalHandlerBroadcaster() {
    }

    public LexicalHandlerBroadcaster(LexicalHandler... handlers) {
        for (LexicalHandler handler : handlers) {
            addHandler(handler);
        }
    }

    public void addHandler(LexicalHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("handler must not be null");
        }
        handlers.add(handler);
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.startDTD(name, publicId, systemId);
        }
    }

    @Override
    public void endDTD() throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.endDTD();
        }
    }

    @Override
    public void startEntity(String name) throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.startEntity(name);
        }
    }

    @Override
    public void endEntity(String name) throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.endEntity(name);
        }
    }

    @Override
    public void startCDATA() throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.startCDATA();
        }
    }

    @Override
    public void endCDATA() throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.endCDATA();
        }
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        for (LexicalHandler handler : handlers) {
            handler.comment(ch, start, length);
        }
    }
}
