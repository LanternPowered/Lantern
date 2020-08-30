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
package org.lanternpowered.server.inventory.client

import com.google.common.base.Preconditions
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import it.unimi.dsi.fastutil.ints.IntArraySet
import it.unimi.dsi.fastutil.ints.IntSet
import org.lanternpowered.api.cause.CauseContextKeys
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withFrame
import org.lanternpowered.api.data.eq
import org.lanternpowered.api.data.neq
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.server.entity.player.LanternPlayer
import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.LanternItemStack
import org.lanternpowered.server.inventory.behavior.ContainerInteractionBehavior
import org.lanternpowered.server.inventory.behavior.MouseButton
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowItemsPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowPropertyPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket
import org.spongepowered.api.data.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.Slot
import java.util.ArrayList
import java.util.Arrays
import java.util.Optional
import java.util.function.IntSupplier
import java.util.function.Supplier
import kotlin.math.min

/**
 * A container that guesses what will change on a client container when a specific
 * operation is executed. For example shift clicking on an item, etc.
 * Additionally can every slot be bound to a specific client slot, and not every slot
 * has to be bound. This allows that you can reorder the complete client inventory
 * without having to modify your original [IInventory]. Just bind each slot
 * to the proper index and the client container will handle the rest.
 */
abstract class ClientContainer(title: Text) : ContainerBase {

    companion object {

        /**
         * Generates a container id.
         *
         * @return The container id
         */
        protected open fun generateContainerId(): Int {
            val containerId = containerIdCounter++
            if (containerIdCounter >= 100) {
                containerIdCounter = 1
            }
            return containerId
        }

        /**
         * The slot index that should be used to bind the cursor slot.
         */
        private const val CURSOR_SLOT_INDEX = 99999

        protected val MAIN_INVENTORY_FLAGS = IntArray(36)

        /**
         * A flag that enables reverse shift insertion behavior from the target slot.
         */
        const val FLAG_REVERSE_SHIFT_INSERTION = 0x1

        /**
         * A flag that disables shift operations to the target slot.
         */
        const val FLAG_DISABLE_SHIFT_INSERTION = 0x2

        /**
         * A flag that defines that not all the shift operation may succeed. For example,
         * in furnaces can the shift operation only succeed if the shifted item
         * is smeltable/cookable.
         */
        protected const val FLAG_POSSIBLY_DISABLED_SHIFT_INSERTION = 0x4

        /**
         * A flag that defines that the slot is present in the main inventory.
         */
        protected const val FLAG_MAIN_INVENTORY = 0x8
        protected const val FLAG_HOTBAR_SHIFT = 4

        /**
         * A flag that defines that the slot is present in the hotbar. The flag
         * uses 4 bits and this is the raw slot index. Counting from 1 to 9.
         */
        protected const val FLAG_HOTBAR_MASK = 0xf shl FLAG_HOTBAR_SHIFT

        /**
         * A flag that defines that only one item (item stack with quantity one)
         * can be present in the target slot.
         */
        protected const val FLAG_ONE_ITEM = 0x100

        /**
         * A flag that defines that the slot is an output slot. Double click can't
         * retrieve items from that slot.
         */
        const val FLAG_IGNORE_DOUBLE_CLICK = 0x200
        protected const val FLAG_SILENT_SLOT_INDEX_SHIFT = 23

        /**
         * A flag that defines the silent update slot index. Not needed to apply
         * on hotbar slot indexes, they are always silently updatable.
         */
        protected const val FLAG_SILENT_SLOT_INDEX_MASK = 0xff shl FLAG_SILENT_SLOT_INDEX_SHIFT

        /**
         * A counter for container ids.
         */
        private var containerIdCounter = 1

        protected var SHIFT_CLICK_WHEN_FULL_TOP = 0x1
        protected var SHIFT_CLICK_TOP_FILTER = 0x2

        @JvmField
        protected var SHIFT_CLICK_WHEN_FULL_TOP_AND_FILTER = SHIFT_CLICK_WHEN_FULL_TOP or SHIFT_CLICK_TOP_FILTER

        /**
         * Compiles a new slot flags array which includes the
         * flags of the main player inventory (36 slots).
         *
         * @param flags The slot flags
         * @return The new slot flags
         */
        @JvmStatic
        protected fun compileAllSlotFlags(flags: IntArray): IntArray {
            val allFlags = IntArray(flags.size + MAIN_INVENTORY_FLAGS.size)
            System.arraycopy(flags, 0, allFlags, 0, flags.size)
            System.arraycopy(MAIN_INVENTORY_FLAGS, 0, allFlags, flags.size, MAIN_INVENTORY_FLAGS.size)
            return allFlags
        }

        init {
            Arrays.fill(MAIN_INVENTORY_FLAGS, 0, MAIN_INVENTORY_FLAGS.size, FLAG_MAIN_INVENTORY)
            for (i in 0..8) {
                // Apply the hotbar flags
                MAIN_INVENTORY_FLAGS[27 + i] = MAIN_INVENTORY_FLAGS[27 + i] or (i + 1 shl 4)
            }
        }
    }

