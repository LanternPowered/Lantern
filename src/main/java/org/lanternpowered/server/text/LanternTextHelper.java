package org.lanternpowered.server.text;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Coerce;

import com.google.common.base.Optional;

public class LanternTextHelper {

    public static ClickAction<?> parseClickAction(String action, String value) {
        if (action.equals("open_url") || action.equals("open_file")) {
            URI uri = null;
            if (action.equals("open_url")) {
                try {
                    uri = new URI(value);
                } catch (URISyntaxException e) {
                }
            } else {
                uri = new File(value).toURI();
            }
            if (uri != null) {
                try {
                    return TextActions.openUrl(uri.toURL());
                } catch (MalformedURLException e) {
                }
            }
        } else if (action.equals("run_command")) {
            return TextActions.runCommand(value);
        } else if (action.equals("suggest_command")) {
            return TextActions.suggestCommand(value);
        } else if (action.equals("change_page")) {
            Optional<Integer> page = Coerce.asInteger(value);
            if (page.isPresent()) {
                return TextActions.changePage(page.get());
            }
        } else {
            throw new IllegalArgumentException("Unknown click action type: " + action);
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    public static HoverAction<?> parseHoverAction(String action, String value) {
        if (action.equals("show_text")) {
           return TextActions.showText(Texts.legacy().fromUnchecked(value));
        } else if (action.equals("show_achievement")) {
            return null; // TODO
        } else if (action.equals("show_item")) {
            return null; // TODO
        } else if (action.equals("show_entity")) {
            return null; // TODO
        } else {
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

        private String value;
        private Text text;

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
