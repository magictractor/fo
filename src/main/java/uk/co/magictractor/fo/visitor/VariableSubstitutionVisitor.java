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
package uk.co.magictractor.fo.visitor;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.w3c.dom.Text;

public class VariableSubstitutionVisitor implements NodeVisitor {

    private final String variableOpen;
    private final String variableClose;

    // Map to actions to allow more complex actions than just text substitution.
    // One possiblity would be to allow a <fo:page-number> Element to be inserted.
    private final Map<String, SubstitutionAction> substitutionActions = new HashMap<>();

    public VariableSubstitutionVisitor() {
        this("${", "}");
    }

    public VariableSubstitutionVisitor(String variableOpen, String variableClose) {
        if (variableOpen.isBlank() || variableClose.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.variableOpen = variableOpen;
        this.variableClose = variableClose;
    }

    public void add(String variableName, String replacement) {
        add(variableName, (text, beginIndex, endIndex) -> replaceWithString(text, beginIndex, endIndex, replacement));
    }

    public void add(String variableName, Supplier<String> replacementSupplier) {
        add(variableName, (text, beginIndex, endIndex) -> replaceWithString(text, beginIndex, endIndex, replacementSupplier.get()));
    }

    private void add(String variableName, SubstitutionAction action) {
        substitutionActions.put(variableOpen + variableName + variableClose, action);
    }

    private void replaceWithString(Text text, int beginIndex, int endIndex, String replacement) {
        String oldData = text.getData();

        StringBuilder newDataBuilder = new StringBuilder();
        if (beginIndex > 0) {
            newDataBuilder.append(oldData.substring(0, beginIndex));
        }
        newDataBuilder.append(replacement);
        newDataBuilder.append(oldData.substring(endIndex));

        text.setData(newDataBuilder.toString());
    }

    @Override
    public int visitText(Text text, int depth) {
        if (text.getData().indexOf(variableOpen) >= 0) {
            makeSubstitutions(text);
        }

        return STATUS_CONTINUE;
    }

    // TODO! multiple substitutions (potentially with nodes being inserted?)
    private void makeSubstitutions(Text text) {
        String data = text.getData();

        int fromIndex = 0;
        int beginIndex = data.indexOf(variableOpen, fromIndex);
        int endIndex = data.indexOf(variableClose, beginIndex + variableOpen.length());
        if (endIndex == -1) {
            throw new IllegalArgumentException();
        }

        String variable = data.substring(beginIndex, endIndex + variableClose.length());

        SubstitutionAction action = this.substitutionActions.get(variable);
        if (action == null) {
            throw new IllegalStateException("No substition defined for " + variable);
        }
        action.substitute(text, beginIndex, endIndex + variableClose.length());
    }

    @FunctionalInterface
    public static interface SubstitutionAction {
        // maybe remainderIndex can be -1??
        void substitute(Text text, int variableIndex, int remainderIndex);
    }

}
