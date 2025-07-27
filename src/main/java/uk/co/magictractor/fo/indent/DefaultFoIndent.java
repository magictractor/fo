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
package uk.co.magictractor.fo.indent;

import java.util.Arrays;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 *
 */
public class DefaultFoIndent implements FoIndent {

    private final char indentChar;
    private final int indentSize;

    private String indentsMax;
    private int indentsMaxDepth;

    // Users should create instance via the static methods on {@code FoIndent}.
    /* default */ DefaultFoIndent(char indentChar, int indentSize) {
        if (!Character.isWhitespace(indentChar)) {
            throw new IllegalArgumentException("indentChar must be whitespace");
        }
        if (indentSize < 0) {
            throw new IllegalArgumentException("indentSize must be greater than or equal to zero");
        }

        this.indentChar = indentChar;
        this.indentSize = indentSize;

        if (indentSize > 0) {
            resizeIndentsMax(indentSize + 4);
        }
        else {
            // Always just a newline.
            indentsMax = "\n";
            indentsMaxDepth = Integer.MAX_VALUE;
        }
    }

    @Override
    public char indentChar() {
        return indentChar;
    }

    @Override
    public int indentSize() {
        return indentSize;
    }

    @Override
    public String createIndent(int depth) {
        int len = 1 + depth * indentSize;
        return indentsMax.substring(0, len);
    }

    private void resizeIndentsMax(int maxDepth) {
        // Add 1 for leading newline.
        int len = 1 + (maxDepth * indentSize);
        char[] indentChars = new char[len];
        indentChars[0] = '\n';
        Arrays.fill(indentChars, 1, indentChars.length, indentChar);
        indentsMax = new String(indentChars);
        indentsMaxDepth = maxDepth;
    }

    @Override
    public void applyIndent(Element element) {
        Node parent = element.getParentNode();

        int depth = 1;
        Node ancestor = parent.getParentNode();
        while (ancestor.getNodeType() == Node.ELEMENT_NODE) {
            depth++;
            ancestor = ancestor.getParentNode();
        }

        Text indent = ((Document) ancestor).createTextNode(createIndent(depth));
        parent.insertBefore(indent, element);

        if (parent.getChildNodes().getLength() == 2) {
            // First and only element, so an indent is also needed before closing the parent.
            indent = ((Document) ancestor).createTextNode(createIndent(depth - 1));
            parent.appendChild(indent);
        }
    }

}
