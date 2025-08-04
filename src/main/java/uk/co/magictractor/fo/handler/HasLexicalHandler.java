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

import org.apache.commons.logging.LogFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

/**
 *
 */
public interface HasLexicalHandler {

    /** May return null. */
    LexicalHandler getLexicalHandler();

    public static LexicalHandler getLexicalHandler(ContentHandler contentHandler) {
        if (contentHandler instanceof LexicalHandler) {
            if (contentHandler instanceof HasLexicalHandler) {
                LogFactory.getLog(contentHandler.getClass())
                        .warn(contentHandler.getClass().getSimpleName()
                                + " implements both LexicalHandler and HasLexicalHandler. Only one should be used. HasLexicalHandler has been ignored.");
            }
            return (LexicalHandler) contentHandler;
        }
        else if (contentHandler instanceof HasLexicalHandler) {
            return ((HasLexicalHandler) contentHandler).getLexicalHandler();
        }
        else {
            return null;
        }
    }

}
