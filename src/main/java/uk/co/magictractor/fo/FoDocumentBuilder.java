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

import static uk.co.magictractor.fo.modifiers.ElementModifiers.attributeSetter;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import uk.co.magictractor.fo.indent.FoIndent;
import uk.co.magictractor.fo.modifiers.ElementModifier;
import uk.co.magictractor.fo.stack.ArrayElementStack;
import uk.co.magictractor.fo.stack.ElementStack;
import uk.co.magictractor.fo.stack.ImmutableElementStack;
import uk.co.magictractor.fo.visitor.NodeVisitor;
import uk.co.magictractor.fo.visitor.VariableSubstitutionVisitor;

/**
 *
 */
public class FoDocumentBuilder {

    // Line separator character can be used to force a newline within a fo:block.
    // https://symbl.cc/en/2028/
    private static final String LINE_SEPARATOR = "\u2028";

    //    h1_font_size: round($base_font_size * 1.5)
    //    h2_font_size: round($base_font_size * 1.3)
    //    h3_font_size: round($base_font_size * 1.115)
    //    h4_font_size: $base_font_size
    //    h5_font_size: $base_font_size_small
    //    h6_font_size: $base_font_size_min
    private static final Map<String, ElementModifier> DEFAULT_ELEMENT_MODIFIER = new HashMap<>();
    //            Map.of(
    //        // space-after works here
    //        // maybe useful:
    //        // page-break-inside="avoid"
    //        // page-break-before="always" (also left/right)
    //        // keep and break: https://www.w3.org/TR/xsl11/#d0e26492
    //        // linefeed-treatment="preserve"
    //        // Spec for Spaces and Conditionality
    //        // https://www.w3.org/TR/xsl11/#spacecond
    //        "p", attributeSetter("space-after", "8pt"),
    //        "h1", attributeSetter("font-weight", "bold", "font-size", "150%", "keep-with-next", "always", "space-before", "20mm", "space-after", "12pt"),
    //        "h2", attributeSetter("font-weight", "bold", "font-size", "130%", "keep-with-next", "always", "space-after", "10pt"),
    //        "h3", attributeSetter("font-weight", "bold", "font-size", "115.5%", "keep-with-next", "always", "space-after", "5pt"),
    //        "h4", attributeSetter("font-weight", "bold", "keep-with-next", "always", "space-after", "3pt"),
    //        "h5", attributeSetter("font-weight", "bold", "font-size", "90%", "keep-with-next", "always"),
    //        "h6", attributeSetter("font-weight", "bold", "font-size", "80%", "keep-with-next", "always"));
    static {
        DEFAULT_ELEMENT_MODIFIER.put("p", attributeSetter("space-after", "8pt"));
        DEFAULT_ELEMENT_MODIFIER.put("h1", attributeSetter("font-weight", "bold", "font-size", "150%", "keep-with-next", "always", "space-before", "20mm", "space-after", "12pt"));
        DEFAULT_ELEMENT_MODIFIER.put("h2", attributeSetter("font-weight", "bold", "font-size", "130%", "keep-with-next", "always", "space-after", "10pt"));
        DEFAULT_ELEMENT_MODIFIER.put("h3", attributeSetter("font-weight", "bold", "font-size", "115.5%", "keep-with-next", "always", "space-after", "5pt"));
        DEFAULT_ELEMENT_MODIFIER.put("h4", attributeSetter("font-weight", "bold", "keep-with-next", "always", "space-after", "3pt"));
        DEFAULT_ELEMENT_MODIFIER.put("h5", attributeSetter("font-weight", "bold", "font-size", "90%", "keep-with-next", "always"));
        DEFAULT_ELEMENT_MODIFIER.put("h6", attributeSetter("font-weight", "bold", "font-size", "80%", "keep-with-next", "always"));
    }

    private Function<Document, Element> bodyFunction;

    private Document domDocument;
    private FoMetadataDom foMetadata;

    private VariableSubstitutionVisitor variableSubstitutionVisitor;

