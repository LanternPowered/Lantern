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
package org.lanternpowered.server.game.registry.type.attribute;

import org.lanternpowered.server.attribute.LanternOperation;
import org.lanternpowered.server.attribute.LanternOperations;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;

public final class AttributeOperationRegistryModule extends DefaultCatalogRegistryModule<LanternOperation> {

    public AttributeOperationRegistryModule() {
        super(LanternOperations.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternOperation(CatalogKey.minecraft("add_amount"), 3, false,
                (base, modifier, current) -> modifier));
        register(new LanternOperation(CatalogKey.minecraft("multiply"), 2, false,
                (base, modifier, current) -> current * modifier - current));
        register(new LanternOperation(CatalogKey.minecraft("multiply_base"), 1, false,
                (base, modifier, current) -> base * modifier - current));
    }
}
