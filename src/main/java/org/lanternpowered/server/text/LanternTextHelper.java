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
package org.lanternpowered.server.text;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.lanternpowered.server.text.action.LanternCallbackHolder;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Coerce;
import org.spongepowered.api.util.annotation.NonnullByDefault;

import javax.annotation.Nullable;

@NonnullByDefault
public class LanternTextHelper {

    public static ClickAction<?> parseClickAction(String action, String value) {
        switch (action) {
            case "open_url":
            case "open_file":
                URI uri = null;
                if (action.equals("open_url")) {
                    try {
                        uri = new URI(value);
                    } catch (URISyntaxException ignored) {
                    }
                } else {
                    uri = new File(value).toURI();
                }
                if (uri != null) {
                    try {
                        return TextActions.openUrl(uri.toURL());
                    } catch (MalformedURLException ignored) {
                    }
                }
                break;
            case "run_command":
                if (value.toLowerCase().contains(LanternCallbackHolder.CALLBACK_COMMAND)) {
                    final String[] parts = value.split(" ");
                    if (parts.length > 1 && parts[0].equalsIgnoreCase(LanternCallbackHolder.CALLBACK_COMMAND)) {
                        try {
                            final UUID uuid = UUID.fromString(parts[1]);
                            Optional<Consumer<CommandSource>> opt = LanternCallbackHolder.getInstance()
                                    .getCallbackForUUID(uuid);
                            if (opt.isPresent()) {
                                return TextActions.executeCallback(opt.get());
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
                return TextActions.runCommand(value);
            case "suggest_command":
                return TextActions.suggestCommand(value);
            case "change_page":
                Optional<Integer> page = Coerce.asInteger(value);
                if (page.isPresent()) {
                    return TextActions.changePage(page.get());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown click action type: " + action);
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static HoverAction<?> parseHoverAction(String action, String value) {
        switch (action) {
            case "show_text":
                return TextActions.showText(Texts.legacy().fromUnchecked(value));
            case "show_achievement":
                return null; // TODO
            case "show_item":
                return null; // TODO
            case "show_entity":
                return null; // TODO
            default:
                throw new IllegalArgumentException("Unknown hover action type: " + action);
        }
    }

    public static RawAction raw(ClickAction<?> clickAction) {
        if (clickAction instanceof ClickAction.ChangePage) {
            return new RawAction("change_page", ((ClickAction.ChangePage) clickAction).getResult().toString());
        } else if (clickAction instanceof ClickAction.OpenUrl) {
            URL url = ((ClickAction.OpenUrl) clickAction).getResult();
            String scheme = url.getProtocol();
            String host = url.getProtocol();
            if ("file".equalsIgnoreCase(scheme) && (host == null || host.equals(""))) {
                return new RawAction("open_file", url.getFile());
            } else {
                return new RawAction("open_url", url.toExternalForm());
            }
        } else if (clickAction instanceof ClickAction.ExecuteCallback) {
            final UUID uniqueId = LanternCallbackHolder.getInstance().getOrCreateIdForCallback(
                    ((ClickAction.ExecuteCallback) clickAction).getResult());
            return new RawAction("run_command", LanternCallbackHolder.CALLBACK_COMMAND_QUALIFIED + " " + uniqueId.toString());
        } else if (clickAction instanceof ClickAction.RunCommand) {
            return new RawAction("run_command", ((ClickAction.RunCommand) clickAction).getResult());
        } else if (clickAction instanceof ClickAction.SuggestCommand) {
            return new RawAction("suggest_command", ((ClickAction.SuggestCommand) clickAction).getResult());
        } else {
            throw new IllegalArgumentException("Unknown click action type: " + clickAction.getClass().getName());
        }
    }

    public static RawAction raw(HoverAction<?> hoverAction) {
        if (hoverAction instanceof HoverAction.ShowText) {
            return new RawAction("show_text", ((HoverAction.ShowText) hoverAction).getResult());
        } else if (hoverAction instanceof HoverAction.ShowAchievement) {
            return new RawAction("show_achievement", ((HoverAction.ShowAchievement) hoverAction).getResult().getId());
        } else if (hoverAction instanceof HoverAction.ShowEntity) {
            return null; // TODO
        } else if (hoverAction instanceof HoverAction.ShowItem) {
            return null; // TODO
        } else {
            throw new IllegalArgumentException("Unknown hover action type: " + hoverAction.getClass().getName());
        }
    }

    public static class RawAction {

        private final String action;

        @Nullable private String value;
        @Nullable private Text text;

        public RawAction(String action, String value) {
            this.action = action;
            this.value = value;
        }

        public RawAction(String action, Text value) {
            this.action = action;
            this.text = value;
        }

        public String getAction() {
            return this.action;
        }

        @SuppressWarnings("deprecation")
        public String getValueAsString() {
            if (this.value != null) {
                return this.value;
            }
            return this.value = Texts.legacy().to(this.text);
        }

        @SuppressWarnings("deprecation")
        public Text getValueAsText() {
            if (this.text != null) {
                return this.text;
            }
            return this.text = Texts.legacy().fromUnchecked(this.value);
        }
    }
}
