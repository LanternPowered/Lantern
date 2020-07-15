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
package org.lanternpowered.server.network.block;

import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.network.block.vanilla.BannerBlockEntityProtocol;
import org.lanternpowered.server.network.block.vanilla.SignBlockEntityProtocol;
import org.spongepowered.api.ResourceKey;

public class BlockEntityProtocolTypeRegistryModule extends DefaultCatalogRegistryModule<BlockEntityProtocolType> {

    public BlockEntityProtocolTypeRegistryModule() {
        super(BlockEntityProtocolTypes.class);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public void registerDefaults() {
        // Now you are probably thinking, use the method reference: ChickenEntityProtocol::new ??
        // well it's not working, at least not outside the development environment, java is throwing
        // "no such constructor" exceptions...
        // Tested with: oracle jre1.8.0_101
        register(LanternBlockEntityProtocolType.of(ResourceKey.minecraft("banner"),
                tileEntity -> new BannerBlockEntityProtocol<>(tileEntity)));
        register(LanternBlockEntityProtocolType.of(ResourceKey.minecraft("default"),
                tileEntity -> new SimpleBlockEntityProtocol<>(tileEntity)));
        register(LanternBlockEntityProtocolType.of(ResourceKey.minecraft("sign"),
                tileEntity -> new SignBlockEntityProtocol<>(tileEntity)));
    }
}
