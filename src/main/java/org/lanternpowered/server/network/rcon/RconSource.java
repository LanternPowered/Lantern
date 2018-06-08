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

import org.lanternpowered.server.permission.AbstractProxySubject;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;
import java.util.regex.Pattern;

public final class RconSource extends AbstractProxySubject implements org.spongepowered.api.command.source.RconSource {

    public static final String NAME_FORMAT = "Rcon[%s]";
    public static final Pattern NAME_PATTERN = Pattern.compile('^' + String.format(NAME_FORMAT, "(.+)") + '$');

    private final StringBuffer buffer = new StringBuffer();
    private final RconConnection connection;
    private final String name;

    // Whether the rcon source is logged in
    private volatile boolean loggedIn;

    RconSource(RconConnection connection) {
        this.name = String.format(NAME_FORMAT, connection.getAddress().getHostName());
        this.connection = connection;
        initializeSubject();
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
        return MessageChannel.TO_ALL;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(this);
    }

    @Override
    public boolean getLoggedIn() {
        return this.loggedIn;
    }

    @Override
    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    @Override
    public RconConnection getConnection() {
        return this.connection;
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
