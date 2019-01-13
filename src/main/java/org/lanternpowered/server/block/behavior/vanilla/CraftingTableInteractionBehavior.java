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
package org.lanternpowered.server.block.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.AbstractContainer;
import org.lanternpowered.server.inventory.carrier.LanternBlockCarrier;
import org.lanternpowered.server.inventory.vanilla.PlayerReturnItemsInventoryCloseListener;
import org.lanternpowered.server.inventory.vanilla.VanillaInventoryArchetypes;
import org.lanternpowered.server.inventory.vanilla.block.CraftingTableInventory;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

import java.util.Optional;

public class CraftingTableInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Player> optPlayer = context.getContext(ContextKeys.PLAYER);
        if (optPlayer.isPresent()) {
            final CraftingTableInventory craftingTableInventory = VanillaInventoryArchetypes.CRAFTING_TABLE.builder()
                    .withCarrier(new LanternBlockCarrier(context.requireContext(ContextKeys.BLOCK_LOCATION)))
                    .build(Lantern.getMinecraftPlugin());
            final Optional<Container> optContainer = optPlayer.get().openInventory(craftingTableInventory);
            if (optContainer.isPresent()) {
                final AbstractContainer container = (AbstractContainer) optContainer.get();
                container.addCloseListener(new PlayerReturnItemsInventoryCloseListener(
                        QueryOperationTypes.INVENTORY_TYPE.of(CraftingInventory.class)));
                return BehaviorResult.SUCCESS;
            }
        }
        return BehaviorResult.PASS;
    }
}
