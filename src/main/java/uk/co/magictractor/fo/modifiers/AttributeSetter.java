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
package uk.co.magictractor.fo.modifiers;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

/**
 * Sets attributes on an {@code Element}.
 */
public class AttributeSetter implements ElementModifier {

    private final boolean requiresContainer;
    private final List<String> attributeNames;
    private final List<String> attributeValues;

    protected AttributeSetter(String... attributes) {
        this(false, attributes);
    }

    protected AttributeSetter(boolean requiresContainer, String... attributes) {
        if ((attributes.length % 2) != 0) {
            throw new IllegalArgumentException();
        }

        this.requiresContainer = requiresContainer;
        attributeNames = new ArrayList<>(attributes.length / 2);
        attributeValues = new ArrayList<>(attributes.length / 2);

        for (int i = 0; i < attributes.length; i += 2) {
            attributeNames.add(attributes[i]);
            attributeValues.add(attributes[i + 1]);
        }
    }

    @Override
    public void accept(Element element) {
        for (int i = 0; i < attributeNames.size(); i++) {
            element.setAttribute(attributeNames.get(i), attributeValues.get(i));
        }
    }

    public String getAttributeName(int index) {
        return attributeNames.get(index);
    }

    public String getAttributeValue(int index) {
        return attributeValues.get(index);
    }

    @Override
    public boolean requiresContainer() {
        return requiresContainer;
    }

}
