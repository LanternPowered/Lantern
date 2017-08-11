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
package org.lanternpowered.server;

import com.google.inject.Inject;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.behavior.ContainerInteractionBehavior;
import org.lanternpowered.server.inventory.behavior.MouseButton;
import org.lanternpowered.server.inventory.behavior.event.ContainerEvent;
import org.lanternpowered.server.inventory.block.ChestInventory;
import org.lanternpowered.server.inventory.client.BottomContainerPart;
import org.lanternpowered.server.inventory.client.ClientContainer;
import org.lanternpowered.server.inventory.client.ClientSlot;
import org.lanternpowered.server.inventory.client.PlayerClientContainer;
import org.lanternpowered.server.text.translation.TextTranslation;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.List;

import javax.annotation.Nullable;

@Plugin(id = "expanded_container_test")
public class FancyTestPlugin {

    @Inject
    private Logger logger;

    @Listener
    public void onStart(GameInitializationEvent event) {
        this.logger.info("Fancy container test plugin enabled!");
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        final LanternPlayer player = (LanternPlayer) event.getTargetEntity();
        final PlayerClientContainer clientContainer = player.getInventoryContainer().getClientContainer();
        final ContainerInteractionBehavior vanillaBehavior = clientContainer.getInteractionBehavior().get();
        clientContainer.bindInteractionBehavior(new FancyContainerInteractionBehavior(vanillaBehavior));
    }

    private static class FancyContainerInteractionBehavior implements ContainerInteractionBehavior {

        private final ChestInventory[] chests = new ChestInventory[8];
        private final ContainerInteractionBehavior behavior;
        private int selectedInventory = 0;

        private FancyContainerInteractionBehavior(ContainerInteractionBehavior behavior) {
            this.behavior = behavior;
            for (int i = 0; i < this.chests.length; i++) {
                this.chests[i] = new ChestInventory(null, TextTranslation.of(Text.of("Bag #" + (i + 1))), 3);
            }
        }

        @Override
        public void handleNumberKey(ClientContainer clientContainer, ClientSlot clientSlot, int number) {
            if (number == this.selectedInventory) {
                return;
            }
            // Check if it's in the main inventory
            final int index = clientContainer.getBottom().get().getSlotIndex(clientSlot);
            if (index == -1 || index >= 27) {
                return;
            }
            final BottomContainerPart part = clientContainer.getBottom().get();
            (number == 1 ? clientContainer.getPlayer().getInventory().getMain() : this.chests[number - 2])
                    .getIndexBySlots().object2IntEntrySet().forEach(entry -> {
                        if (entry.getIntValue() < 27) {
                            part.bindSlot(entry.getIntValue(), entry.getKey());
                        }
                    });
        }

        // Forward all the other actions

        @Override
        public void handleShiftClick(ClientContainer clientContainer, ClientSlot clientSlot, MouseButton mouseButton) {
            this.behavior.handleShiftClick(clientContainer, clientSlot, mouseButton);
        }

        @Override
        public void handleDoubleClick(ClientContainer clientContainer, ClientSlot clientSlot) {
            this.behavior.handleDoubleClick(clientContainer, clientSlot);
        }

        @Override
        public void handleClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, MouseButton mouseButton) {
            this.behavior.handleClick(clientContainer, clientSlot, mouseButton);
        }

        @Override
        public void handleDropKey(ClientContainer clientContainer, ClientSlot clientSlot, boolean ctrl) {
            this.behavior.handleDropKey(clientContainer, clientSlot, ctrl);
        }

        @Override
        public void handleDrag(ClientContainer clientContainer, List<ClientSlot> clientSlots, MouseButton mouseButton) {
            this.behavior.handleDrag(clientContainer, clientSlots, mouseButton);
        }

        @Override
        public void handleCreativeClick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot, @Nullable ItemStack itemStack) {
            this.behavior.handleCreativeClick(clientContainer, clientSlot, itemStack);
        }

        @Override
        public void handlePick(ClientContainer clientContainer, @Nullable ClientSlot clientSlot) {
            this.behavior.handlePick(clientContainer, clientSlot);
        }

        @Override
        public void handleEvent(ClientContainer clientContainer, ContainerEvent event) {
            this.behavior.handleEvent(clientContainer, event);
        }
    }
}
