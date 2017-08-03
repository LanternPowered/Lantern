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
package org.lanternpowered.server.console;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.permission.AbstractProxySubject;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextElement;
import org.spongepowered.api.text.TextTemplate;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.util.Tristate;

import java.util.Map;
import java.util.Optional;

public final class LanternConsoleSource extends AbstractProxySubject implements ConsoleSource {

    public static final String NAME = "Server";
    public static final ConsoleSource INSTANCE = new LanternConsoleSource();

    /**
     * The message channel of the console source.
     */
    private MessageChannel messageChannel = MessageChannel.TO_ALL;

    private LanternConsoleSource() {
        initializeSubject();
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public void sendMessage(Text message) {
        System.out.println(LanternTexts.toLegacy(message));
    }

    @Override
    public void sendMessages(Text... messages) {
        ConsoleSource.super.sendMessages(messages);
    }

    @Override
    public void sendMessages(Iterable<Text> messages) {
        ConsoleSource.super.sendMessages(messages);
    }

    @Override
    public void sendMessage(TextTemplate template) {
        ConsoleSource.super.sendMessage(template);
    }

    @Override
    public void sendMessage(TextTemplate template, Map<String, TextElement> params) {
        ConsoleSource.super.sendMessage(template, params);
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
    public String getSubjectCollectionIdentifier() {
        return PermissionService.SUBJECTS_SYSTEM;
    }

    @Override
    public Tristate getPermissionDefault(String permission) {
        checkNotNull(permission, "permission");
        return Tristate.TRUE;
    }

    @Override
    public MessageChannel getMessageChannel() {
        return this.messageChannel;
    }

    @Override
    public void setMessageChannel(MessageChannel channel) {
        this.messageChannel = checkNotNull(channel, "channel");
    }

}
