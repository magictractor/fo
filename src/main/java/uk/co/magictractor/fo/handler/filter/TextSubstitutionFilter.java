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
package uk.co.magictractor.fo.handler.filter;

import java.nio.CharBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import uk.co.magictractor.fo.FoDocument;

/**
 * <p>
 * Filter that substitutes placeholders like <code>${metadata.title}</code> and
 * <code>${var.profit}</code> with metadata or variable values.
 * </p>
 *
 * @deprecated use {@code VariableSubstitutionVisitor}, likely via
 *             {@code FoDocumentBuilder.TODO()}.
 */
//  TODO! this needs an elegant way to configure the substitutions (or bin it)
@Deprecated(/* forRemoval = true */)
public class TextSubstitutionFilter extends BaseContentHandlerFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextSubstitutionFilter.class);

    private static final char[] START_VAR = new char[] { '$', '{' };
    private static final char END_VAR = '}';

    private final FoDocument foDocument;

    public TextSubstitutionFilter(FoDocument foDocument, ContentHandler wrapped) {
        super(wrapped);
        this.foDocument = foDocument;
    }

    @Override
    public void characters(char ch[], int start, int length)
            throws SAXException {

        int varStartIndex = indexOf(ch, start, start + length, START_VAR);
        if (varStartIndex == -1) {
            // Usual case, no substitution required.
            super.characters(ch, start, length);
        }
        else {
            int end = start + length;
            int remainderStart = start;
            while (varStartIndex >= 0) {
                int varEndIndex = indexOf(ch, varStartIndex, end, END_VAR);
                if (varEndIndex == -1) {
                    break;
                }

                CharBuffer varName = CharBuffer.wrap(ch, varStartIndex + START_VAR.length, varEndIndex - varStartIndex - START_VAR.length);
                String varValue = lookup(varName);
                if (varValue == null) {
                    // Lookup failed.
                    varStartIndex = indexOf(ch, varEndIndex + 1, end, START_VAR);
                    // varEndIndex = varStartIndex == -1 ? -1 : indexOf(ch, varStartIndex, end, END_VAR);
                    continue;
                }

                if (remainderStart < varStartIndex) {
                    super.characters(ch, remainderStart, varStartIndex - remainderStart);
                }

                if (!varValue.isEmpty()) {
                    super.characters(varValue.toCharArray(), 0, varValue.length());
                }

                remainderStart = varEndIndex + 1;
                varStartIndex = indexOf(ch, remainderStart, end, START_VAR);
                //  varEndIndex = varStartIndex == -1 ? -1 : indexOf(ch, varStartIndex, end, END_VAR);
            }

            if (remainderStart < end) {
                super.characters(ch, remainderStart, end - remainderStart);
            }
        }
    }

    //
    protected String lookup(CharSequence var) {
        if ("metadata.title".contentEquals(var)) {
            return foDocument.getMetadata().getTitle();
        }

        // Or can return null to keep the "${var}" in the output.
        // return null;

        LOGGER.warn("Unknown variable name \"{}\", substituted with empty string", var);
        return "";
    }

    public static int indexOf(char[] ch, int start, int end, char[] target) {
        outer: for (int i = start; i < end - target.length + 1; i++) {
            if (ch[i] == target[0]) {
                for (int j = 1; j < target.length; j++) {
                    if (ch[i + j] != target[j]) {
                        continue outer;
                    }
                }
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(char[] ch, int start, int end, char target) {
        for (int i = start; i < end; i++) {
            if (ch[i] == target) {
                return i;
            }
        }
        return -1;
    }

}
