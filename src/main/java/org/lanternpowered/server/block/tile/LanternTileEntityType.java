package org.lanternpowered.server.block.tile;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.catalog.SimpleLanternCatalogType;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.TileEntityType;

public final class LanternTileEntityType extends SimpleLanternCatalogType implements TileEntityType {

    private final Class<? extends TileEntity> tileClass;

    public LanternTileEntityType(String identifier, Class<? extends TileEntity> tileClass) {
        super(identifier);

        this.tileClass = checkNotNull(tileClass, "tileClass");
    }

    @Override
    public Class<? extends TileEntity> getTileEntityType() {
        return this.tileClass;
    }
}
