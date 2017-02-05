/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.network.entity;

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.entity.living.player.LanternPlayer;
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

    private static <E extends LanternEntity> EntityProtocolType<E> dummy(String name) {
        //noinspection unchecked
        return DummyObjectProvider.createFor(EntityProtocolType.class, name);
    }

    private EntityProtocolTypes() {
    }
}
