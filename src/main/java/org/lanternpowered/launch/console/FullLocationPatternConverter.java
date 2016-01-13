/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.launch.console;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.pattern.ConverterKeys;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.util.PropertiesUtil;

@ConverterKeys({ "fqcn", "loc" })
@Plugin(name = "FQCN", category = "Converter")
public final class FullLocationPatternConverter extends LogEventPatternConverter {

    // Whether the full location should be appended, if false it will be ignored
    // event if it's specified in the configuration
    private static final boolean enabled = PropertiesUtil.getProperties().getBooleanProperty("log4j.full-location", true);

    // Packages that will be ignored
    private static final String[] ignoredPackages = { "java.", "kotlin.io." };

    private final String format;

    private FullLocationPatternConverter(String format) {
        super("FQCN", "fqcn");
        this.format = format;
    }

    public static FullLocationPatternConverter newInstance(String[] options) {
        return new FullLocationPatternConverter(options.length > 0 && options[0] != null ? options[0] : "%path");
    }

    @Override
    public void format(LogEvent event, StringBuilder builder) {
        if (!enabled) {
            return;
        }
        final String name = event.getLoggerName();
        final StackTraceElement element;
        if (ConsoleLaunch.isInitialized() && (ConsoleLaunch.REDIRECT_ERR.equals(name) || ConsoleLaunch.REDIRECT_OUT.equals(name))) {
            element = calculateLocation(ConsoleLaunch.REDIRECT_FQCN);
        } else {
            element = event.getSource();
        }
        if (element != null) {
            builder.append(this.format.replaceAll("%path", element.toString()));
        }
    }

    private static StackTraceElement calculateLocation(String fqcn) {
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        StackTraceElement last = null;

        for (int i = stackTrace.length - 1; i > 0; i--) {
            String className = stackTrace[i].getClassName();
            if (fqcn.equals(className)) {
                return last;
            }

            if (className.equals("java.lang.Throwable") && stackTrace[i].getMethodName().equals("printStackTrace")) {
                return null;
            }

            // Ignore Kotlin
            boolean isIgnored = false;
            for (String ignored : ignoredPackages) {
                if (className.startsWith(ignored)) {
                    isIgnored = true;
                    break;
                }
            }

            if (!isIgnored) {
                last = stackTrace[i];
            }
        }

        return null;
    }

}
