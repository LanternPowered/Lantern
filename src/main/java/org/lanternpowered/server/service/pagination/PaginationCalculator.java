/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.service.pagination;

import com.flowpowered.math.GenericMath;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.loader.HeaderMode;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageReceiver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.PrimitiveIterator;

/**
 * Pagination calculator for players.
 */
final class PaginationCalculator {

    private static final String NON_UNICODE_CHARS;
    private static final int[] NON_UNICODE_CHAR_WIDTHS;
    private static final byte[] UNICODE_CHAR_WIDTHS;
    private static final int LINE_WIDTH = 320;

    private final int linesPerPage;

    /**
     * Constructs a new pagination calculator.
     *
     * @param linesPerPage The amount of lines per page there should be
     */
    PaginationCalculator(int linesPerPage) {
        this.linesPerPage = linesPerPage;
    }

    static {
        final ConfigurationLoader<CommentedConfigurationNode> loader = HoconConfigurationLoader.builder()
                .setURL(PaginationCalculator.class.getResource("/internal/font_sizes.json"))
                .setHeaderMode(HeaderMode.PRESET)
                .build();
        try {
            final ConfigurationNode node = loader.load();
            NON_UNICODE_CHARS = node.getNode("non-unicode").getString();
            final List<? extends ConfigurationNode> charWidths = node.getNode("char-widths").getChildrenList();
            NON_UNICODE_CHAR_WIDTHS = new int[charWidths.size()];
            for (int i = 0; i < NON_UNICODE_CHAR_WIDTHS.length; ++i) {
                NON_UNICODE_CHAR_WIDTHS[i] = charWidths.get(i).getInt();
            }
            final List<? extends ConfigurationNode> glyphWidths = node.getNode("glyph-widths").getChildrenList();
            UNICODE_CHAR_WIDTHS = new byte[glyphWidths.size()];
            for (int i = 0; i < UNICODE_CHAR_WIDTHS.length; ++i) {
                UNICODE_CHAR_WIDTHS[i] = (byte) glyphWidths.get(i).getInt();
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public int getLinesPerPage(MessageReceiver source) {
        return this.linesPerPage;
    }

    /**
     * Gets lines per page.
     *
     * @return The amount of lines per page
     */
    public int getLinesPerPage() {
        return this.linesPerPage;
    }

    /**
     * Gets the number of lines the specified text flows into.
     *
     * @param text The text to calculate the number of lines for
     * @return The number of lines that this text flows into
     */
    public int getLines(Text text) {
        return (int) Math.ceil((double) getLength(text) / LINE_WIDTH);
    }

    /**
     * Gets the width of a character with the specified code
     * point, accounting for if its text is bold our not.
     *
     * @param codePoint The code point of the character
     * @param bold Whether or not the character is bold or not
     * @return The width of the character at the code point
     */
    private int getWidth(int codePoint, boolean bold) {
        final int nonUnicodeIdx = NON_UNICODE_CHARS.indexOf(codePoint);
        int width;
        if (codePoint == 167) {
            // Color code character, this has no width
            width = 0;
        } else if (codePoint == 32) {
            // Space
            width = 4;
        } else if (codePoint > 0 && nonUnicodeIdx != -1) {
            width = NON_UNICODE_CHAR_WIDTHS[nonUnicodeIdx];
        } else if (UNICODE_CHAR_WIDTHS[codePoint] != 0) {
            final int temp = UNICODE_CHAR_WIDTHS[codePoint] & 255;
            // Split into high and low nibbles.
            // bit digits
            // 87654321 >>> 4 = 00008765
            final int startColumn = temp >>> 4;
            // 87654321 & 00001111 = 00004321
            final int endColumn = temp & 15;

            width = (endColumn + 1) - startColumn;
            // Why does this scaling happen?
            // I believe it makes unicode fonts skinnier to better match the character widths of the default Minecraft
            // font however there is a int math vs float math bug in the Minecraft FontRenderer.
            // The float math is adjusted for rendering, they attempt to do the same thing for calculating string widths
            // using integer math, this has potential rounding errors, but we should copy it and use ints as well.
            width = (width / 2) + 1;
        } else {
            width = 0;
        }
        // If bold, the width gets 1 added
        if (bold && width > 0) {
            width++;
        }
        return width;
    }

    /**
     * Gets the length of a text.
     *
     * @param text The text to get the length of
     * @return The length of the text
     */
    private int getLength(Text text) {
        return getLength(text, false);
    }

    private int getLength(Text text, boolean bold) {
        final Boolean bold1 = text.getStyle().isBold().orElse(null);
        if (bold1 != null) {
            bold = bold1;
        }
        String txt;
        if (text instanceof LiteralText) {
            txt = ((LiteralText) text).getContent();
        } else {
            txt = text.toPlainSingle();
        }
        int length = getLength(txt, bold);
        // Get the length of all the children
        for (Text child : text.getChildren()) {
            length += getLength(child, bold);
        }
        return length;
    }

    private int getLength(String text, boolean bold) {
        final PrimitiveIterator.OfInt ofInt = text.codePoints().iterator();

        int total = 0;
        int cp;
        boolean newLine = false;
        while (ofInt.hasNext()) {
            cp = ofInt.nextInt();
            if (cp == '\n') {
                // If the previous character is a '\n'
                if (newLine) {
                    total += LINE_WIDTH;
                } else {
                    total = ((int) Math.ceil((double) total / LINE_WIDTH)) * LINE_WIDTH;
                    newLine = true;
                }
            } else {
                final int width = getWidth(cp, bold);
                total += width;
                newLine = false;
            }
        }

        return total;
    }

    /**
     * Centers a text within the middle of the chat box.
     *
     * <p>Generally used for titles and footers.</p>
     *
     * <p>To use no heading, just pass in a 0 width text for
     * the first argument.</p>
     *
     * @param text The text to center
     * @param padding A padding character with a width >1
     * @return The centered text, or if too big, the original text
     */
    public Text center(Text text, Text padding) {
        int inputLength = getLength(text);
        if (inputLength >= LINE_WIDTH) {
            return text;
        }
        final Text textWithSpaces = addSpaces(Text.of(" "), text);
        final boolean addSpaces = getLength(textWithSpaces) <= LINE_WIDTH;

        Text styledPadding = withStyle(padding, text);
        int paddingLength = getLength(styledPadding);
        final Text.Builder output = Text.builder();

        // Using 0 width unicode symbols as padding throws us into an unending loop, replace them with the default padding
        if (paddingLength < 1) {
            padding = Text.of("=");
            styledPadding = withColor(withStyle(padding, text), text);
            paddingLength = getLength(styledPadding);
        }

        // If we only need padding
        if (inputLength == 0) {
            addPadding(padding, output, GenericMath.floor((double) LINE_WIDTH / paddingLength));
        } else {
            if (addSpaces) {
                text = textWithSpaces;
                inputLength = getLength(textWithSpaces);
            }

            int paddingNecessary = LINE_WIDTH - inputLength;
            int paddingCount = GenericMath.floor(paddingNecessary / paddingLength);
            // Pick a halfway point
            int beforePadding = GenericMath.floor(paddingCount / 2.0);
            // Do not use ceil, this prevents floating point errors.
            int afterPadding = paddingCount - beforePadding;

            addPadding(styledPadding, output, beforePadding);
            output.append(text);
            addPadding(styledPadding, output, afterPadding);
        }
        return output.style(text.getStyle()).build();
    }

    private Text withStyle(Text text, Text styled) {
        return text.toBuilder().style(styled.getStyle()).build();
    }

    /**
     * Gives the first text argument the color of the second.
     *
     * @param text The text to color
     * @param colored The colored text
     * @return The original text now colored
     */
    private Text withColor(Text text, Text colored) {
        return text.toBuilder()
                .color(colored.getColor())
                .build();
    }

    /**
     * Adds spaces to both sides of the specified text.
     *
     * <p>Overrides all color and style with the
     * text's color and style.</p>
     *
     * @param spaces The spaces to use
     * @param text The text to add to
     * @return The text with the added spaces
     */
    private Text addSpaces(Text spaces, Text text) {
        return Text.builder()
                .append(spaces)
                .append(text)
                .append(spaces)
                .color(text.getColor())
                .style(text.getStyle())
                .build();
    }

    /**
     * Adds the specified padding text to a piece of text being built
     * up to a certain amount specified by a count.
     *
     * @param padding The padding text to use
     * @param build The work in progress text to add to
     * @param count The amount of padding to add
     */
    private void addPadding(Text padding, Text.Builder build, int count) {
        if (count > 0) {
            build.append(Collections.nCopies(count, padding));
        }
    }
}
