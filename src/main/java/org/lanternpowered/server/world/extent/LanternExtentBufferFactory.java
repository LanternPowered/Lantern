package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;

import org.lanternpowered.server.util.gen.ShortArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;

public final class LanternExtentBufferFactory implements ExtentBufferFactory {

    public static final LanternExtentBufferFactory INSTANCE = new LanternExtentBufferFactory();

    private LanternExtentBufferFactory() {
    }

    @Override
    public MutableBiomeArea createBiomeBuffer(Vector2i size) {
        return new ShortArrayMutableBiomeBuffer(Vector2i.ZERO, size);
    }

    @Override
    public MutableBiomeArea createBiomeBuffer(int xSize, int zSize) {
        return createBiomeBuffer(new Vector2i(xSize, zSize));
    }

    @Override
    public MutableBiomeArea createThreadSafeBiomeBuffer(Vector2i size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableBiomeArea createThreadSafeBiomeBuffer(int xSize, int zSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableBlockVolume createBlockBuffer(Vector3i size) {
        return new ShortArrayMutableBlockBuffer(Vector3i.ZERO, size);
    }

    @Override
    public MutableBlockVolume createBlockBuffer(int xSize, int ySize, int zSize) {
        return createBlockBuffer(new Vector3i(xSize, ySize, zSize));
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(Vector3i size) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(int xSize, int ySize, int zSize) {
        throw new UnsupportedOperationException();
    }

}
