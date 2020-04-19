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
package org.lanternpowered.server.effect.entity;

import org.lanternpowered.api.cause.CauseStack;
import org.spongepowered.api.entity.living.animal.horse.Horse;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class EntityEffectTypes {

    // SORTFIELDS:ON

    /**
     * The {@link EntityEffectType} that is played when a entity gets angry. Is
     * in vanilla minecraft only used {@link Horse}s.
     */
    public static final EntityEffectType ANGRY = DummyObjectProvider.createFor(EntityEffectType.class, "ANGRY");

    /**
     * The {@link EntityEffectType} that is played when a entity dies.
     */
    public static final EntityEffectType DEATH = DummyObjectProvider.createFor(EntityEffectType.class, "DEATH");

    /**
     * The {@link EntityEffectType} that is played when a entity falls and hits the ground.
     * <p>
     * This is a special {@link EntityEffectType}, the fall height
     * will be available in the current {@link CauseStack} when the
     * {@link EntityEffect} is being played.
     */
    public static final EntityEffectType FALL = DummyObjectProvider.createFor(EntityEffectType.class, "FALL");

    /**
     * The {@link EntityEffectType} that is played when a entity takes damage.
     */
    public static final EntityEffectType HURT = DummyObjectProvider.createFor(EntityEffectType.class, "HURT");

    /**
     * The {@link EntityEffectType} that is played when a entity is looking/wandering around.
     */
    public static final EntityEffectType IDLE = DummyObjectProvider.createFor(EntityEffectType.class, "IDLE");

    /**
     * The {@link EntityEffectType} that is played when lightning strikes the ground.
     */
    public static final EntityEffectType LIGHTNING = DummyObjectProvider.createFor(EntityEffectType.class, "LIGHTNING");

    /**
     * The {@link EntityEffectType} that is played when a entity is merged. For
     * example dropped items which merge into one item.
     */
    public static final EntityEffectType MERGE = DummyObjectProvider.createFor(EntityEffectType.class, "MERGE");

    /**
     * The {@link EntityEffectType} that is played when a entity falls into the water.
     */
    public static final EntityEffectType SPLASH = DummyObjectProvider.createFor(EntityEffectType.class, "SPLASH");

    /**
     * The {@link EntityEffectType} that is played when a entity is swimming.
     */
    public static final EntityEffectType SWIM = DummyObjectProvider.createFor(EntityEffectType.class, "SWIM");

    /**
     * The {@link EntityEffectType} that is played when a villagers says no.
     */
    public static final EntityEffectType VILLAGER_NO = DummyObjectProvider.createFor(EntityEffectType.class, "VILLAGER_NO");

    /**
     * The {@link EntityEffectType} that is played when a villagers says yes.
     */
    public static final EntityEffectType VILLAGER_YES = DummyObjectProvider.createFor(EntityEffectType.class, "VILLAGER_YES");

    /**
     * The {@link EntityEffectType} that is played when a entity is walking.
     */
    public static final EntityEffectType WALK = DummyObjectProvider.createFor(EntityEffectType.class, "WALK");

    // SORTFIELDS:OFF

    private EntityEffectTypes() {
    }
}
