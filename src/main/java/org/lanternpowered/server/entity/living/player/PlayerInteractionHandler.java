/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.entity.living.player;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.entity.LanternHotbar;
import org.lanternpowered.server.inventory.entity.OffHandSlot;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.Set;

public final class PlayerInteractionHandler {

    private final LanternPlayer player;

    /**
     * The block that is being digged.
     */
    private Vector3i diggingBlock;
    private BlockType diggingBlockType;

    /**
     * The time when the digging should end.
     */
    private long diggingEndTime;
    /**
     * The amount of time the digging takes.
     */
    private long diggingDuration;
    /**
     * The last send break state.
     */
    private int lastBreakState = -1;

    public PlayerInteractionHandler(LanternPlayer player) {
        this.player = player;
    }

    /**
     * Resets the player interaction handler, is called when the
     * player gets teleported to a different world or is removed.
     */
    void reset() {
        this.sendBreakUpdate(-1);
    }

    /**
     * Pulses the interaction handler.
     */
    void pulse() {
        if (this.diggingBlock != null) {
            int breakState = (int) Math.round(((double) Math.max(0, this.diggingEndTime - System.nanoTime()) / (double) this.diggingDuration) * 10.0);
            if (this.lastBreakState != breakState) {
                this.sendBreakUpdate(breakState);
                this.lastBreakState = breakState;
            }
        }
    }

    /**
     * TODO: Maybe also send this to the player this handler is attached to for
     * custom break times? Only allowed by faster breaking.
     */
    private void sendBreakUpdate(int breakState) {
        LanternWorld world = this.player.getWorld();
        if (world == null) {
            return;
        }
        Set<LanternPlayer> players = this.player.getWorld().getRawPlayers();
        // Update for all the players except the breaker
        if (players.size() - 1 <= 0) {
            MessagePlayOutBlockBreakAnimation message = new MessagePlayOutBlockBreakAnimation(
                    this.diggingBlock, this.player.getEntityId(), breakState);
            players.forEach(player -> {
                if (player != this.player) {
                    player.getConnection().send(message);
                }
            });
        }
    }

    /**
     * Handles the {@link MessagePlayInPlayerDigging}.
     *
     * @param message The message
     */
    public void handleDigging(MessagePlayInPlayerDigging message) {
        MessagePlayInPlayerDigging.Action action = message.getAction();
        Vector3i blockPos = message.getPosition();

        if (action == MessagePlayInPlayerDigging.Action.START) {
            // Check if the block is within the players reach
            if (this.player.getPosition().distanceSquared(blockPos.toDouble().add(0.5, 2.0, 0.5)) > 6.0 * 6.0) {
                return;
            }
            if (this.diggingBlock != null) {
                Lantern.getLogger().warn("{} started breaking a block without finishing the last one.", this.player.getName());
            }

            BlockType blockType = this.player.getWorld().getBlockType(blockPos);
            if (blockType == BlockTypes.AIR) {
                return;
            }

            this.diggingBlock = blockPos;
            this.diggingBlockType = blockType;

            this.diggingDuration = this.getDiggingDuration(blockType);
            // The client won't send a finish message
            if (this.diggingDuration <= 0) {
                this.handleBrokenBlock();
            } else {
                this.diggingEndTime = System.nanoTime() + this.diggingDuration;
            }
        } else if (action == MessagePlayInPlayerDigging.Action.CANCEL) {
            if (this.diggingBlock == null || !this.diggingBlock.equals(blockPos)) {
                return;
            }

            if (this.lastBreakState != -1) {
                this.sendBreakUpdate(-1);
            }
            this.diggingBlock = null;
            this.diggingBlockType = null;
        } else {
            if (this.diggingBlock == null) {
                return;
            }
            BlockType blockType = this.player.getWorld().getBlockType(blockPos);
            if (blockType != this.diggingBlockType) {
                return;
            }
            long deltaTime = System.nanoTime() - this.diggingEndTime;
            if (deltaTime < 0) {
                Lantern.getLogger().warn("{} finished breaking a block too early, {}ms too fast.", this.player.getName(), -(deltaTime / 1000));
            }
            this.handleBrokenBlock();
        }
    }

    private void handleBrokenBlock() {
        this.player.getWorld().setBlock(this.diggingBlock, BlockTypes.AIR.getDefaultState());
        if (this.lastBreakState != -1) {
            this.sendBreakUpdate(-1);
        }
        this.diggingBlock = null;
        this.diggingBlockType = null;
    }

    /**
     * Gets the amount of time it should take to break a block.
     *
     * @return The digging duration
     */
    private long getDiggingDuration(BlockType blockType) {
        return 0;
    }

    public void handleBlockPlacing(MessagePlayInPlayerBlockPlacement message) {
        // The hand the action was excepted from
        // HandType expectedHand = message.getHand();
        // TODO: Send updates if something failed, events, ...

        ItemInteractionResult interactionResult = null;

        // Try the action of the hotbar item first
        LanternHotbar hotbar = this.player.getInventory().query(LanternHotbar.class).first();
        Optional<ItemStack> optItemStack = hotbar.getSelectedSlot().peek();
        if (optItemStack.isPresent()) {
            final LanternItemType itemType = (LanternItemType) optItemStack.get().getItem();
            interactionResult = itemType.onInteractWithItemAt(this.player, this.player.getWorld(), ItemInteractionType.MAIN,
                    optItemStack.get(), message.getPosition(), message.getFace(), message.getClickOffset());
            interactionResult.getResultItem().ifPresent(item -> hotbar.getSelectedSlot().set(item.createStack()));
        }

        if (interactionResult == null || interactionResult.getType().equals(ItemInteractionResult.Type.PASS)) {
            OffHandSlot offHandSlot = this.player.getInventory().query(OffHandSlot.class).first();
            optItemStack = offHandSlot.peek();
            if (optItemStack.isPresent()) {
                final LanternItemType itemType = (LanternItemType) optItemStack.get().getItem();
                interactionResult = itemType.onInteractWithItemAt(this.player, this.player.getWorld(), ItemInteractionType.OFF,
                        optItemStack.get(), message.getPosition(), message.getFace(), message.getClickOffset());
                interactionResult.getResultItem().ifPresent(item -> offHandSlot.set(item.createStack()));
            }
        }
    }
}
