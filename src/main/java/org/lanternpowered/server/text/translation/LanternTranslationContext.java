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

import io.netty.util.concurrent.FastThreadLocal;
import org.lanternpowered.api.util.concurrent.ThreadLocals;
import org.spongepowered.api.text.translation.locale.Locales;

import java.util.Locale;

import javax.annotation.Nullable;

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
