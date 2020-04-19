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
package org.lanternpowered.server.network.vanilla.command;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class RootNode extends Node {

    public RootNode(List<Node> children,
            @Nullable String command,
            @Nullable Node redirect) {
        super(children, redirect, command);
    }
}
