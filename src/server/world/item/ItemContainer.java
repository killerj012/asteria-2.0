package server.world.item;

/**
 * A container for storing and managing items.
 * 
 * @author lare96
 * @author Graham
 */
public class ItemContainer {

    /**
     * A selection of policies that can be applied to this container.
     * 
     * @author lare96
     * @author Graham
     */
    public enum ContainerPolicy {

        /** Stacks the items that are stackable. */
        NORMAL_POLICY,

        /** Stacks all of the items. */
        STACKABLE_POLICY,

        /** Doesn't stack any of the items. */
        STANDALONE_POLICY
    }

    /** The maximum amount of items that can be put into this container. */
    private int capacity;

    /** The backing array of items in this container. */
    private Item[] items;

    /** The policy of this container */
    private ContainerPolicy policy;

    /**
     * Create a new {@link ItemContainer}.
     * 
     * @param policy
     *            the policy of this container.
     * @param capacity
     *            the initial capacity of this container.
     */
    public ItemContainer(ContainerPolicy policy, int capacity) {
        this.policy = policy;
        this.capacity = capacity;
        this.items = new Item[capacity];
    }

    /**
     * Sets the index in the backing array to the specified item.
     * 
     * @param index
     *            the index in the backing array being changed.
     * @param item
     *            the item being set to the index.
     */
    public void set(int index, Item item) {
        items[index] = item;
    }

    /**
     * Sets this containers items to another set of items.
     * 
     * @param items
     *            the new set of items.
     */
    public void setItems(Item[] items) {
        clear();
        for (int i = 0; i < items.length; i++) {
            this.items[i] = items[i];
        }
    }

    /**
     * Creates a new backing array with the previously specified capacity.
     */
    public void clear() {
        items = new Item[capacity];
    }

    /**
     * Adds an item to this container.
     * 
     * @param item
     *            the item to add.
     * @return true if the item was added.
     */
    public boolean add(Item item) {
        return add(item, -1);
    }

    /**
     * Removes an item from this container and keeps it if the amount falls at
     * or below 0.
     * 
     * @param item
     *            the item to remove and keep at 0.
     * @return the amount removed from this item.
     */
    public int removeOrZero(Item item) {
        return remove(item, -1, true);
    }

    /**
     * Gets the maximum amount of items that can be put into this container.
     * 
     * @return the capacity of this container.
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Gets the id of an item by its slot.
     * 
     * @param slot
     *            the slot to get.
     * @return the item id in that slot.
     */
    public int getIdBySlot(int slot) {
        return items[slot].getId();
    }

    /**
     * Checks if a slot is free.
     * 
     * @param slot
     *            the slot to check.
     * @return true if the slot is free.
     */
    public boolean isSlotFree(int slot) {
        return items[slot] == null;
    }

