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

import org.lanternpowered.server.block.entity.LanternBlockEntityType;
import org.lanternpowered.server.block.entity.vanilla.LanternBanner;
import org.lanternpowered.server.block.entity.vanilla.LanternChest;
import org.lanternpowered.server.block.entity.vanilla.LanternEnderChest;
import org.lanternpowered.server.block.entity.vanilla.LanternFurnace;
import org.lanternpowered.server.block.entity.vanilla.LanternJukebox;
import org.lanternpowered.server.block.entity.vanilla.LanternShulkerBox;
import org.lanternpowered.server.block.entity.vanilla.LanternSign;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryArchetypeRegistryModule;
import org.lanternpowered.server.network.block.BlockEntityProtocolTypeRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.BlockEntityType;
import org.spongepowered.api.block.entity.BlockEntityTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RegistrationDependency({ KeyRegistryModule.class, InventoryArchetypeRegistryModule.class, BlockEntityProtocolTypeRegistryModule.class })
public final class BlockEntityTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<BlockEntityType> {

    private static final BlockEntityTypeRegistryModule INSTANCE = new BlockEntityTypeRegistryModule();

    public static BlockEntityTypeRegistryModule get() {
        return INSTANCE;
    }

    private final Map<Class<?>, BlockEntityType> blockEntityTypesByClass = new HashMap<>();

    private BlockEntityTypeRegistryModule() {
        super(BlockEntityTypes.class);
    }

    @Override
    protected void doRegistration(BlockEntityType catalogType, boolean disallowInbuiltPluginIds) {
        checkArgument(!this.blockEntityTypesByClass.containsKey(catalogType.getClass()),
                "There is already a BlockEntityType registered for the class: %s", catalogType.getBlockEntityType().getName());
        super.doRegistration(catalogType, disallowInbuiltPluginIds);
        this.blockEntityTypesByClass.put(catalogType.getBlockEntityType(), catalogType);
    }

    public Optional<BlockEntityType> getByClass(Class<? extends BlockEntity> entityClass) {
        checkNotNull(entityClass, "entityClass");
        return Optional.ofNullable(this.blockEntityTypesByClass.get(entityClass));
    }

    @Override
    public void registerDefaults() {
        register(LanternBlockEntityType.of(CatalogKey.minecraft("banner"), LanternBanner::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("chest"), LanternChest::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("ender_chest"), LanternEnderChest::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("furnace"), LanternFurnace::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("jukebox"), LanternJukebox::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("shulker_box"), LanternShulkerBox::new));
        register(LanternBlockEntityType.of(CatalogKey.minecraft("sign"), LanternSign::new));
    }
}
