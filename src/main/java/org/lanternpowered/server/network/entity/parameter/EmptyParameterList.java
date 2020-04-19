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
package org.lanternpowered.server.network.entity.parameter;

public final class EmptyParameterList extends AbstractParameterList {

    public static EmptyParameterList INSTANCE = new EmptyParameterList();

    private EmptyParameterList() {
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public <T> void add(ParameterType<T> type, T value) {
    }
}
