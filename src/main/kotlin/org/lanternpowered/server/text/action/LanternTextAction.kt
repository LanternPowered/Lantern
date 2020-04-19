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
package org.lanternpowered.server.text.action

import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.command.CommandCause
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.EntityType
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextRepresentable
import org.spongepowered.api.text.action.ClickAction
import org.spongepowered.api.text.action.HoverAction
import org.spongepowered.api.text.action.ShiftClickAction
import org.spongepowered.api.text.action.TextAction
import org.spongepowered.api.util.CopyableBuilder
import java.net.URL
import java.util.UUID
import java.util.function.Consumer

abstract class LanternTextAction<R> internal constructor(): TextAction<R> {

    internal abstract val result: R

    override fun getResult() = this.result
}

abstract class LanternClickAction<R> internal constructor(): LanternTextAction<R>(), ClickAction<R>
abstract class LanternHoverAction<R> internal constructor(): LanternTextAction<R>(), HoverAction<R> {

    override fun toText(): Text {
        val result = this.result
        return if (result is TextRepresentable) result.toText() else Text.of(result.toString())
    }
}
abstract class LanternShiftClickAction<R> internal constructor(): LanternTextAction<R>(), ShiftClickAction<R>

// Click actions

data class OpenUrlClickAction(override val result: URL) : LanternClickAction<URL>(), ClickAction.OpenUrl
data class ChangePageClickAction(override val result: Int) : LanternClickAction<Int>(), ClickAction.ChangePage
data class ExecuteCallbackClickAction(override val result: Consumer<CommandCause>) :
        LanternClickAction<Consumer<CommandCause>>(), ClickAction.ExecuteCallback
data class RunCommandClickAction(override val result: String) : LanternClickAction<String>(), ClickAction.RunCommand
data class SuggestCommandClickAction(override val result: String) : LanternClickAction<String>(), ClickAction.SuggestCommand

// Hover actions

data class ShowItemHoverAction(override val result: ItemStackSnapshot) : LanternHoverAction<ItemStackSnapshot>(), HoverAction.ShowItem
data class ShowEntityHoverAction(override val result: HoverAction.ShowEntity.Ref) :
        LanternHoverAction<HoverAction.ShowEntity.Ref>(), HoverAction.ShowEntity
data class ShowTextHoverAction(override val result: Text) : LanternHoverAction<Text>(), HoverAction.ShowText

// Shift click actions

data class InsertTextShiftClickAction(override val result: String) : LanternShiftClickAction<String>(), ShiftClickAction.InsertText

// Builders

abstract class LanternTextActionBuilder<A : TextAction<R>, R : Any, B : CopyableBuilder<A, B>> internal constructor(): CopyableBuilder<A, B> {

    private var result: R? = null

    internal inline fun apply(fn: LanternTextActionBuilder<A, R, B>.() -> Unit): B {
        fn(this)
        return uncheckedCast()
    }

    fun result(result: R) = apply { this.result = result }

    override fun reset(): B = apply { this.result = null }
    override fun from(value: A) = apply { this.result = value.result }

    fun build() = build(checkNotNull(this.result) { "The result must be set" })

    abstract fun build(result: R): A
}

// Click action builders

class OpenUrlClickActionBuilder : LanternTextActionBuilder<
        ClickAction.OpenUrl, URL, ClickAction.OpenUrl.Builder>(), ClickAction.OpenUrl.Builder {
    override fun url(url: URL) = result(url)
    override fun build(result: URL) = OpenUrlClickAction(result)
}

class ChangePageClickActionBuilder : LanternTextActionBuilder<
        ClickAction.ChangePage, Int, ClickAction.ChangePage.Builder>(), ClickAction.ChangePage.Builder {
    override fun page(page: Int) = result(page)
    override fun build(result: Int) = ChangePageClickAction(result)
}

