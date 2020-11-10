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

import com.google.common.collect.HashMultimap
import it.unimi.dsi.fastutil.ints.IntArraySet
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.container.layout.ContainerLayout
import org.lanternpowered.api.item.inventory.container.layout.ContainerSlot
import org.lanternpowered.api.item.inventory.container.layout.RootContainerLayout
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.SlotChangeTracker
import org.lanternpowered.server.inventory.behavior.MouseButton
import org.lanternpowered.server.network.item.NetworkItemTypeRegistry
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowPropertyPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket
import org.lanternpowered.api.data.Keys
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import java.util.Arrays
import java.util.BitSet
import kotlin.math.min

/**
 * Represents the layout of a container. The layout is closely tied to the container
 * on the client. This can be seen as the "client container protocol", because it handles
 * everything related to the client container.
 *
 * @property slotFlags An array with all the information about the slots in this layout
 */
abstract class LanternContainerLayout(
        title: Text,
        protected val slotFlags: IntArray,
        propertyCount: Int
) : RootContainerLayout, SlotChangeTracker {

    object Flags {

        /**
         * A flag that enables reverse shift insertion behavior from the target slot.
         */
        const val REVERSE_SHIFT_INSERTION = 0x1

        /**
         * A flag that disables shift operations to the target slot.
         */
        const val DISABLE_SHIFT_INSERTION = 0x2

        /**
         * A flag that defines that not all the shift operation may succeed. For example,
         * in furnaces can the shift operation only succeed if the shifted item
         * is smeltable/cookable.
         */
        const val POSSIBLY_DISABLED_SHIFT_INSERTION = 0x4

        /**
         * A flag that defines that the slot is present in the main inventory.
         */
        const val IS_MAIN_INVENTORY = 0x8

        /**
         * The offset at which the hotbar slot index is located.
         */
        const val HOTBAR_SHIFT = 4

        /**
         * A flag that defines that the slot is present in the hotbar. The flag
         * uses 4 bits and this is the raw slot index. Counting from 1 to 9.
         */
        const val HOTBAR_MASK = 0xf shl HOTBAR_SHIFT

        /**
         * A flag that defines that only one item (item stack with quantity one)
         * can be present in the target slot.
         */
        const val ONE_ITEM = 0x100

        /**
         * A flag that defines that the slot is an output slot. Double click can't
         * retrieve items from that slot.
         */
        const val IGNORE_DOUBLE_CLICK = 0x200

        /**
         * The offset at which the silent slot index is located.
         */
        const val SILENT_SLOT_INDEX_SHIFT = 23

        /**
         * A flag that defines the silent update slot index. Not needed to apply
         * on hotbar slot indexes, they are always silently updatable.
         */
        const val SILENT_SLOT_INDEX_MASK = 0xff shl SILENT_SLOT_INDEX_SHIFT

        /**
         * Creates flag that defines the silent update slot index.
         */
        fun silentSlotIndex(silentSlotIndex: Int): Int =
                silentSlotIndex shl SILENT_SLOT_INDEX_SHIFT
    }

    object UpdateFlags {

        /**
         * Whether the state is dirty.
         */
        const val NEEDS_UPDATE = 0x1

        /**
         * Whether the slot should be updated silently.
         */
        const val SILENT_UPDATE = 0x2
    }

    /**
     * All the viewers of this container layout.
     */
    private val viewers = HashMap<Player, ContainerData>()

    /**
     * An array with all the property values.
     */
    private val properties = IntArray(propertyCount)

    /**
     * An array with all the slots.
     */
    val slots by lazy { Array(this.slotFlags.size) { index -> LanternContainerSlot(index, this) } }

    private val containerSlotsBySlot = HashMultimap.create<Slot, LanternContainerSlot>()

    final override var title: Text = title
        set(value) {
            field = value
            this.queueCompleteUpdate()
        }

    final override val size: Int
        get() = this.slots.size

    @Suppress("LeakingThis")
    override val cursor: LanternContainerSlot = LanternContainerSlot(-1, this)

    fun removeSlot(containerSlot: LanternContainerSlot, slot: Slot) {
        this.containerSlotsBySlot.remove(slot, containerSlot)
    }

    fun addSlot(containerSlot: LanternContainerSlot, slot: Slot) {
        this.containerSlotsBySlot.put(slot, containerSlot)
    }

    /**
     * Creates the packet that can be used to open display the container layout.
     */
    abstract fun createOpenPackets(data: ContainerData): List<Packet>

    /**
     * Converts the hotbar slot index (0 - 8) to a slot index
     * within this container.
     *
     * @param hotbarSlot The hotbar slot
     * @return The slot index
     */
    protected open fun getHotbarSlotIndex(hotbarSlot: Int): Int = this.slotFlags.size - (9 - hotbarSlot)

    /**
     * Sets the property value at the given index.
     */
    fun setProperty(index: Int, value: Int) {
        this.properties[index] = value
        for (data in this.viewers.values)
            data.propertyUpdateFlags.set(index)
    }

    /**
     * Gets the container data for the given player, if the
     * player has this container open.
     */
    fun getData(player: Player): ContainerData? =
            this.viewers[player]

    /**
     * The container data of all the viewers.
     */
    val viewerData: Collection<ContainerData>
        get() = this.viewers.values

    /**
     * Data attached to a specific viewer.
     */
    class ContainerData(
            val player: Player,
            val containerId: Int,
            slotCount: Int,
            propertyCount: Int
    ) {

        /**
         * All the dirty states of container properties.
         */
        val propertyUpdateFlags = BitSet(propertyCount)

        /**
         * All the dirty states of this container data.
         */
        val slotUpdateFlags = IntArray(slotCount)

        /**
         * The dirty state of the cursor.
         */
        var cursorUpdateFlags = 0

        /**
         * The indexes of the slots that are currently being dragged.
         */
        val dragSlots = IntArraySet()

        /**
         * The current drag mode.
         */
        var dragMode = -1

        /**
         * The item stack which will be potentially double clicked on.
         */
        var doubleClickItem: ItemStack? = null

        /**
         * Extra update flags which can be used by specific container layouts.
         */
        var extraUpdateFlags = 0

        /**
         * Whether a complete update is required, which means resending all
         * the packets to initialize the container.
         */
        var updateCompletely = false

        fun queueSlotChange(containerSlot: LanternContainerSlot) =
                this.queueSlotChange(containerSlot.index)

        fun queueSlotChangeSafely(containerSlot: LanternContainerSlot) =
                this.queueSilentSlotChangeSafely(containerSlot.index)

        fun queueSilentSlotChange(containerSlot: LanternContainerSlot) =
                this.queueSilentSlotChange(containerSlot.index)

        fun queueSilentSlotChangeSafely(containerSlot: LanternContainerSlot) =
                this.queueSilentSlotChangeSafely(containerSlot.index)

        fun queueSlotChange(index: Int) {
            this.slotUpdateFlags[index] = UpdateFlags.NEEDS_UPDATE
        }

        fun queueSilentSlotChange(index: Int) {
            this.slotUpdateFlags[index] = UpdateFlags.NEEDS_UPDATE + UpdateFlags.SILENT_UPDATE
        }

        fun queueSlotChangeSafely(index: Int) {
            if (this.slotUpdateFlags[index] and UpdateFlags.NEEDS_UPDATE == 0)
                this.slotUpdateFlags[index] = UpdateFlags.NEEDS_UPDATE
        }

        fun queueSilentSlotChangeSafely(index: Int) {
            if (this.slotUpdateFlags[index] and UpdateFlags.NEEDS_UPDATE == 0)
                this.slotUpdateFlags[index] = UpdateFlags.NEEDS_UPDATE + UpdateFlags.SILENT_UPDATE
        }
    }

    fun queueCompleteUpdate() {
        for (data in this.viewerData)
            data.updateCompletely = true
    }

    override fun queueSlotChange(slot: Slot) {
        val containerSlots = this.containerSlotsBySlot[slot]
        for (containerSlot in containerSlots)
            this.queueSlotChange(containerSlot)
    }

    fun queueSlotChange(containerSlot: LanternContainerSlot) =
            this.queueSlotChange(containerSlot.index)

    override fun queueSlotChange(index: Int) {
        for (data in this.viewers.values)
            data.queueSlotChange(index)
    }

    override fun queueSilentSlotChange(slot: Slot) {
        val containerSlots = this.containerSlotsBySlot[slot]
        for (containerSlot in containerSlots)
            this.queueSilentSlotChange(containerSlot)
    }

    fun queueSilentSlotChange(containerSlot: LanternContainerSlot) =
            this.queueSilentSlotChange(containerSlot.index)

    override fun queueSilentSlotChange(index: Int) {
        for (data in this.viewers.values)
            data.queueSilentSlotChange(index)
    }

    fun queueSlotChangeSafely(index: Int) {
        for (data in this.viewers.values)
            data.queueSlotChangeSafely(index)
    }

    fun queueSilentSlotChangeSafely(containerSlot: LanternContainerSlot) =
            this.queueSilentSlotChangeSafely(containerSlot.index)

    fun queueSilentSlotChangeSafely(index: Int) {
        for (data in this.viewers.values)
            data.queueSilentSlotChangeSafely(index)
    }

    protected open fun collectChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        for (index in this.slots.indices)
            this.collectSlotChangePacket(data, packets, index)

        // Update the cursor item if needed
        if (data.cursorUpdateFlags and UpdateFlags.NEEDS_UPDATE != 0) {
            data.cursorUpdateFlags = 0
            packets.add(SetWindowSlotPacket(-1, -1, this.cursor.item))
        }

        // Collect the property changes
        this.collectPropertyChangePackets(data, packets)
    }

    protected fun collectSlotChangePacket(data: ContainerData, packets: MutableList<Packet>, index: Int, forceSilently: Boolean = false) {
        val slot = this.slots[index]
        if (data.slotUpdateFlags[index] and UpdateFlags.NEEDS_UPDATE == 0)
            return
        var containerId = data.containerId
        // Check if we can do a silent update
        var actualIndex = index
        if (data.slotUpdateFlags[index] and UpdateFlags.SILENT_UPDATE != 0 || forceSilently) {
            val flags = this.slotFlags[index]
            var silentIndex = (flags and Flags.HOTBAR_MASK) shr Flags.HOTBAR_SHIFT
            if (silentIndex == 0) {
                silentIndex = (flags and Flags.SILENT_SLOT_INDEX_MASK) shr Flags.SILENT_SLOT_INDEX_SHIFT
            } else {
                silentIndex-- // hotbar silent index is + 1
            }
            if (silentIndex != 0) {
                actualIndex = silentIndex
                containerId = -2
            }
        }
        // Reset the dirty state
        data.slotUpdateFlags[index] = 0
        // Add a update packet
        packets.add(SetWindowSlotPacket(containerId, this.serverSlotIndexToClient(actualIndex), slot.item))
    }

    protected fun collectPropertyChangePackets(data: ContainerData, packets: MutableList<Packet>) {
        for ((index, value) in this.properties.withIndex()) {
            if (data.propertyUpdateFlags.get(index))
                packets.add(SetWindowPropertyPacket(data.containerId, index, value))
        }
        data.propertyUpdateFlags.clear()
    }

    override fun range(offset: Int, size: Int): ContainerLayout {
        if (offset >= this.size || size + offset >= this.size)
            throw IndexOutOfBoundsException("Cannot get range (offset=$offset,size=$size) from " +
                    "this layout (size=${this.size}).")
        return SubContainerLayout(offset, size, this)
    }

    override fun range(range: IntRange): ContainerLayout {
        if (range.first >= this.size || range.last >= this.size)
            throw IndexOutOfBoundsException("Cannot get range ($range) from this layout (size=$size).")
        return SubContainerLayout(range.first, range.last - range.first + 1, this)
    }

    override fun get(index: Int): ContainerSlot = this.slots[index]
    override fun iterator(): Iterator<ContainerSlot> = this.slots.iterator()

    protected open fun clientSlotIndexToServer(index: Int): Int = if (index < 0) -1 else index
    protected open fun serverSlotIndexToClient(index: Int): Int = if (index < 0) -1 else index

    /**
     * Handles a client click action on a button in the container.
     */
    open fun handleButtonClick(player: Player, index: Int) {
    }

    /**
     * Handles a client creative click interaction for the given [player].
     */
    fun handleCreativeClick(player: Player, clientSlotIndex: Int, itemStack: ItemStack) {
        val data = this.viewers[player] ?: return

        // You can only use this in creative mode
        if (player.require(Keys.GAME_MODE) neq GameModes.CREATIVE)
            return
        // Convert the slot index
        val slotIndex = this.clientSlotIndexToServer(clientSlotIndex)

        // Update the target slot and cursor
        if (slotIndex != -1)
            data.queueSilentSlotChange(this.slots[slotIndex])
        // queueSlotChange(this.cursor);

        // ToDO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleCreativeClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], itemStack)
        }
        */
    }

    /**
     * Handles a client click interaction for the given [player].
     */
    fun handleClick(player: Player, clientSlotIndex: Int, mode: Int, button: Int) {
        val data = this.viewers[player] ?: return
        // Convert the slot index to the server mapping
        val slotIndex = this.clientSlotIndexToServer(clientSlotIndex)

        // Handle and/or reset the drag
        val drag = mode == 5
        if (!drag || !this.handleDrag(data, slotIndex, button))
            this.resetDrag(data)

        // Reset the double click
        val doubleClick = mode == 6 && button == 0
        if (!doubleClick)
            data.doubleClickItem = null
        if (mode == 0 && (button == 0 || button == 1)) {
            // Left/right click inside the inventory
            this.handleLeftRightClick(data, slotIndex, button)
        } else if (mode == 1 && (button == 0 || button == 1)) {
            // Shift + left/right click
            this.handleShiftClick(data, slotIndex, button)
        } else if (doubleClick) {
            // Double click
            this.handleDoubleClick(data, slotIndex)
        } else if (mode == 2) {
            // Number keys
            this.handleNumberKey(data, slotIndex, button)
        } else if (mode == 4 && (button == 0 || button == 1)) {
            if (slotIndex == -1) {
                // Left/right click outside the inventory
                this.handleLeftRightClick(data, -1, button)
            } else {
                // (Control) drop key
                this.handleDropKey(data, slotIndex, button == 1)
            }
        } else if (mode == 3 && button == 2) {
            // Middle click
            this.handleMiddleClick(data, slotIndex)
        } else if (!drag) {
            // Warn about unhandled actions
            Lantern.getLogger().warn("Unknown client container click action: slotIndex: $slotIndex, mode: $mode, button: $button")
        }
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was clicked
     */
    private fun handleMiddleClick(data: ContainerData, slotIndex: Int) {
        // Middle click is only used in creative,
        // you can only do it if the cursor is empty
        // and the target slot isn't empty.
        if (slotIndex != -1 && this.cursor.rawItem.isEmpty && !this.slots[slotIndex].rawItem.isEmpty &&
                data.player.require(Keys.GAME_MODE) eq GameModes.CREATIVE)
            this.queueSlotChange(this.cursor)
        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], MouseButton.MIDDLE)
        }
        */
    }

    /**
     * Resets the current drag process.
     */
    private fun resetDrag(data: ContainerData) {
        if (data.dragMode == -1)
            return
        data.dragMode = -1
        // Force each slot to update
        for (i in data.dragSlots.toIntArray())
            data.queueSlotChange(this.slots[i])
        // Also update the cursor
        data.queueSlotChangeSafely(this.cursor)
        data.dragSlots.clear()
    }

    private fun handleDrag(data: ContainerData, slotIndex: Int, button: Int): Boolean {
        // Extract the drag mode and state from the button
        val mode = button shr 2
        val state = button and 0x3
        // Check if the drag mode matches the current one, or if a new drag started
        if (mode != data.dragMode) {
            // Drag mode mismatch and state isn't "start"
            if (state != 0) {
                // Force to update the send slot if it's an add action
                if (state == 1)
                    data.dragSlots.add(slotIndex)
                return false
            }
            data.dragMode = mode
        }
        when (state) {
            0 -> { // Start state
                // Another start action? Just restart the drag.
                this.resetDrag(data)
                data.dragMode = mode
            }
            1 -> { // Add slot state
                data.dragSlots.add(slotIndex)
            }
            2 -> { // Finish state
                if (!data.dragSlots.isEmpty()) {
                    // Only one slot can be considered a normal click
                    if (data.dragSlots.size == 1) {
                        if (mode < 2)
                            this.handleLeftRightClick(data, data.dragSlots.iterator().nextInt(), mode)
                    } else {
                        val clientSlots = Arrays.stream(data.dragSlots.toIntArray())
                                .mapToObj { i: Int -> this.slots[i] }
                                .toImmutableList()
                        val mouseButton = when (mode) {
                            0 -> MouseButton.LEFT
                            1 -> MouseButton.RIGHT
                            else -> MouseButton.MIDDLE
                        }
                        // TODO
                        /*
                        this.tryProcessBehavior { behavior ->

                            behavior.handleDrag(this, clientSlots, mouseButton)
                        }
                        */
                    }
                }
                // Just reset the drag
                this.resetDrag(data)
            }
        }
        return true
    }

    /**
     * Handles a drop key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param ctrl Whether the control button was pressed
     */
    private fun handleDropKey(data: ContainerData, slotIndex: Int, ctrl: Boolean) {
        // The cursor has to be empty and the target slot
        // cannot be empty or nothing will happen
        if (this.cursor.rawItem.isEmpty && !this.slots[slotIndex].rawItem.isEmpty)
            data.queueSlotChangeSafely(this.slots[slotIndex])
        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleDropKey(this, this.slots[slotIndex], ctrl)
        }*/
    }

    /**
     * Handles a double click interaction.
     *
     * @param slotIndex The slot index that was pressed
     */
    private fun handleDoubleClick(data: ContainerData, slotIndex: Int) {
        this.queueDoubleClickChanges(data, slotIndex)

        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleDoubleClick(this, this.slots[slotIndex])
        }
        */
    }

    /**
     * Queues updates for all the slots that are affected by a double
     * click on the given slot index.
     */
    private fun queueDoubleClickChanges(data: ContainerData, slotIndex: Int) {
        // TODO: Consider items that were made unstackable on the
        //  client, they don't need to be updated
        val doubleClickItem = data.doubleClickItem
        if (doubleClickItem != null) {
            val maxStack = NetworkItemTypeRegistry.getByType(doubleClickItem.type)!!.originalMaxStackSize
            val flags = this.slotFlags
            for (i in flags.indices) {
                // The stack is full, stop
                if (doubleClickItem.quantity >= maxStack)
                    break
                if (i == slotIndex || flags[i] and Flags.IGNORE_DOUBLE_CLICK != 0)
                    continue
                val otherSlot = this.slots[i]
                val otherItem = otherSlot.rawItem
                if (otherItem.isEmpty || !doubleClickItem.isSimilarTo(otherItem))
                    continue
                // Increase quantity
                doubleClickItem.quantity = min(maxStack, doubleClickItem.quantity + otherItem.quantity)
                // Queue a slot change
                data.queueSilentSlotChangeSafely(otherSlot)
            }
            // Update the cursor
            data.queueSlotChangeSafely(this.cursor)
        }
        val slot = this.slots[slotIndex]
        data.queueSlotChange(slot)
        data.doubleClickItem = null
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param number The number that was pressed, 0 - 8 (0 is number 1, etc.)
     */
    private fun handleNumberKey(data: ContainerData, slotIndex: Int, number: Int) {
        this.queueNumberKeyChanges(data, slotIndex, number)

        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleNumberKey(this, this.slots[slotIndex], number + 1)
        }
         */
    }

    /**
     * Queues updates for all the slots that are affected by a number
     * key interaction on the given slot index.
     */
    private fun queueNumberKeyChanges(data: ContainerData, slotIndex: Int, number: Int) {
        // Calculate the hotbar slot index
        val hotbarSlotIndex = this.getHotbarSlotIndex(number)
        // Clicking to the same slot won't do anything and
        // if the both slots are empty also nothing will change
        if (slotIndex != hotbarSlotIndex &&
                (!this.slots[slotIndex].rawItem.isEmpty || !this.slots[hotbarSlotIndex].rawItem.isEmpty)) {
            data.queueSilentSlotChangeSafely(this.slots[slotIndex])
            data.queueSilentSlotChangeSafely(this.slots[hotbarSlotIndex])
        }
    }

    /**
     * Handles a left or right click interaction. `slotIndex` with value -1
     * may be passed in when the click interaction occurs outside the container.
     *
     * @param slotIndex The slot index that was clicked
     * @param button The button that was pressed (0: left; 1: right)
     */
    private fun handleLeftRightClick(data: ContainerData, slotIndex: Int, button: Int) {
        this.queueLeftRightClickChanges(data, slotIndex)

        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], if (button == 1) MouseButton.RIGHT else MouseButton.LEFT)
        }
        */
    }

    /**
     * Queues updates for all the slots that are affected by a left
     * or right click on the given slot index.
     */
    private fun queueLeftRightClickChanges(data: ContainerData, slotIndex: Int) {
        val cursor = !this.cursor.rawItem.isEmpty
        if (slotIndex != -1) {
            val slot = this.slots[slotIndex]
            // Only changes can occur if the cursor slot and the target slot aren't empty
            if (cursor || !slot.rawItem.isEmpty) {
                // Update the slot and cursor
                data.queueSilentSlotChangeSafely(slot)
                data.queueSlotChangeSafely(this.cursor)
                if (!cursor) {
                    // Store the clicked item, it's possible that a double click occurs
                    data.doubleClickItem = slot.item
                }
            }
        } else if (cursor) {
            data.queueSlotChange(this.cursor)
        }
    }

    /**
     * Handles a shift click on the specified slot index, this will queue
     * all the slot updates that are required to force the client to revert
     * the changes.
     *
     * @param slotIndex The slot index that was clicked
     * @param button The button that was pressed (0: left; 1: right)
     */
    private fun handleShiftClick(data: ContainerData, slotIndex: Int, button: Int) {
        this.queueShiftClickChanges(data, slotIndex)

        val mouseButton = if (button == 1) MouseButton.RIGHT else MouseButton.LEFT
        // TODO
        /*
        this.tryProcessBehavior { behavior ->
            behavior.handleShiftClick(this, this.slots[slotIndex], mouseButton)
        }
        */
    }

    /**
     * Queues updates for all the slots that are affected by a shift
     * click on the given slot index.
     */
    private fun queueShiftClickChanges(data: ContainerData, slotIndex: Int) {
        val slot = this.slots[slotIndex]
        val item = slot.item
        // Shift clicking on a empty slot doesn't have any effect
        if (item.isEmpty)
            return
        val flags = this.slotFlags
        data.queueSilentSlotChangeSafely(slot)
        for (i in flags.indices) {
            // Don't shift to itself
            if (i == slotIndex)
                continue
            // You can never shift insert to this slot
            if (flags[i] and Flags.DISABLE_SHIFT_INSERTION != 0)
                continue
            val otherSlot = this.slots[i]
            val otherItem = otherSlot.rawItem
            // Get the amount of items that can be put in the stack
            val maxStack = NetworkItemTypeRegistry.getByType(item.type)!!.originalMaxStackSize
            val limit = min(if (flags[i] and Flags.ONE_ITEM != 0) 1 else 64, maxStack)
            // If the items aren't similar, they won't be able to stack anyway,
            // or if the slot is full
            if (!otherItem.isEmpty && (!item.isSimilarTo(otherItem) || otherItem.quantity >= limit))
                continue
            data.queueSilentSlotChangeSafely(otherSlot)
        }
        // TODO: Check if we can some of the previous checks
        //  back to reduce the amount of packets to revert changes?
    }
}
