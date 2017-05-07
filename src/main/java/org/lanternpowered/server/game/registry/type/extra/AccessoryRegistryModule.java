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
package org.lanternpowered.server.game.registry.type.extra;

import org.lanternpowered.server.extra.accessory.Accessory;
import org.lanternpowered.server.extra.accessory.LanternTopHat;
import org.lanternpowered.server.extra.accessory.TopHats;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.data.DyeColorRegistryModule;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.registry.util.RegistrationDependency;

@RegistrationDependency(DyeColorRegistryModule.class)
public final class AccessoryRegistryModule extends PluginCatalogRegistryModule<Accessory> {

    public AccessoryRegistryModule() {
        super(TopHats.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternTopHat("minecraft", "black", DyeColors.BLACK));
        register(new LanternTopHat("minecraft", "blue", DyeColors.BLUE));
        register(new LanternTopHat("minecraft", "brown", DyeColors.BROWN));
        register(new LanternTopHat("minecraft", "cyan", DyeColors.CYAN));
        register(new LanternTopHat("minecraft", "gold"));
        register(new LanternTopHat("minecraft", "gray", DyeColors.GRAY));
        register(new LanternTopHat("minecraft", "green", DyeColors.GREEN));
        register(new LanternTopHat("minecraft", "iron"));
        register(new LanternTopHat("minecraft", "light_blue", DyeColors.LIGHT_BLUE));
        register(new LanternTopHat("minecraft", "lime", DyeColors.LIME));
        register(new LanternTopHat("minecraft", "magenta", DyeColors.MAGENTA));
        register(new LanternTopHat("minecraft", "orange", DyeColors.ORANGE));
        register(new LanternTopHat("minecraft", "pink", DyeColors.PINK));
        register(new LanternTopHat("minecraft", "purple", DyeColors.PURPLE));
        register(new LanternTopHat("minecraft", "red", DyeColors.RED));
        register(new LanternTopHat("minecraft", "silver", DyeColors.SILVER));
        register(new LanternTopHat("minecraft", "stone"));
        register(new LanternTopHat("minecraft", "white", DyeColors.WHITE));
        register(new LanternTopHat("minecraft", "wood"));
        register(new LanternTopHat("minecraft", "yellow", DyeColors.YELLOW));
    }
}
