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

    public static final String IDENTIFIER_FORMAT = "Rcon[%s]";
    public static final Pattern IDENTIFIER_PATTERN = Pattern.compile('^' + String.format(IDENTIFIER_FORMAT, "(.+)") + '$');

    private final StringBuffer buffer = new StringBuffer();
    private final String identifier;

    private final InetSocketAddress address;
    private final InetSocketAddress virtualHost;

    // Whether the rcon source is authorized
    private volatile boolean authorized;

    LanternRconConnection(InetSocketAddress address, InetSocketAddress virtualHost) {
        this.identifier = String.format(IDENTIFIER_FORMAT, address.getHostName());
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
        return this.identifier;
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
