package server.world.item;

import server.util.Misc;

/**
 * A single entry in a table of important data used for {@link Item}s.
 * 
 * @author lare96
 */
public class ItemDefinition {

    /** The item definitions. */
    private static ItemDefinition[] definitions;

    /** The item id. */
    private int itemId;

    /** The item name. */
    private String itemName;

    /** The item description. */
    private String itemDescription;

    /** The equipment slot. */
    private int equipmentSlot;

    /** If this item is noted. */
    private boolean isNoted;

    /** If this item is noteable. */
    private boolean isNoteable;

    /** If this item is stackable. */
    private boolean isStackable;

    /** The unnoted id of this item. */
    private int unNotedId;

    /** The noted id of this item. */
    private int notedId;

    /** If this item is members only. */
    private boolean membersItem;

    /** The special store price. */
    private int specialStorePrice;

    /** The general store price. */
    private int generalStorePrice;

    /** The low alch value price. */
    private int lowAlchValue;

    /** The high alch value price. */
    private int highAlchValue;

    /** The weight of the item. */
    private double weight;

    /** The item bonuses. */
    private int[] bonus = new int[12];

    /**
     * Gets the item id
     * 
     * @return the item id.
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Sets the item id
     * 
     * @param itemId
     *            the itemId to set.
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets the item name
     * 
     * @return the item name.
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * Sets the item name
     * 
     * @param itemName
     *            the itemName to set.
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Gets the item description
     * 
     * @return the item description.
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * Sets the item description
     * 
     * @param itemDescription
     *            the itemDescription to set.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * Gets the equipment slot
     * 
     * @return the equipment slot.
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * Sets the equipment slot
     * 
     * @param equipmentSlot
     *            the equipmentSlot to set.
     */
    public void setEquipmentSlot(int equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    /**
     * Gets if this item is noted.
     * 
     * @return true if this item is noted.
     */
    public boolean isNoted() {
        return isNoted;
    }

    /**
     * Sets if this item is noted.
     * 
     * @param isNoted
     *            the isNoted to set.
     */
    public void setNoted(boolean isNoted) {
        this.isNoted = isNoted;
    }

    /**
     * Gets if this item is noteable.
     * 
     * @return true if this item is able to be noted.
     */
    public boolean isNoteable() {
        return isNoteable;
    }

    /**
     * Sets if this item is noteable.
     * 
     * @param isNoteable
     *            the isNoteable to set.
     */
    public void setNoteable(boolean isNoteable) {
        this.isNoteable = isNoteable;
    }

    /**
     * Gets if this item is stackable.
     * 
     * @return true if this item is stackable.
     */
    public boolean isStackable() {
        return isStackable;
    }

    /**
     * Sets if this item is stackable
     * 
     * @param isStackable
     *            the isStackable to set.
     */
    public void setStackable(boolean isStackable) {
        this.isStackable = isStackable;
    }

    /**
     * Gets the un-noted id.
     * 
     * @return the un-noted id.
     */
    public int getUnNotedId() {
        return unNotedId;
    }

    /**
     * Sets the un-noted id.
     * 
     * @param unNotedId
     *            the unNotedId to set.
     */
    public void setUnNotedId(int unNotedId) {
        this.unNotedId = unNotedId;
    }

    /**
     * Gets the noted id.
     * 
     * @return the noted id.
     */
    public int getNotedId() {
        return notedId;
    }

    /**
     * Sets the noted id.
     * 
     * @param notedId
     *            the notedId to set.
     */
    public void setNotedId(int notedId) {
        this.notedId = notedId;
    }

    /**
     * Gets if this is a members item.
     * 
     * @return true if this item is a members item.
     */
    public boolean isMembersItem() {
        return membersItem;
    }

    /**
     * Sets if this is a members item.
     * 
     * @param membersItem
     *            the membersItem to set.
     */
    public void setMembersItem(boolean membersItem) {
        this.membersItem = membersItem;
    }

    /**
     * Gets the special store price.
     * 
     * @return the special store price.
     */
    public int getSpecialStorePrice() {
        return specialStorePrice;
    }

    /**
     * Sets the special store price.
     * 
     * @param specialStorePrice
     *            the specialStorePrice to set.
     */
    public void setSpecialStorePrice(int specialStorePrice) {
        this.specialStorePrice = specialStorePrice;
    }

    /**
     * Gets the general store price.
     * 
     * @return the general store price.
     */
    public int getGeneralStorePrice() {
        return generalStorePrice;
    }

    /**
     * Sets the general store price.
     * 
     * @param generalStorePrice
     *            the generalStorePrice to set.
     */
    public void setGeneralStorePrice(int generalStorePrice) {
        this.generalStorePrice = generalStorePrice;
    }

    /**
     * Gets the low alch value.
     * 
     * @return the low alch value.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
    }

    /**
     * Sets the low alch value.
     * 
     * @param lowAlchValue
     *            the lowAlchValue to set.
     */
    public void setLowAlchValue(int lowAlchValue) {
        this.lowAlchValue = lowAlchValue;
    }

    /**
     * Gets the high alch value.
     * 
     * @return the high alch value.
     */
    public int getHighAlchValue() {
        return highAlchValue;
    }

    /**
     * Sets the high alch value.
     * 
     * @param highAlchValue
     *            the highAlchValue to set.
     */
    public void setHighAlchValue(int highAlchValue) {
        this.highAlchValue = highAlchValue;
    }

    /**
     * Gets the weight.
     * 
     * @return the weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the weight.
     * 
     * @param weight
     *            the weight to set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Gets the bonus.
     * 
     * @return the bonus.
     */
    public int[] getBonus() {
        return bonus;
    }

    /**
     * Sets the bonus.
     * 
     * @param bonus
     *            the bonus to set.
     */
    public void setBonus(int[] bonus) {
        this.bonus = bonus;
    }

    /**
     * Gets the definitions
     * 
     * @return the definitions.
     */
    public static ItemDefinition[] getDefinitions() {
        return definitions;
    }

    /**
     * Sets the definitions
     * 
     * @param definitions
     *            the definitions to set.
     */
    public static void setDefinitions(ItemDefinition[] definitions) {
        ItemDefinition.definitions = definitions;
    }

    /**
     * Gets if this item is two handed.
     * 
     * @return true if this item is two handed.
     */
    public boolean isTwoHanded() {
        return Misc.getIs2H().contains(itemId);
    }

    /**
     * Gets if this item is a full helm.
     * 
     * @return true if this item is a full helm.
     */
    public boolean isFullHelm() {
        return Misc.getIsFullHelm().contains(itemId);
    }

    /**
     * Gets if this item is a platebody.
     * 
     * @return true if this item is a platebody.
     */
    public boolean isPlatebody() {
        return Misc.getIsPlatebody().contains(itemId);
    }
}