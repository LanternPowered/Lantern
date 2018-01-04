package org.lanternpowered.server.game.registry.type.advancement;

import org.lanternpowered.server.advancement.LanternAdvancementType;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.advancement.AdvancementType;
import org.spongepowered.api.advancement.AdvancementTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextFormat;

public class AdvancementTypeRegistryModule extends PluginCatalogRegistryModule<AdvancementType> {

    public AdvancementTypeRegistryModule() {
        super(AdvancementTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternAdvancementType("minecraft", "task", 0, TextFormat.of(TextColors.YELLOW)));
        register(new LanternAdvancementType("minecraft", "challenge", 1, TextFormat.of(TextColors.LIGHT_PURPLE)));
        register(new LanternAdvancementType("minecraft", "goal", 2, TextFormat.of(TextColors.YELLOW)));
    }
}
