package com.asteria.world.entity.combat.range;

import com.asteria.util.Utility;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.AssignWeaponInterface.WeaponInterface;

/**
 * A table of constants that hold data for all ranged ammo.
 * 
 * @author lare96
 */
public enum CombatRangedAmmo {

    // TODO: Load this externally through a json file.

    /** A collection of arrows. */
    BRONZE_ARROW("Bronze arrow", 7, 10, 44, 3, 43, 31, 19),
    IRON_ARROW("Iron arrow", 10, 9, 44, 3, 43, 31, 18),
    STEEL_ARROW("Steel arrow", 16, 11, 44, 3, 43, 31, 20),
    MITHRIL_ARROW("Mithril arrow", 22, 12, 44, 3, 43, 31, 21),
    ADAMANT_ARROW("Adamant arrow", 31, 13, 44, 3, 43, 31, 22),
    RUNE_ARROW("Rune arrow", 49, 15, 44, 3, 43, 31, 24),
    CRYSTAL_ARROW("Crystal bow", 58, 249, 44, 3, 43, 31, 250),

    /** A collection of bolts. */
    BOLTS("Bolts", 10, 27, 44, 3, 43, 31, 28),
    BARBED_BOLTS("Barbed bolts", 12, 27, 44, 3, 43, 31, 28),
    OPAL_BOLTS("Opal bolts", 14, 27, 44, 3, 43, 31, 28),
    PEARL_BOLTS("Pearl bolts", 48, 27, 44, 3, 43, 31, 28),
    BOLT_RACK("Bolt rack", 55, 27, 44, 3, 43, 31, 28),

    /** A collection of knives. */
    BRONZE_KNIFE("Bronze knife", 3, 212, 33, 3, 45, 37, 219),
    IRON_KNIFE("Iron knife", 4, 213, 33, 3, 45, 37, 220),
    STEEL_KNIFE("Steel knife", 7, 214, 33, 3, 45, 37, 221),
    BLACK_KNIFE("Black knife", 8, 215, 33, 3, 45, 37, 222),
    MITHRIL_KNIFE("Mithril knife", 10, 216, 33, 3, 45, 37, 223),
    ADAMANT_KNIFE("Adamant knife", 14, 217, 33, 3, 45, 37, 224),
    RUNE_KNIFE("Rune knife", 24, 218, 33, 3, 45, 37, 225),

    /** A collection of darts. */
    BRONZE_DART("Bronze dart", 1, 226, 40, 2, 45, 37, 232),
    IRON_DART("Iron dart", 3, 227, 40, 2, 45, 37, 233),
    STEEL_DART("Steel dart", 4, 228, 40, 2, 45, 37, 234),
    BLACK_DART("Black dart", 6, 273, 40, 2, 45, 37, 273),
    MITHRIL_DART("Mithril dart", 7, 229, 40, 2, 45, 37, 235),
    ADAMANT_DART("Adamant dart", 10, 230, 40, 2, 45, 37, 236),
    RUNE_DART("Rune dart", 14, 231, 40, 2, 45, 37, 237),

    /** A collection of javelins. */
    BRONZE_JAVELIN("Bronze javelin", 6, 200, 40, 2, 45, 37, 206),
    IRON_JAVELIN("Iron javelin", 10, 201, 40, 2, 45, 37, 207),
    STEEL_JAVELIN("Steel javelin", 12, 202, 40, 2, 45, 37, 208),
    MITHRIL_JAVELIN("Mithril javelin", 18, 203, 40, 2, 45, 37, 209),
    ADAMANT_JAVELIN("Adamant javelin", 28, 204, 40, 2, 45, 37, 210),
    RUNE_JAVELIN("Rune javelin", 42, 205, 40, 2, 45, 37, 211),

