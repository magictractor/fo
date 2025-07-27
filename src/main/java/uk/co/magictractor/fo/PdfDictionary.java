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

/**
 * Dictionary used for the catalog dictionary which includes options such as the
 * initial page, initial fit to window and whether the doc title is used as the
 * tab name. <pre>{@code
<fo:declarations xmlns:x="adobe:ns:meta/" xmlns:rdf=
"http://www.w3.org/1999/02/22-rdf-syntax-ns#"  xmlns:dc=
"http://purl.org/dc/elements/1.1/" xmlns:xmp=
"http://ns.adobe.com/xap/1.0/"  xmlns:pdf="http://ns.adobe.com/pdf/1.3/">
  <pdf:catalog xmlns:pdf="http://xmlgraphics.apache.org/fop/extensions/pdf">
    <!-- Worked, but not something I would use. Acrobat gives a warning dialog on opening. -->
    <!-- <pdf:name key="PageMode">FullScreen</pdf:name> -->

    <!-- Works, zero based, as mentioned on stackoverflow.com -->
    <!--
    <pdf:array key="OpenAction">
      <pdf:number>2</pdf:number>
      <pdf:name>FitH</pdf:name>
    </pdf:array>
    -->

    <pdf:dictionary type="normal" key="ViewerPreferences">
      <!-- <pdf:boolean key="DisplayDocTitle">true</pdf:boolean> -->
    </pdf:dictionary>
  </pdf:catalog>
</fo:declarations>
}</pre>
 */
// To be treated similarly to FoMetadata and written to fo:declarations
// See https://stackoverflow.com/questions/38347687/does-fop-2-1-support-viewerpreferences
// See {@code Table 28 - Entries in the catalog dictionary} on p73 (81/756)
// and table on p362 (370/756) of
// https://opensource.adobe.com/dc-acrobat-sdk-docs/pdfstandards/PDF32000_2008.pdf
// Nope - other options in sibling values with catalog.
public interface PdfDictionary {
    // Currently just a placeholder.
}
