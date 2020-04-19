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
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.inventory.AbstractInventory;
import org.lanternpowered.server.inventory.InventoryViewerListener;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnderChestInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        if (player == null) {
            return BehaviorResult.CONTINUE;
        }
        final List<Runnable> tasks = new ArrayList<>();
        if (!ChestInteractionBehavior.validateOpenableChestSpace(context,
                context.requireContext(ContextKeys.BLOCK_LOCATION), tasks)) {
            return BehaviorResult.CONTINUE;
        }
        final Location location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        final AbstractInventory inventory = player.getEnderChestInventory();
        final Optional<TileEntity> optTileEntity = location.getTileEntity();
        if (optTileEntity.isPresent() && optTileEntity.get() instanceof InventoryViewerListener) {
            inventory.addViewListener((InventoryViewerListener) optTileEntity.get());
        }
        if (!player.openInventory(inventory).isPresent()) {
            tasks.forEach(Runnable::run);
            return BehaviorResult.CONTINUE;
        }
        return BehaviorResult.SUCCESS;
    }
}
