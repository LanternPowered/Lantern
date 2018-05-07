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
package org.lanternpowered.server.game.registry.type.scoreboard;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.lanternpowered.server.scoreboard.LanternDisplaySlot;
import org.lanternpowered.server.text.FormattingCodeTextSerializer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DisplaySlotRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<DisplaySlot> {

    private final Map<TextColor, DisplaySlot> byTeamColors = new HashMap<>();

    public DisplaySlotRegistryModule() {
        super(DisplaySlots.class);
    }

    @Override
    protected void register(DisplaySlot catalogType, boolean disallowInbuiltPluginIds) {
        super.register(catalogType, disallowInbuiltPluginIds);
        catalogType.getTeamColor().ifPresent(color -> this.byTeamColors.putIfAbsent(color, catalogType));
    }

    @Override
    public void registerDefaults() {
        register(new LanternDisplaySlot("minecraft", "list", null, 0));
        register(new LanternDisplaySlot("minecraft", "sidebar", null, 1));
        register(new LanternDisplaySlot("minecraft", "below_name", "belowName", null, 2));
        for (TextColor textColor : Sponge.getRegistry().getAllOf(TextColor.class)) {
            // There is not mapping for "none"
            if (textColor == TextColors.NONE) {
                continue;
            }
            final char character = FormattingCodeTextSerializer.FORMATS_TO_CODE.getChar(textColor);
            final String id = "below_name_" + textColor.getId();
            final String name = "sidebar.team." + textColor.getId();
            register(new LanternDisplaySlot("minecraft", id, name, textColor, 3 + character));
        }
    }

    public Optional<DisplaySlot> getByTeamColor(TextColor teamColor) {
        checkNotNull(teamColor, "teamColor");
        return Optional.ofNullable(this.byTeamColors.get(teamColor));
    }
}
