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
public class DefaultNamespaces implements Namespaces {

    private static final DefaultNamespaces INSTANCE = new DefaultNamespaces();

    private static final Namespace XML = new Namespace("xml", NAMESPACE_URI_XML);
    private static final Namespace XMLNS = new Namespace("xmlns", NAMESPACE_URI_XMLNS);
    private static final Namespace FO = new Namespace("fo", NAMESPACE_URI_FO);
    private static final Namespace DC = new Namespace("dc", NAMESPACE_URI_DC);
    private static final Namespace XMP = new Namespace("xmp", NAMESPACE_URI_XMP);
    private static final Namespace PDF = new Namespace("pdf", NAMESPACE_URI_PDF);
    private static final Namespace FOX = new Namespace("fox", NAMESPACE_URI_FOX);
    private static final Namespace X = new Namespace("x", NAMESPACE_URI_X);
    private static final Namespace RDF = new Namespace("rdf", NAMESPACE_URI_RDF);
    private static final Namespace MTX = new Namespace("mtx", NAMESPACE_URI_MTX);

    public static DefaultNamespaces get() {
        return INSTANCE;
    }

    private DefaultNamespaces() {
    }

    @Override
    public Namespace xml() {
        return XML;
    }

    @Override
    public Namespace xmlns() {
        return XMLNS;
    }

    @Override
    public Namespace fo() {
        return FO;
    }

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/dc/
    @Override
    public Namespace dc() {
        return DC;
    }

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmp/
    @Override
    public Namespace xmp() {
        return XMP;
    }

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/pdf/
    @Override
    public Namespace pdf() {
        return PDF;
    }

    @Override
    public Namespace fox() {
        return FOX;
    }

    // xmlns:x="adobe:ns:meta/"
    @Override
    public Namespace x() {
        return X;
    }

    // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    @Override
    public Namespace rdf() {
        return RDF;
    }

    @Override
    public Namespace mtx() {
        return MTX;
    }

    @Override
    public Namespace forUri(String namespaceUri) {
        switch (namespaceUri) {
            case NAMESPACE_URI_XMLNS:
                return XMLNS;
            case NAMESPACE_URI_XML:
                return XML;
            case NAMESPACE_URI_FO:
                return FO;
            case NAMESPACE_URI_DC:
                return DC;
            case NAMESPACE_URI_XMP:
                return XMP;
            case NAMESPACE_URI_PDF:
                return PDF;
            case NAMESPACE_URI_FOX:
                return FOX;
            case NAMESPACE_URI_X:
                return X;
            case NAMESPACE_URI_RDF:
                return RDF;
            case NAMESPACE_URI_MTX:
                return MTX;
            default:
                return null;
        }
    }

}
