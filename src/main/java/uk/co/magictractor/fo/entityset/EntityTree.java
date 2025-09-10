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

import java.util.HashMap;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.base.MoreObjects.ToStringHelper;

/**
 * <p>
 * Structure that is used to enable efficient lookups for HTML 5 named character
 * references.
 * </p>
 * <blockquote>Consume the maximum number of characters possible, where the
 * consumed characters are one of the identifiers in the first column of the
 * named character references table. </blockquote>
 * <p>
 * Note that an {@code EntityTree} may have a value and children. For instance,
 * HTML 5 has entities {@code &empty;} and {@code &emptyset;}, so the
 * {@code EntityTree} for {@code &empty;} has a value (&#x2205;) and children
 * leading to the tree for {@code &emptyset;}.
 * </p>
 *
 * @see https://html.spec.whatwg.org/multipage/parsing.html#named-character-reference-state
 */
// TODO! performance tests vs a HashMap.
// TODO! this is not a good solution? Will need to keep reading anyway (a-zA-z0-9) to
// get a name for an error message.
// Better then to use a binary tree or binary search on an array?
// Create performance tests first?
@Deprecated
public class EntityTree {

    // Base identifies the subtree, providing context in toString().
    // Mostly used for debugging, but could also be used to reconstruct keys as they would be used in the base EntityTree.
    private final String base;

    // In most cases one of these is non-null.
    // Rarely, both are non null, such as for &not; which has a value and also a subtree containing &notin;.
    // Never both null.
    private Map<Character, EntityTree> subtrees;
    private String value;

    public EntityTree() {
        base = "";
    }

    private EntityTree(String base) {
        this.base = base;
    }

    public EntityTree(Map<String, String> entityMap) {
        this();
        for (Map.Entry<String, String> entry : entityMap.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public String getValue() {
        return value;
    }

    /**
     * <p>
     * Subtrees may be useful if looking for the entity with the longest name
     * that matches text. A few entities have names that are substrings at the
     * start of other entities, such as {@code &not;} and {@code &notin;}.
     * </p>
     * <p>
     * The HTML 5 specification says:
     * </p>
     * <blockquote>Consume the maximum number of characters possible, where the
     * consumed characters are one of the identifiers in the first column of the
     * named character references table.</blockquote>
     * <p>
     * At the time of writing major browsers do not do this.
     * </p>
     *
     * @see https://html.spec.whatwg.org/multipage/parsing.html#named-character-reference-state
     */
    // TODO! document behaviour of browsers.
    // TODO! document all nodes with children and values (also check whether child has same value)
    public EntityTree getSubtree(char c) {
        return subtrees == null ? null : subtrees.get(c);
    }

    public String get(String key) {
        int n = key.length();
        EntityTree tree = this;
        for (int i = 0; i < n; i++) {
            if (tree.subtrees == null) {
                return null;
            }
            tree = tree.subtrees.get(key.charAt(i));
            if (tree == null) {
                return null;
            }
        }
        return tree.value;
    }

    public void put(String key, String value) {
        int n = key.length();
        EntityTree tree = this;
        for (int i = 0; i < n; i++) {
            char c = key.charAt(i);
            if (tree.subtrees == null) {
                // temp
                if (tree.value != null) {
                    System.out.println("has value and subtrees: " + key + " -> " + value);
                }
                tree.subtrees = new HashMap<>();
                EntityTree subTree = new EntityTree(base + c);
                tree.subtrees.put(c, subTree);
                tree = subTree;
            }
            else {
                tree = tree.subtrees.computeIfAbsent(c, (k) -> new EntityTree(base + c));
            }
        }
        if (tree.subtrees != null) {
            System.out.println("has value and subtrees: " + key + " -> " + value);
        }
        tree.value = value;
    }

    @Override
    public String toString() {
        ToStringHelper helper = MoreObjects.toStringHelper(this)
                .add("base", base);

        // TODO! info about children... keys?

        if (value != null) {
            helper.add("value", value);
        }

        return helper.toString();
    }

    // Ooh, there are loads in HTML 5
    //
    // Modest number in HTML 4
    //    sube starts with the name of another entity: sub, values ⊆ and ⊂
    //    sigmaf starts with the name of another entity: sigma, values ς and σ
    //    thetasym starts with the name of another entity: theta, values ϑ and θ
    //    notin starts with the name of another entity: not, values ∉ and ¬
    //    ordf starts with the name of another entity: or, values ª and ∨
    //    sup2 starts with the name of another entity: sup, values ² and ⊃
    //    sup3 starts with the name of another entity: sup, values ³ and ⊃
    //    sup1 starts with the name of another entity: sup, values ¹ and ⊃
    //    ordm starts with the name of another entity: or, values º and ∨
    //    piv starts with the name of another entity: pi, values ϖ and π
    //    supe starts with the name of another entity: sup, values ⊇ and ⊃
    public static void main(String[] args) {
        EntityTree html4Tree = new EntityTree(EntitySets.html4());
        for (Map.Entry<String, String> entry : EntitySets.html4().entrySet()) {
            String name = entry.getKey();

            EntityTree tree = html4Tree;
            //EntityTree tree = new EntityTree(EntitySets.html4());
            for (int i = 0; i < name.length() - 1; i++) {
                char c = name.charAt(i);
                tree = tree.getSubtree(c);
                if (tree.getValue() != null) {
                    String subName = entry.getKey().substring(0, i + 1);
                    if (entry.getValue().equals(tree.getValue())) {
                        // Ignore aliases
                        continue;
                    }
                    System.out.println(entry.getKey() + " starts with the name of another entity: " + subName
                            + ", values " + entry.getValue() + " and " + tree.getValue());
                }
            }
        }
    }

}
