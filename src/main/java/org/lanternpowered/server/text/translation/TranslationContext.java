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
package org.lanternpowered.server.text.translation;

import org.lanternpowered.server.network.message.Message;
import org.spongepowered.api.text.Text;

import java.util.Locale;

/**
 * If a {@link TranslationContext} is active {@link Text} objects will be translated
 * to be used in network {@link Message}s to match the clients {@link Locale}.
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
