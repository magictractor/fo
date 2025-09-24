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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class BinarySearchEntitySetTest {

    private static BinarySearchEntitySet html5 = new BinarySearchEntitySet(EntitySets.html5());
    private static BinarySearchEntitySet xmlPrefined = new BinarySearchEntitySet(EntitySets.xmlPredefined());

    @Test
    public void testFindExact_match() {
        Entity actual = html5.getEntity("eacute");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("eacute");
    }

    @Test
    public void testFindExact_noMatchStart() {
        assertThat(html5.getEntity("AAA")).isNull();
    }

    @Test
    public void testFindExact_noMatchEnd() {
        assertThat(html5.getEntity("zzz")).isNull();
    }

    @Test
    public void testFindInexact_exactMatch() {
        Entity actual = html5.getLongestEntityAtStart("eacute");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("eacute");
    }

    @Test
    public void testFindInexact_exactMatchFirst() {
        // &AElig;
        Entity first = html5.get(0);
        Entity actual = html5.getLongestEntityAtStart(first.getName());
        assertThat(actual).isEqualTo(first);
    }

    @Test
    public void testFindInexact_exactMatchLast() {
        // &zwjn;
        Entity last = html5.get(html5.size() - 1);
        Entity actual = html5.getLongestEntityAtStart(last.getName());
        assertThat(actual).isEqualTo(last);
    }

    @Test
    public void testFindInexact_inexactMatchFirst() {
        Entity first = html5.get(0);
        Entity actual = html5.getLongestEntityAtStart(first.getName() + "1");
        assertThat(actual).isEqualTo(first);
    }

    // For coverage. Binary search will find the first element which doesn't match.
    @Test
    public void testFindInexact_missFirst() {
        Entity first = html5.get(0);
        // AEliz
        String name = first.getName().substring(0, first.getName().length() - 1) + "z";
        assertThat(html5.getLongestEntityAtStart(name)).isNull();
    }

    @Test
    public void testFindInexact_inexactMatchLast() {
        Entity last = html5.get(html5.size() - 1);
        Entity actual = html5.getLongestEntityAtStart(last.getName() + "a");
        assertThat(actual).isEqualTo(last);
    }

    @Test
    public void testFindInexact_inexactMatch() {
        Entity actual = html5.getLongestEntityAtStart("noti");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("not");
    }

    @Test
    public void testFindInexact_noMatchStart() {
        assertThat(html5.getLongestEntityAtStart("AAA")).isNull();
    }

    @Test
    public void testFindInexact_noMatchEnd() {
        assertThat(html5.getLongestEntityAtStart("zzz")).isNull();
    }

    @Test
    public void testFindInexact_inexactMatchSkippingIntermediate() {
        // Check that gla;, glE; and glj; are skipped.
        Entity actual = html5.getLongestEntityAtStart("glum");
        assertThat(actual).isNotNull();
        assertThat(actual.getName()).isEqualTo("gl");
    }

    @Test
    public void testFindInexact_miss() {
        assertThat(html5.getLongestEntityAtStart("gyro")).isNull();
    }

    @Test
    public void testSize() {
        assertThat(xmlPrefined.size()).isEqualTo(5);
        assertThat(html5.size()).isEqualTo(2125);
    }

    @Test
    public void testIterator() {
        Iterator<Entity> iter = xmlPrefined.iterator();
        assertThat(iter.next().getName()).isEqualTo("amp");
        assertThat(iter.next().getName()).isEqualTo("apos");
        assertThat(iter.next().getName()).isEqualTo("gt");
        assertThat(iter.next().getName()).isEqualTo("lt");
        assertThat(iter.next().getName()).isEqualTo("quot");
        assertThat(iter.hasNext()).isFalse();
    }

    @Test
    public void testStream() {
        List<String> names = xmlPrefined.stream()
                .map(Entity::getName)
                .collect(Collectors.toList());
        assertThat(names).containsExactly("amp", "apos", "gt", "lt", "quot");
    }

}
