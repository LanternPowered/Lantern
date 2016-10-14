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
package org.lanternpowered.server.game.registry.type.attribute;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.lanternpowered.server.attribute.LanternOperation;
import org.lanternpowered.server.attribute.LanternOperations;
import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class AttributeOperationRegistryModule implements CatalogRegistryModule<LanternOperation> {

    @RegisterCatalog(LanternOperations.class) private final Map<String, LanternOperation> operations = Maps.newHashMap();

    @Override
    public void registerDefaults() {
        List<LanternOperation> types = Lists.newArrayList();
        types.add(new LanternOperation("add_amount", 3, false, (base, modifier, current) -> modifier));
        types.add(new LanternOperation("multiply", 2, false, (base, modifier, current) -> current * modifier - current));
        types.add(new LanternOperation("multiply_base", 1, false, (base, modifier, current) -> base * modifier - current));
        types.forEach(type -> this.operations.put(type.getId(), type));
    }

    @Override
    public Optional<LanternOperation> getById(String id) {
        return Optional.ofNullable(this.operations.get(checkNotNull(id).toLowerCase()));
    }

    @Override
    public Collection<LanternOperation> getAll() {
        return ImmutableList.copyOf(this.operations.values());
    }

}
