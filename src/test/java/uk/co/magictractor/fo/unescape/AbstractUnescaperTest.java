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

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractUnescaperTest {

    protected static final String REPLACEMENT_CHARACTER = "\ufffd";

    protected final Unescaper unescaper;

    protected AbstractUnescaperTest(Unescaper unescaper) {
        this.unescaper = unescaper;
    }

    protected void check(String entity, String expectedReplacement, boolean isSupported) {
        check0(entity, isSupported ? expectedReplacement : entity);
    }

    protected void checkUnchanged(String entity) {
        check0(entity, entity);
    }

    protected void check(String entity, String expectedReplacement) {
        if (entity.equals(expectedReplacement)) {
            throw new IllegalArgumentException("Args are equal, use checkUnchanged() instead");
        }
        check0(entity, expectedReplacement);
    }

    private void check0(String entity, String expectedReplacement) {
        String actual = unescaper.unescape(entity);
        // assertThat(actual).isEqualTo(expectedReplacement);
        if (!actual.equals(expectedReplacement)) {
            // Assertions.fail();
            assertThat(uString(actual)).isEqualTo(uString(expectedReplacement));
            // Fallback in case uString() makes them look equal (such as returning "BIG").
            assertThat(actual).isEqualTo(expectedReplacement);
        }
    }

    private String uString(String str) {
        if (str.length() == 1) {
            return uString(str.charAt(0));
        }
        else if (str.length() == 2) {
            return uString(str.charAt(0)) + uString(str.charAt(1));
        }
        //throw new IllegalStateException("Code needs modification to handle string of length " + str.length());
        // return "BIG";
        return str;
    }

    private String uString(char c) {
        return String.format("\\u%04x", (int) c);
    }

}
