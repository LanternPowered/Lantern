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
package org.lanternpowered.testserver.plugin;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

@Plugin(id = "paginationtest", name = "PaginationTest", description = "A plugin to test the pagination service.")
public class PaginationTestPlugin {

    @Inject
    private Logger logger;

    private PaginationList paginationList;

    @Listener
    public void onGamePreInitialization(final GamePreInitializationEvent event) {
        final Optional<PaginationService> paginationService = Sponge.getServiceManager().provide(PaginationService.class);
        if (paginationService.isPresent()) {
            // Defaults to normal amount of lines per page to guarantee it is of appropriate size
            paginationList = paginationService.get().builder()
                    .title(Text.of(TextColors.RED, "This Is A Test"))
                    .padding(Text.of(TextColors.GOLD, "="))
                    .header(Text.of("This is the header"))
                    .footer(Text.of("This is the footer"))
                    .contents(ImmutableList.of(
                            Text.of(TextColors.GRAY, "rhomboid"),
                            Text.of(TextColors.GRAY, "analytic"),
                            Text.of(TextColors.GRAY, "sandwich"),
                            Text.of(TextColors.GRAY, "wallpaper"),
                            Text.of(TextColors.GRAY, "fragmentation"),
                            Text.of(TextColors.GRAY, "elephant"),
                            Text.of(TextColors.GRAY, "idempotence"),
                            Text.of(TextColors.GRAY, "finger"),
                            Text.of(TextColors.GRAY, "licking"),
                            Text.of(TextColors.GRAY, "netherborn"),
                            Text.of(TextColors.GRAY, "facsimile"),
                            Text.of(TextColors.GRAY, "drainpipe"),
                            Text.of(TextColors.GRAY, "limerick"),
                            Text.of(TextColors.GRAY, "toadstool"),
                            Text.of(TextColors.GRAY, "talisman"),
                            Text.of(TextColors.GRAY, "alligator"),
                            Text.of(TextColors.GRAY, "whistle"),
                            Text.of(TextColors.GRAY, "bollard"),
                            Text.of(TextColors.GRAY, "slime"),
                            Text.of(TextColors.GRAY, "gallant"),
                            Text.of(TextColors.GRAY, "twisted"),
                            Text.of(TextColors.GRAY, "moist"),
                            Text.of(TextColors.GRAY, "himalayan"),
                            Text.of(TextColors.GRAY, "mortals"),
                            Text.of(TextColors.GRAY, "dollop"),
                            Text.of(TextColors.GRAY, "pompous"),
                            Text.of(TextColors.GRAY, "squeegee")
                    ))
                    .build();
        } else {
            this.logger.error("The pagination service was not properly registered for some reason :(");
            return;
        }

        Sponge.getCommandManager().register(this,
                CommandSpec.builder()
                        .arguments(GenericArguments.optionalWeak(GenericArguments.onlyOne(GenericArguments.integer(Text.of("page")))))
                        .executor((src, args) -> {
                            this.paginationList.sendTo(src, args.<Integer>getOne("page").orElse(1));

                            return CommandResult.success();
                        })
                        .build(),
                "paginationtest");
    }

}
