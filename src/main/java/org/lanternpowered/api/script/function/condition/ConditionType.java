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
package org.lanternpowered.api.script.function.condition;

import org.lanternpowered.api.script.function.FunctionType;
import org.spongepowered.api.util.annotation.CatalogedBy;

@CatalogedBy(ConditionTypes.class)
public interface ConditionType extends FunctionType<Condition> {

}
