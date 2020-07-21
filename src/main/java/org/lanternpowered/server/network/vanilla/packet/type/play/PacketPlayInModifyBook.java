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
package org.lanternpowered.server.network.vanilla.packet.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.packet.Packet;
import org.spongepowered.api.data.type.HandType;

import java.util.List;

/**
 * A message send when a book is being modified.
 */
public abstract class PacketPlayInModifyBook implements Packet {

    private final HandType hand;
    private final List<String> pages;

    PacketPlayInModifyBook(HandType hand, List<String> pages) {
        this.hand = checkNotNull(hand, "hand");
        this.pages = checkNotNull(pages, "pages");
    }

    /**
     * Gets the pages of the book.
     *
     * @return The pages
     */
    public List<String> getPages() {
        return this.pages;
    }

    /**
     * Gets the {@link HandType} of which the book is being modified.
     *
     * @return The hand type
     */
    public HandType getHand() {
        return this.hand;
    }

    /**
     * Is send by the client when a book edit is being confirmed/saved.
     */
    public static final class Edit extends PacketPlayInModifyBook {

        public Edit(HandType handType, List<String> pages) {
            super(handType, pages);
        }
    }

    /**
     * Is send by the client when a book is being signed/finished editing.
     */
    public static final class Sign extends PacketPlayInModifyBook {

        private final String author;
        private final String title;

        public Sign(HandType handType, List<String> pages, String author, String title) {
            super(handType, pages);
            this.author = checkNotNull(author, "author");
            this.title = checkNotNull(title, "title");
        }

        /**
         * Gets the author name.
         *
         * @return The author
         */
        public String getAuthor() {
            return this.author;
        }

        /**
         * Gets the title of the book.
         *
         * @return The title
         */
        public String getTitle() {
            return this.title;
        }
    }
}
