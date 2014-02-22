package server.world.item;

import server.util.Misc;

/**
 * Represents a single item definition that was parsed.
 * 
 * @author lare96
 */
public class ItemDefinition {

    /**
     * The item definitions.
     */
    private static ItemDefinition[] definitions;

    /**
     * The item id.
     */
    private int itemId;

    /**
     * The item name.
     */
    private String itemName;

    /**
     * The item description.
     */
    private String itemDescription;

    /**
     * The equipment slot.
     */
    private int equipmentSlot;

    /**
     * If this item is noted.
     */
    private boolean isNoted;

    /**
     * If this item is noteable.
     */
    private boolean isNoteable;

    /**
     * If this item is stackable.
     */
    private boolean isStackable;

    /**
     * The unnoted id of this item.
     */
    private int unNotedId;

    /**
     * The noted id of this item.
     */
    private int notedId;

    /**
     * If this item is members only.
     */
    private boolean membersItem;

    /**
     * The special store price.
     */
    private int specialStorePrice;

    /**
     * The general store price.
     */
    private int generalStorePrice;

    /**
     * The low alch value price.
     */
    private int lowAlchValue;

    /**
     * The high alch value price.
     */
    private int highAlchValue;

    /**
     * The weight of the item.
     */
    private double weight;

    /**
     * The item bonuses.
     */
    private int[] bonus = new int[12];

    /**
     * @return the itemId.
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * @param itemId
     *        the itemId to set.
     */
    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the itemName.
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName
     *        the itemName to set.
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the itemDescription.
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * @param itemDescription
     *        the itemDescription to set.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * @return the equipmentSlot.
     */
    public int getEquipmentSlot() {
        return equipmentSlot;
    }

    /**
     * @param equipmentSlot
     *        the equipmentSlot to set.
     */
    public void setEquipmentSlot(int equipmentSlot) {
        this.equipmentSlot = equipmentSlot;
    }

    /**
     * @return the isNoted.
     */
    public boolean isNoted() {
        return isNoted;
    }

    /**
     * @param isNoted
     *        the isNoted to set.
     */
    public void setNoted(boolean isNoted) {
        this.isNoted = isNoted;
    }

    /**
     * @return the isNoteable.
     */
    public boolean isNoteable() {
        return isNoteable;
    }

    /**
     * @param isNoteable
     *        the isNoteable to set.
     */
    public void setNoteable(boolean isNoteable) {
        this.isNoteable = isNoteable;
    }

    /**
     * @return the isStackable.
     */
    public boolean isStackable() {
        return isStackable;
    }

    /**
     * @param isStackable
     *        the isStackable to set.
     */
    public void setStackable(boolean isStackable) {
        this.isStackable = isStackable;
    }

    /**
     * @return the unNotedId.
     */
    public int getUnNotedId() {
        return unNotedId;
    }

    /**
     * @param unNotedId
     *        the unNotedId to set.
     */
    public void setUnNotedId(int unNotedId) {
        this.unNotedId = unNotedId;
    }

    /**
     * @return the notedId.
     */
    public int getNotedId() {
        return notedId;
    }

    /**
     * @param notedId
     *        the notedId to set.
     */
    public void setNotedId(int notedId) {
        this.notedId = notedId;
    }

    /**
     * @return the membersItem.
     */
    public boolean isMembersItem() {
        return membersItem;
    }

    /**
     * @param membersItem
     *        the membersItem to set.
     */
    public void setMembersItem(boolean membersItem) {
        this.membersItem = membersItem;
    }

    /**
     * @return the specialStorePrice.
     */
    public int getSpecialStorePrice() {
        return specialStorePrice;
    }

    /**
     * @param specialStorePrice
     *        the specialStorePrice to set.
     */
    public void setSpecialStorePrice(int specialStorePrice) {
        this.specialStorePrice = specialStorePrice;
    }

    /**
     * @return the generalStorePrice.
     */
    public int getGeneralStorePrice() {
        return generalStorePrice;
    }

    /**
     * @param generalStorePrice
     *        the generalStorePrice to set.
     */
    public void setGeneralStorePrice(int generalStorePrice) {
        this.generalStorePrice = generalStorePrice;
    }

    /**
     * @return the lowAlchValue.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
    }

    /**
     * @param lowAlchValue
     *        the lowAlchValue to set.
     */
    public void setLowAlchValue(int lowAlchValue) {
        this.lowAlchValue = lowAlchValue;
    }

    /**
     * @return the highAlchValue.
     */
    public int getHighAlchValue() {
        return highAlchValue;
    }

    /**
     * @param highAlchValue
     *        the highAlchValue to set.
     */
    public void setHighAlchValue(int highAlchValue) {
        this.highAlchValue = highAlchValue;
    }

    /**
     * @return the weight.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @param weight
     *        the weight to set.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * @return the bonus.
     */
    public int[] getBonus() {
        return bonus;
    }

    /**
     * @param bonus
     *        the bonus to set.
     */
    public void setBonus(int[] bonus) {
        this.bonus = bonus;
    }

    /**
     * @return the definitions.
     */
    public static ItemDefinition[] getDefinitions() {
        return definitions;
    }

    /**
     * @param definitions
     *        the definitions to set.
     */
    public static void setDefinitions(ItemDefinition[] definitions) {
        ItemDefinition.definitions = definitions;
    }

    public boolean isTwoHanded() {
        return Misc.getIs2H()[itemId];
    }
}