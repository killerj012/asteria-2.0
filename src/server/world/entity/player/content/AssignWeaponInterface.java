package server.world.entity.player.content;

import java.util.HashMap;
import java.util.Map;

import server.util.Misc;
import server.world.entity.combat.Combat.CombatType;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * Changes the interface in the first sidebar whenever a new weapon is equipped
 * or an existing item is unequipped.
 * 
 * @author lare96
 */
public class AssignWeaponInterface {

    /**
     * A map of every weapon in the game and the corresponding data for the
     * interface that will be displayed when the weapon is equipped.
     */
    private static Map<Integer, WeaponInterface> weaponInterface = new HashMap<Integer, WeaponInterface>();

    /**
     * Whenever the singleton instance is created, every weapon in the game and
     * the corresponding data for the interface will be loaded into a map.
     */
    static {

        /** Loop through all of the item definitions. */
        for (ItemDefinition def : ItemDefinition.getDefinitions()) {

            /** Filter out the weapons from non-weapons. */
            if (def == null || def.isNoted() || def.getEquipmentSlot() != Misc.EQUIPMENT_SLOT_WEAPON) {
                continue;
            }

            /** Add the weapons and the appropriate interfaces into the map. */
            if (def.getItemName().startsWith("Staff") || def.getItemName().endsWith("staff")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.STAFF);
            } else if (def.getItemName().startsWith("Scythe")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.SCYTHE);
            } else if (def.getItemName().endsWith("warhammer")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.WARHAMMER);
            } else if (def.getItemName().endsWith("battleaxe")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.BATTLE_AXE);
            } else if (def.getItemName().equals("Crossbow") || def.getItemName().endsWith("crossbow")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.CROSSBOW);
            } else if (def.getItemName().endsWith("shortbow")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.SHORT_BOW);
            } else if (def.getItemName().endsWith("longbow")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.LONG_BOW);
            } else if (def.getItemName().endsWith("dagger")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.DAGGER);
            } else if (def.getItemName().endsWith("longsword")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.LONG_SWORD);
            } else if (def.getItemName().endsWith(" sword") && !def.getItemName().endsWith("2h sword")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.SWORD);
            } else if (def.getItemName().endsWith("scimitar")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.SCIMITAR);
            } else if (def.getItemName().endsWith("2h sword")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.TWO_HANDED_SWORD);
            } else if (def.getItemName().endsWith("mace")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.MACE);
            } else if (def.getItemName().endsWith("knife")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.KNIFE);
            } else if (def.getItemName().endsWith("spear")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.SPEAR);
            } else if (def.getItemName().endsWith("pickaxe")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.PICKAXE);
            } else if (def.getItemName().endsWith("claws")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.CLAWS);
            } else if (def.getItemName().endsWith("halberd")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.HALBERD);
            } else if (def.getItemName().endsWith("whip")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.WHIP);
            } else if (def.getItemName().endsWith("thrownaxe")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.THROWNAXE);
            } else if (def.getItemName().endsWith("javelin")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.JAVELIN);
            } else if (def.getItemName().endsWith("dart")) {
                weaponInterface.put(def.getItemId(), WeaponInterface.DART);
            } else {
                weaponInterface.put(def.getItemId(), WeaponInterface.UNARMED);
            }
        }
    }

    /**
     * All of the interfaces for weapons and the data needed to display these
     * interfaces properly.
     * 
     * @author lare96
     */
    public enum WeaponInterface {
        STAFF(328, 331, FightType.STAFF_BASH, CombatType.MELEE),
        WARHAMMER(425, 428, FightType.WARHAMMER_POUND, CombatType.MELEE),
        SCYTHE(776, 779, FightType.SCYTHE_REAP, CombatType.MELEE),
        BATTLE_AXE(1698, 1701, FightType.BATTLEAXE_CHOP, CombatType.MELEE),
        CROSSBOW(1749, 1752, FightType.CROSSBOW_ACCURATE, CombatType.RANGE),
        SHORT_BOW(1764, 1767, FightType.SHORTBOW_ACCURATE, CombatType.RANGE),
        LONG_BOW(1764, 1767, FightType.LONGBOW_ACCURATE, CombatType.RANGE),
        DAGGER(2276, 2279, FightType.DAGGER_STAB, CombatType.MELEE),
        SWORD(2276, 2279, FightType.SWORD_STAB, CombatType.MELEE),
        SCIMITAR(2423, 2426, FightType.SCIMITAR_CHOP, CombatType.MELEE),
        LONG_SWORD(2423, 2426, FightType.LONGSWORD_CHOP, CombatType.MELEE),
        MACE(3796, 3799, FightType.MACE_POUND, CombatType.MELEE),
        KNIFE(4446, 4449, FightType.KNIFE_ACCURATE, CombatType.RANGE),
        SPEAR(4679, 4682, FightType.SPEAR_POUND, CombatType.MELEE),
        TWO_HANDED_SWORD(4705, 4708, FightType.TWOHANDEDSWORD_CHOP, CombatType.MELEE),
        PICKAXE(5570, 5573, FightType.PICKAXE_IMPALE, CombatType.MELEE),
        CLAWS(7762, 7765, FightType.CLAWS_CHOP, CombatType.MELEE),
        HALBERD(8460, 8463, FightType.HALBERD_FEND, CombatType.MELEE),
        UNARMED(5855, 5857, FightType.UNARMED_PUNCH, CombatType.MELEE),
        WHIP(12290, 12293, FightType.WHIP_FLICK, CombatType.MELEE),
        THROWNAXE(4446, 4449, FightType.THROWNAXE_ACCURATE, CombatType.RANGE),
        DART(4446, 4449, FightType.DART_ACCURATE, CombatType.RANGE),
        JAVELIN(4446, 4449, FightType.JAVELIN_ACCURATE, CombatType.RANGE);

        /**
         * The interface that will be displayed on the sidebar.
         */
        private int interfaceId;

        /**
         * The line that the name of the item will be printed to.
         */
        private int nameLineId;

        /**
         * The default fight type for this interface.
         */
        private FightType defaultFightType;

        /**
         * The combat type of the interface.
         */
        private CombatType combatType;

        /**
         * Creates a new weapon interface.
         * 
         * @param interfaceId
         *        the interface that will be displayed on the sidebar.
         * @param nameLineId
         *        the line that the name of the item will be printed to.
         * @param defaultFightType
         *        the default fight type for this interface.
         * @param combatType
         *        the combat type of the interface.
         */
        WeaponInterface(int interfaceId, int nameLineId, FightType defaultFightType, CombatType combatType) {
            this.setInterfaceId(interfaceId);
            this.setNameLineId(nameLineId);
            this.setDefaultFightType(defaultFightType);
            this.setCombatType(combatType);
        }

        /**
         * @return the interfaceId.
         */
        public int getInterfaceId() {
            return interfaceId;
        }

        /**
         * @param interfaceId
         *        the interfaceId to set.
         */
        public void setInterfaceId(int interfaceId) {
            this.interfaceId = interfaceId;
        }

        /**
         * @return the nameLineId.
         */
        public int getNameLineId() {
            return nameLineId;
        }

        /**
         * @param nameLineId
         *        the nameLineId to set.
         */
        public void setNameLineId(int nameLineId) {
            this.nameLineId = nameLineId;
        }

        /**
         * @return the defaultFightType.
         */
        public FightType getDefaultFightType() {
            return defaultFightType;
        }

        /**
         * @param defaultFightType
         *        the defaultFightType to set.
         */
        public void setDefaultFightType(FightType defaultFightType) {
            this.defaultFightType = defaultFightType;
        }

        /**
         * @return the combatType.
         */
        public CombatType getCombatType() {
            return combatType;
        }

        /**
         * @param combatType
         *        the combatType to set.
         */
        public void setCombatType(CombatType combatType) {
            this.combatType = combatType;
        }
    }

    /**
     * The different fight types on the weapon interfaces.
     * 
     * @author lare96
     */
    public enum FightType {
        STAFF_BASH,
        STAFF_POUND,
        STAFF_FOCUS,
        WARHAMMER_POUND,
        WARHAMMER_PUMMEL,
        WARHAMMER_BLOCK,
        SCYTHE_REAP,
        SCYTHE_CHOP,
        SCYTHE_JAB,
        SCYTHE_BLOCK,
        BATTLEAXE_CHOP,
        BATTLEAXE_HACK,
        BATTLEAXE_SMASH,
        BATTLEAXE_BLOCK,
        CROSSBOW_ACCURATE,
        CROSSBOW_RAPID,
        CROSSBOW_LONGRANGE,
        SHORTBOW_ACCURATE,
        SHORTBOW_RAPID,
        SHORTBOW_LONGRANGE,
        LONGBOW_ACCURATE,
        LONGBOW_RAPID,
        LONGBOW_LONGRANGE,
        DAGGER_STAB,
        DAGGER_LUNGE,
        DAGGER_SLASH,
        DAGGER_BLOCK,
        SWORD_STAB,
        SWORD_LUNGE,
        SWORD_SLASH,
        SWORD_BLOCK,
        SCIMITAR_CHOP,
        SCIMITAR_SLASH,
        SCIMITAR_LUNGE,
        SCIMITAR_BLOCK,
        LONGSWORD_CHOP,
        LONGSWORD_SLASH,
        LONGSWORD_LUNGE,
        LONGSWORD_BLOCK,
        MACE_POUND,
        MACE_PUMMEL,
        MACE_SPIKE,
        MACE_BLOCK,
        KNIFE_ACCURATE,
        KNIFE_RAPID,
        KNIFE_LONGRANGE,
        SPEAR_LUNGE,
        SPEAR_SWIPE,
        SPEAR_POUND,
        SPEAR_BLOCK,
        TWOHANDEDSWORD_CHOP,
        TWOHANDEDSWORD_SLASH,
        TWOHANDEDSWORD_SMASH,
        TWOHANDEDSWORD_BLOCK,
        PICKAXE_SPIKE,
        PICKAXE_IMPALE,
        PICKAXE_SMASH,
        PICKAXE_BLOCK,
        CLAWS_CHOP,
        CLAWS_SLASH,
        CLAWS_LUNGE,
        CLAWS_BLOCK,
        HALBERD_JAB,
        HALBERD_SWIPE,
        HALBERD_FEND,
        UNARMED_PUNCH,
        UNARMED_KICK,
        UNARMED_BLOCK,
        WHIP_FLICK,
        WHIP_LASH,
        WHIP_DEFLECT,
        THROWNAXE_ACCURATE,
        THROWNAXE_RAPID,
        THROWNAXE_LONGRANGE,
        DART_ACCURATE,
        DART_RAPID,
        DART_LONGRANGE,
        JAVELIN_ACCURATE,
        JAVELIN_RAPID,
        JAVELIN_LONGRANGE
    }

    /**
     * Assigns the correct interface for the player based on the item.
     * 
     * @param player
     *        the player to assign the interface for.
     * @param item
     *        the item to base the interface on.
     */
    public static void assignInterface(Player player, Item item) {

        /** Block if this item isn't a weapon. */
        if (item == null || item.getDefinition().getEquipmentSlot() != Misc.EQUIPMENT_SLOT_WEAPON) {
            return;
        }

        /** Retrieve the interface for the weapon from the map. */
        WeaponInterface weapon = weaponInterface.get(item.getId());

        /** Write the interface to the sidebar. */
        if (weapon == WeaponInterface.UNARMED) {
            player.getPacketBuilder().sendSidebarInterface(0, weapon.getInterfaceId());
            player.getPacketBuilder().sendString("Unarmed", weapon.getNameLineId());
            player.setWeapon(WeaponInterface.UNARMED);
            return;
        } else if (weapon == WeaponInterface.CROSSBOW) {
            player.getPacketBuilder().sendString("Weapon: ", weapon.getNameLineId() - 1);
        } else if (weapon == WeaponInterface.WHIP) {
            player.getPacketBuilder().sendString("Weapon: ", weapon.getNameLineId() - 1);
        }

        player.getPacketBuilder().sendItemOnInterface(weapon.getInterfaceId() + 1, 200, item.getId());
        player.getPacketBuilder().sendSidebarInterface(0, weapon.getInterfaceId());
        player.getPacketBuilder().sendString("" + item.getDefinition().getItemName() + "", weapon.getNameLineId());
        player.setWeapon(weapon);
    }

    /**
     * Resets the sidebar when an item is unequipped from the weapon slot.
     * 
     * @param player
     *        the player to reset the sidebar for.
     */
    public static void reset(Player player) {

        /** Reset the sidebar back to "unarmed". */
        player.getPacketBuilder().sendSidebarInterface(0, WeaponInterface.UNARMED.getInterfaceId());
        player.getPacketBuilder().sendString("Unarmed", WeaponInterface.UNARMED.getNameLineId());
        player.setWeapon(WeaponInterface.UNARMED);
    }
}
