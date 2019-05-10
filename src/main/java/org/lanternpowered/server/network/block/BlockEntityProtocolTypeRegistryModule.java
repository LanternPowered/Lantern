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
package org.lanternpowered.server.network.block;

import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.lanternpowered.server.network.block.vanilla.BannerBlockEntityProtocol;
import org.lanternpowered.server.network.block.vanilla.SignBlockEntityProtocol;
import org.spongepowered.api.CatalogKey;

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
        register(LanternBlockEntityProtocolType.of(CatalogKey.minecraft("banner"),
                tileEntity -> new BannerBlockEntityProtocol<>(tileEntity)));
        register(LanternBlockEntityProtocolType.of(CatalogKey.minecraft("default"),
                tileEntity -> new SimpleBlockEntityProtocol<>(tileEntity)));
        register(LanternBlockEntityProtocolType.of(CatalogKey.minecraft("sign"),
                tileEntity -> new SignBlockEntityProtocol<>(tileEntity)));
    }
}
