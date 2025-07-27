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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.FoDocument;
import uk.co.magictractor.fo.FoDocumentBuilder;

public class FoDocumentBuilderTest {

    @Test
    public void testMetadata_template() {
        FoDocument doc = new FoDocumentBuilder()
                .withDocumentResource("test_metadata.fo")
                .build();

        assertThat(doc.getMetadata().getTitle()).isEqualTo("DC:TITLE");
        assertThat(doc.getMetadata().getAuthor()).isEqualTo("DC:AUTHOR");
        assertThat(doc.getMetadata().getSubject()).isEqualTo("DC:SUBJECT");
        assertThat(doc.getMetadata().getKeywords()).isEqualTo("PDF:KEYWORDS");
        assertThat(doc.getMetadata().getCreator()).isEqualTo("XMP:CREATOR");
        assertThat(doc.getMetadata().getProducer()).isEqualTo("DC:PRODUCER");
        assertThat(doc.getMetadata().getCreationDate()).isEqualTo(ZonedDateTime.of(2008, 9, 16, 8, 43, 59, 0, ZoneId.of("-07:00")));
        assertThat(doc.getMetadata().getModificationDate()).isEqualTo(ZonedDateTime.of(2022, 1, 21, 12, 0, 1, 0, ZoneId.of("+01:00")));
        assertThat(doc.getMetadata().getCustomProperty("My Property")).isEqualTo("CUSTOM VALUE ONE");
        assertThat(doc.getMetadata().getCustomProperty("My Other Property")).isEqualTo("CUSTOM VALUE TWO");
    }

    @Test
    public void testMetadata_unset() {
        FoDocument doc = new FoDocumentBuilder()
                .build();

        assertThat(doc.getMetadata().getTitle()).isNull();
        assertThat(doc.getMetadata().getAuthor()).isNull();
        assertThat(doc.getMetadata().getSubject()).isNull();
        assertThat(doc.getMetadata().getKeywords()).isNull();
        assertThat(doc.getMetadata().getCreator()).isNull();
        assertThat(doc.getMetadata().getProducer()).isNull();
        assertThat(doc.getMetadata().getCreationDate()).isNull();
        assertThat(doc.getMetadata().getModificationDate()).isNull();
        assertThat(doc.getMetadata().getCustomProperty("KEY")).isNull();
    }

    @Test
    public void testMetadataTitle() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataTitle("TITLE")
                .build();

        assertThat(doc.getMetadata().getTitle()).isEqualTo("TITLE");
    }

    @Test
    public void testMetadataAuthor() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataAuthor("AUTHOR")
                .build();

        assertThat(doc.getMetadata().getAuthor()).isEqualTo("AUTHOR");
    }

    @Test
    public void testMetadataSubject() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataSubject("SUBJECT")
                .build();

        assertThat(doc.getMetadata().getSubject()).isEqualTo("SUBJECT");
    }

    @Test
    public void testMetadataKeywords() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataKeywords("KEYWORDS")
                .build();

        assertThat(doc.getMetadata().getKeywords()).isEqualTo("KEYWORDS");
    }

    @Test
    public void testMetadataCreator() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataCreator("CREATOR")
                .build();

        assertThat(doc.getMetadata().getCreator()).isEqualTo("CREATOR");
    }

    @Test
    public void testMetadataProducer() {
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataProducer("PRODUCER")
                .build();

        assertThat(doc.getMetadata().getProducer()).isEqualTo("PRODUCER");
    }

    @Test
    public void testMetadataCreationDate() {
        ZonedDateTime now = ZonedDateTime.now();
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataCreationDate(now)
                .build();

        assertThat(doc.getMetadata().getCreationDate()).isEqualTo(now);
    }

    @Test
    public void testMetadataModificationDate() {
        ZonedDateTime now = ZonedDateTime.now();
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataModificationDate(now)
                .build();

        assertThat(doc.getMetadata().getModificationDate()).isEqualTo(now);
    }

    @Test
    public void testMetadataCustomPropertyDate() {
        ZonedDateTime now = ZonedDateTime.now();
        FoDocument doc = new FoDocumentBuilder()
                .withMetadataCustomProperty("KEY1", "VALUE1")
                .withMetadataCustomProperty("KEY2", "VALUE2")
                .build();

        assertThat(doc.getMetadata().getCustomProperty("KEY1")).isEqualTo("VALUE1");
        assertThat(doc.getMetadata().getCustomProperty("KEY2")).isEqualTo("VALUE2");
    }

}
