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

import org.lanternpowered.server.game.registry.AdditionalInternalPluginCatalogRegistryModule;
import org.lanternpowered.server.text.chat.LanternChatVisibility;
import org.spongepowered.api.CatalogKey;
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
        register(new LanternChatVisibility(CatalogKey.minecraft("full"), 0,
                type -> true));
        register(new LanternChatVisibility(CatalogKey.minecraft("system"), 1,
                type -> type == ChatTypes.SYSTEM || type == ChatTypes.ACTION_BAR));
        register(new LanternChatVisibility(CatalogKey.minecraft("hidden"), 2,
                type -> false));
    }
}
