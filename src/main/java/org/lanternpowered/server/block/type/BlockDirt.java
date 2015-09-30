package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.lanternpowered.server.block.trait.LanternEnumTrait;
import org.lanternpowered.server.data.type.LanternDirtTypes;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;

public final class BlockDirt extends LanternBlockType {

    public static final BlockTrait<LanternDirtTypes> TYPE = LanternEnumTrait.of("variant", LanternDirtTypes.class);

    public BlockDirt(String identifier) {
        super(identifier, Matter.SOLID, TYPE);
    }
}
