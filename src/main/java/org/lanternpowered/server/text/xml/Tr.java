package org.lanternpowered.server.text.xml;

import com.google.common.collect.ImmutableList;

import org.lanternpowered.server.game.LanternGame;
import org.spongepowered.api.text.TextBuilder;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.translation.Translation;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Tr extends Element {

    @XmlAttribute(required = true)
    private String key;

    public Tr() {}

    public Tr(String key) {
        this.key = key;
    }

    @Override
    protected void modifyBuilder(TextBuilder builder) {
        // TODO: get rid of this
    }

    @Override
    public TextBuilder toText() throws Exception {
        ImmutableList.Builder<Object> build = ImmutableList.builder();
        for (Object child : this.mixedContent) {
            build.add(this.builderFromObject(child).build());
        }
        Translation translation = LanternGame.get().getRegistry().getTranslationManager().get(this.key);
        TextBuilder builder = Texts.builder(translation, build.build().toArray());
        //applyTextActions(builder);
        return builder;
    }
}
