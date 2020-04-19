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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

public class OpenableInteractionBehavior implements InteractWithBlockBehavior {

    public static OpenCloseAccess DEFAULT_ACCESS = (context, action, original) -> {
        final Optional<String> optLockToken = context.requireContext(ContextKeys.BLOCK_LOCATION).get(Keys.LOCK_TOKEN);
        if (optLockToken.isPresent()) {
            final Optional<ItemStack> usedItem = context.getContext(ContextKeys.USED_ITEM_STACK);
            if (usedItem.isPresent()) {
                final ItemStack itemStack = usedItem.get();
                final Optional<String> optItemLockToken = itemStack.get(Keys.LOCK_TOKEN);
                if (optItemLockToken.isPresent()) {
                    return optItemLockToken.get().equals(optLockToken.get());
                }
                final Optional<Text> optName = itemStack.get(Keys.DISPLAY_NAME);
                if (optName.isPresent()) {
                    return LanternTexts.toLegacy(optName.get()).equals(optLockToken.get());
                }
            }
            return false;
        }
        return true;
    };

    private final OpenCloseAccess access;

    public OpenableInteractionBehavior(OpenCloseAccess access) {
        this.access = checkNotNull(access, "access");
    }

    public OpenableInteractionBehavior() {
        this.access = DEFAULT_ACCESS;
    }

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final Optional<Boolean> optIsOpen = location.get(Keys.OPEN);
        if (!optIsOpen.isPresent()) {
            return BehaviorResult.FAIL;
        }

        final boolean isOpen = optIsOpen.get();
        final boolean success = this.access.get(context, isOpen ? OpenCloseAccess.Action.CLOSE : OpenCloseAccess.Action.OPEN, DEFAULT_ACCESS);
        if (!success) {
            return BehaviorResult.PASS;
        }

        final BlockSnapshotBuilder builder = BlockSnapshotBuilder.create();
        builder.from(location);
        context.populateBlockSnapshot(builder, BehaviorContext.PopulationFlags.NOTIFIER);
        builder.add(Keys.OPEN, !isOpen);
        context.addBlockChange(builder.build());
        return BehaviorResult.SUCCESS;
    }

    @FunctionalInterface
    interface OpenCloseAccess {

        enum Action {
            OPEN,
            CLOSE,
        }

        /**
         * Checks whether the block can be opened or closed for the specific {@link BehaviorContext}.
         *
         * @param context The context that should be handled
         * @param action The action that should occur, when closed before, this will be open and closed if it was open
         * @return Whether the action may happen
         */
        boolean get(BehaviorContext context, Action action, @Nullable OpenCloseAccess original);
    }

}
