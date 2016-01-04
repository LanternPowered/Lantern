/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.text.xml;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;

import java.util.Optional;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(Color.C.class)
@XmlRootElement
public class Color extends Element {

    @XmlAttribute
    @Nullable
    private String name;

    @XmlAttribute
    @Nullable
    protected String n;

    public Color() {
    }

    public Color(TextColor color) {
        this.name = color.getName();
    }

    @Override
    protected void modifyBuilder(Text.Builder builder) {
        if (this.name == null && this.n != null) {
            this.name = this.n;
        }
        if (this.name != null) {
            Optional<TextColor> color = LanternGame.get().getRegistry().getType(TextColor.class, this.name.toUpperCase());
            if (color.isPresent()) {
                builder.color(color.get());
            }
        }
    }

    @XmlRootElement
    public static class C extends Color {

        public C() {
        }

        public C(TextColor color) {
            this.n = color.getName();
        }

    }

}
