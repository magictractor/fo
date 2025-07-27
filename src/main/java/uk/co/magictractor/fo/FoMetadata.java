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
 * SAX filter used to substitute the value when generating documents, such as
 * {@code TextSubstitutionFilter}.
 * </p>
 * <p>
 * Some metadata values may be set on {@code FOUserAgent}, but only a subset is
 * available, so that is not used. Metadata value on {@code FOUserAgent} take
 * precedence over values in the DOM.
 * </p>
 *
 * @see https://xmlgraphics.apache.org/fop/2.9/metadata.html
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
public interface FoMetadata {

    String getTitle();

    String getAuthor();

    String getSubject();

    String getKeywords();

    /**
     * <p>
     * Appears as "Application" in Acrobat Reader's Document Properties.
     * </p>
     * <p>
     * Apache FOP 2.9 defaults this to "Apache FOP Version SVN" if null, use
     * empty string for no value.
     * </p>
     */
    String getCreator();

    /**
     * <p>
     * Appears as "Application" in Acrobat Reader's Document Properties.
     * </p>
     * <p>
     * Apache FOP 2.9 defaults this to "Apache FOP Version SVN" if null, use
     * empty string for no value.
     * </p>
     */
    String getProducer();

    // TODO creation and modification date
    // Date, LocalDateTime or ZonedDateTime??
    // https://stackoverflow.com/questions/32274369/how-to-convert-zoneddatetime-to-date
    // what is stored in the PDF and presented in Acrobat Reader? Reader does not indicate the timezone. Does it convert to local?

    ZonedDateTime getCreationDate();

    ZonedDateTime getModificationDate();

    // Trapping
    // Info about what trapping is: http://nickhodge.com/blog/archives/2145

    String getCustomProperty(String key);

}
