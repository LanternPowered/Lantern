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
package org.lanternpowered.server.network.rcon;

import static com.google.common.base.MoreObjects.toStringHelper;

import org.lanternpowered.server.permission.AbstractProxySubject;
import org.spongepowered.api.network.RconConnection;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

public final class LanternRconConnection extends AbstractProxySubject implements RconConnection {

    public static final String NAME_FORMAT = "Rcon[%s]";
    public static final Pattern NAME_PATTERN = Pattern.compile('^' + String.format(NAME_FORMAT, "(.+)") + '$');

    private final StringBuffer buffer = new StringBuffer();
    private final String name;

    private final InetSocketAddress address;
    private final InetSocketAddress virtualHost;

    // Whether the rcon source is authorized
    private volatile boolean authorized;

    LanternRconConnection(InetSocketAddress address, InetSocketAddress virtualHost) {
        this.name = String.format(NAME_FORMAT, address.getHostName());
        this.virtualHost = virtualHost;
        this.address = address;
        initializeSubject();
    }

    @Override
    public InetSocketAddress getAddress() {
        return this.address;
    }

    @Override
    public InetSocketAddress getVirtualHost() {
        return this.virtualHost;
    }

    @Override
    public String toString() {
        return toStringHelper(this)
                .add("address", this.address)
                .add("virtualHost", this.virtualHost)
                .toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void sendMessage(Text message) {
        this.buffer.append(message.toPlain()).append('\n');
    }

    @Override
    public MessageChannel getMessageChannel() {
        return MessageChannel.toPlayersAndServer();
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public boolean isAuthorized() {
        return this.authorized;
    }

    @Override
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }

    @Override
    public String getSubjectCollectionIdentifier() {
        return PermissionService.SUBJECTS_SYSTEM;
    }

    @Override
    public Tristate getPermissionDefault(String permission) {
        return Tristate.TRUE;
    }

    public String flush() {
        final String result = this.buffer.toString();
        this.buffer.setLength(0);
        return result;
    }

}
