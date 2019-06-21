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
package org.lanternpowered.server.console

import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.config.plugins.Plugin
import org.apache.logging.log4j.core.pattern.ConverterKeys
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter

import java.util.regex.Matcher

/**
 * Cannot be converted to Kotlin, the static 'newInstance' method is required.
 */
@ConverterKeys("fullLocation", "loc")
@Plugin(name = "FullLocation", category = "Converter")
class FullLocationPatternConverter private constructor(private val format: String) :
        LogEventPatternConverter("FullLocation", "fullLocation") {

    override fun format(event: LogEvent, builder: StringBuilder) {
        val element = calculateLocation(event.loggerFqcn)
        if (element != null) {
            // quoteReplacement is required for elements leading to inner class (containing a $ character)
            builder.append(this.format.replace("%path".toRegex(), Matcher.quoteReplacement(element.toString())))
        }
    }

    companion object {

        // Packages that will be ignored
        private val ignoredPackages = arrayOf("java.", "kotlin.io.")

        @JvmStatic
        fun newInstance(options: Array<String>): FullLocationPatternConverter {
            return FullLocationPatternConverter(if (options.isNotEmpty()) options[0] else "%path")
        }

        private fun calculateLocation(fqcn: String): StackTraceElement? {
            val stackTrace = Throwable().stackTrace
            var last: StackTraceElement? = null

            for (i in stackTrace.size - 1 downTo 1) {
                val className = stackTrace[i].className
                // Check if the target logger source should be redirected
                if (LanternConsole.redirectFqcns.contains(className) || className == fqcn) {
                    return last
                }
                // Check if the target logger source should be ignored
                if (LanternConsole.ignoreFqcns.contains(className)) {
                    return null
                }
                // Reaching the printStackTrace method is also the end of the road
                if (className == "java.lang.Throwable" && stackTrace[i].methodName == "printStackTrace") {
                    return null
                }
                // Ignore Kotlin and Java packages
                var isIgnored = false
                for (ignored in ignoredPackages) {
                    if (className.startsWith(ignored)) {
                        isIgnored = true
                        break
                    }
                }
                if (!isIgnored) {
                    last = stackTrace[i]
                }
            }

            return null
        }
    }
}
