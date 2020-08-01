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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.data.type.HandPreferences;

public abstract class InsentientEntityProtocol<E extends LanternEntity> extends CreatureEntityProtocol<E> {

    private HandPreference lastDominantHand = HandPreferences.RIGHT.get();

    protected InsentientEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        // Ignore the NoAI tag, isn't used on the client
        parameterList.add(EntityParameters.Insentient.FLAGS,
                (byte) (this.entity.get(Keys.DOMINANT_HAND).orElseGet(HandPreferences.RIGHT) == HandPreferences.LEFT.get() ? 0x2 : 0));
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final HandPreference dominantHand = this.entity.get(Keys.DOMINANT_HAND).orElseGet(HandPreferences.RIGHT);
        if (dominantHand != this.lastDominantHand) {
            // Ignore the NoAI tag, isn't used on the client
            parameterList.add(EntityParameters.Insentient.FLAGS, (byte) (dominantHand == HandPreferences.LEFT.get() ? 0x2 : 0));
            this.lastDominantHand = dominantHand;
        }
    }
}
