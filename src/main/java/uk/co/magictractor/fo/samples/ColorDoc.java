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
package uk.co.magictractor.fo.samples;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import uk.co.magictractor.fo.DocIO;
import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;
import uk.co.magictractor.fo.FoTemplates;
import uk.co.magictractor.fo.handler.FoAreaTreeTransform;
import uk.co.magictractor.fo.handler.FoIntermediateFormatTransform;
import uk.co.magictractor.fo.handler.FoPassthroughTransform;
import uk.co.magictractor.fo.handler.FoPdfTransform;
import uk.co.magictractor.fo.handler.markup.FoAsciidocTransform;
import uk.co.magictractor.fo.modifiers.ElementModifier;
import uk.co.magictractor.fo.modifiers.ElementModifiers;
import uk.co.magictractor.fo.writer.FoWriterBuilder;

/**
 *
 */
public class ColorDoc {

    private static final Log LOGGER = LogFactory.getLog(ColorDoc.class);

    public void createDoc(ElementModifier... colorSetters) {
        FoDocumentBuilder docBuilder = new FoDocumentBuilder(FoTemplates.getTemplate())
                .withMetadataCreationDate(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS))
                .withMetadataTitle("Colours");

        DocIO docIO = new DocIO("colours");

        appendHighlighterExample(docBuilder);

        for (ElementModifier colorSetter : colorSetters) {
            appendColorTable(docBuilder, colorSetter);
        }

        FoWriterBuilder writerBuilder = new FoWriterBuilder();
        writerBuilder.addTransform(new FoPassthroughTransform(), docIO);
        writerBuilder.addTransform(new FoPdfTransform(), docIO);
        writerBuilder.addTransform(new FoAsciidocTransform(), docIO);
        writerBuilder.addTransform(new FoAreaTreeTransform(), docIO);
        writerBuilder.addTransform(new FoIntermediateFormatTransform(), docIO);
        writerBuilder.addTransform(new FoPassthroughTransform(), System.out);

        FoDocument foDoc = docBuilder.build();

        writerBuilder.build().dump(foDoc);
    }

    // ah, spacing is slightly changed
    // by a) padding at start if first word is highlighted (start-indent on the para does not work around this,
    // nor padding-left)
    // b) with set up just right (prefix, mono font) there's a line break earlier with the line containg highlights
    private void appendHighlighterExample(FoDocumentBuilder docBuilder) {
        boolean prefix = false;
        // ElementModifier pAattrs = ElementModifiers.attributeSetter("start-indent", "5px");
        ElementModifier pAattrs = ElementModifiers.attributeSetter("padding-left", "5px", "space-after", "0pt");
        ElementModifier hAattrs = ElementModifiers.attributeSetter();

        docBuilder.startParagraph(pAattrs);
        if (prefix) {
            docBuilder.appendText("> ");
        }
        docBuilder.appendText("Yellow, pink and green highlighted text should not change text spacing.");
        docBuilder.endParagraph();

        docBuilder.startParagraph(pAattrs);
        if (prefix) {
            docBuilder.appendText("> ");
        }
        docBuilder.appendText("Yellow", hAattrs);
        docBuilder.appendText(", ");
        docBuilder.appendText("pink", hAattrs);
        docBuilder.appendText(" and ");
        docBuilder.appendText("green", hAattrs);
        docBuilder.appendText(" highlighted text should not change text spacing.");
        docBuilder.endParagraph();

        docBuilder.startParagraph(pAattrs);
        if (prefix) {
            docBuilder.appendText("> ");
        }
        docBuilder.appendText("Yellow", ElementModifiers.highlighterPastelYellow());
        docBuilder.appendText(", ");
        docBuilder.appendText("pink", ElementModifiers.highlighterPastelPink());
        docBuilder.appendText(" and ");
        docBuilder.appendText("green", ElementModifiers.highlighterPastelGreen());
        docBuilder.appendText(" highlighted text should not change text spacing.");
        docBuilder.endParagraph();
    }

    private void appendColorTable(FoDocumentBuilder docBuilder, ElementModifier colorSetter) {
        // docBuilder.sta
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new ColorDoc().createDoc(
            ElementModifiers.highlighterPastelGreen(),
            ElementModifiers.highlighterPastelYellow(),
            ElementModifiers.highlighterPastelPink());

        long end = System.currentTimeMillis();
        LOGGER.info("Doc creation complete in " + (end - start) + " ms");
    }

}
