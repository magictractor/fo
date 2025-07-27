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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Element;

/**
 *
 */
public class CompoundElementModifier implements ElementModifier {

    private final List<ElementModifier> modifiers = new ArrayList<>();
    private Class<? extends ElementModifier> lastModifierType;
    private boolean requiresContainer;

    // Users should use the static method ElementModifiers.
    protected CompoundElementModifier(ElementModifier... modifiers) {
        for (ElementModifier modifier : modifiers) {
            add(modifier);
        }
    }

    public int size() {
        return modifiers.size();
    }

    public List<ElementModifier> asList() {
        return Collections.unmodifiableList(modifiers);
    }

    public void add(ElementModifier modifier) {
        if (modifier.requiresContainer()) {
            requiresContainer = true;
        }

        Class<? extends ElementModifier> modifierType = modifier.getClass();
        if (modifierType.equals(lastModifierType) && AttributeSetter.class.equals(modifierType)) {
            // If the last modifier is also an AttributeSetter they can be merged.
            // TODO! merge, for now just add
            modifiers.add(modifier);
        }
        else if (NoOpElementModifier.class.equals(modifierType)) {
            // This modifier does nothing, so don't bother adding it. Do nothing.
        }
        else {
            // Usual case.
            modifiers.add(modifier);
            lastModifierType = modifierType;
        }
    }

    @Override
    public void accept(Element element) {
        for (ElementModifier modifier : modifiers) {
            modifier.accept(element);
        }
    }

    @Override
    public boolean requiresContainer() {
        return requiresContainer;
    }

}
