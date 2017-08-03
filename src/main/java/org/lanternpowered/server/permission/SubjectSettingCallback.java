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
package org.lanternpowered.server.permission;

import org.lanternpowered.server.service.permission.LanternPermissionService;
import org.lanternpowered.server.service.permission.UserCollection;
import org.lanternpowered.server.service.permission.base.LanternSubjectCollection;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.SubjectReference;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

import javax.annotation.Nullable;

final class SubjectSettingCallback implements Predicate<PermissionService> {

    private final WeakReference<ProxySubject> ref;

    SubjectSettingCallback(ProxySubject ref) {
        this.ref = new WeakReference<>(ref);
    }

    @Override
    public boolean test(@Nullable PermissionService input) {
        return apply(this.ref.get(), input);
    }

    static boolean apply(@Nullable ProxySubject ref, @Nullable PermissionService input) {
        if (ref == null) {
            // returning false from this predicate means this setting callback will be removed
            // as a listener, and will not be tested again.
            return false;
        }
        // if PS has just been unregistered, ignore the change.
        if (input == null) {
            return true;
        }

        final SubjectReference subject;

        // check if we're using the native Lantern impl
        // we can skip some unnecessary instance creation this way.
        if (input instanceof LanternPermissionService) {
            final LanternPermissionService service = (LanternPermissionService) input;
            final LanternSubjectCollection collection = service.get(ref.getSubjectCollectionIdentifier());

            if (ref instanceof User && collection instanceof UserCollection) {
                // GameProfile is already resolved, use it directly
                subject = collection.get(((User) ref).getProfile().getUniqueId().toString()).asSubjectReference();
            } else {
                subject = collection.get(ref.getIdentifier()).asSubjectReference();
            }
        } else {
            // build a new subject reference using the permission service
            // this doesn't actually load the subject, so it will be lazily init'd when needed.
            subject = input.newSubjectReference(ref.getSubjectCollectionIdentifier(), ref.getIdentifier());
        }

        ref.setInternalSubject(subject);
        return true;
    }

}
