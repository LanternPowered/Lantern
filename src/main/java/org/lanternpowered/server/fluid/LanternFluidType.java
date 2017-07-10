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
package org.lanternpowered.server.fluid;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.lanternpowered.server.data.property.AbstractPropertyHolder;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.extra.fluid.FluidType;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public final class LanternFluidType extends PluginCatalogType.Base.Internal implements FluidType, AbstractPropertyHolder {

    @Nullable private final Supplier<BlockType> blockTypeSupplier;

    public LanternFluidType(String pluginId, String name, int internalId) {
        this(pluginId, name, internalId, null);
    }

    public LanternFluidType(String pluginId, String name, int internalId,
            @Nullable Supplier<BlockType> blockTypeSupplier) {
        super(pluginId, name, internalId);
        this.blockTypeSupplier = blockTypeSupplier;
    }

    @Override
    public Optional<BlockType> getBlockTypeBase() {
        return this.blockTypeSupplier == null ? Optional.empty() :
                Optional.of(this.blockTypeSupplier.get());
    }
}
