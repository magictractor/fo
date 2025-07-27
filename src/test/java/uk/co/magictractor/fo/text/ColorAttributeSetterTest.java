/**
 * Copyright 2024 Ken Dobson
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
package uk.co.magictractor.fo.text;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.modifiers.ColorAttributeSetter;

/**
 *
 */
public class ColorAttributeSetterTest {

    @Test
    public void testIncreaseSaturation() {
        ColorAttributeSetter setter = new ColorAttributeSetter("background-color", "#f7c2d6");
        assertThat(setter.increaseSaturation().getAttributeValue(0)).isEqualTo("#f771a3");
    }

    @Test
    public void testDecreaseSaturation() {
        ColorAttributeSetter setter = new ColorAttributeSetter("background-color", "#f7c2d6");
        assertThat(setter.decreaseSaturation().getAttributeValue(0)).isEqualTo("#f7a3c3");
    }

    @Test
    public void testIncreaseBrightness() {
        ColorAttributeSetter setter = new ColorAttributeSetter("background-color", "#f7c2d6");
        assertThat(setter.increaseBrightness().getAttributeValue(0)).isEqualTo("#ffc93f");
    }

    @Test
    public void testDecreaseBrightness() {
        ColorAttributeSetter setter = new ColorAttributeSetter("background-color", "#f7c2d6");
        assertThat(setter.decreaseBrightness().getAttributeValue(0)).isEqualTo("#ffcf39");
    }

}
