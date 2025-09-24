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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class BinarySearchEntitySet extends AbstractList<Entity> implements EntitySet {

    private final int size;
    // Array sorted by name to permit binary search.
    private final Entity[] sortedEntities;
    // Just the names in order to make searches slightly more efficient.
    private final String[] sortedEntityNames;
    // Used with getLongestEntityAtStart().
    private final int[] startsWithOtherEntityName;

    public BinarySearchEntitySet(Collection<Entity> entities) {
        size = entities.size();
        sortedEntities = new Entity[size];
        sortedEntityNames = new String[size];
        startsWithOtherEntityName = new int[size];
        entities.toArray(sortedEntities);
        initArrays();
    }

    // TODO! remove this constructor, change EntitySet builder to create this directly
    public BinarySearchEntitySet(Map<String, String> entities) {
        size = entities.size();
        sortedEntities = new Entity[size];
        sortedEntityNames = new String[size];
        startsWithOtherEntityName = new int[size];
        int i = 0;
        for (Map.Entry<String, String> entry : entities.entrySet()) {
            sortedEntities[i++] = new Entity(entry.getKey(), entry.getValue());
        }
        initArrays();
    }

    private void initArrays() {
        // Sort sortedEntities.
        Arrays.sort(sortedEntities, Entity.COMPARATOR_NAME_CASE_SENSITIVE);

        // Init sortedEntityNames.
        for (int i = 0; i < size; i++) {
            sortedEntityNames[i] = sortedEntities[i].getName();
        }

        // Init startsWithOtherEntityName.
        Arrays.fill(startsWithOtherEntityName, -1);
        for (int i = 0; i < size; i++) {
            String entityName = sortedEntityNames[i];
            int j = i + 1;
            while (j < size && sortedEntityNames[j].startsWith(entityName)) {
                startsWithOtherEntityName[j++] = i;
            }
        }
    }

    @Override
    public String getValue(String name) {
        return getEntity(name).getValue();
    }

    @Override
    public Entity getEntity(String name) {
        int index = Arrays.binarySearch(sortedEntityNames, name);
        return index >= 0 ? sortedEntities[index] : null;
    }

    @Override
    public Entity getLongestEntityAtStart(String name) {
        int index = Arrays.binarySearch(sortedEntityNames, name);
        if (index >= 0) {
            // Exact match found.
            return sortedEntities[index];
        }
        else {
            // Flip index back to positive.
            index = -index - 2;
            while (index >= 0) {
                if (name.startsWith(sortedEntityNames[index])) {
                    return sortedEntities[index];
                }
                index = startsWithOtherEntityName[index];
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<Entity> iterator() {
        return stream().iterator();
    }

    @Override
    public Stream<Entity> stream() {
        return Arrays.stream(sortedEntities);
    }

    @Override
    public Entity get(int index) {
        return sortedEntities[index];
    }

}
