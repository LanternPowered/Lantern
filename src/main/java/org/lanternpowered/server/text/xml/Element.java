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
package org.lanternpowered.server.text.xml;

import com.google.common.collect.Lists;
import org.lanternpowered.server.text.LanternTextHelper;
import org.lanternpowered.server.text.LanternTextHelper.RawAction;
import org.lanternpowered.server.text.LanternTextSerializer;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TranslatableText;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.ShiftClickAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.text.translation.Translation;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlSeeAlso({
        A.class,
        B.class,
        Color.class,
        I.class,
        Obfuscated.class,
        Strikethrough.class,
        Span.class,
        Tr.class,
        U.class
    })
public abstract class Element {

    private static final Pattern FUNCTION_PATTERN = Pattern.compile("^([^(]+)\\('(.*)'\\)$");

    @XmlAttribute
    @Nullable
    private String onClick = null;

    @XmlAttribute
    @Nullable
    private String onShiftClick = null;

    @XmlAttribute
    @Nullable
    private String onHover = null;

    @XmlElementRef(type = Element.class)
    @XmlMixed
    protected List<Object> mixedContent = Lists.newArrayList();

    protected abstract void modifyBuilder(Text.Builder builder);

    public Text.Builder toText() throws Exception {
        Text.Builder builder;
        if (this.mixedContent.size() == 0) {
            builder = Text.builder();
        } else if (this.mixedContent.size() == 1) { // then we are a thin wrapper around the child
            builder = this.builderFromObject(this.mixedContent.get(0));
        } else {
            if (this.mixedContent.get(0) instanceof String) {
                builder = this.builderFromObject(this.mixedContent.get(0));
                this.mixedContent.remove(0);
            } else {
                builder = Text.builder();
            }
            for (Object child : this.mixedContent) {
                builder.append(this.builderFromObject(child).build());
            }
        }

        this.modifyBuilder(builder);
        this.applyTextActions(builder);

        return builder;
    }

    protected Text.Builder builderFromObject(Object o) throws Exception {
        if (o instanceof String) {
            return Text.builder(String.valueOf(o).replace('\u000B', ' '));
        } else if (o instanceof Element) {
            return ((Element) o).toText();
        } else {
            throw new IllegalArgumentException("What is this even? " + o);
        }
    }

    protected void applyTextActions(Text.Builder builder) throws Exception {
        if (this.onClick != null) {
            Matcher matcher = FUNCTION_PATTERN.matcher(this.onClick);
            if (!matcher.matches()) {
                throw new RuntimeException("Invalid onClick handler in " + this.getClass().getSimpleName() + " tag.");
            }

            String action = matcher.group(1);
            String value = matcher.group(2);

            ClickAction<?> clickAction;
            try {
                clickAction = LanternTextHelper.parseClickAction(action, value);
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException && e.getMessage().startsWith("Unknown")) {
                    throw new RuntimeException("Unknown onClick action " + action + " in " + this.getClass().getSimpleName() + " tag.");
                } else {
                    throw e;
                }
            }
            if (clickAction != null) {
                builder.onClick(clickAction);
            }
        }

        if (this.onShiftClick != null) {
            Matcher matcher = FUNCTION_PATTERN.matcher(this.onShiftClick);
            if (!matcher.matches()) {
                throw new RuntimeException("Invalid onShiftClick handler in " + this.getClass().getSimpleName() + " tag.");
            }

            String action = matcher.group(1);
            String value = matcher.group(2);

            if (!action.equalsIgnoreCase("insert_text")) {
                throw new RuntimeException("Unknown onShiftClick action " + action + " in " + this.getClass().getSimpleName() + " tag.");
            }

            builder.onShiftClick(TextActions.insertText(value));
        }

