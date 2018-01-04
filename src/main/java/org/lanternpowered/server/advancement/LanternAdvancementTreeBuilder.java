package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementTree;

import javax.annotation.Nullable;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class LanternAdvancementTreeBuilder implements AdvancementTree.Builder {

    String id;
    @Nullable String name;
    Advancement rootAdvancement;
    String background;

    public LanternAdvancementTreeBuilder() {
        reset();
    }

    @Override
    public AdvancementTree.Builder rootAdvancement(Advancement rootAdvancement) {
        checkNotNull(rootAdvancement, "rootAdvancement");
        // checkState(((IMixinAdvancement) rootAdvancement).isRegistered(), "The root advancement must be registered.");
        checkState(!rootAdvancement.getParent().isPresent(), "The root advancement cannot have a parent.");
        checkState(rootAdvancement.getDisplayInfo().isPresent(), "The root advancement must have display info.");
        /*checkState(((IMixinDisplayInfo) rootAdvancement.getDisplayInfo().get()).getBackground() == null,
                "The root advancement is already used by a different Advancement Tree.");*/
        this.rootAdvancement = rootAdvancement;
        return this;
    }

    @Override
    public AdvancementTree.Builder background(String background) {
        checkNotNull(background, "background");
        this.background = background;
        return this;
    }

    @Override
    public AdvancementTree.Builder id(String id) {
        checkNotNull(id, "id");
        this.id = id;
        return this;
    }

    @Override
    public AdvancementTree.Builder name(String name) {
        checkNotNull(name, "name");
        this.name = name;
        return this;
    }

    @Override
    public AdvancementTree build() {
        checkState(this.id != null, "The id must be set");
        checkState(this.rootAdvancement != null, "The root advancement must be set");
        final LanternAdvancementTree advancementTree = new LanternAdvancementTree(this);
        // applyTree(this.rootAdvancement, advancementTree);
        return advancementTree;
    }

    /*
    private static void applyTree(Advancement advancement, AdvancementTree tree) {
        ((IMixinAdvancement) advancement).setTree(tree);
        for (Advancement child : advancement.getChildren()) {
            applyTree(child, tree);
        }
    }
*/

    @Override
    public AdvancementTree.Builder reset() {
        this.background = "minecraft:textures/gui/advancements/backgrounds/stone.png";
        this.rootAdvancement = null;
        this.name = null;
        this.id = null;
        return this;
    }
}
