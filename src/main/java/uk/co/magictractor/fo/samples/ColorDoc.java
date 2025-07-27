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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.magictractor.fo.DocIO;
import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;
import uk.co.magictractor.fo.FoWriterBuilder;
import uk.co.magictractor.fo.handler.FoPassthroughTransform;
import uk.co.magictractor.fo.handler.FoPdfTransform;
import uk.co.magictractor.fo.modifiers.ColorAttributeSetter;
import uk.co.magictractor.fo.modifiers.ElementModifiers;

/**
 *
 */
public class ColorDoc {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColorDoc.class);

    public void createDoc(ColorAttributeSetter... colorSetters) {
        FoDocumentBuilder docBuilder = new FoDocumentBuilder()
                .withMetadataCreationDate(ZonedDateTime.now().truncatedTo(ChronoUnit.HOURS))
                .withMetadataTitle("Colours");

        DocIO docIO = new DocIO("colours");

        for (ColorAttributeSetter colorSetter : colorSetters) {
            appendColorTable(docBuilder, colorSetter);
        }

        FoWriterBuilder writerBuilder = new FoWriterBuilder();
        writerBuilder.addTransform(new FoPassthroughTransform(), docIO);
        writerBuilder.addTransform(new FoPdfTransform(), docIO);
        writerBuilder.addTransform(new FoPassthroughTransform(), System.out);

        FoDocument foDoc = docBuilder.build();

        writerBuilder.build().dump(foDoc);
    }

    private void appendColorTable(FoDocumentBuilder docBuilder, ColorAttributeSetter colorSetter) {
        // docBuilder.sta
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        new ColorDoc().createDoc(
            ElementModifiers.highlighterPastelGreen(),
            ElementModifiers.highlighterPastelYellow(),
            ElementModifiers.highlighterPastelPink());

        long end = System.currentTimeMillis();
        LOGGER.info("Doc creation complete in {} ms.", end - start);
    }

}
