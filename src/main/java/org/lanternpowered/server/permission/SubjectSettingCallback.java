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
package org.lanternpowered.server.permission;

import org.lanternpowered.server.service.permission.UserCollection;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectCollection;

import java.lang.ref.WeakReference;
import java.util.function.Predicate;

import javax.annotation.Nullable;

class SubjectSettingCallback implements Predicate<PermissionService> {

    private final WeakReference<AbstractSubject> ref;

    SubjectSettingCallback(AbstractSubject ref) {
        this.ref = new WeakReference<>(ref);
    }

    @Override
    public boolean test(@Nullable PermissionService input) {
        return apply(this.ref.get(), input);
    }

    public static boolean apply(@Nullable AbstractSubject ref, @Nullable PermissionService input) {
        if (ref == null) {
            return false;
        }
        if (input == null) {
            return true;
        }
        final SubjectCollection userSubjects = input.getSubjects(ref.getSubjectCollectionIdentifier());
        //noinspection ConstantConditions
        if (userSubjects != null) {
            final Subject subject;
            if (ref instanceof User && userSubjects instanceof UserCollection) {
                // GameProfile is already resolved, use it directly
                subject = ((UserCollection) userSubjects).get(((User) ref).getProfile());
            } else {
                subject = userSubjects.get(ref.getIdentifier());
            }
            ref.setInternalSubject(subject);
        }
        return true;
    }

}
