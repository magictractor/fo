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
package uk.co.magictractor.fo.entityset;

import java.util.Map;

public final class EntitySets {

    private static Map<String, String> HTML5;
    private static Map<String, String> HTML4;

    /**
     * <p>
     * The HTML MathML Entity Set is recommended by W3C for any new document
     * type and is used by HTML 5 and MathML 3.
     * </p>
     *
     * @see https://www.w3.org/TR/xml-entity-names/#htmlmathml
     */
    public static Map<String, String> html5() {
        if (HTML5 == null) {
            HTML5 = new EntitySetBuilder()
                    .withExpectedSize(2125)
                    .withEntityDeclarationsResource("htmlmathml-f.ent")
                    .build();
        }
        return HTML5;
    }

    /**
     * <p>
     * XHTML uses the same entity sets as HTML 4. See section A.2. Entity Sets
     * in <a href= "https://www.w3.org/TR/xhtml1/#dtds" target=
     * "_blank">https://www.w3.org/TR/xhtml1/#dtds</a>.
     * </p>
     */
    public static Map<String, String> html4() {
        if (HTML4 == null) {
            HTML4 = new EntitySetBuilder()
                    // 96+124+32=252
                    .withExpectedSize(252)
                    .withEntityDeclarationsResource("xhtml-lat1.ent")
                    .withEntityDeclarationsResource("xhtml-symbol.ent")
                    // The &apos; should be here accoring to the spec (section C.16.),
                    // but it is. Filter to match HTML 4.01.
                    .withEntityDeclarationsResource("xhtml-special.ent", (b, e) -> {
                        if (!e.getName().equals("apos")) {
                            b.withEntity(e);
                        }
                    })
                    .build();
        }
        return HTML4;
    }

    private EntitySets() {
    }

    public static void main(String[] args) {
        EntitySets.html5();
    }

}
