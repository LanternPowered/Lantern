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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
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

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ NotePitchRegistryModule.class, BlockRegistryModule.class })
public class ParticleTypeRegistryModule implements CatalogRegistryModule<ParticleType> {

    @RegisterCatalog(ParticleTypes.class)
    private final Map<String, ParticleType> particleTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        Map<String, ParticleType> mappings = Maps.newHashMap();
        mappings.put("explosion_normal", new LanternParticleType(0, "explode", true));
        mappings.put("explosion_large", new LanternParticleType.Resizable(1, "largeexplode", false, 1f));
        mappings.put("explosion_huge", new LanternParticleType(2, "hugeexplosion", false));
        mappings.put("fireworks_spark", new LanternParticleType(3, "fireworksSpark", true));
        mappings.put("water_bubble", new LanternParticleType(4, "bubble", true));
        mappings.put("water_splash", new LanternParticleType(5, "splash", true));
        mappings.put("water_wake", new LanternParticleType(6, "wake", true));
        mappings.put("suspended", new LanternParticleType(7, "suspended", false));
        mappings.put("suspended_depth", new LanternParticleType(8, "depthsuspend", false));
        mappings.put("crit", new LanternParticleType(9, "crit", true));
        mappings.put("crit_magic", new LanternParticleType(10, "magicCrit", true));
        mappings.put("smoke_normal", new LanternParticleType(11, "smoke", true));
        mappings.put("smoke_large", new LanternParticleType(12, "largesmoke", true));
        mappings.put("spell", new LanternParticleType(13, "spell", false));
        mappings.put("spell_instant", new LanternParticleType(14, "instantSpell", false));
        mappings.put("spell_mob", new LanternParticleType.Colorable(15, "mobSpell", false, Color.BLACK));
        mappings.put("spell_mob_ambient", new LanternParticleType.Colorable(16, "mobSpellAmbient", false, Color.BLACK));
        mappings.put("spell_witch", new LanternParticleType(17, "witchMagic", false));
        mappings.put("drip_water", new LanternParticleType(18, "dripWater", false));
        mappings.put("drip_lava", new LanternParticleType(19, "dripLava", false));
        mappings.put("villager_angry", new LanternParticleType(20, "angryVillager", false));
        mappings.put("villager_happy", new LanternParticleType(21, "happyVillager", true));
        mappings.put("town_aura", new LanternParticleType(22, "townaura", true));
        mappings.put("note", new LanternParticleType.Note(23, "note", false, NotePitches.F_SHARP0));
        mappings.put("portal", new LanternParticleType(24, "portal", true));
        mappings.put("enchantment_table", new LanternParticleType(25, "enchantmenttable", true));
        mappings.put("flame", new LanternParticleType(26, "flame", true));
        mappings.put("lava", new LanternParticleType(27, "lava", false));
        mappings.put("footstep", new LanternParticleType(28, "footstep", false));
        mappings.put("cloud", new LanternParticleType(29, "cloud", true));
        mappings.put("redstone", new LanternParticleType.Colorable(30, "reddust", false, Color.RED));
        mappings.put("snowball", new LanternParticleType(31, "snowballpoof", false));
        mappings.put("snow_shovel", new LanternParticleType(32, "snowshovel", true));
        mappings.put("slime", new LanternParticleType(33, "slime", false));
        mappings.put("heart", new LanternParticleType(34, "heart", false));
        mappings.put("barrier", new LanternParticleType(35, "barrier", false));
        mappings.put("item_crack", new LanternParticleType.Item(36, "iconcrack", true, new LanternItemStack(BlockTypes.STONE)));
        mappings.put("block_crack", new LanternParticleType.Block(37, "blockcrack", true, BlockTypes.STONE.getDefaultState()));
        mappings.put("block_dust", new LanternParticleType.Block(38, "blockdust", true, BlockTypes.STONE.getDefaultState()));
        mappings.put("water_drop", new LanternParticleType(39, "droplet", false));
        mappings.put("item_take", new LanternParticleType(40, "take", false));
        mappings.put("mob_appearance", new LanternParticleType(41, "mobappearance", false));
        mappings.forEach((key, value) -> {
            this.particleTypes.put(key, value);
            this.particleTypes.put(value.getId(), value);
        });
    }

    @Override
    public Optional<ParticleType> getById(String id) {
        return Optional.ofNullable(this.particleTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<ParticleType> getAll() {
        return ImmutableList.copyOf(this.particleTypes.values());
    }

}
