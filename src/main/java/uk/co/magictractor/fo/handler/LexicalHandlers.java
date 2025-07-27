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

import org.xml.sax.ext.LexicalHandler;

/**
 *
 */
public final class LexicalHandlers {

    private LexicalHandlers() {
    }

    public static LexicalHandler add(LexicalHandler handler1, LexicalHandler handler2) {
        if (handler1 == null) {
            return handler2;
        }
        else if (handler2 == null) {
            return handler1;
        }

        if (handler2 instanceof LexicalHandlerBroadcast) {
            throw new IllegalArgumentException();
        }

        if (handler1 instanceof LexicalHandlerBroadcast) {
            ((LexicalHandlerBroadcast) handler1).addHandler(handler2);
            return handler1;
        }
        else {
            return new LexicalHandlerBroadcast(handler1, handler2);
        }
    }

    public static LexicalHandler add(LexicalHandler handler1, HasLexicalHandler handler2) {
        return add(handler1, handler2.getLexicalHandler());
    }

}
