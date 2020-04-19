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
