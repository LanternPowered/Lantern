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
import org.lanternpowered.server.text.format.LanternTextStyle;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TextStyleRegistryModule implements CatalogRegistryModule<TextStyle.Base> {

    @RegisterCatalog(TextStyles.class)
    private final Map<String, TextStyle.Base> chatStyles = Maps.newHashMap();

    @EarlyRegistration
    @Override
    public void registerDefaults() {
        List<TextStyle.Base> types = Lists.newArrayList();
        types.add(new LanternTextStyle("bold", true, null, null, null, null));
        types.add(new LanternTextStyle("italic", null, true, null, null, null));
        types.add(new LanternTextStyle("underline", null, null, true, null, null));
        types.add(new LanternTextStyle("strikethrough", null, null, null, true, null));
        types.add(new LanternTextStyle("obfuscated", null, null, null, null, true));
        types.add(new LanternTextStyle("reset", false, false, false, false, false));
        types.forEach(type -> this.chatStyles.put(type.getId(), type));
    }

    @Override
    public Optional<TextStyle.Base> getById(String id) {
        return Optional.ofNullable(this.chatStyles.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<TextStyle.Base> getAll() {
        return ImmutableSet.copyOf(this.chatStyles.values());
    }

}
