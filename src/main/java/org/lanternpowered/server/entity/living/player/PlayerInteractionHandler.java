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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.entity.OffHandSlot;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.item.ItemInteractionResult;
import org.lanternpowered.server.item.ItemInteractionType;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.HardnessProperty;
import org.spongepowered.api.data.property.block.UnbreakableProperty;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

public final class PlayerInteractionHandler {

    private final LanternPlayer player;

    /**
     * The block that is being digged.
     */
    @Nullable private Vector3i diggingBlock;
    @Nullable private BlockType diggingBlockType;

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

    public void handleUseItem(MessagePlayInPlayerUseItem message) {

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
        final Set<LanternPlayer> players = this.player.getWorld().getRawPlayers();
        // Update for all the players except the breaker
        if (players.size() - 1 <= 0) {
            final MessagePlayOutBlockBreakAnimation message = new MessagePlayOutBlockBreakAnimation(
                    this.diggingBlock, this.player.getNetworkId(), breakState);
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
        final MessagePlayInPlayerDigging.Action action = message.getAction();
        final Vector3i blockPos = message.getPosition();

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

            this.diggingDuration = this.getDiggingDuration(blockPos, blockType);
            // The client won't send a finish message
            if (this.diggingDuration == 0) {
                this.handleBrokenBlock();
            } else {
                this.diggingEndTime = this.diggingDuration == -1 ? -1 : System.nanoTime() + this.diggingDuration;
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
            if (this.diggingEndTime == -1) {
                Lantern.getLogger().warn("{} attempted to break a unbreakable block.", this.player.getName());
            } else {
                long deltaTime = System.nanoTime() - this.diggingEndTime;
                if (deltaTime < 0) {
                    Lantern.getLogger().warn("{} finished breaking a block too early, {}ms too fast.",
                            this.player.getName(), -(deltaTime / 1000));
                }
                this.handleBrokenBlock();
            }
        }
    }

    private void handleBrokenBlock() {
        this.player.getWorld().setBlock(this.diggingBlock, BlockTypes.AIR.getDefaultState(),
                Cause.source(this.player).build());
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
    private long getDiggingDuration(Vector3i pos, BlockType blockType) {
        final World world = this.player.getWorld();
        final Optional<UnbreakableProperty> optUnbreakableProperty = world.getProperty(pos, UnbreakableProperty.class);
        if (optUnbreakableProperty.isPresent() && optUnbreakableProperty.get().getValue() == Boolean.TRUE) {
            return -1L;
        }
        final Optional<HardnessProperty> optHardnessProperty = world.getProperty(pos, HardnessProperty.class);
        if (optHardnessProperty.isPresent()) {
            final Double value = optHardnessProperty.get().getValue();
            double hardness = value == null ? 0 : value;
            // TODO: Calculate the duration
            return hardness <= 0 ? 0 : 1;
        }
        return 0;
    }

    public void handleBlockPlacing(MessagePlayInPlayerBlockPlacement message) {
        final ItemInteractionType itemInteractionType = message.getInteractionType();
        // Ignore the off hand interaction type for now, a main hand message
        // will always be send before this message. So we will only listen for
        // the main hand message.
        if (itemInteractionType == ItemInteractionType.OFF) {
            return;
        }

        // Try the action of the hotbar item first
        final LanternSlot hotbarSlot = this.player.getInventory().getHotbar().getSelectedSlot();
        final OffHandSlot offHandSlot = this.player.getInventory().getOffhand();

        // The offset can round up to 1, causing
        // an incorrect clicked block location
        final Vector3d pos2 = message.getClickOffset();
        final double dx = Math.min(pos2.getX(), 0.999);
        final double dy = Math.min(pos2.getY(), 0.999);
        final double dz = Math.min(pos2.getZ(), 0.999);

        final Location<World> clickedLocation = new Location<>(this.player.getWorld(),
                message.getPosition().toDouble().add(dx, dy, dz));
        final Direction face = message.getFace();

        ItemInteractionResult interactionResult;
        Optional<ItemStack> optItemStack;

        if (!this.player.get(Keys.IS_SNEAKING).orElse(false)) {
            final BlockState blockState = this.player.getWorld().getBlock(message.getPosition());
            optItemStack = hotbarSlot.peek();

            interactionResult = ((LanternBlockType) blockState.getType()).onInteractWithItemAt(this.player, optItemStack.orElse(null),
                    ItemInteractionType.MAIN, clickedLocation, face);
            interactionResult.getResultItem().ifPresent(item -> hotbarSlot.set(item.createStack()));
            if (!interactionResult.getType().equals(ItemInteractionResult.Type.PASS)) {
                return;
            }

            optItemStack = offHandSlot.peek();
            interactionResult = ((LanternBlockType) blockState.getType()).onInteractWithItemAt(this.player, optItemStack.orElse(null),
                    ItemInteractionType.OFF, clickedLocation, face);
            interactionResult.getResultItem().ifPresent(item -> offHandSlot.set(item.createStack()));
            if (!interactionResult.getType().equals(ItemInteractionResult.Type.PASS)) {
                return;
            }
        }

        optItemStack = hotbarSlot.peek();
        if (optItemStack.isPresent()) {
            final LanternItemType itemType = (LanternItemType) optItemStack.get().getItem();
            interactionResult = itemType.onInteractWithItemAt(this.player, optItemStack.get(),
                    ItemInteractionType.MAIN, clickedLocation, face);
            interactionResult.getResultItem().ifPresent(item -> hotbarSlot.set(item.createStack()));
            if (!interactionResult.getType().equals(ItemInteractionResult.Type.PASS)) {
                return;
            }
        }

        optItemStack = offHandSlot.peek();
        if (optItemStack.isPresent()) {
            final LanternItemType itemType = (LanternItemType) optItemStack.get().getItem();
            interactionResult = itemType.onInteractWithItemAt(this.player, optItemStack.get(),
                    ItemInteractionType.OFF, clickedLocation, face);
            interactionResult.getResultItem().ifPresent(item -> offHandSlot.set(item.createStack()));
        }
    }
}
