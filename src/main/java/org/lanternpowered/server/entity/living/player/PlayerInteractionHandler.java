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
package org.lanternpowered.server.entity.living.player;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.behavior.BehaviorContextImpl;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.event.SwingHandEntityEvent;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.inventory.entity.OffHandSlot;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.item.LanternItemType;
import org.lanternpowered.server.item.behavior.types.FinishUsingItemBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.lanternpowered.server.item.property.DualWieldProperty;
import org.lanternpowered.server.item.property.MaximumUseDurationProperty;
import org.lanternpowered.server.item.property.MinimumUseDurationProperty;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutFinishUsingItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerBlockPlacement;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerDigging;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerSwingArm;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerUseItem;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutBlockBreakAnimation;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityAnimation;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.HardnessProperty;
import org.spongepowered.api.data.property.block.UnbreakableProperty;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Objects;
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

    private long lastInteractionTime = -1;

    @Nullable private HandType lastActiveHand;
    @Nullable private ItemStack lastActiveItemStack;

    private long activeHandStartTime = -1L;

    public PlayerInteractionHandler(LanternPlayer player) {
        this.player = player;
    }

    /**
     * Resets the player interaction handler, is called when the
     * player gets teleported to a different world or is removed.
     */
    void reset() {
        sendBreakUpdate(-1);
    }

    /**
     * Pulses the interaction handler.
     */
    void pulse() {
        if (this.diggingBlock != null) {
            final int breakState = (int) Math.round(((double) Math.max(0, this.diggingEndTime - System.nanoTime())
                    / (double) this.diggingDuration) * 10.0);
            if (this.lastBreakState != breakState) {
                sendBreakUpdate(breakState);
                this.lastBreakState = breakState;
            }
        }
        final HandType activeHand = this.player.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty()).orElse(null);
        final LanternSlot slot = activeHand == null ? null : activeHand == HandTypes.MAIN_HAND ?
                this.player.getInventory().getHotbar().getSelectedSlot() : this.player.getInventory().getOffhand();
        // The interaction just started
        if (!Objects.equals(activeHand, this.lastActiveHand)) {
            this.lastActiveHand = activeHand;
            this.lastActiveItemStack = slot == null ? null : slot.getRawItemStack();
        } else if (activeHand != null) {
            if (this.activeHandStartTime == -1L) {
                this.activeHandStartTime = LanternGame.currentTimeTicks();
            }
            //noinspection ConstantConditions
            final ItemStack itemStack = slot.getRawItemStack();
            if (itemStack == null || this.lastActiveItemStack != itemStack) {
                // Stop the interaction
                resetItemUseTime();
            } else {
                final MaximumUseDurationProperty property = itemStack.getProperty(MaximumUseDurationProperty.class).orElse(null);
                if (property != null) {
                    // Check if the interaction reached it's max time
                    final long time = LanternGame.currentTimeTicks();
                    //noinspection ConstantConditions
                    if (time - this.activeHandStartTime > property.getValue()) {
                        handleFinishItemInteraction0(slot, activeHand);
                    }
                }
            }
        }
    }

    /**
     * TODO: Maybe also send this to the player this handler is attached to for
     * custom break times? Only allowed by faster breaking.
     */
    private void sendBreakUpdate(int breakState) {
        final LanternWorld world = this.player.getWorld();
        //noinspection ConstantConditions
        if (world == null) {
            return;
        }
        final Set<LanternPlayer> players = this.player.getWorld().getRawPlayers();
        // Update for all the players except the breaker
        if (players.size() - 1 <= 0) {
            //noinspection ConstantConditions
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

            final BlockType blockType = this.player.getWorld().getBlockType(blockPos);
            if (blockType == BlockTypes.AIR) {
                return;
            }

            this.diggingBlock = blockPos;
            this.diggingBlockType = blockType;

            this.diggingDuration = getDiggingDuration(blockPos, blockType);
            // The client won't send a finish message
            if (this.diggingDuration == 0) {
                handleBrokenBlock();
            } else {
                this.diggingEndTime = this.diggingDuration == -1 ? -1 : System.nanoTime() + this.diggingDuration;
            }
        } else if (action == MessagePlayInPlayerDigging.Action.CANCEL) {
            if (this.diggingBlock == null || !this.diggingBlock.equals(blockPos)) {
                return;
            }

            if (this.lastBreakState != -1) {
                sendBreakUpdate(-1);
            }
            this.diggingBlock = null;
            this.diggingBlockType = null;
        } else {
            if (this.diggingBlock == null) {
                return;
            }
            final BlockType blockType = this.player.getWorld().getBlockType(blockPos);
            if (blockType != this.diggingBlockType) {
                return;
            }
            if (this.diggingEndTime == -1) {
                Lantern.getLogger().warn("{} attempted to break a unbreakable block.", this.player.getName());
            } else {
                final long deltaTime = System.nanoTime() - this.diggingEndTime;
                if (deltaTime < 0) {
                    Lantern.getLogger().warn("{} finished breaking a block too early, {}ms too fast.",
                            this.player.getName(), -(deltaTime / 1000));
                }
                handleBrokenBlock();
            }
        }
    }

    private void handleBrokenBlock() {
        //noinspection ConstantConditions
        final Location<World> location = new Location<>(this.player.getWorld(), this.diggingBlock);

        final BehaviorContextImpl context = new BehaviorContextImpl(Cause.source(this.player).build());
        context.set(Parameters.PLAYER, this.player);
        context.set(Parameters.INTERACTION_LOCATION, location);
        context.set(Parameters.BLOCK_LOCATION, location);

        final BlockState blockState = location.getBlock();
        final LanternBlockType blockType = (LanternBlockType) blockState.getType();
        if (context.process(blockType.getPipeline().pipeline(BreakBlockBehavior.class),
                (ctx, behavior) -> behavior.tryBreak(blockType.getPipeline(), ctx)).isSuccess()) {
            context.accept();

            this.diggingBlock = null;
            this.diggingBlockType = null;
        } else {
            // TODO: Resend tile entity data, action data, ... ???
            this.player.sendBlockChange(this.diggingBlock, blockState);
        }

        if (this.lastBreakState != -1) {
            sendBreakUpdate(-1);
        }
    }

    /**
     * Gets the amount of time it should take to break a block.
     *
     * @return The digging duration
     */
    private long getDiggingDuration(Vector3i pos, BlockType blockType) {
        if (this.player.get(Keys.GAME_MODE).get() == GameModes.CREATIVE) {
            return 0;
        }
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
        final HandType handType = message.getHandType();
        // Ignore the off hand interaction type for now, a main hand message
        // will always be send before this message. So we will only listen for
        // the main hand message.
        if (handType == HandTypes.OFF_HAND) {
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

        final BehaviorContextImpl context = new BehaviorContextImpl(Cause.source(this.player).build());
        context.set(Parameters.INTERACTION_FACE, face);
        context.set(Parameters.INTERACTION_LOCATION, clickedLocation);
        context.set(Parameters.PLAYER, this.player);

        BehaviorContextImpl.Snapshot snapshot = context.createSnapshot();

        if (!this.player.get(Keys.IS_SNEAKING).orElse(false)) {
            final BlockState blockState = this.player.getWorld().getBlock(message.getPosition());

            final LanternBlockType blockType = (LanternBlockType) blockState.getType();
            context.set(Parameters.BLOCK_TYPE, blockState.getType());
            context.set(Parameters.USED_BLOCK_STATE, blockState);

            final BehaviorContextImpl.Snapshot snapshot1 = context.createSnapshot();
            context.set(Parameters.USED_ITEM_STACK, hotbarSlot.peek().orElse(null));
            context.set(Parameters.USED_SLOT, hotbarSlot);
            context.set(Parameters.INTERACTION_HAND, HandTypes.MAIN_HAND);

            final BehaviorResult result = context.process(blockType.getPipeline().pipeline(InteractWithBlockBehavior.class),
                    (ctx, behavior) -> behavior.tryInteract(blockType.getPipeline(), ctx));
            if (result.isSuccess()) {
                snapshot = context.createSnapshot();
                // We can still continue, doing other operations
                if (result == BehaviorResult.CONTINUE && handleMainHandItemInteraction(context, snapshot)) {
                    return; // Is already accepted
                }
                context.accept();
                return;
            }

            context.restoreSnapshot(snapshot1);
            context.set(Parameters.USED_ITEM_STACK, offHandSlot.peek().orElse(null));
            context.set(Parameters.USED_SLOT, offHandSlot);
            context.set(Parameters.INTERACTION_HAND, HandTypes.OFF_HAND);

            if (result.isSuccess()) {
                snapshot = context.createSnapshot();
                // We can still continue, doing other operations
                if (result == BehaviorResult.CONTINUE && handleOffHandItemInteraction(context, snapshot)) {
                    return; // Is already accepted
                }
                context.accept();
                return;
            }

            context.restoreSnapshot(snapshot);
        }

        handleItemInteraction(context, snapshot);
    }

    public void handleSwingArm(MessagePlayInPlayerSwingArm message) {
        if (message.getHandType() == HandTypes.OFF_HAND) {
            return;
        }
        this.player.triggerEvent(SwingHandEntityEvent.of(HandTypes.MAIN_HAND));
    }

    public void handleFinishItemInteraction(MessagePlayInOutFinishUsingItem message) {
        final Optional<HandType> activeHand = this.player.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty());
        // The player is already interacting
        if (!activeHand.isPresent() || this.activeHandStartTime == -1L) {
            return;
        }

        // Try the action of the hotbar item first
        final LanternSlot slot = activeHand.get() == HandTypes.MAIN_HAND ?
                this.player.getInventory().getHotbar().getSelectedSlot() : this.player.getInventory().getOffhand();

        final ItemStack rawItemStack = slot.getRawItemStack();
        if (rawItemStack == null) {
            return;
        }

        // Require a minimum amount of ticks for the interaction to succeed
        final MinimumUseDurationProperty property = rawItemStack.getProperty(MinimumUseDurationProperty.class).orElse(null);
        if (property != null) {
            final long time = LanternGame.currentTimeTicks();
            //noinspection ConstantConditions
            if (time - this.activeHandStartTime < property.getValue()) {
                resetItemUseTime();
                return;
            }
        }

        handleFinishItemInteraction0(slot, activeHand.get());
    }

    private void handleFinishItemInteraction0(LanternSlot slot, HandType handType) {
        final BehaviorContextImpl context = new BehaviorContextImpl(Cause.source(this.player).build());
        context.set(Parameters.PLAYER, this.player);

        final Optional<ItemStack> handItem = slot.peek();
        if (handItem.isPresent()) {
            final LanternItemType itemType = (LanternItemType) handItem.get().getItem();
            context.set(Parameters.USED_ITEM_STACK, handItem.get());
            context.set(Parameters.USED_SLOT, slot);
            context.set(Parameters.INTERACTION_HAND, handType);
            context.set(Parameters.ITEM_TYPE, itemType);

            if (context.process(itemType.getPipeline().pipeline(FinishUsingItemBehavior.class),
                    (ctx, behavior) -> behavior.tryUse(itemType.getPipeline(), ctx)).isSuccess()) {
                context.accept();
            }
        }

        resetItemUseTime();
    }

    private void resetItemUseTime() {
        this.lastActiveItemStack = null;
        this.lastActiveHand = null;
        this.activeHandStartTime = -1L;
        this.player.offer(LanternKeys.ACTIVE_HAND, Optional.empty());
    }

    public void handleItemInteraction(MessagePlayInPlayerUseItem message) {
        // Prevent duplicate messages
        final long time = System.currentTimeMillis();
        if (this.lastInteractionTime != -1L && time - this.lastInteractionTime < 40) {
            return;
        }
        this.lastInteractionTime = time;

        final BehaviorContextImpl context = new BehaviorContextImpl(Cause.source(this.player).build());
        context.set(Parameters.PLAYER, this.player);

        final BehaviorContextImpl.Snapshot snapshot = context.createSnapshot();
        if (!handleItemInteraction(context, snapshot)) {
            if (!this.player.get(LanternKeys.CAN_DUAL_WIELD).orElse(false)) {
                return;
            }
            final OffHandSlot offHandSlot = this.player.getInventory().getOffhand();
            final Optional<ItemStack> handItem = offHandSlot.peek();

            if (handItem.isPresent()) {
                final DualWieldProperty property = handItem.get().getProperty(DualWieldProperty.class).orElse(null);
                //noinspection ConstantConditions
                if (property != null && property.getValue()) {
                    /*
                    final Vector3d position = this.player.getPosition().add(0, this.player.get(Keys.IS_SNEAKING).get() ? 1.54 : 1.62, 0);
                    final Optional<BlockRayHit<LanternWorld>> hit = BlockRay.from(this.player.getWorld(), position)
                            .direction(this.player.getDirectionVector())
                            .distanceLimit(5)
                            // Is this supposed to be inverted?
                            .skipFilter(Predicates.not(BlockRay.onlyAirFilter()))
                            .build()
                            .end();
                    if (hit.isPresent() && hit.get().getLocation().getBlock().getType() != BlockTypes.AIR) {
                        return;
                    }
                    */
                    this.player.getConnection().send(new MessagePlayOutEntityAnimation(this.player.getNetworkId(), 3));
                    this.player.triggerEvent(SwingHandEntityEvent.of(HandTypes.OFF_HAND));
                    /*
                    final CooldownTracker cooldownTracker = this.player.getCooldownTracker();
                    cooldownTracker.set(handItem.get().getItem(), 15);
                    */
                }
            }
        }
    }

    private boolean isInteracting() {
        return this.player.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty()).isPresent();
    }

    private boolean handleOffHandItemInteraction(BehaviorContextImpl context,
            @Nullable BehaviorContextImpl.Snapshot snapshot) {
        return handleHandItemInteraction(context, HandTypes.OFF_HAND,
                this.player.getInventory().getOffhand(), snapshot);
    }

    private boolean handleMainHandItemInteraction(BehaviorContextImpl context,
            @Nullable BehaviorContextImpl.Snapshot snapshot) {
        return handleHandItemInteraction(context, HandTypes.MAIN_HAND,
                this.player.getInventory().getHotbar().getSelectedSlot(), snapshot);
    }

    private boolean handleHandItemInteraction(BehaviorContextImpl context, HandType handType, LanternSlot slot,
            @Nullable BehaviorContextImpl.Snapshot snapshot) {
        final Optional<HandType> activeHand = this.player.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty());
        // The player is already interacting
        if (activeHand.isPresent()) {
            return true;
        }
        final Optional<ItemStack> handItem = slot.peek();
        if (handItem.isPresent()) {
            final LanternItemType itemType = (LanternItemType) handItem.get().getItem();
            context.set(Parameters.USED_ITEM_STACK, handItem.get());
            context.set(Parameters.USED_SLOT, slot);
            context.set(Parameters.INTERACTION_HAND, handType);
            context.set(Parameters.ITEM_TYPE, itemType);

            final BehaviorResult result = context.process(itemType.getPipeline().pipeline(InteractWithItemBehavior.class),
                    (ctx, behavior) -> behavior.tryInteract(itemType.getPipeline(), ctx));
            if (result.isSuccess()) {
                context.accept();
                return true;
            }
            if (snapshot != null) {
                context.restoreSnapshot(snapshot);
            }
        }
        return false;
    }

    private boolean handleItemInteraction(BehaviorContextImpl context, BehaviorContextImpl.Snapshot snapshot) {
        final Optional<HandType> activeHand = this.player.get(LanternKeys.ACTIVE_HAND).orElse(Optional.empty());
        return activeHand.isPresent() || handleMainHandItemInteraction(context, snapshot) || handleOffHandItemInteraction(context, null);
    }
}
