package org.lanternpowered.server.text.chat;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.text.chat.ChatType;

public final class LanternChatType extends SimpleLanternCatalogType implements ChatType {

    public LanternChatType(String identifier) {
        super(identifier);
    }
}
