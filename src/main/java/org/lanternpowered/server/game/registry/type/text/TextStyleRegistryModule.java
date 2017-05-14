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
package org.lanternpowered.server.game.registry.type.text;

import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.game.registry.SimpleCatalogRegistryModule;
import org.lanternpowered.server.text.TextConstants;
import org.lanternpowered.server.text.format.LanternTextStyle;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

public final class TextStyleRegistryModule extends SimpleCatalogRegistryModule<TextStyle.Base> {

    public TextStyleRegistryModule() {
        super(TextStyles.class);
    }

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        register(new LanternTextStyle.Formatting("bold", true, null, null, null, null, TextConstants.BOLD));
        register(new LanternTextStyle.Formatting("italic", null, true, null, null, null, TextConstants.ITALIC));
        register(new LanternTextStyle.Formatting("underline", null, null, true, null, null, TextConstants.UNDERLINE));
        register(new LanternTextStyle.Formatting("strikethrough", null, null, null, true, null, TextConstants.STRIKETHROUGH));
        register(new LanternTextStyle.Formatting("obfuscated", null, null, null, null, true, TextConstants.OBFUSCATED));
        register(new LanternTextStyle.Formatting("reset", false, false, false, false, false, TextConstants.RESET));
        register(new LanternTextStyle("none", null, null, null, null, null));
    }
}