    /** A collection of throwing axes. */
    BRONZE_THROWNAXE("Bronze thrownaxe", 5, 35, 44, 3, 43, 31, 43),
    IRON_THROWNAXE("Iron thrownaxe", 7, 36, 44, 3, 43, 31, 42),
    STEEL_THROWNAXE("Steel thrownaxe", 11, 37, 44, 3, 43, 31, 44),
    MITHRIL_THROWNAXE("Mithril thrownaxe", 16, 38, 44, 3, 43, 31, 45),
    ADAMANT_THROWNAXE("Adamant thrownaxe", 23, 39, 44, 3, 43, 31, 46),
    RUNE_THROWNAXE("Rune thrownaxe", 26, 41, 44, 3, 43, 31, 48),

    /** Other miscellaneous range ammo. */
    TOKTZ_XIL_UL("Toktz-xil-ul", 50, 442, 44, 3, 43, 31, 0);

    /** The name of this ranged ammo. */
    private String name;

    /** The strength of this ranged ammo. */
    private int strength;

    /** The projectile data for this ranged ammo. */
    private int projectile, delay, speed, startHeight, endHeight;

    /** The graphic of this ranged ammo. */
    private int graphic;

    /**
     * Create a new {@link CombatRangedAmmo}.
     * 
     * @param name
     *            the name of this ranged ammo.
     * @param strength
     *            the strength of this ranged ammo.
     * @param projectile
     *            the projectile for this ranged ammo.
     * @param delay
     *            the projectile delay for this ranged ammo.
     * @param speed
     *            the projectile speed for this ranged ammo.
     * @param startHeight
     *            the projectile start height for this ranged ammo.
     * @param endHeight
     *            the projectile end height for this ranged ammo.
     * @param graphic
     *            the graphic of this ranged ammo.
     */
    private CombatRangedAmmo(String name, int strength, int projectile,
            int delay, int speed, int startHeight, int endHeight, int graphic) {
        this.name = name;
        this.strength = strength;
        this.projectile = projectile;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.graphic = graphic;
    }

    /**
     * Gets the constant of the ammo being used either in the weapon or arrows
     * slot.
     * 
     * @param player
     *            the player to get the ammo constant for.
     * @return the ammo constant.
     */
    public static CombatRangedAmmo getPlayerAmmo(Player player) {
        if (CombatFactory.crystalBow(player)) {
            return CombatRangedAmmo.CRYSTAL_ARROW;
        }

        int slot = player.getWeapon() == WeaponInterface.SHORTBOW || player
                .getWeapon() == WeaponInterface.LONGBOW || player.getWeapon() == WeaponInterface.CROSSBOW ? Utility.EQUIPMENT_SLOT_ARROWS
                : Utility.EQUIPMENT_SLOT_WEAPON;

        for (CombatRangedAmmo ammo : CombatRangedAmmo.values()) {
            if (player.getEquipment().get(slot)
                    .getDefinition().getItemName().toLowerCase()
                    .contains(ammo.name.toLowerCase())) {
                return ammo;
            }
        }
        return null;
    }

    /**
     * Get the strength of this ranged ammo.
     * 
     * @return the strength of this ranged ammo.
     */
    public int getStrength() {
        return strength;
    }

    /**
     * Get the projectile for this ranged ammo.
     * 
     * @return the projectile for this ranged ammo.
     */
    public int getProjectile() {
        return projectile;
    }

    /**
     * Get the projectile delay for this ranged ammo.
     * 
     * @return the projectile delay for this ranged ammo.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Get the projectile speed for this ranged ammo.
     * 
     * @return the projectile speed for this ranged ammo.
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Get the projectile start height for this ranged ammo.
     * 
     * @return the projectile start height for this ranged ammo.
     */
    public int getStartHeight() {
        return startHeight;
    }

    /**
     * Get the projectile end height for this ranged ammo.
     * 
     * @return the projectile end height for this ranged ammo.
     */
    public int getEndHeight() {
        return endHeight;
    }

    /**
     * Get the graphic of this ranged ammo.
     * 
     * @return the graphic of this ranged ammo.
     */
    public int getGraphic() {
        return graphic;
    }
}
