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
package org.lanternpowered.server.text

import org.spongepowered.api.text.Text
import java.util.NoSuchElementException

/**
 * Represents a recursive [Iterator] for [Text] including the text
 * itself as well as all children texts.
 */
internal class TextIterator(private val text: LanternText) : Iterator<Text> {

    private var children: Iterator<Text>? = null
    private var currentChildIterator: Iterator<Text>? = null

    override fun hasNext(): Boolean {
        val children = this.children
        val currentChildIterator = this.currentChildIterator
        return children == null || currentChildIterator != null && currentChildIterator.hasNext() || children.hasNext()
    }

    override fun next(): Text {
        if (!hasNext()) {
            throw NoSuchElementException()
        }
        val children = this.children
        if (children == null) {
            this.children = this.text.children.iterator()
            return this.text
        }
        var currentChildIterator = this.currentChildIterator
        if (currentChildIterator == null || !currentChildIterator.hasNext()) {
            currentChildIterator = children.next().withChildren().iterator()
            this.currentChildIterator = currentChildIterator
        }
        return currentChildIterator.next()
    }
}
