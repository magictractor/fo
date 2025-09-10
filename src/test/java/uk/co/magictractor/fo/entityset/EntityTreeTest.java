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

import org.junit.jupiter.api.Test;

public class EntityTreeTest {

    @Test
    public void testGet_depth1() {
        EntityTree tree = new EntityTree();
        tree.put("Z", "Zookeeper");
        assertThat(tree.get("Z")).isEqualTo("Zookeeper");
    }

    @Test
    public void testGet_depth2() {
        EntityTree tree = new EntityTree();
        tree.put("lt", "<");
        assertThat(tree.get("lt")).isEqualTo("<");
    }

    @Test
    public void testGet_depth3() {
        EntityTree tree = new EntityTree();
        tree.put("amp", "&");
        assertThat(tree.get("amp")).isEqualTo("&");
    }

    @Test
    public void testGet_failEmpty() {
        EntityTree tree = new EntityTree();
        assertThat(tree.get("amp")).isNull();
    }

    @Test
    public void testGet_failSubstring() {
        EntityTree tree = new EntityTree();
        tree.put("amp", "&");
        assertThat(tree.get("am")).isNull();
    }

    @Test
    public void testGet_failSuperstring() {
        EntityTree tree = new EntityTree();
        tree.put("amp", "&");
        assertThat(tree.get("amps")).isNull();
    }

    @Test
    public void testGet_substring() {
        EntityTree tree = new EntityTree();
        tree.put("emptyset", "a");
        tree.put("empty", "b");
        assertThat(tree.get("emptyset")).isEqualTo("a");
        assertThat(tree.get("empty")).isEqualTo("b");
    }

    @Test
    public void testPut_superstring() {
        EntityTree tree = new EntityTree();
        tree.put("empty", "a");
        tree.put("emptyset", "b");
        assertThat(tree.get("empty")).isEqualTo("a");
        assertThat(tree.get("emptyset")).isEqualTo("b");
    }

    @Test
    public void testHtml5() {
        assertThat(EntitySets.html5Tree().get("eacute")).isEqualTo("\u00e9");
    }

}
