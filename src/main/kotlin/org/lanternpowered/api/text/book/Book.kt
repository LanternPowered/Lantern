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
package org.lanternpowered.api.text.book

import org.lanternpowered.api.text.Text

typealias Book = net.kyori.adventure.inventory.Book

/**
 * Constructs a new book with the given [title], [author] and [pages].
 */
fun bookOf(title: Text, author: Text, pages: Collection<Text>): Book =
        Book.book(title, author, pages)
