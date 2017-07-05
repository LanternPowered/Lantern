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
