package server.world.entity.combat.range;

import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;

/**
 * A fixed table that holds data for ranged ammo.
 * 
 * @author lare96
 */
public enum RangedAmmo {

    /** A collection of arrows. */
    BRONZE_ARROW("Bronze arrow", 7, 10, 44, 3, 43, 31, 19),
    IRON_ARROW("Iron arrow", 10, 9, 44, 3, 43, 31, 18),
    STEEL_ARROW("Steel arrow", 16, 11, 44, 3, 43, 31, 20),
    MITHRIL_ARROW("Mithril arrow", 22, 12, 44, 3, 43, 31, 21),
    ADAMANT_ARROW("Adamant arrow", 31, 13, 44, 3, 43, 31, 22),
    RUNE_ARROW("Rune arrow", 49, 15, 44, 3, 43, 31, 24),

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

    /** The item id of this ammo. */
    private String name;

    /** The strength of this ammo. */
    private int rangedStrength;

    /** The projectile data for of this ammo. */
    private int projectileId, delay, speed, startHeight, endHeight;

    /** The graphic id of this ammo. */
    private int graphicId;

    /**
     * Create a new {@link RangedAmmo}.
     * 
     * @param name
     *        the name of this ammo.
     * @param rangedStrength
     *        the strength of this ammo.
     * @param projectileId
     *        the projectile data for of this ammo.
     * @param delay
     *        the projectile data for of this ammo.
     * @param speed
     *        the projectile data for of this ammo.
     * @param startHeight
     *        the projectile data for of this ammo.
     * @param endHeight
     *        the projectile data for of this ammo.
     * @param graphicId
     *        the graphic id of this ammo.
     */
    private RangedAmmo(String name, int rangedStrength, int projectileId, int delay, int speed, int startHeight, int endHeight, int graphicId) {
        this.name = name;
        this.rangedStrength = rangedStrength;
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.graphicId = graphicId;
    }

    /**
     * Gets the ammo you're using.
     * 
     * @param player
     *        the player to calculate for.
     * @return the ammo being used.
     */
    public static RangedAmmo getAmmo(Player player) {
        if (player.getWeapon() == WeaponInterface.SHORTBOW || player.getWeapon() == WeaponInterface.LONGBOW || player.getWeapon() == WeaponInterface.CROSSBOW) {
            for (RangedAmmo tableItem : RangedAmmo.values()) {
                if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_ARROWS).getDefinition().getItemName().startsWith(tableItem.getName())) {
                    return tableItem;
                }
            }
        } else {
            for (RangedAmmo tableItem : RangedAmmo.values()) {
                if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().getItemName().startsWith(tableItem.getName())) {
                    return tableItem;
                }
            }
        }
        return null;
    }

    /**
     * Get the name of this ammo.
     * 
     * @return the name of this ammo.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the strength of this ammo.
     * 
     * @return the rangedStrength
     */
    public int getRangedStrength() {
        return rangedStrength;
    }

    /**
     * Get the projectile data for of this ammo.
     * 
     * @return the projectileId
     */
    public int getProjectileId() {
        return projectileId;
    }

    /**
     * Get the projectile data for of this ammo.
     * 
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Get the projectile data for of this ammo.
     * 
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Get the projectile data for of this ammo.
     * 
     * @return the startHeight
     */
    public int getStartHeight() {
        return startHeight;
    }

    /**
     * Get the projectile data for of this ammo.
     * 
     * @return the endHeight
     */
    public int getEndHeight() {
        return endHeight;
    }

    /**
     * Get the graphic id of this ammo.
     * 
     * @return the graphicId
     */
    public int getGraphicId() {
        return graphicId;
    }
}
