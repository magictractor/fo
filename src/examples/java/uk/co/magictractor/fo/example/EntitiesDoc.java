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
package uk.co.magictractor.fo.example;

import static uk.co.magictractor.fo.modifiers.ElementModifiers.attributeSetter;
import static uk.co.magictractor.fo.modifiers.ElementModifiers.width;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.magictractor.fo.DocIO;
import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;
import uk.co.magictractor.fo.FoTemplates;
import uk.co.magictractor.fo.handler.FoPassthroughTransform;
import uk.co.magictractor.fo.handler.FoPdfTransform;
import uk.co.magictractor.fo.modifiers.ElementModifier;
import uk.co.magictractor.fo.unescape.JSoupUnescaper;
import uk.co.magictractor.fo.unescape.Unescaper;
import uk.co.magictractor.fo.writer.FoWriterBuilder;

/**
 * <p>
 * When using a {@code FoDocumentBuilder}, elements are added to a DOM. Parsing
 * the template file to create the initial DOM does convert entities, five
 * predefined by name and others by decimal or hexidecimal unicode values.
 * </p>
 * <p>
 * A full range of characters may be added to the DOM using Strings such as
 * "\u2705" for a check mark.
 * </p>
 * <p>
 * Sometimes it is desirable to use familiar entity names such as {@code &copy;}
 * and {@code &eacute}. That can be done by using an {@code Unescaper} to
 * unescape the enties Third party libraries may be used to map entity names to
 * characters.
 * </p>
 */
// TODO! create a doc page comparing the Unescaper implementation functionality (and performance?) and refer to it in the JavaDoc here.
public class EntitiesDoc {

    private static final Log LOGGER = LogFactory.getLog(EntitiesDoc.class);

    // An ElementModifier (or similar) would be nice, but would ideally have some architectural changes.
    // For example, if the Modifier were added to a block (or even the top level of the doc), then it should
    // descendant text added later rather than requiring all text added to include an unescaper.
    // This could be done by putting some ElementModifiers onto the stack and adding and removing them
    // from a set of listeners as they are added and removed from the stack. But the stack needs to be reworked first.
    //
    // Alternatively (or additionally?), use Unescapers on the area tree or intermediate format.
    private static final Unescaper UNESCAPER = new JSoupUnescaper();

    public void createDoc() {
        FoDocumentBuilder docBuilder = new FoDocumentBuilder(FoTemplates.getTemplate())
                .withMetadataCreationDate(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS))
                .withMetadataTitle("Entities")
                .withFontUrl("file:fonts/Iansui-Regular.ttf");

        DocIO docIO = new DocIO("entities");

        appendEntitiesExamples(docBuilder);

        FoWriterBuilder writerBuilder = new FoWriterBuilder();
        writerBuilder.addTransform(new FoPassthroughTransform(), docIO);
        writerBuilder.addTransform(new FoPdfTransform(), docIO);
        // writerBuilder.addTransform(new FoAsciidocTransform(), docIO);
        writerBuilder.addTransform(new FoPassthroughTransform(), System.out);

        FoDocument foDoc = docBuilder.build();

        writerBuilder.build().write(foDoc);
    }

    private void appendEntitiesExamples(FoDocumentBuilder docBuilder) {
        // https: //en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references#Standard_public_entity_sets_for_characters
        appendEntityExample(docBuilder, "2 &gt; 1", "uses a predefined entity");

        appendEntityExample(docBuilder, "caf&eacute;", "uses named entity");
        appendEntityExample(docBuilder, "caf&#xE9;", "uses hex entity");
        appendEntityExample(docBuilder, "caf&#233;", "uses decimal entity");
        appendEntityExample(docBuilder, "&#x4e2d;&#x6587;", "Chinese", attributeSetter("font-family", "Iansui"));
    }

    private void appendEntityExample(FoDocumentBuilder docBuilder, String word, String description, ElementModifier... elementModifiers) {
        docBuilder.startParagraph();
        docBuilder.appendText(word, width("12em"));
        docBuilder.appendText(UNESCAPER.unescape(word), width("8em").andThen(elementModifiers));
        docBuilder.appendText(" " + description);
        docBuilder.endParagraph();
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new EntitiesDoc().createDoc();

        long end = System.currentTimeMillis();
        LOGGER.info("Doc creation complete in " + (end - start) + " ms");
    }

}
