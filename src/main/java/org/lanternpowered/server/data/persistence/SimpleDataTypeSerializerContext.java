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
package org.lanternpowered.server.data.persistence;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleDataTypeSerializerContext implements DataTypeSerializerContext {

    private final DataTypeSerializerCollection serializers;

    public SimpleDataTypeSerializerContext(DataTypeSerializerCollection serializers) {
        this.serializers = checkNotNull(serializers, "serializers");
    }

    @Override
    public DataTypeSerializerCollection getSerializers() {
        return this.serializers;
    }
}
