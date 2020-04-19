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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.inventory.PlayerContainerSession;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.handler.Handler;

import java.util.function.BiConsumer;

public class HandlerPlayInContainerSessionForwarding<M extends Message> implements Handler<M> {

    private final BiConsumer<PlayerContainerSession, M> function;

    public HandlerPlayInContainerSessionForwarding(BiConsumer<PlayerContainerSession, M> function) {
        checkNotNull(function, "function");
        this.function = function;
    }

    @Override
    public void handle(NetworkContext context, M message) {
        this.function.accept(context.getSession().getPlayer().getContainerSession(), message);
    }
}
