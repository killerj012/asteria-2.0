package com.asteria.world.entity;

import java.util.Iterator;
import java.util.Objects;

import com.asteria.world.WorldFullException;

/**
 * A container for holding and managing {@link Entity}s. This container uses a
 * fail-safe {@link Iterator} implementation which allows for insertion and
 * removal of elements during an iteration.
 * 
 * @author lare96
 * @param <T>
 *            the type of {@link Entity} to hold in this container.
 */
public class EntityContainer<T extends Entity> implements Iterable<T> {

    /** The size of this container. */
    private int size;

    /** The backing array for this container. */
    private T[] backingArray;

    /**
     * Create a new {@link EntityContainer} with the specified capacity.
     * 
     * @param capacity
     *            the maximum amount of entities this container is allowed to
     *            hold.
     */
    @SuppressWarnings("unchecked")
    public EntityContainer(int capacity) {
        this.backingArray = (T[]) new Entity[capacity];
        this.size = 0;
    }

    /**
     * Finds a slot for this entity and places it in that slot. If no slot is
     * found then a {@link WorldFullException} is thrown.
     * 
     * @param entity
     *            the entity to add to the found slot.
     * @return this container for chaining.
     */
    public EntityContainer<T> add(T entity) {

        // Locates a free slot for this entity.
        int foundSlot = getFreeSlot();

        // No more space, throw an exception.
        if (foundSlot == -1) {
            throw new WorldFullException(entity);
        }

        // Otherwise add the entity to the found slot.
        return addSlot(foundSlot, entity);
    }

    /**
     * Adds an entity to the specified slot.
     * 
     * @param slot
     *            the slot to add this entity to.
     * @param entity
     *            the entity to add to the slot.
     * @return this container for chaining.
     */
    private EntityContainer<T> addSlot(int slot, T entity) {

        // Check if the slot is in range.
        checkSlot(slot);

        // Add the entity and set its slot.
        backingArray[slot] = Objects.requireNonNull(entity);
        backingArray[slot].setSlot(slot);
        size++;
        return this;
    }

    /**
     * Remove this entity from the slot its currently in.
     * 
     * @param entity
     *            the entity to remove from its slot.
     * @return this container for chaining.
     */
    public EntityContainer<T> remove(T entity) {

        // Attempt to remove the entity from the container.
        return removeSlot(entity.getSlot());
    }

    /**
     * Remove the entity on the specified slot.
     * 
     * @param slot
     *            the slot to remove the entity on.
     * @return this container for chaining.
     */
    private EntityContainer<T> removeSlot(int slot) {

        // Check if the slot is in range.
        checkSlot(slot);

        // Check if the entity is even online.
        if (isSlotFree(slot)) {
            return this;
        }

        // Otherwise remove the entity from the container and flag them as
        // unregistered.
        backingArray[slot].setUnregistered(true);
        backingArray[slot] = null;
        size--;
        return this;
    }

    /**
     * Determines if this container has the specified entity.
     * 
     * @param entity
     *            the entity to check this container for.
     * @return true if this container has the entity.
     */
    public boolean contains(T entity) {

        // Check if the slot is in range.
        checkSlot(entity.getSlot());

        // Determine if this container contains the entity.
        return backingArray[entity.getSlot()] != null;
    }

    /**
     * Determines if the argued slot is in range or not. An
     * {@link IllegalArgumentException} is thrown if the slot is out of range.
     * 
     * @param slot
     *            the argued slot.
     * @return true if the slot is in range.
     */
    private void checkSlot(int slot) {

        // Throw an exception if the slot is out of the range.
        if (slot > backingArray.length || slot < 1) {
            throw new IllegalArgumentException("Slot out of range: " + slot);
        }
    }

    /**
     * Determines if the argued slot is free.
     * 
     * @param slot
     *            the slot to determine if free.
     * @return true if the slot is free.
     */
    public boolean isSlotFree(int slot) {
        return backingArray[slot] == null;
    }

    /**
     * Gets the entity on the specified slot.
     * 
     * @param slot
     *            the slot to retrieve the entity on.
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
     * Gets the amount of free slots left in this container.
     * 
     * @return the amount of free slots left in this container.
     */
    public int getRemainingSize() {
        return backingArray.length - size;
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
                    throw new ArrayIndexOutOfBoundsException(
                            "Can only call 'next()' in amount to 'backingArray.length'.");
                }

                int i = currentIndex;
                currentIndex++;
                return backingArray[lastElementIndex = i];
            }

            @Override
            public void remove() {
                if (lastElementIndex < 0) {
                    throw new IllegalStateException(
                            "Can only call 'remove()' once in call to 'next()'.");
                }

                removeSlot(lastElementIndex);
                currentIndex = lastElementIndex;
                lastElementIndex = -1;
            }
        };
    }
}
