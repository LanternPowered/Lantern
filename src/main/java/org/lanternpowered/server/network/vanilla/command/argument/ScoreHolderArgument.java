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

public class ScoreHolderArgument extends Argument {

    private final boolean multipleEntities;
    private final boolean unknownFlag;

    public ScoreHolderArgument(boolean multipleEntities, boolean unknownFlag) {
        this.multipleEntities = multipleEntities;
        this.unknownFlag = unknownFlag;
    }

    public boolean allowMultipleEntities() {
        return this.multipleEntities;
    }

    public boolean hasUnknownFlag() {
        return this.unknownFlag;
    }
}
