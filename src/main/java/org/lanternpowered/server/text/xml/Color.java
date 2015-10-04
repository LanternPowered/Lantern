package org.lanternpowered.server.text.xml;

import java.util.Optional;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.format.TextColor;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(Color.C.class)
@XmlRootElement
public class Color extends Element {

    @XmlAttribute
    private String name;

    @XmlAttribute
    protected String n;

    public Color() {
    }

    public Color(TextColor color) {
        this.name = color.getName();
    }

    @Override
    protected void modifyBuilder(TextBuilder builder) {
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
