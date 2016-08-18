/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.script.function.action.misc;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.lanternpowered.api.script.function.action.Action;
import org.lanternpowered.api.script.function.value.FloatValueProvider;
import org.lanternpowered.api.script.context.ContextParameters;
import org.lanternpowered.api.script.ScriptContext;
import org.spongepowered.api.effect.sound.SoundCategories;
import org.spongepowered.api.effect.sound.SoundCategory;
import org.spongepowered.api.effect.sound.SoundType;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class PlaySoundAction implements Action {

    /**
     * The sound type that should be played.
     */
    @Expose
    @SerializedName("type")
    private SoundType soundType = SoundTypes.UI_BUTTON_CLICK;

    /**
     * The sound category that should be used to play the sound.
     */
    @Expose
    @SerializedName("category")
    private SoundCategory soundCategory = SoundCategories.MASTER;

    /**
     * The volume of the sound effect.
     */
    @Expose
    @SerializedName("volume")
    private FloatValueProvider volume = FloatValueProvider.constant(1.0f);

    /**
     * The pitch of the sound effect.
     */
    @Expose
    @SerializedName("pitch")
    private FloatValueProvider pitch = FloatValueProvider.constant(1.0f);

    @Override
    public void run(ScriptContext context) {
        final Location<World> location = context.get(ContextParameters.TARGET_LOCATION).get();
        location.getExtent().playSound(this.soundType, this.soundCategory, location.getPosition(),
                this.volume.get(context), this.pitch.get(context));
    }
}
