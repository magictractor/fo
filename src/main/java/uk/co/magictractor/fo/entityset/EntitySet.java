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

import java.util.Collection;

public interface EntitySet extends Collection<Entity> {

    /**
     * Returns the value of the {@code Entity} with the given name; null if
     * there is no matching {@code Entity}.
     */
    String getValue(String name);

    /**
     * Returns the {@code Entity} with the given name; null if there is no
     * matching {@code Entity}.
     */
    Entity getEntity(String name);

    Entity getLongestEntityAtStart(String name);

}
