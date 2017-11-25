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
package org.lanternpowered.server.item.behavior.simple;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.behavior.types.PlaceBlockBehavior;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class InteractWithBlockItemBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location<World>> optLocation = context.getContext(ContextKeys.INTERACTION_LOCATION);
        if (!optLocation.isPresent()) {
            return BehaviorResult.CONTINUE;
        }

        final Direction blockFace = context.getContext(ContextKeys.INTERACTION_FACE).get();
        Location<World> location = optLocation.get();
        if (!location.getProperty(ReplaceableProperty.class).get().getValue()) {
            location = location.add(blockFace.asBlockOffset());
        }
        context.addContext(ContextKeys.BLOCK_LOCATION, location);

        final LanternBlockType blockType = (LanternBlockType) context.getContext(ContextKeys.ITEM_TYPE).get().getBlock().get();
        context.addContext(ContextKeys.BLOCK_TYPE, blockType);

        final BehaviorContext.Snapshot snapshot = context.pushSnapshot();

        // Continue processing through the placement pipeline
        final boolean success = context.process(blockType.getPipeline().pipeline(PlaceBlockBehavior.class),
                (context1, behavior1) -> behavior1.tryPlace(pipeline, context1)).isSuccess();

        if (success) {
            for (BlockSnapshot blockSnapshot : context.getBlockSnapshots()) {
                final Location<World> location1 = blockSnapshot.getLocation().get();
                final int buildHeight = location1.getExtent().getDimension().getBuildHeight();
                // Check if the block is placed within the building limits
                if (location1.getBlockY() >= buildHeight) {
                    context.popSnapshot(snapshot);
                    context.getContext(ContextKeys.PLAYER).ifPresent(player ->
                            player.sendMessage(ChatTypes.ACTION_BAR, t("build.tooHigh", buildHeight)));
                    return BehaviorResult.FAIL;
                }
            }
            context.getContext(ContextKeys.PLAYER).ifPresent(player -> {
                if (!player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                    context.requireContext(ContextKeys.USED_SLOT).poll(1);
                }
            });
            return BehaviorResult.SUCCESS;
        }

        return BehaviorResult.PASS;
    }
}
