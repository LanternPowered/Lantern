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
package org.lanternpowered.server.data;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.lanternpowered.server.data.meta.LanternPatternLayer;
import org.lanternpowered.server.data.persistence.DataTranslators;
import org.lanternpowered.server.data.persistence.DataTypeSerializers;
import org.lanternpowered.server.data.property.block.GroundLuminancePropertyStore;
import org.lanternpowered.server.data.property.block.SkyLuminancePropertyStore;
import org.lanternpowered.server.data.property.entity.DominantHandPropertyStore;
import org.lanternpowered.server.effect.potion.LanternPotionEffectBuilder;
import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.registry.type.data.DataSerializerRegistry;
import org.lanternpowered.server.item.enchantment.LanternEnchantmentBuilder;
import org.lanternpowered.server.profile.LanternGameProfileBuilder;
import org.lanternpowered.server.profile.LanternProfilePropertyBuilder;
import org.lanternpowered.server.text.serializer.BookViewConfigSerializer;
import org.lanternpowered.server.text.serializer.TextConfigSerializer;
import org.lanternpowered.server.util.copy.Copyable;
import org.spongepowered.api.data.meta.PatternLayer;
import org.spongepowered.api.data.property.Properties;
import org.spongepowered.api.data.property.PropertyRegistry;
import org.spongepowered.api.effect.potion.PotionEffect;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.RespawnLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataRegistrar {

    public static void setupRegistrations(LanternGame game) {
        Copyable.register(ImmutableMap.class, map -> map);
        Copyable.register(ImmutableList.class, list -> list);
        Copyable.register(ImmutableSet.class, set -> set);
        Copyable.register(List.class, ArrayList::new);
        Copyable.register(Set.class, HashSet::new);
        Copyable.register(Map.class, HashMap::new);

        final PropertyRegistry propertyRegistry = game.getPropertyRegistry();

        // Block property stores
        propertyRegistry.register(Properties.SKY_LUMINANCE, new SkyLuminancePropertyStore());
        propertyRegistry.register(Properties.BLOCK_LUMINANCE, new GroundLuminancePropertyStore());

        // Entity property stores
        propertyRegistry.register(Properties.DOMINANT_HAND, new DominantHandPropertyStore());

        // Item property stores
        // TODO
        // propertyRegistry.register(BurningFuelProperty.class, new BurningFuelPropertyStore());

        final LanternDataManager dataManager = game.getDataManager();
        // Register the data type serializers
        DataTypeSerializers.registerSerializers(DataSerializerRegistry.INSTANCE);
        // Register the data serializers
        DataTranslators.registerSerializers(DataSerializerRegistry.INSTANCE);

        // Register the data builders
        dataManager.registerBuilder(PatternLayer.class, new LanternPatternLayer.Builder(game));
        dataManager.registerBuilder(Text.class, new TextConfigSerializer());
        dataManager.registerBuilder(BookView.class, new BookViewConfigSerializer());
        dataManager.registerBuilder(PotionEffect.class, new LanternPotionEffectBuilder());
        dataManager.registerBuilder(RespawnLocation.class, new RespawnLocation.Builder());
        dataManager.registerBuilder(Enchantment.class, new LanternEnchantmentBuilder());
        dataManager.registerBuilder(ProfileProperty.class, new LanternProfilePropertyBuilder());
        dataManager.registerBuilder(GameProfile.class, new LanternGameProfileBuilder());
    }

    public static void finalizeRegistrations(LanternGame game) {
        game.getPropertyRegistry().completeRegistration();
    }
}
