/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import org.lanternpowered.server.data.type.LanternBrickType;
import org.spongepowered.api.data.type.BrickType;
import org.spongepowered.api.data.type.BrickTypes;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public final class BrickTypeRegistryModule implements CatalogRegistryModule<BrickType> {

    @RegisterCatalog(BrickTypes.class) private final Map<String, BrickType> brickTypes = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        for (LanternBrickType type : LanternBrickType.values()) {
            this.brickTypes.put(type.getId(), type);
            this.brickTypes.put(type.name().toLowerCase(), type);
        }
    }

    @Override
    public Optional<BrickType> getById(String id) {
        return Optional.ofNullable(this.brickTypes.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<BrickType> getAll() {
        return ImmutableList.copyOf(this.brickTypes.values());
    }

}
