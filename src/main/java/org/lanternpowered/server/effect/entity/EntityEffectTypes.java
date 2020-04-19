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
