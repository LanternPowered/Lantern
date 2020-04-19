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
package org.lanternpowered.server.command.test;

import org.lanternpowered.server.command.CommandProvider;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class CommandSpawnEntity extends CommandProvider {

    public CommandSpawnEntity() {
        super(2, "spawn");
    }

    @Override
    public void completeSpec(PluginContainer pluginContainer, CommandSpec.Builder specBuilder) {
        specBuilder
                .arguments(
                        GenericArguments.catalogedElement(Text.of("type"), EntityType.class),
                        GenericArguments.location(Text.of("location")),
                        GenericArguments.string(Text.of("name")))
                .executor((src, args) -> {
                    final EntityType entityType = args.<EntityType>getOne("type").get();
                    final String name = args.<String>getOne("name").get();
                    final Location<World> location = args.<Location<World>>getOne("location").get();
                    final Entity entity = location.getExtent().createEntity(entityType, location.getPosition());
                    entity.offer(Keys.DISPLAY_NAME, Text.of(name));
                    location.getExtent().spawnEntity(entity);
                    src.sendMessage(Text.of("Successfully spawned a ", entityType.getTranslation().get(),
                            " named ", TextColors.LIGHT_PURPLE, name));
                    return CommandResult.success();
                });
    }
}