    abstract class BaseClientSlot protected constructor(val index: Int) : ClientSlot {
        @JvmField
        var dirtyState = 0
        abstract val raw: ItemStack

        companion object {
            /**
             * Whether the state is dirty.
             */
            const val IS_DIRTY = 0x1

            /**
             * Whether the slot should be updated silently.
             */
            const val SILENT_UPDATE = 0x2
        }

    }

    private inner class EmptyClientSlot(index: Int) : BaseClientSlot(index), ClientSlot.Empty {
        override fun getItem(): ItemStack {
            return ItemStack.empty()
        }

        protected override val raw: ItemStack
            protected get() = ItemStack.empty()
    }

    private inner class SlotClientSlot(index: Int, private val slot: AbstractSlot) : BaseClientSlot(index), ClientSlot.Slot {
        override fun getItem(): ItemStack {
            return slot.peek()
        }

        override fun getSlot(): AbstractSlot {
            return slot
        }

        protected override val raw: ItemStack
            protected get() {
                val itemStack: ItemStack = slot.getRawItemStack()
                return itemStack ?: ItemStack.empty()
            }

    }

    private inner class IconClientSlot(index: Int) : BaseClientSlot(index), ClientSlot.Button {
        protected override var raw = ItemStack.empty()
            private set

        override fun getItem(): ItemStack {
            return raw.copy()
        }

        override fun setItem(itemStack: ItemStack) {
            raw = Preconditions.checkNotNull(itemStack, "itemStack").copy()
            queueSilentSlotChange(this)
        }

    }

    private var title: Text
    private val slotMap: Multimap<AbstractSlot, SlotClientSlot> = HashMultimap.create()
    private var cursor: BaseClientSlot = EmptyClientSlot(CURSOR_SLOT_INDEX) // Not really a slot, but the implementation does the trick

    /**
     * Gets the container id.
     *
     * @return The container id
     */
    val containerId: Int

    protected var slots: Array<BaseClientSlot>
    private var player: LanternPlayer? = null
    private var interactionBehavior: ContainerInteractionBehavior? = null

    private abstract inner class AbstractContainerPart : ContainerPart {
        override fun getRoot(): ClientContainer {
            return this@ClientContainer
        }

        override fun queueSlotChange(slot: Slot) {
            root.queueSlotChange(slot)
        }

        override fun queueSlotChange(clientSlot: ClientSlot) {
            root.queueSlotChange(clientSlot)
        }

        override fun queueSlotChange(index: Int) {
            root.queueSlotChange(localToGlobalIndex(index))
        }

        override fun queueSilentSlotChange(slot: Slot) {
            root.queueSilentSlotChange(slot)
        }

        override fun queueSilentSlotChange(clientSlot: ClientSlot) {
            root.queueSilentSlotChange(clientSlot)
        }

        override fun queueSilentSlotChange(index: Int) {
            root.queueSilentSlotChange(localToGlobalIndex(index))
        }

        override fun bindSlot(index: Int, slot: AbstractSlot): ClientSlot.Slot {
            return root.bindSlot(localToGlobalIndex(index), slot)
        }

        override fun bindButton(index: Int): ClientSlot.Button {
            return root.bindButton(localToGlobalIndex(index))
        }

        override fun getSlot(index: Int): Optional<AbstractSlot> {
            return root.getSlot(localToGlobalIndex(index))
        }

        override fun getClientSlot(index: Int): Optional<ClientSlot> {
            return root.getClientSlot(localToGlobalIndex(index))
        }

        override fun unbind(index: Int) {
            root.unbind(localToGlobalIndex(index))
        }

        protected abstract fun localToGlobalIndex(index: Int): Int
    }

    private inner class TopContainerPartImpl : AbstractContainerPart(), TopContainerPart {
        override fun localToGlobalIndex(index: Int): Int {
            Preconditions.checkState(index >= 0 && index < topSlotsCount)
            return index
        }

        override fun getSlotIndex(clientSlot: ClientSlot): Int {
            val index = (clientSlot as BaseClientSlot).index
            val size = topSlotsCount
            return if (index >= 0 && index < size) index else -1
        }
    }

