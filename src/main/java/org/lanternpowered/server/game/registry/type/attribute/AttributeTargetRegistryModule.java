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

import org.lanternpowered.server.attribute.AttributeTargets;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.registry.RegistryModule;
import org.spongepowered.api.registry.util.RegisterCatalog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public final class AttributeTargetRegistryModule implements RegistryModule {

    @RegisterCatalog(AttributeTargets.class)
    private final Map<String, Predicate<DataHolder>> targets = new HashMap<>();

    @Override
    public void registerDefaults() {
        this.targets.put("generic", target -> target instanceof Living);
        this.targets.put("horse", target -> target instanceof Horse);
        this.targets.put("zombie", target -> target instanceof Zombie);
    }
}
