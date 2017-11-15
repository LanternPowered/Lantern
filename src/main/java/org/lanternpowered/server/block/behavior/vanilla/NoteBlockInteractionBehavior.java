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
import org.lanternpowered.server.block.action.vanilla.PlayNoteAction;
import org.lanternpowered.server.block.behavior.types.InteractWithBlockBehavior;
import org.lanternpowered.server.block.trait.LanternIntegerTraits;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.property.block.InstrumentProperty;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class NoteBlockInteractionBehavior implements InteractWithBlockBehavior {

    @Override
    public BehaviorResult tryInteract(BehaviorPipeline<Behavior> pipeline, BehaviorContext context) {
        final Location<World> location = context.requireContext(ContextKeys.INTERACTION_LOCATION);
        BlockState state = location.getBlock();
        final int notePitch = (state.getTraitValue(LanternIntegerTraits.NOTE).get() + 1) % 25;
        state = state.withTrait(LanternIntegerTraits.NOTE, notePitch).get();
        // Get the instrument type based on the underlying block
        // TODO: Use the following line once the note block state can be updated by surrounding changes
        //  final LanternInstrumentType instrumentType = state.getTraitValue(LanternEnumTraits.INSTRUMENT).get();
        final InstrumentType instrumentType = location.getBlockRelative(Direction.DOWN).getProperty(InstrumentProperty.class)
                .map(InstrumentProperty::getValue).orElse(InstrumentTypes.HARP);
        // Trigger the note play effect
        ((LanternWorld) location.getExtent()).addBlockAction(
                location.getBlockPosition(), state.getType(), PlayNoteAction.INSTANCE);
        // Calculate the pitch value based on the note pitch
        final double pitch = Math.pow(2.0, ((double) notePitch - 12.0) / 12.0);
        location.getExtent().playSound(instrumentType.getSound(), SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 3.0, pitch);
        context.addBlockChange(BlockSnapshot.builder()
                .from(location)
                .blockState(state)
                .build());
        return BehaviorResult.SUCCESS;
    }
}
