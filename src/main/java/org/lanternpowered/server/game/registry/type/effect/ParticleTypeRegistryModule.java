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
package org.lanternpowered.server.game.registry.type.effect;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Color;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ NotePitchRegistryModule.class, BlockRegistryModule.class })
public class ParticleTypeRegistryModule implements CatalogRegistryModule<ParticleType> {

    @RegisterCatalog(ParticleTypes.class)
    private final Map<String, ParticleType> particleTypes = new HashMap<>();

    @Override
    public void registerDefaults() {
        List<ParticleType> types = new ArrayList<>();
        types.add(new LanternParticleType(0, "explosion_normal", true));
        types.add(new LanternParticleType.Resizable(1, "explosion_large", false, 1f));
        types.add(new LanternParticleType(2, "explosion_huge", false));
        types.add(new LanternParticleType(3, "fireworks_spark", true));
        types.add(new LanternParticleType(4, "water_bubble", true));
        types.add(new LanternParticleType(5, "water_splash", true));
        types.add(new LanternParticleType(6, "water_wake", true));
        types.add(new LanternParticleType(7, "suspended", false));
        types.add(new LanternParticleType(8, "suspended_depth", false));
        types.add(new LanternParticleType(9, "crit", true));
        types.add(new LanternParticleType(10, "crit_magic", true));
        types.add(new LanternParticleType(11, "smoke_normal", true));
        types.add(new LanternParticleType(12, "smoke_large", true));
        types.add(new LanternParticleType(13, "spell", false));
        types.add(new LanternParticleType(14, "spell_instant", false));
        types.add(new LanternParticleType.Colorable(15, "spell_mob", false, Color.BLACK));
        types.add(new LanternParticleType.Colorable(16, "spell_mob_ambient", false, Color.BLACK));
        types.add(new LanternParticleType(17, "spell_witch", false));
        types.add(new LanternParticleType(18, "drip_water", false));
        types.add(new LanternParticleType(19, "drip_lava", false));
        types.add(new LanternParticleType(20, "villager_angry", false));
        types.add(new LanternParticleType(21, "villager_happy", true));
        types.add(new LanternParticleType(22, "town_aura", true));
        types.add(new LanternParticleType.Note(23, "note", false, NotePitches.F_SHARP0));
        types.add(new LanternParticleType(24, "portal", true));
        types.add(new LanternParticleType(25, "enchantment_table", true));
        types.add(new LanternParticleType(26, "flame", true));
        types.add(new LanternParticleType(27, "lava", false));
        types.add(new LanternParticleType(28, "footstep", false));
        types.add(new LanternParticleType(29, "cloud", true));
        types.add(new LanternParticleType.Colorable(30, "redstone", false, Color.RED));
        types.add(new LanternParticleType(31, "snowball", false));
        types.add(new LanternParticleType(32, "snow_shovel", true));
        types.add(new LanternParticleType(33, "slime", false));
        types.add(new LanternParticleType(34, "heart", false));
        types.add(new LanternParticleType(35, "barrier", false));
        types.add(new LanternParticleType.Item(36, "item_crack", true, new LanternItemStack(BlockTypes.STONE)));
        types.add(new LanternParticleType.Block(37, "block_crack", true, BlockTypes.STONE.getDefaultState()));
        types.add(new LanternParticleType.Block(38, "block_dust", true, BlockTypes.STONE.getDefaultState()));
        types.add(new LanternParticleType(39, "water_drop", false));
        types.add(new LanternParticleType(40, "item_take", false));
        types.add(new LanternParticleType(41, "mob_appearance", false));
        types.forEach(value -> this.particleTypes.put(value.getId(), value));
    }

    @Override
    public Optional<ParticleType> getById(String id) {
        return Optional.ofNullable(this.particleTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<ParticleType> getAll() {
        return ImmutableSet.copyOf(this.particleTypes.values());
    }

}
