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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.entity.LanternEntity;
import org.spongepowered.api.CatalogType;

import java.util.function.Function;

public interface EntityProtocolType<E extends LanternEntity> extends CatalogType {

    Class<E> getEntityType();

    Function<E, AbstractEntityProtocol<E>> getSupplier();
}
