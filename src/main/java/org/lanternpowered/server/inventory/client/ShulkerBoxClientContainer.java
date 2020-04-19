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
package org.lanternpowered.server.inventory.client;

import static org.lanternpowered.server.text.translation.TranslationHelper.t;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenWindow;
import org.spongepowered.api.text.Text;

public class ShulkerBoxClientContainer extends ChestClientContainer {

    static class Title {
        static final Text DEFAULT = t("container.shulkerBox");
    }

    public ShulkerBoxClientContainer() {
        super(Title.DEFAULT, 3);
    }

    @Override
    protected Message createInitMessage() {
        return new MessagePlayOutOpenWindow(getContainerId(), ClientWindowTypes.SHULKER_BOX, getTitle());
    }
}
