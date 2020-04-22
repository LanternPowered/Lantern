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
package org.lanternpowered.api.util.collections

/**
 * Creates a checked list for the specified backing list, all the operations
 * should be done through the new list.
 *
 * @param checker The checker that should be used to validate the added values
 * @return The checked list
 */
fun <T> MutableList<T>.asCheckedList(checker: (T) -> Unit): MutableList<T> {
    return CheckedList(this, checker)
}

private class CheckedListIterator<T>(
        private val backing: MutableListIterator<T>,
        private val checker: (T) -> Unit
) : MutableListIterator<T> by backing {

    override fun set(element: T) {
        this.checker(element)
        this.backing.set(element)
    }

    override fun add(element: T) {
        this.checker(element)
        this.backing.add(element)
    }
}

private class CheckedList<T>(private val backing: MutableList<T>, private val checker: (T) -> Unit) : MutableList<T> by backing {

    override fun add(element: T): Boolean {
        this.checker(element)
        return this.backing.add(element)
    }

    override fun add(index: Int, element: T) {
        this.checker(element)
        return this.backing.add(index, element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach(this.checker)
        return this.backing.addAll(elements)
    }

    override fun addAll(index: Int, elements: Collection<T>): Boolean {
        elements.forEach(this.checker)
        return this.backing.addAll(index, elements)
    }

    override fun set(index: Int, element: T): T {
        this.checker(element)
        return this.backing.set(index, element)
    }

    override fun iterator(): MutableIterator<T> = listIterator()
    override fun listIterator(): MutableListIterator<T> = CheckedListIterator(this.backing.listIterator(), this.checker)
    override fun listIterator(index: Int): MutableListIterator<T> = CheckedListIterator(this.backing.listIterator(index), this.checker)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<T> = CheckedList(this.backing.subList(fromIndex, toIndex), this.checker)
}
