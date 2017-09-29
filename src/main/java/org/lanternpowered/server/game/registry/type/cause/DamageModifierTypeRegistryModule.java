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
package org.lanternpowered.server.game.registry.type.cause;

import org.lanternpowered.server.cause.entity.damage.LanternDamageModifierType;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierType;
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes;

public class DamageModifierTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<DamageModifierType> {

    public DamageModifierTypeRegistryModule() {
        super(DamageModifierTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternDamageModifierType("minecraft", "absorption"));
        register(new LanternDamageModifierType("minecraft", "armor"));
        register(new LanternDamageModifierType("minecraft", "armor_enchantment"));
        register(new LanternDamageModifierType("minecraft", "attack_cooldown"));
        register(new LanternDamageModifierType("minecraft", "critical_hit"));
        register(new LanternDamageModifierType("minecraft", "defensive_potion_effect"));
        register(new LanternDamageModifierType("minecraft", "difficulty"));
        register(new LanternDamageModifierType("minecraft", "hard_hat"));
        register(new LanternDamageModifierType("minecraft", "magic"));
        register(new LanternDamageModifierType("minecraft", "negative_potion_effect"));
        register(new LanternDamageModifierType("minecraft", "offensive_potion_effect"));
        register(new LanternDamageModifierType("minecraft", "shield"));
        register(new LanternDamageModifierType("minecraft", "sweaping"));
        register(new LanternDamageModifierType("minecraft", "weapon_enchantment"));
    }
}
