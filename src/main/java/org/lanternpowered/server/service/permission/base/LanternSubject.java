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
package org.lanternpowered.server.service.permission.base;

import org.spongepowered.api.service.context.Context;
import org.spongepowered.api.service.permission.MemorySubjectData;
import org.spongepowered.api.service.permission.PermissionService;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.service.permission.SubjectReference;
import org.spongepowered.api.util.Tristate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public abstract class LanternSubject implements Subject {

    public abstract PermissionService getService();

    @Override
    public MemorySubjectData getTransientSubjectData() {
        return getSubjectData();
    }

    @Override
    public abstract MemorySubjectData getSubjectData();

    @Override
    public boolean isSubjectDataPersisted() {
        return false;
    }

    @Override
    public SubjectReference asSubjectReference() {
        return getService().newSubjectReference(getContainingCollection().getIdentifier(), getIdentifier());
    }

    @Override
    public boolean hasPermission(Set<Context> contexts, String permission) {
        return getPermissionValue(contexts, permission) == Tristate.TRUE;
    }

    @Override
    public Tristate getPermissionValue(Set<Context> contexts, String permission) {
        return getDataPermissionValue(getTransientSubjectData(), permission);
    }

    protected Tristate getDataPermissionValue(MemorySubjectData subject, String permission) {
        Tristate res = subject.getNodeTree(SubjectData.GLOBAL_CONTEXT).get(permission);
        if (res == Tristate.UNDEFINED) {
            for (SubjectReference parent : subject.getParents(SubjectData.GLOBAL_CONTEXT)) {
                res = parent.resolve().join().getPermissionValue(SubjectData.GLOBAL_CONTEXT, permission);
                if (res != Tristate.UNDEFINED) {
                    return res;
                }
            }
        }
        return res;
    }

    @Override
    public boolean isChildOf(Set<Context> contexts, SubjectReference parent) {
        return getSubjectData().getParents(contexts).contains(parent);
    }

    @Override
    public List<SubjectReference> getParents(Set<Context> contexts) {
        return getSubjectData().getParents(contexts);
    }

    protected Optional<String> getDataOptionValue(MemorySubjectData subject, String option) {
        Optional<String> res = Optional.ofNullable(subject.getOptions(SubjectData.GLOBAL_CONTEXT).get(option));
        if (!res.isPresent()) {
            for (SubjectReference parent : subject.getParents(SubjectData.GLOBAL_CONTEXT)) {
                res = parent.resolve().join().getOption(SubjectData.GLOBAL_CONTEXT, option);
                if (res.isPresent()) {
                    return res;
                }
            }
        }
        return res;
    }

    @Override
    public Optional<String> getOption(Set<Context> contexts, String key) {
        return getDataOptionValue(getTransientSubjectData(), key);
    }

    @Override
    public Set<Context> getActiveContexts() {
        return SubjectData.GLOBAL_CONTEXT;
    }
}
