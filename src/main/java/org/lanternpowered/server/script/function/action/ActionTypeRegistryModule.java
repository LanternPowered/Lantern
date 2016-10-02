/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.script.function.action;

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
        super(null);
    }

    @Override
    public void registerDefaults() {
        this.register(new ActionTypeImpl("lantern", "multi", MultiAction.class));
        this.register(new ActionTypeImpl("lantern", "conditional", ConditionalAction.class));
        this.register(new ActionTypeImpl("lantern", "lightning_weather_spawner", LightningSpawnerAction.class));
    }
}
