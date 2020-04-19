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
