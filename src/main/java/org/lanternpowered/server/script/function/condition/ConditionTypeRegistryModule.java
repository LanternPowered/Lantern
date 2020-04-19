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
package org.lanternpowered.server.script.function.condition;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.script.function.condition.AndCondition;
import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.ConditionType;
import org.lanternpowered.api.script.function.condition.ConditionTypes;
import org.lanternpowered.api.script.function.condition.OrCondition;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;

public class ConditionTypeRegistryModule extends AbstractObjectTypeRegistryModule<Condition, ConditionType> {

    private final static ConditionTypeRegistryModule INSTANCE = new ConditionTypeRegistryModule();

    public static ConditionTypeRegistryModule get() {
        return INSTANCE;
    }

    private ConditionTypeRegistryModule() {
        super(ConditionTypes.class);
    }

    @Override
    public void registerDefaults() {
        this.register(new ConditionTypeImpl(CatalogKeys.lantern("and"), AndCondition.class));
        this.register(new ConditionTypeImpl(CatalogKeys.lantern("or"), OrCondition.class));
    }
}
