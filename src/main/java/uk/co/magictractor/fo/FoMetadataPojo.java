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
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * Metadata values. These are displayed in Acrobat Reader's Document Properties
 * dialog.
 * </p>
 * <p>
 * Every {@code FoDocument} has a {@FoMetadata}. That metadata could be
 * prepopulated with values that are likely to be the same across documents in a
 * given environment (author etc) [not implemented yet], or have all fields
 * null.
 * </p>
 * <p>
 * Metadata values are expected to be set around the same time as the body of
 * the {@code FoDocument} is appended and before output is generated.
 * </p>
 * <p>
 * Metadata values may be referred to within the text appended to
 * {@code FoDocument} using syntax such as <code>${metadata.title}</code> with a
 * SAX filter used to substitute the value when generating documents [not
 * implemented yet].
 * </p>
 * <p>
 * Metadata values are set on {@code FOUserAgent} so that they are included in
 * PDF properties. TODO! embed them in the document instead, only a subset is
 * included in FoUserAgent.
 * </p>
 *
 * @see https://xmlgraphics.apache.org/fop/2.1/metadata.html
 */
// https://developer.adobe.com/xmp/docs/XMPNamespaces/
//
// https://developer.adobe.com/xmp/docs/XMPNamespaces/XMPDataTypes/
//A date-time value is represented using a subset of the formats as defined in Date and Time Formats:
//
//YYYY
//YYYY-MM
//YYYY-MM-DD
//YYYY-MM-DDThh:mmTZD
//YYYY-MM-DDThh:mm:ssTZD
//YYYY-MM-DDThh:mm:ss.sTZD
//
// See table on p550 (558/756) of
// https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/PDF32000_2008.pdf
public class FoMetadataPojo implements FoMetadata {

    private String title;
    private String author;
    private String subject;
    private String keywords;
    private String creator;
    private String producer;
    private ZonedDateTime creationDate = ZonedDateTime.now();
    private ZonedDateTime modificationDate = null;
    private Map<String, String> customProperties = new LinkedHashMap<>();

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(ZonedDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @Override
    public ZonedDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(ZonedDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    @Override
    public String getCustomProperty(String key) {
        return customProperties.get(key);
    }

    public void setCustomProperty(String key, String value) {
        customProperties.put(key, value);
    }

}
