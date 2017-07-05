package org.lanternpowered.server.item.behavior.vanilla;

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.Parameters;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.BlockSnapshotBuilder;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
import org.lanternpowered.server.item.behavior.types.InteractWithItemBehavior;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class BedItemInteractionBehavior implements InteractWithItemBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Optional<Location<World>> optLocation = context.get(Parameters.INTERACTION_LOCATION);
        if (!optLocation.isPresent()) {
            return BehaviorResult.PASS;
        }

        final LanternPlayer player = (LanternPlayer) context.get(Parameters.PLAYER).orElse(null);

        // Get the direction the chest should face
        Direction facing;
        if (player != null) {
            facing = player.getHorizontalDirection(Direction.Division.CARDINAL);
        } else {
            facing = Direction.SOUTH;
        }

        final Direction blockFace = context.get(Parameters.INTERACTION_FACE).get();
        if (blockFace != Direction.DOWN) {
            return BehaviorResult.PASS;
        }
        final Location<World> footLoc = optLocation.get().add(0, 1, 0);
        final Location<World> headLoc = footLoc.getBlockRelative(facing);

        if (footLoc.getProperty(ReplaceableProperty.class).get().getValue() &&
                headLoc.getProperty(ReplaceableProperty.class).get().getValue()) {
            context.set(CheckBuildHeightInteractionBehavior.SNAPSHOT, context.createSnapshot());
            final BlockSnapshotBuilder snapshotBuilder = BlockSnapshotBuilder.create()
                    .blockState(BlockTypes.BED.getDefaultState()
                            .with(LanternKeys.BED_PART, LanternBedPart.FOOT).get()
                            .with(Keys.DIRECTION, facing).get())
                    .location(footLoc);
            context.addBlockChange(snapshotBuilder.build());
            snapshotBuilder
                    .blockState(BlockTypes.BED.getDefaultState()
                            .with(LanternKeys.BED_PART, LanternBedPart.HEAD).get()
                            .with(Keys.DIRECTION, facing).get())
                    .location(headLoc);
            context.addBlockChange(snapshotBuilder.build());
            if (player != null && !player.get(Keys.GAME_MODE).orElse(GameModes.NOT_SET).equals(GameModes.CREATIVE)) {
                context.tryGet(Parameters.USED_SLOT).poll(1);
            }
            return BehaviorResult.CONTINUE;
        }

        return BehaviorResult.PASS;
    }
}