    /**
     * Checks if a slot is used.
     * 
     * @param slot
     *            the slot to check.
     * @return true if the slot is used.
     */
    public boolean isSlotUsed(int slot) {
        return items[slot] != null;
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @return the amount removed.
     */
    public int remove(Item item) {
        return remove(item, -1, false);
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @param preferredSlot
     *            the preferred slot to remove the item from.
     * @return the amount removed.
     */
    public int remove(Item item, int preferredSlot) {
        return remove(item, preferredSlot, false);
    }

    /**
     * Checks if this container has a certain item.
     * 
     * @param id
     *            the item to check in this container for.
     * @return true if this container has the item.
     */
    public boolean contains(int id) {
        return getSlotById(id) != -1;
    }

    /**
     * Checks if this container has a certain item.
     * 
     * @param item
     *            the item to check in this container for.
     * @return true if this container has the item.
     */
    public boolean contains(Item item) {
        for (Item i : items) {
            if (i == null) {
                continue;
            }

            if (item.getId() == i.getId() && i.getAmount() >= item.getAmount()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if this container has a set of certain items.
     * 
     * @param item
     *            the item to check in this container for.
     * @return true if this container has the item.
     */
    public boolean contains(Item[] item) {
        if (item.length == 0) {
            return false;
        }

        for (Item nextItem : item) {
            if (nextItem == null) {
                continue;
            }

            if (!contains(nextItem)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if this container has a set of certain item id's.
     * 
     * @param item
     *            the item id's to check in this container for.
     * @return true if this container has the item id.
     */
    public boolean contains(int[] id) {
        if (id.length == 0) {
            return false;
        }

        for (int nextItemId : id) {
            if (!contains(nextItemId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates a free slot.
     * 
     * @return the free slot, -1 if there are no free slots left.
     */
    public int freeSlot() {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the amount of free slots left.
     * 
     * @return the amount of slots left.
     */
    public int freeSlots() {
        return capacity - size();
    }

    /**
     * Gets an item by its index.
     * 
     * @param index
     *            the index.
     * @return the item on this index.
     */
    public Item getItem(int index) {
        if (index == -1 || index >= items.length)
            return null;
        return items[index];
    }

    /**
     * Gets an item id by its index.
     * 
     * @param index
     *            the index.
     * @return the item id on this index.
     */
    public int getItemId(int index) {
        if (index == -1 || items[index] == null)
            return -1;
        return items[index].getId();
    }

    /**
     * Gets an item id by its index.
     * 
     * @param index
     *            the index.
     * @return the item id on this index.
     */
    public Item getById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (items[i].getId() == id) {
                return items[i];
            }
        }
        return null;
    }

    /**
     * Gets the amount of times an item is in your inventory by its id.
     * 
     * @param id
     *            the id.
     * @return the amount of times this item is in your inventory.
     */
    public int getCount(int id) {
        int total = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                if (items[i].getId() == id) {
                    total += items[i].getAmount();
                }
            }
        }
        return total;
    }

    /**
     * Gets the slot of an item by its id.
     * 
     * @param id
     *            the id.
     * @return the slot of the item.
     */
    public int getSlotById(int id) {
        for (int i = 0; i < items.length; i++) {
            if (items[i] == null) {
                continue;
            }
            if (items[i].getId() == id) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Adds an item to this container.
     * 
     * @param item
     *            the item to add.
     * @param slot
     *            the slot to add it to.
     * @return true if the item was added.
     */
    public boolean add(Item item, int slot) {
        if (item == null) {
            return false;
        }
        int newSlot = (slot > -1) ? slot : freeSlot();
        if ((item.getDefinition().isStackable() || policy
                .equals(ContainerPolicy.STACKABLE_POLICY))
                && !policy.equals(ContainerPolicy.STANDALONE_POLICY)) {
            if (getCount(item.getId()) > 0) {
                newSlot = getSlotById(item.getId());
            }
        }
        if (newSlot == -1) {
            return false;
        }
        if (getItem(newSlot) != null) {
            newSlot = freeSlot();
        }
        if ((item.getDefinition().isStackable() || policy
                .equals(ContainerPolicy.STACKABLE_POLICY))
                && !policy.equals(ContainerPolicy.STANDALONE_POLICY)) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getId() == item.getId()) {
                    set(i, new Item(items[i].getId(), items[i].getAmount()
                            + item.getAmount()));
                    return true;
                }
            }
            if (newSlot == -1) {
                return false;
            }
            set(slot > -1 ? newSlot : freeSlot(), item);
            return true;
        }

        int slots = freeSlots();

        if (slots >= item.getAmount()) {
            for (int i = 0; i < item.getAmount(); i++) {
                set(slot > -1 ? newSlot : freeSlot(), new Item(item.getId(), 1));
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if this container has room for this item.
     * 
     * @param item
     *            the item to check if this has room for.
     * @return true if it has room for the item.
     */
    public boolean hasRoomFor(Item item) {
        if ((item.getDefinition().isStackable() || policy
                .equals(ContainerPolicy.STACKABLE_POLICY))
                && !policy.equals(ContainerPolicy.STANDALONE_POLICY)) {
            for (int i = 0; i < items.length; i++) {
                if (items[i] != null && items[i].getId() == item.getId()) {
                    int totalCount = item.getAmount() + items[i].getAmount();
                    if (totalCount >= Integer.MAX_VALUE || totalCount < 1) {
                        return false;
                    }
                    return true;
                }
            }
            int slot = freeSlot();
            return slot != -1;
        }

        int slots = freeSlots();
        return slots >= item.getAmount();
    }

    /**
     * Inserts an item into a new slot.
     * 
     * @param fromSlot
     *            the slot the item is coming from.
     * @param toSlot
     *            the new slot the item is going to.
     */
    public void insert(int fromSlot, int toSlot) {
        Item from = items[fromSlot];
        if (from == null) {
            return;
        }
        items[fromSlot] = null;
        if (fromSlot > toSlot) {
            int shiftFrom = toSlot;
            int shiftTo = fromSlot;
            for (int i = (toSlot + 1); i < fromSlot; i++) {
                if (items[i] == null) {
                    shiftTo = i;
                    break;
                }
            }
            Item[] slice = new Item[shiftTo - shiftFrom];
            System.arraycopy(items, shiftFrom, slice, 0, slice.length);
            System.arraycopy(slice, 0, items, shiftFrom + 1, slice.length);
        } else {
            int sliceStart = fromSlot + 1;
            int sliceEnd = toSlot;
            for (int i = (sliceEnd - 1); i >= sliceStart; i--) {
                if (items[i] == null) {
                    sliceStart = i;
                    break;
                }
            }
            Item[] slice = new Item[sliceEnd - sliceStart + 1];
            System.arraycopy(items, sliceStart, slice, 0, slice.length);
            System.arraycopy(slice, 0, items, sliceStart - 1, slice.length);
        }
        items[toSlot] = from;
    }

    /**
     * Removes an item from this container.
     * 
     * @param item
     *            the item to remove.
     * @param preferredSlot
     *            the slot to remove it from.
     * @param allowZero
     *            if the item amount can stay at 0 without being removed.
     * @return the amount removed.
     */
    public int remove(Item item, int preferredSlot, boolean allowZero) {
        if (item == null) {
            return -1;
        }
        int removed = 0;
        if ((item.getDefinition().isStackable() || policy
                .equals(ContainerPolicy.STACKABLE_POLICY))
                && !policy.equals(ContainerPolicy.STANDALONE_POLICY)) {
            int slot = getSlotById(item.getId());
            Item stack = getItem(slot);
            if (stack == null) {
                return -1;
            }
            if (stack.getAmount() > item.getAmount()) {
                removed = item.getAmount();
                set(slot,
                        new Item(stack.getId(), stack.getAmount()
                                - item.getAmount()));
            } else {
                removed = stack.getAmount();
                set(slot, allowZero ? new Item(stack.getId(), 0) : null);
            }
        } else {
            for (int i = 0; i < item.getAmount(); i++) {
                int slot = getSlotById(item.getId());
                if (i == 0 && preferredSlot != -1) {
                    Item inSlot = getItem(preferredSlot);
                    if (inSlot == null) {
                        return -1;
                    }
                    if (inSlot.getId() == item.getId()) {
                        slot = preferredSlot;
                    }
                }
                if (slot != -1) {
                    removed++;
                    set(slot, null);
                } else {
                    break;
                }
            }
        }
        return removed;
    }

    /**
     * Clears all empty spaces in this container by shifting all of the items to
     * remove <code>null</code> values.
     */
    public void compact() {
        Item[] previousItems = items;
        items = new Item[capacity];
        int newIndex = 0;
        for (int i = 0; i < items.length; i++) {
            if (previousItems[i] != null) {
                items[newIndex] = previousItems[i];
                newIndex++;
            }
        }
    }

    /**
     * Resizes this container with a new capacity.
     * 
     * @param capacity
     *            the new capacity to set this container to (must be greater
     *            than the current size).
     */
    public void resize(int capacity) {
        if (capacity < size()) {
            throw new IllegalArgumentException(
                    "Capacity must not be lower than the current size!");
        }

        compact();
        Item[] temporaryItems = items;
        this.capacity = capacity;
        clear();

        for (int i = 0; i < temporaryItems.length; i++) {
            items[i] = temporaryItems[i];
        }
    }

    /**
     * Gets the size of this container.
     * 
     * @return the amount of items in this container.
     */
    public int size() {
        int size = 0;
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                size++;
            }
        }
        return size;
    }

    /**
     * Switches the item in one spot with the item in another spot.
     * 
     * @param fromSlot
     *            the slot of the item being swapped.
     * @param toSlot
     *            the slot of the item being swapped with the other item.
     */
    public void swap(int fromSlot, int toSlot) {
        Item temp = getItem(fromSlot);
        set(fromSlot, getItem(toSlot));
        set(toSlot, temp);
    }

    /**
     * Transfers items to another container.
     * 
     * @param from
     *            from this container.
     * @param to
     *            to this container.
     * @param fromSlot
     *            from this slot.
     * @param id
     *            with this id.
     * @return true if it was transferred.
     */
    public static boolean transfer(ItemContainer from, ItemContainer to,
            int fromSlot, int id) {
        Item fromItem = from.getItem(fromSlot);
        if (fromItem == null || fromItem.getId() != id) {
            return false;
        }
        if (to.add(fromItem)) {
            from.set(fromSlot, null);
            return true;
        }

        return false;
    }

    /**
     * The backing array of items.
     * 
     * @return the array of items.
     */
    public Item[] toArray() {
        return items;
    }

    /**
     * The backing array of items excluding nulls.
     * 
     * @return the array of items.
     */
    public Item[] toCleanArray() {
        Item[] itemsCopy = new Item[size()];
        int slot = 0;

        for (Item item : itemsCopy) {
            if (item == null) {
                continue;
            }
            itemsCopy[slot++] = item;
        }
        return itemsCopy;
    }
}