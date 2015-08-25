package org.lanternpowered.server.text.xml;

import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Placeholder extends Span {

    @XmlAttribute(required = true)
    private String key;

    public Placeholder() {
    }

    public Placeholder(String key) {
        this.key = key;
    }

    @Override
    protected void modifyBuilder(TextBuilder builder) {
        // TODO: get rid of this
    }

    @Override
    public TextBuilder toText() throws Exception {
        TextBuilder.Placeholder builder = Texts.placeholderBuilder(this.key);
        if (!this.mixedContent.isEmpty()) {
            builder.fallback(super.toText().build());
        }
        //applyTextActions(builder);
        return builder;
    }
}
