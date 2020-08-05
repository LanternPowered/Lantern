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
package org.lanternpowered.server.advancement;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.advancement.criteria.AbstractCriterion;
import org.lanternpowered.server.advancement.criteria.EmptyCriterion;
import org.lanternpowered.server.advancement.criteria.LanternAndCriterion;
import org.lanternpowered.server.advancement.criteria.LanternCriterion;
import org.lanternpowered.server.advancement.criteria.LanternOrCriterion;
import org.lanternpowered.server.advancement.criteria.LanternScoreCriterion;
import org.lanternpowered.server.advancement.criteria.progress.AbstractCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternAndCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternEmptyCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternOrCriterionProgress;
import org.lanternpowered.server.advancement.criteria.progress.LanternScoreCriterionProgress;
import org.lanternpowered.server.entity.player.LanternPlayer;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.advancement.Advancement;
import org.spongepowered.api.advancement.AdvancementProgress;
import org.spongepowered.api.advancement.DisplayInfo;
import org.spongepowered.api.advancement.criteria.AdvancementCriterion;
import org.spongepowered.api.advancement.criteria.CriterionProgress;
import org.spongepowered.api.advancement.criteria.ScoreAdvancementCriterion;
import org.spongepowered.api.advancement.criteria.ScoreCriterionProgress;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.advancement.AdvancementEvent;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.message.MessageEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.gamerule.GameRules;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LanternAdvancementProgress implements AdvancementProgress {

    private final LanternPlayerAdvancements playerAdvancements;
    private final LanternAdvancement advancement;

    private final Map<AdvancementCriterion, AbstractCriterionProgress> progress = new HashMap<>();

    private boolean achievedState;

    // Whether the progress of the advancement should be updated
    boolean dirtyProgress;

    // Whether the visibility of the advancement should be updated
    boolean dirtyVisibility;

    // Whether the advancement is currently visible
    boolean visible;

    LanternAdvancementProgress(LanternPlayerAdvancements playerAdvancements, LanternAdvancement advancement) {
        this.playerAdvancements = playerAdvancements;
        this.advancement = advancement;
        for (AdvancementCriterion criterion : AbstractCriterion.getRecursiveCriteria(this.advancement.getCriterion())) {
            final AbstractCriterionProgress<?> progress;
            if (criterion instanceof LanternAndCriterion) {
                progress = new LanternAndCriterionProgress((LanternAndCriterion) criterion, this);
            } else if (criterion instanceof LanternOrCriterion) {
                progress = new LanternOrCriterionProgress((LanternOrCriterion) criterion, this);
            } else if (criterion instanceof LanternScoreCriterion) {
                progress = new LanternScoreCriterionProgress((LanternScoreCriterion) criterion, this);
            } else if (criterion instanceof LanternCriterion) {
                progress = new LanternCriterionProgress((LanternCriterion) criterion, this);
            } else if (criterion instanceof EmptyCriterion) {
                progress = new LanternEmptyCriterionProgress((EmptyCriterion) criterion, this);
            } else {
                throw new IllegalStateException("Unsupported criterion: " + criterion);
            }
            progress.attachTrigger();
            this.progress.put(criterion, progress);
        }
    }

    void cleanup() {
        for (AbstractCriterionProgress progress : this.progress.values()) {
            progress.detachTrigger();
        }
    }

    void loadProgress(Map<String, Instant> progressMap) {
        for (AbstractCriterionProgress progress : this.progress.values()) {
            progress.loadProgress(progressMap);
        }
        for (AbstractCriterionProgress progress : this.progress.values()) {
            if (progress.achieved()) {
                progress.detachTrigger();
            } else {
                progress.attachTrigger();
            }
        }
        this.achievedState = achieved();
    }

    Map<String, Instant> saveProgress() {
        final Map<String, Instant> progressMap = new HashMap<>();
        for (Map.Entry<AdvancementCriterion, AbstractCriterionProgress> entry : this.progress.entrySet()) {
            entry.getValue().saveProgress(progressMap);
        }
        return progressMap;
    }

    @Override
    public Advancement getAdvancement() {
        return this.advancement;
    }

    @Override
    public Optional<ScoreCriterionProgress> get(ScoreAdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        return Optional.ofNullable((ScoreCriterionProgress) this.progress.get(criterion));
    }

    @Override
    public Optional<CriterionProgress> get(AdvancementCriterion criterion) {
        checkNotNull(criterion, "criterion");
        return Optional.ofNullable(this.progress.get(criterion));
    }

    @Override
    public Optional<Instant> get() {
        return get(this.advancement.getCriterion()).get().get();
    }

    @Override
    public Instant grant() { // TODO: Make this a optional in the API
        return get(this.advancement.getCriterion()).get().grant();
    }

    @Override
    public Optional<Instant> revoke() {
        return get(this.advancement.getCriterion()).get().revoke();
    }

    public LanternPlayerAdvancements getPlayerAdvancements() {
        return this.playerAdvancements;
    }

    public LanternPlayer getPlayer() {
        return this.playerAdvancements.getPlayer();
    }

    public void invalidateAchievedState() {
        // Invalidate the achieved state of all the criteria progress
        this.progress.values().forEach(AbstractCriterionProgress::invalidateAchievedState);
        // Get the new achieved state
        final boolean achievedState = achieved();
        if (!this.achievedState && achievedState) {
            // The advancement got granted
            this.dirtyVisibility = true;
            for (AbstractCriterionProgress progress : this.progress.values()) {
                progress.detachTrigger();
            }
            final Optional<DisplayInfo> optDisplay = this.advancement.getDisplayInfo();
            final boolean sendMessage = getPlayer().getWorld().getGameRule(GameRules.ANNOUNCE_ADVANCEMENTS) &&
                    optDisplay.map(DisplayInfo::doesAnnounceToChat).orElse(false);
            final Text message = optDisplay.<Text>map(display -> {
                final Translation translation = tr("chat.type.advancement." + display.getType().getKey().getValue());
                return Text.of(translation, getPlayer().getName(), this.advancement.toText());
            }).orElseGet(() -> Text.of(getPlayer().getName() + " achieved ", this.advancement.toText()));
            final MessageEvent.MessageFormatter formatter = new MessageEvent.MessageFormatter(message);
            final Cause cause = CauseStack.current().getCurrentCause();
            final Instant instant = get().orElseThrow(() -> new IllegalStateException("Something funky happened"));
            final AdvancementEvent.Grant event = SpongeEventFactory.createAdvancementEventGrant(cause, MessageChannel.toPlayers(),
                    Optional.of(MessageChannel.toPlayers()), this.advancement, formatter, getPlayer(), instant, !sendMessage);
            Sponge.getEventManager().post(event);
            if (!event.isMessageCancelled()) {
                event.getChannel().ifPresent(channel -> channel.send(event.getMessage()));
            }
        } else if (this.achievedState && !achievedState) {
            // The advancement got revoked
            this.dirtyVisibility = true;
            for (AbstractCriterionProgress progress : this.progress.values()) {
                if (!progress.achieved()) {
                    progress.attachTrigger();
                }
            }
            final Cause cause = CauseStack.current().getCurrentCause();
            final AdvancementEvent.Revoke event = SpongeEventFactory.createAdvancementEventRevoke(
                    cause, this.advancement, getPlayer());
            Sponge.getEventManager().post(event);
        }
        this.achievedState = achievedState;
        // The progress should be updated
        this.dirtyProgress = true;
        this.playerAdvancements.dirtyProgress.add(this);
    }

    Object2LongMap<String> collectProgress() {
        final Object2LongMap<String> progress = new Object2LongOpenHashMap<>();
        this.advancement.clientCriteria.getFirst().forEach(criterion -> this.progress.get(criterion).fillProgress(progress));
        return progress;
    }
}
