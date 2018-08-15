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
