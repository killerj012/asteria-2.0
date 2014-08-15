package com.asteria.world.item.container;

import java.util.Collection;

import com.asteria.util.Utility;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.AssignSkillRequirement;
import com.asteria.world.entity.player.content.AssignWeaponAnimation;
import com.asteria.world.entity.player.content.AssignWeaponInterface;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;

/**
 * An {@link ItemContainer} implementation that will manage a player's equipped
 * items.
 * 
 * @author lare96
 */
public class EquipmentContainer extends ItemContainer {

    /** The player's equipment being managed. */
    private Player player;

    /**
     * Create a new {@link EquipmentContainer}.
     * 
     * @param player
     *            the player's equipment being managed.
     */
    public EquipmentContainer(Player player) {
        super(Policy.NORMAL, 14);
        this.player = player;
    }

    /** Refreshes the items displayed on the equipment interface. */
    public void refresh() {
        refresh(1688, player);
    }

    /**
     * Equips the item in the argued slot from the player's inventory.
     * 
     * @param inventorySlot
     *            the item on this slot to add into the container.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean equipItem(int inventorySlot) {
        Item item = player.getInventory().get(inventorySlot);

        if (item == null) {
            return false;
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canEquip(player, item, item.getDefinition()
                        .getEquipmentSlot())) {
                    return false;
                }
            }
        }

        if (!AssignSkillRequirement.checkRequirement(player, item)) {
            return false;
        }

        if (item.getDefinition().isStackable()) {
            int designatedSlot = item.getDefinition().getEquipmentSlot();
            Item equipItem = get(designatedSlot);

            if (isSlotUsed(designatedSlot)) {

                if (item.getId() == equipItem.getId()) {

                    set(designatedSlot, new Item(item.getId(),
                            item.getAmount() + equipItem.getAmount()));
                } else {

                    player.getInventory().set(inventorySlot, equipItem);
                    player.getInventory().refresh();
                    set(designatedSlot, item);
                }
            } else {

                set(designatedSlot, item);
            }

            player.getInventory().remove(item, inventorySlot);
        } else {
            int designatedSlot = item.getDefinition().getEquipmentSlot();

            if (designatedSlot == Utility.EQUIPMENT_SLOT_WEAPON && item
                    .getDefinition().isTwoHanded()) {
                unequipItem(Utility.EQUIPMENT_SLOT_SHIELD, true);

                if (isSlotUsed(Utility.EQUIPMENT_SLOT_SHIELD)) {
                    return false;
                }
            }

            if (designatedSlot == Utility.EQUIPMENT_SLOT_SHIELD && isSlotUsed(Utility.EQUIPMENT_SLOT_WEAPON)) {
                if (get(Utility.EQUIPMENT_SLOT_WEAPON).getDefinition()
                        .isTwoHanded()) {
                    unequipItem(Utility.EQUIPMENT_SLOT_WEAPON, true);

                    if (isSlotUsed(Utility.EQUIPMENT_SLOT_WEAPON)) {
                        return false;
                    }
                }
            }

            if (isSlotUsed(designatedSlot)) {
                Item equipItem = get(designatedSlot);

                player.getInventory().set(inventorySlot, equipItem);
                player.getInventory().refresh();

            } else {
                player.getInventory().set(inventorySlot, item);
                player.getInventory().refresh();
            }

            set(designatedSlot, new Item(item.getId(), item.getAmount()));
        }

        if (item.getDefinition().getEquipmentSlot() == Utility.EQUIPMENT_SLOT_WEAPON) {
            AssignWeaponInterface.assignInterface(player, item);
            AssignWeaponAnimation.assignAnimation(player, item);
            AssignWeaponInterface.changeFightType(player);
            player.setCastSpell(null);
            player.setAutocastSpell(null);
            player.setAutocast(false);
            player.getPacketBuilder().sendConfig(108, 0);
            player.getUpdateAnimation().reset();
        }

        player.writeBonus();
        refresh();
        player.getFlags().flag(Flag.APPEARANCE);
        return true;
    }

    /**
     * Unequips the item in the argued slot from this container.
     * 
     * @param equipmentSlot
     *            the slot to unequip the item on.
     * @param addItem
     *            if the item should be added back to the inventory after being
     *            unequipped.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean unequipItem(int equipmentSlot, boolean addItem) {

        if (isSlotFree(equipmentSlot)) {
            return false;
        }

        Item item = get(equipmentSlot);

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canUnequip(player, item, equipmentSlot)) {
                    return false;
                }
            }
        }

        if (!player.getInventory().spaceFor(item)) {
            player.getPacketBuilder().sendMessage(
                    "You do not have enough space in your inventory!");
            return false;
        }

        remove(item, equipmentSlot);

        if (addItem)
            player.getInventory().add(new Item(item.getId(), item.getAmount()));

        if (equipmentSlot == Utility.EQUIPMENT_SLOT_WEAPON) {
            AssignWeaponInterface.assignInterface(player, null);
            AssignWeaponInterface.changeFightType(player);
            player.setCastSpell(null);
            player.setAutocastSpell(null);
            player.setAutocast(false);
            player.getPacketBuilder().sendConfig(108, 0);
            player.getUpdateAnimation().reset();
        }

        player.writeBonus();
        refresh();
        player.getInventory().refresh();
        player.getFlags().flag(Flag.APPEARANCE);
        return true;
    }

    /**
     * Unequips the argued item from this container.
     * 
     * @param item
     *            the item to remove from this container.
     * @param addItem
     *            if the item should be added back to the inventory after being
     *            unequipped.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean unequipItem(Item item, boolean addItem) {
        return unequipItem(getSlot(item.getId()), addItem);
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean add(Item item, int slot) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean add(Item item) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean addAll(Collection<? extends Item> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean remove(Item item, int slot) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean remove(Item item) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }
}