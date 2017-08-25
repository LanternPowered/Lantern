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
package org.lanternpowered.server.game.registry.type.block;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.block.tile.LanternTileEntityType;
import org.lanternpowered.server.block.tile.vanilla.LanternChest;
import org.lanternpowered.server.block.tile.vanilla.LanternEnderChest;
import org.lanternpowered.server.block.tile.vanilla.LanternFurnace;
import org.lanternpowered.server.block.tile.vanilla.LanternJukebox;
import org.lanternpowered.server.block.tile.vanilla.LanternNote;
import org.lanternpowered.server.block.tile.vanilla.LanternShulkerBox;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryArchetypeRegistryModule;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ KeyRegistryModule.class, InventoryArchetypeRegistryModule.class })
public final class TileEntityTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<TileEntityType> {

    private static final TileEntityTypeRegistryModule INSTANCE = new TileEntityTypeRegistryModule();

    public static TileEntityTypeRegistryModule get() {
        return INSTANCE;
    }

    private final Map<Class<?>, TileEntityType> tileEntityTypesByClass = new HashMap<>();

    private TileEntityTypeRegistryModule() {
        super(TileEntityTypes.class);
    }

    @Override
    protected void register(TileEntityType catalogType, boolean disallowInbuiltPluginIds) {
        checkNotNull(catalogType, "catalogType");
        checkArgument(!this.tileEntityTypesByClass.containsKey(catalogType.getClass()),
                "There is already a TileEntityType registered for the class: %s", catalogType.getTileEntityType().getName());
        super.register(catalogType, disallowInbuiltPluginIds);
        this.tileEntityTypesByClass.put(catalogType.getTileEntityType(), catalogType);
    }

    public Optional<TileEntityType> getByClass(Class<? extends TileEntity> entityClass) {
        checkNotNull(entityClass, "entityClass");
        return Optional.ofNullable(this.tileEntityTypesByClass.get(entityClass));
    }

    @Override
    public void registerDefaults() {
        register(LanternTileEntityType.of("minecraft", "chest", LanternChest::new));
        register(LanternTileEntityType.of("minecraft", "ender_chest", LanternEnderChest::new));
        register(LanternTileEntityType.of("minecraft", "furnace", LanternFurnace::new));
        register(LanternTileEntityType.of("minecraft", "jukebox", LanternJukebox::new));
        register(LanternTileEntityType.of("minecraft", "noteblock", LanternNote::new));
        register(LanternTileEntityType.of("minecraft", "shulker_box", LanternShulkerBox::new));
    }

    @Override
    public Map<String, TileEntityType> provideCatalogMap() {
        final Map<String, TileEntityType> map = new HashMap<>(super.provideCatalogMap());
        // Because they had to give the mapping a different name
        map.putIfAbsent("note", getById("minecraft:noteblock").get());
        return map;
    }
}
