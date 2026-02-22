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
package uk.co.magictractor.fo.build.diff;

import java.util.ArrayList;
import java.util.List;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffAlgorithmI;
import com.github.difflib.algorithm.myers.MyersDiffWithLinearSpace;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Chunk;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.google.common.base.MoreObjects;

import uk.co.magictractor.fo.build.FoDocumentBuilder;
import uk.co.magictractor.fo.modifiers.AttributeSetter;
import uk.co.magictractor.fo.modifiers.ElementModifier;
import uk.co.magictractor.fo.modifiers.ElementModifiers;

/**
 *
 */
public class DifferenceAppender {

    // private static final Logger LOGGER = LoggerFactory.getLogger(DifferenceAppender.class);

    // private static final AttributeSetter REDUCED_SPACE_AFTER = attributeSetter("space-after", "2mm");
    private static final AttributeSetter HIGHLIGHTER_PADDING = ElementModifiers.attributeSetter("padding-left", "1pt", "padding-right", "1pt", "padding-top", "1pt");

    // MeyersDiffWithLinearSpace is more eager. This gives better results with Scarabi, showing a change at the first "[" rather than the second.
    // MeyersDiffWithLinearSpace also uses DELETE and INSERT rather than CHANGE.
    private DiffAlgorithmI<Word> algorithm = MyersDiffWithLinearSpace.factory().create(Word::equals);

    private ElementModifier styleDeleted = ElementModifiers.highlighterPastelPink();
    private ElementModifier styleInserted = ElementModifiers.highlighterPastelGreen();

    public void append(FoDocumentBuilder docBuilder, String original, String revised) {
        List<Word> originalWords = toWords(original);
        List<Word> revisedWords = toWords(revised);

        Patch<Word> diff = DiffUtils.diff(originalWords, revisedWords, algorithm, null, true);

        append(docBuilder, originalWords, diff);
    }

    private void append(FoDocumentBuilder docBuilder, List<Word> spellWords, Patch<Word> diff) {
        List<AbstractDelta<Word>> deltas = diff.getDeltas();

        for (int i = 0; i < deltas.size(); i++) {
            AbstractDelta<Word> delta = deltas.get(i);
            switch (delta.getType()) {
                case EQUAL:
                    appendChunk(docBuilder, delta.getSource());
                    break;
                case CHANGE:
                    // What might be expected to be CHANGEs are often (always?) INSERT followed by DELETE.
                    // Could post-process and create a CHANGE?
                    throw new IllegalStateException("Aah, CHANGE is used");
                // appendChunk(docBuilder, delta.getSource(), styleDeleted);
                // appendChunk(docBuilder, delta.getTarget(), styleInserted);
                // break;
                case DELETE:
                    if (!delta.getTarget().getLines().isEmpty()) {
                        throw new IllegalStateException();
                    }
                    appendChunk(docBuilder, delta.getSource(), styleDeleted);
                    break;
                case INSERT:
                    if (!delta.getSource().getLines().isEmpty()) {
                        throw new IllegalStateException();
                    }

                    // If INSERT is followed immediately by DELETE then show the DELETE first
                    // Unit test??
                    if ((i + 1) < deltas.size()
                            && DeltaType.DELETE.equals(deltas.get(i + 1).getType())
                    /* && deltas.get(i + 1). */) {
                        // WED - add Word.withoutPreamble() and always
                        // TODO! but what if they have a different preamble??
                        appendChunk(docBuilder, deltas.get(i + 1).getSource(), styleDeleted);
                        if (delta.getTarget().getLines().get(0).preamble.isEmpty()) {
                            // Unusual case, has been seen in magic range with Rune Chanter.
                            //docBuilder.appendText(" ");
                            // TODO! constant in a common file for NBSP and other common non-ASCII chars.
                            docBuilder.appendText("\u00a0");
                        }
                        appendChunk(docBuilder, delta.getTarget(), styleInserted);
                        i++;
                    }
                    else {
                        appendChunk(docBuilder, delta.getTarget(), styleInserted);
                    }
                    break;
                default:
                    throw new IllegalStateException("Code must be modified to handle DeltaType " + delta.getType());
            }
        }
    }