        if (this.onHover != null) {
            final Matcher matcher = FUNCTION_PATTERN.matcher(this.onHover);
            if (!matcher.matches()) {
                throw new RuntimeException("Invalid onHover handler in " + this.getClass().getSimpleName() + " tag.");
            }

            String action = matcher.group(1);
            String value = matcher.group(2);

            HoverAction<?> hoverAction;
            try {
                hoverAction = LanternTextHelper.parseHoverAction(action, value);
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException && e.getMessage().startsWith("Unknown")) {
                    throw new RuntimeException("Unknown onHover action " + action + " in " + this.getClass().getSimpleName() + " tag.");
                } else {
                    throw e;
                }
            }
            if (hoverAction != null) {
                builder.onHover(hoverAction);
            }
        }
    }

    public static Element fromText(Text text, Locale locale) {
        final AtomicReference<Element> fixedRoot = new AtomicReference<>();
        Element currentElement = null;
        if (text.getColor() != TextColors.NONE) {
            currentElement = update(fixedRoot, currentElement, new Color.C(text.getColor()));
        }
        if (text.getStyle().contains(TextStyles.BOLD)) {
            currentElement = update(fixedRoot, currentElement, new B());
        }
        if (text.getStyle().contains(TextStyles.ITALIC)) {
            currentElement = update(fixedRoot, currentElement, new I());
        }
        if (text.getStyle().contains(TextStyles.OBFUSCATED)) {
            currentElement = update(fixedRoot, currentElement, new Obfuscated.O());
        }
        if (text.getStyle().contains(TextStyles.STRIKETHROUGH)) {
            currentElement = update(fixedRoot, currentElement, new Strikethrough.S());
        }
        if (text.getStyle().contains(TextStyles.UNDERLINE)) {
            currentElement = update(fixedRoot, currentElement, new U());
        }
        if (text.getClickAction().isPresent()) {
            if (text.getClickAction().get() instanceof ClickAction.OpenUrl) {
                currentElement = update(fixedRoot, currentElement, new A(((ClickAction.OpenUrl) text.getClickAction().get()).getResult()));
            } else {
                if (currentElement == null) {
                    fixedRoot.set(currentElement = new Span());
                }
                RawAction raw = LanternTextHelper.raw(text.getClickAction().get());
                currentElement.onClick = raw.getAction() + "('" + ((LanternTextSerializer) TextSerializers.TEXT_XML)
                        .serialize(raw.getValueAsText(), locale) + "')";
            }
        } else {
            if (currentElement == null) {
                fixedRoot.set(currentElement = new Span());
            }
        }

        if (text.getHoverAction().isPresent()) {
            RawAction raw = LanternTextHelper.raw(text.getHoverAction().get());
            currentElement.onHover = raw.getAction() + "('" + ((LanternTextSerializer) TextSerializers.TEXT_XML)
                    .serialize(raw.getValueAsText(), locale) + "')";
        }

        if (text.getShiftClickAction().isPresent()) {
            ShiftClickAction<?> action = text.getShiftClickAction().get();
            if (!(action instanceof ShiftClickAction.InsertText)) {
                throw new IllegalArgumentException("Shift-click action is not an insertion. Currently not supported!");
            }
            currentElement.onShiftClick = "insert_text('" + action.getResult() + "')";
        }

        if (text instanceof LiteralText) {
            currentElement.mixedContent.add(((LiteralText) text).getContent());
        } else if (text instanceof TranslatableText) {
            Translation transl = ((TranslatableText) text).getTranslation();
            currentElement = update(fixedRoot, currentElement, new Tr(transl.getId()));
            for (Object o : ((TranslatableText) text).getArguments()) {
                if (o instanceof Text) {
                    currentElement.mixedContent.add(Element.fromText(((Text) o), locale));
                } else {
                    currentElement.mixedContent.add(String.valueOf(o));
                }
            }
        } else {
            throw new IllegalArgumentException("Text was of type " + text.getClass() + ", which is unsupported by the XML format");
        }

        for (Text child : text.getChildren()) {
            currentElement.mixedContent.add(Element.fromText(child, locale));
        }

        return fixedRoot.get();
    }

    private static Element update(AtomicReference<Element> fixedRoot, @Nullable Element parent, Element child) {
        if (parent == null) {
            fixedRoot.set(child);
            return child;
        } else {
            parent.mixedContent.add(child);
            return child;
        }
    }
}
