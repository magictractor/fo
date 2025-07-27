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

import org.slf4j.LoggerFactory;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * <p>
 * Visitor implementations are likely to be stateful and single use.
 * </p>
 * Expected uses include:
 * <ul>
 * <li>Replace <code>${var}</code> with a variable value.</li>
 * <li>Replace <code>${var}</code> with an {@code Element} such as
 * {@code <fo:page-number/>}.</li>
 * </ul>
 * <p>
 * {@code DocumentTraversal} could be used instead.
 * </p>
 */
// Design: ints are used for status rather than an enum so that implementations may override visit()
// and add additional statuses
public interface NodeVisitor {

    /**
     * Continue traversal. This is the default behaviour for preChildren() and
     * postChildren().
     */
    static int STATUS_CONTINUE = 100;

    /**
     * Skip the children and postChildren() call for an DocumentNodeVisitor.
     * Only suitable as a result from preChildren().
     */
    static int STATUS_SKIP_CHILDREN = 200;

    /** Stop traversal immediately. */
    static int STATUS_DONE = 900;

    /**
     * <p>
     * Returns the visitor allowing code like:
     * </p>
     * <pre>
     * result = DocumentNodeVisitor.traverse(bounds, visitor).bestMatch();
     * </pre>
     */
    public static <V extends NodeVisitor> V traverse(Node node, V visitor) {
        visitor.visitNode(node, 0);
        // hmm, why return the visitor??
        return visitor;
    }

    default int visitNode(Node node, int depth) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE:
                return visitElement((Element) node, depth);
            case Node.TEXT_NODE:
                return visitText((Text) node, depth);
            case Node.COMMENT_NODE:
                return visitComment((Comment) node, depth);
            case Node.DOCUMENT_NODE:
                return visitDocument((Document) node, depth);
            default:
                LoggerFactory.getLogger(NodeVisitor.class).warn("Code needs modification to handle node type " + node.getNodeType() + " for " + node.getClass().getSimpleName());
                return STATUS_CONTINUE;
        }
    }

    default int visitDocument(Document document, int depth) {
        return visitElement(document.getDocumentElement(), depth + 1);
    }

    default int preChildren(Element element, int depth) {
        return STATUS_CONTINUE;
    }

    default int postChildren(Element element, int depth) {
        return STATUS_CONTINUE;
    }

    default int visitElement(Element element, int depth) {
        int preStatus = preChildren(element, depth);
        if (preStatus != STATUS_CONTINUE) {
            return preStatus;
        }

        NodeList childNodes = element.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            int childStatus = visitNode(childNode, depth + 1);
            if (childStatus == STATUS_DONE) {
                return STATUS_DONE;
            }
        }

        int postStatus = postChildren(element, depth);
        if (postStatus == STATUS_SKIP_CHILDREN) {
            throw new IllegalStateException("postChildren() should not return STATUS_SKIP_CHILDREN, children have already been visited");
        }

        // Done or Continue
        return postStatus;
    }

    default int visitText(Text text, int depth) {
        return STATUS_CONTINUE;
    }

    default int visitComment(Comment comment, int depth) {
        return STATUS_CONTINUE;
    }

}
