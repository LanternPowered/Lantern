/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.text.translation;

import org.lanternpowered.server.network.packet.Packet;

import java.util.Locale;

/**
 * If a {@link TranslationContext} is active {@link Text} objects will be translated
 * to be used in network {@link Packet}s to match the clients {@link Locale}.
 * <p>This will only be applied to the current thread, so this will
 * can be used in concurrent environments.</p>
 */
public interface TranslationContext extends AutoCloseable {

    /**
     * Gets the current {@link TranslationContext}.
     *
     * @return The current context
     */
    static TranslationContext current() {
        return LanternTranslationContext.currentContext.get();
    }

    /**
     * Starts a new {@link TranslationContext}.
     *
     * @return The context
     */
    static TranslationContext enter() {
        return new LanternTranslationContext().enter();
    }

    /**
     * Sets the {@link Locale} that should be used within this context.
     *
     * @param locale The locale
     * @return This context, for chaining
     */
    TranslationContext locale(Locale locale);

    /**
     * Prevents the {@link Text} objects from being forced to be translated,
     * this means that the json format doesn't get the translated strings
     * for non-vanilla translations, but the non-translated form (key and args)
     *
     * @return This context, for chaining
     */
    TranslationContext disableForcedTranslation();

    /**
     * Forces the {@link Text} objects to be translated,
     * this means that the json format forces non-vanilla translations
     * to be translated so that they are readable on the client.
     *
     * @return This context, for chaining
     */
    TranslationContext enableForcedTranslations();

    /**
     * Gets whether translations are being forced.
     *
     * @return Are being forced
     */
    boolean forcesTranslations();

    /**
     * Gets the {@link Locale} used by this context.
     *
     * @return The locale
     */
    Locale getLocale();

    @Override
    void close();
}
