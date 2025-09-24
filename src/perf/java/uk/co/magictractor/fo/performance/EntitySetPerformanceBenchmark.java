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
package uk.co.magictractor.fo.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import uk.co.magictractor.fo.entityset.BinarySearchEntitySet;
import uk.co.magictractor.fo.entityset.EntitySets;
import uk.co.magictractor.fo.entityset.EntityTree;

public class EntitySetPerformanceBenchmark extends PerformanceBenchmark<String, String> {

    private static final Map<String, String> MAP = EntitySets.html5();
    private static final BinarySearchEntitySet ENTITY_SET = new BinarySearchEntitySet(MAP);
    private static final EntityTree ENTITY_TREE = new EntityTree(MAP);
    private static final List<String> ENTITY_NAMES = new ArrayList<>(MAP.keySet());

    static {
        for (int i = 0; i < ENTITY_NAMES.size(); i++) {
            // ENTITY_NAMES.set(i, ENTITY_NAMES.get(i) + "zzz");
        }
    }

    private static final int ITERATIONS = 10;

    public EntitySetPerformanceBenchmark() {
        addAction("HashMap", this::mapLookup);
        addAction("BinarySearch", this::entitySetLookup);
        addAction("EntityTree", this::entityTreeLookup);

        //verify();

        // TODO! revisit this, first iteration is still much slower.
        warmUp("copy");
    }

    @Override
    public void singleRun(Function<String, String> entityValueLookup) {
        for (int i = 0; i < ITERATIONS; i++) {
            for (String entityName : ENTITY_NAMES) {
                entityValueLookup.apply(entityName);
            }
        }
    }

    private String mapLookup(String entityName) {
        return MAP.get(entityName);
    }

    private String entitySetLookup(String entityName) {
        return ENTITY_SET.getEntity(entityName).getValue();
        // return ENTITY_SET.findInexact(entityName).getValue();
    }

    private String entityTreeLookup(String entityName) {
        return ENTITY_TREE.get(entityName);
    }

    public static void main(String[] args) {
        new EntitySetPerformanceBenchmark().runBenchmarks();
    }

}
