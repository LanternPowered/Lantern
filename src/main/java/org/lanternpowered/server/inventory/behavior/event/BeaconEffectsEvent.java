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
package org.lanternpowered.server.inventory.behavior.event;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.inventory.client.BeaconClientContainer;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

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
