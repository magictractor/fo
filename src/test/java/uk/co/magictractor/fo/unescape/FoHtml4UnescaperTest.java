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

import org.junit.jupiter.api.Test;

public class FoHtml4UnescaperTest extends AbstractFoUnescaperTest {

    protected FoHtml4UnescaperTest() {
        super(new FoHtml4Unescaper());
    }

    // HTML 4 does not include &apos;.
    @Test
    public void testUnescape_namedReferenceApos() {
        checkUnchanged("&apos;");
    }

    @Test
    public void testUnescape_namedReferenceUnclosed() {
        checkUnchanged("&lt");
    }

    @Test
    public void testUnescape_namedReferenceEmptyClosedAndUnclosed() {
        check("&lt;boo&gt", "<boo&gt");
    }

}
