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

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.action.ActionType;
import org.lanternpowered.api.script.function.action.ConditionalAction;
import org.lanternpowered.api.script.function.action.MultiAction;
import org.lanternpowered.server.script.AbstractObjectTypeRegistryModule;
import org.lanternpowered.server.world.weather.action.LightningSpawnerAction;

public class ActionTypeRegistryModule extends AbstractObjectTypeRegistryModule<Action, ActionType> {

    private final static ActionTypeRegistryModule INSTANCE = new ActionTypeRegistryModule();

    public static ActionTypeRegistryModule get() {
        return INSTANCE;
    }

    private ActionTypeRegistryModule() {
    }

    @Override
    public void registerDefaults() {
        register(new ActionTypeImpl(CatalogKeys.lantern("multi"), MultiAction.class));
        register(new ActionTypeImpl(CatalogKeys.lantern("conditional"), ConditionalAction.class));
        register(new ActionTypeImpl(CatalogKeys.lantern("lightning_weather_spawner"), LightningSpawnerAction.class));
    }
}
