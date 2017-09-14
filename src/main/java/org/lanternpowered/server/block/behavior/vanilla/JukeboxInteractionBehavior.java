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
import org.lanternpowered.server.block.tile.vanilla.LanternJukebox;
import org.lanternpowered.server.event.CauseStack;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.item.RecordProperty;
import org.spongepowered.api.effect.sound.record.RecordType;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class JukeboxInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final CauseStack causeStack = context.getCauseStack();
        final Location<World> location = causeStack.requireContext(ContextKeys.INTERACTION_LOCATION);
        final Optional<TileEntity> optTile = location.getTileEntity();
        if (optTile.isPresent()) {
            final TileEntity tile = optTile.get();
            if (tile instanceof Jukebox) {
                final LanternJukebox jukebox = (LanternJukebox) tile;
                final Optional<Entity> optEjectedItem = jukebox.ejectRecordItem();
                boolean success = false;
                if (optEjectedItem.isPresent()) {
                    final Entity entity = optEjectedItem.get();
                    entity.getWorld().spawnEntity(optEjectedItem.get());
                    // TODO: Include the entity in the behavior context
                    success = true;
                }
                final Optional<ItemStack> optItemStack = causeStack.getContext(ContextKeys.USED_ITEM_STACK);
                if (optItemStack.isPresent()) {
                    final ItemStack itemStack = optItemStack.get();
                    final RecordProperty property = itemStack.getProperty(RecordProperty.class).orElse(null);
                    final RecordType recordType = property == null ? null : property.getValue();
                    if (recordType != null) {
                        final ItemStackSnapshot oldSnapshot = itemStack.createSnapshot();
                        itemStack.setQuantity(itemStack.getQuantity() - 1);
                        final ItemStackSnapshot newSnapshot = itemStack.createSnapshot();
                        causeStack.getContext(ContextKeys.PLAYER).ifPresent(player -> {
                            if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                                causeStack.getContext(ContextKeys.USED_SLOT).ifPresent(slot -> context.addSlotChange(
                                        new SlotTransaction(slot, oldSnapshot, newSnapshot)));
                            }
                        });
                        itemStack.setQuantity(1);
                        jukebox.insertRecord(itemStack);
                        jukebox.playRecord();
                        success = true;
                    }
                }
                if (success) {
                    return BehaviorResult.SUCCESS;
                }
            }
        }
        return BehaviorResult.PASS;
    }
}
