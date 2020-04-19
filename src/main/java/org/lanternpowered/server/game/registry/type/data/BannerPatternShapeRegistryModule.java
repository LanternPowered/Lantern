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
package org.lanternpowered.server.game.registry.type.data;

import static com.google.common.base.Preconditions.checkArgument;

import org.lanternpowered.server.data.type.LanternBannerPatternShape;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.BannerPatternShapes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BannerPatternShapeRegistryModule extends DefaultCatalogRegistryModule<BannerPatternShape> {

    private static final BannerPatternShapeRegistryModule INSTANCE = new BannerPatternShapeRegistryModule();

    public static BannerPatternShapeRegistryModule get() {
        return INSTANCE;
    }

    private final Map<String, BannerPatternShape> byInternalId = new HashMap<>();

    private BannerPatternShapeRegistryModule() {
        super(BannerPatternShapes.class);
    }

    @Override
    protected void doRegistration(BannerPatternShape catalogType, boolean disallowInbuiltPluginIds) {
        final String internalId = ((LanternBannerPatternShape) catalogType).getInternalId();
        checkArgument(!this.byInternalId.containsKey(internalId),
                "The internal id %s is already in use", internalId);
        super.doRegistration(catalogType, disallowInbuiltPluginIds);
        this.byInternalId.put(internalId, catalogType);
    }

    /**
     * Gets the {@link BannerPatternShape} by using the internal id.
     *
     * @param internalId The internal id
     * @return The catalog type if present
     */
    public Optional<BannerPatternShape> getByInternalId(String internalId) {
        return Optional.ofNullable(this.byInternalId.get(internalId));
    }

    @Override
    public void registerDefaults() {
        registerMinecraft("base", "b");
        registerMinecraft("border", "bo");
        registerMinecraft("bricks", "bri");
        registerMinecraft("circle_middle", "mc");
        registerMinecraft("creeper", "cre");
        registerMinecraft("cross", "cr");
        registerMinecraft("curly_border", "cbo");
        registerMinecraft("diagonal_left", "ld");
        registerMinecraft("diagonal_left_mirror", "lud");
        registerMinecraft("diagonal_right", "rd");
        registerMinecraft("diagonal_right_mirror", "rud");
        registerMinecraft("flower", "flo");
        registerMinecraft("gradient", "gra");
        registerMinecraft("gradient_up", "gru");
        registerMinecraft("half_horizontal", "hh");
        registerMinecraft("half_horizontal_mirror", "hhb");
        registerMinecraft("half_vertical", "vh");
        registerMinecraft("half_vertical_mirror", "vhr");
        registerMinecraft("mojang", "moj");
        registerMinecraft("rhombus_middle", "mr");
        registerMinecraft("skull", "sku");
        registerMinecraft("square_bottom_left", "bl");
        registerMinecraft("square_bottom_right", "br");
        registerMinecraft("square_top_left", "tl");
        registerMinecraft("square_top_right", "tr");
        registerMinecraft("straight_cross", "sc");
        registerMinecraft("stripe_bottom", "bs");
        registerMinecraft("stripe_center", "cs");
        registerMinecraft("stripe_downleft", "dls");
        registerMinecraft("stripe_downright", "drs");
        registerMinecraft("stripe_left", "ls");
        registerMinecraft("stripe_middle", "ms");
        registerMinecraft("stripe_right", "rs");
        registerMinecraft("stripe_small", "ss");
        registerMinecraft("stripe_top", "ts");
        registerMinecraft("triangles_bottom", "bts");
        registerMinecraft("triangles_top", "tts");
        registerMinecraft("triangle_bottom", "bt");
        registerMinecraft("triangle_top", "tt");
    }

    private void registerMinecraft(String id, String internalId) {
        register(new LanternBannerPatternShape(CatalogKey.minecraft(id), internalId));
    }
}
