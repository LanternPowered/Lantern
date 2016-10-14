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
package org.lanternpowered.server.text.gson;

class TextConstants {

    // Common

    /// Style
    static final String COLOR = "color";
    static final String BOLD = "bold";
    static final String ITALIC = "italic";
    static final String UNDERLINE = "underlined";
    static final String STRIKETHROUGH = "strikethrough";
    static final String OBFUSCATED = "obfuscated";

    /// Children
    static final String CHILDREN = "extra";

    /// Events
    static final String CLICK_EVENT = "clickEvent";
    static final String HOVER_EVENT = "hoverEvent";
    static final String EVENT_ACTION = "action";
    static final String EVENT_VALUE = "value";
    static final String INSERTION = "insertion";

    // Literal
    static final String TEXT = "text";

    // Translatable
    static final String TRANSLATABLE = "translate";
    static final String TRANSLATABLE_ARGS = "with";

    // Selector
    static final String SELECTOR = "selector";

    // Score
    static final String SCORE_NAME = "name";
    static final String SCORE_VALUE = "score";
    static final String SCORE_MAIN_OBJECTIVE = "objective";
    static final String SCORE_EXTRA_OBJECTIVES = "extraObjectives";
    static final String SCORE_OVERRIDE = "override";

    private TextConstants() {
    }
}