class ExecuteCallbackClickActionBuilder : LanternTextActionBuilder<
        ClickAction.ExecuteCallback, Consumer<CommandCause>, ClickAction.ExecuteCallback.Builder>(), ClickAction.ExecuteCallback.Builder {
    override fun callback(callback: Consumer<CommandCause>) = result(callback)
    override fun build(result: Consumer<CommandCause>) = ExecuteCallbackClickAction(result)
}

class RunCommandClickActionBuilder : LanternTextActionBuilder<
        ClickAction.RunCommand, String, ClickAction.RunCommand.Builder>(), ClickAction.RunCommand.Builder {
    override fun command(command: String) = result(command)
    override fun build(result: String) = RunCommandClickAction(result)
}

class SuggestCommandClickActionBuilder : LanternTextActionBuilder<
        ClickAction.SuggestCommand, String, ClickAction.SuggestCommand.Builder>(), ClickAction.SuggestCommand.Builder {
    override fun command(command: String) = result(command)
    override fun build(result: String) = SuggestCommandClickAction(result)
}

// Hover action builders

class ShowItemHoverActionBuilder : LanternTextActionBuilder<
        HoverAction.ShowItem, ItemStackSnapshot, HoverAction.ShowItem.Builder>(), HoverAction.ShowItem.Builder {
    override fun item(stack: ItemStackSnapshot) = result(stack)
    override fun build(result: ItemStackSnapshot) = ShowItemHoverAction(result)
}

class ShowEntityHoverActionBuilder : LanternTextActionBuilder<
        HoverAction.ShowEntity, HoverAction.ShowEntity.Ref, HoverAction.ShowEntity.Builder>(), HoverAction.ShowEntity.Builder {
    override fun entity(entity: Entity, name: String) = result(ShowEntityRef(entity.uniqueId, name, entity.type))
    override fun entity(ref: HoverAction.ShowEntity.Ref) = result(ref)
    override fun build(result: HoverAction.ShowEntity.Ref) = ShowEntityHoverAction(result)
}

class ShowTextHoverActionBuilder : LanternTextActionBuilder<
        HoverAction.ShowText, Text, HoverAction.ShowText.Builder>(), HoverAction.ShowText.Builder {
    override fun text(text: Text) = result(text)
    override fun build(result: Text) = ShowTextHoverAction(result)
}

// Shift click action builders

class InsertTextShiftClickActionBuilder : LanternTextActionBuilder<
        ShiftClickAction.InsertText, String, ShiftClickAction.InsertText.Builder>(), ShiftClickAction.InsertText.Builder {
    override fun text(text: String) = result(text)
    override fun build(result: String) = InsertTextShiftClickAction(result)
}

// Show entity ref

data class ShowEntityRef(
        internal val uniqueId: UUID,
        internal val name: String,
        internal val type: EntityType<*>?
) : HoverAction.ShowEntity.Ref {
    override fun getUniqueId() = this.uniqueId
    override fun getName() = this.name
    override fun getType() = this.type.optional()
}

class ShowEntityRefBuilder : HoverAction.ShowEntity.Ref.Builder {

    private var uniqueId: UUID? = null
    private var name: String? = null
    private var type: EntityType<*>? = null

    override fun from(value: HoverAction.ShowEntity.Ref) = apply {
        value as ShowEntityRef
        this.uniqueId = value.uniqueId
        this.name = value.name
        this.type = value.type
    }

    override fun reset() = apply {
        this.uniqueId = null
        this.name = null
        this.type = null
    }

    override fun uniqueId(uniqueId: UUID) = apply { this.uniqueId = uniqueId }
    override fun name(name: String) = apply { this.name = name }
    override fun type(type: EntityType<*>?) = apply { this.type = type }

    override fun build(): HoverAction.ShowEntity.Ref {
        val uniqueId = checkNotNull(this.uniqueId) { "The uniqueId must be set" }
        val name = checkNotNull(this.name) { "The name must be set" }
        return ShowEntityRef(uniqueId, name, this.type)
    }
}
