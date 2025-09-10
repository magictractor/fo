/**
 * Copyright 2025 Ken Dobson
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
package uk.co.magictractor.fo.unescape;

import org.jsoup.parser.Parser;

// Discussion of using ParserErrorList
// at https://github.com/jhy/jsoup/discussions/2394
public class JSoupUnescaper implements Unescaper {

    @Override
    public String unescape(String text) {
        // false, otherwise incomplete named character references
        // are treated as though they are in an attribute.
        return Parser.unescapeEntities(text, false);
    }

}
