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
 * Version 5 of Spring is currently being used for compatibility with JDK 8.
 * However, the issues with Spring's {@code HtmlUtils.htmlUnescape()} are still
 * there with the latest version of Spring at the time of writing (6.2.10 tested
 * with JDK 17).
 */
// Did not find an existing bug report in https://github.com/spring-projects/spring-framework.
public class SpringUnescaper implements Unescaper {

    @Override
    public String unescape(String text) {
        return HtmlUtils.htmlUnescape(text);
    }

}
