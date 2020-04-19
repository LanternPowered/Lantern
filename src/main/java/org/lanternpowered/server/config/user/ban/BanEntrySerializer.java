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
package org.lanternpowered.server.config.user.ban;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;

public final class BanEntrySerializer implements TypeSerializer<BanEntry> {

    @Override
    public BanEntry deserialize(TypeToken<?> type, ConfigurationNode value) throws ObjectMappingException {
        if (!value.getNode("ip").isVirtual()) {
            return value.getValue(TypeToken.of(BanEntry.Ip.class));
        } else {
            return value.getValue(TypeToken.of(BanEntry.Profile.class));
        }
    }

    @Override
    public void serialize(TypeToken<?> type, BanEntry obj, ConfigurationNode value) throws ObjectMappingException {
        if (obj instanceof BanEntry.Ip) {
            value.setValue(TypeToken.of(BanEntry.Ip.class), (BanEntry.Ip) obj);
        } else {
            value.setValue(TypeToken.of(BanEntry.Profile.class), (BanEntry.Profile) obj);
        }
    }

}
