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

import com.google.common.collect.ImmutableList;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Node {

    @Nullable private final String command;
    @Nullable private final Node redirect;
    private final List<Node> children;

    public Node(List<Node> children,
            @Nullable Node redirect,
            @Nullable String command) {
        this.children = ImmutableList.copyOf(children);
        this.redirect = redirect;
        this.command = command;
    }

    @Nullable
    public String getCommand() {
        return this.command;
    }

    @Nullable
    public Node getRedirect() {
        return this.redirect;
    }

    public List<Node> getChildren() {
        return this.children;
    }
}
