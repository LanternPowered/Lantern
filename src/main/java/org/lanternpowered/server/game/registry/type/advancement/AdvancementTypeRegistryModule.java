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
package org.lanternpowered.server.game.registry.type.advancement;

import org.lanternpowered.server.advancement.LanternAdvancementType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;

public class AdvancementTypeRegistryModule extends PluginCatalogRegistryModule<AdvancementType> {

    public AdvancementTypeRegistryModule() {
        super(AdvancementTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternAdvancementType("minecraft", "task", 0, TextFormat.of(TextColors.YELLOW)));
        register(new LanternAdvancementType("minecraft", "challenge", 1, TextFormat.of(TextColors.LIGHT_PURPLE)));
        register(new LanternAdvancementType("minecraft", "goal", 2, TextFormat.of(TextColors.YELLOW)));
    }
}
