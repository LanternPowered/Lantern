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

import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.api.util.concurrent.ThreadLocals;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

final class LanternTranslationContext implements TranslationContext {

    static final FastThreadLocal<TranslationContext> currentContext =
            ThreadLocals.of(() -> new LanternTranslationContext().locale(Locales.DEFAULT));

    @Nullable private TranslationContext previousContext;
    private Locale locale;
    private boolean forcesTranslations;

    LanternTranslationContext() {
    }

    TranslationContext enter() {
        this.previousContext = currentContext.get();
        currentContext.set(this);
        if (this.previousContext != null) {
            this.locale = this.previousContext.getLocale();
            this.forcesTranslations = this.previousContext.forcesTranslations();
        } else {
            this.locale = Locales.DEFAULT;
        }
        return this;
    }

    @Override
    public TranslationContext locale(Locale locale) {
        this.locale = locale;
        return this;
    }

    @Override
    public TranslationContext disableForcedTranslation() {
        this.forcesTranslations = false;
        return this;
    }

    @Override
    public TranslationContext enableForcedTranslations() {
        this.forcesTranslations = true;
        return this;
    }

    @Override
    public boolean forcesTranslations() {
        return this.forcesTranslations;
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }

    @Override
    public void close() {
        if (this.previousContext != null) {
            currentContext.set(this.previousContext);
        } else {
            currentContext.remove();
        }
    }
}
