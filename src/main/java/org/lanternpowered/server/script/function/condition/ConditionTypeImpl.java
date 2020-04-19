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

import org.lanternpowered.api.script.function.condition.Condition;
import org.lanternpowered.api.script.function.condition.ConditionType;
import org.lanternpowered.server.script.function.AbstractFunctionType;
import org.spongepowered.api.CatalogKey;

public class ConditionTypeImpl extends AbstractFunctionType<Condition> implements ConditionType {

    public ConditionTypeImpl(CatalogKey key, Class<? extends Condition> type) {
        super(key, type);
    }
}
