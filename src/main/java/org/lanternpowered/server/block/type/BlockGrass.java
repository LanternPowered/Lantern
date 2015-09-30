package org.lanternpowered.server.block.type;

import org.lanternpowered.server.block.LanternBlockType;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;

public final class BlockGrass extends LanternBlockType {

    public BlockGrass(String identifier) {
        super(identifier, Matter.SOLID);
    }
}
