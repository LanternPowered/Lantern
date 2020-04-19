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

public final class LiteralNode extends Node {

    private final String literal;

    public LiteralNode(List<Node> children, String literal,
            @Nullable Node redirect,
            @Nullable String command) {
        super(children, redirect, command);
        this.literal = literal;
    }

    public String getLiteral() {
        return this.literal;
    }
}
