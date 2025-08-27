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

import java.util.UUID;

import org.w3c.dom.Element;

/**
 * <p>
 * An {@code ElementModifier} that abuses the {@code id} attribute to pass
 * values into FOP's area tree and intermediate format.
 * </p>
 * <p>
 * This is for use with custom processors that modify the area tree or
 * intermediate format. Such processors should restore the {@code id} to its
 * original value, otherwise features such as links and bookmarks won't work
 * properly.
 * </p>
 * <p>
 * FOP's architecture permits extensions. However I wasn't able to pass a custom
 * attribute into the area tree or intermediate format using a custom attribute
 * with an extension, and custom elements are fairly heavyweight to implement
 * and didn't fit the design I had in mind. TODO! link to a doc with more
 * information.
 * <p>
 * <p>
 * This {@code ElementModifier} adds additional information into the id. For
 * example, a marker that a processor should resize the element using
 * {@code id=";resize=scale-down-to-fit"}. If the element had a regular id
 * {@code id="anchor123"} then the overloaded id would be
 * {@code id="anchor123;resize=scale-down-to-fit"}. A processor could perform
 * the scaling on the area tree or intermediate format with the results passed
 * on to the usual output processors and the processor should restore the
 * {@code id}.
 * </p>
 * If this is used see TODO! for processing and restoring the overloaded
 * {@code id}.
 */
public class IdOverloadSetter implements ElementModifier {

    private static final String SEPARATOR = ";";

    private final String value;

    public IdOverloadSetter(String key, String value) {
        if (key == null || value == null) {
            throw new IllegalStateException();
        }
        if (key.contains(SEPARATOR) || key.contains("=")) {
            throw new IllegalStateException();
        }
        if (value.contains(SEPARATOR) || value.contains("=")) {
            throw new IllegalStateException();
        }
        this.value = key + "=" + value;
    }

    @Override
    public void modify(Element t) {
        String id = t.getAttribute("id");
        if (id == null || id.isEmpty()) {
            // TODO! something smaller and many more configurable than guid
            id = SEPARATOR + value + ";unique=" + UUID.randomUUID();
        }
        else {
            // If there's an id it should already be unique.
            id = id + SEPARATOR + value;
        }
        t.setAttribute("id", id);
    }

    @Override
    public boolean requiresContainer() {
        return false;
    }

}
