/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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
package org.lanternpowered.server.network.forge.message.type.handshake;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.message.Message;

public final class MessageForgeHandshakeInOutAck implements Message {

    private final ForgeHandshakePhase phase;

    public MessageForgeHandshakeInOutAck(ForgeClientHandshakePhase phase) {
        this((ForgeHandshakePhase) phase);
    }

    public MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase phase) {
        this((ForgeHandshakePhase) phase);
    }

    private MessageForgeHandshakeInOutAck(ForgeHandshakePhase phase) {
        this.phase = checkNotNull(phase, "phase");
    }

    public ForgeHandshakePhase getPhase() {
        return this.phase;
    }
}
