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

import com.google.common.base.MoreObjects;

import org.w3c.dom.Element;

/**
 * Users should use {@link ElementModifiers#of(ElementModifier...)}.
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
            // The last modifier is also an AttributeSetter so they can be merged.
            int lastIndex = modifiers.size() - 1;
            AttributeSetter merged = new AttributeSetter((AttributeSetter) modifiers.get(lastIndex), (AttributeSetter) modifier);
            modifiers.set(lastIndex, merged);
        }
        else if (NoOpElementModifier.class.equals(modifierType)) {
            // This modifier does nothing, so don't bother adding it. Do nothing.
        }
        else if (ResetElementModifier.class.equals(modifierType)) {
            // This modifier discards any previous modifiers and results in a NoOp if nothing else is added.
            modifiers.clear();
            lastModifierType = null;
        }
        else {
            // Usual case.
            modifiers.add(modifier);
            lastModifierType = modifierType;
        }
    }

    @Override
    public void modify(Element element) {
        for (ElementModifier modifier : modifiers) {
            modifier.modify(element);
        }
    }

    @Override
    public boolean requiresContainer() {
        return requiresContainer;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("modifiers", modifiers)
                .toString();
    }

}
