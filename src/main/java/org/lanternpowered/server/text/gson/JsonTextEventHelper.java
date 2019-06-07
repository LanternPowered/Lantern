/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
package org.lanternpowered.server.text.gson;

import static org.lanternpowered.server.util.UncheckedThrowables.throwUnchecked;

import com.google.gson.JsonParseException;
import org.lanternpowered.server.data.io.store.item.ItemStackStore;
import org.lanternpowered.server.data.persistence.json.JsonDataFormat;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.network.item.ItemStackContextualValueType;
import org.lanternpowered.server.text.LanternTexts;
import org.lanternpowered.server.text.action.LanternClickActionCallbacks;
import org.lanternpowered.server.text.translation.TranslationContext;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.action.ClickAction;
import org.spongepowered.api.text.action.HoverAction;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.util.Coerce;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import org.checkerframework.checker.nullness.qual.Nullable;

final class JsonTextEventHelper {

    private static final DataQuery SHOW_ENTITY_ID = DataQuery.of("id");
    private static final DataQuery SHOW_ENTITY_TYPE = DataQuery.of("type");
    private static final DataQuery SHOW_ENTITY_NAME = DataQuery.of("name");

    @Nullable
    static ClickAction<?> parseClickAction(String action, String value) {
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
                final Matcher matcher = LanternClickActionCallbacks.COMMAND_PATTERN.matcher(value.trim().toLowerCase());
                if (matcher.matches()) {
                    final UUID uniqueId = UUID.fromString(matcher.group(1));
                    final Optional<Consumer<CommandSource>> callback = LanternClickActionCallbacks.get().getCallbackForUUID(uniqueId);
                    if (callback.isPresent()) {
                        return TextActions.executeCallback(callback.get());
                    }
                }
                return TextActions.runCommand(value);
            case "suggest_command":
                return TextActions.suggestCommand(value);
            case "change_page":
                final Optional<Integer> page = Coerce.asInteger(value);
                if (page.isPresent()) {
                    return TextActions.changePage(page.get());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown click action type: " + action);
        }

        return null;
    }

    static HoverAction<?> parseHoverAction(String action, String value) throws JsonParseException {
        final DataView dataView;
        switch (action) {
            case "show_text":
                return TextActions.showText(LanternTexts.fromLegacy(value));
            case "show_item":
                try {
                    dataView = JsonDataFormat.readContainer(value, false);
                } catch (IOException e) {
                    throw new JsonParseException("Failed to parse the item data container", e);
                }
                ItemStackContextualValueType.deserializeFromNetwork(dataView);
                final ItemStack itemStack = ItemStackStore.INSTANCE.deserialize(dataView);
                return TextActions.showItem(itemStack.createSnapshot());
            case "show_entity":
                try {
                    dataView = JsonDataFormat.readContainer(value, false);
                } catch (IOException e) {
                    throw new JsonParseException("Failed to parse the entity data container", e);
                }

                final UUID uuid = UUID.fromString(dataView.getString(SHOW_ENTITY_ID).get());
                final String name = dataView.getString(SHOW_ENTITY_NAME).get();
                EntityType entityType = null;
                if (dataView.contains(SHOW_ENTITY_TYPE)) {
                    entityType = Sponge.getRegistry().getType(EntityType.class,
                            CatalogKey.resolve(dataView.getString(SHOW_ENTITY_TYPE).get())).orElse(null);
                }

                return TextActions.showEntity(uuid, name, entityType);
            default:
                throw new IllegalArgumentException("Unknown hover action type: " + action);
        }
    }

    static RawAction raw(ClickAction<?> clickAction) {
        if (clickAction instanceof ClickAction.ChangePage) {
            return new RawAction("change_page", ((ClickAction.ChangePage) clickAction).getResult().toString());
        } else if (clickAction instanceof ClickAction.OpenUrl) {
            final URL url = ((ClickAction.OpenUrl) clickAction).getResult();
            final String scheme = url.getProtocol();
            final String host = url.getHost();
            if ("file".equalsIgnoreCase(scheme) && (host == null || host.equals(""))) {
                return new RawAction("open_file", url.getFile());
            } else {
                return new RawAction("open_url", url.toExternalForm());
            }
        } else if (clickAction instanceof ClickAction.ExecuteCallback) {
            final UUID uniqueId = LanternClickActionCallbacks.get().getOrCreateIdForCallback(
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

    static RawAction raw(HoverAction<?> hoverAction) {
        if (hoverAction instanceof HoverAction.ShowText) {
            return new RawAction("show_text", LanternTexts.toLegacy(((HoverAction.ShowText) hoverAction).getResult()));
        } else if (hoverAction instanceof HoverAction.ShowEntity) {
            final HoverAction.ShowEntity.Ref ref = ((HoverAction.ShowEntity) hoverAction).getResult();

            final DataContainer dataContainer = DataContainer.createNew()
                    .set(SHOW_ENTITY_ID, ref.getUniqueId().toString())
                    .set(SHOW_ENTITY_NAME, ref.getName());
            ref.getType().ifPresent(type -> dataContainer.set(SHOW_ENTITY_TYPE, type.getKey().toString()));

            try {
                return new RawAction("show_entity", JsonDataFormat.writeAsString(dataContainer));
            } catch (IOException e) {
                throw throwUnchecked(e);
            }
        } else if (hoverAction instanceof HoverAction.ShowItem) {
            final ItemStackSnapshot itemStackSnapshot = ((HoverAction.ShowItem) hoverAction).getResult();
            final LanternItemStack itemStack = (LanternItemStack) itemStackSnapshot.createStack();
            final TranslationContext ctx = TranslationContext.current();
            final DataView dataView;
            if (ctx.forcesTranslations()) {
                dataView = ItemStackContextualValueType.serializeForNetwork(itemStack);
            } else {
                dataView = ItemStackStore.INSTANCE.serialize(itemStack);
            }
            try {
                return new RawAction("show_item", JsonDataFormat.writeAsString(dataView));
            } catch (IOException e) {
                throw throwUnchecked(e);
            }
        } else {
            throw new IllegalArgumentException("Unknown hover action type: " + hoverAction.getClass().getName());
        }
    }

    static final class RawAction {

        final String action;
        final String value;

        RawAction(String action, String value) {
            this.action = action;
            this.value = value;
        }
    }

    private JsonTextEventHelper() {
    }

}
