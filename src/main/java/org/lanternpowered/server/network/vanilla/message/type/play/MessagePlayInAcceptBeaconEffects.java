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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayInAcceptBeaconEffects implements Message {

    @Nullable private final PotionEffectType primaryEffect;
    @Nullable private final PotionEffectType secondaryEffect;

    public MessagePlayInAcceptBeaconEffects(@Nullable PotionEffectType primaryEffect, @Nullable PotionEffectType secondaryEffect) {
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
