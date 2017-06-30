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
package org.lanternpowered.server.data.io.store.tile;

import org.lanternpowered.server.block.tile.vanilla.LanternNote;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.game.registry.type.data.NotePitchRegistryModule;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.NotePitches;

public class NoteblockTileEntitySerializer<T extends LanternNote> extends TileEntityObjectStore<T> {

    private static final DataQuery NOTE = DataQuery.of("note");

    @Override
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(object, valueContainer, dataView);
        dataView.getInt(NOTE).ifPresent(note -> valueContainer.set(Keys.NOTE_PITCH, NotePitchRegistryModule.get()
                .getByInternalId(note).orElse(NotePitches.F_SHARP0)));
    }

    @Override
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(object, valueContainer, dataView);
        valueContainer.remove(Keys.NOTE_PITCH).ifPresent(notePitch ->
                dataView.set(NOTE, (byte) ((LanternNotePitch) notePitch).getInternalId()));
    }
}
