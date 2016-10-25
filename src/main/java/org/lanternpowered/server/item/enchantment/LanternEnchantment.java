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
package org.lanternpowered.server.item.enchantment;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.item.Enchantment;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.translation.Translation;

public class LanternEnchantment extends PluginCatalogType.Base.Translatable.Internal implements Enchantment {

    public LanternEnchantment(String pluginId, String name, String translation, int internalId) {
        super(pluginId, name, translation, internalId);
    }

    public LanternEnchantment(String pluginId, String name, Translation translation, int internalId) {
        super(pluginId, name, translation, internalId);
    }

    public LanternEnchantment(String pluginId, String id, String name, String translation, int internalId) {
        super(pluginId, id, name, translation, internalId);
    }

    public LanternEnchantment(String pluginId, String id, String name, Translation translation, int internalId) {
        super(pluginId, id, name, translation, internalId);
    }

    @Override
    public int getWeight() {
        return 0;
    }

    @Override
    public int getMinimumLevel() {
        return 0;
    }

    @Override
    public int getMaximumLevel() {
        return 0;
    }

    @Override
    public int getMinimumEnchantabilityForLevel(int level) {
        return 0;
    }

    @Override
    public int getMaximumEnchantabilityForLevel(int level) {
        return 0;
    }

    @Override
    public boolean canBeAppliedToStack(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeAppliedByTable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isCompatibleWith(Enchantment ench) {
        return false;
    }
}
