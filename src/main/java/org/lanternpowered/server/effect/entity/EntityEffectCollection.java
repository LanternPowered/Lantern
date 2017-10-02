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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.util.ResettableBuilder;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public interface EntityEffectCollection {

    /**
     * Constructs a empty {@link EntityEffectCollection}.
     *
     * @return The entity effect collection
     */
    static EntityEffectCollection build() {
        return builder().build();
    }

    /**
     * Constructs a new {@link Builder}.
     *
     * @return The builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets a collection of {@link EntityEffect}s for the given
     * {@link EntityEffectType}.
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    Collection<EntityEffect> getAll(EntityEffectType effectType);

    /**
     * Attempts to get the combined {@link EntityEffect} for the given
     * {@link EntityEffectType}. This {@link EntityEffect} will trigger
     * all the {@link EntityEffect} that are registered for the given
     * type. Will return {@link Optional#empty()} if no effects could
     * be found.
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    Optional<EntityEffect> getCombined(EntityEffectType effectType);

    /**
     * Attempts to get the combined {@link EntityEffect} for the given
     * {@link EntityEffectType}. This {@link EntityEffect} will trigger
     * all the {@link EntityEffect} that are registered for the given
     * type. Will return {@link EntityEffect#NONE} if no effects could
     * be found.
     *
     * @param effectType The entity effect type
     * @return The entity effects
     */
    default EntityEffect getCombinedOrEmpty(EntityEffectType effectType) {
        return getCombined(effectType).orElse(EntityEffect.NONE);
    }

    /**
     * Adds a {@link EntityEffect} for the given {@link EntityEffectType}.
     *
     * @param effectType The entity effect type
     * @param entityEffect The entity effect effect
     * @return Whether the effect was added
     */
    boolean add(EntityEffectType effectType, EntityEffect entityEffect);

    /**
     * Removes a {@link EntityEffect} for the given {@link EntityEffectType}.
     *
     * @param effectType The entity effect type
     * @param entityEffect The entity effect effect
     * @return Whether the effect was removed
     */
    boolean remove(EntityEffectType effectType, EntityEffect entityEffect);

    /**
     * Creates a copy of this {@link EntityEffectCollection}.
     *
     * @return The copy
     */
    EntityEffectCollection copy();

    /**
     * Creates a new {@link Builder} from this {@link EntityEffectCollection}.
     *
     * @return The builder
     */
    default Builder toBuilder() {
        return builder().from(this);
    }

    /**
     * A builder to construct {@link EntityEffectCollection}s.
     */
    interface Builder extends ResettableBuilder<EntityEffectCollection, Builder> {

        /**
         * Adds a {@link EntityEffect} for the
         * given {@link EntityEffectType}.
         *
         * @param effectType The entity effect type
         * @param entityEffect The entity effect effect
         * @return This builder, for chaining
         */
        Builder add(EntityEffectType effectType, EntityEffect entityEffect);

        Builder replaceOrAdd(EntityEffectType effectType, Class<? extends EntityEffect> effectToReplace, EntityEffect entityEffect);

        default Builder remove(EntityEffectType effectType, Class<? extends EntityEffect> effectToRemove) {
            return removeIf(effectType, effectToRemove::isInstance);
        }

        Builder removeIf(EntityEffectType effectType, Predicate<EntityEffect> predicate);

        Builder reset(EntityEffectType effectType);

        /**
         * Builds a {@link EntityEffectCollection}.
         *
         * @return The entity sound collection
         */
        EntityEffectCollection build();
    }
}
