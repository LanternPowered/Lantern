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
package org.lanternpowered.server.game.registry.type.text;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.game.registry.EarlyRegistration;
import org.lanternpowered.server.text.format.LanternTextColor;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.Color;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TextColorRegistryModule implements CatalogRegistryModule<TextColor> {

    @RegisterCatalog(TextColors.class) private final Map<String, TextColor> chatColors = Maps.newHashMap();

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        List<TextColor> types = Lists.newArrayList();
        types.add(new LanternTextColor("black", Color.BLACK));
        types.add(new LanternTextColor("dark_blue", Color.ofRgb(0x0000AA)));
        types.add(new LanternTextColor("dark_green", Color.ofRgb(0x00AA00)));
        types.add(new LanternTextColor("dark_aqua", Color.ofRgb(0x00AAAA)));
        types.add(new LanternTextColor("dark_red", Color.ofRgb(0xAA0000)));
        types.add(new LanternTextColor("dark_purple", Color.ofRgb(0xAA00AA)));
        types.add(new LanternTextColor("gold", Color.ofRgb(0xFFAA00)));
        types.add(new LanternTextColor("gray", Color.ofRgb(0xAAAAAA)));
        types.add(new LanternTextColor("dark_gray", Color.ofRgb(0x555555)));
        types.add(new LanternTextColor("blue", Color.ofRgb(0x5555FF)));
        types.add(new LanternTextColor("green", Color.ofRgb(0x55FF55)));
        types.add(new LanternTextColor("aqua", Color.ofRgb(0x00FFFF)));
        types.add(new LanternTextColor("red", Color.ofRgb(0xFF5555)));
        types.add(new LanternTextColor("light_purple", Color.ofRgb(0xFF55FF)));
        types.add(new LanternTextColor("yellow", Color.ofRgb(0xFFFF55)));
        types.add(new LanternTextColor("white", Color.WHITE));
        types.add(new LanternTextColor("reset", Color.WHITE));
        types.add(TextColors.NONE);
        types.forEach(type -> this.chatColors.put(type.getId(), type));
        System.out.println("DEBUG B");
    }

    @Override
    public Optional<TextColor> getById(String id) {
        return Optional.ofNullable(this.chatColors.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<TextColor> getAll() {
        return ImmutableSet.copyOf(this.chatColors.values());
    }

}
