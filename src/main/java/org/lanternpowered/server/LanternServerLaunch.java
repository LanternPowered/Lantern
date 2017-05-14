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
package org.lanternpowered.server;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import joptsimple.BuiltinHelpFormatter;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSpec;
import net.minecrell.terminalconsole.TerminalConsoleAppender;
import org.jline.terminal.Terminal;
import org.lanternpowered.server.inject.LanternModule;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Platform;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class LanternServerLaunch {

    public void main(String[] args) {
        // Get the default logger
        final Logger logger = LoggerFactory.getLogger(InternalPluginsInfo.Implementation.NAME);
        try {
            // Create the shared option parser
            final OptionParser optionParser = new OptionParser();
            optionParser.allowsUnrecognizedOptions();
            final OptionSpec<Void> version = optionParser.acceptsAll(Arrays.asList("version", "v"),
                    "Display the Lantern version");
            if (optionParser.parse(args).has(version)) {
                final Package pack = Platform.class.getPackage();
                logger.info(pack.getImplementationTitle() + ' ' + pack.getImplementationVersion());
                logger.info(pack.getSpecificationTitle() + ' ' + pack.getSpecificationVersion());
                return;
            }

            final OptionSpec<Void> help = optionParser.acceptsAll(Arrays.asList("help", "h", "?"),
                    "Show this help text").forHelp();

            // Initialize the injector
            final LanternModule module = new LanternModule(logger, args, optionParser);
            final Injector injector = Guice.createInjector(Stage.DEVELOPMENT, module);
            logger.info("Instantiated the Injector.");

            // Create the server instance
            final LanternServer lanternServer = injector.getInstance(LanternServer.class);
            // Initialize and start the server
            lanternServer.initialize();

            try {
                final Field field = OptionParser.class.getDeclaredField("allowsUnrecognizedOptions");
                field.setAccessible(true);
                field.set(optionParser, false);

                optionParser.parse(args);
            } catch (OptionException e) {
                logger.warn("Something went wrong while parsing options", e);
            } catch (Exception e) {
                logger.error("Unexpected error", e);
            }

            // First initialize, then parse help, so that the @Option
            // annotations will be detected
            if (optionParser.parse(args).has(help)) {
                if (System.console() != null) {
                    // Terminal is (very likely) supported, use the terminal width provided by jline
                    final Terminal terminal = TerminalConsoleAppender.getTerminal();
                    if (terminal != null) {
                        optionParser.formatHelpWith(new BuiltinHelpFormatter(terminal.getWidth(), 3));
                    }
                }
                optionParser.printHelpOn(System.err);
                return;
            }

            lanternServer.start();
        } catch (Throwable t) {
            logger.error("Error during server startup.", t);
            System.exit(1);
        }
    }
}
