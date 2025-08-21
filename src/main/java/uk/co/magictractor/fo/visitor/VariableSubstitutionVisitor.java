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

    // TODO! null or doc is pretty ugly, something more elegant should be possible
    // revisit when looking at input and output of HTML entities
    // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
    /*
     * null for templates, substitutions should only be performed on docs when
     * they are created
     */
    private final FoDocument document;

    private final String variableOpen;
    private final String variableClose;

    // Map to actions to allow more complex actions than just text substitution.
    // One possiblity would be to allow a <fo:page-number> Element to be inserted.
    // private final Map<String, SubstitutionAction> substitutionActions;
    private Map<String, Function<FoDocument, String>> replacementValueFunctions;

    public VariableSubstitutionVisitor(FoDocument document, VariableSubstitutionVisitor template) {
        this.document = document;
        this.variableOpen = template.variableOpen;
        this.variableClose = template.variableClose;
        this.replacementValueFunctions = new HashMap<>(template.replacementValueFunctions);
    }

    public VariableSubstitutionVisitor() {
        this("${", "}");
    }

    public VariableSubstitutionVisitor(String variableOpen, String variableClose) {
        //        if (variableOpen.isBlank() || variableClose.isBlank()) {
        //            throw new IllegalArgumentException();
        //        }

        this.document = null;
        this.variableOpen = variableOpen;
        this.variableClose = variableClose;
        this.replacementValueFunctions = new HashMap<>();
    }

    public void add(String variableName, Function<FoDocument, String> replacementValueFunction) {
        replacementValueFunctions.put(variableOpen + variableName + variableClose, replacementValueFunction);
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

        Function<FoDocument, String> replacementValueFunction = this.replacementValueFunctions.get(variable);
        if (replacementValueFunction == null) {
            throw new IllegalStateException("No replacement value defined for " + variable);
        }
        String replacement = replacementValueFunction.apply(document);

        String oldData = text.getData();

        StringBuilder newDataBuilder = new StringBuilder();
        if (beginIndex > 0) {
            newDataBuilder.append(oldData.substring(0, beginIndex));
        }
        newDataBuilder.append(replacement);
        newDataBuilder.append(oldData.substring(endIndex + variableClose.length()));

        text.setData(newDataBuilder.toString());
    }

}
