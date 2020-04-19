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
package org.lanternpowered.server.item.behavior.simple;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public abstract class InteractWithBlockItemBaseBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location> optLocation = context.getContext(ContextKeys.INTERACTION_LOCATION);
        if (!optLocation.isPresent()) {
            return BehaviorResult.CONTINUE;
        }

        final Direction blockFace = context.getContext(ContextKeys.INTERACTION_FACE).get();
        Location location = optLocation.get();
        if (!location.getProperty(ReplaceableProperty.class).get().getValue()) {
            location = location.add(blockFace.asBlockOffset());
        }
        context.addContext(ContextKeys.BLOCK_LOCATION, location);

        final BehaviorContext.Snapshot snapshot = context.pushSnapshot();
        final boolean success = place(pipeline, context);

        if (success) {
            for (BlockSnapshot blockSnapshot : context.getBlockSnapshots()) {
                final Location location1 = blockSnapshot.getLocation().get();
                final int buildHeight = location1.getWorld().getDimension().getBuildHeight();
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

    protected abstract boolean place(BehaviorPipeline<Behavior> pipeline, BehaviorContext context);
}
