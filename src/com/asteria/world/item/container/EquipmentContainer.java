package com.asteria.world.item.container;

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
import com.asteria.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage a player's equipped items.
 * 
 * @author lare96
 * @author Vix
 */
public class EquipmentContainer {

    /** The player's equipment being managed. */
    private Player player;

    /** The container that will hold this player's equipped items. */
    private ItemContainer container = new ItemContainer(
            ContainerPolicy.NORMAL_POLICY, 14);

    /**
     * Create a new {@link EquipmentContainer}.
     * 
     * @param player
     *            the player's equipment being managed.
     */
    public EquipmentContainer(Player player) {
        this.player = player;
    }

    /**
     * Refreshes all of the items displayed on the equipment interface.
     */
    public void refresh() {
        Item[] items = container.toArray();
        player.getPacketBuilder().sendUpdateItems(1688, items);
    }

    /**
     * Adds an item into the container from the specified slot in the player's
     * inventory.
     * 
     * @param slot
     *            the item on this slot to add into the container.
     */
    public void equipItem(int slot) {

        Item item = player.getInventory().getContainer().getItem(slot);

        if (item == null) {
            return;
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canEquip(player, item, item.getDefinition()
                        .getEquipmentSlot())) {
                    return;
                }
            }
        }

        if (!AssignSkillRequirement.checkRequirement(player, item)) {
            return;
        }

        if (item.getDefinition().isStackable()) {
            int designatedSlot = item.getDefinition().getEquipmentSlot();
            Item equipItem = container.getItem(designatedSlot);

            if (container.isSlotUsed(designatedSlot)) {

                if (item.getId() == equipItem.getId()) {

                    container.set(
                            designatedSlot,
                            new Item(item.getId(), item.getAmount() + equipItem
                                    .getAmount()));
                } else {

                    player.getInventory().overrideItemSlot(equipItem, slot);
                    container.set(designatedSlot, item);
                }
            } else {

                container.set(designatedSlot, item);
            }

            player.getInventory().deleteItemSlot(item, slot);
        } else {
            int designatedSlot = item.getDefinition().getEquipmentSlot();

            if (designatedSlot == Utility.EQUIPMENT_SLOT_WEAPON && item
                    .getDefinition().isTwoHanded()) {
                removeItem(Utility.EQUIPMENT_SLOT_SHIELD);

                if (container.isSlotUsed(Utility.EQUIPMENT_SLOT_SHIELD)) {
                    return;
                }
            }

            if (designatedSlot == Utility.EQUIPMENT_SLOT_SHIELD && container
                    .isSlotUsed(Utility.EQUIPMENT_SLOT_WEAPON)) {
                if (container.getItem(Utility.EQUIPMENT_SLOT_WEAPON)
                        .getDefinition().isTwoHanded()) {
                    removeItem(Utility.EQUIPMENT_SLOT_WEAPON);

                    if (container.isSlotUsed(Utility.EQUIPMENT_SLOT_WEAPON)) {
                        return;
                    }
                }
            }

            if (container.isSlotUsed(designatedSlot)) {
                Item equipItem = container.getItem(designatedSlot);

                player.getInventory().overrideItemSlot(equipItem, slot);

            } else {
                player.getInventory().deleteItemSlot(item, slot);
            }

            container.set(designatedSlot,
                    new Item(item.getId(), item.getAmount()));
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

    }

    /**
     * Removes an item from a specified slot in the container.
     * 
     * @param slot
     *            the slot to remove the item from.
     */
    public void removeItem(int slot) {
        if (container.isSlotFree(slot)) {
            return;
        }

        Item item = container.getItem(slot);

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canUnequip(player, item, slot)) {
                    return;
                }
            }
        }

        if (!player.getInventory().getContainer().hasRoomFor(item)) {
            player.getPacketBuilder().sendMessage(
                    "You do not have enough space in your inventory!");
            return;
        }

        container.remove(item, slot);
        player.getInventory().addItem(new Item(item.getId(), item.getAmount()));

        if (slot == Utility.EQUIPMENT_SLOT_WEAPON) {
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
    }

    /**
     * Gets the container that will hold this player's equipped items.
     * 
     * @return the container that holds the items.
     */
    public ItemContainer getContainer() {
        return container;
    }
}