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

import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.block.BlockState;

public final class BlockStateRegistryModule extends DefaultCatalogRegistryModule<BlockState> {

    public BlockStateRegistryModule() {
        super(new Class[0], "^[a-z][a-z0-9-_]+:[a-z][a-z0-9-_\\[\\]=,]+$");
    }

    void registerState(BlockState catalogType) {
        register(catalogType);
    }
}
