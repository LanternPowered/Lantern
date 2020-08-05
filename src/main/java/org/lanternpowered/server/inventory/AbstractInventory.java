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
package org.lanternpowered.server.inventory;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.common.reflect.TypeToken;
import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.data.property.PropertyHolderBase;
import org.lanternpowered.server.data.property.LanternPropertyRegistry;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.inventory.query.LanternQueryOperation;
import org.lanternpowered.server.item.predicate.ItemPredicate;
import org.spongepowered.api.data.property.Property;
import org.spongepowered.api.data.property.provider.PropertyProvider;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.lang.reflect.TypeVariable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * The base implementation for all the {@link Inventory}s.
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public abstract class AbstractInventory implements IInventory {

    /**
     * Represents a invalid slot index.
     */
    static final int INVALID_SLOT_INDEX = -1;

    private static final TypeVariable<Class<CarriedInventory>> carrierTypeVariable = CarriedInventory.class.getTypeParameters()[0];
    private static LoadingCache<Class<?>, Class<? extends Carrier>> carrierTypeCache = Caffeine.newBuilder()
            .weakKeys()
            .build(key -> (Class) TypeToken.of(key).resolveType(carrierTypeVariable).getRawType());

    static class Name {
        static final Translation INVENTORY = tr("inventory.name"); // The default name
        static final Translation SLOT = tr("inventory.slot.name"); // The default slot name
        static final Translation CONTAINER = tr("inventory.container.name"); // The default container name
    }

    @Nullable private Translation name;
    @Nullable private AbstractInventory parent;
    @Nullable private AbstractInventory cachedRoot;

    /**
     * The reference of the carrier of this inventory, will only
     * be used if this class implements {@link ICarriedInventory}.
     */
    @Nullable private CarrierReference carrierReference;

    /**
     * The viewers of this inventory, will only be used if this
     * class implements {@link IViewableInventory}.
     */
    @Nullable private Map<Player, AbstractContainer> viewers;

    /**
     * A map with all the event listeners attached to this inventory.
     */
    @Nullable private Multimap<Class<? extends Event>, Consumer<? super Event>> eventListeners;

    protected AbstractInventory() {
        if (this instanceof ICarriedInventory) {
            this.carrierReference = CarrierReference.of(carrierTypeCache.get(getClass()));
        }
        if (this instanceof IViewableInventory) {
            this.viewers = new ConcurrentHashMap<>();
        }
    }

    @Nullable
    Multimap<Class<? extends Event>, Consumer<? super Event>> getEventListeners() {
        return this.eventListeners;
    }

    void setEventListeners(@Nullable Multimap<Class<? extends Event>, Consumer<? super Event>> eventListeners) {
        this.eventListeners = eventListeners;
    }

    /**
     * Adds a {@link Player} viewer to this inventory.
     *
     * @param player The player
     * @param container The container
     */
    void addViewer(Player player, AbstractContainer container) {
        if (this.viewers != null) {
            this.viewers.put(player, container);
        }
    }

    /**
     * Removes a {@link Player} viewer from this inventory.
     *
     * @param player The player
     * @param container The container
     */
    void removeViewer(Player player, AbstractContainer container) {
        if (this.viewers != null) {
            this.viewers.remove(player, container);
        }
    }

    @Nullable
    Map<Player, AbstractContainer> getViewersMap() {
        return this.viewers;
    }

    /**
     * Gets the {@link CarrierReference} of this inventory.
     *
     * @return  The carrier reference
     */
    @Nullable
    CarrierReference getCarrierReference() {
        return this.carrierReference;
    }

    /**
     * Sets the {@link Carrier} of this {@link Inventory}.
     *
     * @param carrier The carrier
     * @param override Whether the carrier should be overridden even if one was already assigned
     */
    void setCarrier(Carrier carrier, boolean override) {
        if (this.carrierReference != null &&
                (override || !this.carrierReference.get().isPresent())) {
            this.carrierReference.set(carrier);
        }
    }

    /**
     * Sets the name of this inventory.
     *
     * @param name The name
     */
    void setName(Translation name) {
        this.name = name;
    }

    /**
     * Sets the parent {@link Inventory} of this inventory.
     *
     * @param parent The parent inventory
     */
    void setParent(@Nullable AbstractInventory parent) {
        this.parent = parent;
        this.cachedRoot = null;
    }

    /**
     * Sets the parent {@link Inventory} of this inventory safely. This
     * doesn't override the current parent if already set.
     *
     * @param parent The parent inventory
     */
    void setParentSafely(AbstractInventory parent) {
        if (this.parent == null) {
            setParent(parent);
        }
    }

    protected void init() {
    }

    /**
     * Gets the {@link EmptyInventory} as a
     * result of type {@link T}.
     *
     * @param <T> The inventory type
     * @return The empty inventory as T
     */
    protected final <T extends Inventory> T genericEmpty() {
        return (T) empty();
    }

    /**
     * Gets all the {@link AbstractSlot}s that could be found in the
     * children inventories. This method may return a empty {@link List}
     * if the subclass doesn't support children, for example slots and
     * empty inventories.
     *
     * @return The slots
     */
    protected abstract List<AbstractSlot> getSlots();

    /**
     * Gets all the children inventories.
     *
     * @return The children inventories
     */
    protected abstract List<? extends AbstractInventory> getChildren();

    /**
     * Attempts to peek offer the specified {@link ItemStack} to this inventory.
     *
     * <p>The input stack will be (partially) consumed in the process.</p>
     *
     * @param stack The item stack
     * @param transactionAdder The transaction adder, can be {@code null} if not collecting transactions
     */
    protected abstract void peekOffer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder);

    /**
     * Attempts to offer the specified {@link ItemStack} to this inventory.
     *
     * <p>The input stack will be (partially) consumed in the process.</p>
     *
     * @param stack The item stack
     * @param transactionAdder The transaction adder, can be {@code null} if not collecting transactions
     */
    protected abstract void offer(ItemStack stack, @Nullable Consumer<SlotTransaction> transactionAdder);

    /**
     * Attempts to set the specified {@link ItemStack} to this inventory.
     *
     * @param stack The item stack
     * @param force With force, ignores any filter
     * @param transactionAdder The transaction adder, can be {@code null} if not collecting transactions
     */
    protected abstract void set(ItemStack stack, boolean force, @Nullable Consumer<SlotTransaction> transactionAdder);

    void close(CauseStack causeStack) {
    }

    @Override
    public Optional<ViewableInventory> asViewable() {
        if (this instanceof ViewableInventory) {
            return Optional.of((ViewableInventory) this);
        }
        return Optional.ofNullable(toViewable());
    }

    /**
     * Converts this inventory into a {@link ViewableInventory}.
     *
     * @return The viewable inventory
     */
    @Nullable
    protected abstract ViewableInventory toViewable();

    @Override
    public List<Slot> slots() {
        return (List) getSlots();
    }

    @Override
    public List<Inventory> children() {
        return (List) getChildren();
    }

    // Basic inventory stuff

    @Override
    public PluginContainer getPlugin() {
        // Use the plugin container from the parent if possible
        return this.parent == null ? Lantern.getMinecraftPlugin() : this.parent.getPlugin();
    }

    @Override
    public Translation getNameTranslation() {
        if (this.name != null) {
            return this.name;
        }
        if (this instanceof Slot) {
            return Name.SLOT;
        } else if (this instanceof Container) {
            return Name.CONTAINER;
        }
        return Name.INVENTORY;
    }

    @Override
    public AbstractInventory parent() {
        return this.parent == null ? this : this.parent;
    }

    @Override
    public AbstractInventory root() {
        if (this.cachedRoot != null) {
            return this.cachedRoot;
        }
        AbstractInventory parent = this;
        AbstractInventory parent1;
        while ((parent1 = parent.parent()) != parent) {
            parent = parent1;
        }
        return this.cachedRoot = parent;
    }

    // Queries

    static final class ChildrenInventoryQuery extends AbstractChildrenInventory implements IQueryInventory {
    }

    @Override
    public IQueryInventory query(QueryOperation<?>... operations) {
        final Set<AbstractInventory> inventories = new LinkedHashSet<>();
        try {
            queryInventories(inventory -> {
                checkNotNull(inventory, "inventory");
                for (QueryOperation operation : operations) {
                    if (((LanternQueryOperation) operation).test(inventory)) {
                        inventories.add((AbstractInventory) inventory);
                        return;
                    }
                }
            });
        } catch (QueryInventoryAdder.Stop ignored) {
        }
        if (inventories.isEmpty()) {
            return genericEmpty();
        }
        final ChildrenInventoryQuery result = new ChildrenInventoryQuery();
        result.initWithChildren((List) ImmutableList.copyOf(inventories), true);
        return result;
    }

    private static final class FirstQueriedInventoryFunction<T extends Inventory> implements QueryInventoryAdder {

        private final Predicate<T> predicate;
        @Nullable T inventory;

        private FirstQueriedInventoryFunction(Predicate<T> predicate) {
            this.predicate = predicate;
        }

        @Override
        public void add(Inventory inventory) {
            checkNotNull(inventory, "inventory");
            if (this.inventory != null) {
                throw Stop.INSTANCE;
            } else if (this.predicate.test((T) inventory)) {
                this.inventory = (T) inventory;
                throw Stop.INSTANCE;
            }
        }
    }

    @Override
    public <T extends Inventory> Optional<T> query(Class<T> inventoryType) {
        final FirstQueriedInventoryFunction<T> function = new FirstQueriedInventoryFunction<>(inventoryType::isInstance);
        try {
            queryInventories(function);
        } catch (QueryInventoryAdder.Stop ignored) {
        }
        return Optional.ofNullable(function.inventory);
    }

    /**
     * Queries for {@link Inventory}s that are located within this inventory. The inventories
     * should be added through the given adder function.
     * <p>The adder can throw {@link QueryInventoryAdder.Stop} to indicate that it's no
     * longer needed to add more inventories. See {@link QueryInventoryAdder#add(Inventory)}.
     *
     * @param adder The adder function
     */
    protected abstract void queryInventories(QueryInventoryAdder adder) throws QueryInventoryAdder.Stop;

    @Override
    public boolean offerFast(ItemStack stack) {
        offer(stack, null);
        return stack.isEmpty();
    }

    // Peek/poll operations

    @Override
    public boolean canFit(ItemStack stack) {
        checkNotNull(stack, "stack");
        final int quantity = stack.getQuantity();
        peekOffer(stack, null);
        try {
            return stack.isEmpty();
        } finally {
            stack.setQuantity(quantity);
        }
    }

    @Override
    public LanternItemStack poll() {
        return poll(stack -> true);
    }

    @Override
    public LanternItemStack poll(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(stack -> stack.getType().equals(itemType));
    }

    @Override
    public LanternItemStack poll(ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return poll(itemPredicate.asStackPredicate());
    }

    @Override
    public LanternItemStack poll(int limit) {
        return poll(limit, stack -> true);
    }

    @Override
    public LanternItemStack poll(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(limit, stack -> stack.getType().equals(itemType));
    }

    @Override
    public LanternItemStack poll(int limit, ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return poll(limit, itemPredicate.asStackPredicate());
    }

    @Override
    public LanternItemStack peek() {
        return peek(stack -> true);
    }

    @Override
    public LanternItemStack peek(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(stack -> stack.getType().equals(itemType));
    }

    @Override
    public LanternItemStack peek(ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return peek(itemPredicate.asStackPredicate());
    }

    @Override
    public LanternItemStack peek(int limit) {
        return peek(limit, stack -> true);
    }

    @Override
    public LanternItemStack peek(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(limit, stack -> stack.getType().equals(itemType));
    }

    @Override
    public LanternItemStack peek(int limit, ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return peek(limit, itemPredicate.asStackPredicate());
    }

    @Override
    public PeekedPollTransactionResult peekPoll(ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return peekPoll(itemPredicate.asStackPredicate());
    }

    @Override
    public PeekedPollTransactionResult peekPoll(int limit, ItemPredicate itemPredicate) {
        checkNotNull(itemPredicate, "itemPredicate");
        return peekPoll(limit, itemPredicate.asStackPredicate());
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack stack) {
        checkNotNull(stack, "stack");
        final ImmutableList.Builder<SlotTransaction> transactionsBuilder = ImmutableList.builder();
        peekOffer(stack, transactionsBuilder::add);
        return new PeekedOfferTransactionResult(transactionsBuilder.build(), stack.createSnapshot());
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        final InventoryTransactionResult.Builder builder = InventoryTransactionResult.builder();
        offer(stack, builder::transaction);
        if (stack.isEmpty()) {
            builder.type(InventoryTransactionResult.Type.SUCCESS);
        } else {
            builder.type(InventoryTransactionResult.Type.FAILURE).reject(stack);
        }
        return builder.build();
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack, boolean force) {
        final InventoryTransactionResult.Builder builder = InventoryTransactionResult.builder();
        set(stack, force, builder::transaction);
        if (stack.isEmpty()) {
            builder.type(InventoryTransactionResult.Type.SUCCESS);
        } else {
            builder.type(InventoryTransactionResult.Type.FAILURE).reject(stack);
        }
        return builder.build();
    }

    @Override
    public Optional<ItemStack> pollFrom(int index) {
        return getSlot(index).map(Inventory::poll);
    }

    @Override
    public Optional<ItemStack> pollFrom(int index, int limit) {
        return getSlot(index).map(slot -> slot.poll(limit));
    }

    @Override
    public Optional<ItemStack> peekAt(int index) {
        return getSlot(index).map(Inventory::peek);
    }

    @Override
    public Optional<ItemStack> peekAt(int index, int limit) {
        return getSlot(index).map(slot -> slot.peek(limit));
    }

    @Override
    public InventoryTransactionResult offer(int index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.offer(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    @Override
    public InventoryTransactionResult set(int index, ItemStack stack) {
        return getSlot(index).map(slot -> slot.set(stack)).orElse(CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS);
    }

    // Properties

    @Override
    public final <V> Optional<V> getProperty(Property<V> property) {
        final Optional<V> optValue = getInventoryProperty(property);
        if (optValue.isPresent()) {
            return optValue;
        }
        return LanternPropertyRegistry.INSTANCE.getStoreForInventory(property).getFor(this);
    }

    @Override
    public final OptionalDouble getDoubleProperty(Property<Double> property) {
        return getProperty(property).map(OptionalDouble::of).orElse(OptionalDouble.empty());
    }

    @Override
    public final OptionalInt getIntProperty(Property<Integer> property) {
        return getProperty(property).map(OptionalInt::of).orElse(OptionalInt.empty());
    }

    @Override
    public <V> Optional<V> getProperty(Inventory child, Property<V> property) {
        Optional<V> optValue = tryGetProperty(child, property);
        if (optValue.isPresent()) {
            return optValue;
        }
        optValue = tryGetProperty(property);
        if (optValue.isPresent()) {
            return optValue;
        }
        return LanternPropertyRegistry.INSTANCE.getStoreForInventory(property).getFor(this);
    }

    /**
     * Gets the inventory {@link Property} of this inventory. This method
     * will not be delegated through {@link PropertyProvider}s.
     *
     * @param property The property
     * @param <V> The property value type
     * @return The property value
     */
    public final <V> Optional<V> getInventoryProperty(Property<V> property) {
        Optional<V> optValue = tryGetProperty(property);
        if (optValue.isPresent()) {
            return optValue;
        }
        final AbstractInventory parent = parent();
        if (parent != this) {
            optValue = parent.tryGetProperty(this, property);
            if (optValue.isPresent()) {
                return optValue;
            }
        }
        return Optional.empty();
    }

    /**
     * Attempts to get a {@link Property} value from this {@link Inventory}.
     *
     * @param property The property
     * @param <V> The property value type
     * @return The property value
     */
    protected <V> Optional<V> tryGetProperty(Property<V> property) {
        if (property == InventoryProperties.TITLE) {
            return Optional.of((V) TextTranslation.toText(getName()));
        } else if (property == InventoryProperties.CAPACITY) {
            return Optional.of((V) (Integer) capacity());
        }
        return Optional.empty();
    }

    /**
     * Attempts to get a {@link Property} value for the specified child
     * {@link Inventory} based on this {@link Inventory}.
     *
     * @param child The child inventory
     * @param property The property
     * @param <V> The property value type
     * @return The property value
     */
    protected <V> Optional<V> tryGetProperty(Inventory child, Property<V> property) {
        return Optional.empty();
    }
}
