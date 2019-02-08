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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.text.action.hover

import org.lanternpowered.api.text.Text
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.action.TextActions
import java.util.UUID

typealias HoverAction<R> = org.spongepowered.api.text.action.HoverAction<R>

typealias ShowItemHoverAction = org.spongepowered.api.text.action.HoverAction.ShowItem
typealias ShowTextHoverAction = org.spongepowered.api.text.action.HoverAction.ShowText
typealias ShowEntityHoverAction = org.spongepowered.api.text.action.HoverAction.ShowEntity
typealias ShowEntityRef = org.spongepowered.api.text.action.HoverAction.ShowEntity.Ref
typealias ShowEntityRefBuilder = org.spongepowered.api.text.action.HoverAction.ShowEntity.Ref.Builder

@JvmName("showItemOf")
inline fun ShowItemHoverAction(stack: ItemStackSnapshot) = TextActions.showItem(stack)

@JvmName("showTextOf")
inline fun ShowTextHoverAction(text: Text) = TextActions.showText(text)

@JvmName("showEntityOf")
inline fun ShowEntityHoverAction(entity: Entity, name: String) = TextActions.showEntity(entity, name)

@JvmName("showEntityOf")
inline fun ShowEntityHoverAction(entityRef: ShowEntityRef) = TextActions.showEntity(entityRef)

@JvmName("showEntityRefOf")
inline fun ShowEntityRef(uniqueId: UUID, name: String, type: EntityType<*>?) =
        org.spongepowered.api.text.action.HoverAction.ShowEntity.Ref.builder().uniqueId(uniqueId).name(name).type(type).build()
