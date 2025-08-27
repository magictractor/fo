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
package uk.co.magictractor.fo.modifiers;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import uk.co.magictractor.fo.Namespace;

/**
 *
 */
public class ElementModifiers {

    /**
     * Cache of ElementModifiers. A few commonly used modifiers have their own
     * field, others are stored here.
     */
    private static final Map<String, ElementModifier> CACHE = new HashMap<>();

    private static final AttributeSetter BOLD = new AttributeSetter("font-weight", "bold");
    private static final AttributeSetter ITALIC = new AttributeSetter("font-style", "italic");

    // https://stackoverflow.com/questions/22388955/foinline-wrap-option-no-wrap-not-working
    private static final AttributeSetter NO_WRAP = new AttributeSetter("keep-together.within-line", "always");
    private static final AttributeSetter PAGE_BREAK_BEFORE = new AttributeSetter("page-break-before", "always");
    private static final AttributeSetter AVOID_PAGE_BREAK_INSIDE = new AttributeSetter("page-break-inside", "avoid");
    private static final AttributeSetter KEEP_WITH_NEXT = new AttributeSetter("keep-with-next", "always");

    public static ElementModifier of(ElementModifier... modifiers) {
        CompoundElementModifier compound = new CompoundElementModifier(modifiers);
        switch (compound.size()) {
            case 0:
                return noOp();
            case 1:
                return compound.asList().get(0);
            default:
                return compound;
        }
    }

    private static <T extends ElementModifier> T getOrCreateElementModifier(String key, Supplier<T> elementModifierSupplier) {
        @SuppressWarnings("unchecked")
        T result = (T) CACHE.get(key);
        if (result == null) {
            result = elementModifierSupplier.get();
        }

        return result;
    }

    public static final AttributeSetter attributeSetter(String... keysAndValues) {
        return new AttributeSetter(keysAndValues);
    }

    public static final AttributeSetter attributeSetterNS(Namespace namespace, String... keysAndValues) {
        if (namespace == null) {
            throw new IllegalArgumentException();
        }
        return new AttributeSetter(false, namespace, keysAndValues);
    }

    public static final AttributeSetter attributeSetterRequiresContainer(String... keysAndValues) {
        return new AttributeSetter(true, keysAndValues);
    }

    public static final AttributeSetter bold() {
        return BOLD;
    }

    public static final AttributeSetter italic() {
        return ITALIC;
    }

    // Value "30mm" etc.
    public static final AttributeSetter width(String value) {
        return getOrCreateElementModifier("width_" + value, () -> new AttributeSetter(true, "width", value));
    }

    public static final AttributeSetter strikethrough() {
        // Revisit this: both STRIKETHROUGH and UNDERLINE have very fine lines and some control over that would be nice.
        return getOrCreateElementModifier("strikethrough", () -> new AttributeSetter("text-decoration", "line-through"));
    }

    public static final AttributeSetter underline() {
        // Revisit this: both STRIKETHROUGH and UNDERLINE have very fine lines and some control over that would be nice.
        return getOrCreateElementModifier("underline", () -> new AttributeSetter("text-decoration", "underline"));
    }

    public static final AttributeSetter noWrap() {
        return NO_WRAP;
    }

    /**
     * Used to force a page break before a block. This is typically used with
     * headers.
     */
    public static final AttributeSetter pageBreakBefore() {
        return PAGE_BREAK_BEFORE;
    }

    public static final AttributeSetter avoidPageBreakInside() {
        return AVOID_PAGE_BREAK_INSIDE;
    }

    /** Alias for {@link #avoidPageBreakInside}. */
    public static final AttributeSetter keepTogether() {
        return AVOID_PAGE_BREAK_INSIDE;
    }

    public static final AttributeSetter keepWithNext() {
        return KEEP_WITH_NEXT;
    }

    /**
     * Value should be a number plus units, such as {@code "5mm"} or a
     * percentage. The number may be negative for an outdent.
     *
     * @see https://www.w3.org/TR/xsl11/#start-indent
     */
    public static final AttributeSetter startIndent(String value) {
        return new AttributeSetter("start-indent", value);
    }

    /** Alias for {@link #startIndent}. */
    public static final AttributeSetter indent(String value) {
        return startIndent(value);
    }

    public static final AttributeSetter endIndent(String value) {
        return new AttributeSetter("end-indent", value);
    }

    public static final NoOpElementModifier noOp() {
        return NoOpElementModifier.INSTANCE;
    }

    public static final ResetElementModifier reset() {
        return ResetElementModifier.INSTANCE;
    }

    /*
     * Pastel colours based on RGBs from
     * https://web-highlights.com/blog/highlight-the-web-with-the-colors-of-your
     * -beloved-stabilo-highlighters.
     */
    // TODO! add methods for lighter/darker??

    public static final ElementModifier highlighterPastelGreen() {
        return getOrCreateElementModifier("highlighterPastelGreen", () -> createHighlighter("#a7e8c8"));
    }

    public static final ElementModifier highlighterPastelOrange() {
        return getOrCreateElementModifier("highlighterPastelOrange", () -> createHighlighter("#f8b6a8"));
    }

    public static final ElementModifier highlighterPastelYellow() {
        return getOrCreateElementModifier("highlighterPastelYellow", () -> createHighlighter("#fdffb4"));
    }

    public static final ElementModifier highlighterPastelPink() {
        return getOrCreateElementModifier("highlighterPastelPink", () -> createHighlighter("#f7c2d6"));
    }

    public static final ElementModifier highlighterPastelViolet() {
        return getOrCreateElementModifier("highlighterPastelViolet", () -> createHighlighter("#c3bbec"));
    }

    public static final ElementModifier highlighterPastelBlue() {
        return getOrCreateElementModifier("highlighterPastelBlue", () -> createHighlighter("#c3effc"));
    }

    private static final ElementModifier createHighlighter(String colour) {
        // return new ColorAttributeSetter("background-color", colour).andThen(ElementModifiers.attributeSetterNS(Namespace.MTX, "highlighter", colour));
        // TODO! pass in dimensions rather than colour to the id?
        return new AttributeSetter("background-color", colour).andThen(new IdOverloadSetter("highlight", colour));
    }

    private ElementModifiers() {
    }

}
