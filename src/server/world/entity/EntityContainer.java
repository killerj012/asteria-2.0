package server.world.entity;

import java.util.Collection;
import java.util.Iterator;

import server.util.Misc.GenericAction;
import server.world.WorldFullException;

/**
 * A container for holding and managing entities.
 * 
 * @author lare96
 * @param <T>
 *        the type of entity to hold in this container.
 */
public class EntityContainer<T extends Entity> implements Iterable<T> {

    /** The backing array for this container. */
    private T[] backingArray;

    /**
     * Create a new {@link EntityContainer} with the specified capacity.
     * 
     * @param capacity
     *        the maximum amount of entities this container is allowed to hold.
     */
    @SuppressWarnings("unchecked")
    public EntityContainer(int capacity) {
        this.backingArray = (T[]) new Entity[capacity];
    }

    /**
     * Create a new {@link EntityContainer} with a collection of entities
     * already added. The capacity of the container depends on the size of the
     * given collection.
     * 
     * @param collection
     *        the collection with the entities to add to this container.
     */
    public EntityContainer(Collection<T> collection) {
        this(collection.size());

        /** So we don't waste time looping to find a slot. */
        int addSlot = 0;

        for (T element : collection) {
            this.addSlot(addSlot, element);
            addSlot++;
        }
    }

    /**
     * Finds a slot for this entity and places it in that slot. If no slot is
     * found then a {@link WorldFullException} is thrown.
     * 
     * @param entity
     *        the entity to add to the found slot.
     * @return this container for chaining.
     */
    public void add(T entity) {

        /** Get a slot for this entity. */
        int foundSlot = getFreeSlot();

        /** If no slot was found throw an exception. */
        if (foundSlot == -1) {
            throw new WorldFullException(entity);
        }

        /** Otherwise add the entity to the found slot. */
        addSlot(foundSlot, entity);
    }

    /**
     * Adds an entity to the specified slot.
     * 
     * @param slot
     *        the slot to add this entity to.
     * @param entity
     *        the entity to add to the slot.
     * @return this container for chaining.
     */
    public void addSlot(int slot, T entity) {

        /** Throw an exception if this is a malformed entity. */
        if (entity == null) {
            throw new IllegalArgumentException("Cannot add a malformed entity to this container!");
        }

        /** Throw an exception if the slot is out of the range. */
        if (slot > backingArray.length || slot < 1) {
            throw new IllegalArgumentException("Invalid entry slot requested!");
        }

        /** Add the entity and set utility values. */
        backingArray[slot] = entity;
        backingArray[slot].setSlot(slot);
    }

    /**
     * Remove this entity from the slot its currently in.
     * 
     * @param entity
     *        the entity to remove from its slot.
     * @return this container for chaining.
     */
    public void remove(T entity) {

        /** Throw an exception if this is a malformed entity. */
        if (entity == null) {
            throw new IllegalArgumentException("Cannot add a malformed entity to this container!");
        }

        /** Otherwise remove the entity. */
        removeSlot(entity.getSlot());
    }

    /**
     * Remove the entity on the specified slot.
     * 
     * @param slot
     *        the slot to remove the entity on.
     * @return this container for chaining.
     */
    public void removeSlot(int slot) {

        /** Throw an exception if the slot is out of the range. */
        if (slot > backingArray.length || slot < 1) {
            throw new IllegalArgumentException("Invalid remove slot requested!");
        }

        /** Check if the entity is even online. */
        if (isSlotFree(slot)) {
            return;
        }

        /** Otherwise remove the entity from the slot and set utility values. */
        backingArray[slot].setUnregistered(true);
        backingArray[slot] = null;
    }

    /**
     * Performs an action on every single valid entity in this container.
     * 
     * @param task
     *        the action to perform.
     */
    public void loopTask(GenericAction<T> task) {
        for (T entity : backingArray) {
            if (entity == null) {
                continue;
            }

            task.fireAction(entity);
        }
    }

    /**
     * Determines if this container has the specified entity.
     * 
     * @param entity
     *        the entity to check this container for.
     * @return true if this container has the entity.
     */
    public boolean contains(T entity) {
        if (entity.getSlot() > backingArray.length || entity.getSlot() < 1) {
            return false;
        }

        return backingArray[entity.getSlot()] != null;
    }

    /**
     * Determines if the given slot is free.
     * 
     * @param slot
     *        the slot to determine if free.
     * @return true if the slot is free.
     */
    public boolean isSlotFree(int slot) {
        return backingArray[slot] == null;
    }

    /**
     * Gets the entity on the specified slot.
     * 
     * @param slot
     *        the slot to retrieve the entity on.
     * @return the entity on the specified slot.
     */
    public T get(int slot) {
        return backingArray[slot];
    }

    /**
     * Gets the maximum amount of entities this container can hold.
     * 
     * @return the capacity of this container.
     */
    public int getCapacity() {
        return backingArray.length;
    }

    /**
     * Gets the amount of non-malformed entities in this container.
     * 
     * @return the amount of non-malformed entities.
     */
    public int getSize() {
        int size = 0;

        for (T element : backingArray) {
            if (element == null) {
                continue;
            }

            size++;
        }
        return size;
    }

    /**
     * Gets a free slot in this container.
     * 
     * @return the free slot or -1 if there are none left.
     */
    public int getFreeSlot() {
        for (int slot = 1; slot < backingArray.length; slot++) {
            if (backingArray[slot] == null) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Gets the total amount of free slots in this container.
     * 
     * @return the amount of free slots left in this container.
     */
    public int getFreeSlotAmount() {
        return backingArray.length - getSize();
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            /** The current index we are iterating on. */
            private int currentIndex;

            /** The last index we iterated over. */
            private int lastElementIndex = -1;

            @Override
            public boolean hasNext() {
                return !(currentIndex + 1 > backingArray.length);
            }

            @Override
            public T next() {
                if (currentIndex >= backingArray.length) {
                    throw new ArrayIndexOutOfBoundsException("Can only call 'next()' in amount to 'backingArray.length'.");
                }

                int i = currentIndex;
                currentIndex++;
                return backingArray[lastElementIndex = i];
            }

            @Override
            public void remove() {
                if (lastElementIndex < 0) {
                    throw new IllegalStateException("Can only call 'remove()' once in call to 'next()'.");
                }

                removeSlot(lastElementIndex);
                currentIndex = lastElementIndex;
                lastElementIndex = -1;
            }
        };
    }
}
