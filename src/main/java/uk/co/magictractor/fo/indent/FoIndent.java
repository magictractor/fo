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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public interface FoIndent {

    char indentChar();

    /**
     * The number of {@code char}s in a single indent. May be zero, in which
     * case all indents have a newline without spaces or tabs.
     */
    int indentSize();

    /** @return indent text: newline followed by {@code n} indents */
    //   default String createIndent(int depth) {
    //        return "\n" + Characte
    //    }
    String createIndent(int depth);

    void applyIndent(Element element);

    public static FoIndent of(char indentChar, int indentSize) {
        return new DefaultFoIndent(indentChar, indentSize);
    }

    public static FoIndent of(String indent) {
        if (indent == null) {
            throw new IllegalArgumentException();
        }
        if (indent.length() == 0) {
            return new DefaultFoIndent(' ', 0);
        }

        char c0 = indent.charAt(0);
        int len = indent.length();
        for (int i = 1; i < len; i++) {
            if (indent.charAt(i) != c0) {
                throw new IllegalArgumentException();
            }
        }

        return new DefaultFoIndent(c0, len);
    }

    public static FoIndent infer(Document document) {
        return infer(document.getDocumentElement());
    }

    //        Element body = elementStack.peek();
    //        String bodyIndent = XmlUtil.findIndent(body);
    //        String parentIndent = XmlUtil.findIndent((Element) body.getParentNode());
    //
    //        if (bodyIndent == null || parentIndent == null) {
    //            throw new IllegalStateException("Indents cannot be inferred from the template");
    //        }
    //        if (!bodyIndent.startsWith(parentIndent)) {
    //            // For example, tabs on one line and spaces on another.
    //            throw new IllegalStateException("Indents are inconsistent");
    //        }
    //
    //        indentSize = bodyIndent.length() - parentIndent.length();
    //        if (bodyIndent.length() != indentSize * (elementStack.depth() - 1)) {
    //            throw new UnsupportedOperationException(
    //                "Inconsistent sizes: indentSize=" + indentSize + ", depth=" + elementStack.depth() + " but indent size was " + bodyIndent.length());
    //        }
    //
    //        // Add 1 for leading newline.
    //        //  indentBase = 1 + bodyIndent.length() + indentSize;
    //        //  indentDepth = 1;
    //        // For now max=1 is enough. Might change if we start nesting more, maybe with fo:block-container.
    //        initIndentMax(bodyIndent.charAt(0), elementStack.depth() + 4);

    private static FoIndent infer(Element element) {
        Node outerIndentNode;
        Node innerIndentNode;

        NodeList children = element.getChildNodes();
        if (children.getLength() >= 2) {
            outerIndentNode = element.getPreviousSibling();
            if (outerIndentNode == null) {
                // Likely root (could confirm??)
                // DOM does not preserve whitespace before the root element,
                // use the whitespace before closing it instead.
                outerIndentNode = children.item(children.getLength() - 1);
            }
            innerIndentNode = children.item(0);
        }
        else {
            outerIndentNode = element.getParentNode().getPreviousSibling();
            innerIndentNode = element.getPreviousSibling();
        }

        if (outerIndentNode == null || innerIndentNode == null
                || outerIndentNode.getNodeType() != Node.TEXT_NODE || innerIndentNode.getNodeType() != Node.TEXT_NODE) {
            return null;
        }

        String outerIndent = outerIndentNode.getTextContent();
        String innerIndent = innerIndentNode.getTextContent();

        // TODO! more checks: bother start with newline, both contain same chars.
        return of(innerIndent.substring(outerIndent.length()));
    }

}
