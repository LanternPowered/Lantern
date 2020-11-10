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
package org.lanternpowered.server.network.entity.vanilla

import org.lanternpowered.api.data.Keys
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.effect.potion.PotionEffect
import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.server.LanternGame
import org.lanternpowered.server.data.key.LanternKeys
import org.lanternpowered.server.entity.LanternEntity
import org.lanternpowered.server.entity.Pose
import org.lanternpowered.server.entity.event.DamagedEntityEvent
import org.lanternpowered.server.entity.event.EntityEvent
import org.lanternpowered.server.entity.event.SwingHandEntityEvent
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext
import org.lanternpowered.server.network.entity.parameter.ParameterList
import org.lanternpowered.server.network.vanilla.packet.type.play.AddPotionEffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityAnimationPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.RemovePotionEffectPacket
import org.spongepowered.api.data.type.HandTypes

abstract class LivingEntityProtocol<E : LanternEntity> protected constructor(entity: E) : EntityProtocol<E>(entity) {

    private var lastHealth = 0f
    private var lastArrowsInEntity = 0
    private var lastLivingFlags: Byte = 0
    private var lastPotionEffects: MutableMap<PotionEffectType, PotionEffect>? = null
    private var lastPotionSendTime = -1L

    private val livingFlags: Byte
        get() {
            val activeHand = this.entity.get(LanternKeys.ACTIVE_HAND).orNull()
            var value = 0
            if (activeHand != null) {
                value = 0x1
                if (activeHand eq HandTypes.OFF_HAND)
                    value += 0x2
            }
            if (this.pose == Pose.SPIN_ATTACK)
                value += 0x4
            return value.toByte()
        }

    override fun spawn(parameterList: ParameterList) {
        super.spawn(parameterList)
        parameterList.add(EntityParameters.Living.FLAGS, this.livingFlags)
        parameterList.add(EntityParameters.Living.HEALTH, this.entity.get(Keys.HEALTH).map { it.toFloat() }.orElse(1f))
        parameterList.add(EntityParameters.Living.ARROWS_IN_ENTITY, this.entity.get(LanternKeys.ARROWS_IN_ENTITY).orElse(0))
        parameterList.add(EntityParameters.Living.POTION_EFFECT_COLOR, 0)
        parameterList.add(EntityParameters.Living.POTION_EFFECT_AMBIENT, false)
    }

    override fun update(parameterList: ParameterList) {
        super.update(parameterList)
        val health = this.entity.get(Keys.HEALTH).map { it.toFloat() }.orElse(1f)
        if (health != this.lastHealth) {
            parameterList.add(EntityParameters.Living.HEALTH, health)
            this.lastHealth = health
        }
        val arrowsInEntity = this.entity.get(LanternKeys.ARROWS_IN_ENTITY).orElse(0)
        if (arrowsInEntity != this.lastArrowsInEntity) {
            parameterList.add(EntityParameters.Living.ARROWS_IN_ENTITY, arrowsInEntity)
            this.lastArrowsInEntity = arrowsInEntity
        }
        val livingFlags = this.livingFlags
        if (livingFlags != this.lastLivingFlags) {
            parameterList.add(EntityParameters.Living.FLAGS, livingFlags)
            this.lastLivingFlags = livingFlags
        }
    }

    override fun update(context: EntityProtocolUpdateContext) {
        super.update(context)
        val potionEffects = this.entity.get(Keys.POTION_EFFECTS).orElse(emptyList())
        val potionEffectMap = HashMap<PotionEffectType, PotionEffect>()
        for (potionEffect in potionEffects) {
            if (potionEffect.duration > 0)
                potionEffectMap[potionEffect.type] = potionEffect
        }
        val time = LanternGame.currentTimeTicks()
        if (this.lastPotionSendTime == -1L) {
            for (potionEffect in potionEffects)
                context.sendToAll { this.createAddMessage(potionEffect) }
        } else {
            val delay = (time - this.lastPotionSendTime).toInt()
            for (potionEffect in potionEffectMap.values) {
                val oldEntry = this.lastPotionEffects!!.remove(potionEffect.type)
                if (oldEntry == null ||
                        oldEntry.duration - delay != potionEffect.duration ||
                        oldEntry.amplifier != potionEffect.amplifier ||
                        oldEntry.isAmbient != potionEffect.isAmbient ||
                        oldEntry.showsParticles() != potionEffect.showsParticles() ||
                        oldEntry.showsIcon() != potionEffect.showsIcon()) {
                    context.sendToAll { this.createAddMessage(potionEffect) }
                }
            }
            for (potionEffect in this.lastPotionEffects!!.values)
                context.sendToAll { RemovePotionEffectPacket(rootEntityId, potionEffect.type) }
        }
        this.lastPotionSendTime = time
        this.lastPotionEffects = potionEffectMap
    }

    private fun createAddMessage(potionEffect: PotionEffect): AddPotionEffectPacket {
        return AddPotionEffectPacket(rootEntityId, potionEffect.type, potionEffect.duration,
                potionEffect.amplifier, potionEffect.isAmbient, potionEffect.showsParticles())
    }

    override fun handleEvent(context: EntityProtocolUpdateContext, event: EntityEvent) {
        return when (event) {
            is DamagedEntityEvent -> context.sendToAll { EntityAnimationPacket(this.rootEntityId, 1) }
            is SwingHandEntityEvent -> {
                val handType = event.handType
                when {
                    handType eq HandTypes.MAIN_HAND -> context.sendToAllExceptSelf { EntityAnimationPacket(this.rootEntityId, 0) }
                    handType eq HandTypes.OFF_HAND -> context.sendToAllExceptSelf { EntityAnimationPacket(this.rootEntityId, 3) }
                    else -> super.handleEvent(context, event)
                }
            }
            else -> super.handleEvent(context, event)
        }
    }
}
