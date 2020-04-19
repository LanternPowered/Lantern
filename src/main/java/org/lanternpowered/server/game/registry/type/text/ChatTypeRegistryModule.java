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
package org.lanternpowered.server.game.registry.type.text;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.text.chat.LanternChatType;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

public final class ChatTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<ChatType> {

    public ChatTypeRegistryModule() {
        super(ChatTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternChatType(CatalogKey.minecraft("chat"),
                text -> new MessagePlayOutChatMessage(text, MessagePlayOutChatMessage.Type.CHAT)));
        register(new LanternChatType(CatalogKey.minecraft("action_bar"), MessagePlayOutTitle.SetActionbarTitle::new));
        register(new LanternChatType(CatalogKey.minecraft("system"),
                text -> new MessagePlayOutChatMessage(text, MessagePlayOutChatMessage.Type.SYSTEM)));
    }
}
