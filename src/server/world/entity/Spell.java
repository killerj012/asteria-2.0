package server.world.entity;

import java.util.ArrayList;
import java.util.List;

import server.util.Misc;
import server.world.entity.combat.magic.MagicRuneCombination;
import server.world.entity.combat.magic.MagicRuneStaff;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A dynamic class that is primarily used for implemenation, although it can
 * represent any generic castable spell.
 * 
 * @author lare96
 */
public abstract class Spell {

    /**
     * Determines if the spell is able to be cast.
     * 
     * @param cast
     *        the person casting the spell.
     * @param castOn
     *        the target of the spell.
     * @return true if the spell is successful.
     */
    public boolean prepareCast(Entity cast, Entity castOn) {
        if (cast.isPlayer()) {
            Player player = (Player) cast;

            /** Check the level required. */
            if (player.getSkills()[Misc.MAGIC].getLevel() < this.levelRequired()) {
                player.getPacketBuilder().sendMessage("You need a Magic level of " + this.levelRequired() + " to cast this spell.");
                return false;
            }

            /** Check the items required. */
            if (this.itemsRequired(player) != null) {
                Item[] compareItem = this.itemsRequired(player).clone();
                // MagicRuneStaff runeStaff = this.getStaff(player);
                // MagicRuneCombination[] combinationRune =
                // this.getCombinationRunes(player);

                // XXX: filter out staff here
                // XXX: filter out combination runes here

                if (!player.getInventory().getContainer().contains(compareItem)) {
                    player.getPacketBuilder().sendMessage("You do not have the required items to cast this spell.");
                    cast.getCombatBuilder().reset();
                    return false;
                }

                player.getInventory().deleteItemSet(compareItem);
            }

            /** Check the equipment required. */
            if (this.equipmentRequired(player) != null) {
                if (!player.getEquipment().getContainer().contains(this.equipmentRequired(player))) {
                    player.getPacketBuilder().sendMessage("You do not have the required equipment to cast this spell.");
                    cast.getCombatBuilder().reset();
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    /**
     * Gets the staff that the player is currently wielding (if any).
     * 
     * @param player
     *        the player that will be checked for a staff.
     * @return the staff that the player is currently wielding.
     */
    public MagicRuneStaff getStaff(Player player) {
        for (MagicRuneStaff runeStaff : MagicRuneStaff.values()) {
            for (int itemId : runeStaff.getStaffIds()) {
                if (itemId == player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON)) {
                    return runeStaff;
                }
            }
        }
        return null;
    }

    /**
     * Gets the combination runes in the players inventory (if any).
     * 
     * @param player
     *        the player that will be checked for a staff.
     * @return the staff that the player is currently wielding.
     */
    public MagicRuneCombination[] getCombinationRunes(Player player) {
        List<MagicRuneCombination> combinationRune = new ArrayList<MagicRuneCombination>();

        for (MagicRuneCombination rune : MagicRuneCombination.values()) {
            if (player.getInventory().getContainer().contains(rune.getCombinationRune())) {
                combinationRune.add(rune);
            }
        }

        if (combinationRune.isEmpty()) {
            return null;
        }
        return (MagicRuneCombination[]) combinationRune.toArray();
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
     *        the player's inventory to check.
     * 
     * @return the items required to cast this spell.
     */
    public abstract Item[] itemsRequired(Player player);

    /**
     * The equipment required to cast this spell.
     * 
     * @param player
     *        the player's equipment to check.
     * 
     * @return the equipment required to cast this spell.
     */
    public abstract Item[] equipmentRequired(Player player);

    /**
     * Invoked when the spell is cast.
     * 
     * @param cast
     *        the person casting the spell.
     * @param castOn
     *        the target of the spell.
     */
    public abstract void castSpell(Entity cast, Entity castOn);
}
