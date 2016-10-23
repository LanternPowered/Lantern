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
package org.lanternpowered.server.game.registry.type.data;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.type.LanternBannerPatternShape;
import org.lanternpowered.server.game.registry.PluginCatalogRegistryModule;
import org.spongepowered.api.data.type.BannerPatternShape;
import org.spongepowered.api.data.type.BannerPatternShapes;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class BannerPatternShapeRegistryModule extends PluginCatalogRegistryModule<BannerPatternShape> {

    private static final BannerPatternShapeRegistryModule INSTANCE = new BannerPatternShapeRegistryModule();

    public static BannerPatternShapeRegistryModule get() {
        return INSTANCE;
    }

    private final Map<String, BannerPatternShape> byInternalId = new HashMap<>();

    private BannerPatternShapeRegistryModule() {
        super(BannerPatternShapes.class);
    }

    @Override
    protected void register(BannerPatternShape catalogType, boolean disallowInbuiltPluginIds) {
        checkNotNull(catalogType, "catalogType");
        final String internalId = ((LanternBannerPatternShape) catalogType).getInternalId();
        checkArgument(!this.byInternalId.containsKey(internalId),
                "The internal id %s is already in use", internalId);
        super.register(catalogType, disallowInbuiltPluginIds);
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
        register(new LanternBannerPatternShape("minecraft", "base", "b"));
        register(new LanternBannerPatternShape("minecraft", "border", "bo"));
        register(new LanternBannerPatternShape("minecraft", "bricks", "bri"));
        register(new LanternBannerPatternShape("minecraft", "circle_middle", "mc"));
        register(new LanternBannerPatternShape("minecraft", "creeper", "cre"));
        register(new LanternBannerPatternShape("minecraft", "cross", "cr"));
        register(new LanternBannerPatternShape("minecraft", "curly_border", "cbo"));
        register(new LanternBannerPatternShape("minecraft", "diagonal_left", "ld"));
        register(new LanternBannerPatternShape("minecraft", "diagonal_left_mirror", "lud"));
        register(new LanternBannerPatternShape("minecraft", "diagonal_right", "rd"));
        register(new LanternBannerPatternShape("minecraft", "diagonal_right_mirror", "rud"));
        register(new LanternBannerPatternShape("minecraft", "flower", "flo"));
        register(new LanternBannerPatternShape("minecraft", "gradient", "gra"));
        register(new LanternBannerPatternShape("minecraft", "gradient_up", "gru"));
        register(new LanternBannerPatternShape("minecraft", "half_horizontal", "hh"));
        register(new LanternBannerPatternShape("minecraft", "half_horizontal_mirror", "hhb"));
        register(new LanternBannerPatternShape("minecraft", "half_vertical", "vh"));
        register(new LanternBannerPatternShape("minecraft", "half_vertical_mirror", "vhr"));
        register(new LanternBannerPatternShape("minecraft", "mojang", "moj"));
        register(new LanternBannerPatternShape("minecraft", "rhombus_middle", "mr"));
        register(new LanternBannerPatternShape("minecraft", "skull", "sku"));
        register(new LanternBannerPatternShape("minecraft", "square_bottom_left", "bl"));
        register(new LanternBannerPatternShape("minecraft", "square_bottom_right", "br"));
        register(new LanternBannerPatternShape("minecraft", "square_top_left", "tl"));
        register(new LanternBannerPatternShape("minecraft", "square_top_right", "tr"));
        register(new LanternBannerPatternShape("minecraft", "straight_cross", "sc"));
        register(new LanternBannerPatternShape("minecraft", "stripe_bottom", "bs"));
        register(new LanternBannerPatternShape("minecraft", "stripe_center", "cs"));
        register(new LanternBannerPatternShape("minecraft", "stripe_downleft", "dls"));
        register(new LanternBannerPatternShape("minecraft", "stripe_downright", "drs"));
        register(new LanternBannerPatternShape("minecraft", "stripe_left", "ls"));
        register(new LanternBannerPatternShape("minecraft", "stripe_middle", "ms"));
        register(new LanternBannerPatternShape("minecraft", "stripe_right", "rs"));
        register(new LanternBannerPatternShape("minecraft", "stripe_small", "ss"));
        register(new LanternBannerPatternShape("minecraft", "stripe_top", "ts"));
        register(new LanternBannerPatternShape("minecraft", "triangles_bottom", "bts"));
        register(new LanternBannerPatternShape("minecraft", "triangles_top", "tts"));
        register(new LanternBannerPatternShape("minecraft", "triangle_bottom", "bt"));
        register(new LanternBannerPatternShape("minecraft", "triangle_top", "tt"));
    }
}
