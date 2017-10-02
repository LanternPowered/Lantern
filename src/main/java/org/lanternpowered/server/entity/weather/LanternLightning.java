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
package org.lanternpowered.server.entity.weather;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.effect.entity.EntityEffectCollection;
import org.lanternpowered.server.effect.entity.EntityEffectTypes;
import org.lanternpowered.server.effect.entity.sound.weather.LightningSoundEffect;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.event.CauseStack;
import org.lanternpowered.server.network.entity.EntityProtocolTypes;
import org.spongepowered.api.effect.sound.SoundCategories;

import java.util.UUID;

public class LanternLightning extends LanternEntity implements AbstractLightning {

    public static final EntityEffectCollection DEFAULT_SOUND_COLLECTION = EntityEffectCollection.builder()
            .add(EntityEffectTypes.LIGHTNING, new LightningSoundEffect())
            .build();

    /**
     * The amount of ticks that the lightning will be alive.
     */
    private int ticksToLive = 10;
    private boolean remove;

    public LanternLightning(UUID uniqueId) {
        super(uniqueId);
        setEntityProtocolType(EntityProtocolTypes.LIGHTNING);
        setEffectCollection(DEFAULT_SOUND_COLLECTION.copy());
        setSoundCategory(SoundCategories.WEATHER);
    }

    @Override
    public void registerKeys() {
        super.registerKeys();
        getValueCollection().registerNonRemovable(LanternKeys.IS_EFFECT, false);
    }

    @Override
    public void pulse(int deltaTicks) {
        super.pulse(deltaTicks);

        this.ticksToLive -= deltaTicks;
        if (this.ticksToLive > 0) {
            return;
        }
        if (this.remove) {
            try (CauseStack.Frame frame = CauseStack.current().pushCauseFrame()) {
                // Add this entity to the cause of removal
                frame.pushCause(this);
                // Remove the entity
                remove();
            }
        } else {
            // Remove the entity the next pulse
            this.remove = true;

            // Play the sound effect
            getEffectCollection().getCombinedOrEmpty(EntityEffectTypes.LIGHTNING).play(this);

            // TODO: Damage entities?
            // TODO: Create fire
        }
    }
}
