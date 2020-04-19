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
package org.lanternpowered.server.script.function.action;

import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.ActionType;
import org.lanternpowered.server.script.function.AbstractFunctionType;
import org.spongepowered.api.CatalogKey;

public class ActionTypeImpl extends AbstractFunctionType<Action> implements ActionType {

    public ActionTypeImpl(CatalogKey key, Class<? extends Action> type) {
        super(key, type);
    }
}
