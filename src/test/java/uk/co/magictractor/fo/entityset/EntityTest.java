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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.util.Preconditions;
import org.junit.jupiter.api.Test;

public class EntityTest {

    @Test
    public void testConstructor_nullName() {
        assertThatThrownBy(() -> new Entity(null, "h"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("name and value must not be null");
    }

    @Test
    public void testConstructor_emptyName() {
        assertThatThrownBy(() -> new Entity("", "h"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("name must not be empty");
    }

    @Test
    public void testConstructor_nullValue() {
        assertThatThrownBy(() -> new Entity("aitch", null))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("name and value must not be null");
    }

    @Test
    public void testConstructor_invalidNameStartsWithNumber() {
        checkInvalidNameFirstLetter("1a");
    }

    @Test
    public void testConstructor_invalidNameStartsWithDot() {
        checkInvalidNameFirstLetter(".com");
    }

    @Test
    public void testConstructor_invalidNameStartsWithHyphen() {
        checkInvalidNameFirstLetter("-dash");
    }

    @Test
    public void testConstructor_invalidNameStartsWithUnderscore() {
        // Underscore is located between the groups of letters.
        checkInvalidNameFirstLetter("_abc");
    }

    @Test
    public void testConstructor_invalidNameStartsWithTilde() {
        // Tilde is located between after the letters.
        checkInvalidNameFirstLetter("~abc");
    }

    @Test
    public void testConstructor_invalidNameCharBeforeHyphen() {
        // Comma before hyphen (the first valid char).
        String name = "a,b";
        Preconditions.checkState(name.indexOf('-' - 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharBeforeZero() {
        // Slash before zero.
        String name = "ac/dc";
        Preconditions.checkState(name.indexOf('0' - 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharAfterNine() {
        // colon before '9'
        String name = "x:y";
        Preconditions.checkState(name.indexOf('9' + 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharBeforeUppercaseA() {
        // at sign before 'A'
        String name = "x@y";
        Preconditions.checkState(name.indexOf('A' - 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharAfterUppercaseZ() {
        // Square bracket after 'Z'
        String name = "bad[";
        Preconditions.checkState(name.indexOf('Z' + 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharBeforeLowercaseA() {
        // backtick before 'A'
        String name = "tick`";
        Preconditions.checkState(name.indexOf('a' - 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    @Test
    public void testConstructor_invalidNameCharAfterLowercaseZ() {
        // Curly bracket after '{'
        String name = "curly{";
        Preconditions.checkState(name.indexOf('z' + 1) >= 0, "oops");
        checkInvalidNameNotFirstLetter(name);
    }

    private void checkInvalidNameFirstLetter(String name) {
        assertThatThrownBy(() -> new Entity(name, "meh"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity name " + name + " must start with an ASCII letter");
    }

    private void checkInvalidNameNotFirstLetter(String name) {
        assertThatThrownBy(() -> new Entity(name, "meh"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity name " + name + " must contain only ASCII letters, digits, dot or hyphen");
    }

    @Test
    public void testName_lowercase() {
        checkValidName("hello");
    }

    @Test
    public void testName_uppercase() {
        checkValidName("HELLO");
    }

    @Test
    public void testName_numerics() {
        checkValidName("joe90");
    }

    @Test
    public void testName_dot() {
        // This name is used in the isogrk4 Alternative Greek Symbols entity set.
        checkValidName("b.alpha");
    }

    @Test
    public void testName_hyphen() {
        // I haven't observed hyphens in the wild, but they are permitted in
        // section "3.2.3. Names" of https://www.ietf.org/rfc/rfc1866.txt
        checkValidName("b-alpha");
    }

    private void checkValidName(String name) {
        Entity entity = new Entity(name, "h");
        assertThat(entity.getName()).isEqualTo(name);
    }

    @Test
    public void testValue() {
        Entity entity = new Entity("aitch", "h");
        assertThat(entity.getValue()).isEqualTo("h");
    }

    @Test
    public void testEquals_true() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("aitch", "h");
        assertThat(entity1.equals(entity2)).isTrue();
    }

    @Test
    public void testEquals_falseNull() {
        Entity entity = new Entity("aitch", "h");
        assertThat(entity.equals(null)).isFalse();
    }

    @Test
    public void testEquals_falseType() {
        Entity entity = new Entity("aitch", "h");
        assertThat(entity.equals(new Object())).isFalse();
    }

    @Test
    public void testEquals_falseName() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("Aitch", "h");
        assertThat(entity1.equals(entity2)).isFalse();
    }

    @Test
    public void testEquals_falseValue() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("aitch", "H");
        assertThat(entity1.equals(entity2)).isFalse();
    }

    @Test
    public void testHashCode_equal() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("aitch", "h");
        assertThat(entity2.hashCode()).isEqualTo(entity1.hashCode());
    }

    @Test
    public void testHashCode_notEqualName() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("Aitch", "h");
        assertThat(entity2.hashCode()).isNotEqualTo(entity1.hashCode());
    }

    @Test
    public void testHashCode_notEqualValue() {
        Entity entity1 = new Entity("aitch", "h");
        Entity entity2 = new Entity("aitch", "H");
        assertThat(entity2.hashCode()).isNotEqualTo(entity1.hashCode());
    }

    @Test
    public void testToString() {
        Entity entity = new Entity("aitch", "h");
        assertThat(entity.toString()).isEqualTo("Entity{name=aitch, value=h}");
    }
}
