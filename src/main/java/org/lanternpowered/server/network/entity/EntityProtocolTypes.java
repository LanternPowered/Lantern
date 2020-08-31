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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public final class EntityProtocolTypes {

    public static final EntityProtocolType<LanternEntity> ARMOR_STAND = dummy("ARMOR_STAND");

    public static final EntityProtocolType<LanternEntity> BAT = dummy("BAT");

    public static final EntityProtocolType<LanternEntity> CHICKEN = dummy("CHICKEN");

    public static final EntityProtocolType<LanternEntity> ENDER_DRAGON = dummy("ENDER_DRAGON");

    public static final EntityProtocolType<LanternEntity> ENDERMITE = dummy("ENDERMITE");

    public static final EntityProtocolType<LanternEntity> EXPERIENCE_ORB = dummy("EXPERIENCE_ORB");

    public static final EntityProtocolType<LanternEntity> GIANT = dummy("EXPERIENCE_ORB");

    public static final EntityProtocolType<LanternEntity> HUMAN = dummy("HUMAN");

    public static final EntityProtocolType<LanternEntity> HUSK = dummy("HUSK");

    public static final EntityProtocolType<LanternEntity> IRON_GOLEM = dummy("IRON_GOLEM");

    public static final EntityProtocolType<LanternEntity> ITEM = dummy("ITEM");

    public static final EntityProtocolType<LanternEntity> LIGHTNING = dummy("LIGHTNING");

    public static final EntityProtocolType<LanternEntity> MAGMA_CUBE = dummy("MAGMA_CUBE");

    public static final EntityProtocolType<LanternEntity> PAINTING = dummy("PAINTING");

    public static final EntityProtocolType<LanternEntity> PIG = dummy("PIG");

    public static final EntityProtocolType<LanternPlayer> PLAYER = dummy("PLAYER");

    public static final EntityProtocolType<LanternEntity> RABBIT = dummy("RABBIT");

    public static final EntityProtocolType<LanternEntity> SHEEP = dummy("SHEEP");

    public static final EntityProtocolType<LanternEntity> SILVERFISH = dummy("SILVERFISH");

    public static final EntityProtocolType<LanternEntity> SLIME = dummy("SLIME");

    public static final EntityProtocolType<LanternEntity> SNOWMAN = dummy("SNOWMAN");

    public static final EntityProtocolType<LanternEntity> VILLAGER = dummy("VILLAGER");

    public static final EntityProtocolType<LanternEntity> ZOMBIE = dummy("ZOMBIE");

    public static final EntityProtocolType<LanternEntity> ZOMBIE_VILLAGER = dummy("ZOMBIE_VILLAGER");

    public static final EntityProtocolType<LanternEntity> HORSE = dummy("HORSE");

    public static final EntityProtocolType<LanternEntity> DONKEY = dummy("DONKEY");

    public static final EntityProtocolType<LanternEntity> LLAMA = dummy("LLAMA");

    private static <E extends LanternEntity> EntityProtocolType<E> dummy(String name) {
        //noinspection unchecked
        return DummyObjectProvider.createFor(EntityProtocolType.class, name);
    }

    private EntityProtocolTypes() {
    }
}
