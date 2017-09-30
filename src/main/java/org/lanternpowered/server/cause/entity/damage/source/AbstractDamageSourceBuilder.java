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
package org.lanternpowered.server.cause.entity.damage.source;

import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.cause.entity.damage.DamageType;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractDamageSourceBuilder<T extends DamageSource, B extends DamageSource.DamageSourceBuilder<T, B>, O extends B>
        extends org.spongepowered.api.event.cause.entity.damage.source.common.AbstractDamageSourceBuilder<T, B> {

    @Nullable protected Double exhaustion;

    /**
     * Sets the exhaustion that should be added
     * to the damaged {@link Entity}.
     *
     * @param exhaustion The exhaustion
     * @return This builder, for chaining
     */
    public O exhaustion(double exhaustion) {
        this.exhaustion = exhaustion;
        return (O) this;
    }

    @Override
    public O absolute() {
        return (O) super.absolute();
    }

    @Override
    public O bypassesArmor() {
        return (O) super.bypassesArmor();
    }

    @Override
    public O creative() {
        return (O) super.creative();
    }

    @Override
    public O explosion() {
        return (O) super.explosion();
    }

    @Override
    public O magical() {
        return (O) super.magical();
    }

    @Override
    public O scalesWithDifficulty() {
        return (O) super.scalesWithDifficulty();
    }

    @Override
    public O type(DamageType damageType) {
        return (O) super.type(damageType);
    }

    @Override
    public O from(T value) {
        return (O) super.from(value);
    }

    @Override
    public O reset() {
        return (O) super.reset();
    }
}
