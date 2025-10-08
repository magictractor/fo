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
import java.util.function.Function;

import org.w3c.dom.Text;

import uk.co.magictractor.fo.FoDocument;

public class VariableSubstitutionVisitor implements NodeVisitor {

    private final String variableOpen;
    private final String variableClose;

    // Map to actions to allow more complex actions than just text substitution.
    // One possibility would be to allow a <fo:page-number> Element to be inserted.
    // private final Map<String, SubstitutionAction> substitutionActions;
    private Map<String, String> replacementValues = new HashMap<>();

    public VariableSubstitutionVisitor(String variableOpen, String variableClose, FoDocument document, Map<String, Function<FoDocument, String>> variableSubstitutions) {
        this.variableOpen = variableOpen;
        this.variableClose = variableClose;
        for (Map.Entry<String, Function<FoDocument, String>> variableSubstitution : variableSubstitutions.entrySet()) {
            replacementValues.put(variableOpen + variableSubstitution.getKey() + variableClose, variableSubstitution.getValue().apply(document));
        }
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

        String replacementValue = this.replacementValues.get(variable);
        String oldData = text.getData();

        StringBuilder newDataBuilder = new StringBuilder();
        if (beginIndex > 0) {
            newDataBuilder.append(oldData.substring(0, beginIndex));
        }
        newDataBuilder.append(replacementValue);
        newDataBuilder.append(oldData.substring(endIndex + variableClose.length()));

        text.setData(newDataBuilder.toString());
    }

}
