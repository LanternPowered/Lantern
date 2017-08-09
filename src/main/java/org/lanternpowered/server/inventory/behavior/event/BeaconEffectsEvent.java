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
package org.lanternpowered.server.inventory.behavior.event;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.client.BeaconClientContainer;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Will be thrown when the accept button in the
 * {@link BeaconClientContainer} is pressed. The
 * two selected {@link PotionEffectType}s are available
 * in this event.
 */
public final class BeaconEffectsEvent implements ContainerEvent {

    @Nullable private final PotionEffectType primaryEffect;
    @Nullable private final PotionEffectType secondaryEffect;

    public BeaconEffectsEvent(@Nullable PotionEffectType primaryEffect, @Nullable PotionEffectType secondaryEffect) {
        this.secondaryEffect = secondaryEffect;
        this.primaryEffect = primaryEffect;
    }

    /**
     * Gets the primary {@link PotionEffectType}. This effect
     * may be {@link Optional#empty()}.
     *
     * @return The primary potion effect type
     */
    public Optional<PotionEffectType> getPrimaryEffect() {
        return Optional.ofNullable(this.primaryEffect);
    }

    /**
     * Gets the secondary {@link PotionEffectType}. This effect
     * may be {@link Optional#empty()}.
     *
     * @return The secondary potion effect type
     */
    public Optional<PotionEffectType> getSecondaryEffect() {
        return Optional.ofNullable(this.secondaryEffect);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("primaryEffect", this.primaryEffect)
                .add("secondaryEffect", this.secondaryEffect)
                .toString();
    }
}
