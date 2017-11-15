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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.data.type.HandType;

import java.util.List;

/**
 * A message send when a book is being modified.
 */
public abstract class MessagePlayInModifyBook implements Message {

    private final HandType hand;
    private final List<String> pages;

    MessagePlayInModifyBook(HandType hand, List<String> pages) {
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
    public static final class Edit extends MessagePlayInModifyBook {

        public Edit(HandType handType, List<String> pages) {
            super(handType, pages);
        }
    }

    /**
     * Is send by the client when a book is being signed/finished editing.
     */
    public static final class Sign extends MessagePlayInModifyBook {

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