    // Stack could/should contain more info? isImplict and info about newlines...
    private ElementStack elementStack = new ArrayElementStack();
    // inline-containers have an implicit block
    private int implicitBlocksOnElementStack = 0;

    private FoIndent foIndent;

    // TODO! bin this and have a no-op FoIndent instead?
    private boolean isPrettyPrint = true;

    // If false, then appending text implies startParagraph().
    // Generally it is better to explicitly start the paragraph to allow the paragraph's attributes to be modified.
    // TODO! rename: startHeader() is also treated as a paragraph - textBlock?
    private boolean isParagraph;

    private boolean isStartOfLine = true;

    public FoDocumentBuilder(FoTemplate template) {
        // TODO! rework this, withDocumentResource() predates the constructors with args.
        withDocument0((Document) template.getDomDocument().cloneNode(true));

        if (template.getVariableSubstitutionVisitor() != null) {
            variableSubstitutionVisitor = new VariableSubstitutionVisitor(null, template.getVariableSubstitutionVisitor());
        }
        // TODO! copy indent from the template?
        foIndent = FoIndent.infer(domDocument);

    }

    /**
     * Constructor typically used for building templates from resource files.
     */
    public FoDocumentBuilder(String resourceName) {
        // TODO! rework this, withDocumentResource() predates the constructors with args.
        withDocumentResource(resourceName);
        foIndent = FoIndent.infer(domDocument);
    }

    private FoDocumentBuilder withDocumentResource(String resourceName) {
        try (InputStream in = getClass().getResourceAsStream(resourceName)) {
            if (in == null) {
                throw new IllegalArgumentException("Missing resource " + resourceName);
            }
            return withDocumentStream(in);
        }
        catch (IOException closeException) {
            throw new UncheckedIOException(closeException);
        }
    }

