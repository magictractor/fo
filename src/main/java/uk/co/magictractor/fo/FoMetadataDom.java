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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import uk.co.magictractor.fo.namespace.Namespaces;

/**
 * <pre>{@code
<fo:declarations xmlns:x="adobe:ns:meta/" xmlns:rdf=
"http://www.w3.org/1999/02/22-rdf-syntax-ns#"  xmlns:dc=
"http://purl.org/dc/elements/1.1/" xmlns:xmp=
"http://ns.adobe.com/xap/1.0/"  xmlns:pdf="http://ns.adobe.com/pdf/1.3/">
  <x:xmpmeta>
    <rdf:RDF>
      <rdf:Description>
        <dc:title>!!TITLE!!</dc:title>
        <dc:creator>!!AUTHOR!!</dc:creator>
        <dc:description>!!SUBJECT!!</dc:description>
        <dc:publisher>??PUBLISHER??</dc:publisher>
        <pdf:Producer>!!PRODUCER!!</pdf:Producer>
        <!-- Where did the quote come from?? -->
        <pdf:Keywords>Quality control; Laboratory reference material; Mussel tissue; Polycyclic aromatic hydrocarbons; Trace metals</pdf:Keywords>
        <pdf:PDFVersion>0.0.1</pdf:PDFVersion>
        <pdf:Trapped>true</pdf:Trapped>
        <xmp:CreatorTool>!!CREATOR!!</xmp:CreatorTool>
        <!-- appended to pdf:Keywords when both present -->
        <xmp:Keywords>!!KEYWORDS!!</xmp:Keywords>
        <xmp:Producer>!!XMP_PRODUCER!!</xmp:Producer>
        <xmp:CreateDate>2008-09-16T08:43:43-07:00</xmp:CreateDate>
        <xmp:ModifyDate>2022-01-21T12:00:00+01:00</xmp:ModifyDate>
      </rdf:Description>
    </rdf:RDF>
  </x:xmpmeta>

  <fox:info xmlns:fox="http://xmlgraphics.apache.org/fop/extensions/pdf">
    <fox:name key="MyProperty">!!CUSTOM1 VALUE!!</fox:name>
    <fox:name key="MyOtherProperty">!!CUSTOM2 VALUE!!</fox:name>
  </fox:info>
</fo:declarations>
}</pre>
 */
public class FoMetadataDom implements FoMetadata {

    private final Document domDocument;
    private final Namespaces namespaces;
    private final Consumer<Element> createdElementCallback;

    // Ternary: null for unknown, empty for not in DOM (reading), else present
    private Optional<Element> rdfDescription;
    private Optional<Element> foxInfo;

    public FoMetadataDom(Document domDocument, Namespaces namespaces) {
        this(domDocument, namespaces, (element) -> {
            throw new IllegalStateException("Attempted to modify immutable instance of " + FoMetadataDom.class.getSimpleName());
        });
    }

    public FoMetadataDom(Document domDocument, Namespaces namespaces, Consumer<Element> createdElementCallback) {
        if (domDocument == null) {
            throw new IllegalArgumentException("Document must not be null");
        }
        if (createdElementCallback == null) {
            throw new IllegalArgumentException("Created element callback must not be null (but it could be a stub or throw an exception)");
        }
        this.domDocument = domDocument;
        this.namespaces = namespaces;
        this.createdElementCallback = createdElementCallback;
    }

    @Override
    public String getTitle() {
        return getRdfDescriptionString(namespaces.dc().qName("title"));
    }

    public void setTitle(String title) {
        setRdfDescriptionString(namespaces.dc().qName("title"), title);
    }

    @Override
    public String getAuthor() {
        return getRdfDescriptionString(namespaces.dc().qName("creator"));
    }

    public void setAuthor(String author) {
        setRdfDescriptionString(namespaces.dc().qName("creator"), author);
    }

    @Override
    public String getSubject() {
        return getRdfDescriptionString(namespaces.dc().qName("description"));
    }

    public void setSubject(String subject) {
        setRdfDescriptionString(namespaces.dc().qName("description"), subject);
    }

    // TODO! where to get/set keywords - there are two options pdf:Keywords and xmp:Keywords
    @Override
    public String getKeywords() {
        return getRdfDescriptionString(namespaces.pdf().qName("Keywords"));
    }

    public void setKeywords(String keywords) {
        setRdfDescriptionString(namespaces.pdf().qName("Keywords"), keywords);
    }

    /**
     * <p>
     * Appears as "Application" in Acrobat Reader's Document Properties.
     * </p>
     * <p>
     * Apache FOP 2.9 defaults this to "Apache FOP Version SVN" if null, use
     * empty string for no value.
     * </p>
     */
    @Override
    public String getCreator() {
        return getRdfDescriptionString(namespaces.xmp().qName("CreatorTool"));
    }

    public void setCreator(String creator) {
        setRdfDescriptionString(namespaces.xmp().qName("CreatorTool"), creator);
    }

    /**
     * <p>
     * Appears as "Application" in Acrobat Reader's Document Properties.
     * </p>
     * <p>
     * Apache FOP 2.9 defaults this to "Apache FOP Version SVN" if null, use
     * empty string for no value.
     * </p>
     */
    @Override
    public String getProducer() {
        return getRdfDescriptionString(namespaces.pdf().qName("Producer"));
    }

    public void setProducer(String producer) {
        setRdfDescriptionString(namespaces.pdf().qName("Producer"), producer);
    }

