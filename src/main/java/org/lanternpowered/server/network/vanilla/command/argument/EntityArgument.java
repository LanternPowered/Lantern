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
package org.lanternpowered.server.network.vanilla.command.argument;

public class EntityArgument extends Argument {

    private final boolean multipleEntities;
    private final boolean onlyPlayers;

    public EntityArgument(boolean multipleEntities, boolean onlyPlayers) {
        this.multipleEntities = multipleEntities;
        this.onlyPlayers = onlyPlayers;
    }

    public boolean allowMultipleEntities() {
        return this.multipleEntities;
    }

    public boolean allowOnlyPlayers() {
        return this.onlyPlayers;
    }
}