    private inner class BottomContainerPartImpl : AbstractContainerPart(), BottomContainerPart {
        override fun localToGlobalIndex(index: Int): Int {
            Preconditions.checkState(index >= 0 && index < MAIN_INVENTORY_FLAGS.size)
            return index + topSlotsCount
        }

        override fun getSlotIndex(clientSlot: ClientSlot): Int {
            var index = (clientSlot as BaseClientSlot).index
            val size = topSlotsCount
            index -= size
            return if (index >= 0 && index < MAIN_INVENTORY_FLAGS.size) index else -1
        }
    }

    private val topContainerPart: TopContainerPart = TopContainerPartImpl()
    private var bottomContainerPart: BottomContainerPart? = null

    // Double click data
    private var doubleClickItem: LanternItemStack? = null

    // Drag mode data
    private val dragSlots: IntSet = IntArraySet()
    private val propertySuppliers: MutableList<PropertyEntry?> = ArrayList()
    private var dragMode = -1

    private class PropertyEntry(val intSupplier: IntSupplier) {
        var previousValue = Int.MAX_VALUE // Force a update
    }

    init {
        // Generate a new container id
        this.containerId = generateContainerId()
        this.title = title
    }

    fun getInteractionBehavior(): Optional<ContainerInteractionBehavior> {
        return Optional.ofNullable(interactionBehavior)
    }

    /**
     * Gets the [ClientSlot] that is bound
     * to the given hotbar slot index.
     *
     * @param hotbarSlotIndex The hotbar slot index, 0 - 8
     * @return The hotbar slot, if found
     */
    fun getHotbarSlot(hotbarSlotIndex: Int): Optional<ClientSlot> {
        populate()
        val slotFlags = slotFlags
        for (i in slots!!.indices) {
            val slotIndex = (slotFlags[i] and FLAG_HOTBAR_MASK shr FLAG_HOTBAR_SHIFT) - 1
            if (slotIndex != -1 && hotbarSlotIndex == slotIndex) {
                return Optional.of(slots!![i])
            }
        }
        return Optional.empty()
    }

    /**
     * Gets the [TopContainerPart] of this [ClientContainer]. This
     * is the top inventory.
     *
     * @return The top container part
     */
    val top: TopContainerPart
        get() {
            populate()
            return topContainerPart
        }

    /**
     * Gets the bound [BottomContainerPart] of this [ClientContainer]. This
     * is the bottom inventory.
     *
     * @return The bottom container part
     */
    val bottom: Optional<BottomContainerPart>
        get() {
            populate()
            return Optional.ofNullable(bottomContainerPart)
        }

    /**
     * Binds the [BottomContainerPart] of this [ClientContainer]. This
     * is the bottom inventory. Calling this method overrides the player bottom
     * inventory, by default will that inventory used.
     *
     * @return The bottom container part
     */
    fun bindBottom(): BottomContainerPart? {
        populate()
        if (this.bottomContainerPart == null) {
            this.bottomContainerPart = BottomContainerPartImpl()
        } else {
            val s = this.topSlotsCount
            for (i in MAIN_INVENTORY_FLAGS.indices)
                this.slots[s + i] = EmptyClientSlot(s + i)
        }
        return this.bottomContainerPart
    }

    fun bindBottom(bottomContainerPart: BottomContainerPart): BottomContainerPart? {
        populate()
        if (this.bottomContainerPart == null) {
            this.bottomContainerPart = BottomContainerPartImpl()
        }
        val clientContainer = bottomContainerPart.root
        val s1 = topSlotsCount
        val s2 = clientContainer.topSlotsCount
        for (i in MAIN_INVENTORY_FLAGS.indices) {
            val index = s1 + i
            removeSlot(index)
            var clientSlot = clientContainer.slots!![s2 + i]
            if (clientSlot is SlotClientSlot) {
                val slot = clientSlot.slot
                clientSlot = SlotClientSlot(index, slot)
                slotMap.put(slot, clientSlot)
                if (player != null) {
                    slot.addTracker(this)
                }
            } else if (clientSlot is IconClientSlot) {
                val itemStack = clientSlot.item
                clientSlot = IconClientSlot(index)
                clientSlot.item = itemStack
            } else {
                clientSlot = EmptyClientSlot(index)
            }
            slots!![index] = clientSlot
        }
        return this.bottomContainerPart
    }

    /**
     * Populates this [ClientContainer]
     * with initial content.
     */
    private fun populate() {
        // Is already populated
        if (slots != null) {
            return
        }
        val flags = slotFlags
        // Create a array to bind slots
        this.slots = Array(this.slotFlags.size) { i -> EmptyClientSlot(i) }
    }

    /**
     * Gets the title.
     *
     * @return The title
     */
    fun getTitle(): Text {
        return title
    }

    /**
     * Sets the title.
     *
     * @param title The title
     */
    fun setTitle(title: Text) {
        this.title = title
    }

