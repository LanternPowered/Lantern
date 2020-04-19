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
package org.lanternpowered.server.network.vanilla.message.type.play;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.data.type.HandPreference;
import org.spongepowered.api.text.chat.ChatVisibility;

import java.util.Locale;

public final class MessagePlayInClientSettings implements Message {

    private final Locale locale;
    private final ChatVisibility chatVisibility;
    private final HandPreference dominantHand;
    private final int viewDistance;
    private final int skinPartsBitPattern;
    private final boolean enableColors;

    public MessagePlayInClientSettings(Locale locale, int viewDistance, ChatVisibility chatVisibility,
            HandPreference dominantHand, boolean enableColors, int skinPartsBitPattern) {
        this.dominantHand = dominantHand;
        this.skinPartsBitPattern = skinPartsBitPattern;
        this.chatVisibility = chatVisibility;
        this.viewDistance = viewDistance;
        this.enableColors = enableColors;
        this.locale = locale;
    }

    public Locale getLocale() {
        return this.locale;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public int getSkinPartsBitPattern() {
        return this.skinPartsBitPattern;
    }

    public ChatVisibility getChatVisibility() {
        return this.chatVisibility;
    }

    public boolean getEnableColors() {
        return this.enableColors;
    }

    public HandPreference getDominantHand() {
        return this.dominantHand;
    }
}
