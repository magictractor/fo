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
package uk.co.magictractor.fo.unescape;

import org.springframework.web.util.HtmlUtils;

/**
 * <p>
 * Version 5 of Spring is used in this project for compatibility with JDK 8.
 * Version 6 of Spring requires at least JDK 17, but this class may be used with
 * any version of Spring after 1.2.9.
 * </p>
 * The release notes for 1.2.9 (July 2007) say: <blockquote>reimplemented
 * HtmlUtils' "htmlUnescape" to correctly handle any entity references,
 * including decimal and hex style </blockquote>
 * <p>
 * This class delegates to Spring Web's {@code HtmlUtils.htmlUnescape()}.
 * </p>
 * Known issues:
 * <ul>
 * <li>Returns incorrect values for numeric character entities for code points
 * greater than {@code 0xFFFF}. The bug is present in the latest version of
 * Spring at the time of writing (6.2.10 tested with JDK 17).
 * </p>
 *
 * @see https://github.com/spring-projects/spring-framework/issues/35426
 */
public class SpringUnescaper implements Unescaper {

    @Override
    public String unescape(String text) {
        return HtmlUtils.htmlUnescape(text);
    }

}
