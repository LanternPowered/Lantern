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
package org.lanternpowered.server.game.registry.type.text;

import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.lanternpowered.server.text.chat.LanternChatVisibility;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.chat.ChatVisibilities;
import org.spongepowered.api.text.chat.ChatVisibility;

public final class ChatVisibilityRegistryModule extends AdditionalInternalPluginCatalogRegistryModule<ChatVisibility> {

    private static final ChatVisibilityRegistryModule instance = new ChatVisibilityRegistryModule();

    public static ChatVisibilityRegistryModule get() {
        return instance;
    }

    private ChatVisibilityRegistryModule() {
        super(ChatVisibilities.class);
    }

    @Override
    protected boolean isDuplicateInternalIdAllowed() {
        return true;
    }

    @Override
    public void registerDefaults() {
        this.register(new LanternChatVisibility("minecraft", "full", 0,
                type -> true));
        this.register(new LanternChatVisibility("minecraft", "system", 1,
                type -> type == ChatTypes.SYSTEM || type == ChatTypes.ACTION_BAR));
        this.register(new LanternChatVisibility("minecraft", "hidden", 2,
                type -> false));
    }
}
