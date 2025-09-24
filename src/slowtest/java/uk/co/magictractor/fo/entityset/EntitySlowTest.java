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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class EntitySlowTest {

    @ParameterizedTest
    @MethodSource("asciiChars")
    public void testNameFirstChar(Character c) {
        boolean expectedAllowed = Character.isLetter(c);
        String name = c + "z";

        if (expectedAllowed) {
            Entity entity = new Entity(name, "value");
            assertThat(entity.getName()).isEqualTo(name);
        }
        else {
            assertThatThrownBy(() -> new Entity(name, "value"))
                    .hasMessage("Entity name " + name + " must start with an ASCII letter");
        }
    }

    @ParameterizedTest
    @MethodSource("asciiChars")
    public void testNameNotFirstChar(Character c) {
        boolean expectedAllowed = Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '-';
        String name = "Z" + c;

        if (expectedAllowed) {
            Entity entity = new Entity(name, "value");
            assertThat(entity.getName()).isEqualTo(name);
        }
        else {
            assertThatThrownBy(() -> new Entity(name, "value"))
                    .hasMessage("Entity name " + name + " must contain only ASCII letters, digits, dot or hyphen");
        }
    }

    public static List<Character> asciiChars() {
        List<Character> asciiChars = new ArrayList<>(127);
        for (char c = '\u0000'; c <= '\u007f'; c++) {
            asciiChars.add(c);
        }
        return asciiChars;
    }

}
