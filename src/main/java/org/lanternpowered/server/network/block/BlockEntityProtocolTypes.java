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
package org.lanternpowered.server.network.block;

import org.lanternpowered.server.block.entity.LanternBlockEntity;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

@SuppressWarnings("unchecked")
public final class BlockEntityProtocolTypes {

    public static final BlockEntityProtocolType<LanternBlockEntity> BANNER = dummy("BANNER");

    public static final BlockEntityProtocolType<LanternBlockEntity> DEFAULT = dummy("DEFAULT");

    public static final BlockEntityProtocolType<LanternBlockEntity> SIGN = dummy("SIGN");

    private static <E extends LanternBlockEntity> BlockEntityProtocolType<E> dummy(String name) {
        return DummyObjectProvider.createFor(BlockEntityProtocolType.class, name);
    }

    private BlockEntityProtocolTypes() {
    }
}
