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
import org.lanternpowered.server.block.behavior.types.BreakBlockBehavior;
import org.lanternpowered.server.block.tile.vanilla.LanternChest;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.data.type.LanternChestAttachment;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ChestBreakBehavior implements BreakBlockBehavior {

    @Override
    public BehaviorResult tryBreak(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        Location<World> location = context.requireContext(ContextKeys.BLOCK_LOCATION);
        final BlockState state = location.getBlock();
        final Direction connectionDir = LanternChest.getConnectedDirection(state);
        if (connectionDir != Direction.NONE) {
            location = location.getBlockRelative(connectionDir);
            final BlockState relState = location.getBlock();
            if (relState.getType() == state.getType() &&
                    relState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get() ==
                    state.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get()) {
                final LanternChestAttachment relConnection = relState.getTraitValue(LanternEnumTraits.CHEST_ATTACHMENT).get();
                final LanternChestAttachment connection = state.getTraitValue(LanternEnumTraits.CHEST_ATTACHMENT).get();
                if ((relConnection == LanternChestAttachment.LEFT && connection == LanternChestAttachment.RIGHT) ||
                        (relConnection == LanternChestAttachment.RIGHT && connection == LanternChestAttachment.LEFT)) {
                    context.addBlockChange(BlockSnapshot.builder()
                            .from(location)
                            .add(Keys.CHEST_ATTACHMENT, LanternChestAttachment.SINGLE)
                            .build());
                }
            }
        }
        return BehaviorResult.CONTINUE;
    }
}
