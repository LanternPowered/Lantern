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
package org.lanternpowered.server.catalog

import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.util.ToString
import org.spongepowered.api.CatalogType
import org.spongepowered.api.NamedCatalogType

abstract class AbstractCatalogType : CatalogType, ToString {
    override fun toStringHelper(): ToStringHelper = applyCatalog(super.toStringHelper())
    override fun toString(): String = toStringHelper().toString()
}

private fun CatalogType.applyCatalog(helper: ToStringHelper): ToStringHelper = helper.apply {
    if (this@applyCatalog is InternalCatalogType) {
        helper.addFirst("internalId", internalId)
    }
    if (this@applyCatalog is NamedCatalogType) {
        helper.addFirst("name", name)
    }
    helper.addFirst("id", key)
}

fun CatalogType.asString(): String {
    val helper: ToStringHelper = if (this is ToString) {
        toStringHelper()
    } else {
        ToStringHelper(this)
    }
    return applyCatalog(helper).toString()
}
