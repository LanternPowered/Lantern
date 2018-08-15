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
package org.lanternpowered.server.text.serializer

import com.google.common.reflect.TypeToken
import ninja.leaping.configurate.ConfigurationNode
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.text.LanternTextTemplate
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import java.util.ArrayList

/**
 * Represents a [TypeSerializer] for [TextTemplate]s. TextTemplates
 * are serialized in two parts.
 *
 * First, the template's arguments as defined by
 * [TextTemplate.getArguments] are serialized to the "arguments" node.
 * This is where the argument definitions are kept.
 *
 * Second, the template's text representation as defined by
 * [TextTemplate.toText] is serialized to the "content" node.
 *
 * Deserialization is a bit more complicated. We start by loading the
 * "content" Text and check the root Text element as well as it's children. If
 * a [LiteralText] value is found that is wrapped in curly braces we
 * check to see if the value inside the braces is defined as an argument in the
 * "arguments" nodes. If so, we use the name and format from the original
 * LiteralText and obtain whether the argument is optional from the definition.
 * This is repeated until there are no more Text children to check and we
 * return a TextTemplate of the elements we have collected.
 */
class TextTemplateConfigSerializer : TypeSerializer<TextTemplate> {

    data class ParserData(
            val root : ConfigurationNode,
            val openArg : String,
            val closeArg : String
    )

    override fun deserialize(type: TypeToken<*>, value: ConfigurationNode): TextTemplate {
        val openArg = value.getNode(NODE_OPEN_ARG).getString(TextTemplate.DEFAULT_OPEN_ARG)
        val closeArg = value.getNode(NODE_CLOSE_ARG).getString(TextTemplate.DEFAULT_CLOSE_ARG)
        val parserData = ParserData(value, openArg, closeArg)
        val content = value.getNode(NODE_CONTENT).getValue(TOKEN_TEXT)
        val elements = ArrayList<Any>()
        parse(parserData, content!!, elements)
        return LanternTextTemplate.of(TextTemplate.DEFAULT_OPEN_ARG, TextTemplate.DEFAULT_CLOSE_ARG, elements.toList())
    }

    override fun serialize(type: TypeToken<*>, obj: TextTemplate?, value: ConfigurationNode) {
        if (obj == null) {
            return
        }
        value.getNode(NODE_OPTIONS, NODE_OPEN_ARG).value = obj.openArgString
        value.getNode(NODE_OPTIONS, NODE_CLOSE_ARG).value = obj.closeArgString
        value.getNode(NODE_ARGS).setValue(TOKEN_ARGS, obj.arguments)
        value.getNode(NODE_CONTENT).setValue(TOKEN_TEXT, obj.toText())
    }

    @Throws(ObjectMappingException::class)
    private fun parse(parserData: ParserData, content: Text, into: MutableList<Any>) {
        if (isArg(parserData, content)) {
            parseArg(parserData, content as LiteralText, into)
        } else {
            into.add(content.toBuilder().removeAll().build())
        }
        for (child in content.children) {
            parse(parserData, child, into)
        }
    }

    private fun parseArg(parserData: ParserData, source: LiteralText, into: MutableList<Any>) {
        val name = unwrap(parserData, source.content)
        val optional = parserData.root.getNode(NODE_ARGS, name, NODE_OPT).boolean
        val defaultValue = parserData.root.getNode(NODE_ARGS, name, NODE_DEF_VAL).getValue(TOKEN_TEXT)!!
        val format = source.format
        into.add(TextTemplate.arg(name).format(format).optional(optional).defaultValue(defaultValue).build())
    }

    private fun isArg(parserData: ParserData, element: Text): Boolean {
        if (element !is LiteralText) {
            return false
        }
        val literal = element.content
        return (literal.startsWith(parserData.openArg) && literal.endsWith(parserData.closeArg) &&
                isArgDefined(parserData, unwrap(parserData, literal)))
    }

    private fun unwrap(parserData: ParserData, str: String) = str.substring(parserData.openArg.length, str.length - parserData.closeArg.length)
    private fun isArgDefined(parserData: ParserData, argName: String) = !parserData.root.getNode(NODE_ARGS, argName).isVirtual

    companion object {

        private const val NODE_CONTENT = "content"

        private const val NODE_ARGS = "arguments"
        const val NODE_OPT = "optional"
        const val NODE_DEF_VAL = "defaultValue"

        private const val NODE_OPTIONS = "options"
        private const val NODE_OPEN_ARG = "openArg"
        private const val NODE_CLOSE_ARG = "closeArg"

        private val TOKEN_TEXT = typeTokenOf<Text>()
        private val TOKEN_ARGS = typeTokenOf<Map<String, TextTemplate.Arg>>()
    }
}
