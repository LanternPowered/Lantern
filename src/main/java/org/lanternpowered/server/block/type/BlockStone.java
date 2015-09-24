package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternStoneTypes;
import org.spongepowered.api.block.trait.BlockTrait;

public final class BlockStone extends LanternBlockType {

    public static final BlockTrait<LanternStoneTypes> TYPE = LanternEnumTrait.of("variant", LanternStoneTypes.class);

    public BlockStone(String identifier) {
        super(identifier, MaterialType.SOLID, TYPE);
        this.setFullBlock(true);
    }
}
