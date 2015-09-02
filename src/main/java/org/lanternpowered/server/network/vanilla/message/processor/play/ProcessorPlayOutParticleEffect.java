package org.lanternpowered.server.network.vanilla.message.processor.play;

import io.netty.handler.codec.CodecException;

import java.awt.Color;
import java.util.List;
import java.util.Random;

import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.effect.particle.LanternParticleType;
import org.lanternpowered.server.item.LanternItems;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.processor.Processor;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutParticleEffect;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutSpawnParticle;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleType;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.item.ItemBlock;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;

import com.flowpowered.math.vector.Vector3f;

@Caching
public final class ProcessorPlayOutParticleEffect implements Processor<MessagePlayOutParticleEffect> {

    @Override
    public void process(CodecContext context, MessagePlayOutParticleEffect message, List<Message> output) throws CodecException {
        ParticleEffect effect = message.getParticleEffect();
        LanternParticleType type = (LanternParticleType) effect.getType();

        Vector3f position = message.getPosition().toFloat();
        Vector3f offset = effect.getOffset().toFloat();

        int count = effect.getCount();
        // Don't even try...
        if (count <= 0) {
            return;
        }

        int[] extra = new int[0];

        // The extra values, normal behavior offsetX, offsetY, offsetZ
        float f0 = 0f;
        float f1 = 0f;
        float f2 = 0f;

        // Depends on behavior
        // Note: If the count > 0 -> speed = 0f else if count = 0 -> speed = 1f

        if (effect instanceof ParticleEffect.Material) {
            ItemStack item = ((ParticleEffect.Material) effect).getItem();
            ItemType itemType = item.getItem();
            int extraData = 0;
            if (type == ParticleTypes.ITEM_CRACK) {
                extraData = LanternItems.getId(itemType);
            } else if (type == ParticleTypes.BLOCK_CRACK || type == ParticleTypes.BLOCK_DUST) {
                // Only block types are allowed
                if (itemType instanceof ItemBlock) {
                    int id = LanternBlocks.getTypeId(((ItemBlock) itemType).getBlock());
                    int data = 0; // TODO: Retrieve data value from item stack
                    extraData = data << 12 | id;
                }
            }
            if (extraData == 0) {
                return;
            }
            extra = new int[] { extraData };
        }

        if (effect instanceof ParticleEffect.Resizable) {
            float size = ((ParticleEffect.Resizable) effect).getSize();

            // The formula of the large explosion acts strange
            // Client formula: sizeClient = 1 - sizeServer * 0.5
            // The particle effect returns the client value so
            // Server formula: sizeServer = (-sizeClient * 2) + 2
            if (type == ParticleTypes.EXPLOSION_LARGE) {
                size = (-size * 2f) + 2f;
            }

            if (size == 0f) {
                output.add(new MessagePlayOutSpawnParticle(count, position, offset, size, count, extra));
                return;
            }

            f0 = size;
        } else if (effect instanceof ParticleEffect.Colorable) {
            Color color0 = ((ParticleEffect.Colorable) effect).getColor();
            Color color1 = ((ParticleType.Colorable) type).getDefaultColor();

            if (color0.equals(color1)) {
                output.add(new MessagePlayOutSpawnParticle(count, position, offset, 0f, count, extra));
                return;
            }

            f0 = color0.getRed() / 255f;
            f1 = color0.getGreen() / 255f;
            f2 = color0.getBlue() / 255f;

            // If the f0 value 0 is, the redstone will set it automatically to red 255
            if (f0 == 0f && type == ParticleTypes.REDSTONE) {
                f0 = 0.00001f;
            }
        } else if (effect instanceof ParticleEffect.Note) {
            float note = ((ParticleEffect.Note) effect).getNote();

            if (note == 0f) {
                output.add(new MessagePlayOutSpawnParticle(count, position, offset, 0f, count, extra));
                return;
            }

            f0 = note / 24f;
        } else if (type.hasMotion()) {
            Vector3f motion = effect.getMotion().toFloat();

            float mx = motion.getX();
            float my = motion.getY();
            float mz = motion.getZ();

            // The y value won't work for this effect, if the value isn't 0 the motion won't work
            if (type == ParticleTypes.WATER_SPLASH) {
                my = 0f;
            }

            if (mx == 0f && my == 0f && mz == 0f) {
                output.add(new MessagePlayOutSpawnParticle(count, position, offset, 0f, count, extra));
                return;
            }

            f0 = mx;
            f1 = my;
            f2 = mz;
        }

        // Is this check necessary?
        if (f0 == 0f && f1 == 0f && f2 == 0f) {
            output.add(new MessagePlayOutSpawnParticle(count, position, offset, 0f, count, extra));
            return;
        }

        if (offset.equals(Vector3f.ZERO)) {
            for (int i = 0; i < count; i++) {
                output.add(new MessagePlayOutSpawnParticle(count, position, offset, 1f, 0, extra));
            }
        } else {
            Random random = new Random();

            float px = position.getX();
            float py = position.getY();
            float pz = position.getZ();

            float ox = offset.getX();
            float oy = offset.getY();
            float oz = offset.getZ();

            Vector3f value = new Vector3f(f0, f1, f2);

            for (int i = 0; i < count; i++) {
                double px0 = (px + (random.nextFloat() * 2f - 1f) * ox);
                double py0 = (py + (random.nextFloat() * 2f - 1f) * oy);
                double pz0 = (pz + (random.nextFloat() * 2f - 1f) * oz);

                output.add(new MessagePlayOutSpawnParticle(count, new Vector3f(px0, py0, pz0), value, 1f, 0, extra));
            }
        }
    }
}
