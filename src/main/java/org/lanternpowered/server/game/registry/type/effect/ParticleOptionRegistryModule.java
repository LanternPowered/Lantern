/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.game.registry.type.effect;

import kotlin.jvm.functions.Function1;
import org.lanternpowered.server.effect.particle.LanternParticleOption;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.effect.particle.ParticleOption;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.potion.PotionEffectType;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.util.Color;
import org.spongepowered.api.util.Direction;
import org.spongepowered.math.vector.Vector3d;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public class ParticleOptionRegistryModule extends DefaultCatalogRegistryModule<ParticleOption> {

    public ParticleOptionRegistryModule() {
        super(ParticleOptions.class);
    }

    @Override
    public void registerDefaults() {
        registerOption("block_state", BlockState.class);
        registerOption("color", Color.class);
        registerOption("direction", Direction.class);
        registerOption("firework_effects", List.class,
                value -> value.isEmpty() ? new IllegalArgumentException("The firework effects list may not be empty") : null);
        registerOption("quantity", Integer.class,
                value -> value < 1 ? new IllegalArgumentException("Quantity must be at least 1") : null);
        registerOption("item_stack_snapshot", ItemStackSnapshot.class);
        registerOption("note", NotePitch.class);
        registerOption("offset", Vector3d.class);
        registerOption("potion_effect_type", PotionEffectType.class);
        registerOption("scale", Double.class,
                value -> value < 0 ? new IllegalArgumentException("Scale may not be negative") : null);
        registerOption("velocity", Vector3d.class);
        registerOption("slow_horizontal_velocity", Boolean.class);
    }

    private <V> void registerOption(String id, Class<V> valueType) {
        registerOption(id, valueType, null);
    }

    private <V> void registerOption(String id, Class<V> valueType, @Nullable Function1<V, IllegalArgumentException> valueValidator) {
        register(new LanternParticleOption<>(CatalogKey.minecraft(id), valueType,
                valueValidator == null ? v -> null : valueValidator));
    }
}