    /**
     * Creates a init [Packet] that can be used to
     * open the container on the client. A `null` may
     * be returned if the container can't be opened by
     * the server.
     *
     * @return The init message
     */
    protected abstract fun createInitMessage(): Packet?

    /**
     * Binds the [ContainerInteractionBehavior] to this container.
     *
     * @param interactionBehavior The interaction behavior
     */
    fun bindInteractionBehavior(interactionBehavior: ContainerInteractionBehavior) {
        Preconditions.checkNotNull(interactionBehavior, "interactionBehavior")
        this.interactionBehavior = interactionBehavior
    }

    fun bindCursor(slot: AbstractSlot) {
        bindSlot(CURSOR_SLOT_INDEX, slot)
    }

    private fun unbind(index: Int) {
        populate()
        if (slots[index] is ClientSlot.Empty ||
                index == CURSOR_SLOT_INDEX && cursor is ClientSlot.Empty) {
            return
        }
        removeSlot(index)
        val clientSlot = EmptyClientSlot(index)
        if (index == CURSOR_SLOT_INDEX) {
            cursor = clientSlot
        } else {
            slots[index] = clientSlot
        }
        queueSilentSlotChangeSafely(clientSlot)
    }

    protected open fun bindSlot(index: Int, slot: AbstractSlot): ClientSlot.Slot {
        this.populate()
        val clientSlot = SlotClientSlot(index, slot)
        this.removeSlot(index)
        if (index == CURSOR_SLOT_INDEX) {
            this.cursor = clientSlot
        } else {
            this.slots[index] = clientSlot
        }
        this.slotMap.put(slot, clientSlot)
        if (this.player != null)
            slot.addTracker(this)
        this.queueSilentSlotChange(clientSlot)
        return clientSlot
    }

    private fun bindButton(index: Int): ClientSlot.Button {
        this.populate()
        val clientSlot = IconClientSlot(index)
        this.removeSlot(index)
        this.slots[index] = clientSlot
        return clientSlot
    }

    private fun removeSlot(index: Int) {
        // Cleanup the old client slot
        val oldClientSlot = if (index == CURSOR_SLOT_INDEX) this.cursor else this.slots[index]
        if (oldClientSlot is SlotClientSlot) {
            val slot = oldClientSlot.slot
            // Remove the tracker from this slot
            if (this.slotMap.remove(slot, oldClientSlot) && this.player != null && this.slotMap[slot].isEmpty())
                slot.removeTracker(this)
        }
    }

    /**
     * Binds a [ContainerProperty] type to the given [Supplier].
     *
     * @param propertyType The property type
     * @param supplier The supplier
     * @param <T> The property type
    </T> */
    open fun <T> bindPropertySupplier(propertyType: ContainerProperty<T>?, supplier: Supplier<T>?) {
        Preconditions.checkNotNull(propertyType!!, "propertyType")
        Preconditions.checkNotNull(supplier!!, "supplier")
    }

    /**
     * Binds a [ContainerProperty] type to
     * the given constant value.
     *
     * @param property The property
     * @param <T> The property type
    </T> */
    fun <T> bindProperty(propertyType: ContainerProperty<T>?, property: T) {
        bindPropertySupplier(propertyType, Supplier { property })
    }

    protected fun bindInternalProperty(propertyIndex: Int, property: IntSupplier) {
        Preconditions.checkState(propertyIndex >= 0, "propertyIndex")
        Preconditions.checkNotNull(property, "property")
        // Fill the property suppliers until the provided index is available
        while (propertySuppliers.size <= propertyIndex) {
            propertySuppliers.add(null)
        }
        // Register the property
        propertySuppliers[propertyIndex] = PropertyEntry(property)
    }

    override fun queueSlotChange(slot: Slot) {
        for (clientSlot in this.slotMap[slot as AbstractSlot])
            this.queueSlotChange(clientSlot)
    }

    override fun queueSlotChange(clientSlot: ClientSlot) {
        this.queueSlotChange(clientSlot as BaseClientSlot)
    }

    override fun queueSlotChange(index: Int) {
        this.queueSlotChange(this.slots[index])
    }

    protected fun queueSlotChange(clientSlot: BaseClientSlot) {
        this.populate()
        if (this.player == null)
            return
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY
    }

    protected fun queueSlotChangeSafely(clientSlot: BaseClientSlot) {
        this.populate()
        if (this.player == null)
            return
        if (clientSlot.dirtyState and BaseClientSlot.IS_DIRTY == 0)
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY
    }

    override fun queueSilentSlotChange(slot: Slot) {
        for (clientSlot in this.slotMap[slot as AbstractSlot])
            this.queueSilentSlotChange(clientSlot)
    }

    override fun queueSilentSlotChange(clientSlot: ClientSlot) {
        this.queueSilentSlotChange(clientSlot as BaseClientSlot)
    }

