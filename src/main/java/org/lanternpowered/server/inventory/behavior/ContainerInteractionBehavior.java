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
package org.lanternpowered.server.inventory.behavior;

import org.lanternpowered.server.inventory.LanternContainer;
import org.lanternpowered.server.inventory.behavior.event.ContainerEvent;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientSlot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

import javax.annotation.Nullable;

/**
 * Represents the behavior when a {@link Player} interacts with
 * a {@link ClientContainer} and {@link LanternContainer}.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType") // Ignore, only we should call this class
public interface ContainerInteractionBehavior {

    /**
     * Handles a shift-click operation with a specific {@link MouseButton}
     * for the target {@link ClientSlot}.
     * <p>
     * Shift-clicking in combination with {@link MouseButton#MIDDLE} is
     * currently not supported by the client.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was clicked
     * @param mouseButton The mouse button that was used in the shift click
     */
    void handleShiftClick(ClientContainer clientContainer, ClientSlot clientSlot, MouseButton mouseButton);

    /**
     * Handles a double-click operation for the target {@link ClientSlot}.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was double clicked
     */
    void handleDoubleClick(ClientContainer clientContainer, ClientSlot clientSlot);

    /**
     * Handles a regular click operation for the target {@link ClientSlot}
     * ({@code null} can occur when clicking outside the container),
     * only a specific {@link MouseButton} is used (no other keys/buttons).
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was clicked
     * @param mouseButton The mouse button that was used in the regular click
     */
    void handleClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, MouseButton mouseButton);

    /**
     * Handles a drop key operation for the target {@link ClientSlot}. {@code ctrl}
     * defines that the control key was pressed when pressing the drop key.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was selected when pressing the key
     * @param ctrl Is the control key pressed
     */
    void handleDropKey(ClientContainer clientContainer, ClientSlot clientSlot, boolean ctrl);

    /**
     * Handles a number key operation for the target {@link ClientSlot}. {@code number}
     * defines which number key was pressed.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot that was selected when pressing the key
     * @param number The pressed number key, counting from 1 to 9
     */
    void handleNumberKey(ClientContainer clientContainer, ClientSlot clientSlot, int number);

    /**
     * Handles a drag operation for the target {@link ClientSlot}s. While dragging
     * was a specific {@link MouseButton} used. The {@link ClientSlot} are provided
     * in the order that they were dragged, the list will never be empty.
     *
     * @param clientContainer The client container
     * @param clientSlots The client slots
     * @param mouseButton The mouse button
     */
    void handleDrag(ClientContainer clientContainer, List<ClientSlot> clientSlots, MouseButton mouseButton);

    /**
     * Handles a creative click operation for the target {@link ClientSlot}
     * ({@code null} can occur when clicking outside the container).
     * On vanilla minecraft will the provided {@link ItemStack} be put in
     * the target {@link ClientSlot}.
     *
     * @param clientContainer The client container
     * @param clientSlot The client slot
     * @param itemStack The item stack
     */
    void handleCreativeClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, @Nullable ItemStack itemStack);

    /**
     * Handles the target {@link ContainerEvent}.
     *
     * @param clientContainer The client container
     * @param event The event to process
     */
    void handleEvent(ClientContainer clientContainer, ContainerEvent event);
}