    // TODO creation and modification date
    // Date, LocalDateTime or ZonedDateTime??
    // https://stackoverflow.com/questions/32274369/how-to-convert-zoneddatetime-to-date
    // what is stored in the PDF and presented in Acrobat Reader? Reader does not indicate the timezone. Does it convert to local?

    // TODO! maybe round date (e.g. truncate to nearest minute/hour/day) - configurable?

    @Override
    public ZonedDateTime getCreationDate() {
        return getRdfDescriptionZonedDateTime(namespaces.xmp().qName("CreateDate"));
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        setRdfDescriptionZonedDateTime(namespaces.xmp().qName("CreateDate"), creationDate);
    }

    @Override
    public ZonedDateTime getModificationDate() {
        return getRdfDescriptionZonedDateTime(namespaces.xmp().qName("ModifyDate"));
    }

    public void setModificationDate(ZonedDateTime modificationDate) {
        setRdfDescriptionZonedDateTime(namespaces.xmp().qName("ModifyDate"), modificationDate);
    }

    private String getRdfDescriptionString(QName qName) {
        ensureRdfDescription(false);
        if (!rdfDescription.isPresent()) {
            return null;
        }

        Element valueElement = DomUtil.findChildNullable(rdfDescription.get(), qName);
        return valueElement == null ? null : valueElement.getTextContent();
    }

    private void setRdfDescriptionString(QName qName, String value) {
        ensureRdfDescription(true);

        DomUtil.addNamespace(rdfDescription.get(), qName.getPrefix(), qName.getNamespaceURI());

        Element valueElement = DomUtil.findOrCreateChild(rdfDescription.get(), qName, createdElementCallback);
        valueElement.setTextContent(value);
    }

    private ZonedDateTime getRdfDescriptionZonedDateTime(QName qName) {
        String string = getRdfDescriptionString(qName);
        return string == null ? null : DateTimeFormatter.ISO_OFFSET_DATE_TIME.parse(string, ZonedDateTime::from);
    }

    private void setRdfDescriptionZonedDateTime(QName qName, ZonedDateTime value) {
        setRdfDescriptionString(qName, DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
    }

    // <fo:declarations xmlns:x="adobe:ns:meta/" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"  xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:xmp="http://ns.adobe.com/xap/1.0/"  xmlns:pdf="http://ns.adobe.com/pdf/1.3/">
    //   <x:xmpmeta>
    //     <rdf:RDF>
    //       <rdf:Description>
    //         <dc:title>TITLE</dc:title>
    private void ensureRdfDescription(boolean create) {
        if (create && (rdfDescription == null || !rdfDescription.isPresent())) {
            Element declarations = DomUtil.findOrCreateFoDeclarations(domDocument, createdElementCallback);
            Element xmpmeta = DomUtil.findOrCreateChild(declarations, namespaces.x().qName("xmpmeta"), createdElementCallback);
            Element rdf = DomUtil.findOrCreateChild(xmpmeta, namespaces.rdf().qName("RDF"), createdElementCallback);
            rdfDescription = Optional.of(DomUtil.findOrCreateChild(rdf, namespaces.rdf().qName("Description"), createdElementCallback));
        }
        else if (!create && rdfDescription == null) {
            Element declarations = DomUtil.findFoDeclarationsNullable(domDocument);
            if (declarations == null) {
                rdfDescription = Optional.empty();
                return;
            }

            Element xmpmeta = DomUtil.findChildNullable(declarations, namespaces.x().qName("xmpmeta"));
            if (xmpmeta == null) {
                rdfDescription = Optional.empty();
                return;
            }

            Element rdf = DomUtil.findChildNullable(xmpmeta, namespaces.rdf().qName("RDF"));
            if (rdf == null) {
                rdfDescription = Optional.empty();
                return;
            }

            rdfDescription = Optional.of(DomUtil.findChildNullable(rdf, namespaces.rdf().qName("Description")));
        }
    }

    @Override
    public String getCustomProperty(String key) {
        ensureFoxInfo(false);
        if (!foxInfo.isPresent()) {
            return null;
        }

        Element property = DomUtil.findChildNullable(foxInfo.get(), namespaces.fox().qName("name"), "key", key);
        return property == null ? null : property.getTextContent();
    }

    public void setCustomProperty(String key, String value) {
        ensureFoxInfo(true);

        Element property = DomUtil.findOrCreateChild(foxInfo.get(), namespaces.fox().qName("name"), createdElementCallback, "key", key);
        property.setTextContent(value);
    }

    private void ensureFoxInfo(boolean create) {
        if (create && (foxInfo == null || !foxInfo.isPresent())) {
            Element root = (Element) domDocument.getFirstChild();
            Element declarations = DomUtil.findOrCreateChild(root, namespaces.fo().qName("declarations"), createdElementCallback);
            foxInfo = Optional.of(DomUtil.findOrCreateChild(declarations, namespaces.fox().qName("info"), createdElementCallback));
        }
        else if (!create && foxInfo == null) {
            Element root = (Element) domDocument.getFirstChild();
            Element declarations = DomUtil.findChildNullable(root, namespaces.fo().qName("declarations"));
            if (declarations == null) {
                foxInfo = Optional.empty();
            }
            else {
                foxInfo = Optional.ofNullable(DomUtil.findChildNullable(declarations, namespaces.fox().qName("info")));
            }
        }
    }

}
