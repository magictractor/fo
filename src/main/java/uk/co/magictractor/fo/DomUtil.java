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
package uk.co.magictractor.fo;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Consumer;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.input.ReaderInputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Utility methods for working with {@code org.w3c.dom} classes.
 */
public final class DomUtil {

    private DomUtil() {
    }

    public static Element findChild(Element parent, QName childName, String... attributes) {
        Element child = findChildNullable(parent, childName, attributes);
        if (child == null) {
            throw new IllegalArgumentException(parent + " does not have a child with name " + childName);
        }
        return child;
    }

    public static Element findOrCreateChild(Element parent, QName childName, Consumer<Element> createdElementCallback, String... attributes) {
        Element child = findChildNullable(parent, childName, attributes);
        if (child == null) {
            child = addChild(parent, childName, createdElementCallback, attributes);
        }

        return child;
    }

    private static Element addChild(Element parent, QName childName, Consumer<Element> createdElementCallback, String... attributes) {
        Element child = parent.getOwnerDocument().createElementNS(childName.getNamespaceURI(), childName.getPrefix() + ":" + childName.getLocalPart());
        for (int i = 0; i < attributes.length; i += 2) {
            child.setAttribute(attributes[i], attributes[i + 1]);
        }

        NodeList siblings = parent.getChildNodes();
        if (siblings.getLength() > 0 && siblings.item(siblings.getLength() - 1).getNodeType() == Node.TEXT_NODE) {
            // Could check for multiple text nodes at the end.
            parent.insertBefore(child, siblings.item(siblings.getLength() - 1));
        }
        else {
            parent.appendChild(child);
        }

        createdElementCallback.accept(child);

        return child;
    }

    public static Element findFoDeclarationsNullable(Document document) {
        Node root = document.getFirstChild();
        String prefix = rootPrefix(root);

        int foDeclarationsIndex = nextElementIndex(root, prefix, "declarations", 0);
        Element foDeclarations = null;

        if (foDeclarationsIndex >= 0) {
            // fo:declarations was found in the document.
            foDeclarations = (Element) root.getChildNodes().item(foDeclarationsIndex);
        }

        return foDeclarations;
    }

    public static Element findOrCreateFoDeclarations(Document document, Consumer<Element> createdElementCallback) {
        Node root = document.getFirstChild();
        String prefix = rootPrefix(root);

        int foLayoutMasterSetIndex = nextElementIndex(root, prefix, "layout-master-set", 0);

        int foDeclarationsIndex = nextElementIndex(root, prefix, "declarations", 0);
        Element foDeclarations;
        if (foDeclarationsIndex >= 0) {
            // fo:declarations was found in the document.
            foDeclarations = (Element) root.getChildNodes().item(foDeclarationsIndex);
        }
        else {
            // Insert fo:declarations. It must be after fo:layout-master-set
            // and before fo:bookmark-tree, fo:page-sequence or fo:page-sequence-wrapper.
            foDeclarations = document.createElementNS(root.lookupNamespaceURI(prefix), prefix + ":declarations");
            root.insertBefore(foDeclarations, root.getChildNodes().item(foLayoutMasterSetIndex + 1));
            createdElementCallback.accept(foDeclarations);
        }

        return foDeclarations;
    }

    private static String rootPrefix(Node root) {
        if (!"root".equals(root.getLocalName())) {
            throw new IllegalArgumentException();
        }
        return root.getPrefix();
    }

    private static int nextElementIndex(Node parent, String prefix, String localName, int start) {
        NodeList children = parent.getChildNodes();
        int index = start;
        do {
            Node candidate = children.item(index);
            if (candidate.getNodeType() == Node.ELEMENT_NODE && prefix.equals(candidate.getPrefix()) && localName.equals(candidate.getLocalName())) {
                return index;
            }
            index++;
        } while (index < children.getLength());

        return -1;
    }

    /**
     * Explicitly sets the namespace on an {@code Element}. This can be used to
     * set the namespace on an ancestor to avoid the implicit namespace
     * declaration being repeated on multiple descendants.
     * </p>
     */
    public static void addNamespace(Element element, String prefix, String namespaceURI) {
        element.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + prefix, namespaceURI);
    }

    public static Element findChildNullable(Element parent, QName childName, String... attributes) {
        if (attributes.length % 2 != 0) {
            throw new IllegalArgumentException();
        }

        NodeList children = parent.getChildNodes();
        int n = children.getLength();
        for (int i = 0; i < n; i++) {
            Node child = children.item(i);
            if (child instanceof Element) {
                Element candidate = (Element) child;
                if (matches(candidate, childName, attributes)) {
                    return candidate;
                }
            }
        }

        return null;
    }

    private static boolean matches(Element candidate, QName qName, String... attributes) {
        if (!candidate.getLocalName().equals(qName.getLocalPart())) {
            return false;
        }

        // TODO! and URI or prefix

        for (int i = 0; i < attributes.length; i += 2) {
            if (!Objects.equals(candidate.getAttribute(attributes[i]), attributes[i + 1])) {
                return false;
            }
        }

        return true;
    }

    // Remove trailing whitespace from within an Element.
    public static void stripTrailingWhiteSpace(Element element) {
        NodeList children = element.getChildNodes();
        int n = children.getLength();
        for (int i = n - 1; i >= 0; i--) {
            Node last = children.item(i);
            if (last.getNodeType() != Node.TEXT_NODE) {
                break;
            }
            if (!last.getTextContent().trim().isEmpty()) {
                break;
            }
            element.removeChild(last);
        }
    }

    public static Document parseXml(String xml) {
        return parseInputStream(new ReaderInputStream(new StringReader(xml), StandardCharsets.US_ASCII));
    }

    // Caller is responsible for closing the stream.
    public static Document parseInputStream(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        // TODO! cache the builder
        DocumentBuilder builder;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
        if (!builder.isNamespaceAware()) {
            throw new IllegalStateException("DocumentBuilder must be namespace aware");
        }
        return parseDocumentSupplier(() -> builder.parse(in));

        // return parseDocumentSupplier(() -> DocumentBuilderFactory.newDefaultNSInstance().newDocumentBuilder().parse(in));
        //return parseDocumentSupplier(() -> DocumentBuilderFactory.newInstance().setNamespaceAware(true).newDocumentBuilder().parse(in));
    }

    private static Document parseDocumentSupplier(DocumentSupplier supplier) {
        try {
            return supplier.get();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
        catch (SAXException e) {
            throw new IllegalStateException(e);
        }
    }

    @FunctionalInterface
    private static interface DocumentSupplier {
        Document get() throws SAXException, ParserConfigurationException, IOException;
    }

}