    private void appendChunk(FoDocumentBuilder docBuilder, Chunk<Word> chunk) {
        for (Word word : chunk.getLines()) {
            docBuilder.appendText(word.preamble);
            docBuilder.appendText(word.text);
        }
    }

    private void appendChunk(FoDocumentBuilder docBuilder, Chunk<Word> chunk, ElementModifier textModifier) {
        appendChunk(docBuilder, chunk, textModifier, false);
    }

    private void appendChunk(FoDocumentBuilder docBuilder, Chunk<Word> chunk, ElementModifier textModifier, boolean highlightFirstPreamble) {
        boolean highlightPreamble = highlightFirstPreamble;

        for (Word word : chunk.getLines()) {
            if (!highlightPreamble) {
                docBuilder.appendText(word.preamble);
                if (!word.text.isEmpty()) {
                    docBuilder.startInline(HIGHLIGHTER_PADDING, textModifier);
                    docBuilder.appendText(word.text);
                    highlightPreamble = true;
                }
            }
            else {
                docBuilder.appendText(word.preamble);
                docBuilder.appendText(word.text);
            }
        }

        if (highlightPreamble) {
            docBuilder.endInline();
        }
    }

    public List<Word> toWords(String line) {
        List<Word> result = new ArrayList<>();
        int preambleStart = 0;
        int textStart = 0;
        boolean hasText = false;
        int endWord = -1;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (ignoreInDiff(c)) {
                if (hasText) {
                    endWord = i;
                }
            }
            else {
                if (!hasText) {
                    hasText = true;
                    textStart = i;
                    if (isSingleCharWord(c)) {
                        endWord = i + 1;
                    }
                }
                else if (isSingleCharWord(c)) {
                    // Single char word which has a non-ignored char directly in front of it.
                    // This is typically a hyphen in a range. Hyphen is not treated as whitespace because
                    // sometimes it should be included in magic range, e.g. constant damage becomes range damage.

                    // Add the previous word terminated by this single char word
                    Word word = new Word(line.substring(preambleStart, textStart), line.substring(textStart, i));
                    result.add(word);

                    // And the single char word will be added by the code below
                    preambleStart = textStart = i;
                    endWord = i + 1;
                }
            }

            if (endWord >= 0) {
                Word word = new Word(line.substring(preambleStart, textStart), line.substring(textStart, endWord));
                result.add(word);
                preambleStart = endWord;
                hasText = false;
                endWord = -1;
            }
        }

        if (hasText) {
            result.add(new Word(line.substring(preambleStart, textStart), line.substring(textStart)));
        }
        else if (preambleStart < line.length()) {
            result.add(new Word(line.substring(preambleStart), ""));
        }

        return result;
    }

    /**
     * Determines characters which are to be retained in output but ignored for
     * diffs.
     */
    private boolean ignoreInDiff(char c) {
        return c == ' ' || c == ',' || c == '.' || c == '\u00a0';
    }

    private boolean isSingleCharWord(char c) {
        return c == '-' || c == '[' || c == ']';
    }

    public static class Word {
        private final String preamble;
        private final String text;
        // postamble is generally empty, but the preamble from the first Word in a change may
        // be moved to the last postamble in an equals delta before it
        private final String postamble;

        public Word(String preamble, String text) {
            if (preamble == null) {
                throw new IllegalArgumentException();
            }
            // text can be null, typically with a full stop at the end of text.
            if (text == null) {
                throw new IllegalArgumentException();
            }
            if (preamble.isEmpty() && text.isEmpty()) {
                throw new IllegalArgumentException();
            }
            this.preamble = preamble;
            this.text = text;
            this.postamble = "";
        }

        public String getPreamble() {
            return preamble;
        }

        public String getText() {
            return text;
        }

        @Override
        public boolean equals(Object obj) {
            if (!Word.class.isInstance(obj)) {
                return false;
            }
            Word other = (Word) obj;
            return this.text.equalsIgnoreCase(other.text);
        }

        @Override
        public int hashCode() {
            return text.toLowerCase().hashCode();
        }

        //        public Word withoutPreamble() {
        //            return preamble.isEmpty() ? this : new Word("", text);
        //        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .omitEmptyValues()
                    .add("preamble", preamble)
                    .add("text", text)
                    .add("postamble", postamble)
                    .toString();
        }
    }

}
