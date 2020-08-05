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
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.world.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OpenableContainerInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        final Optional<TileEntity> optTileEntity = location.getTileEntity();
        if (!optTileEntity.isPresent()) {
            return BehaviorResult.CONTINUE;
        }
        final List<Runnable> tasks = new ArrayList<>();
        if (!validateOpenableSpace(context, location, tasks)) {
            return BehaviorResult.CONTINUE;
        }
        final Optional<Inventory> optInventory = getInventoryFrom(optTileEntity.get());
        if (!optInventory.isPresent()) {
            return BehaviorResult.CONTINUE;
        }
        final LanternPlayer player = (LanternPlayer) context.getContext(ContextKeys.PLAYER).orElse(null);
        if (player == null) {
            return BehaviorResult.CONTINUE;
        }
        if (player.openInventory(optInventory.get()).isPresent()) {
            tasks.forEach(Runnable::run);
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.CONTINUE;
    }

    protected boolean validateOpenableSpace(BehaviorContext context, Location location, List<Runnable> task) {
        return true;
    }

    protected Optional<Inventory> getInventoryFrom(TileEntity tileEntity) {
        return tileEntity instanceof Carrier ? Optional.of(((Carrier) tileEntity).getInventory()) : Optional.empty();
    }
}
