/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.text.gson;

class TextConstants {

    // Common

    /// Style
    public static final String COLOR = "color";
    public static final String BOLD = "bold";
    public static final String ITALIC = "italic";
    public static final String UNDERLINE = "underlined";
    public static final String STRIKETHROUGH = "strikethrough";
    public static final String OBFUSCATED = "obfuscated";

    /// Children
    public static final String CHILDREN = "extra";

    /// Events
    public static final String CLICK_EVENT = "clickEvent";
    public static final String HOVER_EVENT = "hoverEvent";
    public static final String EVENT_ACTION = "action";
    public static final String EVENT_VALUE = "value";
    public static final String INSERTION = "insertion";

    // Literal
    public static final String TEXT = "text";

    // Translatable
    public static final String TRANSLATABLE = "translate";
    public static final String TRANSLATABLE_ARGS = "with";

    // Selector
    public static final String SELECTOR = "selector";

    // Score
    public static final String SCORE_NAME = "name";
    public static final String SCORE_VALUE = "score";
    public static final String SCORE_MAIN_OBJECTIVE = "objective";
    public static final String SCORE_EXTRA_OBJECTIVES = "extraObjectives";
    public static final String SCORE_OVERRIDE = "override";
}
