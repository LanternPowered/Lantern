package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternDirtTypes;
import org.spongepowered.api.block.trait.BlockTrait;

public final class BlockDirt extends LanternBlockType {

    public static final BlockTrait<LanternDirtTypes> TYPE = LanternEnumTrait.of("variant", LanternDirtTypes.class);

    public BlockDirt(String identifier) {
        super(identifier, MaterialType.SOLID, TYPE);
        this.setFullBlock(true);
    }
}
