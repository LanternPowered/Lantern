package org.lanternpowered.server.item;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.block.type.BlockSlabBase;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.EnumTrait;
import org.spongepowered.api.data.property.block.ReplaceableProperty;
import org.spongepowered.api.data.type.PortionType;
import org.spongepowered.api.data.type.PortionTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;

public class BlockItemSlab extends BlockItemType {

    public BlockItemSlab(String pluginId, String identifier, BlockSlabBase blockType) {
        super(pluginId, identifier, blockType);
    }

    @Override
    public ItemInteractionResult onInteractWithItemAt(@Nullable Player player, World world, ItemInteractionType interactionType,
            ItemStack itemStack, Vector3i clickedBlock, Direction blockFace, Vector3d cursorOffset) {
        final BlockSlabBase slabBase = (BlockSlabBase) this.blockType;
        if (slabBase.isHalf()) {
            BlockState state = world.getBlock(clickedBlock);
            BlockState blockState = null;
            boolean success = false;
            if (state.getType() == this.blockType) {
                final EnumTrait variantTrait = (EnumTrait) slabBase.getVariantTrait();
                if (state.getTraitValue(variantTrait).get()
                        .equals(state.getType().getDefaultState().getTraitValue(variantTrait).get())) {
                    PortionType portionType = state.getTraitValue(BlockSlabBase.PORTION).get();
                    if ((blockFace == Direction.DOWN && portionType == PortionTypes.BOTTOM) ||
                            (blockFace == Direction.UP && portionType == PortionTypes.TOP)) {
                        blockState = slabBase.getDouble().getDefaultState().withTrait(variantTrait,
                                state.getTraitValue(variantTrait).get()).get();
                        success = true;
                    }
                }
            } else if (world.getProperty(clickedBlock, ReplaceableProperty.class).get().getValue() == Boolean.TRUE) {
                success = true;
            }
            if (!success) {
                clickedBlock = clickedBlock.add(blockFace.getOpposite().asBlockOffset());
                state = world.getBlock(clickedBlock);
                if (state.getType() == this.blockType) {
                    final EnumTrait variantTrait = (EnumTrait) slabBase.getVariantTrait();
                    if (state.getTraitValue(variantTrait).get()
                            .equals(state.getType().getDefaultState().getTraitValue(variantTrait).get())) {
                        PortionType portionType = state.getTraitValue(BlockSlabBase.PORTION).get();
                        if ((blockFace == Direction.DOWN && portionType == PortionTypes.TOP) ||
                                (blockFace == Direction.UP && portionType == PortionTypes.BOTTOM)) {
                            blockState = slabBase.getDouble().getDefaultState().withTrait(variantTrait,
                                    state.getTraitValue(variantTrait).get()).get();
                            success = true;
                        }
                    }
                } else if (world.getProperty(clickedBlock, ReplaceableProperty.class).get().getValue() == Boolean.TRUE) {
                    success = true;
                }
            }
            if (success) {
                if (blockState == null) {
                    PortionType portionType;
                    if (blockFace == Direction.UP) {
                        portionType = PortionTypes.TOP;
                    } else if (blockFace == Direction.DOWN) {
                        portionType = PortionTypes.BOTTOM;
                    } else {
                        if (cursorOffset.getY() >= 0.5) {
                            portionType = PortionTypes.TOP;
                        } else {
                            portionType = PortionTypes.BOTTOM;
                        }
                    }
                    blockState = slabBase.getDefaultState().withTrait(BlockSlabBase.PORTION, portionType).get();
                }
            } else {
                return ItemInteractionResult.pass();
            }
            itemStack = itemStack.copy();
            itemStack.setQuantity(itemStack.getQuantity() - 1);
            world.setBlock(clickedBlock, blockState);
            return ItemInteractionResult.builder()
                    .type(ItemInteractionResult.Type.SUCCESS)
                    .resultItem(itemStack.createSnapshot())
                    .build();
        } else {
            return super.onInteractWithItemAt(player, world, interactionType, itemStack, clickedBlock, blockFace, cursorOffset);
        }
    }
}
