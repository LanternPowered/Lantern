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
package org.lanternpowered.server.item.behavior.vanilla;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.Parameter;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

public final class CheckBuildHeightInteractionBehavior implements InteractWithItemBehavior {

    public static final Parameter<BehaviorContext.Snapshot> SNAPSHOT =
            Parameter.of(BehaviorContext.Snapshot.class, "BuildHeightCheckResetSnapshot");

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<BehaviorContext.Snapshot> optSnapshot = context.get(SNAPSHOT);
        if (optSnapshot.isPresent()) {
            for (BlockSnapshot blockSnapshot : context.getBlockSnapshots()) {
                final Location<World> location1 = blockSnapshot.getLocation().get();
                final int buildHeight = location1.getExtent().getDimension().getBuildHeight();
                // Check if the block is placed within the building limits
                if (location1.getBlockY() >= buildHeight) {
                    context.restoreSnapshot(optSnapshot.get());
                    context.get(Parameters.PLAYER).ifPresent(player ->
                            player.sendMessage(ChatTypes.ACTION_BAR, t("build.tooHigh", buildHeight)));
                    return BehaviorResult.FAIL;
                }
            }
            return BehaviorResult.SUCCESS;
        }
        return BehaviorResult.PASS;
    }
}
