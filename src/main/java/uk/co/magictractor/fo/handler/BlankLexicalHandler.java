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

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public interface BlankLexicalHandler extends LexicalHandler {

    @Override
    default void startDTD(String name, String publicId, String systemId)
            throws SAXException {
    }

    @Override
    default void endDTD()
            throws SAXException {
    }

    @Override
    default void startEntity(String name)
            throws SAXException {
    }

    @Override
    default void endEntity(String name)
            throws SAXException {
    }

    @Override
    default void startCDATA()
            throws SAXException {
    }

    @Override
    default void endCDATA()
            throws SAXException {
    }

    @Override
    default void comment(char ch[], int start, int length)
            throws SAXException {
    }

}
