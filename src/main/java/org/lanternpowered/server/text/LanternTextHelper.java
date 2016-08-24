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
package org.lanternpowered.server.text;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.lanternpowered.server.data.translator.JsonTranslator;
import org.lanternpowered.server.entity.LanternEntityType;
import org.lanternpowered.server.text.action.LanternClickActionCallbacks;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Coerce;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import javax.annotation.Nullable;

public final class LanternTextHelper {

    private static final Gson GSON = new Gson();

    private static final DataQuery SHOW_ENTITY_ID = DataQuery.of("id");
    private static final DataQuery SHOW_ENTITY_TYPE = DataQuery.of("type");
    private static final DataQuery SHOW_ENTITY_NAME = DataQuery.of("name");

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
                // Check for a valid click action callback
                Matcher matcher = LanternClickActionCallbacks.COMMAND_PATTERN.matcher(value.trim().toLowerCase());
                if (matcher.matches()) {
                    UUID uniqueId = UUID.fromString(matcher.group(1));
                    Optional<Consumer<CommandSource>> callback = LanternClickActionCallbacks.getInstance().getCallbackForUUID(uniqueId);
                    if (callback.isPresent()) {
                        return TextActions.executeCallback(callback.get());
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
                return TextActions.showText(TextSerializers.LEGACY_FORMATTING_CODE.deserializeUnchecked(value));
            case "show_achievement":
                return null; // TODO
            case "show_item":
                return null; // TODO
            case "show_entity":
                DataView dataView = JsonTranslator.instance().translate(GSON.fromJson(value, JsonObject.class));

                UUID uuid = UUID.fromString(dataView.getString(SHOW_ENTITY_ID).get());
                String name = dataView.getString(SHOW_ENTITY_NAME).get();
                EntityType entityType = null;
                if (dataView.contains(SHOW_ENTITY_TYPE)) {
                    entityType = Sponge.getRegistry().getType(EntityType.class, dataView.getString(SHOW_ENTITY_TYPE).get()).orElse(null);
                }

                return TextActions.showEntity(uuid, name, entityType);
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
            final UUID uniqueId = LanternClickActionCallbacks.getInstance().getOrCreateIdForCallback(
                    ((ClickAction.ExecuteCallback) clickAction).getResult());
            return new RawAction("run_command", LanternClickActionCallbacks.COMMAND_BASE + uniqueId.toString());
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
            HoverAction.ShowEntity.Ref ref = ((HoverAction.ShowEntity) hoverAction).getResult();

            DataContainer dataContainer = new MemoryDataContainer();
            dataContainer.set(SHOW_ENTITY_ID, ref.getUniqueId().toString());
            dataContainer.set(SHOW_ENTITY_NAME, ref.getName());
            ref.getType().ifPresent(type -> dataContainer.set(SHOW_ENTITY_TYPE, ((LanternEntityType) type).getMinecraftId()));

            return new RawAction("show_entity", GSON.toJson(JsonTranslator.instance().translate(dataContainer)));
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
            return this.value = LanternTexts.toLegacy(this.text);
        }

        @SuppressWarnings("deprecation")
        public Text getValueAsText() {
            if (this.text != null) {
                return this.text;
            }
            return this.text = LanternTexts.fromLegacy(this.value);
        }
    }

    private LanternTextHelper() {
    }

}
