/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.game.registry.type.data;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class NotePitchRegistryModule implements CatalogRegistryModule<NotePitch> {

    private static final String[] SORTED_NOTE_PITCHES = {
            "F_SHARP0",
            "G0",
            "G_SHARP0",
            "A1",
            "A_SHARP1",
            "B1",
            "C1",
            "C_SHARP1",
            "D1",
            "D_SHARP1",
            "E1",
            "F1",
            "F_SHARP1",
            "G1",
            "G_SHARP1",
            "A2",
            "A_SHARP2",
            "B2",
            "C2",
            "C_SHARP2",
            "D2",
            "D_SHARP2",
            "E2",
            "F2",
            "F_SHARP2",
    };

    @RegisterCatalog(NotePitches.class)
    private final Map<String, NotePitch> notePitches = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<LanternNotePitch> entries = Lists.newArrayList();
        for (String noteName : SORTED_NOTE_PITCHES) {
            LanternNotePitch notePitch = new LanternNotePitch(noteName, entries.size());
            this.notePitches.put(noteName.toLowerCase(), notePitch);
            entries.add(notePitch);
        }
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setNext(entries.get((i + 1) % entries.size()));
        }
    }

    @Override
    public Optional<NotePitch> getById(String id) {
        return Optional.ofNullable(this.notePitches.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<NotePitch> getAll() {
        return ImmutableSet.copyOf(this.notePitches.values());
    }

}
