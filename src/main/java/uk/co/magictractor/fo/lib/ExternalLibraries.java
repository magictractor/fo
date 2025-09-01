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
package uk.co.magictractor.fo.lib;

public final class ExternalLibraries {

    public static final ExternalLibrary FOP = new ExternalLibrary("org.apache.fop.apps")
            .withGroupIdAndArtifactId("org.apache.xmlgraphics", "fop-core");

    public static final ExternalLibrary JSOUP = new ExternalLibrary("org.jsoup.parser.Parser")
            .withGroupIdAndArtifactId("org.jsoup", "jsoup");

    public static final ExternalLibrary APACHE_TEXT = new ExternalLibrary("org.apache.commons.text.StringEscapeUtils")
            .withGroupIdAndArtifactId("org.apache.commons", "commons-text");

    public static final ExternalLibrary SPRING_WEB = new ExternalLibrary("org.springframework.web.util.HtmlUtils")
            .withGroupIdAndArtifactId("org.springframework", "spring-web");

    public static final ExternalLibrary UNBESCAPE = new ExternalLibrary("uk.co.magictractor.fo.unescape.HtmlEscape")
            .withGroupIdAndArtifactId("org.unbescape", "unbescape");

    private ExternalLibraries() {
    }

}
