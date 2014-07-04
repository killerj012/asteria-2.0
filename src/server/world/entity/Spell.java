package server.world.entity;

import server.util.Misc;
import server.world.entity.combat.magic.CombatMagicRuneCombination;
import server.world.entity.combat.magic.CombatMagicStaff;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A dynamic class that is primarily used for the implementation of more
 * specific spells although it can be used for any generic spell.
 * 
 * @author lare96
 */
public abstract class Spell {

    /**
     * Determines if the spell is able to be cast.
     * 
     * @param cast
     *            the person casting the spell.
     * @param castOn
     *            the target of the spell.
     * @return true if the spell is successful.
     */
    public boolean prepareCast(Entity cast, Entity castOn) {
        if (cast.type() == EntityType.PLAYER) {
            Player player = (Player) cast;

            /** Check the level required. */
            if (player.getSkills()[Misc.MAGIC].getLevel() < levelRequired()) {
                player.getPacketBuilder().sendMessage(
                        "You need a Magic level of " + levelRequired()
                                + " to cast this spell.");
                return false;
            }

            /** Check the items required. */
            if (this.itemsRequired(player) != null) {
                Item[] compareItem = itemsRequired(player).clone();
                CombatMagicStaff runeStaff = getStaff(player);
                CombatMagicRuneCombination[] combinationRune = getCombinationRunes(player);
                Item[] removeRune = new Item[compareItem.length
                        + combinationRune.length];
                int slot = 0;

                if (runeStaff != null) {
                    for (int i = 0; i < compareItem.length; i++) {
                        if (compareItem[i] == null) {
                            continue;
                        }

                        for (int runeId : runeStaff.getRuneIds()) {
                            if (compareItem[i] == null) {
                                continue;
                            }

                            if (compareItem[i].getId() == runeId) {
                                compareItem[i] = null;
                                continue;
                            }
                        }
                    }
                }

                for (int i = 0; i < compareItem.length; i++) {
                    if (compareItem[i] == null) {
                        continue;
                    }

                    for (CombatMagicRuneCombination rune : combinationRune) {
                        if (compareItem[i] == null || rune == null) {
                            continue;
                        }

                        int runesNeeded = compareItem[i].getAmount();

                        if (compareItem[i].getId() == rune.getFirstRune()) {
                            if (runesNeeded > player.getInventory()
                                    .getContainer()
                                    .getCount(rune.getCombinationRune())) {
                                continue;
                            }

                            compareItem[i].decrementAmountBy(runesNeeded);
                            removeRune[slot++] = new Item(
                                    rune.getCombinationRune(), runesNeeded);
                            player.getInventory().getContainer()
                                    .getById(rune.getCombinationRune())
                                    .decrementAmountBy(runesNeeded);
                        } else if (compareItem[i].getId() == rune
                                .getSecondRune()) {
                            if (runesNeeded > player.getInventory()
                                    .getContainer()
                                    .getCount(rune.getCombinationRune())) {
                                continue;
                            }

                            compareItem[i].decrementAmountBy(runesNeeded);
                            player.getInventory().getContainer()
                                    .getById(rune.getCombinationRune())
                                    .decrementAmountBy(runesNeeded);
                            removeRune[slot++] = new Item(
                                    rune.getCombinationRune(), runesNeeded);
                        }

                        if (compareItem[i].getAmount() == 0) {
                            compareItem[i] = null;
                        }
                    }
                }

                if (!player.getInventory().getContainer().contains(compareItem)) {
                    player.getPacketBuilder()
                            .sendMessage(
                                    "You do not have the required items to cast this spell.");

                    if (cast.getCombatBuilder().isAttacking()
                            || cast.getCombatBuilder().isBeingAttacked()) {
                        player.setAutocastSpell(null);
                        player.setAutocast(false);
                        player.getPacketBuilder().sendConfig(108, 0);
                        player.setCastSpell(null);
                    }

                    player.getInventory().addItemSet(removeRune);
                    return false;
                }

                player.getInventory().deleteItemSet(compareItem);
            }

            /** Check the equipment required. */
            if (this.equipmentRequired(player) != null) {
                if (!player.getEquipment().getContainer()
                        .contains(this.equipmentRequired(player))) {
                    player.getPacketBuilder()
                            .sendMessage(
                                    "You do not have the required equipment to cast this spell.");

                    if (cast.getCombatBuilder().isAttacking()
                            || cast.getCombatBuilder().isBeingAttacked()) {
                        player.setAutocastSpell(null);
                        player.setAutocast(false);
                        player.getPacketBuilder().sendConfig(108, 0);
                        player.setCastSpell(null);
                    }
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /**
     * Gets the staff that the player is currently wielding if any.
     * 
     * @param player
     *            the player that will be checked for a staff.
     * @return the staff that the player is currently wielding.
     */
    public CombatMagicStaff getStaff(Player player) {
        for (CombatMagicStaff runeStaff : CombatMagicStaff.values()) {
            for (int itemId : runeStaff.getStaffIds()) {
                if (itemId == player.getEquipment().getContainer()
                        .getItemId(Misc.EQUIPMENT_SLOT_WEAPON)) {
                    return runeStaff;
                }
            }
        }
        return null;
    }

    /**
     * Gets the combination runes in the players inventory if any.
     * 
     * @param player
     *            the player that will be checked for rune.
     * @return the runes in the players inventory.
     */
    public CombatMagicRuneCombination[] getCombinationRunes(Player player) {
        CombatMagicRuneCombination[] array = new CombatMagicRuneCombination[CombatMagicRuneCombination
                .values().length];
        int slot = 0;

        for (CombatMagicRuneCombination rune : CombatMagicRuneCombination
                .values()) {
            if (player.getInventory().getContainer()
                    .contains(rune.getCombinationRune())) {
                array[slot++] = rune;
            }
        }
        return array;
    }

    /**
     * The id of the spell being cast (if any).
     * 
     * @return the id of the spell.
     */
    public abstract int spellId();

    /**
     * The level required to cast this spell.
     * 
     * @return the level required to cast this spell.
     */
    public abstract int levelRequired();

    /**
     * The base experience given when this spell is cast.
     * 
     * @return the base experience given when this spell is cast.
     */
    public abstract int baseExperience();

    /**
     * The items required to cast this spell.
     * 
     * @param player
     *            the player's inventory to check.
     * 
     * @return the items required to cast this spell.
     */
    public abstract Item[] itemsRequired(Player player);

    /**
     * The equipment required to cast this spell.
     * 
     * @param player
     *            the player's equipment to check.
     * 
     * @return the equipment required to cast this spell.
     */
    public abstract Item[] equipmentRequired(Player player);

    /**
     * Invoked when the spell is cast.
     * 
     * @param cast
     *            the person casting the spell.
     * @param castOn
     *            the target of the spell.
     */
    public abstract void castSpell(Entity cast, Entity castOn);
}
