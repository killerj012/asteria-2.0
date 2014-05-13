package server.world.entity.player.container;

import server.util.Misc;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignSkillRequirement;
import server.world.entity.player.content.AssignWeaponAnimation;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage a player's equipped items.
 * 
 * @author lare96
 * @author Vix
 */
public class EquipmentContainer {

    /**
     * The player that will have all of their equipped items managed by this
     * container.
     */
    private Player player;

    /** The container that will hold this player's equipped items. */
    private ItemContainer container = new ItemContainer(ContainerPolicy.NORMAL_POLICY, 14);

    /**
     * Create a new {@link EquipmentContainer}.
     * 
     * @param player
     *        the player that will have all of their equipped items managed by
     *        this container.
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
     *        the item on this slot to add into the container.
     */
    public void equipItem(int slot) {

        /** Get the item on this slot. */
        Item item = player.getInventory().getContainer().getItem(slot);

        /** Check if this item is even valid. */
        if (item == null) {
            return;
        }

        /** Check if we can equip the item based on the minigame we're in. */
        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canEquip(player, item, item.getDefinition().getEquipmentSlot())) {
                    return;
                }
            }
        }

        /** Check if we have the required level to equip this item. */
        if (!AssignSkillRequirement.checkRequirement(player, item)) {
            return;
        }

        /** Equip the item a certain way if it's stackable. */
        if (item.getDefinition().isStackable()) {

            /** Get the designated slot for this item. */
            int designatedSlot = item.getDefinition().getEquipmentSlot();

            /** Gets the item on this slot. */
            Item equipItem = container.getItem(designatedSlot);

            /** If there is already an item on this spot... */
            if (container.isSlotUsed(designatedSlot)) {

                /** And the two items are the same... */
                if (item.getId() == equipItem.getId()) {

                    /** Just add on to the amount. */
                    container.set(designatedSlot, new Item(item.getId(), item.getAmount() + equipItem.getAmount()));
                } else {

                    /** Otherwise replace the item. */
                    player.getInventory().overrideItemSlot(equipItem, slot);
                    container.set(designatedSlot, item);
                }
            } else {

                /** If there's no item on this slot, add it normally. */
                container.set(designatedSlot, item);
            }

            /** Delete the item from your inventory. */
            player.getInventory().deleteItemSlot(item, slot);

            /** Otherwise equip the item the normal way (if it's not stackable). */
        } else {

            /** Get the designated slot for this item. */
            int designatedSlot = item.getDefinition().getEquipmentSlot();

            /**
             * If the item is going into the weapon slot and is two handed we
             * need to remove any other armor currently in the shield and weapon
             * spot.
             */
            if (designatedSlot == Misc.EQUIPMENT_SLOT_WEAPON && item.getDefinition().isTwoHanded()) {
                removeItem(Misc.EQUIPMENT_SLOT_SHIELD);

                if (container.isSlotUsed(Misc.EQUIPMENT_SLOT_SHIELD)) {
                    return;
                }
            }

            /**
             * If the item is going into the shield slot and we currently have a
             * two-handed item equipped we need to remove it.
             */
            if (designatedSlot == Misc.EQUIPMENT_SLOT_SHIELD && container.isSlotUsed(Misc.EQUIPMENT_SLOT_WEAPON)) {
                if (container.getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().isTwoHanded()) {
                    removeItem(Misc.EQUIPMENT_SLOT_WEAPON);

                    if (container.isSlotUsed(Misc.EQUIPMENT_SLOT_WEAPON)) {
                        return;
                    }
                }
            }

            /**
             * If there is an item in the designated slot add it back into the
             * inventory in place of the item you are equipping.
             */
            if (container.isSlotUsed(designatedSlot)) {
                Item equipItem = container.getItem(designatedSlot);

                player.getInventory().overrideItemSlot(equipItem, slot);

                /**
                 * Otherwise just remove the item you are equipping from your
                 * inventory.
                 */
            } else {
                player.getInventory().deleteItemSlot(item, slot);
            }

            /** And set the newly equipped item. */
            container.set(designatedSlot, new Item(item.getId(), item.getAmount()));
        }

        if (item.getDefinition().getEquipmentSlot() == Misc.EQUIPMENT_SLOT_WEAPON) {

            /** Assign the new sidebar interface based on the weapon. */
            AssignWeaponInterface.assignInterface(player, item);

            /** Assign the new animation based on the weapon. */
            AssignWeaponAnimation.assignAnimation(player, item);

            /** Assign a new fight type based on the weapon. */
            AssignWeaponInterface.changeFightType(player);
        }

        /** Write the item bonus. */
        player.writeBonus();

        /** Refresh everything. */
        refresh();
        player.getFlags().flag(Flag.APPEARANCE);
        player.setAutocast(false);
        player.getPacketBuilder().sendConfig(108, 0);
    }

    /**
     * Removes an item from a specified slot in the container.
     * 
     * @param slot
     *        the slot to remove the item from.
     */
    public void removeItem(int slot) {

        /** Check if an item even exists on this slot. */
        if (container.isSlotFree(slot)) {
            return;
        }

        /** Get the item on this slot. */
        Item item = container.getItem(slot);

        /** Check if we can remove the item based on the minigame we're in. */
        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canUnequip(player, item, slot)) {
                    return;
                }
            }
        }

        /** Check if we have enough space to remove it. */
        if (!player.getInventory().getContainer().hasRoomFor(item)) {
            player.getPacketBuilder().sendMessage("You do not have enough space in your inventory!");
            return;
        }

        /** Remove the item and add it to the inventory. */
        container.remove(item, slot);
        player.getInventory().addItem(new Item(item.getId(), item.getAmount()));

        /** Reset the sidebar interface and appearance animation. */
        if (slot == Misc.EQUIPMENT_SLOT_WEAPON) {
            AssignWeaponInterface.reset(player);
            AssignWeaponInterface.changeFightType(player);
            player.getUpdateAnimation().reset();
        }

        /** Write the bonus. */
        player.writeBonus();

        /** Refresh everything. */
        refresh();
        player.getInventory().refresh(3214);
        player.getFlags().flag(Flag.APPEARANCE);
        player.setAutocast(false);
        player.getPacketBuilder().sendConfig(108, 0);
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