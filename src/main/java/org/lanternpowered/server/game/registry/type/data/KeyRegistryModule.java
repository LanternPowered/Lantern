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

import static org.spongepowered.api.data.DataQuery.of;
import static org.spongepowered.api.data.key.KeyFactory.makeSingleKey;

import com.google.common.collect.ImmutableMap;
import org.lanternpowered.server.game.registry.CatalogMappingData;
import org.lanternpowered.server.game.registry.CatalogMappingDataHolder;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DirtType;
import org.spongepowered.api.data.type.StoneType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.registry.RegistryModule;

import java.util.Collections;
import java.util.List;

public final class KeyRegistryModule implements RegistryModule, CatalogMappingDataHolder {

    @Override
    public List<CatalogMappingData> getCatalogMappings() {
        return Collections.singletonList(new CatalogMappingData(Keys.class, ImmutableMap.<String, Object>builder()
                .put("dirt_type", makeSingleKey(DirtType.class, Value.class, of("DirtType")))
                .put("snowed", makeSingleKey(Boolean.class, Value.class, of("Snowed")))
                .put("stone_type", makeSingleKey(StoneType.class, Value.class, of("StoneType")))
                .put("tree_type", makeSingleKey(TreeType.class, Value.class, of("TreeType")))
                .build()));
    }
}
