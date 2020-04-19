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
package org.lanternpowered.server.block.action.vanilla;

import org.lanternpowered.server.block.action.BlockAction;
import org.lanternpowered.server.block.action.BlockActionData;

public final class PlayNoteAction implements BlockAction {

    public static PlayNoteAction INSTANCE = new PlayNoteAction();

    private PlayNoteAction() {
    }

    @Override
    public void fill(BlockActionData actionData) {
    }

    @Override
    public Type type() {
        return Type.SINGLE;
    }
}
