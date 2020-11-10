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
package org.lanternpowered.server.inventory.container.layout

import org.lanternpowered.api.effect.potion.PotionEffectType
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.container.layout.BeaconContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry

class RootBeaconContainerLayout : LanternTopBottomContainerLayout<BeaconContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 3
) {

    companion object {

        private val TITLE = translatableTextOf("container.beacon")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.DISABLE_SHIFT_INSERTION // Payment slot
        )

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private const val POWER_LEVEL = 0
        private const val PRIMARY_POTION_EFFECT = 1
        private const val SECONDARY_POTION_EFFECT = 2

        private const val NO_POTION_EFFECT = -1
    }

    init {
        this.setProperty(PRIMARY_POTION_EFFECT, NO_POTION_EFFECT)
        this.setProperty(SECONDARY_POTION_EFFECT, NO_POTION_EFFECT)
    }

    private val onSelectEffects = ArrayList<(Player, PotionEffectType?, PotionEffectType?) -> Unit>()

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.BEACON, this.title))

    override val top: BeaconContainerLayout = SubBeaconContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    var selectedPrimaryEffect: PotionEffectType? = null
        set(value) {
            field = value
            // Update the client property
            this.setProperty(PRIMARY_POTION_EFFECT,
                    if (value == null) NO_POTION_EFFECT else PotionEffectTypeRegistry.getId(value))
        }

    var selectedSecondaryEffect: PotionEffectType? = null
        set(value) {
            field = value
            // Update the client property
            this.setProperty(SECONDARY_POTION_EFFECT,
                    if (value == null) NO_POTION_EFFECT else PotionEffectTypeRegistry.getId(value))
        }

    var powerLevel: Int = 0
        set(value) {
            field = value.coerceIn(0, 4)
            // Update the client property
            this.setProperty(POWER_LEVEL, field)
        }

    fun onSelectEffects(fn: (player: Player, primaryEffect: PotionEffectType?, secondaryEffect: PotionEffectType?) -> Unit) {
        this.onSelectEffects += fn
    }

    /**
     * Handles a set beacon effects and pay packet for the given player.
     */
    fun handleSelectEffects(
            player: Player, primaryEffect: PotionEffectType?, secondaryEffect: PotionEffectType?) {
        val data = this.getData(player) ?: return
        // Force the payment slot to update
        data.queueSilentSlotChange(this.slots[0])

        for (onSelectEffects in this.onSelectEffects)
            onSelectEffects(player, primaryEffect, secondaryEffect)
    }
}

private class SubBeaconContainerLayout(
        offset: Int, size: Int, private val root: RootBeaconContainerLayout
) : SubContainerLayout(offset, size, root), BeaconContainerLayout {

    override val payment: ContainerSlot
        get() = this[0]

    override var powerLevel: Int
        get() = this.root.powerLevel
        set(value) { this.root.powerLevel = value }

    override var selectedPrimaryEffect: PotionEffectType?
        get() = this.root.selectedPrimaryEffect
        set(value) { this.root.selectedPrimaryEffect = value }

    override var selectedSecondaryEffect: PotionEffectType?
        get() = this.root.selectedSecondaryEffect
        set(value) { this.root.selectedSecondaryEffect = value }

    override fun onSelectEffects(fn: (player: Player, primaryEffect: PotionEffectType?, secondaryEffect: PotionEffectType?) -> Unit) {
        this.root.onSelectEffects(fn)
    }
}
