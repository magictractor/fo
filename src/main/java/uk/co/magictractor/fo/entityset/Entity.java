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

/**
 * <p>
 * For use with {@code EntitySetBuilder} to allow {@code Function}s that parse
 * lines to return the name and value of an entity.
 * </p>
 */
public class Entity {

    private final String name;
    private final String value;

    public Entity(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isWhitespace(c)) {
                // Check this against the HTML 5 spec.
                throw new IllegalStateException("Entity name " + name + " must not contain whitespace");
            }
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

}
