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

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.Pose;
import org.lanternpowered.server.entity.event.DamagedEntityEvent;
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.SwingHandEntityEvent;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.packet.type.play.AddPotionEffectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityAnimation;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutRemovePotionEffect;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.effect.potion.PotionEffectType;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class LivingEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    private float lastHealth;
    private int lastArrowsInEntity;
    private byte lastLivingFlags;

    @Nullable private Map<PotionEffectType, PotionEffect> lastPotionEffects;
    private long lastPotionSendTime = -1L;

    protected LivingEntityProtocol(E entity) {
        super(entity);
    }

    private byte getLivingFlags() {
        final Optional<HandType> activeHand = this.entity.get(LanternKeys.ACTIVE_HAND);
        byte value = 0;
        if (activeHand.isPresent()) {
            value = 0x1;
            if (activeHand.get() == HandTypes.OFF_HAND.get()) {
                value |= 0x2;
            }
        }
        if (getPose() == Pose.SPIN_ATTACK) {
            value |= 0x4;
        }
        return value;
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Living.FLAGS, getLivingFlags());
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
        final byte livingFlags = getLivingFlags();
        if (livingFlags != this.lastLivingFlags) {
            parameterList.add(EntityParameters.Living.FLAGS, livingFlags);
            this.lastLivingFlags = livingFlags;
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
                        oldEntry.showsParticles() != potionEffect.showsParticles()) {
                    context.sendToAll(() -> createAddMessage(potionEffect));
                }
            }
            this.lastPotionEffects.values().forEach(potionEffect -> context.sendToAll(
                    () -> new PacketPlayOutRemovePotionEffect(getRootEntityId(), potionEffect.getType())));
        }
        this.lastPotionSendTime = time;
        this.lastPotionEffects = potionEffectMap;
    }

    private AddPotionEffectPacket createAddMessage(PotionEffect potionEffect) {
        return new AddPotionEffectPacket(getRootEntityId(), potionEffect.getType(), potionEffect.getDuration(),
                potionEffect.getAmplifier(), potionEffect.isAmbient(), potionEffect.showsParticles());
    }

    @Override
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof DamagedEntityEvent) {
            context.sendToAll(() -> new PacketPlayOutEntityAnimation(getRootEntityId(), 1));
        } else if (event instanceof SwingHandEntityEvent) {
            final HandType handType = ((SwingHandEntityEvent) event).getHandType();
            if (handType == HandTypes.MAIN_HAND.get()) {
                context.sendToAllExceptSelf(() -> new PacketPlayOutEntityAnimation(getRootEntityId(), 0));
            } else if (handType == HandTypes.OFF_HAND.get()) {
                context.sendToAllExceptSelf(() -> new PacketPlayOutEntityAnimation(getRootEntityId(), 3));
            } else {
                super.handleEvent(context, event);
            }
        } else {
            super.handleEvent(context, event);
        }
    }
}
