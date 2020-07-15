/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
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
import org.lanternpowered.server.game.registry.type.item.inventory.InventoryArchetypeRegistryModule;
import org.lanternpowered.server.network.block.BlockEntityProtocolTypeRegistryModule;
import org.spongepowered.api.ResourceKey;
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
        register(LanternBlockEntityType.of(ResourceKey.minecraft("banner"), LanternBanner::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("chest"), LanternChest::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("ender_chest"), LanternEnderChest::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("furnace"), LanternFurnace::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("jukebox"), LanternJukebox::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("shulker_box"), LanternShulkerBox::new));
        register(LanternBlockEntityType.of(ResourceKey.minecraft("sign"), LanternSign::new));
    }
}
