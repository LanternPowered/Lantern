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
package org.lanternpowered.server.data.type;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.data.type.ArmorType;
import org.spongepowered.api.item.ItemType;

import java.util.Optional;
import java.util.function.Supplier;

import javax.annotation.Nullable;

public class LanternArmorType extends PluginCatalogType.Base implements ArmorType {

    @Nullable private final Supplier<ItemType> repairItemType;

    public LanternArmorType(String pluginId, String name, Supplier<ItemType> repairItemType) {
        super(pluginId, name);
        this.repairItemType = checkNotNull(repairItemType, "repairItemType");
    }

    public LanternArmorType(String pluginId, String id, String name, Supplier<ItemType> repairItemType) {
        super(pluginId, id, name);
        this.repairItemType = checkNotNull(repairItemType, "repairItemType");
    }

    public LanternArmorType(String pluginId, String name) {
        super(pluginId, name);
        this.repairItemType = null;
    }

    public LanternArmorType(String pluginId, String id, String name) {
        super(pluginId, id, name);
        this.repairItemType = null;
    }

    @Override
    public Optional<ItemType> getRepairItemType() {
        return this.repairItemType == null ? Optional.empty() : Optional.of(this.repairItemType.get());
    }
}