    override fun queueSilentSlotChange(index: Int) {
        this.queueSilentSlotChange(this.slots[index])
    }

    protected fun queueSilentSlotChange(clientSlot: BaseClientSlot) {
        this.populate()
        if (this.player == null)
            return
        clientSlot.dirtyState = BaseClientSlot.IS_DIRTY + BaseClientSlot.SILENT_UPDATE
    }

    protected fun queueSilentSlotChangeSafely(clientSlot: BaseClientSlot) {
        this.populate()
        if (this.player == null)
            return
        if (clientSlot.dirtyState and BaseClientSlot.IS_DIRTY == 0)
            clientSlot.dirtyState = BaseClientSlot.IS_DIRTY + BaseClientSlot.SILENT_UPDATE
    }

    fun getPlayer(): LanternPlayer = this.player ?: error("No player is bound")

    /**
     * Binds this [ClientContainer] to the given [LanternPlayer].
     *
     * @param player The player
     */
    fun bind(player: Player) {
        if (this.player != null)
            error("There is already a player bound")
        this.populate()
        this.player = player as LanternPlayer
        // Add the tracker to each slot
        for (slot in this.slotMap.keySet())
            slot.addTracker(this)
    }

    /**
     * Initializes the container for the bounded [Player].
     */
    fun init() {
        Preconditions.checkState(player != null)
        this.populate()
        val packets = ArrayList<Packet>()
        val packet = this.createInitMessage()
        if (packet != null)
            packets.add(packet)
        val items = Array<ItemStack>(this.slotFlags.size) { emptyItemStack() }
        for (i in items.indices) {
            items[this.serverSlotIndexToClient(i)] = slots[i].item
            this.slots[i].dirtyState = 0
        }
        // Send the inventory content
        packets.add(SetWindowItemsPacket(containerId, items))
        // Send the cursor item if present
        if (!this.cursor.raw.isEmpty) {
            packets.add(SetWindowSlotPacket(-1, -1, cursor.item))
            this.cursor.dirtyState = 0
        }
        // Collect additional messages
        collectInitMessages(packets)
        // Stream the messages to the player
        this.getPlayer().connection.send(packets)
    }

    protected open fun collectInitMessages(packets: List<Packet>?) {}

    fun update() {
        Preconditions.checkState(player != null)
        populate()
        val packets: MutableList<Packet> = ArrayList()
        // Collect all the changes
        collectChangeMessages(packets)
        if (!packets.isEmpty()) {
            // Stream the messages to the player
            player!!.connection.send(packets)
        }
    }

    protected open fun collectChangeMessages(packets: MutableList<Packet>) {
        for (i in slots!!.indices) {
            collectSlotChangeMessages(packets, i, false)
        }
        // Update the cursor item if needed
        if (cursor.dirtyState and BaseClientSlot.IS_DIRTY != 0) {
            packets.add(SetWindowSlotPacket(-1, -1, cursor.item))
            cursor.dirtyState = 0
        }
        // Collect the property changes
        collectPropertyChanges(packets)
    }

    protected fun collectSlotChangeMessages(packets: MutableList<Packet>, index: Int, forceSilently: Boolean) {
        var index = index
        val slot = this.slots[index]
        if (slot.dirtyState and BaseClientSlot.IS_DIRTY != 0) {
            var containerId = this.containerId
            // Check if we can do a silent update
            if (slot.dirtyState and BaseClientSlot.SILENT_UPDATE != 0 || forceSilently) {
                val flags = this.slotFlags[index]
                var silentIndex = flags and FLAG_HOTBAR_MASK shr FLAG_HOTBAR_SHIFT
                if (silentIndex == 0) {
                    silentIndex = flags and FLAG_SILENT_SLOT_INDEX_MASK shr FLAG_SILENT_SLOT_INDEX_SHIFT
                } else {
                    silentIndex-- // hotbar silent index is + 1
                }
                if (silentIndex != 0) {
                    index = silentIndex
                    containerId = -2
                }
            }
            // Reset the dirty state
            slot.dirtyState = 0
            // Add a update message
            packets.add(SetWindowSlotPacket(containerId, serverSlotIndexToClient(index), slot.item))
        }
    }

    protected fun collectPropertyChanges(packets: MutableList<Packet>) {
        for (i in propertySuppliers.indices) {
            val entry = propertySuppliers[i]
            if (entry != null) {
                val newValue = entry.intSupplier.asInt
                if (newValue != entry.previousValue) {
                    entry.previousValue = newValue
                    packets.add(SetWindowPropertyPacket(containerId, i, newValue))
                }
            }
        }
    }

