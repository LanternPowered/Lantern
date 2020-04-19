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
