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
package uk.co.magictractor.fo;

import uk.co.magictractor.fo.handler.FoPdfTransform;

public class Example101 {

    public static void main(String[] args) {
        FoDocumentBuilder docBuilder = new FoDocumentBuilder();
        docBuilder.appendHeading(1, "First Example");
        docBuilder.appendText("Some text.");

        // TODO! this should be implicit (end when calling build()).
        docBuilder.endDocument();
        FoDocument doc = docBuilder.build();

        FoWriterBuilder writerBuilder = new FoWriterBuilder();
        writerBuilder.addTransform(new FoPdfTransform(), new DocIO("examples"));

        FoWriter writer = writerBuilder.build();
        writer.dump(doc);
    }

}
