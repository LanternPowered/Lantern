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
package org.lanternpowered.server.block.tile.vanilla;

import org.lanternpowered.server.block.action.vanilla.NoteAction;
import org.lanternpowered.server.block.property.InstrumentProperty;
import org.lanternpowered.server.block.tile.LanternTileEntity;
import org.lanternpowered.server.data.type.LanternInstrumentType;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.world.LanternWorld;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Note;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class LanternNote extends LanternTileEntity implements Note {

    @Override
    public void registerKeys() {
        super.registerKeys();
        getValueCollection().register(Keys.NOTE_PITCH, NotePitches.A1);
    }

    /**
     * Cycles the current {@link NotePitch} to the next one.
     */
    public void nextNote() {
        final NotePitch notePitch = get(Keys.NOTE_PITCH).get();
        offer(Keys.NOTE_PITCH, notePitch.cycleNext());
    }

    @Override
    public void playNote() {
        final Location<World> location = getLocation();
        final Location<World> downLocation = location.add(0, -1, 0);
        // Get the instrument type based on the underlying block
        final InstrumentType instrumentType = downLocation.getProperty(InstrumentProperty.class)
                .map(InstrumentProperty::getValue).orElse(InstrumentTypes.HARP);
        final NotePitch notePitch = get(Keys.NOTE_PITCH).get();
        // Trigger the note play effect
        ((LanternWorld) location.getExtent()).addBlockAction(location.getBlockPosition(),
                getBlock().getType(), new NoteAction(instrumentType, notePitch));
        // Calculate the pitch value based on the note pitch
        double pitch = (double) ((LanternNotePitch) notePitch).getInternalId();
        pitch = Math.pow(2.0, (pitch - 12.0) / 12.0);
        location.getExtent().playSound(((LanternInstrumentType) instrumentType).getSound(), SoundCategories.BLOCK,
                location.getPosition().add(0.5, 0.5, 0.5), 3.0, pitch);
    }

    @Override
    public BlockState getBlock() {
        final BlockState block = getLocation().getBlock();
        return block.getType() == BlockTypes.NOTEBLOCK ? block : BlockTypes.NOTEBLOCK.getDefaultState();
    }
}