    // Caller is responsible for closing the stream.
    private FoDocumentBuilder withDocumentStream(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("InputStream must not be null");
        }
        return withDocument0(DomUtil.parseInputStream(in));
    }

    // a public variant should clone the doc
    private FoDocumentBuilder withDocument0(Document domDocument) {
        if (this.domDocument != null) {
            throw new IllegalArgumentException("A document has already been set");
        }

        this.domDocument = domDocument;
        Element body = getBody();
        // TODO! strip is only needed after reading a resource
        DomUtil.stripTrailingWhiteSpace(body);

        Node p = body;
        do {
            elementStack.addFirst((Element) p);
            p = p.getParentNode();
            // stops at Document
        } while (p.getNodeType() == Node.ELEMENT_NODE);

        return this;
    }

    public FoDocumentBuilder withBodyFunction(Function<Document, Element> bodyFunction) {
        // TODO! after introducing templates this will always be null
        if (this.domDocument != null) {
            throw new IllegalArgumentException("Body function should be set before the document (because the body has whitespace removed");
        }
        if (this.bodyFunction != null) {
            throw new IllegalArgumentException("A body function has already been set");
        }

        this.bodyFunction = bodyFunction;

        return this;
    }

    /**
     * The returned {@code Map} is immutable.
     */
    // TODO! allow the map to be customised per template
    // Likely a property-like resource file: template.fo.attrs
    //    public Map<String, Map<String, String>> getAttributeMap() {
    //        return DEFAULT_ATTRIBUTE_MAP;
    //    }

    private Element getBody() {
        return bodyFunction == null ? getBody0(domDocument) : bodyFunction.apply(domDocument);
    }

    // </fo:root>
    //   </fo:page-sequence>
    //     </fo:flow>
    private Element getBody0(Document domDocument) {
        Element root = domDocument.getDocumentElement();
        // TODO! add DomUtil.getQName(Element)?
        if (!"fo:root".equals(root.getNodeName())) {
            throw new IllegalArgumentException("Expected the top-level Element to be fo:root");
        }
        Element pageSequence = DomUtil.findChild(root, Namespace.FO.qName("page-sequence"));
        Element flow = DomUtil.findChild(pageSequence, Namespace.FO.qName("flow"));

        return flow;
    }

    public FoDocument build() {
        FoDocument document = new Template(domDocument, Collections.emptyList(), null);
        NodeVisitor documentVisitor = new VariableSubstitutionVisitor(document, variableSubstitutionVisitor);
        NodeVisitor.traverse(document.getDomDocument(), documentVisitor);

        return document;
    }

    public FoTemplate buildTemplate() {
        return new Template(domDocument, Collections.emptyList(), variableSubstitutionVisitor);
    }

    public Element appendHeading(int level, String text, ElementModifier... elementModifiers) {
        if (text == null) {
            throw new IllegalArgumentException("text must not be null");
        }

        Element result = startHeading(level, elementModifiers);
        appendText(text);

        // The heading element ends immediately because its creation was inferred.
        endParagraph();

        return result;
    }

    /**
     * <p>
     * Starts a heading, subsequent calls to {@code #appendText} will be added
     * to the heading. This allows more complex uses such as using inlines
     * within a header. In most cases the simpler {@link #appendHeading} can be
     * used instead.
     * </p>
     * <p>
     * All headings are assigned an {@code id}, even if the header is not
     * bookmarked (bookmarks refer to the {@code id}). This is to allow handlers
     * to determine which {@code fo:block}s are headings.
     * </p>
     */
    public Element startHeading(int level, ElementModifier... elementModifiers) {
        if (level < 1 || level > 6) {
            // This is consistent with HTML headers.
            // Asciidoc uses levels 0-5 mapping to HTML h1-h6, with 0 reserved for the title of book documents.
            // https://docs.asciidoctor.org/asciidoc/latest/sections/titles-and-levels
            throw new IllegalStateException("headerLevel must be between 1 and 6");
        }

        //        if (isParagraph) {
        //            endParagraph();
        //        }
        //
        //        Element foBlock = pushElement("h" + level, textModifiers);
        //
        //        if (text != null) {
        //            foBlock.setTextContent(text);
        //        }

        Element result = startParagraph("h" + level, elementModifiers);

        // Headings should contain only a small amount of text so keep it on the same line.
        isStartOfLine = false;

        return result;
    }

    public void endHeading() {
        // TODO! verify that a heading was at the top of the stack.
        // TODO! loop to close inlines, like para?
    }

    public void appendText(String text) {
        if (!isParagraph) {
            startParagraph();
        }

        appendText0(text);
    }

    private void appendText0(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Cannot append null");
        }

        Text textNode = domDocument.createTextNode(text);
        append(textNode);
    }

    public void appendText(String text, ElementModifier... elementModifiers) {
        startInline(elementModifiers);
        // appendText0() skips the paragraph check already done in startInline()
        appendText0(text);

        // This inline ends immediately because its creation was inferred.
        // TODO! do something better to handle implicit blocks...
        if (requiresContainer(elementModifiers)) {
            elementStack.pop();
        }
        elementStack.pop();
    }

    // no modifiers is permitted, the calling code could add attributes to the returned Element
    public Element startInline(ElementModifier... elementModifiers) {
        if (!isParagraph) {
            startParagraph();
        }

        boolean requiresContainer = requiresContainer(elementModifiers);
        String name = requiresContainer ? "inline-container" : "inline";
        Element foInline = createElementNS(name, Namespace.FO);
        for (ElementModifier elementModifier : elementModifiers) {
            elementModifier.accept(foInline);
        }

        append(foInline);
        elementStack.push(foInline);

        if (requiresContainer) {
            pushBlock(null);
            // TODO! how to close the block robustly (for now only done in appendText())
        }

        return foInline;
    }

    private boolean requiresContainer(ElementModifier... elementModifiers) {
        for (ElementModifier elementModifier : elementModifiers) {
            if (elementModifier.requiresContainer()) {
                return true;
            }
        }
        return false;
    }

    public void endInline() {
        // Can be inline-container in which case we need two pops...
        if ("block".equals(elementStack.peek().getLocalName())) {
            elementStack.pop();
            if (!"inline-container".equals(elementStack.peek().getLocalName())) {
                throw new IllegalStateException("Expected inline-container element at top of stack, but is " + elementStack.peek().getLocalName());
            }
        }
        else if (!"inline".equals(elementStack.peek().getLocalName())) {
            throw new IllegalStateException("Expected inline element at top of stack, but is " + elementStack.peek().getLocalName());
        }

        elementStack.pop();
    }

    public void startParagraph(ElementModifier... elementModifiers) {
        startParagraph("p", elementModifiers);
    }

    public Element startParagraph(String attributesKey, ElementModifier... elementModifiers) {
        if (isParagraph) {
            // maybe have a flag to configure whether implicit end is permitted?
            // throw new IllegalStateException("Paragraph has already started");
            endParagraph();
        }

        Element result = pushBlock(attributesKey, elementModifiers);

        isParagraph = true;

        return result;
    }

    public void endParagraph() {
        if (!isParagraph) {
            throw new IllegalStateException();
        }

        popBlock(true);

        isParagraph = false;
    }

    public Element startBlock(ElementModifier... elementModifiers) {
        if (isParagraph) {
            // This might change in future?
            throw new IllegalStateException("Explicit blocks are expected to wrap paragraphs.");
        }

        return pushBlock(null, elementModifiers);
    }

    public void endBlock() {
        // TODO! maybe need validation to ensure an explicit block is being closed?
        if (isParagraph) {
            endParagraph();
        }

        // could pass a param indicating no recursion?
        popBlock(true);
    }

    private void popBlock(boolean doIndents) {
        // Usually the paragraph fo:block is last,
        // but there could be stacked fo:inline Elements too,
        // so possibly multiple pops.
        Element popped;
        //
        do {
            popped = elementStack.pop();
        } while (!"block".equals(popped.getLocalName()));

        if (isPrettyPrint && doIndents) {
            popped.appendChild(createIndentNode());
            isStartOfLine = true;
        }
    }

    public void endDocument() {
        if (isParagraph) {
            endParagraph();
        }

        if (isPrettyPrint) {
            Element body = elementStack.pop();
            body.appendChild(createIndentNode());
        }

        elementStack = new ImmutableElementStack();
    }

    public void lineBreak() {
        // Unicode line separator, as suggested at
        // https://stackoverflow.com/questions/3661483/inserting-a-line-break-in-a-pdf-generated-from-xsl-fo-using-xslvalue-of
        appendText(LINE_SEPARATOR);
        isStartOfLine = true;
    }

    /**
     * <p>
     * Inserts a newline into the current paragraph or between blocks.
     * </p>
     * <p>
     * This is generally just to make the XML more human-readable. Attributes
     * can be added to blocks to preserve whitespace for rendering blocks of
     * code etc.
     * </p>
     */
    public void newline() {
        if (isStartOfLine && isPrettyPrint) {
            elementStack.peek().appendChild(domDocument.createTextNode("\n"));
        }

        isStartOfLine = true;
    }

    private Node createIndentNode() {
        String indent = foIndent.createIndent(elementStack.depth());
        return domDocument.createTextNode(indent);
    }

    // heading should open and close block on same line
    // paragraph should should have all text on lines in between
    private Element pushBlock(String attributeKey, ElementModifier... elementModifiers) {
        isStartOfLine = true;

        Element foBlock = createElementNS("block", Namespace.FO);

        if (attributeKey != null) {
            applyDefaultElementModification(foBlock, attributeKey);
        }

        for (ElementModifier elementModifier : elementModifiers) {
            elementModifier.accept(foBlock);
        }

        append(foBlock);
        elementStack.push(foBlock);

        isStartOfLine = true;

        return foBlock;
    }

    // Append Element to the last Element on the stack.
    // Does indent if appropriate.
    // Does NOT push the Element onto the stack.
    private void append(Node node) {
        if (isPrettyPrint && isStartOfLine) {
            elementStack.peek().appendChild(createIndentNode());
        }

        elementStack.peek().appendChild(node);
        isStartOfLine = false;
    }

    private void applyDefaultElementModification(Element foBlock, String attributeKey) {
        // TODO! customise attributes
        //        Map<String, String> attributes = DEFAULT_ATTRIBUTE_MAP.get(attributeKey);
        //        for (Map.Entry<String, String> kv : attributes.entrySet()) {
        //            foBlock.setAttribute(kv.getKey(), kv.getValue());
        //        }

        ElementModifier modifier = DEFAULT_ELEMENT_MODIFIER.get(attributeKey);
        modifier.accept(foBlock);
    }

    // <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    private Element createElementNS(String name, Namespace namespace) {
        return domDocument.createElementNS(namespace.getUri(), namespace.getPrefix() + ":" + name);
    }

    //// Metadata

    private FoMetadataDom getMetadata() {
        if (foMetadata == null) {
            foMetadata = new FoMetadataDom(domDocument, foIndent::applyIndent);
        }
        return foMetadata;
    }

    public FoDocumentBuilder withMetadataTitle(String title) {
        getMetadata().setTitle(title);
        return this;
    }

    public FoDocumentBuilder withMetadataAuthor(String author) {
        getMetadata().setAuthor(author);
        return this;
    }

    public FoDocumentBuilder withMetadataSubject(String subject) {
        getMetadata().setSubject(subject);
        return this;
    }

    public FoDocumentBuilder withMetadataKeywords(String keywords) {
        getMetadata().setKeywords(keywords);
        return this;
    }

    public FoDocumentBuilder withMetadataCreator(String creator) {
        getMetadata().setCreator(creator);
        return this;
    }

    public FoDocumentBuilder withMetadataProducer(String producer) {
        getMetadata().setProducer(producer);
        return this;
    }

    // TODO! maybe round date (e.g. truncate to nearest minute/hour/day) - configurable?
    public FoDocumentBuilder withMetadataCreationDate(ZonedDateTime creationDate) {
        getMetadata().setCreationDate(creationDate);
        return this;
    }

    public FoDocumentBuilder withMetadataModificationDate(ZonedDateTime modificationDate) {
        getMetadata().setModificationDate(modificationDate);
        return this;
    }

    public FoDocumentBuilder withMetadataCustomProperty(String key, String value) {
        getMetadata().setCustomProperty(key, value);
        return this;
    }

    public FoDocumentBuilder withVariableSubstitution(String variableName, String replacement) {
        return withVariableSubstitution(variableName, (doc) -> replacement);
    }

    public FoDocumentBuilder withVariableSubstitution(String variableName, Function<FoDocument, String> replacementValueFunction) {
        if (variableSubstitutionVisitor == null) {
            variableSubstitutionVisitor = new VariableSubstitutionVisitor();
        }
        variableSubstitutionVisitor.add(variableName, replacementValueFunction);
        return this;
    }

    private static class Template implements FoTemplate {

        private final Document domDocument;
        private final List<URL> fontUrls;
        private final VariableSubstitutionVisitor variableSubstitutionVisitor;

        private FoMetadata foMetadata;

        /* default */ Template(Document domDocument, List<URL> fontUrls, VariableSubstitutionVisitor variableSubstitutionVisitor) {
            this.domDocument = domDocument;
            this.fontUrls = fontUrls;
            this.variableSubstitutionVisitor = variableSubstitutionVisitor;
        }

        @Override
        public Document getDomDocument() {
            return domDocument;
        }

        @Override
        public FoMetadata getMetadata() {
            if (foMetadata == null) {
                foMetadata = new FoMetadataDom(domDocument);
            }
            return foMetadata;
        }

        @Override
        public List<URL> getFontUrls() {
            return fontUrls;
        }

        @Override
        public VariableSubstitutionVisitor getVariableSubstitutionVisitor() {
            return variableSubstitutionVisitor;
        }

    }

}
