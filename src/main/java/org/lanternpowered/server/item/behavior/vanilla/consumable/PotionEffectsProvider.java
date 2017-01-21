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
package org.lanternpowered.server.item.behavior.vanilla.consumable;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.potion.PotionEffectHelper;
import org.lanternpowered.server.effect.potion.PotionType;
import org.lanternpowered.server.item.ObjectProvider;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class PotionEffectsProvider implements ObjectProvider<Collection<PotionEffect>> {

    @Override
    public Collection<PotionEffect> get(ItemType itemType, @Nullable ItemStack itemStack) {
        if (itemStack == null) {
            return Collections.emptyList();
        }
        final PotionType potionType = itemStack.get(LanternKeys.POTION_TYPE).orElse(null);
        List<PotionEffect> potionEffects = null;
        if (potionType != null) {
            potionEffects = potionType.getEffects();
        }
        final List<PotionEffect> extraPotionEffects = itemStack.get(Keys.POTION_EFFECTS).orElse(null);
        if (extraPotionEffects != null) {
            if (potionEffects != null) {
                potionEffects = PotionEffectHelper.merge(potionEffects, extraPotionEffects);
            } else {
                potionEffects = extraPotionEffects;
            }
        }
        return potionEffects == null ? ImmutableSet.of() : ImmutableSet.copyOf(potionEffects);
    }
}
