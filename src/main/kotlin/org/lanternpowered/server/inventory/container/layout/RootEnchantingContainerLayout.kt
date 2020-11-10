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

import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.enchantment.Enchantment
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.EnchantingContainerLayout
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.server.inventory.container.ClientWindowTypes
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket
import org.lanternpowered.server.registry.type.item.EnchantmentTypeRegistry

class RootEnchantingContainerLayout : LanternTopBottomContainerLayout<EnchantingContainerLayout>(
        title = TITLE, slotFlags = ALL_INVENTORY_FLAGS, propertyCount = 10
) {

    companion object {

        private val TITLE = translatableTextOf("container.enchant")

        private val TOP_INVENTORY_FLAGS = intArrayOf(
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION + Flags.ONE_ITEM, // Input slot
                Flags.REVERSE_SHIFT_INSERTION + Flags.POSSIBLY_DISABLED_SHIFT_INSERTION // Lapis lazuli slot
        )

        private val ALL_INVENTORY_FLAGS = TOP_INVENTORY_FLAGS + MAIN_INVENTORY_FLAGS

        private const val REQUIRED_EXPERIENCE_LEVEL_1 = 0
        private const val REQUIRED_EXPERIENCE_LEVEL_2 = 1
        private const val REQUIRED_EXPERIENCE_LEVEL_3 = 2
        private const val ENCHANTMENT_SEED = 3
        private const val SHOWN_ENCHANTMENT_LEVEL_1 = 4
        private const val SHOWN_ENCHANTMENT_LEVEL_2 = 5
        private const val SHOWN_ENCHANTMENT_LEVEL_3 = 6
        private const val SHOWN_ENCHANTMENT_1 = 7
        private const val SHOWN_ENCHANTMENT_2 = 8
        private const val SHOWN_ENCHANTMENT_3 = 9
    }

    private val onClickButton = ArrayList<(Player, EnchantingContainerLayout.Button) -> Unit>()

    override fun createOpenPackets(data: ContainerData): List<Packet> =
            listOf(OpenWindowPacket(data.containerId, ClientWindowTypes.ENCHANTMENT, this.title))

    override val top: EnchantingContainerLayout = SubEnchantingContainerLayout(0, TOP_INVENTORY_FLAGS.size, this)

    val buttons: List<EnchantingContainerLayout.Button> = listOf(
            EnchantingButton(0, REQUIRED_EXPERIENCE_LEVEL_1, SHOWN_ENCHANTMENT_1, SHOWN_ENCHANTMENT_LEVEL_1, this),
            EnchantingButton(1, REQUIRED_EXPERIENCE_LEVEL_2, SHOWN_ENCHANTMENT_2, SHOWN_ENCHANTMENT_LEVEL_2, this),
            EnchantingButton(2, REQUIRED_EXPERIENCE_LEVEL_3, SHOWN_ENCHANTMENT_3, SHOWN_ENCHANTMENT_LEVEL_3, this)
    )

    var seed: Int = 0
        set(value) {
            field = value
            // Update the client property
            this.setProperty(ENCHANTMENT_SEED, value)
        }

    fun onClickButton(fn: (player: Player, button: EnchantingContainerLayout.Button) -> Unit) {
        this.onClickButton += fn
    }

    override fun handleButtonClick(player: Player, index: Int) {
        this.getData(player) ?: return

        val button = this.buttons[index]
        for (onClickButton in this.onClickButton)
            onClickButton(player, button)
    }
}

private class EnchantingButton(
        override val index: Int,
        private val levelRequirementProperty: Int,
        private val enchantmentTypeProperty: Int,
        private val enchantmentLevelProperty: Int,
        private val root: RootEnchantingContainerLayout
) : EnchantingContainerLayout.Button {

    init {
        this.root.setProperty(this.enchantmentTypeProperty, -1)
        this.root.setProperty(this.enchantmentLevelProperty, -1)
    }

    override var levelRequirement: Int? = null
        set(value) {
            field = value
            this.root.setProperty(this.levelRequirementProperty, value ?: 0)
        }

    override var enchantment: Enchantment? = null
        set(value) {
            field = value
            val enchantmentId = if (value == null) -1 else EnchantmentTypeRegistry.getId(value.type)
            this.root.setProperty(this.enchantmentTypeProperty, enchantmentId)
            this.root.setProperty(this.enchantmentLevelProperty, enchantment?.level ?: -1)
        }
}

private class SubEnchantingContainerLayout(
        offset: Int, size: Int, private val root: RootEnchantingContainerLayout
) : SubContainerLayout(offset, size, root), EnchantingContainerLayout {

    override var seed: Int
        get() = this.root.seed
        set(value) { this.root.seed = value }

    override val buttons: List<EnchantingContainerLayout.Button>
        get() = this.root.buttons

    override val input: ContainerSlot
        get() = this[0]

    override val lapis: ContainerSlot
        get() = this[1]

    override fun onClickButton(fn: (player: Player, button: EnchantingContainerLayout.Button) -> Unit) {
        this.root.onClickButton(fn)
    }
}
