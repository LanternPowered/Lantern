/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInResourcePackStatus;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.resourcepack.ResourcePack;

import java.util.Optional;

public final class HandlerPlayInResourcePackStatus implements Handler<MessagePlayInResourcePackStatus> {

    @Override
    public void handle(NetworkContext context, MessagePlayInResourcePackStatus message) {
        LanternResourcePackFactory factory = LanternGame.get().getRegistry().getResourcePackFactory();
        Optional<ResourcePack> resourcePack = factory.getByHash(message.getHash());
        if (!resourcePack.isPresent()) {
            resourcePack = factory.getById(message.getHash());
        }
        if (!resourcePack.isPresent()) {
            LanternGame.log().warn("Received unknown resource pack with hash or id: " + message.getHash() +
                    " and status: " + message.getStatus().toString().toLowerCase());
            return;
        }
        Session session = context.getSession();
        LanternGame.get().getEventManager().post(SpongeEventFactory.createResourcePackStatusEvent(
                Cause.source(session.getPlayer()).build(), resourcePack.get(), session.getPlayer(), message.getStatus()));
    }
}
