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
package org.lanternpowered.server.game.registry.type.block;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.block.PropertyProviderCollections.INSTANT_BROKEN;
import static org.lanternpowered.server.block.PropertyProviderCollections.PASSABLE;
import static org.lanternpowered.server.block.PropertyProviders.blastResistance;
import static org.lanternpowered.server.block.PropertyProviders.flammableInfo;
import static org.lanternpowered.server.block.PropertyProviders.hardness;
import static org.lanternpowered.server.block.PropertyProviders.lightEmission;
import static org.lanternpowered.server.block.PropertyProviders.replaceable;
import static org.lanternpowered.server.item.PropertyProviders.equipmentType;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ShortMap;
import it.unimi.dsi.fastutil.objects.Object2ShortOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import org.lanternpowered.server.block.BlockTypeBuilder;
import org.lanternpowered.server.block.BlockTypeBuilderImpl;
import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.PropertyProviderCollections;
import org.lanternpowered.server.block.TranslationProvider;
import org.lanternpowered.server.block.aabb.BoundingBoxes;
import org.lanternpowered.server.block.behavior.simple.BlockSnapshotProviderPlaceBehavior;
import org.lanternpowered.server.block.behavior.simple.SimpleBlockDropsProviderBehavior;
import org.lanternpowered.server.block.behavior.simple.SimpleBreakBehavior;
import org.lanternpowered.server.block.behavior.simple.SimplePlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ChestInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.ChestPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.EnderChestInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.HopperPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.HorizontalRotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.LogAxisRotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.OpeneableContainerInteractionBehavior;
import org.lanternpowered.server.block.behavior.vanilla.OppositeFaceDirectionalPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.QuartzLinesRotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.RotationPlacementBehavior;
import org.lanternpowered.server.block.behavior.vanilla.TorchPlacementBehavior;
import org.lanternpowered.server.block.extended.SnowyExtendedBlockStateProvider;
import org.lanternpowered.server.block.state.LanternBlockState;
import org.lanternpowered.server.block.trait.LanternBooleanTraits;
import org.lanternpowered.server.block.trait.LanternEnumTraits;
import org.lanternpowered.server.block.trait.LanternIntegerTraits;
import org.lanternpowered.server.block.translation.SpongeTranslationProvider;
import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.data.type.LanternBedPart;
import org.lanternpowered.server.data.type.LanternBigMushroomType;
import org.lanternpowered.server.data.type.LanternBrickType;
import org.lanternpowered.server.data.type.LanternDirtType;
import org.lanternpowered.server.data.type.LanternDisguisedBlockType;
import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.data.type.LanternPlantType;
import org.lanternpowered.server.data.type.LanternPortionType;
import org.lanternpowered.server.data.type.LanternPrismarineType;
import org.lanternpowered.server.data.type.LanternQuartzType;
import org.lanternpowered.server.data.type.LanternRailDirection;
import org.lanternpowered.server.data.type.LanternSandType;
import org.lanternpowered.server.data.type.LanternSandstoneType;
import org.lanternpowered.server.data.type.LanternShrubType;
import org.lanternpowered.server.data.type.LanternSlabType;
import org.lanternpowered.server.data.type.LanternStoneType;
import org.lanternpowered.server.data.type.LanternTreeType;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.game.registry.type.data.KeyRegistryModule;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.game.registry.type.item.inventory.equipment.EquipmentTypeRegistryModule;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.lanternpowered.server.item.behavior.vanilla.SlabItemInteractionBehavior;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntityTypes;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SlabType;
import org.spongepowered.api.data.type.TreeType;
import org.spongepowered.api.item.inventory.equipment.EquipmentTypes;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.util.Direction;

import java.util.Optional;
import java.util.function.Supplier;

@RegistrationDependency({
        KeyRegistryModule.class,
        EquipmentTypeRegistryModule.class
})
public final class BlockRegistryModule extends AdditionalPluginCatalogRegistryModule<BlockType> implements BlockRegistry {

    private static final BlockRegistryModule INSTANCE = new BlockRegistryModule();

    public static BlockRegistryModule get() {
        return INSTANCE;
    }

    private final Short2ObjectMap<BlockType> blockTypeByInternalId = new Short2ObjectOpenHashMap<>();
    private final Object2ShortMap<BlockType> internalIdByBlockType = new Object2ShortOpenHashMap<>();

    private final Short2ObjectMap<BlockState> blockStateByPackedType = new Short2ObjectOpenHashMap<>();
    private final Object2ShortMap<BlockState> packedTypeByBlockState = new Object2ShortOpenHashMap<>();

    // The counter for custom block ids. (Non vanilla ones.)
    private int blockIdCounter = 1024;

    public BlockRegistryModule() {
        super(BlockTypes.class);
    }

    @Override
    public int getBlockStatesCount() {
        return this.blockStateByPackedType.size();
    }

    private void register0(int internalId, LanternBlockType blockType, BlockState2DataFunction stateToDataConverter) {
        checkNotNull(stateToDataConverter, "stateToDataConverter");
        checkState(internalId >= 0, "The internal id cannot be negative: %s", internalId);
        checkState(internalId <= 0xfff, "The internal id exceeded the internal id limit: %s > %s", internalId, 0xfff);
        final short internalId0 = (short) internalId;
        checkState(!this.blockTypeByInternalId.containsKey(internalId0), "The internal id is already used: %s", internalId);
        super.register(blockType);
        this.blockTypeByInternalId.put(internalId0, blockType);
        this.internalIdByBlockType.put(blockType, internalId0);
        Byte2ObjectMap<BlockState> usedValues = new Byte2ObjectOpenHashMap<>();
        int internalStateIdBase = (internalId & 0xfff) << 4;
        for (BlockState blockState : blockType.getBlockStateBase().getBlockStates()) {
            if (((LanternBlockState) blockState).isExtended()) {
                continue;
            }
            byte value = checkNotNull(stateToDataConverter.apply(blockState));
            if (usedValues.containsKey(value)) {
                throw new IllegalStateException("The data value " + value + " for state '" + blockState.getId() +
                        "' is already used by '" + usedValues.get(value).getId() + "'");
            }
            usedValues.put(value, blockState);
            final short internalStateId =  (short) (internalStateIdBase | value & 0xf);
            this.blockStateByPackedType.put(internalStateId, blockState);
            this.packedTypeByBlockState.put(blockState, internalStateId);
        }
        final BlockState defaultBlockState = blockType.getDefaultState();
        for (byte b = 0; b <= 0xf; b++) {
            if (!usedValues.containsKey(b)) {
                final short internalStateId = (short) (internalStateIdBase | b & 0xf);
                this.blockStateByPackedType.put(internalStateId, defaultBlockState);
            }
        }
        for (BlockState blockState : blockType.getBlockStateBase().getBlockStates()) {
            if (!((LanternBlockState) blockState).isExtended()) {
                continue;
            }
            blockState = blockType.getExtendedBlockStateProvider().remove(blockState);
            this.packedTypeByBlockState.put(blockState, checkNotNull(this.packedTypeByBlockState.get(blockState)));
        }
        final BlockStateRegistryModule blockStateRegistryModule = Lantern.getRegistry()
                .getRegistryModule(BlockStateRegistryModule.class).get();
        blockType.getAllBlockStates().forEach(blockStateRegistryModule::registerState);
        blockType.getItem().ifPresent(itemType -> ItemRegistryModule.get().register(internalId, itemType));
        LanternPropertyRegistry.getInstance().registerBlockPropertyStores(blockType.getPropertyProviderCollection());
    }

    @Override
    public void register(int internalId, BlockType blockType, BlockState2DataFunction stateToDataConverter) {
        register0(internalId, (LanternBlockType) blockType, stateToDataConverter);
    }

    @Override
    public void register(int internalId, BlockType blockType) {
        LanternBlockType blockType0 = (LanternBlockType) checkNotNull(blockType, "blockType");
        checkState(blockType0.getBlockStateBase().getBlockStates().stream()
                        .filter(s -> !((LanternBlockState) s).isExtended()).count() <= 1,
                "You cannot register a blockType with more then one state with this method.");
        register0(internalId, blockType0, blockState -> (byte) 0);
    }

    @Override
    public void register(BlockType blockType, BlockState2DataFunction stateToDataConverter) {
        register(this.nextInternalId(), blockType, stateToDataConverter);
    }

