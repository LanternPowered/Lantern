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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

final class LanternEntityEffectCollection implements EntityEffectCollection {

    final Multimap<EntityEffectType, EntityEffect> effects;
    private final Map<EntityEffectType, Optional<EntityEffect>> combinedEffects = new HashMap<>();

    LanternEntityEffectCollection(Multimap<EntityEffectType, EntityEffect> effects) {
        this.effects = effects;
    }

    @Override
    public Collection<EntityEffect> getAll(EntityEffectType effectType) {
        checkNotNull(effectType, "effectType");
        return Collections.unmodifiableCollection(this.effects.get(effectType));
    }

    @Override
    public Optional<EntityEffect> getCombined(EntityEffectType effectType) {
        checkNotNull(effectType, "effectType");
        return this.combinedEffects.computeIfAbsent(effectType, type -> {
            final List<EntityEffect> effects = ImmutableList.copyOf(this.effects.get(type));
            return effects.isEmpty() ? Optional.empty() : Optional.of(entity -> {
                for (EntityEffect effect : effects) {
                    effect.play(entity);
                }
            });
        });
    }

    @Override
    public boolean add(EntityEffectType effectType, EntityEffect entityEffect) {
        checkNotNull(effectType, "effectType");
        checkNotNull(entityEffect, "entityEffect");
        if (this.effects.put(effectType, entityEffect)) {
            // Invalidate
            this.combinedEffects.remove(effectType);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(EntityEffectType effectType, EntityEffect entityEffect) {
        checkNotNull(effectType, "effectType");
        checkNotNull(entityEffect, "entityEffect");
        if (this.effects.remove(effectType, entityEffect)) {
            // Invalidate
            this.combinedEffects.remove(effectType);
            return true;
        }
        return false;
    }

    @Override
    public EntityEffectCollection copy() {
        return new LanternEntityEffectCollection(HashMultimap.create(this.effects));
    }
}
