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

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import org.lanternpowered.api.text.TextBuilder
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.optional.optional
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextElement
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.TextTemplateArgumentException
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextFormat
import org.spongepowered.api.text.format.TextStyle

data class LanternTextTemplate internal constructor(
        private val openArg: String,
        private val closeArg: String,
        private val elements: ImmutableList<Any>,
        private val arguments: ImmutableMap<String, TextTemplate.Arg>
) : TextTemplate {

    private val text: Text

    init {
        // Build text representation
        var builder: Text.Builder? = null
        this.elements.forEach { builder = apply(it, builder) }
        this.text = (builder ?: Text.builder()).build()
    }

    override fun getElements() = this.elements
    override fun getArguments() = this.arguments
    override fun getOpenArgString() = this.openArg
    override fun getCloseArgString() = this.closeArg

    override fun concat(other: TextTemplate): TextTemplate {
        val elements = this.elements.toMutableList()
        elements.addAll(other.elements)
        return of(this.openArg, this.closeArg, elements.toTypedArray().asIterable())
    }

    override fun apply(): Text.Builder = apply(emptyMap<String, Any>())
    override fun apply(params: Map<String, *>): Text.Builder = apply(null, params)

    private fun apply(builder: Text.Builder?, params: Map<String, *>): Text.Builder {
        var result = builder
        for (element in this.elements) {
            result = apply(element, result, params)
        }
        return result ?: Text.builder()
    }

    private fun apply(element: Any, builder: Text.Builder?, params: Map<String, *>): Text.Builder? {
        var theBuilder = builder
        // Note: The builder is initialized as null to avoid unnecessary Text nesting
        if (element is Arg) {
            val param = params[element.name]
            if (param == null) {
                element.checkOptional()
                if (element.defaultValue != null) {
                    theBuilder = applyArg(element.defaultValue, element, theBuilder)
                }
            } else {
                theBuilder = applyArg(param, element, theBuilder)
            }
        } else {
            theBuilder = apply(element, theBuilder)
        }
        return theBuilder
    }

    private fun apply(element: Any, builder: TextBuilder?): TextBuilder {
        var theBuilder = builder
        if (element is Text) {
            if (theBuilder == null) {
                theBuilder = element.toBuilder()
            } else {
                theBuilder.append(element)
            }
        } else if (element is TextElement) {
            if (theBuilder == null) {
                theBuilder = Text.builder()
            }
            element.applyTo(theBuilder!!)
        } else {
            val str = element.toString()
            if (theBuilder == null) {
                theBuilder = Text.builder(str)
            } else {
                theBuilder.append(Text.of(str))
            }
        }
        return theBuilder!!
    }

    private fun applyArg(param: Any, arg: Arg, builder: TextBuilder?): TextBuilder {
        val builder1 = builder ?: Text.builder()
        // wrap the parameter in the argument format
        val wrapper = Text.builder().format(arg.format)
        apply(param, wrapper)
        builder1.append(wrapper.build())
        return builder1
    }

    override fun toText() = this.text
    override fun iterator() = this.elements.iterator()

    @ConfigSerializable
    data class Arg internal constructor(
            internal val name: String,
            @field:Setting internal val optional: Boolean,
            @field:Setting internal val defaultValue: Text?,
            internal val format: TextFormat,
            private val openArg: String = TextTemplate.DEFAULT_OPEN_ARG,
            private val closeArg: String = TextTemplate.DEFAULT_CLOSE_ARG
    ) : TextTemplate.Arg {

        internal constructor(arg: Arg, openArg: String, closeArg: String) :
                this(arg.name, arg.optional, arg.defaultValue, arg.format, openArg, closeArg)

        internal fun checkOptional() {
            if (!this.optional) {
                throw TextTemplateArgumentException("Missing required argument in TextTemplate \"" + this.name + "\".")
            }
        }

        override fun getName() = this.name
        override fun isOptional() = this.optional
        override fun getDefaultValue() = this.defaultValue.optional()
        override fun getFormat() = this.format
        override fun getOpenArgString() = this.openArg
        override fun getCloseArgString() =  this.closeArg

        override fun toText(): Text = Text.builder(this.openArg + this.name + this.closeArg).format(this.format).build()

        override fun toString(): String {
            return ToStringHelper(this::class)
                    .omitNullValues()
                    .add("optional", this.optional)
                    .add("defaultValue", this.defaultValue)
                    .add("name", this.name)
                    .add("format", if (this.format.isEmpty) null else this.format)
                    .add("openArg", this.openArg)
                    .add("closeArg", this.closeArg)
                    .toString()
        }

        class Builder : TextTemplate.Arg.Builder {

            internal var name: String? = null
            internal var optional = false
            internal var defaultValue: Text? = null
            internal var format = TextFormat.of()

            override fun name(name: String) = apply { this.name = name }
            override fun optional(optional: Boolean) = apply { this.optional = optional }
            override fun defaultValue(defaultValue: Text) = apply { this.defaultValue = defaultValue }
            override fun format(format: TextFormat) = apply { this.format = format }
            override fun color(color: TextColor) = apply { this.format = this.format.color(color) }
            override fun style(style: TextStyle) = apply { this.format = this.format.style(style) }

            override fun build(): Arg {
                val name = checkNotNull(this.name) { "The name must be set" }
                return Arg(name, this.optional, this.defaultValue, this.format)
            }

            override fun toString(): String {
                return ToStringHelper(this)
                        .omitNullValues()
                        .add("name", this.name)
                        .add("optional", this.optional)
                        .add("defaultValue", this.defaultValue)
                        .add("format", if (this.format.isEmpty) null else this.format)
                        .toString()
            }

            override fun from(value: TextTemplate.Arg) = apply {
                value as Arg
                this.name = value.name
                this.optional = value.isOptional
                this.defaultValue = value.defaultValue
                this.format = value.format
            }

            override fun reset() = Builder()
        }
    }

    companion object {

        /**
         * Empty representation of a [TextTemplate]. This is returned if the
         * array supplied to [.of] is empty.
         */
        val EMPTY = LanternTextTemplate(TextTemplate.DEFAULT_OPEN_ARG, TextTemplate.DEFAULT_CLOSE_ARG, ImmutableList.of(), ImmutableMap.of())

        fun of(openArg: String, closeArg: String, elements: Iterable<Any>): LanternTextTemplate {
            // Collect elements
            val elementsBuilder = ImmutableList.builder<Any>()
            val argumentMap = mutableMapOf<String, Arg>()

            elements.forEach {
                var element = it
                if (element is Arg.Builder) {
                    element = element.build()
                }
                if (element is Arg) {
                    // check for non-equal duplicate argument
                    val newArg = Arg(element, openArg, closeArg)
                    val oldArg = argumentMap[newArg.name]
                    if (oldArg != null && oldArg != newArg) {
                        throw TextTemplateArgumentException(
                                "Tried to supply an unequal argument with a duplicate name \"${newArg.name}\" to TextTemplate.")
                    }
                    argumentMap[newArg.name] = newArg
                    element = newArg
                }
                elementsBuilder.add(element)
            }

            return LanternTextTemplate(openArg, closeArg, elementsBuilder.build(), ImmutableMap.copyOf(argumentMap))
        }
    }
}