    @Override
    public void register(BlockType catalogType) {
        register(nextInternalId(), catalogType);
    }

    private int nextInternalId() {
        int internalId;
        do {
            internalId = this.blockIdCounter++;
        } while (this.blockTypeByInternalId.containsKey((short) internalId));
        return internalId;
    }

    @Override
    public Optional<BlockState> getStateByInternalId(int internalId) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) ((internalId & 0xfff) << 4)));
    }

    @Override
    public Optional<BlockState> getStateByInternalIdAndData(int internalId, byte data) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) (((internalId & 0xfff) << 4) | (data & 0xf))));
    }

    @Override
    public Optional<BlockState> getStateByTypeAndData(BlockType blockType, byte data) {
        return Optional.ofNullable(this.blockStateByPackedType.get(
                (short) ((this.packedTypeByBlockState.get(blockType.getDefaultState()) & 0xfff0) | (data & 0xf))));
    }

    @Override
    public Optional<BlockState> getStateByInternalIdAndData(int internalIdAndData) {
        return Optional.ofNullable(this.blockStateByPackedType.get((short) (internalIdAndData & 0xffff)));
    }

    @Override
    public byte getStateData(BlockState blockState) {
        return (byte) (this.packedTypeByBlockState.get(checkNotNull(blockState, "blockState")) & 0xf);
    }

    @Override
    public short getStateInternalId(BlockState blockState) {
        return this.internalIdByBlockType.getShort(checkNotNull(blockState, "blockState").getType());
    }

    @Override
    public short getStateInternalIdAndData(BlockState blockState) {
        return this.packedTypeByBlockState.get(checkNotNull(blockState, "blockState"));
    }

    @Override
    public int getPackedVersion(int internalId, byte data) {
        return ((internalId & 0xfff) << 4) | (data & 0xf);
    }

    @Override
    public void registerDefaults() {
        // @formatter:off

        ///////////////////
        ///    Air      ///
        ///////////////////
        register(0, builder()
                        .properties(PropertyProviderCollections.DEFAULT_GAS)
                        .translation("tile.air.name")
                        .build("minecraft", "air"));
        ///////////////////
        ///    Stone    ///
        ///////////////////
        register(1, simpleBuilder()
                        .trait(LanternEnumTraits.STONE_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.STONE_TYPE, LanternStoneType.STONE).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.STONE_TYPE, LanternStoneType.STONE)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(30.0)))
                        .translation(TranslationProvider.of(LanternEnumTraits.STONE_TYPE))
                        .build("minecraft", "stone"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.STONE_TYPE).get().getInternalId());
        ///////////////////
        ///    Grass    ///
        ///////////////////
        register(2, simpleBuilder()
                        .trait(LanternBooleanTraits.SNOWY)
                        .extendedStateProvider(new SnowyExtendedBlockStateProvider())
                        .defaultState(state -> state.withTrait(LanternBooleanTraits.SNOWY, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .translation("tile.grass.name")
                        .build("minecraft", "grass"));
        ///////////////////
        ///    Dirt     ///
        ///////////////////
        register(3, simpleBuilder()
                        .traits(LanternEnumTraits.DIRT_TYPE, LanternBooleanTraits.SNOWY)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.DIRT_TYPE, LanternDirtType.DIRT).get()
                                .withTrait(LanternBooleanTraits.SNOWY, false).get())
                        .extendedStateProvider(new SnowyExtendedBlockStateProvider())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.DIRT_TYPE, LanternDirtType.DIRT)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation(TranslationProvider.of(LanternEnumTraits.DIRT_TYPE))
                        .build("minecraft", "dirt"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.DIRT_TYPE).get().getInternalId());
        ///////////////////
        /// Cobblestone ///
        ///////////////////
        register(4, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(3.0)))
                        .translation("tile.stonebrick.name")
                        .build("minecraft", "cobblestone"));
        ///////////////////
        ///    Planks   ///
        ///////////////////
        register(5, simpleBuilder()
                        .trait(LanternEnumTraits.TREE_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.TREE_TYPE, LanternTreeType.OAK).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.TREE_TYPE, LanternTreeType.OAK)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(5.0))
                                .add(flammableInfo(5, 20)))
                        .translation(TranslationProvider.of(LanternEnumTraits.TREE_TYPE, type ->
                                tr("tile.planks." + type.getTranslationKeyBase() + ".name")))
                        .build("minecraft", "planks"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.TREE_TYPE).get().getInternalId());
        ////////////////////
        ///    Sapling   ///
        ////////////////////
        register(6, simpleBuilder()
                        .traits(LanternEnumTraits.TREE_TYPE, LanternIntegerTraits.SAPLING_GROWTH_STAGE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.TREE_TYPE, LanternTreeType.OAK).get()
                                .withTrait(LanternIntegerTraits.SAPLING_GROWTH_STAGE, 0).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.TREE_TYPE, LanternTreeType.OAK)
                                )
                        )
                        .properties(builder -> builder
                                .add(PASSABLE)
                                .add(INSTANT_BROKEN))
                        .translation(TranslationProvider.of(LanternEnumTraits.TREE_TYPE, type ->
                                tr("tile.sapling." + type.getTranslationKeyBase() + ".name")))
                        .build("minecraft", "sapling"),
                blockState -> {
                    final int type = blockState.getTraitValue(LanternEnumTraits.TREE_TYPE).get().getInternalId();
                    final int stage = blockState.getTraitValue(LanternIntegerTraits.SAPLING_GROWTH_STAGE).get();
                    return (byte) (stage << 3 | type);
                });
        ////////////////////
        ///    Bedrock   ///
        ////////////////////
        register(7, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(PropertyProviderCollections.UNBREAKABLE))
                        .translation("tile.bedrock.name")
                        .build("minecraft", "bedrock"));
        ////////////////////
        ///     Sand     ///
        ////////////////////
        register(12, simpleBuilder()
                        .trait(LanternEnumTraits.SAND_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.SAND_TYPE, LanternSandType.NORMAL).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.SAND_TYPE, LanternSandType.NORMAL)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation(TranslationProvider.of(LanternEnumTraits.SAND_TYPE))
                        .build("minecraft", "sand"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.SAND_TYPE).get().getInternalId());
        // TODO: Sand physics behavior
        ////////////////////
        ///    Gravel    ///
        ////////////////////
        register(13, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .translation("tile.gravel.name")
                        .build("minecraft", "gravel"));
        // TODO: Gravel physics behavior
        ////////////////////
        ///   Gold Ore   ///
        ////////////////////
        register(14, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreGold.name")
                        .build("minecraft", "gold_ore"));
        ////////////////////
        ///   Iron Ore   ///
        ////////////////////
        register(15, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreIron.name")
                        .build("minecraft", "iron_ore"));
        ////////////////////
        ///   Coal Ore   ///
        ////////////////////
        register(16, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreCoal.name")
                        .build("minecraft", "coal_ore"));
        ////////////////////
        ///    Log 1     ///
        ////////////////////
        register(17, logBuilder(LanternEnumTraits.LOG1_TYPE, LanternTreeType.OAK)
                        .build("minecraft", "log"),
                blockState -> logData(blockState, blockState.getTraitValue(LanternEnumTraits.LOG1_TYPE).get().getInternalId()));
        ////////////////////
        ///   Leaves 1   ///
        ////////////////////
        register(18, leavesBuilder(LanternEnumTraits.LEAVES1_TYPE, LanternTreeType.OAK)
                        .build("minecraft", "leaves"),
                blockState -> leavesData(blockState, blockState.getTraitValue(LanternEnumTraits.LEAVES1_TYPE).get().getInternalId()));
        ////////////////////
        ///    Sponge    ///
        ////////////////////
        register(19, simpleBuilder()
                        .trait(LanternBooleanTraits.IS_WET)
                        .defaultState(state -> state.withTrait(LanternBooleanTraits.IS_WET, false).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.IS_WET, false)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .translation(new SpongeTranslationProvider())
                        .build("minecraft", "sponge"),
                blockState -> (byte) (blockState.getTraitValue(LanternBooleanTraits.IS_WET).get() ? 1 : 0));
        ////////////////////
        ///    Glass     ///
        ////////////////////
        register(20, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.3))
                                .add(blastResistance(1.5)))
                        .translation("tile.glass.name")
                        .build("minecraft", "glass"));
        ////////////////////
        ///   Lapis Ore  ///
        ////////////////////
        register(21, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreLapis.name")
                        .build("minecraft", "lapis_ore"));
        ////////////////////
        ///  Lapis Block ///
        ////////////////////
        register(22, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.blockLapis.name")
                        .build("minecraft", "lapis_block"));
        ////////////////////
        ///   Dispenser  ///
        ////////////////////
        register(23, simpleBuilder()
                        .traits(LanternEnumTraits.FACING, LanternBooleanTraits.TRIGGERED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.FACING, Direction.NORTH).get()
                                .withTrait(LanternBooleanTraits.TRIGGERED, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.5))
                                .add(blastResistance(17.5)))
                        // .tileEntityType(() -> TileEntityTypes.DISPENSER)
                        .translation("tile.dispenser.name")
                        .behaviors(pipeline -> pipeline
                                .add(new RotationPlacementBehavior()))
                        .build("minecraft", "dispenser"),
                blockState -> {
                    int data = directionData(blockState.getTraitValue(LanternEnumTraits.FACING).get());
                    if (blockState.getTraitValue(LanternBooleanTraits.TRIGGERED).get()) {
                        data |= 0x8;
                    }
                    return (byte) data;
                });
        ////////////////////
        ///   Sandstone  ///
        ////////////////////
        register(24, simpleBuilder()
                        .trait(LanternEnumTraits.SANDSTONE_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.SANDSTONE_TYPE, LanternSandstoneType.DEFAULT).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.SANDSTONE_TYPE, LanternSandstoneType.DEFAULT)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(4.0)))
                        .translation(TranslationProvider.of(LanternEnumTraits.SANDSTONE_TYPE))
                        .build("minecraft", "sandstone"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.SANDSTONE_TYPE).get().getInternalId());
        ////////////////////
        ///   Noteblock  ///
        ////////////////////
        register(25, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(4.0)))
                        .translation("tile.musicBlock.name")
                        .build("minecraft", "noteblock"));
        ////////////////////
        ///     Bed      ///
        ////////////////////
        register(26, simpleBuilder()
                        .traits(LanternEnumTraits.HORIZONTAL_FACING, LanternEnumTraits.BED_PART, LanternBooleanTraits.OCCUPIED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get()
                                .withTrait(LanternEnumTraits.BED_PART, LanternBedPart.FOOT).get()
                                .withTrait(LanternBooleanTraits.OCCUPIED, false).get())
                        .properties(builder -> builder
                                .add(hardness(0.2))
                                .add(blastResistance(1.0)))
                        .translation("tile.bed.name")
                        .build("minecraft", "bed"),
                blockState -> {
                    final Direction facing = blockState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get();
                    int type = facing == Direction.SOUTH  ? 0 : facing == Direction.WEST ? 1 :
                            facing == Direction.NORTH ? 2 : facing == Direction.EAST ? 3 : -1;
                    checkArgument(type != -1);
                    if (blockState.getTraitValue(LanternBooleanTraits.OCCUPIED).get()) {
                        type |= 0x4;
                    }
                    if (blockState.getTraitValue(LanternEnumTraits.BED_PART).get() == LanternBedPart.HEAD) {
                        type |= 0x8;
                    }
                    return (byte) type;
                });
        //////////////////////
        ///   Golden Rail  ///
        //////////////////////
        register(27, simpleBuilder()
                        .traits(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternBooleanTraits.POWERED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternRailDirection.NORTH_SOUTH).get()
                                .withTrait(LanternBooleanTraits.POWERED, false).get())
                        .itemType()
                        .boundingBox(BoundingBoxes::rail)
                        .properties(builder -> builder
                                .add(PASSABLE)
                                .add(hardness(0.7))
                                .add(blastResistance(3.5)))
                        .translation("tile.goldenRail.name")
                        .build("minecraft", "golden_rail"),
                blockState -> {
                    int type = blockState.getTraitValue(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION).get().getInternalId();
                    if (blockState.getTraitValue(LanternBooleanTraits.POWERED).get()) {
                        type |= 0x8;
                    }
                    return (byte) type;
                });
        ////////////////////////
        ///   Detector Rail  ///
        ////////////////////////
        register(28, simpleBuilder()
                        .traits(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternBooleanTraits.POWERED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternRailDirection.NORTH_SOUTH).get()
                                .withTrait(LanternBooleanTraits.POWERED, false).get())
                        .itemType()
                        .boundingBox(BoundingBoxes::rail)
                        .properties(builder -> builder
                                .add(PASSABLE)
                                .add(hardness(0.7))
                                .add(blastResistance(3.5)))
                        .translation("tile.detectorRail.name")
                        .build("minecraft", "detector_rail"),
                blockState -> {
                    int type = blockState.getTraitValue(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION).get().getInternalId();
                    if (blockState.getTraitValue(LanternBooleanTraits.POWERED).get()) {
                        type |= 0x8;
                    }
                    return (byte) type;
                });
        // TODO: 29
        ///////////////
        ///   Web   ///
        ///////////////
        register(30, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(PASSABLE)
                                .add(hardness(4.0))
                                .add(blastResistance(20.0)))
                        .translation("tile.web.name")
                        .build("minecraft", "web"));
        //////////////////////
        ///   Tall Grass   ///
        //////////////////////
        register(31, simpleBuilder()
                        .traits(LanternEnumTraits.SHRUB_TYPE)
                            .defaultState(state -> state
                                    .withTrait(LanternEnumTraits.SHRUB_TYPE, LanternShrubType.DEAD_BUSH).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.SHRUB_TYPE, LanternShrubType.DEAD_BUSH)))
                        .boundingBox(BoundingBoxes.bush())
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE)
                                .add(replaceable(true)))
                        .translation("tile.tallgrass.name")
                        .build("minecraft", "tallgrass"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.SHRUB_TYPE).get().getInternalId());
        /////////////////////
        ///   Dead Bush   ///
        /////////////////////
        register(32, simpleBuilder()
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE)
                                .add(replaceable(true)))
                        .boundingBox(BoundingBoxes.bush())
                        .itemType()
                        .translation("tile.deadbush.name")
                        .build("minecraft", "deadbush"));
        // TODO: 33
        // TODO: 34
        ///////////////////
        ///     Wool    ///
        ///////////////////
        register(35, dyedBuilder("tile.wool.%s.name")
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(4.0)))
                        .build("minecraft", "wool"),
                this::dyedData);
        // TODO: 36
        /////////////////////////
        ///   Yellow Flower   ///
        /////////////////////////
        register(37, simpleBuilder()
                        .traits(LanternEnumTraits.YELLOW_FLOWER_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.YELLOW_FLOWER_TYPE, LanternPlantType.DANDELION).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.PLANT_TYPE, LanternPlantType.DANDELION)))
                        .boundingBox(BoundingBoxes.bush())
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE))
                        .translation(TranslationProvider.of(LanternEnumTraits.YELLOW_FLOWER_TYPE))
                        .build("minecraft", "yellow_flower"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.YELLOW_FLOWER_TYPE).get().getInternalId());
        //////////////////////
        ///   Red Flower   ///
        //////////////////////
        register(38, simpleBuilder()
                        .traits(LanternEnumTraits.RED_FLOWER_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.RED_FLOWER_TYPE, LanternPlantType.POPPY).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.PLANT_TYPE, LanternPlantType.POPPY)))
                        .boundingBox(BoundingBoxes.bush())
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE))
                        .translation(TranslationProvider.of(LanternEnumTraits.RED_FLOWER_TYPE))
                        .build("minecraft", "red_flower"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.RED_FLOWER_TYPE).get().getInternalId());
        //////////////////////////
        ///   Brown Mushroom   ///
        //////////////////////////
        register(39, simpleBuilder()
                        .boundingBox(BoundingBoxes.bush())
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE)
                                .add(lightEmission(1)))
                        .translation("tile.mushroom.name")
                        .build("minecraft", "brown_mushroom"));
        ////////////////////////
        ///   Red Mushroom   ///
        ////////////////////////
        register(40, simpleBuilder()
                        .boundingBox(BoundingBoxes.bush())
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN)
                                .add(PASSABLE))
                        .translation("tile.mushroom.name")
                        .build("minecraft", "red_mushroom"));
        //////////////////////
        ///   Gold Block   ///
        //////////////////////
        register(41, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(10.0)))
                        .translation("tile.blockGold.name")
                        .build("minecraft", "gold_block"));
        //////////////////////
        ///   Iron Block   ///
        //////////////////////
        register(42, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(10.0)))
                        .translation("tile.blockIron.name")
                        .build("minecraft", "iron_block"));
        ///////////////////////////
        /// Double Stone Slab 1 ///
        ///////////////////////////
        register(43, doubleStoneSlab(LanternEnumTraits.STONE_SLAB1_TYPE, LanternSlabType.STONE)
                        .translation("tile.stoneSlab.name")
                        .build("minecraft", "double_stone_slab"),
                blockState -> doubleStoneSlabData(blockState, blockState.getTraitValue(LanternEnumTraits.STONE_SLAB1_TYPE).get().getInternalId()));
        ////////////////////////
        ///   Stone Slab 1   ///
        ////////////////////////
        register(44, stoneSlab(LanternEnumTraits.STONE_SLAB1_TYPE, LanternSlabType.STONE,
                () -> BlockTypes.STONE_SLAB,
                () -> BlockTypes.DOUBLE_STONE_SLAB)
                        .translation("tile.stoneSlab.name")
                        .boundingBox(BoundingBoxes::slab)
                        .build("minecraft", "stone_slab"),
                blockState -> stoneSlabData(blockState, blockState.getTraitValue(LanternEnumTraits.STONE_SLAB1_TYPE).get().getInternalId()));
        ///////////////////////
        ///   Brick Block   ///
        ///////////////////////
        register(45, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(10.0)))
                        .translation("tile.brick.name")
                        .build("minecraft", "brick_block"));
        ///////////////
        ///   TNT   ///
        ///////////////
        register(46, simpleBuilder()
                        .trait(LanternBooleanTraits.EXPLODE)
                        .defaultState(state -> state
                                .withTrait(LanternBooleanTraits.EXPLODE, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN))
                        .translation("tile.tnt.name")
                        .build("minecraft", "tnt"),
                blockState -> (byte) (blockState.getTraitValue(LanternBooleanTraits.EXPLODE).get() ? 1 : 0));
        /////////////////////
        ///   Bookshelf   ///
        /////////////////////
        register(47, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(7.5)))
                        .translation("tile.bookshelf.name")
                        .build("minecraft", "bookshelf"));
        /////////////////////////////
        ///   Mossy Cobblestone   ///
        /////////////////////////////
        register(48, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(10.0)))
                        .translation("tile.stoneMoss.name")
                        .build("minecraft", "mossy_cobblestone"));
        ////////////////////
        ///   Obsidian   ///
        ////////////////////
        register(49, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(50.0))
                                .add(blastResistance(2000.0)))
                        .translation("tile.obsidian.name")
                        .build("minecraft", "obsidian"));
        /////////////////
        ///   Torch   ///
        /////////////////
        register(50, builder()
                        .trait(LanternEnumTraits.TORCH_FACING)
                            .defaultState(state -> state
                                    .withTrait(LanternEnumTraits.TORCH_FACING, Direction.UP).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(INSTANT_BROKEN))
                        .translation("tile.torch.name")
                        .boundingBox(BoundingBoxes::torch)
                        .behaviors(pipeline -> pipeline
                                .add(new BlockSnapshotProviderPlaceBehavior())
                                .add(new TorchPlacementBehavior())
                                .add(new SimpleBreakBehavior()))
                        .build("minecraft", "torch"),
                blockState -> {
                    final Direction direction = blockState.getTraitValue(LanternEnumTraits.TORCH_FACING).get();
                    switch (direction) {
                        case EAST:
                            return (byte) 1;
                        case WEST:
                            return (byte) 2;
                        case SOUTH:
                            return (byte) 3;
                        case NORTH:
                            return (byte) 4;
                        case UP:
                            return (byte) 5;
                        default:
                            throw new IllegalArgumentException();
                    }
                });
        // TODO: 52 Monster Spawner
        // TODO: 53 Oak Wood Stairs
        ////////////////////
        ///     Chest    ///
        ////////////////////
        register(54, chestBuilder()
                        .translation("tile.chest.name")
                        .build("minecraft", "chest"),
                this::chestData);
        // TODO: 55 Redstone Wire
        ///////////////////////
        ///   Diamond Ore   ///
        ///////////////////////
        register(56, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreDiamond.name")
                        .build("minecraft", "diamond_ore"));
        /////////////////////////
        ///   Diamond Block   ///
        /////////////////////////
        register(57, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.blockDiamond.name")
                        .build("minecraft", "diamond_block"));
        //////////////////////////
        ///   Crafting Table   ///
        //////////////////////////
        register(58, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.5))
                                .add(blastResistance(12.5)))
                        .translation("tile.workbench.name")
                        .build("minecraft", "crafting_table"));
        // TODO: Crafting functionality
        // TODO: 59 Wheat Crops
        ////////////////////
        ///   Farmland   ///
        ////////////////////
        register(60, simpleBuilder()
                .itemType()
                .properties(builder -> builder
                        .add(hardness(0.6))
                        .add(blastResistance(3.0)))
                .translation("tile.farmland.name")
                .build("minecraft", "farmland"));
        // TODO: Trampling of farmland
        // TODO: 61 Furnace
        // TODO: 62 Burning Furnace
        // TODO: 63 Standing Sign Block
        // TODO: 64 Oak Door Block
        // TODO: 65 Ladder
        ///////////////
        ///   Rail  ///
        ///////////////
        register(66, simpleBuilder()
                        .trait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION, LanternRailDirection.NORTH_SOUTH).get())
                        .itemType()
                        .boundingBox(BoundingBoxes::rail)
                        .properties(builder -> builder
                                .add(PASSABLE)
                                .add(hardness(0.7))
                                .add(blastResistance(3.5)))
                        .translation("tile.rail.name")
                        .build("minecraft", "rail"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.STRAIGHT_RAIL_DIRECTION).get().getInternalId());
        // TODO: 67 Cobblestone Stairs
        // TODO: 68 Wall-mounted Sign Block
        // TODO: 69 Lever
        // TODO: 70 Stone Pressure Plate
        // TODO: 71 Iron Door Block
        // TODO: 72 Wooden Pressure Plate
        // TODO: 73 Redstone Ore
        // TODO: 74 Glowing Redstone Ore
        // TODO: 75 Redstone Torch (off)
        // TODO: 76 Redstone Torch (on)
        // TODO: 77 Stone Button
        // TODO: 78 Snow(layer)
        ///////////////
        ///   Ice   ///
        ///////////////
        register(79, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation("tile.ice.name")
                        .build("minecraft", "ice"));
        ////////////////
        ///   Snow   ///
        ////////////////
        register(80, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.2))
                                .add(blastResistance(1.0)))
                        .translation("tile.snow.name")
                        .build("minecraft", "snow"));
        // TODO: 81 Cactus
        ////////////////
        ///   Clay   ///
        ////////////////
        register(82, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.0)))
                        .translation("tile.clay.name")
                        .build("minecraft", "clay"));
        // TODO: 83 Sugar Canes
        //////////////////
        ///   Jukebox  ///
        //////////////////
        register(84, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.jukebox.name")
                        .build("minecraft", "jukebox"));
        ////////////////////
        ///    Pumpkin   ///
        ////////////////////
        register(86, pumpkinBuilder()
                        .itemType(builder -> builder
                                .properties(properties -> properties
                                        .add(equipmentType(EquipmentTypes.HEADWEAR))))
                        .translation("tile.pumpkin.name")
                        .build("minecraft", "pumpkin"),
                this::pumpkinData);
        /////////////////////
        ///   Netherrack  ///
        /////////////////////
        register(87, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.4))
                                .add(blastResistance(2.0)))
                        .translation("tile.hellrock.name")
                        .build("minecraft", "netherrack"));

        // TODO: 88 Soul Sand
        /////////////////////
        ///   Glowstone   ///
        /////////////////////
        register(89, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.3))
                                .add(blastResistance(1.5))
                                .add(lightEmission(15)))
                        .translation("tile.lightgem.name")
                        .build("minecraft", "glowstone"));
        // TODO: 90 Nether Portal
        ////////////////////
        ///  Lit Pumpkin ///
        ////////////////////
        register(91, pumpkinBuilder()
                        .properties(builder -> builder
                                .add(lightEmission(15)))
                        .translation("tile.litpumpkin.name")
                        .build("minecraft", "lit_pumpkin"),
                this::pumpkinData);
        // TODO: 92 Cake Block
        // TODO: 93 Redstone Repeater Block (off)
        // TODO: 94 Redstone Repeater Block (on)
        /////////////////////
        /// Stained Glass ///
        /////////////////////
        register(95, dyedBuilder("tile.stainedGlass.%s.name")
                        .properties(builder -> builder
                                .add(hardness(0.3))
                                .add(blastResistance(1.5)))
                        .build("minecraft", "stained_glass"),
                this::dyedData);
        // TODO: 96 Wooden Trapdoor
        ///////////////////////
        ///   Monster Egg   ///
        ///////////////////////
        register(97, simpleBuilder()
                        .trait(LanternEnumTraits.DISGUISED_BLOCK_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.DISGUISED_BLOCK_TYPE, LanternDisguisedBlockType.STONE).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                    .registerKey(Keys.DISGUISED_BLOCK_TYPE, LanternDisguisedBlockType.STONE)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.75))
                                .add(blastResistance(3.75)))
                        .build("minecraft", "monster_egg"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.DISGUISED_BLOCK_TYPE).get().getInternalId());
        ////////////////////////
        ///   Stone Bricks   ///
        ////////////////////////
        register(98, simpleBuilder()
                        .trait(LanternEnumTraits.BRICK_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.BRICK_TYPE, LanternBrickType.DEFAULT).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer.registerKey(Keys.BRICK_TYPE, LanternBrickType.DEFAULT)))
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(30)))
                        .translation(TranslationProvider.of(LanternEnumTraits.BRICK_TYPE))
                        .build("minecraft", "stonebrick"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.BRICK_TYPE).get().getInternalId());
        ////////////////////////////////
        ///   Brown Mushroom Block   ///
        ////////////////////////////////
        register(99, mushroomBlockBuilder()
                        .build("minecraft", "brown_mushroom_block"),
            this::mushroomData);
        //////////////////////////////
        ///   Red Mushroom Block   ///
        //////////////////////////////
        register(100, mushroomBlockBuilder()
                        .build("minecraft", "red_mushroom_block"),
            this::mushroomData);
        // TODO: 101 Iron Bars
        // TODO: 102 Glass Pane
        // TODO: 103 Melon Block
        // TODO: 104 Pumpkin Stem
        // TODO: 105 Melon Stem
        // TODO: 106 Vines
        // TODO: 107 Oak Fence Gate
        // TODO: 108 Brick Stairs
        ////////////////////
        ///   Mycelium   ///
        ////////////////////
        register(110, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(2.5)))
                        .translation("tile.mycel.name")
                        .build("minecraft", "mycelium"));
        // TODO: 111 Lily Pad
        register(112, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.netherBrick.name")
                        .build("minecraft", "nether_brick"));
        // TODO: 113 Nether Brick Fence
        // TODO: 114 Nether Brick Stairs
        // TODO: 115 Nether Wart
        register(116, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(6000.0)))
                        .translation("tile.enchantmentTable.name")
                        .build("minecraft", "enchanting_table"));
        // TODO: 116 Enchantment Table Functionality/Behavior
        // TODO: 117 Brewing Stand
        // TODO: 118 Cauldron
        // TODO: 119 End Portal
        // TODO: 120 End Portal Frame
        ////////////////////
        ///   End Stone  ///
        ////////////////////
        register(121, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(45.0)))
                        .translation("tile.whiteStone.name")
                        .build("minecraft", "end_stone"));
        // TODO: 122 Dragon Egg
        // TODO: 123 Redstone Lamp (inactive)
        // TODO: 124 Redstone Lamp (active)
        //////////////////////////
        /// Double Wooden Slab ///
        //////////////////////////
        register(125, simpleBuilder()
                        .traits(LanternEnumTraits.TREE_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.TREE_TYPE, LanternTreeType.OAK).get())
                        .translation(TranslationProvider.of(LanternEnumTraits.TREE_TYPE, type ->
                                tr("tile.woodSlab." + type.getTranslationKeyBase() + ".name")))
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.TREE_TYPE, LanternTreeType.OAK)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(5.0)))
                        .build("minecraft", "double_wooden_slab"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.TREE_TYPE).get().getInternalId());
        //////////////////////////
        ///     Wooden Slab    ///
        //////////////////////////
        register(126, simpleBuilder()
                        .traits(LanternEnumTraits.PORTION_TYPE, LanternEnumTraits.TREE_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.PORTION_TYPE, LanternPortionType.BOTTOM).get()
                                .withTrait(LanternEnumTraits.TREE_TYPE, LanternTreeType.OAK).get())
                        .translation(TranslationProvider.of(LanternEnumTraits.TREE_TYPE, type ->
                                tr("tile.woodSlab." + type.getTranslationKeyBase() + ".name")))
                        .itemType(builder -> builder
                                .behaviors(pipeline -> pipeline
                                        .add(new SlabItemInteractionBehavior<>(LanternEnumTraits.TREE_TYPE,
                                                () -> BlockTypes.WOODEN_SLAB,
                                                () -> BlockTypes.DOUBLE_WOODEN_SLAB)))
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.TREE_TYPE, LanternTreeType.OAK)
                                )
                        )
                        .boundingBox(BoundingBoxes::slab)
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(5.0)))
                        .build("minecraft", "wooden_slab"),
                blockState -> {
                    final int type = blockState.getTraitValue(LanternEnumTraits.TREE_TYPE).get().getInternalId();
                    final int portion = (byte) blockState.getTraitValue(LanternEnumTraits.PORTION_TYPE).get().getInternalId();
                    return (byte) (portion << 3 | type);
                });
        // TODO: 127 Cocoa
        // TODO: 128 Sandstone Stairs
        ///////////////////////
        ///   Emerald Ore   ///
        ///////////////////////
        register(129, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.oreEmerald.name")
                        .build("minecraft", "emerald_ore"));
        /////////////////////
        ///  Ender Chest  ///
        /////////////////////
        register(130, simpleBuilder()
                        .trait(LanternEnumTraits.HORIZONTAL_FACING)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                        .itemType()
                        .tileEntityType(() -> TileEntityTypes.ENDER_CHEST)
                        .properties(builder -> builder
                                .add(hardness(22.5))
                                .add(blastResistance(3000.0))
                                .add(lightEmission(7)))
                        .translation("tile.enderChest.name")
                        .boundingBox(BoundingBoxes.chest())
                        .behaviors(pipeline -> pipeline
                                .add(new HorizontalRotationPlacementBehavior())
                                .add(new EnderChestInteractionBehavior()))
                        .build("minecraft", "ender_chest"),
                this::chestData);
        // TODO: 131 Tripwire Hook
        // TODO: 131 Tripwire
        /////////////////////////
        ///   Emerald Block   ///
        /////////////////////////
        register(133, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.blockEmerald.name")
                        .build("minecraft", "emerald_block"));
        // TODO: 134 Spruce Wood Stairs
        // TODO: 135 Birch Wood Stairs
        // TODO: 136 Jungle Wood Stairs
        // TODO: 137 Command Block
        // TODO: 138 Beacon
        // TODO: 139 Cobblestone Wall
        // TODO: 140 Flower Pot
        // TODO: 141 Carrots
        // TODO: 142 Potatoes
        // TODO: 143 Wooden Button
        // TODO: 144 Mob Head
        // TODO: 145 Anvil
        /////////////////////
        /// Trapped Chest ///
        /////////////////////
        register(146, chestBuilder()
                        .translation("tile.chestTrap.name")
                        .build("minecraft", "trapped_chest"),
                this::chestData);
        // TODO: 147 Weighted Pressure Plate (light)
        // TODO: 148 Weighted Pressure Plate (heavy)
        // TODO: 149 Redstone Comparator (inactive)
        // TODO: 150 Redstone Comparator (active)
        // TODO: 151 Daylight Sensor
        ///////////////////////
        /// Redstone Block  ///
        ///////////////////////
        register(152, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.blockRedstone.name")
                        .build("minecraft", "redstone_block"));
        ////////////////////
        ///  Quartz Ore  ///
        ////////////////////
        register(153, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(15.0)))
                        .translation("tile.netherquartz.name")
                        .build("minecraft", "quartz_ore"));
        ////////////////////
        ///     Hopper   ///
        ////////////////////
        register(154, simpleBuilder()
                        .traits(LanternEnumTraits.HOPPER_FACING, LanternBooleanTraits.ENABLED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.HOPPER_FACING, Direction.DOWN).get()
                                .withTrait(LanternBooleanTraits.ENABLED, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.0))
                                .add(blastResistance(8.0)))
                        .translation("tile.hopper.name")
                        .behaviors(pipeline -> pipeline
                                .add(new HopperPlacementBehavior()))
                        .build("minecraft", "hopper"),
                blockState -> {
                    int data = directionData(blockState.getTraitValue(LanternEnumTraits.HOPPER_FACING).get());
                    if (!blockState.getTraitValue(LanternBooleanTraits.ENABLED).get()) {
                        data |= 0x8;
                    }
                    return (byte) data;
                });
        //////////////////////
        ///  Quartz Block  ///
        //////////////////////
        register(155, simpleBuilder()
                        .trait(LanternEnumTraits.QUARTZ_TYPE)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.QUARTZ_TYPE, LanternQuartzType.DEFAULT).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.QUARTZ_TYPE, LanternQuartzType.DEFAULT)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(2.4)))
                        .translation(TranslationProvider.of(LanternEnumTraits.QUARTZ_TYPE))
                        .behaviors(pipeline -> pipeline
                                .add(new QuartzLinesRotationPlacementBehavior()))
                        .build("minecraft", "quartz_block"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.QUARTZ_TYPE).get().getInternalId());
        // TODO: 156 Quartz Stairs
        // TODO: 157 Activator Rail
        ////////////////////
        ///    Dropper   ///
        ////////////////////
        register(158, simpleBuilder()
                        .traits(LanternEnumTraits.FACING, LanternBooleanTraits.TRIGGERED)
                        .defaultState(state -> state
                                .withTrait(LanternEnumTraits.FACING, Direction.NORTH).get()
                                .withTrait(LanternBooleanTraits.TRIGGERED, false).get())
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(3.5))
                                .add(blastResistance(17.5)))
                        // .tileEntityType(() -> TileEntityTypes.DROPPER)
                        .translation("tile.dropper.name")
                        .behaviors(pipeline -> pipeline
                                .add(new RotationPlacementBehavior()))
                        .build("minecraft", "dropper"),
                blockState -> {
                    int data = directionData(blockState.getTraitValue(LanternEnumTraits.FACING).get());
                    if (blockState.getTraitValue(LanternBooleanTraits.TRIGGERED).get()) {
                        data |= 0x8;
                    }
                    return (byte) data;
                });
        //////////////////////////////
        /// Stained Hardended Clay ///
        //////////////////////////////
        register(159, dyedBuilder("tile.clayHardenedStained.%s.name")
                        .properties(builder -> builder
                                .add(hardness(1.25))
                                .add(blastResistance(7.0)))
                        .build("minecraft", "stained_hardened_clay"),
                this::dyedData);
        //////////////////////////
        /// Stained Glass Pane ///
        //////////////////////////
        register(160, dyedBuilder("tile.thinStainedGlass.%s.name")
                        .properties(builder -> builder
                                .add(hardness(0.3))
                                .add(blastResistance(1.5)))
                        .build("minecraft", "stained_glass_pane"),
                this::dyedData);
        ////////////////////
        ///   Leaves 2   ///
        ////////////////////
        register(161, leavesBuilder(LanternEnumTraits.LEAVES2_TYPE, LanternTreeType.ACACIA)
                        .build("minecraft", "leaves2"),
                blockState -> leavesData(blockState, blockState.getTraitValue(LanternEnumTraits.LEAVES2_TYPE).get().getInternalId() - 4));
        ////////////////////
        ///    Log 2     ///
        ////////////////////
        register(162, logBuilder(LanternEnumTraits.LOG2_TYPE, LanternTreeType.ACACIA)
                        .build("minecraft", "log2"),
                blockState -> logData(blockState, blockState.getTraitValue(LanternEnumTraits.LOG2_TYPE).get().getInternalId() - 4));
        ////////////////////////
        ///   Slime Block    ///
        ////////////////////////
        register(166, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.0))
                                .add(blastResistance(0.0)))
                        .translation("tile.slime.name")
                        .build("minecraft", "slime"));
        // TODO: Slime block functionality
        ////////////////////
        ///   Barrier    ///
        ////////////////////
        register(166, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(PropertyProviderCollections.UNBREAKABLE))
                        .translation("tile.barrier.name")
                        .build("minecraft", "barrier"));
        // TODO: 167 Iron Trapdoor
        //////////////////////
        ///   Prismarine   ///
        //////////////////////
        register(168, simpleBuilder()
                        .trait(LanternEnumTraits.PRISMARINE_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.PRISMARINE_TYPE, LanternPrismarineType.ROUGH).get())
                        .itemType(builder -> builder
                            .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.PRISMARINE_TYPE, LanternPrismarineType.ROUGH)
                            )
                        )
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(30.0)))
                        .translation(TranslationProvider.of(LanternEnumTraits.PRISMARINE_TYPE))
                        .build("minecraft", "prismarine"),
                    blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.PRISMARINE_TYPE).get().getInternalId());
        ///////////////////////
        ///   Sea Lantern   ///
        ///////////////////////
        register(169, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.3))
                                .add(blastResistance(1.5))
                                .add(lightEmission(15)))
                        .translation("tile.seaLantern.name")
                        .build("minecraft", "sea_lantern"));
        ////////////////////
        ///   Hay Bale   ///
        ////////////////////
        register(170, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation("tile.hayBlock.name")
                        .build("minecraft", "hay_block"));
        // TODO: 171 Carpet
        /////////////////////////
        ///   Hardened Clay   ///
        /////////////////////////
        register(172, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(1.25))
                                .add(blastResistance(30)))
                        .translation("tile.clayHardened.name")
                        .build("minecraft", "hardened_clay"));
        /////////////////////////
        ///   Block of Coal   ///
        /////////////////////////
        register(173, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(5.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.blockCoal.name")
                        .build("minecraft", "coal_block"));
        /////////////////////
        ///   Packed Ice  ///
        /////////////////////
        register(174, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation("tile.icePacked.name")
                        .build("minecraft", "packed_ice"));
        // TODO: 175 Lilac + other flowers
        // TODO: 175 Free-standing Banner
        // TODO: 177 Wall-mounted Banner
        // TODO: 178 Inverted Daylight Sensor
        /////////////////////
        /// Red Sandstone ///
        /////////////////////
        register(179, simpleBuilder()
                        .trait(LanternEnumTraits.SANDSTONE_TYPE)
                        .defaultState(state -> state.withTrait(LanternEnumTraits.SANDSTONE_TYPE, LanternSandstoneType.DEFAULT).get())
                        .itemType(builder -> builder
                                .keysProvider(valueContainer -> valueContainer
                                        .registerKey(Keys.SANDSTONE_TYPE, LanternSandstoneType.DEFAULT)
                                )
                        )
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(4.0)))
                        .translation(TranslationProvider.of(LanternEnumTraits.SANDSTONE_TYPE))
                        .build("minecraft", "red_sandstone"),
                blockState -> (byte) blockState.getTraitValue(LanternEnumTraits.SANDSTONE_TYPE).get().getInternalId());
        ///////////////////////////
        /// Double Stone Slab 2 ///
        ///////////////////////////
        register(181, doubleStoneSlab(LanternEnumTraits.STONE_SLAB2_TYPE, LanternSlabType.RED_SAND)
                        .translation("tile.stoneSlab2.name")
                        .build("minecraft", "double_stone_slab2"),
                blockState -> doubleStoneSlabData(blockState,
                        blockState.getTraitValue(LanternEnumTraits.STONE_SLAB2_TYPE).get().getInternalId() - 8));
        ////////////////////////
        ///   Stone Slab 2   ///
        ////////////////////////
        register(182, stoneSlab(LanternEnumTraits.STONE_SLAB2_TYPE, LanternSlabType.RED_SAND,
                () -> BlockTypes.STONE_SLAB2,
                () -> BlockTypes.DOUBLE_STONE_SLAB2)
                        .translation("tile.stoneSlab2.name")
                        .boundingBox(BoundingBoxes::slab)
                        .build("minecraft", "stone_slab2"),
                blockState -> stoneSlabData(blockState, blockState.getTraitValue(LanternEnumTraits.STONE_SLAB2_TYPE).get().getInternalId() - 8));
        // TODO: 183 Spruce Fence Gate
        // TODO: 184 Birch Fence Gate
        // TODO: 185 Jungle Fence Gate
        // TODO: 186 Dark Oak Fence Gate
        // TODO: 187 Acacia Fence Gate
        // TODO: 188 Spruce Fence
        // TODO: 189 Birch Fence
        // TODO: 190 Jungle Fence
        // TODO: 191 Dark Oak Fence
        // TODO: 192 Acacia Fence
        // TODO: 193 Spruce Door Block
        // TODO: 194 Birch Door Block
        // TODO: 195 Jungle Door Block
        // TODO: 196 Acacia Door Block
        // TODO: 197 Dark Oak Block
        // TODO: 198 End Rod
        // TODO: 199 Chorus Plant
        // TODO: 200 Chorus Flower
        ///////////////////////
        ///   Purpur Block  ///
        ///////////////////////
        register(201, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(30.0)))
                        .translation("tile.purpurBlock.name")
                        .build("minecraft", "purpur_block"));
        ////////////////////////
        ///   Purpur Pillar  ///
        ////////////////////////
        register(202, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(1.5))
                                .add(blastResistance(30.0)))
                        .translation("tile.purpurPillar.name")
                        .build("minecraft", "purpur_pillar"));
        // TODO: 203 Purpur Stairs
        // TODO: 204 Purpur Double Slab
        // TODO: 205 Purpur Slab
        ///////////////////////////
        ///   End Stone Bricks  ///
        ///////////////////////////
        register(202, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.8))
                                .add(blastResistance(4.0)))
                        .translation("tile.endBricks.name")
                        .build("minecraft", "end_bricks"));
        // TODO: 207 Beetroot Block
        /////////////////////
        ///   Grass Path  ///
        /////////////////////
        register(208, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.6))
                                .add(blastResistance(3.25)))
                        .translation("tile.grassPath.name")
                        .build("minecraft", "grass_path"));
        // TODO: 209 End Gateway
        // TODO: 210 Repeating Command Block
        // TODO: 211 Chain Command Block
        //////////////////////
        ///   Frosted Ice  ///
        //////////////////////
        register(212, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation("tile.frostedIce.name")
                        .build("minecraft", "frosted_ice"));
        //////////////////////
        ///   Magma Block  ///
        //////////////////////
        register(213, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(0.5))
                                .add(blastResistance(2.5)))
                        .translation("tile.magma.name")
                        .build("minecraft", "magma"));
        // TODO: Magma Block Damage Functionality
        ////////////////////////////
        ///   Nether Wart Block  ///
        ////////////////////////////
        register(214, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(1.0))
                                .add(blastResistance(5.0)))
                        .translation("tile.netherWartBlock.name")
                        .build("minecraft", "nether_wart_block"));
        ///////////////////////////
        ///   Red Nether Brick  ///
        ///////////////////////////
        register(215, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.redNetherBrick.name")
                    .build("minecraft", "red_nether_brick"));
        /////////////////////
        ///   Bone Block  ///
        /////////////////////
        register(216, simpleBuilder()
                        .itemType()
                        .properties(builder -> builder
                                .add(hardness(2.0))
                                .add(blastResistance(30.0)))
                        .translation("tile.boneBlock.name")
                        .build("minecraft", "bone_block"));
        // TODO: 217 Structure Void
        // TODO: 218 Observer
        ///////////////////////////
        ///  White Shulker Box  ///
        ///////////////////////////
        register(219, shulkerBox()
                        .translation("tile.shulkerBoxWhite.name")
                        .build("minecraft", "white_shulker_box"),
                this::shulkerBoxData);
        ///////////////////////////
        ///  Orange Shulker Box ///
        ///////////////////////////
        register(220, shulkerBox()
                        .translation("tile.shulkerBoxOrange.name")
                        .build("minecraft", "orange_shulker_box"),
                this::shulkerBoxData);
        ////////////////////////////
        ///  Magenta Shulker Box ///
        ////////////////////////////
        register(221, shulkerBox()
                        .translation("tile.shulkerBoxMagenta.name")
                        .build("minecraft", "magenta_shulker_box"),
                this::shulkerBoxData);
        ///////////////////////////////
        ///  Light Blue Shulker Box ///
        ///////////////////////////////
        register(222, shulkerBox()
                        .translation("tile.shulkerBoxLightBlue.name")
                        .build("minecraft", "light_blue_shulker_box"),
                this::shulkerBoxData);
        ///////////////////////////
        ///  Yellow Shulker Box ///
        ///////////////////////////
        register(223, shulkerBox()
                        .translation("tile.shulkerBoxYellow.name")
                        .build("minecraft", "yellow_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Lime Shulker Box ///
        /////////////////////////
        register(224, shulkerBox()
                        .translation("tile.shulkerBoxLime.name")
                        .build("minecraft", "lime_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Pink Shulker Box ///
        /////////////////////////
        register(225, shulkerBox()
                        .translation("tile.shulkerBoxPink.name")
                        .build("minecraft", "pink_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Gray Shulker Box ///
        /////////////////////////
        register(226, shulkerBox()
                        .translation("tile.shulkerBoxGray.name")
                        .build("minecraft", "gray_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Gray Shulker Box ///
        /////////////////////////
        register(227, shulkerBox()
                        .translation("tile.shulkerBoxSilver.name")
                        .build("minecraft", "silver_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Cyan Shulker Box ///
        /////////////////////////
        register(228, shulkerBox()
                        .translation("tile.shulkerBoxCyan.name")
                        .build("minecraft", "cyan_shulker_box"),
                this::shulkerBoxData);
        ///////////////////////////
        ///  Purple Shulker Box ///
        ///////////////////////////
        register(229, shulkerBox()
                        .translation("tile.shulkerBoxPurple.name")
                        .build("minecraft", "purple_shulker_box"),
                this::shulkerBoxData);
        /////////////////////////
        ///  Blue Shulker Box ///
        /////////////////////////
        register(230, shulkerBox()
                        .translation("tile.shulkerBoxBlue.name")
                        .build("minecraft", "blue_shulker_box"),
                this::shulkerBoxData);
        //////////////////////////
        ///  Brown Shulker Box ///
        //////////////////////////
        register(231, shulkerBox()
                        .translation("tile.shulkerBoxBrown.name")
                        .build("minecraft", "brown_shulker_box"),
                this::shulkerBoxData);
        //////////////////////////
        ///  Green Shulker Box ///
        //////////////////////////
        register(232, shulkerBox()
                        .translation("tile.shulkerBoxGreen.name")
                        .build("minecraft", "green_shulker_box"),
                this::shulkerBoxData);
        ////////////////////////
        ///  Red Shulker Box ///
        ////////////////////////
        register(233, shulkerBox()
                        .translation("tile.shulkerBoxRed.name")
                        .build("minecraft", "red_shulker_box"),
                this::shulkerBoxData);
        //////////////////////////
        ///  Black Shulker Box ///
        //////////////////////////
        register(234, shulkerBox()
                        .translation("tile.shulkerBoxBlack.name")
                        .build("minecraft", "black_shulker_box"),
                this::shulkerBoxData);
        // TODO: 255 Structure Block

        // @formatter:on
    }

    private BlockTypeBuilder simpleBuilder() {
        return builder()
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new SimplePlacementBehavior())
                        .add(new SimpleBreakBehavior()));
        // TODO: Item drops?
    }

    private BlockTypeBuilder builder() {
        return new BlockTypeBuilderImpl();
    }

    private int directionData(Direction direction) {
        switch (direction) {
            case DOWN:
                return 0;
            case UP:
                return 1;
            case NORTH:
                return 2;
            case SOUTH:
                return 3;
            case WEST:
                return 4;
            case EAST:
                return 5;
            default:
                throw new IllegalArgumentException();
        }
    }

    private int horizontalDirectionData(Direction direction) {
        switch (direction) {
            case SOUTH:
                return 0;
            case WEST:
                return 1;
            case NORTH:
                return 2;
            case EAST:
                return 3;
            default:
                throw new IllegalArgumentException();
        }
    }

    private BlockTypeBuilder pumpkinBuilder() {
        return simpleBuilder()
                .itemType()
                .traits(LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                .properties(builder -> builder
                        .add(hardness(1.0))
                        .add(blastResistance(5.0)))
                .behaviors(pipeline -> pipeline
                        .add(new HorizontalRotationPlacementBehavior()));
    }

    private byte pumpkinData(BlockState blockState) {
        return (byte) horizontalDirectionData(blockState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get());
    }

    private BlockTypeBuilder mushroomBlockBuilder() {
        return simpleBuilder()
                .itemType()
                .trait(LanternEnumTraits.BIG_MUSHROOM_TYPE)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.BIG_MUSHROOM_TYPE, LanternBigMushroomType.ALL_OUTSIDE).get())
                .properties(builder -> builder
                        .add(hardness(0.2))
                        .add(blastResistance(1.0)))
                .translation("tile.mushroom.name");
    }

    private byte mushroomData(BlockState blockState) {
        return (byte) blockState.getTraitValue(LanternEnumTraits.BIG_MUSHROOM_TYPE).get().getInternalId();
    }

    private BlockTypeBuilder dyedBuilder(String translationKey) {
        return simpleBuilder()
                .traits(LanternEnumTraits.DYE_COLOR)
                .defaultState(state -> state
                        .withTrait(LanternEnumTraits.DYE_COLOR, LanternDyeColor.WHITE).get())
                .itemType(builder -> builder
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.DYE_COLOR, LanternDyeColor.WHITE)
                        )
                )
                .properties(builder -> builder
                        .add(hardness(0.8))
                        .add(blastResistance(4.0)))
                .translation(TranslationProvider.of(LanternEnumTraits.DYE_COLOR, color ->
                        tr(String.format(translationKey, color.getTranslationPart()))));
    }

    private byte dyedData(BlockState blockState) {
        return (byte) blockState.getTraitValue(LanternEnumTraits.DYE_COLOR).get().getInternalId();
    }

    /**
     * Generates a leaves block builder.
     *
     * @param enumTrait The tree type enum trait
     * @param <E> The enum value type
     * @return The block type builder
     */
    private <E extends Enum<E> & TreeType> BlockTypeBuilder leavesBuilder(EnumTrait<E> enumTrait, E defaultTreeType) {
        return simpleBuilder()
                .traits(LanternBooleanTraits.DECAYABLE, LanternBooleanTraits.CHECK_DECAY, enumTrait)
                .defaultState(state -> state.withTrait(enumTrait, defaultTreeType).get())
                .itemType(builder -> builder
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.TREE_TYPE, defaultTreeType)
                        )
                )
                .properties(builder -> builder
                        .add(hardness(0.2))
                        .add(blastResistance(1.0))
                        .add(flammableInfo(30, 60)))
                .translation(TranslationProvider.of(enumTrait, type ->
                        tr("tile.leaves." + ((LanternTreeType) type).getTranslationKeyBase() + ".name")));
    }

    private byte leavesData(BlockState blockState, int type) {
        if (blockState.getTraitValue(LanternBooleanTraits.DECAYABLE).get()) {
            type |= 0x4;
        }
        if (blockState.getTraitValue(LanternBooleanTraits.CHECK_DECAY).get()) {
            type |= 0x8;
        }
        return (byte) type;
    }

    /**
     * Generates a log block builder.
     *
     * @param enumTrait The tree type enum trait
     * @param <E> The enum value type
     * @return The block type builder
     */
    private <E extends Enum<E> & TreeType> BlockTypeBuilder logBuilder(EnumTrait<E> enumTrait, E defaultTreeType) {
        return simpleBuilder()
                .traits(LanternEnumTraits.LOG_AXIS, enumTrait)
                .defaultState(state -> state.withTrait(enumTrait, defaultTreeType).get())
                .itemType(builder -> builder
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.TREE_TYPE, defaultTreeType)
                        )
                )
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(5.0))
                        .add(flammableInfo(5, 5)))
                .translation(TranslationProvider.of(enumTrait, type ->
                        tr("tile.log." + ((LanternTreeType) type).getTranslationKeyBase() + ".name")))
                .behaviors(pipeline -> pipeline
                        .add(new LogAxisRotationPlacementBehavior())
                        .add(new SimpleBlockDropsProviderBehavior(/* No items yet? */)));
    }

    private byte logData(BlockState blockState, int type) {
        final int axis = blockState.getTraitValue(LanternEnumTraits.LOG_AXIS).get().getInternalId();
        return (byte) (axis << 2 | type);
    }

    private byte chestData(BlockState blockState) {
        final Direction facing = blockState.getTraitValue(LanternEnumTraits.HORIZONTAL_FACING).get();
        return (byte) directionData(facing);
    }

    private BlockTypeBuilder chestBuilder() {
        return builder()
                .trait(LanternEnumTraits.HORIZONTAL_FACING)
                .defaultState(state -> state.withTrait(LanternEnumTraits.HORIZONTAL_FACING, Direction.NORTH).get())
                .itemType()
                .tileEntityType(() -> TileEntityTypes.CHEST)
                .boundingBox(BoundingBoxes::doubleChest)
                .properties(builder -> builder
                        .add(hardness(2.5))
                        .add(blastResistance(12.5)))
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new ChestPlacementBehavior())
                        .add(new ChestInteractionBehavior())
                        .add(new SimpleBreakBehavior()));
        // TODO: Item drops?
    }

    private BlockTypeBuilder shulkerBox() {
        return builder()
                .trait(LanternEnumTraits.FACING)
                .defaultState(state -> state.withTrait(LanternEnumTraits.FACING, Direction.UP).get())
                .itemType(builder -> builder
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(LanternKeys.INVENTORY_SNAPSHOT, InventorySnapshot.EMPTY)
                        )
                        .maxStackQuantity(1)
                )
                .tileEntityType(() -> TileEntityTypes.SHULKER_BOX)
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)))
                .behaviors(pipeline -> pipeline
                        .add(new BlockSnapshotProviderPlaceBehavior())
                        .add(new SimplePlacementBehavior())
                        .add(new OppositeFaceDirectionalPlacementBehavior())
                        .add(new OpeneableContainerInteractionBehavior())
                        .add(new SimpleBreakBehavior()));
        // TODO: Item drops?
    }

    private byte shulkerBoxData(BlockState blockState) {
        final Direction facing = blockState.getTraitValue(LanternEnumTraits.FACING).get();
        return (byte) directionData(facing);
    }

    private <E extends Enum<E> & SlabType> BlockTypeBuilder stoneSlab(EnumTrait<E> enumTrait, E defaultValue,
            Supplier<BlockType> halfSlabType, Supplier<BlockType> doubleSlabType) {
        return simpleBuilder()
                .traits(LanternEnumTraits.PORTION_TYPE, enumTrait)
                .defaultState(state -> state
                        .withTrait(enumTrait, defaultValue).get()
                        .withTrait(LanternEnumTraits.PORTION_TYPE, LanternPortionType.BOTTOM).get())
                .translation(TranslationProvider.of(enumTrait))
                .itemType(builder -> builder
                        .behaviors(pipeline -> pipeline
                                .add(new SlabItemInteractionBehavior<>(enumTrait, halfSlabType, doubleSlabType)))
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.SLAB_TYPE, defaultValue)
                        )
                )
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)));
    }

    private byte stoneSlabData(BlockState blockState, int type) {
        final int portion = (byte) blockState.getTraitValue(LanternEnumTraits.PORTION_TYPE).get().getInternalId();
        return (byte) (portion << 3 | type);
    }

    private <E extends Enum<E> & SlabType> BlockTypeBuilder doubleStoneSlab(EnumTrait<E> enumTrait, E defaultValue) {
        return simpleBuilder()
                .traits(LanternBooleanTraits.SEAMLESS, enumTrait)
                .defaultState(state -> state
                        .withTrait(enumTrait, defaultValue).get()
                        .withTrait(LanternBooleanTraits.SEAMLESS, false).get())
                .translation(TranslationProvider.of(enumTrait))
                .itemType(builder -> builder
                        .keysProvider(valueContainer -> valueContainer
                                .registerKey(Keys.SLAB_TYPE, defaultValue)
                        )
                )
                .properties(builder -> builder
                        .add(hardness(2.0))
                        .add(blastResistance(10.0)));
    }

    private byte doubleStoneSlabData(BlockState blockState, int type) {
        final byte seamless = (byte) (blockState.getTraitValue(LanternBooleanTraits.SEAMLESS).get() ? 1 : 0);
        return (byte) (seamless << 3 | type);
    }
}
