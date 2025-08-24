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
package uk.co.magictractor.fo;

import java.util.Map;

import uk.co.magictractor.fo.modifiers.ElementModifier;
import uk.co.magictractor.fo.visitor.VariableSubstitutionVisitor;

/**
 *
 */
public interface FoTemplate extends FoDocument {

    Map<String, ElementModifier> styleModifiers();

    /**
     * <p>
     * Visitor that is applied to the DOM when building a {@code FoDocument}
     * from this template. Typically used to replace placeholders in the footer
     * with values from metadata.
     * </p>
     * <p>
     * May be null.
     * </p>
     */
    VariableSubstitutionVisitor getVariableSubstitutionVisitor();

}
