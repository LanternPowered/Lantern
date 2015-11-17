/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.game;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.lanternpowered.server.attribute.AttributeTargets;
import org.lanternpowered.server.attribute.LanternAttribute;
import org.lanternpowered.server.attribute.LanternAttributeBuilder;
import org.lanternpowered.server.attribute.LanternAttributeCalculator;
import org.lanternpowered.server.attribute.LanternAttributeModifierBuilder;
import org.lanternpowered.server.attribute.LanternOperation;
import org.lanternpowered.server.block.LanternBlockRegistry;
import org.lanternpowered.server.block.LanternBlockStateBuilder;
import org.lanternpowered.server.block.type.BlockAir;
import org.lanternpowered.server.block.type.BlockBedrock;
import org.lanternpowered.server.block.type.BlockDirt;
import org.lanternpowered.server.block.type.BlockGrass;
import org.lanternpowered.server.block.type.BlockStone;
import org.lanternpowered.server.catalog.CatalogTypeRegistry;
import org.lanternpowered.server.catalog.LanternCatalogTypeRegistry;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.data.type.LanternDirtTypes;
import org.lanternpowered.server.data.type.LanternDoublePlantType;
import org.lanternpowered.server.data.type.LanternDoublePlantTypes;
import org.lanternpowered.server.data.type.LanternNotePitch;
import org.lanternpowered.server.data.type.LanternPlantType;
import org.lanternpowered.server.data.type.LanternPlantTypes;
import org.lanternpowered.server.data.type.LanternShrubType;
import org.lanternpowered.server.data.type.LanternShrubTypes;
import org.lanternpowered.server.data.type.LanternStoneType;
import org.lanternpowered.server.data.type.LanternStoneTypes;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.effect.sound.LanternSoundType;
import org.lanternpowered.server.entity.living.player.gamemode.LanternGameMode;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.LanternItemRegistry;
import org.lanternpowered.server.resourcepack.LanternResourcePackFactory;
import org.lanternpowered.server.status.LanternFavicon;
import org.lanternpowered.server.text.LanternTextFactory;
import org.lanternpowered.server.text.format.LanternTextColor;
import org.lanternpowered.server.text.format.LanternTextStyle;
import org.lanternpowered.server.text.selector.LanternArgumentHolder;
import org.lanternpowered.server.text.selector.LanternSelectorFactory;
import org.lanternpowered.server.text.selector.LanternSelectorType;
import org.lanternpowered.server.text.sink.LanternMessageSinkFactory;
import org.lanternpowered.server.text.translation.LanternTranslationManager;
import org.lanternpowered.server.text.translation.TranslationManager;
import org.lanternpowered.server.util.rotation.LanternRotation;
import org.lanternpowered.server.world.LanternWeather;
import org.lanternpowered.server.world.LanternWorldBuilder;
import org.lanternpowered.server.world.biome.LanternBiomeRegistry;
import org.lanternpowered.server.world.difficulty.LanternDifficulty;
import org.lanternpowered.server.world.dimension.LanternDimensionEnd;
import org.lanternpowered.server.world.dimension.LanternDimensionNether;
import org.lanternpowered.server.world.dimension.LanternDimensionOverworld;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.lanternpowered.server.world.extent.LanternExtentBufferFactory;
import org.lanternpowered.server.world.gen.LanternGeneratorTypeNether;
import org.lanternpowered.server.world.gen.debug.DebugGeneratorType;
import org.lanternpowered.server.world.gen.flat.FlatGeneratorType;
import org.lanternpowered.server.world.gen.skylands.SkylandsGeneratorType;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.GameProfile;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.ImmutableDataRegistry;
import org.spongepowered.api.data.manipulator.DataManipulatorRegistry;
import org.spongepowered.api.data.type.DirtType;
import org.spongepowered.api.data.type.DirtTypes;
import org.spongepowered.api.data.type.DoublePlantType;
import org.spongepowered.api.data.type.DoublePlantTypes;
import org.spongepowered.api.data.type.NotePitch;
import org.spongepowered.api.data.type.NotePitches;
import org.spongepowered.api.data.type.PlantType;
import org.spongepowered.api.data.type.PlantTypes;
import org.spongepowered.api.data.type.ShrubType;
import org.spongepowered.api.data.type.ShrubTypes;
import org.spongepowered.api.data.type.StoneType;
import org.spongepowered.api.data.type.StoneTypes;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.animal.Horse;
import org.spongepowered.api.entity.living.monster.Zombie;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.extra.skylands.SkylandsWorldGeneratorModifier;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.recipe.RecipeRegistry;
import org.spongepowered.api.resourcepack.ResourcePack;
import org.spongepowered.api.resourcepack.ResourcePackFactory;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlot;
import org.spongepowered.api.statistic.BlockStatistic;
import org.spongepowered.api.statistic.EntityStatistic;
import org.spongepowered.api.statistic.ItemStatistic;
import org.spongepowered.api.statistic.Statistic;
import org.spongepowered.api.statistic.StatisticGroup;
import org.spongepowered.api.statistic.TeamStatistic;
import org.spongepowered.api.status.Favicon;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyle;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.selector.ArgumentHolder;
import org.spongepowered.api.text.selector.ArgumentType;
import org.spongepowered.api.text.selector.ArgumentTypes;
import org.spongepowered.api.text.selector.SelectorType;
import org.spongepowered.api.text.selector.SelectorTypes;
import org.spongepowered.api.text.selector.Selectors;
import org.spongepowered.api.text.sink.MessageSinks;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.util.ResettableBuilder;
import org.spongepowered.api.util.rotation.Rotation;
import org.spongepowered.api.util.rotation.Rotations;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.WorldBuilder;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.difficulty.Difficulties;
import org.spongepowered.api.world.difficulty.Difficulty;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.gamerule.DefaultGameRules;
import org.spongepowered.api.world.gen.PopulatorFactory;
import org.spongepowered.api.world.gen.PopulatorType;
import org.spongepowered.api.world.gen.WorldGeneratorModifier;
import org.spongepowered.api.world.weather.Weather;
import org.spongepowered.api.world.weather.Weathers;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class LanternGameRegistry implements GameRegistry {

    private final LanternGame game;
    private final Set<String> defaultGameRules;
    private final LanternTranslationManager translationManager = new LanternTranslationManager();
    private final LanternResourcePackFactory resourcePackFactory = new LanternResourcePackFactory();
    private final LanternAttributeCalculator attributeCalculator = new LanternAttributeCalculator();
    private final LanternBiomeRegistry biomeRegistry = new LanternBiomeRegistry();
    private final LanternBlockRegistry blockRegistry = new LanternBlockRegistry();
    private final LanternItemRegistry itemRegistry = new LanternItemRegistry();
    private final CatalogTypeRegistry<Difficulty> difficultyRegistry = new LanternCatalogTypeRegistry<Difficulty>();
    private final CatalogTypeRegistry<GameMode> gameModeRegistry = new LanternCatalogTypeRegistry<GameMode>();
    private final CatalogTypeRegistry<LanternAttribute> attributeRegistry = new LanternCatalogTypeRegistry<LanternAttribute>();
    private final CatalogTypeRegistry<LanternOperation> attributeOperationRegistry = new LanternCatalogTypeRegistry<LanternOperation>();
    private final CatalogTypeRegistry<TextColor> textColorRegistry = new LanternCatalogTypeRegistry<TextColor>();
    private final CatalogTypeRegistry<TextStyle.Base> textStyleRegistry = new LanternCatalogTypeRegistry<TextStyle.Base>();
    private final CatalogTypeRegistry<WorldGeneratorModifier> worldGeneratorModifierRegistry =
            new LanternCatalogTypeRegistry<WorldGeneratorModifier>();
    private final CatalogTypeRegistry<Rotation> rotationRegistry = new LanternCatalogTypeRegistry<Rotation>();
    private final CatalogTypeRegistry<DimensionType> dimensionTypeRegistry = new LanternCatalogTypeRegistry<DimensionType>();
    private final CatalogTypeRegistry<GeneratorType> generatorTypeRegistry = new LanternCatalogTypeRegistry<GeneratorType>();
    private final CatalogTypeRegistry<SoundType> soundTypeRegistry = new LanternCatalogTypeRegistry<SoundType>();
    private final CatalogTypeRegistry<NotePitch> notePitchRegistry = new LanternCatalogTypeRegistry<NotePitch>();
    private final CatalogTypeRegistry<ShrubType> shrubTypeRegistry = new LanternCatalogTypeRegistry<ShrubType>();
    private final CatalogTypeRegistry<DoublePlantType> doublePlantTypeRegistry = new LanternCatalogTypeRegistry<DoublePlantType>();
    private final CatalogTypeRegistry<PlantType> plantTypeRegistry = new LanternCatalogTypeRegistry<PlantType>();
    private final CatalogTypeRegistry<SelectorType> selectorTypeRegistry = new LanternCatalogTypeRegistry<SelectorType>();
    private final CatalogTypeRegistry<DirtType> dirtTypeRegistry = new LanternCatalogTypeRegistry<DirtType>();
    private final CatalogTypeRegistry<StoneType> stoneTypeRegistry = new LanternCatalogTypeRegistry<StoneType>();
    private final CatalogTypeRegistry<ParticleType> particleTypeRegistry = new LanternCatalogTypeRegistry<ParticleType>();
    private final CatalogTypeRegistry<PopulatorType> populatorTypeRegistry = new LanternCatalogTypeRegistry<PopulatorType>();
    private final CatalogTypeRegistry<LanternWeather> weatherRegistry = new LanternCatalogTypeRegistry<LanternWeather>();
    private final Map<Class<?>, CatalogTypeRegistry<?>> catalogTypeRegistries = ImmutableMap.<Class<?>, CatalogTypeRegistry<?>>builder()
            .put(LanternAttribute.class, this.attributeRegistry)
            .put(LanternOperation.class, this.attributeOperationRegistry)
            .put(BiomeType.class, this.biomeRegistry)
            .put(BlockType.class, this.blockRegistry)
            .put(ItemType.class, this.itemRegistry)
            .put(Difficulty.class, this.difficultyRegistry)
            .put(GameMode.class, this.gameModeRegistry)
            .put(TextColor.class, this.textColorRegistry)
            // Not sure which class I should use, so lets do both
            .put(TextStyle.class, this.textStyleRegistry)
            .put(TextStyle.Base.class, this.textStyleRegistry)
            .put(Rotation.class, this.rotationRegistry)
            .put(DimensionType.class, this.dimensionTypeRegistry)
            .put(SoundType.class, this.soundTypeRegistry)
            .put(NotePitch.class, this.notePitchRegistry)
            .put(GeneratorType.class, this.generatorTypeRegistry)
            .put(ShrubType.class, this.shrubTypeRegistry)
            .put(DoublePlantType.class, this.doublePlantTypeRegistry)
            .put(PlantType.class, this.plantTypeRegistry)
            .put(WorldGeneratorModifier.class, this.worldGeneratorModifierRegistry)
            .put(SelectorType.class, this.selectorTypeRegistry)
            .put(DirtType.class, this.dirtTypeRegistry)
            .put(StoneType.class, this.stoneTypeRegistry)
            .put(ParticleType.class, this.particleTypeRegistry)
            .put(PopulatorType.class, this.populatorTypeRegistry)
            .put(Weather.class, this.weatherRegistry)
            .build();
    private final Map<Class<?>, Supplier<Object>> builderFactories = ImmutableMap.<Class<?>, Supplier<Object>>builder()
            .put(LanternAttributeBuilder.class, () -> new LanternAttributeBuilder(this.attributeRegistry))
            .put(BlockState.Builder.class, LanternBlockStateBuilder::new)
            .put(WorldBuilder.class, () -> createWorldBuilder())
            .build();

    // We cannot add this method directly in builderFactories map,
    // the compiler will throw a error that the game parameter may
    // not be initialized yet.
    private LanternWorldBuilder createWorldBuilder() {
        return new LanternWorldBuilder(this.game);
    }

    {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (Field field : DefaultGameRules.class.getFields()) {
            if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
                try {
                    builder.add((String) field.get(null));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.defaultGameRules = builder.build();
    }

    private boolean registered;

    public LanternGameRegistry(LanternGame game) {
        this.game = game;
    }

    public void registerGameObjects() {
        if (this.registered) {
            throw new IllegalStateException("You can only register the game objects once!");
        }
        this.registered = true;
        this.registerWeathers();
        this.registerNotePitches();
        // The particle types, requires NotePitches
        this.registerParticleTypes();
        this.registerTextFactory();
        this.registerTextStyles();
        this.registerTextColors();
        this.registerMessageSinkFactory();
        this.registerSoundTypes();
        this.registerShrubTypes();
        this.registerPlantTypes();
        this.registerDoublePlantTypes();
        this.registerDirtTypes();
        this.registerStoneTypes();
        this.registerGeneratorModifiers();
        this.registerGeneratorTypes();
        this.registerDimensionTypes();
        this.registerDifficulties();
        this.registerGameModes();
        this.registerRotations();
        this.registerAttributes();
        this.registerSelectors();
        this.registerBlockTypes();
    }

    private void registerWeathers() {
        Map<String, LanternWeather> mappings = Maps.newHashMap();
        mappings.put("clear", new LanternWeather("minecraft", "clear", 0f, 0f, 0f, 0f));
        mappings.put("rain", new LanternWeather("minecraft", "rain", 1f, 0f, 0f, 0f));
        mappings.put("thunder_storm", new LanternWeather("minecraft", "thunderStorm", 1f, 1f, 0.00001f, 0.00001f));
        mappings.forEach((key, value) -> this.weatherRegistry.register(value));
        RegistryHelper.mapFields(Weathers.class, mappings);
    }
 
    private void registerParticleTypes() {
        Map<String, ParticleType> mappings = Maps.newHashMap();
        mappings.put("explosion_normal", new LanternParticleType(0, "explode", true));
        mappings.put("explosion_large", new LanternParticleType.Resizable(1, "largeexplode", false, 1f));
        mappings.put("explosion_huge", new LanternParticleType(2, "hugeexplosion", false));
        mappings.put("fireworks_spark", new LanternParticleType(3, "fireworksSpark", true));
        mappings.put("water_bubble", new LanternParticleType(4, "bubble", true));
        mappings.put("water_splash", new LanternParticleType(5, "splash", true));
        mappings.put("water_wake", new LanternParticleType(6, "wake", true));
        mappings.put("suspended", new LanternParticleType(7, "suspended", false));
        mappings.put("suspended_depth", new LanternParticleType(8, "depthsuspend", false));
        mappings.put("crit", new LanternParticleType(9, "crit", true));
        mappings.put("crit_magic", new LanternParticleType(10, "magicCrit", true));
        mappings.put("smoke_normal", new LanternParticleType(11, "smoke", true));
        mappings.put("smoke_large", new LanternParticleType(12, "largesmoke", true));
        mappings.put("spell", new LanternParticleType(13, "spell", false));
        mappings.put("spell_instant", new LanternParticleType(14, "instantSpell", false));
        mappings.put("spell_mob", new LanternParticleType.Colorable(15, "mobSpell", false, Color.BLACK));
        mappings.put("spell_mob_ambient", new LanternParticleType.Colorable(16, "mobSpellAmbient", false, Color.BLACK));
        mappings.put("spell_witch", new LanternParticleType(17, "witchMagic", false));
        mappings.put("drip_water", new LanternParticleType(18, "dripWater", false));
        mappings.put("drip_lava", new LanternParticleType(19, "dripLava", false));
        mappings.put("villager_angry", new LanternParticleType(20, "angryVillager", false));
        mappings.put("villager_happy", new LanternParticleType(21, "happyVillager", true));
        mappings.put("town_aura", new LanternParticleType(22, "townaura", true));
        mappings.put("note", new LanternParticleType.Note(23, "note", false, NotePitches.F_SHARP0));
        mappings.put("portal", new LanternParticleType(24, "portal", true));
        mappings.put("enchantment_table", new LanternParticleType(25, "enchantmenttable", true));
        mappings.put("flame", new LanternParticleType(26, "flame", true));
        mappings.put("lava", new LanternParticleType(27, "lava", false));
        mappings.put("footstep", new LanternParticleType(28, "footstep", false));
        mappings.put("cloud", new LanternParticleType(29, "cloud", true));
        mappings.put("redstone", new LanternParticleType.Colorable(30, "reddust", false, Color.RED));
        mappings.put("snowball", new LanternParticleType(31, "snowballpoof", false));
        mappings.put("snow_shovel", new LanternParticleType(32, "snowshovel", true));
        mappings.put("slime", new LanternParticleType(33, "slime", false));
        mappings.put("heart", new LanternParticleType(34, "heart", false));
        mappings.put("barrier", new LanternParticleType(35, "barrier", false));
        mappings.put("item_crack", new LanternParticleType.Material(36, "iconcrack", true, new LanternItemStack(BlockTypes.STONE)));
        mappings.put("block_crack", new LanternParticleType.Material(37, "blockcrack", true, new LanternItemStack(BlockTypes.STONE)));
        mappings.put("block_dust", new LanternParticleType.Material(38, "blockdust", true, new LanternItemStack(BlockTypes.STONE)));
        mappings.put("water_drop", new LanternParticleType(39, "droplet", false));
        mappings.put("item_take", new LanternParticleType(40, "take", false));
        mappings.put("mob_appearance", new LanternParticleType(41, "mobappearance", false));
        mappings.forEach((key, value) -> this.particleTypeRegistry.register(value));
        RegistryHelper.mapFields(ParticleTypes.class, this.particleTypeRegistry.getDelegateMap());
    }

    private void registerPlantTypes() {
        for (LanternPlantType type : LanternPlantTypes.values()) {
            this.plantTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(PlantTypes.class, this.plantTypeRegistry.getDelegateMap());
    }

    private void registerDoublePlantTypes() {
        for (LanternDoublePlantType type : LanternDoublePlantTypes.values()) {
            this.doublePlantTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(DoublePlantTypes.class, this.doublePlantTypeRegistry.getDelegateMap());
    }

    private void registerStoneTypes() {
        for (LanternStoneType type : LanternStoneTypes.values()) {
            this.stoneTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(StoneTypes.class, this.stoneTypeRegistry.getDelegateMap());
    }

    private void registerDirtTypes() {
        for (LanternDirtType type : LanternDirtTypes.values()) {
            this.dirtTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(DirtTypes.class, this.dirtTypeRegistry.getDelegateMap());
    }

    private void registerShrubTypes() {
        for (LanternShrubType type : LanternShrubTypes.values()) {
            this.shrubTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(ShrubTypes.class, this.shrubTypeRegistry.getDelegateMap());
    }

    private void registerGeneratorModifiers() {
        this.worldGeneratorModifierRegistry.register(new SkylandsWorldGeneratorModifier());
    }

    private void registerGeneratorTypes() {
        this.generatorTypeRegistry.register(new LanternGeneratorTypeNether("minecraft", "nether"));
        this.generatorTypeRegistry.register(new FlatGeneratorType("minecraft", "flat"));
        this.generatorTypeRegistry.register(new DebugGeneratorType("minecraft", "debug"));
        this.generatorTypeRegistry.register(new SkylandsGeneratorType("sponge", "skylands"));
        RegistryHelper.mapFields(GeneratorTypes.class, this.generatorTypeRegistry.getDelegateMap());
    }

    private void registerDimensionTypes() {
        this.dimensionTypeRegistry.register(new LanternDimensionType<>("minecraft", "end", -1,
                LanternDimensionEnd.class, true, false, false,
                (world, type) -> new LanternDimensionEnd(world, type.getName(), type)));
        this.dimensionTypeRegistry.register(new LanternDimensionType<>("minecraft", "overworld", 0,
                LanternDimensionOverworld.class, true, false, false,
                (world, type) -> new LanternDimensionOverworld(world, type.getName(), type)));
        this.dimensionTypeRegistry.register(new LanternDimensionType<>("minecraft", "nether", 1,
                LanternDimensionNether.class, true, false, false,
                (world, type) -> new LanternDimensionNether(world, type.getName(), type)));
        RegistryHelper.mapFields(DimensionTypes.class, this.dimensionTypeRegistry.getDelegateMap());
    }

    private void registerAttributes() {
        this.attributeOperationRegistry.register(new LanternOperation("add_amount", 3, false,
                (base, modifier, current) -> modifier));
        this.attributeOperationRegistry.register(new LanternOperation("multiply", 2, false,
                (base, modifier, current) -> current * modifier - current));
        this.attributeOperationRegistry.register(new LanternOperation("multiply_base", 1, false,
                (base, modifier, current) -> base * modifier - current));
        // RegistryHelper.mapFields(Operations.class, this.attributeOperationRegistry.getDelegateMap());
        Map<String, Predicate<DataHolder>> targetMappings = Maps.newHashMap();
        targetMappings.put("generic", target -> target instanceof Living);
        targetMappings.put("horse", target -> target instanceof Horse);
        targetMappings.put("zombie", target -> target instanceof Zombie);
        RegistryHelper.mapFields(AttributeTargets.class, targetMappings);
        Map<String, LanternAttribute> mappings = Maps.newHashMap();
        mappings.put("GENERIC_ARMOR", this.registerDefaultAttribute(
                "generic.armor", 0.0, 0.0, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("GENERIC_MAX_HEALTH", this.registerDefaultAttribute(
                "generic.maxHealth", 20.0, 0.0, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("GENERIC_FOLLOW_RANGE", this.registerDefaultAttribute(
                "generic.followRange", 32.0D, 0.0D, 2048.0D, AttributeTargets.GENERIC));
        mappings.put("GENERIC_ATTACK_DAMAGE", this.registerDefaultAttribute(
                "generic.attackDamage", 2.0D, 0.0D, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("GENERIC_ATTACK_SPEED", this.registerDefaultAttribute(
                "generic.attackSpeed", 4.0, 0.0, 1024.0D, AttributeTargets.GENERIC));
        mappings.put("GENERIC_KNOCKBACK_RESISTANCE", this.registerDefaultAttribute(
                "generic.knockbackResistance", 0.0D, 0.0D, 1.0D, AttributeTargets.GENERIC));
        mappings.put("GENERIC_MOVEMENT_SPEED", this.registerDefaultAttribute(
                "generic.movementSpeed", 0.7D, 0.0D, Double.MAX_VALUE, AttributeTargets.GENERIC));
        mappings.put("HORSE_JUMP_STRENGTH", this.registerDefaultAttribute(
                "horse.jumpStrength", 0.7D, 0.0D, 2.0D, AttributeTargets.HORSE));
        mappings.put("ZOMBIE_SPAWN_REINFORCEMENTS", this.registerDefaultAttribute(
                "zombie.spawnReinforcements", 0.0D, 0.0D, 1.0D, AttributeTargets.ZOMBIE));
        // RegistryHelper.mapFields(Attributes.class, mappings);
    }

    private LanternAttribute registerDefaultAttribute(String id, double def, double min, double max, Predicate<DataHolder> targets) {
        return this.createAttributeBuilder().id(id)
                .defaultValue(def)
                .maximum(max)
                .minimum(min)
                .name(Texts.of(this.translationManager.get("attribute.name." + id)))
                .targets(targets)
                .build();
    }

    private void registerRotations() {
        this.rotationRegistry.register(new LanternRotation("top", 0));
        this.rotationRegistry.register(new LanternRotation("top_right", 45));
        this.rotationRegistry.register(new LanternRotation("right", 90));
        this.rotationRegistry.register(new LanternRotation("bottom_right", 135));
        this.rotationRegistry.register(new LanternRotation("bottom", 180));
        this.rotationRegistry.register(new LanternRotation("bottom_left", 225));
        this.rotationRegistry.register(new LanternRotation("left", 270));
        this.rotationRegistry.register(new LanternRotation("top_left", 315));
        RegistryHelper.mapFields(Rotations.class, this.rotationRegistry.getDelegateMap());
    }

    private void registerTextFactory() {
        RegistryHelper.setFactory(Texts.class, new LanternTextFactory(this.translationManager));
    }

    private void registerMessageSinkFactory() {
        RegistryHelper.setFactory(MessageSinks.class, new LanternMessageSinkFactory());
    }

    private void registerTextStyles() {
        this.textStyleRegistry.register(new LanternTextStyle("bold", true, null, null, null, null));
        this.textStyleRegistry.register(new LanternTextStyle("italic", null, true, null, null, null));
        this.textStyleRegistry.register(new LanternTextStyle("underline", null, null, true, null, null));
        this.textStyleRegistry.register(new LanternTextStyle("strikethrough", null, null, null, true, null));
        this.textStyleRegistry.register(new LanternTextStyle("obfuscated", null, null, null, null, true));
        this.textStyleRegistry.register(new LanternTextStyle("reset", false, false, false, false, false));
        RegistryHelper.mapFields(TextStyles.class, this.textStyleRegistry.getDelegateMap());
    }

    private void registerTextColors() {
        this.textColorRegistry.register(new LanternTextColor("black", Color.BLACK));
        this.textColorRegistry.register(new LanternTextColor("dark_blue", new Color(0x0000AA)));
        this.textColorRegistry.register(new LanternTextColor("dark_green", new Color(0x00AA00)));
        this.textColorRegistry.register(new LanternTextColor("dark_aqua", new Color(0x00AAAA)));
        this.textColorRegistry.register(new LanternTextColor("dark_red", new Color(0xAA0000)));
        this.textColorRegistry.register(new LanternTextColor("dark_purple", new Color(0xAA00AA)));
        this.textColorRegistry.register(new LanternTextColor("gold", new Color(0xFFAA00)));
        this.textColorRegistry.register(new LanternTextColor("gray", new Color(0xAAAAAA)));
        this.textColorRegistry.register(new LanternTextColor("dark_gray", new Color(0x555555)));
        this.textColorRegistry.register(new LanternTextColor("blue", new Color(0x5555FF)));
        this.textColorRegistry.register(new LanternTextColor("green", new Color(0x55FF55)));
        this.textColorRegistry.register(new LanternTextColor("aqua", new Color(0x00FFFF)));
        this.textColorRegistry.register(new LanternTextColor("red", new Color(0xFF5555)));
        this.textColorRegistry.register(new LanternTextColor("light_purple", new Color(0xFF55FF)));
        this.textColorRegistry.register(new LanternTextColor("yellow", new Color(0xFFFF55)));
        this.textColorRegistry.register(new LanternTextColor("white", Color.WHITE));
        this.textColorRegistry.register(new LanternTextColor("reset", Color.WHITE));
        this.textColorRegistry.register(TextColors.NONE);
        RegistryHelper.mapFields(TextColors.class, this.textColorRegistry.getDelegateMap());
    }

    private void registerSelectors() {
        Map<String, SelectorType> selectorMappings = Maps.newHashMap();
        selectorMappings.put("all_players", new LanternSelectorType("a"));
        selectorMappings.put("all_entities", new LanternSelectorType("e"));
        selectorMappings.put("nearest_player", new LanternSelectorType("p"));
        selectorMappings.put("random", new LanternSelectorType("r"));
        for (SelectorType type : selectorMappings.values()) {
            this.selectorTypeRegistry.register(type);
        }
        RegistryHelper.mapFields(SelectorTypes.class, selectorMappings);

        LanternSelectorFactory factory = new LanternSelectorFactory(this.selectorTypeRegistry);
        Map<String, ArgumentHolder<?>> argMappings = Maps.newHashMap();
        // POSITION
        ArgumentType<Integer> x = factory.createArgumentType("x", Integer.class);
        ArgumentType<Integer> y = factory.createArgumentType("y", Integer.class);
        ArgumentType<Integer> z = factory.createArgumentType("z", Integer.class);
        ArgumentHolder.Vector3<Vector3i, Integer> position = new LanternArgumentHolder.LanternVector3<Vector3i, Integer>(x, y, z, Vector3i.class);
        argMappings.put("position", position);

        // RADIUS
        ArgumentType<Integer> rmin = factory.createArgumentType("rm", Integer.class);
        ArgumentType<Integer> rmax = factory.createArgumentType("r", Integer.class);
        ArgumentHolder.Limit<ArgumentType<Integer>> radius = new LanternArgumentHolder.LanternLimit<ArgumentType<Integer>>(rmin, rmax);
        argMappings.put("radius", radius);

        // GAME_MODE
        argMappings.put("game_mode", factory.createArgumentType("m", GameMode.class));

        // COUNT
        argMappings.put("count", factory.createArgumentType("c", Integer.class));

        // LEVEL
        ArgumentType<Integer> lmin = factory.createArgumentType("lm", Integer.class);
        ArgumentType<Integer> lmax = factory.createArgumentType("l", Integer.class);
        ArgumentHolder.Limit<ArgumentType<Integer>> level = new LanternArgumentHolder.LanternLimit<ArgumentType<Integer>>(lmin, lmax);
        argMappings.put("level", level);

        // TEAM
        argMappings.put("team", factory.createInvertibleArgumentType("team", Integer.class,
                org.spongepowered.api.scoreboard.Team.class.getName()));

        // NAME
        argMappings.put("name", factory.createInvertibleArgumentType("name", String.class));

        // DIMENSION
        ArgumentType<Integer> dx = factory.createArgumentType("dx", Integer.class);
        ArgumentType<Integer> dy = factory.createArgumentType("dy", Integer.class);
        ArgumentType<Integer> dz = factory.createArgumentType("dz", Integer.class);
        ArgumentHolder.Vector3<Vector3i, Integer> dimension =
                new LanternArgumentHolder.LanternVector3<Vector3i, Integer>(dx, dy, dz, Vector3i.class);
        argMappings.put("dimension", dimension);

        // ROTATION
        ArgumentType<Double> rotxmin = factory.createArgumentType("rxm", Double.class);
        ArgumentType<Double> rotymin = factory.createArgumentType("rym", Double.class);
        ArgumentType<Double> rotzmin = factory.createArgumentType("rzm", Double.class);
        ArgumentHolder.Vector3<Vector3d, Double> rotmin =
                new LanternArgumentHolder.LanternVector3<Vector3d, Double>(rotxmin, rotymin, rotzmin, Vector3d.class);
        ArgumentType<Double> rotxmax = factory.createArgumentType("rx", Double.class);
        ArgumentType<Double> rotymax = factory.createArgumentType("ry", Double.class);
        ArgumentType<Double> rotzmax = factory.createArgumentType("rz", Double.class);
        ArgumentHolder.Vector3<Vector3d, Double> rotmax =
                new LanternArgumentHolder.LanternVector3<Vector3d, Double>(rotxmax, rotymax, rotzmax, Vector3d.class);
        ArgumentHolder.Limit<ArgumentHolder.Vector3<Vector3d, Double>> rot =
                new LanternArgumentHolder.LanternLimit<ArgumentHolder.Vector3<Vector3d, Double>>(rotmin, rotmax);
        argMappings.put("rotation", rot);

        // ENTITY_TYPE
        argMappings.put("entity_type", factory.createInvertibleArgumentType("type", EntityType.class));

        RegistryHelper.mapFields(ArgumentTypes.class, argMappings);
        RegistryHelper.setFactory(Selectors.class, factory);
    }

    private void registerDifficulties() {
        this.difficultyRegistry.register(new LanternDifficulty("peaceful", 0));
        this.difficultyRegistry.register(new LanternDifficulty("easy", 1));
        this.difficultyRegistry.register(new LanternDifficulty("normal", 2));
        this.difficultyRegistry.register(new LanternDifficulty("hard", 3));
        RegistryHelper.mapFields(Difficulties.class, this.difficultyRegistry.getDelegateMap());
    }

    private void registerGameModes() {
        this.gameModeRegistry.register(new LanternGameMode("not_set", -1));
        this.gameModeRegistry.register(new LanternGameMode("survival", 0));
        this.gameModeRegistry.register(new LanternGameMode("creative", 1));
        this.gameModeRegistry.register(new LanternGameMode("adventure", 2));
        this.gameModeRegistry.register(new LanternGameMode("spectator", 3));
        RegistryHelper.mapFields(GameModes.class, this.gameModeRegistry.getDelegateMap());
    }

    private void registerSoundTypes() {
        Map<String, String> soundMappings = Maps.newHashMap();
        soundMappings.put("ambience_cave", "ambient.cave.cave");
        soundMappings.put("ambience_rain", "ambient.weather.rain");
        soundMappings.put("ambience_thunder", "ambient.weather.thunder");
        soundMappings.put("anvil_break", "random.anvil_break");
        soundMappings.put("anvil_land", "random.anvil_land");
        soundMappings.put("anvil_use", "random.anvil_use");
        soundMappings.put("arrow_hit", "random.bowhit");
        soundMappings.put("burp", "random.burp");
        soundMappings.put("chest_close", "random.chestclosed");
        soundMappings.put("chest_open", "random.chestopen");
        soundMappings.put("click", "random.click");
        soundMappings.put("door_close", "random.door_close");
        soundMappings.put("door_open", "random.door_open");
        soundMappings.put("drink", "random.drink");
        soundMappings.put("eat", "random.eat");
        soundMappings.put("explode", "random.explode");
        soundMappings.put("fall_big", "game.player.hurt.fall.big");
        soundMappings.put("fall_small", "game.player.hurt.fall.small");
        soundMappings.put("fire", "fire.fire");
        soundMappings.put("fire_ignite", "fire.ignite");
        soundMappings.put("firecharge_use", "item.fireCharge.use");
        soundMappings.put("fizz", "random.fizz");
        soundMappings.put("fuse", "game.tnt.primed");
        soundMappings.put("glass", "dig.glass");
        soundMappings.put("gui_button", "gui.button.press");
        soundMappings.put("hurt_flesh", "game.player.hurt");
        soundMappings.put("item_break", "random.break");
        soundMappings.put("item_pickup", "random.pop");
        soundMappings.put("lava", "liquid.lava");
        soundMappings.put("lava_pop", "liquid.lavapop");
        soundMappings.put("level_up", "random.levelup");
        soundMappings.put("minecart_base", "minecart.base");
        soundMappings.put("minecart_inside", "minecart.inside");
        soundMappings.put("music_game", "music.game");
        soundMappings.put("music_creative", "music.game.creative");
        soundMappings.put("music_end", "music.game.end");
        soundMappings.put("music_credits", "music.game.end.credits");
        soundMappings.put("music_dragon", "music.game.end.dragon");
        soundMappings.put("music_nether", "music.game.nether");
        soundMappings.put("music_menu", "music.menu");
        soundMappings.put("note_bass", "note.bass");
        soundMappings.put("note_piano", "note.harp");
        soundMappings.put("note_bass_drum", "note.bd");
        soundMappings.put("note_sticks", "note.hat");
        soundMappings.put("note_bass_guitar", "note.bassattack");
        soundMappings.put("note_snare_drum", "note.snare");
        soundMappings.put("note_pling", "note.pling");
        soundMappings.put("orb_pickup", "random.orb");
        soundMappings.put("piston_extend", "tile.piston.out");
        soundMappings.put("piston_retract", "tile.piston.in");
        soundMappings.put("portal", "portal.portal");
        soundMappings.put("portal_travel", "portal.travel");
        soundMappings.put("portal_trigger", "portal.trigger");
        soundMappings.put("potion_smash", "game.potion.smash");
        soundMappings.put("records_11", "records.11");
        soundMappings.put("records_13", "records.13");
        soundMappings.put("records_blocks", "records.blocks");
        soundMappings.put("records_cat", "records.cat");
        soundMappings.put("records_chirp", "records.chirp");
        soundMappings.put("records_far", "records.far");
        soundMappings.put("records_mall", "records.mall");
        soundMappings.put("records_mellohi", "records.mellohi");
        soundMappings.put("records_stal", "records.stal");
        soundMappings.put("records_strad", "records.strad");
        soundMappings.put("records_wait", "records.wait");
        soundMappings.put("records_ward", "records.ward");
        soundMappings.put("shoot_arrow", "random.bow");
        soundMappings.put("splash", "random.splash");
        soundMappings.put("splash2", "game.player.swim.splash");
        soundMappings.put("step_grass", "step.grass");
        soundMappings.put("step_gravel", "step.gravel");
        soundMappings.put("step_ladder", "step.ladder");
        soundMappings.put("step_sand", "step.sand");
        soundMappings.put("step_snow", "step.snow");
        soundMappings.put("step_stone", "step.stone");
        soundMappings.put("step_wood", "step.wood");
        soundMappings.put("step_wool", "step.cloth");
        soundMappings.put("swim", "game.player.swim");
        soundMappings.put("water", "liquid.water");
        soundMappings.put("wood_click", "random.wood_click");
        soundMappings.put("bat_death", "mob.bat.death");
        soundMappings.put("bat_hurt", "mob.bat.hurt");
        soundMappings.put("bat_idle", "mob.bat.idle");
        soundMappings.put("bat_loop", "mob.bat.loop");
        soundMappings.put("bat_takeoff", "mob.bat.takeoff");
        soundMappings.put("blaze_breath", "mob.blaze.breathe");
        soundMappings.put("blaze_death", "mob.blaze.death");
        soundMappings.put("blaze_hit", "mob.blaze.hit");
        soundMappings.put("cat_hiss", "mob.cat.hiss");
        soundMappings.put("cat_hit", "mob.cat.hitt");
        soundMappings.put("cat_meow", "mob.cat.meow");
        soundMappings.put("cat_purr", "mob.cat.purr");
        soundMappings.put("cat_purreow", "mob.cat.purreow");
        soundMappings.put("chicken_idle", "mob.chicken.say");
        soundMappings.put("chicken_hurt", "mob.chicken.hurt");
        soundMappings.put("chicken_egg_pop", "mob.chicken.plop");
        soundMappings.put("chicken_walk", "mob.chicken.step");
        soundMappings.put("cow_idle", "mob.cow.say");
        soundMappings.put("cow_hurt", "mob.cow.hurt");
        soundMappings.put("cow_walk", "mob.cow.step");
        soundMappings.put("creeper_hiss", "creeper.primed");
        soundMappings.put("creeper_hit", "mob.creeper.say");
        soundMappings.put("creeper_death", "mob.creeper.death");
        soundMappings.put("enderdragon_death", "mob.enderdragon.end");
        soundMappings.put("enderdragon_growl", "mob.enderdragon.growl");
        soundMappings.put("enderdragon_hit", "mob.enderdragon.hit");
        soundMappings.put("enderdragon_wings", "mob.enderdragon.wings");
        soundMappings.put("enderman_death", "mob.endermen.death");
        soundMappings.put("enderman_hit", "mob.endermen.hit");
        soundMappings.put("enderman_idle", "mob.endermen.idle");
        soundMappings.put("enderman_teleport", "mob.endermen.portal");
        soundMappings.put("enderman_scream", "mob.endermen.scream");
        soundMappings.put("enderman_stare", "mob.endermen.stare");
        soundMappings.put("ghast_scream", "mob.ghast.scream");
        soundMappings.put("ghast_scream2", "mob.ghast.affectionate_scream");
        soundMappings.put("ghast_charge", "mob.ghast.charge");
        soundMappings.put("ghast_death", "mob.ghast.death");
        soundMappings.put("ghast_fireball", "mob.ghast.fireball");
        soundMappings.put("ghast_moan", "mob.ghast.moan");
        soundMappings.put("guardian_idle", "mob.guardian.idle");
        soundMappings.put("guardian_attack", "mob.guardian.attack");
        soundMappings.put("guardian_curse", "mob.guardian.curse");
        soundMappings.put("guardian_flop", "mob.guardian.flop");
        soundMappings.put("guardian_elder_idle", "mob.guardian.elder.idle");
        soundMappings.put("guardian_land_idle", "mob.guardian.land.idle");
        soundMappings.put("guardian_hit", "mob.guardian.hit");
        soundMappings.put("guardian_elder_hit", "mob.guardian.elder.hit");
        soundMappings.put("guardian_land_hit", "mob.guardian.land.hit");
        soundMappings.put("guardian_death", "mob.guardian.death");
        soundMappings.put("guardian_elder_death", "mob.guardian.elder.death");
        soundMappings.put("guardian_land_death", "mob.guardian.land.death");
        soundMappings.put("hostile_death", "game.hostile.die");
        soundMappings.put("hostile_hurt", "game.hostile.hurt");
        soundMappings.put("hostile_fall_big", "game.hostile.hurt.fall.big");
        soundMappings.put("hostile_fall_small", "game.hostile.hurt.fall.small");
        soundMappings.put("hostile_swim", "game.hostile.swim");
        soundMappings.put("hostile_splash", "game.hostile.swim.splash");
        soundMappings.put("irongolem_death", "mob.irongolem.death");
        soundMappings.put("irongolem_hit", "mob.irongolem.hit");
        soundMappings.put("irongolem_throw", "mob.irongolem.throw");
        soundMappings.put("irongolem_walk", "mob.irongolem.walk");
        soundMappings.put("magmacube_walk", "mob.magmacube.big");
        soundMappings.put("magmacube_walk2", "mob.magmacube.small");
        soundMappings.put("magmacube_jump", "mob.magmacube.jump");
        soundMappings.put("neutral_death", "game.neutral.die");
        soundMappings.put("neutral_hurt", "game.neutral.hurt");
        soundMappings.put("neutral_fall_big", "game.neutral.hurt.fall.big");
        soundMappings.put("neutral_fall_small", "game.neutral.hurt.fall.small");
        soundMappings.put("neutral_swim", "game.neutral.swim");
        soundMappings.put("neutral_splash", "game.neutral.swim.splash");
        soundMappings.put("pig_idle", "mob.pig.say");
        soundMappings.put("pig_death", "mob.pig.death");
        soundMappings.put("pig_walk", "mob.pig.step");
        soundMappings.put("player_death", "game.player.die");
        soundMappings.put("rabbit_idle", "mob.rabbit.idle");
        soundMappings.put("rabbit_hurt", "mob.rabbit.hurt");
        soundMappings.put("rabbit_hop", "mob.rabbit.hop");
        soundMappings.put("rabbit_death", "mob.rabbit.death");
        soundMappings.put("sheep_idle", "mob.sheep.say");
        soundMappings.put("sheep_shear", "mob.sheep.shear");
        soundMappings.put("sheep_walk", "mob.sheep.step");
        soundMappings.put("silverfish_hit", "mob.silverfish.hit");
        soundMappings.put("silverfish_death", "mob.silverfish.kill");
        soundMappings.put("silverfish_idle", "mob.silverfish.say");
        soundMappings.put("silverfish_walk", "mob.silverfish.step");
        soundMappings.put("skeleton_idle", "mob.skeleton.say");
        soundMappings.put("skeleton_death", "mob.skeleton.death");
        soundMappings.put("skeleton_hurt", "mob.skeleton.hurt");
        soundMappings.put("skeleton_walk", "mob.skeleton.step");
        soundMappings.put("slime_attack", "mob.slime.attack");
        soundMappings.put("slime_walk", "mob.slime.big");
        soundMappings.put("slime_walk2", "mob.slime.small");
        soundMappings.put("spider_idle", "mob.spider.say");
        soundMappings.put("spider_death", "mob.spider.death");
        soundMappings.put("spider_walk", "mob.spider.step");
        soundMappings.put("wither_death", "mob.wither.death");
        soundMappings.put("wither_hurt", "mob.wither.hurt");
        soundMappings.put("wither_idle", "mob.wither.idle");
        soundMappings.put("wither_shoot", "mob.wither.shoot");
        soundMappings.put("wither_spawn", "mob.wither.spawn");
        soundMappings.put("wolf_bark", "mob.wolf.bark");
        soundMappings.put("wolf_death", "mob.wolf.death");
        soundMappings.put("wolf_growl", "mob.wolf.growl");
        soundMappings.put("wolf_howl", "mob.wolf.howl");
        soundMappings.put("wolf_hurt", "mob.wolf.hurt");
        soundMappings.put("wolf_pant", "mob.wolf.panting");
        soundMappings.put("wolf_shake", "mob.wolf.shake");
        soundMappings.put("wolf_walk", "mob.wolf.step");
        soundMappings.put("wolf_whine", "mob.wolf.whine");
        soundMappings.put("zombie_metal", "mob.zombie.metal");
        soundMappings.put("zombie_wood", "mob.zombie.wood");
        soundMappings.put("zombie_woodbreak", "mob.zombie.woodbreak");
        soundMappings.put("zombie_idle", "mob.zombie.say");
        soundMappings.put("zombie_death", "mob.zombie.death");
        soundMappings.put("zombie_hurt", "mob.zombie.hurt");
        soundMappings.put("zombie_infect", "mob.zombie.infect");
        soundMappings.put("zombie_unfect", "mob.zombie.unfect");
        soundMappings.put("zombie_remedy", "mob.zombie.remedy");
        soundMappings.put("zombie_walk", "mob.zombie.step");
        soundMappings.put("zombie_pig_idle", "mob.zombiepig.zpig");
        soundMappings.put("zombie_pig_angry", "mob.zombiepig.zpigangry");
        soundMappings.put("zombie_pig_death", "mob.zombiepig.zpigdeath");
        soundMappings.put("zombie_pig_hurt", "mob.zombiepig.zpighurt");
        soundMappings.put("dig_wool", "dig.cloth");
        soundMappings.put("dig_grass", "dig.grass");
        soundMappings.put("dig_gravel", "dig.gravel");
        soundMappings.put("dig_sand", "dig.sand");
        soundMappings.put("dig_snow", "dig.snow");
        soundMappings.put("dig_stone", "dig.stone");
        soundMappings.put("dig_wood", "dig.wood");
        soundMappings.put("firework_blast", "fireworks.blast");
        soundMappings.put("firework_blast2", "fireworks.blast_far");
        soundMappings.put("firework_large_blast", "fireworks.largeblast");
        soundMappings.put("firework_large_blast2", "fireworks.largeblast_far");
        soundMappings.put("firework_twinkle", "fireworks.twinkle");
        soundMappings.put("firework_twinkle2", "fireworks.twinkle_far");
        soundMappings.put("firework_launch", "fireworks.launch");
        soundMappings.put("successful_hit", "random.successful_hit");
        soundMappings.put("horse_angry", "mob.horse.angry");
        soundMappings.put("horse_armor", "mob.horse.armor");
        soundMappings.put("horse_breathe", "mob.horse.breathe");
        soundMappings.put("horse_death", "mob.horse.death");
        soundMappings.put("horse_gallop", "mob.horse.gallop");
        soundMappings.put("horse_hit", "mob.horse.hit");
        soundMappings.put("horse_idle", "mob.horse.idle");
        soundMappings.put("horse_jump", "mob.horse.jump");
        soundMappings.put("horse_land", "mob.horse.land");
        soundMappings.put("horse_saddle", "mob.horse.leather");
        soundMappings.put("horse_soft", "mob.horse.soft");
        soundMappings.put("horse_wood", "mob.horse.wood");
        soundMappings.put("donkey_angry", "mob.horse.donkey.angry");
        soundMappings.put("donkey_death", "mob.horse.donkey.death");
        soundMappings.put("donkey_hit", "mob.horse.donkey.hit");
        soundMappings.put("donkey_idle", "mob.horse.donkey.idle");
        soundMappings.put("horse_skeleton_death", "mob.horse.skeleton.death");
        soundMappings.put("horse_skeleton_hit", "mob.horse.skeleton.hit");
        soundMappings.put("horse_skeleton_idle", "mob.horse.skeleton.idle");
        soundMappings.put("horse_zombie_death", "mob.horse.zombie.death");
        soundMappings.put("horse_zombie_hit", "mob.horse.zombie.hit");
        soundMappings.put("horse_zombie_idle", "mob.horse.zombie.idle");
        soundMappings.put("villager_death", "mob.villager.death");
        soundMappings.put("villager_haggle", "mob.villager.haggle");
        soundMappings.put("villager_hit", "mob.villager.hit");
        soundMappings.put("villager_idle", "mob.villager.idle");
        soundMappings.put("villager_no", "mob.villager.no");
        soundMappings.put("villager_yes", "mob.villager.yes");
        Map<String, SoundType> mappings = Maps.newHashMap();
        for (Entry<String, String> en : soundMappings.entrySet()) {
            SoundType soundType = new LanternSoundType(en.getValue());
            mappings.put(en.getKey(), soundType);
            this.soundTypeRegistry.register(soundType);
        }
        RegistryHelper.mapFields(SoundTypes.class, mappings);
    }

    private void registerNotePitches() {
        final List<LanternNotePitch> entries = Lists.newArrayList();
        RegistryHelper.mapFields(NotePitches.class, new Function<String, NotePitch>() {
            int counter;
            @Override
            public NotePitch apply(String input) {
                LanternNotePitch notePitch = new LanternNotePitch(input, this.counter++);
                notePitchRegistry.register(notePitch);
                entries.add(notePitch);
                return notePitch;
            }
        });
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).setNext(entries.get((i + 1) % entries.size()));
        }
    }

    private void registerBlockTypes() {
        this.blockRegistry.register(0, new BlockAir("minecraft:air"));
        this.blockRegistry.register(1, new BlockStone("minecraft:stone"),
                state -> state.getTraitValue(BlockStone.TYPE).get().getInternalId());
        this.blockRegistry.register(2, new BlockGrass("minecraft:grass"));
        this.blockRegistry.register(3, new BlockDirt("minecraft:dirt"),
                state -> state.getTraitValue(BlockDirt.TYPE).get().getInternalId());
        this.blockRegistry.register(7, new BlockBedrock("minecraft:bedrock"));

        Map<String, BlockType> mappings = Maps.newHashMap();
        for (BlockType blockType : this.blockRegistry.getAll()) {
            String id = blockType.getId();
            mappings.put(id.replaceFirst("minecraft:", ""), blockType);
        }
        RegistryHelper.mapFields(BlockTypes.class, mappings);
    }

    /**
     * Gets the {@link CatalogTypeRegistry<WorldGeneratorModifier>}.
     * 
     * @return the world generator modifier registry
     */
    public CatalogTypeRegistry<WorldGeneratorModifier> getWorldGeneratorModifierRegistry() {
        return this.worldGeneratorModifierRegistry;
    }

    /**
     * Gets the {@link CatalogTypeRegistry<Attribute>}.
     * 
     * @return the attribute registry
     */
    public CatalogTypeRegistry<LanternAttribute> getAttributeRegistry() {
        return this.attributeRegistry;
    }

    /**
     * Gets the {@link LanternBlockRegistry}.
     * 
     * @return the block registry
     */
    public LanternBlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    /**
     * Gets the {@link LanternItemRegistry}.
     * 
     * @return the item registry
     */
    public LanternItemRegistry getItemRegistry() {
        return this.itemRegistry;
    }

    /**
     * Gets the {@link LanternBiomeRegistry}.
     * 
     * @return the biome registry
     */
    public LanternBiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    /**
     * Gets the {@link ResourcePackFactory}.
     * 
     * @return the resource pack factory
     */
    public LanternResourcePackFactory getResourcePackFactory() {
        return this.resourcePackFactory;
    }

    /**
     * Gets the {@link TranslationManager}.
     * 
     * @return the translation manager
     */
    public TranslationManager getTranslationManager() {
        return this.translationManager;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Optional<T> getType(Class<T> typeClass, String id) {
        if (this.catalogTypeRegistries.containsKey(typeClass)) {
            return (Optional<T>) this.catalogTypeRegistries.get(typeClass).get(id);
        }
        return Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends CatalogType> Collection<T> getAllOf(Class<T> typeClass) {
        if (this.catalogTypeRegistries.containsKey(typeClass)) {
            return (Collection<T>) this.catalogTypeRegistries.get(typeClass).getAll();
        }
        return ImmutableList.of();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ResettableBuilder<? super T>> T createBuilder(Class<T> builderClass) throws IllegalArgumentException {
        if (this.builderFactories.containsKey(builderClass)) {
            return (T) this.builderFactories.get(builderClass).get();
        }
        throw new IllegalArgumentException();
    }

    public LanternAttributeCalculator getAttributeCalculator() {
        return this.attributeCalculator;
    }

    @Override
    public Collection<String> getDefaultGameRules() {
        return this.defaultGameRules;
    }

    @Override
    public Optional<EntityStatistic> getEntityStatistic(StatisticGroup statisticGroup, EntityType entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ItemStatistic> getItemStatistic(StatisticGroup statisticGroup, ItemType itemType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<BlockStatistic> getBlockStatistic(StatisticGroup statisticGroup, BlockType blockType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<TeamStatistic> getTeamStatistic(StatisticGroup statisticGroup, TextColor teamColor) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<Statistic> getStatistics(StatisticGroup statisticGroup) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerStatistic(Statistic stat) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Optional<Rotation> getRotationFromDegree(int degrees) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public GameProfile createGameProfile(UUID uuid, String name) {
        return new LanternGameProfile(uuid, name);
    }

    @Override
    public Favicon loadFavicon(String raw) throws IOException {
        return LanternFavicon.load(raw);
    }

    @Override
    public Favicon loadFavicon(Path path) throws IOException {
        return LanternFavicon.load(path);
    }

    @Override
    public Favicon loadFavicon(URL url) throws IOException {
        return LanternFavicon.load(url);
    }

    @Override
    public Favicon loadFavicon(InputStream in) throws IOException {
        return LanternFavicon.load(in);
    }

    @Override
    public Favicon loadFavicon(BufferedImage image) throws IOException {
        return LanternFavicon.load(image);
    }

    @Override
    public RecipeRegistry getRecipeRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ImmutableDataRegistry getImmutableDataRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<ResourcePack> getResourcePackById(String id) {
        return this.resourcePackFactory.getById(id);
    }

    @Override
    public Optional<DisplaySlot> getDisplaySlotForColor(TextColor color) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerWorldGeneratorModifier(WorldGeneratorModifier modifier) {
        this.worldGeneratorModifierRegistry.register(modifier);
    }

    @Override
    public PopulatorFactory getPopulatorFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Translation> getTranslationById(String id) {
        return this.translationManager.getIfPresent(id);
    }

    public LanternAttributeModifierBuilder createAttributeModifierBuilder() {
        return new LanternAttributeModifierBuilder();
    }

    public LanternAttributeBuilder createAttributeBuilder() {
        return new LanternAttributeBuilder(this.attributeRegistry);
    }

    @Override
    public DataManipulatorRegistry getManipulatorRegistry() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExtentBufferFactory getExtentBufferFactory() {
        return LanternExtentBufferFactory.INSTANCE;
    }
}
