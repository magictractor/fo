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

/**
 * <p>
 * This class is public, but customisation is more likely to extend
 * {@code FoHtml5Unescaper} or {@code FoHtml4Unescaper}.
 * </p>
 */
public abstract class AbstractFoUnescaper implements Unescaper {

    /**
     * HTML 5 should override this to return
     * {@code U+FFFD REPLACEMENT CHARACTER} in some cases, and map some C1
     * control codes.
     */
    protected String convertCodePoint(int codePoint) {
        return new String(Character.toChars(codePoint));
    }

}
