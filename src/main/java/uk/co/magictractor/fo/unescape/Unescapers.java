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

import uk.co.magictractor.fo.thirdparty.OptionalLibraries;

/**
 *
 */
public class Unescapers {

    private static Unescaper INSTANCE;

    // TODO! allow this to be configured
    // usual pattern is
    // 1) System property
    // 2) Configuration file
    // 3) SPI
    public static Unescaper getInstance() {
        if (INSTANCE == null) {
            // TODO! and JSoup. Tests should determine which is better and therefore should go first.
            if (OptionalLibraries.JSOUP.isPresent()) {
                // Apache includes HTML 5 names. TODO! and more? find out...
                INSTANCE = new JSoupUnescaper();
            }
            else if (OptionalLibraries.APACHE_TEXT.isPresent()) {
                // Apache includes HTML 4 names.
                INSTANCE = new ApacheTextUnescaper();
            }
            else {
                INSTANCE = new FoUnescaper();
            }
        }
        return INSTANCE;
    }

}
