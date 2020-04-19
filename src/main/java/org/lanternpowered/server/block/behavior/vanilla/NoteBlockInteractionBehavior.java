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

import org.lanternpowered.server.behavior.Behavior;
import org.lanternpowered.server.behavior.BehaviorContext;
import org.lanternpowered.server.behavior.BehaviorResult;
import org.lanternpowered.server.behavior.ContextKeys;
import org.lanternpowered.server.behavior.pipeline.BehaviorPipeline;
import org.lanternpowered.server.block.action.vanilla.PlayNoteAction;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.block.state.BlockStateProperties;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.InstrumentProperty;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;

public class NoteBlockInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        BlockState state = location.getBlock();
        final int notePitch = (state.getTraitValue(BlockStateProperties.NOTE).get() + 1) % 25;
        state = state.withTrait(BlockStateProperties.NOTE, notePitch).get();
        // Get the instrument type based on the underlying block
        // TODO: Use the following line once the note block state can be updated by surrounding changes
        //  final LanternInstrumentType instrumentType = state.getTraitValue(BlockStateProperties.INSTRUMENT).get();
        final InstrumentType instrumentType = location.getBlockRelative(Direction.DOWN).getProperty(InstrumentProperty.class)
                .map(InstrumentProperty::getValue).orElse(InstrumentTypes.HARP);
        // Trigger the note play effect
        ((LanternWorld) location.getWorld()).addBlockAction(
                location.getBlockPosition(), state.getType(), PlayNoteAction.INSTANCE);
        // Calculate the pitch value based on the note pitch
        final double pitch = Math.pow(2.0, ((double) notePitch - 12.0) / 12.0);
        location.getWorld().playSound(instrumentType.getSound(), SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 3.0, pitch);
        context.addBlockChange(BlockSnapshot.builder()
                .from(location)
                .blockState(state)
                .build());
        return BehaviorResult.SUCCESS;
    }
}
