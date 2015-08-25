package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.format.TextStyles;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso(Obfuscated.O.class)
@XmlRootElement
public class Obfuscated extends Element {

    @Override
    protected void modifyBuilder(TextBuilder builder) {
        builder.style(TextStyles.OBFUSCATED);
    }

    @XmlRootElement
    public static class O extends Obfuscated {
    }
}
