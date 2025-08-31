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

/**
 * <p>
 * An interface for class that convert entities such as {@code &eacute;} and
 * {@code &#xe9;} to characters.
 * </p>
 * <p>
 * Various third party libraries provide classes that can be used to convert the
 * entities. Implementations are provided that wrap Apache Commons Lang and
 * JSoup. An implementation that does not rely on any third party is also
 * provided, but that only uses the five predefined XML entity names. Wrapping
 * any other library should be straightforward.
 * </p>
 * <p>
 * The naming is similar to Guava's {@code Escaper} that converts characters to
 * entities. However, Guava does not provide code for unescaping, saying "the
 * inverse process of "unescaping" the text is performed automatically by the
 * relevant parser". TODO! link to source for the quote.
 *
 * @see https://www.javacodegeeks.com/unescape-html-characters-in-java.html
 *      TODO! links to other sources TODO! maybe link to an .adoc that compares
 *      third party libraries
 */

@FunctionalInterface
public interface Unescaper {

    String unescape(String text);

}
