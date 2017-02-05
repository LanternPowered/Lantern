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
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.event.DamageEntityEvent;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.SwingHandEntityEvent;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutAddPotionEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutRemovePotionEffect;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

public abstract class LivingEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    private float lastHealth;
    private int lastArrowsInEntity;
    private byte lastHandData;

    @Nullable private Map<PotionEffectType, PotionEffect> lastPotionEffects;
    private long lastPotionSendTime = -1L;

    protected LivingEntityProtocol(E entity) {
        super(entity);
    }

    private byte getHandData() {
        final Optional<HandType> activeHand = this.entity.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty());
        byte value;
        if (activeHand.isPresent()) {
            value = 0x1;
            if (activeHand.get() == HandTypes.OFF_HAND) {
                value |= 0x2;
            }
        } else {
            value = 0;
        }
        return value;
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Living.HAND_DATA, getHandData());
        parameterList.add(EntityParameters.Living.HEALTH, this.entity.get(Keys.HEALTH).map(Double::floatValue).orElse(1f));
        parameterList.add(EntityParameters.Living.ARROWS_IN_ENTITY, this.entity.get(LanternKeys.ARROWS_IN_ENTITY).orElse(0));
        parameterList.add(EntityParameters.Living.POTION_EFFECT_COLOR, 0);
        parameterList.add(EntityParameters.Living.POTION_EFFECT_AMBIENT, false);
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final float health = this.entity.get(Keys.HEALTH).map(Double::floatValue).orElse(1f);
        if (health != this.lastHealth) {
            parameterList.add(EntityParameters.Living.HEALTH, health);
            this.lastHealth = health;
        }
        final int arrowsInEntity = this.entity.get(LanternKeys.ARROWS_IN_ENTITY).orElse(0);
        if (arrowsInEntity != this.lastArrowsInEntity) {
            parameterList.add(EntityParameters.Living.ARROWS_IN_ENTITY, arrowsInEntity);
            this.lastArrowsInEntity = arrowsInEntity;
        }
        final byte handData = getHandData();
        if (handData != this.lastHandData) {
            parameterList.add(EntityParameters.Living.HAND_DATA, handData);
            this.lastHandData = handData;
        }
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        super.update(context);
        final List<PotionEffect> potionEffects = this.entity.get(Keys.POTION_EFFECTS).orElse(Collections.emptyList());
        final Map<PotionEffectType, PotionEffect> potionEffectMap = new HashMap<>();
        for (PotionEffect potionEffect : potionEffects) {
            if (potionEffect.getDuration() > 0) {
                potionEffectMap.put(potionEffect.getType(), potionEffect);
            }
        }
        final long time = LanternGame.currentTimeTicks();
        if (this.lastPotionSendTime == -1L) {
            potionEffects.forEach(potionEffect -> context.sendToAll(() -> createAddMessage(potionEffect)));
        } else {
            final int delay = (int) (time - this.lastPotionSendTime);
            for (PotionEffect potionEffect : potionEffectMap.values()) {
                //noinspection ConstantConditions
                final PotionEffect oldEntry = this.lastPotionEffects.remove(potionEffect.getType());
                if (oldEntry == null ||
                        oldEntry.getDuration() - delay != potionEffect.getDuration() ||
                        oldEntry.getAmplifier() != potionEffect.getAmplifier() ||
                        oldEntry.isAmbient() != potionEffect.isAmbient() ||
                        oldEntry.getShowParticles() != potionEffect.getShowParticles()) {
                    context.sendToAll(() -> createAddMessage(potionEffect));
                }
            }
            this.lastPotionEffects.values().forEach(potionEffect -> context.sendToAll(
                    () -> new MessagePlayOutRemovePotionEffect(getRootEntityId(), potionEffect.getType())));
        }
        this.lastPotionSendTime = time;
        this.lastPotionEffects = potionEffectMap;
    }

    private MessagePlayOutAddPotionEffect createAddMessage(PotionEffect potionEffect) {
        return new MessagePlayOutAddPotionEffect(getRootEntityId(), potionEffect.getType(), potionEffect.getDuration(),
                potionEffect.getAmplifier(), potionEffect.isAmbient(), potionEffect.getShowParticles());
    }

    @Override
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof DamageEntityEvent) {
            context.sendToAll(() -> new MessagePlayOutEntityAnimation(getRootEntityId(), 1));
        } else if (event instanceof SwingHandEntityEvent) {
            final HandType handType = ((SwingHandEntityEvent) event).getHandType();
            if (handType == HandTypes.MAIN_HAND) {
                context.sendToAllExceptSelf(() -> new MessagePlayOutEntityAnimation(getRootEntityId(), 0));
            } else if (handType == HandTypes.OFF_HAND) {
                context.sendToAllExceptSelf(() -> new MessagePlayOutEntityAnimation(getRootEntityId(), 3));
            } else {
                super.handleEvent(context, event);
            }
        } else {
            super.handleEvent(context, event);
        }
    }
}
