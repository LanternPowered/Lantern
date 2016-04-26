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
        List<LanternParticleType> types = new ArrayList<>();
        types.add(new LanternParticleType(0, "explosion_normal", "explode", true));
        types.add(new LanternParticleType.Resizable(1, "explosion_large", "largeexplode", false, 1f));
        types.add(new LanternParticleType(2, "explosion_huge", "hugeexplosion", false));
        types.add(new LanternParticleType(3, "fireworks_spark", "fireworksSpark", true));
        types.add(new LanternParticleType(4, "water_bubble", "bubble", true));
        types.add(new LanternParticleType(5, "water_splash", "splash", true));
        types.add(new LanternParticleType(6, "water_wake", "wake", true));
        types.add(new LanternParticleType(7, "suspended", "suspended", false));
        types.add(new LanternParticleType(8, "suspended_depth", "depthsuspend", false));
        types.add(new LanternParticleType(9, "crit", "crit", true));
        types.add(new LanternParticleType(10, "crit_magic", "magicCrit", true));
        types.add(new LanternParticleType(11, "smoke_normal", "smoke", true));
        types.add(new LanternParticleType(12, "smoke_large", "largesmoke", true));
        types.add(new LanternParticleType(13, "spell", "spell", false));
        types.add(new LanternParticleType(14, "spell_instant", "instantSpell", false));
        types.add(new LanternParticleType.Colorable(15, "spell_mob", "mobSpell", false, Color.BLACK));
        types.add(new LanternParticleType.Colorable(16, "spell_mob_ambient", "mobSpellAmbient", false, Color.BLACK));
        types.add(new LanternParticleType(17, "spell_witch", "witchMagic", false));
        types.add(new LanternParticleType(18, "drip_water", "dripWater", false));
        types.add(new LanternParticleType(19, "drip_lava", "dripLava", false));
        types.add(new LanternParticleType(20, "villager_angry", "angryVillager", false));
        types.add(new LanternParticleType(21, "villager_happy", "happyVillager", true));
        types.add(new LanternParticleType(22, "town_aura", "townaura", true));
        types.add(new LanternParticleType.Note(23, "note", "note", false, NotePitches.F_SHARP0));
        types.add(new LanternParticleType(24, "portal", "portal", true));
        types.add(new LanternParticleType(25, "enchantment_table", "enchantmenttable", true));
        types.add(new LanternParticleType(26, "flame", "flame", true));
        types.add(new LanternParticleType(27, "lava", "lava", false));
        types.add(new LanternParticleType(28, "footstep", "footstep", false));
        types.add(new LanternParticleType(29, "cloud", "cloud", true));
        types.add(new LanternParticleType.Colorable(30, "redstone", "reddust", false, Color.RED));
        types.add(new LanternParticleType(31, "snowball", "snowballpoof", false));
        types.add(new LanternParticleType(32, "snow_shovel", "snowshovel", true));
        types.add(new LanternParticleType(33, "slime", "slime", false));
        types.add(new LanternParticleType(34, "heart", "heart", false));
        types.add(new LanternParticleType(35, "barrier", "barrier", false));
        types.add(new LanternParticleType.Item(36, "item_crack", "iconcrack", true, new LanternItemStack(BlockTypes.STONE)));
        types.add(new LanternParticleType.Block(37, "block_crack", "blockcrack", true, BlockTypes.STONE.getDefaultState()));
        types.add(new LanternParticleType.Block(38, "block_dust", "blockdust", true, BlockTypes.STONE.getDefaultState()));
        types.add(new LanternParticleType(39, "water_drop", "droplet", false));
        types.add(new LanternParticleType(40, "item_take", "take", false));
        types.add(new LanternParticleType(41, "mob_appearance", "mobappearance", false));
        types.forEach(value -> {
            this.particleTypes.put(value.getVanillaId(), value);
            this.particleTypes.put(value.getId(), value);
        });
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
