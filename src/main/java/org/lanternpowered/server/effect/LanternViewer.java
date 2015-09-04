package org.lanternpowered.server.effect;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatType;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;

public interface LanternViewer extends Viewer {

    @Override
    default void sendMessage(ChatType type, Text... messages) {
        this.sendMessage(type, Lists.newArrayList(checkNotNull(messages, "messages")));
    }

    @Override
    default void playSound(SoundType sound, Vector3d position, double volume) {
        this.playSound(sound, position, volume, 1.0);
    }

    @Override
    default void playSound(SoundType sound, Vector3d position, double volume, double pitch) {
        this.playSound(sound, position, volume, pitch, 0.0);
    }
}
