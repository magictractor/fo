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
package uk.co.magictractor.fo.modifiers;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;
import uk.co.magictractor.fo.FoTestTemplates;
import uk.co.magictractor.fo.handler.FoIntermediateFormatTransform;
import uk.co.magictractor.fo.writer.FoWriterBuilder;

public class IdOverloadSetterTest {

    @Test
    public void t() {
        FoDocumentBuilder docBuilder = new FoDocumentBuilder(FoTestTemplates.getTemplate());
        docBuilder.appendText("text1");
        docBuilder.startBlock(new IdOverloadSetter("test1", "value1"));
        docBuilder.appendText("text2");
        docBuilder.startBlock(new IdOverloadSetter("test2", "value2"), ElementModifiers.attributeSetter("background-color", "cyan"));
        docBuilder.appendText("text3");
        docBuilder.endBlock();
        docBuilder.appendText("text4");
        docBuilder.endBlock();
        docBuilder.appendText("text5");

        FoDocument foDoc = docBuilder.build();

        // DocIO docIO = new DocIO("test");

        FoWriterBuilder writerBuilder = new FoWriterBuilder();
        writerBuilder.addTransform(new FoIntermediateFormatTransform(), System.out);

        writerBuilder.build().dump(foDoc);
    }
    //    <?xml version="1.0" encoding="UTF-8"?><document xmlns="http://xmlgraphics.apache.org/fop/intermediate" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:nav="http://xmlgraphics.apache.org/fop/intermediate/document-navigation" xmlns:foi="http://xmlgraphics.apache.org/fop/internal" version="2.0">
    //    <header>
    //    <locale xml:lang="en"/>
    //    <x:xmpmeta xmlns:x="adobe:ns:meta/">
    //    <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
    //    <rdf:Description xmlns:xmp="http://ns.adobe.com/xap/1.0/" rdf:about="">
    //    <xmp:CreatorTool>Apache FOP Version 2.11</xmp:CreatorTool>
    //    <xmp:MetadataDate>2025-08-26T13:20:28+01:00</xmp:MetadataDate>
    //    <xmp:CreateDate>2025-08-26T13:20:28+01:00</xmp:CreateDate>
    //    </rdf:Description>
    //    </rdf:RDF>
    //    </x:xmpmeta>
    //    </header>
    //    <page-sequence xml:lang="en" xml:space="preserve">
    //    <page index="0" name="1" page-master-name="page-a4" width="595275" height="841889">
    //    <page-header/>
    //    <content>
    //    <viewport transform="translate(70866,34015)" width="490394" height="773032">
    //    <font family="serif" style="normal" weight="400" variant="normal" size="11000" color="#000000"/>
    //    <text x="0" y="9163">text1</text>
    //    <id name=";test1=value1"/>
    //    <text x="0" y="22363">text2</text>
    //    <g>
    //    <clip-rect x="0" y="26400" width="490394" height="13200"/>
    //    <rect x="0" y="26400" width="490394" height="13200" fill="#00ffff"/>
    //    </g>
    //    <id name=";test2=value2"/>
    //    <text x="0" y="35563">text3</text>
    //    <id name=""/>
    //    <text x="0" y="48763">text4</text>
    //    <text x="0" y="69963">text5</text>
    //    </viewport>
    //    </content>
    //    <page-trailer/>
    //    </page>
    //    </page-sequence>
    //    <trailer/>
    //    </document>

}
