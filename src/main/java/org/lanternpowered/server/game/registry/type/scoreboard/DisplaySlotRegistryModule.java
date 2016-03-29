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
package org.lanternpowered.server.game.registry.type.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.lanternpowered.server.game.registry.type.text.TextColorRegistryModule;
import org.lanternpowered.server.game.registry.type.text.TextSerializersRegistryModule;
import org.lanternpowered.server.scoreboard.LanternDisplaySlot;
import org.lanternpowered.server.scoreboard.LanternObjectiveDisplayMode;
import org.lanternpowered.server.text.FormattingCodeTextSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayMode;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DisplaySlotRegistryModule implements CatalogRegistryModule<DisplaySlot> {

    @RegisterCatalog(DisplaySlots.class)
    private final Map<String, DisplaySlot> objectiveDisplayModes = Maps.newHashMap();

    private final TIntObjectMap<DisplaySlot> displaySlotByInternalIds = new TIntObjectHashMap<>();

    @Override
    public void registerDefaults() {
        Map<String, DisplaySlot> types = new HashMap<>();
        types.put("list", new LanternDisplaySlot("list", null, 0));
        types.put("sidebar", new LanternDisplaySlot("sidebar", null, 1));
        types.put("below_name", new LanternDisplaySlot("belowName", null, 2));
        for (TextColor textColor : Sponge.getRegistry().getAllOf(TextColor.class)) {
            // There is not mapping for "none"
            if (textColor == TextColors.NONE) {
                continue;
            }
            Character character = FormattingCodeTextSerializer.FORMATS.get(textColor);
            types.put("below_name_" + textColor.getId(), new LanternDisplaySlot("sidebar.team." + textColor.getId(), textColor, 3 + character));
        }
        types.entrySet().forEach(entry -> {
            this.objectiveDisplayModes.put(entry.getKey(), entry.getValue());
            this.objectiveDisplayModes.put(entry.getValue().getId(), entry.getValue());
            this.displaySlotByInternalIds.put(((LanternDisplaySlot) entry.getValue()).getInternalId(), entry.getValue());
        });
    }

    @Override
    public Optional<DisplaySlot> getById(String id) {
        return Optional.ofNullable(this.objectiveDisplayModes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<DisplaySlot> getAll() {
        return ImmutableSet.copyOf(this.objectiveDisplayModes.values());
    }

    public Optional<DisplaySlot> getByInternalId(int internalId) {
        return Optional.ofNullable(this.displaySlotByInternalIds.get(internalId));
    }

}
