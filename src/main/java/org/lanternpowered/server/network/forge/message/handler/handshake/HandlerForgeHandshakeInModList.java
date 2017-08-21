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
package org.lanternpowered.server.network.forge.message.handler.handshake;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import io.netty.util.Attribute;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.forge.ForgeProtocol;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.message.handler.Handler;

import java.util.HashMap;

public final class HandlerForgeHandshakeInModList implements Handler<MessageForgeHandshakeInOutModList> {

    @Override
    public void handle(NetworkContext context, MessageForgeHandshakeInOutModList message) {
        final NetworkSession session = context.getSession();
        final Attribute<ForgeServerHandshakePhase> phase = context.getChannel().attr(ForgeProtocol.HANDSHAKE_PHASE);
        if (phase.get() != ForgeServerHandshakePhase.HELLO) {
            session.disconnect(t("Retrieved unexpected forge handshake modList message."));
            return;
        }
        // We don't need to validate the mods for now, maybe in the future, just poke back
        session.getInstalledMods().addAll(message.getEntries().keySet());
        // Just use a empty map for now
        session.send(new MessageForgeHandshakeInOutModList(new HashMap<>()));
        phase.set(ForgeServerHandshakePhase.WAITING_ACK);
        Lantern.getLogger().debug("{}: Forge handshake -> Received modList message.", session.getGameProfile().getName().get());
    }
}
