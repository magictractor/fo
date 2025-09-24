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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public abstract class AbstractEntitySetSlowTest {

    private final static List<String> ENTITIES = new ArrayList<>(EntitySets.html5().keySet());
    static {
        ENTITIES.sort(Comparator.comparing(String::toLowerCase));
    }

    private final EntitySet entitySet;

    protected AbstractEntitySetSlowTest(EntitySet entitySet) {
        if (entitySet.size() != ENTITIES.size()) {
            throw new IllegalArgumentException();
        }
        this.entitySet = entitySet;
    }

    @ParameterizedTest(name = "{0} [{index}]")
    @MethodSource("entityNames")
    public void getLongestEntityAtStart(String entityName) {
        Entity atStart = entitySet.getLongestEntityAtStart(entityName + "zzzzhuhkdfhhueiwajkaskf");
        assertThat(atStart.getName()).isEqualTo(entityName);
    }

    public static List<String> entityNames() {
        return ENTITIES;
    }

}
