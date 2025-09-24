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

import java.util.Comparator;

import com.google.common.base.MoreObjects;

/**
 * <p>
 * For use with {@code EntitySetBuilder} to allow {@code Function}s that parse
 * lines to return the name and value of an entity.
 * </p>
 * <p>
 * A subclass may be created with an override of {@code validateName()} to
 * permit a broader range of characters in the {@code Entity} name. However,
 * this class supports current and legacy W3C entity sets and should be
 * sufficient for most uses.
 * </p>
 * <p>
 * Dots can be found in the legacy isogrk4 Alternative Greek Symbols entity set
 * in names such as {@code b.alpha}.
 * </p>
 *
 * @see https://www.w3.org/TR/WD-sgml-lex-951115.html
 * @see Section "3.2.3. Names" of https://www.ietf.org/rfc/rfc1866.txt
 */
public class Entity {

    public final static Comparator<Entity> COMPARATOR_NAME_CASE_SENSITIVE = Comparator.comparing(Entity::getName);
    public final static Comparator<Entity> COMPARATOR_NAME_CASE_INSENSITIVE = Comparator.comparing(e -> e.getName().toLowerCase());
    public final static Comparator<Entity> COMPARATOR_VALUE = Comparator.comparing(Entity::getValue);

    private final String name;
    private final String value;

    public Entity(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("name and value must not be null");
        }
        if (name.isEmpty()) {
            throw new IllegalArgumentException("name must not be empty");
        }
        validateName(name);
        validateValue(value);

        this.name = name;
        this.value = value;
    }

    /**
     * <p>
     * Validates names using the rules from the W3C's "Lexical Analyzer for HTML
     * and Basic SGML" document.
     * </p>
     * <blockquote>A name is a name-start character -- a letter -- followed by
     * any number of name characters -- letters, digits, periods, or
     * hyphens.</blockquote>
     * <p>
     * May be overridden to provide a custom {@code Entity} implementation that
     * allows a broader range of characters in the name. Overrides probably
     * should not permit whitespace or semicolons in the {@code Entity} name.
     * </p>
     *
     * @param name non-null and non-empty name, those cases are checked in the
     *        constructor
     * @see https://www.w3.org/TR/WD-sgml-lex-951115.html
     */
    protected void validateName(String name) {
        char c = name.charAt(0);
        if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
            throw new IllegalArgumentException("Entity name " + name + " must start with an ASCII letter");
        }

        for (int i = 1; i < name.length(); i++) {
            c = name.charAt(i);
            if (c > 'z' || c < '-' || (c > '9' && c < 'A') || (c > 'Z' && c < 'a') || c == '/') {
                throw new IllegalArgumentException("Entity name " + name + " must contain only ASCII letters, digits, dot or hyphen");
            }
        }
    }

    protected void validateValue(String value) {
        // Empty value is permitted.
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(this.getClass())) {
            return false;
        }
        Entity other = (Entity) obj;
        return name.equals(other.name) && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return name.hashCode() ^ value.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("value", value)
                .toString();
    }

}
