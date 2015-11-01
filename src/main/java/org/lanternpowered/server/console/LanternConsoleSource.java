/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.console;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Iterator;
import java.util.Optional;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.permission.SubjectBase;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.sink.MessageSink;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.util.Tristate;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.source.ConsoleSource;

public final class LanternConsoleSource extends SubjectBase implements ConsoleSource {

    public static final ConsoleSource INSTANCE = new LanternConsoleSource();

    private MessageSink messageSink;

    private LanternConsoleSource() {
    }

    @Override
    public String getName() {
        return "Console";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendMessage(Text... messages) {
        for (Text message : messages) {
            if (message != null) {
                LanternGame.log().info(Texts.legacy().to(message));
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void sendMessage(Iterable<Text> messages) {
        Iterator<Text> it = messages.iterator();
        while (it.hasNext()) {
            Text message = it.next();
            if (message != null) {
                LanternGame.log().info(Texts.legacy().to(message));
            }
        }
    }

    @Override
    public String getIdentifier() {
        return this.getName();
    }

    @Override
    public Optional<CommandSource> getCommandSource() {
        return Optional.of(this);
    }

    @Override
    protected String getSubjectCollectionIdentifier() {
        return PermissionService.SUBJECTS_SYSTEM;
    }

    @Override
    protected Tristate getPermissionDefault(String permission) {
        checkNotNull(permission, "permission");
        return Tristate.TRUE;
    }

    @Override
    public MessageSink getMessageSink() {
        if (this.messageSink == null) {
            this.messageSink = MessageSinks.toAll();
        }
        return this.messageSink;
    }

    @Override
    public void setMessageSink(MessageSink sink) {
        this.messageSink = checkNotNull(sink, "sink");
    }
}
