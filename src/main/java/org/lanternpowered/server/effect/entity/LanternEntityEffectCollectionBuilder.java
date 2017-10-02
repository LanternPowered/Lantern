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
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.function.Predicate;

public class LanternEntityEffectCollectionBuilder implements EntityEffectCollection.Builder {

    private final Multimap<EntityEffectType, EntityEffect> effects = HashMultimap.create();

    @Override
    public EntityEffectCollection.Builder add(EntityEffectType effectType, EntityEffect entityEffect) {
        checkNotNull(effectType, "effectType");
        checkNotNull(entityEffect, "entityEffect");
        this.effects.put(effectType, entityEffect);
        return this;
    }

    @Override
    public EntityEffectCollection.Builder replaceOrAdd(EntityEffectType effectType,
            Class<? extends EntityEffect> effectToReplace, EntityEffect entityEffect) {
        checkNotNull(effectType, "effectType");
        checkNotNull(entityEffect, "entityEffect");
        checkNotNull(effectToReplace, "effectToReplace");
        final Collection<EntityEffect> effects = this.effects.get(effectType);
        effects.removeIf(effectToReplace::isInstance);
        effects.add(entityEffect);
        return this;
    }

    @Override
    public EntityEffectCollection.Builder reset(EntityEffectType effectType) {
        checkNotNull(effectType, "effectType");
        this.effects.removeAll(effectType);
        return this;
    }

    @Override
    public EntityEffectCollection.Builder removeIf(EntityEffectType effectType, Predicate<EntityEffect> predicate) {
        checkNotNull(effectType, "effectType");
        checkNotNull(predicate, "predicate");
        this.effects.get(effectType).removeIf(predicate);
        return this;
    }

    @Override
    public EntityEffectCollection build() {
        return new LanternEntityEffectCollection(HashMultimap.create(this.effects));
    }

    @Override
    public EntityEffectCollection.Builder from(EntityEffectCollection value) {
        checkNotNull(value, "value");
        this.effects.clear();
        this.effects.putAll(((LanternEntityEffectCollection) value).effects);
        return this;
    }

    @Override
    public EntityEffectCollection.Builder reset() {
        this.effects.clear();
        return this;
    }
}
