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
import org.lanternpowered.launch.Environment;
import org.lanternpowered.launch.LanternClassLoader;
import org.lanternpowered.launch.transformer.Exclusion;
import org.lanternpowered.server.inject.LanternModule;
import org.lanternpowered.server.plugin.InternalPluginsInfo;
import org.lanternpowered.server.util.SyncLanternThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Platform;

import java.lang.reflect.Field;
import java.util.Arrays;

public final class LanternServerLaunch {

    public void main(String[] args) {
        final LanternClassLoader classLoader = LanternClassLoader.get();
        classLoader.addTransformerExclusion(Exclusion.forPackage("org.objectweb.asm")); // Exclude the ASM library
        classLoader.addTransformerExclusion(Exclusion.forPackage("org.lanternpowered.server.transformer"));
        classLoader.addTransformerExclusion(Exclusion.forClass("org.lanternpowered.server.util.BytecodeUtils"));
        classLoader.addTransformerExclusion(Exclusion.forClass("org.lanternpowered.server.util.UncheckedExceptions"));
        // TODO: Re-add the fast value container transformer, but with a more flexible and type-safe system

        final SyncLanternThread thread = new SyncLanternThread(() -> start(args), "init");
        thread.start();
    }

    private void start(String[] args) {
        // Get the default logger
        final Logger logger = LoggerFactory.getLogger(InternalPluginsInfo.Implementation.IDENTIFIER);
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
            logger.info("Instantiated the Injector in {} mode.", Environment.get().name().toLowerCase());

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
