package com.asteria.world.item;

import com.asteria.util.JsonLoader;
import com.asteria.util.Utility;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * A single entry in a table of important data used for {@link Item}s.
 * 
 * @author lare96
 */
public class ItemDefinition {

    /** The item definitions. */
    private static ItemDefinition[] definitions = new ItemDefinition[7956];

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

    /** The un-noted id of this item. */
    private int unNotedId;

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
    private int[] bonus;

    /** If this item is two handed. */
    private boolean twoHanded;

    /** If this item is a full helm. */
    private boolean fullHelm;

    /** If this item is a platebody. */
    private boolean platebody;

    /**
     * Prepares the dynamic json loader for loading item definitions.
     * 
     * @return the dynamic json loader.
     * @throws Exception
     *             if any errors occur while preparing for load.
     */
    public static JsonLoader parseItems() throws Exception {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                int index = reader.get("id").getAsInt();
                definitions[index] = new ItemDefinition();
                definitions[index].itemId = index;
                definitions[index].itemName = reader.get("name").getAsString();
                definitions[index].itemDescription = reader.get("examine")
                        .getAsString();
                definitions[index].equipmentSlot = reader.get("equipmentType")
                        .getAsInt();
                definitions[index].isNoted = reader.get("noted").getAsBoolean();
                definitions[index].isNoteable = reader.get("noteable")
                        .getAsBoolean();
                definitions[index].isStackable = reader.get("stackable")
                        .getAsBoolean();
                definitions[index].unNotedId = reader.get("parentId")
                        .getAsInt();
                definitions[index].specialStorePrice = reader.get(
                        "specialStorePrice").getAsInt();
                definitions[index].generalStorePrice = reader.get(
                        "generalStorePrice").getAsInt();
                definitions[index].highAlchValue = reader.get("highAlchValue")
                        .getAsInt();
                definitions[index].lowAlchValue = reader.get("lowAlchValue")
                        .getAsInt();
                definitions[index].weight = reader.get("weight").getAsDouble();
                definitions[index].bonus = builder.fromJson(
                        reader.get("bonuses").getAsJsonArray(), int[].class);

                if (definitions[index].equipmentSlot == Utility.EQUIPMENT_SLOT_ARROWS
                        || definitions[index].itemName.contains("knife")
                        || definitions[index].itemName.contains("dart")
                        || definitions[index].itemName.contains("thrownaxe")
                        || definitions[index].itemName.contains("javelin")) {
                    definitions[index].bonus[11] = 0;
                }

                definitions[index].twoHanded = reader.get("twoHanded")
                        .getAsBoolean();
                definitions[index].platebody = reader.get("platebody")
                        .getAsBoolean();
                definitions[index].fullHelm = reader.get("fullHelm")
                        .getAsBoolean();
            }

            @Override
            public String filePath() {
                return "./data/json/items/item_definitions.json";
            }
        };
    }

    /**
     * Gets the item id
     * 
     * @return the item id.
     */
    public int getItemId() {
        return itemId;
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
     * Gets the item description
     * 
     * @return the item description.
     */
    public String getItemDescription() {
        return itemDescription;
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
     * Gets if this item is noted.
     * 
     * @return true if this item is noted.
     */
    public boolean isNoted() {
        return isNoted;
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
     * Gets if this item is stackable.
     * 
     * @return true if this item is stackable.
     */
    public boolean isStackable() {
        return isStackable;
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
     * Gets the special store price.
     * 
     * @return the special store price.
     */
    public int getSpecialStorePrice() {
        return specialStorePrice;
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
     * Gets the low alch value.
     * 
     * @return the low alch value.
     */
    public int getLowAlchValue() {
        return lowAlchValue;
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
     * Gets the weight.
     * 
     * @return the weight.
     */
    public double getWeight() {
        return weight;
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
     * Gets if this is a two handed weapon.
     * 
     * @return true if this is a two handed weapon.
     */
    public boolean isTwoHanded() {
        return twoHanded;
    }

    /**
     * Gets if this is a full helm.
     * 
     * @return true if this is a full helm.
     */
    public boolean isFullHelm() {
        return fullHelm;
    }

    /**
     * Gets if this is a platebody.
     * 
     * @return true if this is a platebody.
     */
    public boolean isPlatebody() {
        return platebody;
    }

    /**
     * Gets the definitions
     * 
     * @return the definitions.
     */
    public static ItemDefinition[] getDefinitions() {
        return definitions;
    }
}