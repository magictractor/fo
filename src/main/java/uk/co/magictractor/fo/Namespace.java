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

import javax.xml.namespace.QName;

/**
 *
 */
// https://www.w3schools.com/xml/xml_namespaces.asp
public class Namespace {

    public static final Namespace FO = new Namespace("fo", "http://www.w3.org/1999/XSL/Format");

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/dc/
    public static final Namespace DC = new Namespace("dc", "http://purl.org/dc/elements/1.1/");

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/xmp/
    public static final Namespace XMP = new Namespace("dc", "http://ns.adobe.com/xap/1.0/");

    // https://developer.adobe.com/xmp/docs/XMPNamespaces/pdf/
    public static final Namespace PDF = new Namespace("pdf", "http://ns.adobe.com/pdf/1.3/");

    public static final Namespace FOX = new Namespace("fox", "http://xmlgraphics.apache.org/fop/extensions/pdf");

    // xmlns:x="adobe:ns:meta/"
    public static final Namespace X = new Namespace("x", "adobe:ns:meta/");

    // xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    public static final Namespace RDF = new Namespace("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");

    private final String prefix;
    private final String uri;

    public Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getUri() {
        return uri;
    }

    public QName qName(String localName) {
        return new QName(uri, localName, prefix);
    }

}