    /**
     * Releases all the [AbstractSlot] and
     * removes the [LanternPlayer].
     */
    fun release() {
        populate()
        if (player == null) {
            return
        }
        player = null
        // Remove the tracker from each slot
        for (slot in slotMap.keySet()) {
            slot.removeTracker(this)
        }
    }

    /**
     * Converts the hotbar slot index (0 - 8) to
     * a slot index within this container.
     *
     * @param hotbarSlot The hotbar slot
     * @return The slot index
     */
    fun getHotbarSlotIndex(hotbarSlot: Int): Int = this.slotFlags.size - (9 - hotbarSlot)

    override fun getClientSlot(index: Int): Optional<ClientSlot> {
        this.populate()
        return if (index == CURSOR_SLOT_INDEX) {
            this.cursor.asOptional()
        } else if (index < 0 || index >= this.slots.size) {
            emptyOptional()
        } else {
            this.slots[index].asOptional<ClientSlot>()
        }
    }

    override fun getSlot(index: Int): Optional<AbstractSlot> {
        this.populate()
        if (index != CURSOR_SLOT_INDEX && (index < 0 || index >= this.slots.size))
            return Optional.empty()
        val clientSlot = if (index == CURSOR_SLOT_INDEX) this.cursor else this.slots[index]
        return if (clientSlot is SlotClientSlot) Optional.of(clientSlot.slot) else Optional.empty()
    }

    val topSlotsCount: Int
        get() = this.topSlotFlags.size

    /**
     * Gets a array that contains all the flags
     * of the slots in this container. This does
     * not include the flags of the main player
     * inventory.
     *
     * @return The slot flags
     */
    protected abstract val topSlotFlags: IntArray

    /**
     * Gets a array that contains all the flags
     * of the slots in this container.
     *
     * @return The slot flags
     */
    protected open val slotFlags: IntArray
        protected get() = compileAllSlotFlags(topSlotFlags)

    /**
     * Gets flags related to shift clicking.
     *
     * @return Shift click flags
     */
    protected open val shiftFlags: Int
        protected get() = 0

    protected open fun clientSlotIndexToServer(index: Int): Int = if (index < 0) -1 else index
    protected open fun serverSlotIndexToClient(index: Int): Int = if (index < 0) -1 else index

    fun handlePick(slotIndex: Int) {
        this.populate()
        // Convert the slot index
        val serverSlotIndex = clientSlotIndexToServer(slotIndex)
        this.queueSilentSlotChangeSafely(this.slots[serverSlotIndex])
        val hotbarSlotIndex: Int = player.getInventoryContainer().getClientContainer().getSelectedHotbarSlotIndex()
        this.queueSilentSlotChangeSafely(this.slots[this.getHotbarSlotIndex(hotbarSlotIndex)])
        this.tryProcessBehavior { behavior ->
            behavior.handlePick(this, this.slots[serverSlotIndex])
        }
    }

