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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class EntitySetsTest {

    @Test
    public void testHtml5MatchesW3cLegacyEntitySets() {
        // https://www.w3.org/TR/xml-entity-names/index.html#sets highlights them and says they're not in MathML3/HTML5.
        // https://www.w3.org/TR/MathML3/
        // http://www.w3.org/Math/DTD/mathml3/mathml3.dtd
        Map<String, String> w3c = new EntitySetBuilder()
                // Using removeEntity() could cause the resizes to happen,
                // but that's fine for a test.
                .withExpectedSize(2125)
                .withEntityDeclarationsResource("w3centities-f.ent")
                .withEntityDeclarationsResource("isogrk1.ent", (b, e) -> b.removeEntity(e))
                .withEntityDeclarationsResource("isogrk2.ent", (b, e) -> b.removeEntity(e))
                .withEntityDeclarationsResource("isogrk4.ent", (b, e) -> b.removeEntity(e))
                .build();

        Diff diff = new Diff("Html5", EntitySets.html5(), "W3C_entities", w3c);
        assertSame(diff);
    }

    @Test
    public void testHtml5MatchesJson() {
        // https://www.w3.org/TR/xml-entity-names/index.html#sets highlights them and says they're not in MathML3/HTML5.
        // https://www.w3.org/TR/MathML3/
        // http://www.w3.org/Math/DTD/mathml3/mathml3.dtd
        Map<String, String> jsonMap = new EntitySetBuilder()
                .withExpectedSize(2125)
                .withResource("entities.json", this::parseHtml5Json)
                .build();

        Diff diff = new Diff("Html5", EntitySets.html5(), "entities.json", jsonMap);
        assertSame(diff);
    }

    //   "&Acirc;": { "codepoints": [194], "characters": "\u00C2" },
    private List<Entity> parseHtml5Json(String line) {
        if (!line.startsWith("  \"&")) {
            return null;
        }

        int nameEndIndex = line.indexOf('\"', 4);
        String name = line.substring(4, nameEndIndex - 1);
        if (line.charAt(nameEndIndex - 1) != ';') {
            // Discards 106 duplicates (2231 - 2125).
            // LOG.debug("Discarding " + name + ", assumed to be a duplicate");
            return null;
        }

        int startBracketIndex = line.indexOf('[');
        int commaIndex = line.indexOf(',', startBracketIndex + 1);
        int endBracketIndex = line.indexOf(']', startBracketIndex + 1);
        String value;
        if (commaIndex > endBracketIndex) {
            // Single codepoint.
            int codePoint = Integer.parseInt(line.substring(startBracketIndex + 1, endBracketIndex));
            value = new String(Character.toChars(codePoint));
        }
        else {
            // Two codepoints. If more there will be a NumberFormatException.
            StringBuilder sb = new StringBuilder(2);
            int codePoint = Integer.parseInt(line.substring(startBracketIndex + 1, commaIndex));
            sb.appendCodePoint(codePoint);
            // There's one space after the comma.
            codePoint = Integer.parseInt(line.substring(commaIndex + 2, endBracketIndex));
            sb.appendCodePoint(codePoint);
            value = sb.toString();
            // temp
            if (value.length() > 2) {
                throw new IllegalStateException("increase capacity");
            }
        }

        return Collections.singletonList(new Entity(name, value));
    }

    @Test
    public void testHtml4IsSubsetOfHtml5() {
        // &rang; and &lang; have similar appearance,
        // but are distinct characters in HTML4 and HTML5.
        // Values are tested in AbstractUnescaperTest.
        Diff diff = new Diff("Html4", EntitySets.html4(), "Html5", EntitySets.html5());
        assertThat(diff.map1OnlyKeys).isEmpty();
        assertThat(diff.valueMismatchKeys).containsExactlyInAnyOrder("lang", "rang");
        assertThat(diff.map2OnlyKeys).hasSize(1873);
    }

    @Test
    public void testHtml4MatchesXhtml() {
        // XHTML uses the same entity definitions as HTML 4,
        // but "modified to be valid XML 1.0 entity declarations".
        // See https://www.w3.org/TR/xhtml1/#dtds
        Map<String, String> html401 = new EntitySetBuilder()
                // 96+124+32=252
                .withExpectedSize(252)
                .withEntityDeclarationsResource("HTMLlat1.ent")
                .withEntityDeclarationsResource("HTMLsymbol.ent")
                .withEntityDeclarationsResource("HTMLspecial.ent")
                .build();
        Diff diff = new Diff("xhtml", EntitySets.html4(), "html401", html401);
        assertSame(diff);
    }

    private void assertSame(Diff diff) {
        if (!diff.map1OnlyKeys.isEmpty()) {
            fail(diff.name1 + " contains " + diff.map1OnlyKeys.size() + " that are not in " + diff.name2 + " :" + diff.map1OnlyKeys);
        }
        assertThat(diff.map2OnlyKeys).isEmpty();
        assertThat(diff.valueMismatchKeys).isEmpty();
    }

    private static class Diff {
        private final String name1;
        private final String name2;
        private final Set<String> commonKeys;
        private final Set<String> map1OnlyKeys;
        private final Set<String> map2OnlyKeys;
        private final Set<String> valueMismatchKeys;

        /* default */ Diff(String name1, Map<String, String> map1, String name2, Map<String, String> map2) {
            this.name1 = name1;
            this.name2 = name2;

            commonKeys = new HashSet<>(map1.keySet());
            commonKeys.retainAll(map2.keySet());

            map1OnlyKeys = new HashSet<>(map1.keySet());
            map1OnlyKeys.removeAll(commonKeys);

            map2OnlyKeys = new HashSet<>(map2.keySet());
            map2OnlyKeys.removeAll(commonKeys);

            valueMismatchKeys = new HashSet<>();
            for (String key : commonKeys) {
                if (!map1.get(key).equals(map2.get(key))) {
                    valueMismatchKeys.add(key);
                    //System.out.println("Value mismatch for " + key + ", " + name1 + " has " + debugValue(map1.get(key))
                    //        + ", " + name2 + " has " + debugValue(map2.get(key)));
                }
            }

            if (!map1OnlyKeys.isEmpty()) {
                //System.out.println(name1 + " contains " + map1OnlyKeys.size() + " that are not in " + name2 + " :" + map1OnlyKeys);
            }
            if (!map2OnlyKeys.isEmpty()) {
                //System.out.println(name2 + " contains " + map2OnlyKeys.size() + " that are not in " + name1 + " :" + new TreeSet<>(map2OnlyKeys));
            }
        }
    }

    private static String debugValue(String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(value);
        sb.append(" (");
        for (int i = 0; i < value.length(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(0 + value.charAt(i));
        }
        sb.append(")");

        return sb.toString();
    }

}
