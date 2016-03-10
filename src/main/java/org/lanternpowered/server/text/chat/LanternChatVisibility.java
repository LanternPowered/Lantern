/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
package org.lanternpowered.server.text.chat;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatVisibility;
import org.spongepowered.api.text.translation.Translation;

import java.util.function.Predicate;

public final class LanternChatVisibility extends SimpleLanternCatalogType implements ChatVisibility {

    private final static TIntObjectMap<LanternChatVisibility> lookup = new TIntObjectHashMap<>();

    private final Predicate<ChatType> chatTypePredicate;
    private final Translation translation;
    private final int internalId;

    public LanternChatVisibility(int internalId, String identifier, Predicate<ChatType> chatTypePredicate) {
        super(identifier);
        this.internalId = internalId;
        this.chatTypePredicate = chatTypePredicate;
        this.translation = Lantern.getGame().getRegistry().getTranslationManager().get(
                "options.chat.visibility." + identifier);
        lookup.put(internalId, this);
    }

    public int getInternalId() {
        return this.internalId;
    }

    @Override
    public boolean isVisible(ChatType chatType) {
        return this.chatTypePredicate.test(chatType);
    }

    @Override
    public Translation getTranslation() {
        return this.translation;
    }

    /**
     * Gets the {@link ChatVisibility} by using it's internal id.
     *
     * @param internalId the internal id
     * @return the chat visibility
     */
    public static LanternChatVisibility fromInternalId(int internalId) {
        return lookup.get(internalId);
    }

}