    fun handleCreativeClick(player: Player, clientSlotIndex: Int, itemStack: ItemStack) {
        this.populate()
        // You can only use this in creative mode
        if (player.require(Keys.GAME_MODE) neq GameModes.CREATIVE)
            return
        // Convert the slot index
        val slotIndex = this.clientSlotIndexToServer(clientSlotIndex)

        // Update the target slot and cursor
        if (slotIndex != -1)
            this.queueSilentSlotChange(this.slots[slotIndex])
        // queueSlotChange(this.cursor);
        this.tryProcessBehavior { behavior ->
            behavior.handleCreativeClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], itemStack)
        }
    }

    fun handleClick(player: Player, clientSlotIndex: Int, mode: Int, button: Int) {
        // Convert the slot index to the server mapping
        val slotIndex = this.clientSlotIndexToServer(clientSlotIndex)
        this.populate()

        // Handle and/or reset the drag
        val drag = mode == 5
        if (!drag || !this.handleDrag(slotIndex, button))
            this.resetDrag()

        // Reset the double click
        val doubleClick = mode == 6 && button == 0
        if (!doubleClick)
            this.doubleClickItem = null
        if (mode == 0 && (button == 0 || button == 1)) {
            // Left/right click inside the inventory
            this.handleLeftRightClick(slotIndex, button)
        } else if (mode == 1 && (button == 0 || button == 1)) {
            // Shift + left/right click
            this.handleShiftClick(slotIndex, button)
        } else if (doubleClick) {
            // Double click
            this.handleDoubleClick(slotIndex)
        } else if (mode == 2) {
            // Number keys
            this.handleNumberKey(slotIndex, button)
        } else if (mode == 4 && (button == 0 || button == 1)) {
            if (slotIndex == -1) {
                // Left/right click outside the inventory
                this.handleLeftRightClick(-1, button)
            } else {
                // (Control) drop key
                this.handleDropKey(slotIndex, button == 1)
            }
        } else if (mode == 3 && button == 2) {
            // Middle click
            this.handleMiddleClick(slotIndex)
        } else if (!drag) {
            // Warn about unhandled actions
            Lantern.getLogger().warn("Unknown client container click action: slotIndex: $slotIndex, mode: $mode, button: $button")
        }
    }

    protected fun tryProcessBehavior(behavior: (behavior: ContainerInteractionBehavior) -> Unit) {
        val interactionBehavior = this.interactionBehavior
        if (interactionBehavior != null) {
            CauseStack.current().withFrame { frame ->
                frame.pushCause(this.getPlayer())
                frame.pushCause(this)
                // Also add the player as context
                frame.addContext(CauseContextKeys.PLAYER, this.getPlayer())
                try {
                    behavior(interactionBehavior)
                } catch (t: Throwable) {
                    Lantern.getLogger().error("Failed to process the inventory interaction behavior", t)
                }
            }
        }
    }

    /**
     * Resets the current drag process.
     */
    private fun resetDrag() {
        if (this.dragMode == -1)
            return
        this.dragMode = -1
        // Force each slot to update
        for (i in this.dragSlots.toIntArray())
            this.queueSlotChange(this.slots[i])
        // Also update the cursor
        this.queueSlotChangeSafely(this.cursor)
        this.dragSlots.clear()
    }

    private fun handleDrag(slotIndex: Int, button: Int): Boolean {
        // Extract the drag mode and state from the button
        val mode = button shr 2
        val state = button and 0x3
        // Check if the drag mode matches the current one, or if a new drag started
        if (mode != this.dragMode) {
            // Drag mode mismatch and state isn't "start"
            if (state != 0) {
                // Force to update the send slot if it's an add action
                if (state == 1)
                    this.dragSlots.add(slotIndex)
                return false
            }
            this.dragMode = mode
        }
        when (state) {
            0 -> { // Start state
                // Another start action? Just restart the drag.
                this.resetDrag()
                this.dragMode = mode
            }
            1 -> { // Add slot state
                this.dragSlots.add(slotIndex)
            }
            2 -> { // Finish state
                if (!this.dragSlots.isEmpty()) {
                    // Only one slot can be considered a normal click
                    if (this.dragSlots.size == 1) {
                        // TODO: Can this be considered a "long" click? New click type? Check if this can work.
                        if (mode < 2) {
                            this.handleLeftRightClick(this.dragSlots.iterator().nextInt(), mode)
                        }
                    } else {
                        this.tryProcessBehavior { behavior ->
                            val clientSlots = Arrays.stream(this.dragSlots.toIntArray())
                                    .mapToObj { i: Int -> this.slots[i] }
                                    .toImmutableList()
                            val mouseButton = when (mode) {
                                0 -> MouseButton.LEFT
                                1 -> MouseButton.RIGHT
                                else -> MouseButton.MIDDLE
                            }
                            behavior.handleDrag(this, clientSlots, mouseButton)
                        }
                    }
                }
                // Just reset the drag
                this.resetDrag()
            }
        }
        return true
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was clicked
     */
    private fun handleMiddleClick(slotIndex: Int) {
        val player = this.player
        // Middle click is only used in creative,
        // you can only do it if the cursor is empty
        // and the target slot isn't empty.
        if (slotIndex != -1 && this.cursor.raw.isEmpty && !this.slots[slotIndex].raw.isEmpty &&
                player != null && player.require(Keys.GAME_MODE) eq GameModes.CREATIVE)
            this.queueSlotChange(this.cursor)
        this.tryProcessBehavior { behavior ->
            behavior.handleClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], MouseButton.MIDDLE)
        }
    }

    /**
     * Handles a number key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param number The number that was pressed, 0 - 8 (0 is number 1, etc.)
     */
    private fun handleNumberKey(slotIndex: Int, number: Int) {
        // Calculate the hotbar slot index
        val hotbarSlotIndex = this.getHotbarSlotIndex(number)
        // Clicking to the same slot won't do anything and
        // if the both slots are empty also nothing will change
        if (slotIndex != hotbarSlotIndex &&
                (!this.slots[slotIndex].raw.isEmpty || !this.slots[hotbarSlotIndex].raw.isEmpty)) {
            this.queueSilentSlotChangeSafely(this.slots[slotIndex])
            this.queueSilentSlotChangeSafely(this.slots[hotbarSlotIndex])
        }
        this.tryProcessBehavior { behavior ->
            behavior.handleNumberKey(this, this.slots[slotIndex], number + 1)
        }
    }

    /**
     * Handles a double click interaction.
     *
     * @param slotIndex The slot index that was pressed
     */
    private fun handleDoubleClick(slotIndex: Int) {
        val doubleClickItem = this.doubleClickItem
        if (doubleClickItem != null) {
            val maxStack = ClientItemStackSizes.getOriginalMaxSize(doubleClickItem.type)
            val flags = slotFlags
            for (i in flags.indices) {
                // The stack is full, stop
                if (doubleClickItem.quantity >= maxStack)
                    break
                if (i == slotIndex || flags[i] and FLAG_IGNORE_DOUBLE_CLICK != 0)
                    continue
                val slot1 = this.slots[i]
                val itemStack1 = slot1.raw
                if (itemStack1.isEmpty || !doubleClickItem.isSimilarTo(itemStack1))
                    continue
                // Increase quantity
                doubleClickItem.quantity = min(maxStack, doubleClickItem.quantity + itemStack1.quantity)
                // Queue a slot change
                this.queueSilentSlotChangeSafely(slot1)
            }
            // Update the cursor
            this.queueSlotChangeSafely(this.cursor)
        }
        val slot = this.slots[slotIndex]
        this.queueSlotChange(slot)
        this.doubleClickItem = null
        this.tryProcessBehavior { behavior ->
            behavior.handleDoubleClick(this, this.slots[slotIndex])
        }
    }

    /**
     * Handles a drop key interaction.
     *
     * @param slotIndex The slot index that was selected while pressing the button
     * @param ctrl Whether the control button was pressed
     */
    private fun handleDropKey(slotIndex: Int, ctrl: Boolean) {
        // The cursor has to be empty and the target slot
        // cannot be empty or nothing will happen
        if (this.cursor.raw.isEmpty && !this.slots[slotIndex].raw.isEmpty)
            this.queueSlotChangeSafely(this.slots[slotIndex])
        this.tryProcessBehavior { behavior ->
            behavior.handleDropKey(this, this.slots[slotIndex], ctrl)
        }
    }

    /**
     * Handles a left or right click interaction. `slotIndex` with value -1
     * may be passed in when the click interaction occurs outside the container.
     *
     * @param slotIndex The slot index that was clicked
     * @param button The button that was pressed (0: left; 1: right)
     */
    private fun handleLeftRightClick(slotIndex: Int, button: Int) {
        val cursor = !this.cursor.raw.isEmpty
        if (slotIndex != -1) {
            val slot = this.slots[slotIndex]
            // Only changes can occur if the cursor slot and the target slot aren't empty
            if (cursor || !slot.raw.isEmpty) {
                // Update the slot and cursor
                this.queueSilentSlotChangeSafely(slot)
                this.queueSlotChangeSafely(this.cursor)
                if (!cursor) {
                    // Store the clicked item, it's possible that a double click occurs
                    this.doubleClickItem = slot.item as LanternItemStack
                }
            }
        } else if (cursor) {
            this.queueSlotChange(this.cursor)
        }
        this.tryProcessBehavior { behavior ->
            behavior.handleClick(this,
                    if (slotIndex == -1) null else this.slots[slotIndex], if (button == 1) MouseButton.RIGHT else MouseButton.LEFT)
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
    private fun handleShiftClick(slotIndex: Int, button: Int) {
        this.queueShiftClickChanges(slotIndex)
        this.tryProcessBehavior { behavior ->
            val mouseButton = if (button == 1) MouseButton.RIGHT else MouseButton.LEFT
            behavior.handleShiftClick(this, this.slots[slotIndex], mouseButton)
        }
    }

    /**
     * Queues updates for all the slots that are affected by a shift
     * click on the given slot index.
     */
    private fun queueShiftClickChanges(slotIndex: Int) {
        val slot = this.slots[slotIndex]
        val itemStack = slot.item
        // Shift clicking on a empty slot doesn't have any effect
        if (itemStack.isEmpty)
            return
        val flags = this.slotFlags
        this.queueSilentSlotChangeSafely(slot)
        for (i in flags.indices) {
            // Don't shift to itself
            if (i == slotIndex)
                continue
            // You can never shift insert to this slot
            if (flags[i] and FLAG_DISABLE_SHIFT_INSERTION != 0)
                continue
            val slot1 = this.slots[i]
            val itemStack1 = slot1.raw
            // Get the amount of items that can be put in the stack
            val maxStack = ClientItemStackSizes.getOriginalMaxSize(itemStack.type)
            val limit = min(if (flags[i] and FLAG_ONE_ITEM != 0) 1 else 64, maxStack)
            // If the items aren't similar, they won't be able to stack anyway,
            // or if the slot is full
            if (!itemStack1.isEmpty && (!itemStack.isSimilarTo(itemStack1) || itemStack1.quantity >= limit))
                continue
            this.queueSilentSlotChangeSafely(slot1)
        }
    }
}
