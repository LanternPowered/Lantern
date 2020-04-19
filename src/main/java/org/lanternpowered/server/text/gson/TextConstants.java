/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
