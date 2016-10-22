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
import org.lanternpowered.server.text.format.LanternTextColor;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;

public final class TextColorRegistryModule extends SimpleCatalogRegistryModule<TextColor> {

    public TextColorRegistryModule() {
        super(TextColors.class);
    }

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        register(new LanternTextColor("black", Color.BLACK, TextConstants.BLACK));
        register(new LanternTextColor("dark_blue", Color.ofRgb(0x0000AA), TextConstants.DARK_BLUE));
        register(new LanternTextColor("dark_green", Color.ofRgb(0x00AA00), TextConstants.DARK_GREEN));
        register(new LanternTextColor("dark_aqua", Color.ofRgb(0x00AAAA), TextConstants.DARK_AQUA));
        register(new LanternTextColor("dark_red", Color.ofRgb(0xAA0000), TextConstants.DARK_RED));
        register(new LanternTextColor("dark_purple", Color.ofRgb(0xAA00AA), TextConstants.DARK_PURPLE));
        register(new LanternTextColor("gold", Color.ofRgb(0xFFAA00), TextConstants.GOLD));
        register(new LanternTextColor("gray", Color.ofRgb(0xAAAAAA), TextConstants.GRAY));
        register(new LanternTextColor("dark_gray", Color.ofRgb(0x555555), TextConstants.DARK_GRAY));
        register(new LanternTextColor("blue", Color.ofRgb(0x5555FF), TextConstants.BLUE));
        register(new LanternTextColor("green", Color.ofRgb(0x55FF55), TextConstants.GREEN));
        register(new LanternTextColor("aqua", Color.ofRgb(0x55FFFF), TextConstants.AQUA));
        register(new LanternTextColor("red", Color.ofRgb(0xFF5555), TextConstants.RED));
        register(new LanternTextColor("light_purple", Color.ofRgb(0xFF55FF), TextConstants.LIGHT_PURPLE));
        register(new LanternTextColor("yellow", Color.ofRgb(0xFFFF55), TextConstants.YELLOW));
        register(new LanternTextColor("white", Color.WHITE, TextConstants.WHITE));
        register(new LanternTextColor("reset", Color.WHITE, TextConstants.RESET));
        // Replacing the api class, ids may not be uppercase
        register(new TextColor() {
            @Override
            public String getId() {
                return "none";
            }

            @Override
            public String getName() {
                return getId();
            }

            @Override
            public Color getColor() {
                return Color.BLACK;
            }

            @Override
            public String toString() {
                return getId();
            }
        });
    }
}
