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
package uk.co.magictractor.fo.build.diff;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import uk.co.magictractor.fo.build.diff.DifferenceAppender.Word;

public class DifferenceAppenderTest {

    private static final String EMPTY = "";
    private static final String SPACE = " ";
    private static final String COMMA = ",";
    private static final String FULL_STOP = ".";

    private DifferenceAppender diff = new DifferenceAppender();

    @Test
    public void testWords_simple() {
        List<Word> words = diff.toWords("Mary had a little lamb");
        checkText(words, "Mary", "had", "a", "little", "lamb");
        checkPreamble(words, EMPTY, SPACE, SPACE, SPACE, SPACE);
    }

    @Test
    public void testWords_punctuation() {
        List<Word> words = diff.toWords("a,b, c.");
        checkText(words, "a", "b", "c", "");
        checkPreamble(words, EMPTY, COMMA, COMMA + SPACE, FULL_STOP);
    }

    @Test
    public void testWords_squareBrackets() {
        List<Word> words = diff.toWords("[hello]");
        checkText(words, "[", "hello", "]");
        checkPreamble(words, EMPTY, EMPTY, EMPTY);
    }

    @Test
    public void testWords_squareBracketsAndSpaces() {
        List<Word> words = diff.toWords("[ hello ]");
        checkText(words, "[", "hello", "]");
        checkPreamble(words, EMPTY, SPACE, SPACE);
    }

    @Test
    public void testWords_hyphen() {
        List<Word> words = diff.toWords("2-4");
        checkText(words, "2", "-", "4");
        checkPreamble(words, EMPTY, EMPTY, EMPTY);
    }

    @Test
    public void testWords_hyphenAndSpaces() {
        List<Word> words = diff.toWords("2 - 4");
        checkText(words, "2", "-", "4");
        checkPreamble(words, EMPTY, SPACE, SPACE);
    }

    @Test
    public void testWords_magicRange() {
        List<Word> words = diff.toWords("Deal [(Magic + 3) / 2] - [Magic + 3] damage");
        checkText(words, "Deal", "[", "(Magic", "+", "3)", "/", "2", "]", "-", "[", "Magic", "+", "3", "]", "damage");
        checkPreamble(words, EMPTY, SPACE, EMPTY, SPACE, SPACE, SPACE, SPACE, EMPTY, SPACE, SPACE, EMPTY, SPACE, SPACE, EMPTY, SPACE);
    }

    private void checkText(List<Word> words, String... expectedText) {
        List<String> actualText = words.stream().map(Word::getText).collect(Collectors.toList());
        assertThat(actualText).containsExactly(expectedText);
    }

    private void checkPreamble(List<Word> words, String... expectedPreamble) {
        List<String> actualPremable = words.stream().map(Word::getPreamble).collect(Collectors.toList());
        assertThat(actualPremable).containsExactly(expectedPreamble);
    }

}
