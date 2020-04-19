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

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.spongepowered.api.CatalogType;

import java.util.function.Function;

public interface BlockEntityProtocolType<T extends LanternBlockEntity> extends CatalogType {

    Class<T> getBlockEntityType();

    Function<T, AbstractBlockEntityProtocol<T>> getSupplier();
}
