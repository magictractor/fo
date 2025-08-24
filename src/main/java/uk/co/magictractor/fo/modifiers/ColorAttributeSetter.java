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
package uk.co.magictractor.fo.modifiers;

import java.awt.Color;
import java.util.Arrays;

/**
 *
 */
// https://www.learnui.design/blog/the-hsb-color-system-practicioners-primer.html
//
// Maybe use JavaFX ColorAdjust? https://docs.oracle.com/javase/8/javafx/api/javafx/scene/effect/ColorAdjust.html
// JavaFX
public class ColorAttributeSetter extends AttributeSetter {

    // Note that Apache FOP does not support z-index (as of 2.11)
    // see https://xmlgraphics.apache.org/fop/compliance.html
    // Can use transparency with a fourth colour digit, but not helpful here (maybe if colours tweaked accordingly)
    // see https://xmlgraphics.apache.org/fop/2.11/extensions.html

    // Good, but slight difference with highlighted and not (see ColourDoc), try px instead
    // private static final String PADDING_TOP = "0.1em";
    // private static final String PADDING_BOTTOM = "0em";
    //  private static final String PADDING_SIDES = "0.15em";
    // private static final String PADDING_SIDES = "2em";

    private static final String PADDING_TOP = "0.15em";
    private static final String PADDING_BOTTOM = "0.15em";
    private static final String PADDING_SIDES = "0.3em";

    private static final String PADDING = PADDING_TOP + " " + PADDING_SIDES + " " + PADDING_BOTTOM + " " + PADDING_SIDES;
    private static final String SPACE_START_AND_END = "-" + PADDING_SIDES;

    //  private static final double DEFAULT_HUE_INCREMENT = 360 / 20;
    private static final double DEFAULT_INCREMENT = 1.0 / 20;

    private final String colorString;
    private Color color;
    private float[] hsb;

    // TODO! bin attributeName and rename this class
    public ColorAttributeSetter(String attributeName, String attributeValue) {
        // super(attributeName, attributeValue);
        // colorString = attributeValue;
        this(attributeValue);
    }

    public ColorAttributeSetter(String attributeValue) {
        // Aah, fourth digit 00 makes it invisible, 77 dulls highlighted text and text either side if extended far enough.
        super(false, "background-color", attributeValue,
            // "padding-left", "0.5em",
            // top right bottom left
            "padding", PADDING,
            //"border-style", "solid", "border-width", PADDING, "border-color", "#e8a7c8",
            // "z-index", "-1",
            // "margin-left", "-10px", "margin-left.precedence", "force",
            "space-start", SPACE_START_AND_END, "space-end", SPACE_START_AND_END);
        colorString = attributeValue;
    }

    // TODO! bin attributeName and rename this class
    public ColorAttributeSetter(String attributeName, Color color) {
        // super(attributeName, "#" + Integer.toHexString(color.getRGB() & 0xffffff));
        // colorString = "#" + Integer.toHexString(color.getRGB() & 0xffffff);
        this("#" + Integer.toHexString(color.getRGB() & 0xffffff));
    }

    public Color getColor() {
        if (color == null) {
            color = Color.decode(colorString);
        }
        return color;
    }

    public float[] getHSB() {
        if (hsb == null) {
            Color rgb = getColor();
            hsb = new float[3];
            Color.RGBtoHSB(rgb.getRed(), rgb.getGreen(), rgb.getBlue(), hsb);
        }
        return Arrays.copyOf(hsb, 3);
    }

    public ColorAttributeSetter increaseHue() {
        return increaseHue(DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter decreaseHue() {
        return increaseHue(-DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter increaseHue(double increment) {
        return increaseHSB(increment, 0.0, 0.0);
    }

    public ColorAttributeSetter increaseSaturation() {
        return increaseSaturation(DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter decreaseSaturation() {
        return increaseSaturation(-DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter increaseSaturation(double increment) {
        return increaseHSB(1.0, increment, 1.0);
    }

    public ColorAttributeSetter increaseBrightness() {
        return increaseBrightness(DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter decreaseBrightness() {
        return increaseBrightness(-DEFAULT_INCREMENT);
    }

    public ColorAttributeSetter increaseBrightness(double increment) {
        return increaseHSB(1.0, 1.0, increment);
    }

    public ColorAttributeSetter increaseHSB(double hueIncrement, double saturationIncrement, double brightnessIncrement) {
        float[] ajustedHSB = getHSB();

        float h = (float) (ajustedHSB[0] + hueIncrement);
        // TODO! these two should be capped at 1.0
        float s = (float) (ajustedHSB[1] + saturationIncrement);
        float b = (float) (ajustedHSB[2] + brightnessIncrement);

        // int rgb = Color.HSBtoRGB(h, s, b);

        return new ColorAttributeSetter(getAttributeName(0), Color.getHSBColor(h, s, b));
    }

}
