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
package uk.co.magictractor.fo.modifiers;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mockito;
import org.w3c.dom.Element;

import uk.co.magictractor.fo.mockito.MockitoExtension;

public class ElementModifiersTest {

    private static AttributeSetter SETTER1 = ElementModifiers.attributeSetter("one", "1");
    private static AttributeSetter SETTER2 = ElementModifiers.attributeSetter("two", "2");

    @RegisterExtension
    public MockitoExtension mockitoExtension = new MockitoExtension();

    @Test
    public void testOf_empty() {
        ElementModifier actual = ElementModifiers.of();
        assertThat(actual).isSameAs(ElementModifiers.noOp());
    }

    @Test
    public void testOf_noOp() {
        ElementModifier actual = ElementModifiers.of(ElementModifiers.noOp());
        Assertions.assertThat(actual).isSameAs(ElementModifiers.noOp());
    }

    @Test
    public void testOf_noOp_noOp() {
        ElementModifier actual = ElementModifiers.of(ElementModifiers.noOp(), ElementModifiers.noOp());
        assertThat(actual).isSameAs(ElementModifiers.noOp());
    }

    @Test
    public void testOf_reset() {
        ElementModifier actual = ElementModifiers.of(ElementModifiers.reset());
        assertThat(actual).isSameAs(ElementModifiers.noOp());
    }

    @Test
    public void testOf_setter_reset() {
        ElementModifier actual = ElementModifiers.of(SETTER1, ElementModifiers.reset());
        assertThat(actual).isSameAs(ElementModifiers.noOp());
    }

    @Test
    public void testOf_setter1_reset_setter2() {
        ElementModifier actual = ElementModifiers.of(SETTER1, ElementModifiers.reset(), SETTER2);
        assertThat(actual).isSameAs(SETTER2);
    }

    @Test
    public void testOf_setter1_noOp_setter2() {
        ElementModifier actual = ElementModifiers.of(SETTER1, ElementModifiers.noOp(), SETTER2);
        assertThat(actual).isExactlyInstanceOf(AttributeSetter.class);

        AttributeSetter actualSetter = (AttributeSetter) actual;
        assertThat(actualSetter.getAttributeCount()).isEqualTo(2);
        assertThat(actualSetter.getAttributeName(0)).isEqualTo("one");
        assertThat(actualSetter.getAttributeName(1)).isEqualTo("two");
        assertThat(actualSetter.getAttributeValue(0)).isEqualTo("1");
        assertThat(actualSetter.getAttributeValue(1)).isEqualTo("2");
    }

    @Test
    public void testAttributeSetter_oddArgs() {
        Assertions.assertThatThrownBy(() -> ElementModifiers.attributeSetter("one"))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("attributes must contains pairs and names and values, but has an odd number of elements");
    }

    @Test
    public void testAttributeSetter_modify() {
        AttributeSetter setter = ElementModifiers.attributeSetter("a", "A", "b", "B");

        Element element = mockitoExtension.mock(Element.class);
        setter.modify(element);
        Mockito.verify(element).setAttribute("a", "A");
        Mockito.verify(element).setAttribute("b", "B");
    }

    @Test
    public void testNoOp_modify() {
        NoOpElementModifier noOp = ElementModifiers.noOp();

        Element element = mockitoExtension.mock(Element.class);
        noOp.modify(element);
        Mockito.verifyNoInteractions(element);
    }

}
