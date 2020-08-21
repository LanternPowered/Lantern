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
package org.lanternpowered.server.inventory.behavior

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.server.inventory.behavior.event.ContainerEvent
import org.lanternpowered.server.inventory.client.ClientContainer
import org.lanternpowered.server.inventory.client.ClientSlot

/**
 * Represents the behavior when a [Player] interacts with a [ClientContainer].
 *
 * The current [CauseStack] will always be populated before
 * a handler method is invoked. In the process will the [Player]
 * be added as normal cause and context value. The [ClientContainer]
 * will also be available in the cause.
 *
 * A [CauseStack.Frame] will be entered before a handler method is
 * executed and will be exited when it is done.
 */
interface ContainerInteractionBehavior {

    /**
     * Handles a shift-click operation with a specific [MouseButton]
     * for the target [ClientSlot].
     *
     * Shift-clicking in combination with [MouseButton.MIDDLE] is
     * currently not supported by the client.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was clicked
     * @param mouseButton The mouse button that was used in the shift click
     */
    fun handleShiftClick(clientContainer: ClientContainer, clientSlot: ClientSlot, mouseButton: MouseButton)

    /**
     * Handles a double-click operation for the target [ClientSlot].
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was double clicked
     */
    fun handleDoubleClick(clientContainer: ClientContainer, clientSlot: ClientSlot)

    /**
     * Handles a regular click operation for the target [ClientSlot]
     * (`null` can occur when clicking outside the container),
     * only a specific [MouseButton] is used (no other keys/buttons).
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was clicked
     * @param mouseButton The mouse button that was used in the regular click
     */
    fun handleClick(clientContainer: ClientContainer, clientSlot: ClientSlot?, mouseButton: MouseButton)

    /**
     * Handles a drop key operation for the target [ClientSlot]. `ctrl`
     * defines that the control key was pressed when pressing the drop key.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was selected when pressing the key
     * @param ctrl Is the control key pressed
     */
    fun handleDropKey(clientContainer: ClientContainer, clientSlot: ClientSlot, ctrl: Boolean)

    /**
     * Handles a number key operation for the target [ClientSlot]. `number`
     * defines which number key was pressed.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was selected when pressing the key
     * @param number The pressed number key, counting from 1 to 9
     */
    fun handleNumberKey(clientContainer: ClientContainer, clientSlot: ClientSlot, number: Int)

    /**
     * Handles a drag operation for the target [ClientSlot]s. While dragging
     * was a specific [MouseButton] used. The [ClientSlot] are provided
     * in the order that they were dragged, the list will never be empty.
     *
     * @param clientContainer The client container
     * @param clientSlots The client slots
     * @param mouseButton The mouse button
     */
    fun handleDrag(clientContainer: ClientContainer, clientSlots: List<ClientSlot>, mouseButton: MouseButton?)

    /**
     * Handles a creative click operation for the target [ClientSlot]
     * (`null` can occur when clicking outside the container).
     * On vanilla minecraft will the provided [ItemStack] be put in
     * the target [ClientSlot].
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot
     * @param itemStack The item stack
     */
    fun handleCreativeClick(clientContainer: ClientContainer, clientSlot: ClientSlot, itemStack: ItemStack)

    /**
     * Handles a item pick operation. The client sends a [ClientSlot] to swap
     * the contents with the hotbar slot, this occurs when a player middle clicks a
     * block. The block item stack must exactly match the contents in the target slot
     * before a operation will be executed.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot
     */
    fun handlePick(clientContainer: ClientContainer, clientSlot: ClientSlot)

    /**
     * Handles the target [ContainerEvent].
     *
     * @param clientContainer The client container
     * @param event The event to process
     */
    fun handleEvent(clientContainer: ClientContainer, event: ContainerEvent)
}
