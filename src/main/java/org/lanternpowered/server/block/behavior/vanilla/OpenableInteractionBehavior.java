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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.key.Keys;
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
