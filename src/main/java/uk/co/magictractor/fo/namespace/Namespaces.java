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
package uk.co.magictractor.fo.namespace;

/**
 * <p>
 * The {@code Namespace}s used, or that can be used, in a {@code FoDocument} or
 * {@code FoTemplate}.
 * </p>
 * <p>
 * This permits prefixes to be customised. Typically, the namespaces used in a
 * template resource file are read and the namespace prefixes there are used
 * subsequently, falling back to defaults when a namespace is used subsequently
 * that was not in the template resource file.
 * </p>
 * <table>
 * <tr>
 * <th>Default prefix</th>
 * <td></td>
 * </tr>
 * <tr>
 * <td>fo</td>
 * <td>http://www.w3.org/1999/XSL/Format</td>
 * </tr>
 * <tr>
 * <td>dc</td>
 * <td>http://purl.org/dc/elements/1.1/</td>
 * </tr>
 * <tr>
 * <td>xmp</td>
 * <td>http://ns.adobe.com/xap/1.0/</td>
 * </tr>
 * <tr>
 * <td>pdf</td>
 * <td>http://ns.adobe.com/pdf/1.3/</td>
 * </tr>
 * <tr>
 * <td>fox</td>
 * <td>http://xmlgraphics.apache.org/fop/extensions/pdf</td>
 * </tr>
 * <tr>
 * <td>x</td>
 * <td>adobe:ns:meta/</td>
 * </tr>
 * <tr>
 * <td>rdf</td>
 * <td>http://www.w3.org/1999/02/22-rdf-syntax-ns#</td>
 * </tr>
 * </table>
 *
 * @see https://xmlgraphics.apache.org/fop/2.11/metadata.html
 * @see https://xmlgraphics.apache.org/fop/2.11/extensions.html#fox-namespace
 */
public interface Namespaces {

    // xml and xmlns are reserved
    // https://www.w3.org/TR/xml-names/#ns-decl
    /**
     * <blockquote> The prefix {@code xml} is by definition bound to the
     * namespace name http://www.w3.org/XML/1998/namespace. It MAY, but need
     * not, be declared, and MUST NOT be bound to any other namespace name.
     * Other prefixes MUST NOT be bound to this namespace name, and it MUST NOT
     * be declared as the default namespace. </blockquote>
     *
     * @see https://www.w3.org/TR/xml-names/#ns-decl
     */
    static final String NAMESPACE_URI_XML = "http://www.w3.org/XML/1998/namespace";
    /**
     * <blockquote> The prefix {@code xmlns} is used only to declare namespace
     * bindings and is by definition bound to the namespace name
     * http://www.w3.org/2000/xmlns/. It MUST NOT be declared . Other prefixes
     * MUST NOT be bound to this namespace name, and it MUST NOT be declared as
     * the default namespace. Element names MUST NOT have the prefix
     * xmlns.</blockquote>
     *
     * @see https://www.w3.org/TR/xml-names/#ns-decl
     */
    static final String NAMESPACE_URI_XMLNS = "http://www.w3.org/2000/xmlns/";

    static final String NAMESPACE_URI_FO = "http://www.w3.org/1999/XSL/Format";
    static final String NAMESPACE_URI_DC = "http://purl.org/dc/elements/1.1/";
    static final String NAMESPACE_URI_XMP = "http://ns.adobe.com/xap/1.0/";
    static final String NAMESPACE_URI_PDF = "http://ns.adobe.com/pdf/1.3/";
    static final String NAMESPACE_URI_FOX = "http://xmlgraphics.apache.org/fop/extensions/pdf";
    static final String NAMESPACE_URI_X = "adobe:ns:meta/";
    static final String NAMESPACE_URI_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";

    static final String NAMESPACE_URI_MTX = "http://magictractor.co.uk/fo/extensions/pdf/1.0/";

    Namespace xml();

    Namespace xmlns();

    Namespace fo();

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/dc/
    Namespace dc();

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmp/
    Namespace xmp();

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/pdf/
    Namespace pdf();

    Namespace fox();

    // xmlns:x="adobe:ns:meta/"
    Namespace x();

    // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    Namespace rdf();

    Namespace mtx();

    Namespace forUri(String namespaceUri);

}
