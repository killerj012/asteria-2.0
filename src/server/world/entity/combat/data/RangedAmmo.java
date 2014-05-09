package server.world.entity.combat.data;

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
    BRONZE_ARROW(882, 7, 10, 44, 3, 43, 31, 15, 19),
    IRON_ARROW(884, 10, 9, 44, 3, 43, 31, 15, 18),
    STEEL_ARROW(886, 16, 11, 44, 3, 43, 31, 15, 20),
    MITHRIL_ARROW(888, 22, 12, 44, 3, 43, 31, 15, 21),
    ADAMANT_ARROW(890, 31, 13, 44, 3, 43, 31, 15, 22),
    RUNE_ARROW(892, 49, 15, 44, 3, 43, 31, 15, 24),

    /** A collection of bolts. */
    BOLTS(877, 10, 27, 44, 3, 43, 31, 15, 28),
    BARBED_BOLTS(881, 12, 27, 44, 3, 43, 31, 15, 28),
    OPAL_BOLTS(879, 14, 27, 44, 3, 43, 31, 15, 28),
    PEARL_BOLTS(880, 48, 27, 44, 3, 43, 31, 15, 28),
    BOLT_RACK(4740, 55, 27, 44, 3, 43, 31, 15, 28),

    /** A collection of knives. */
    BRONZE_KNIFE(864, 3, 212, 33, 3, 45, 37, 5, 219),
    IRON_KNIFE(863, 4, 213, 33, 3, 45, 37, 5, 220),
    STEEL_KNIFE(865, 7, 214, 33, 3, 45, 37, 5, 221),
    BLACK_KNIFE(869, 8, 215, 33, 3, 45, 37, 5, 222),
    MITHRIL_KNIFE(866, 10, 216, 33, 3, 45, 37, 5, 223),
    ADAMANT_KNIFE(867, 14, 217, 33, 3, 45, 37, 5, 224),
    RUNE_KNIFE(868, 24, 218, 33, 3, 45, 37, 5, 225),

    /** A collection of darts. */
    BRONZE_DART(806, 1, 226, 40, 2, 45, 37, 5, 232),
    IRON_DART(807, 3, 227, 40, 2, 45, 37, 5, 233),
    STEEL_DART(808, 4, 228, 40, 2, 45, 37, 5, 234),
    BLACK_DART(3093, 6, 273, 40, 2, 45, 37, 5, 273),
    MITHRIL_DART(809, 7, 229, 40, 2, 45, 37, 5, 235),
    ADAMANT_DART(810, 10, 230, 40, 2, 45, 37, 5, 236),
    RUNE_DART(811, 14, 231, 40, 2, 45, 37, 5, 237),

    /** A collection of javelins. */
    BRONZE_JAVELIN(825, 6, 200, 40, 2, 45, 37, 5, 206),
    IRON_JAVELIN(826, 10, 201, 40, 2, 45, 37, 5, 207),
    STEEL_JAVELIN(827, 12, 202, 40, 2, 45, 37, 5, 208),
    MITHRIL_JAVELIN(828, 18, 203, 40, 2, 45, 37, 5, 209),
    ADAMANT_JAVELIN(829, 28, 204, 40, 2, 45, 37, 5, 210),
    RUNE_JAVELIN(830, 42, 205, 40, 2, 45, 37, 5, 211),

    /** A collection of throwing axes. */
    BRONZE_THROWNAXE(800, 5, 35, 44, 3, 43, 31, 15, 43),
    IRON_THROWNAXE(801, 7, 36, 44, 3, 43, 31, 15, 42),
    STEEL_THROWNAXE(802, 11, 37, 44, 3, 43, 31, 15, 44),
    MITHRIL_THROWNAXE(803, 16, 38, 44, 3, 43, 31, 15, 45),
    ADAMANT_THROWNAXE(804, 23, 39, 44, 3, 43, 31, 15, 46),
    RUNE_THROWNAXE(805, 26, 41, 44, 3, 43, 31, 15, 48),

    /** Other miscellaneous range ammo. */
    TOKTZ_XIL_UL(6522, 50, 442, 44, 3, 43, 31, 15, 0);

    /** The item id of this ammo. */
    private int itemId;

    /** The strength of this ammo. */
    private int rangedStrength;

    /** The projectile data for of this ammo. */
    private int projectileId, delay, speed, startHeight, endHeight, curve;

    /** The graphic id of this ammo. */
    private int graphicId;

    /**
     * Create a new {@link RangedAmmo}.
     * 
     * @param itemId
     *        the item id of this ammo.
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
     * @param curve
     *        the projectile data for of this ammo.
     * @param graphicId
     *        the graphic id of this ammo.
     */
    private RangedAmmo(int itemId, int rangedStrength, int projectileId, int delay, int speed, int startHeight, int endHeight, int curve, int graphicId) {
        this.itemId = itemId;
        this.rangedStrength = rangedStrength;
        this.projectileId = projectileId;
        this.delay = delay;
        this.speed = speed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.curve = curve;
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
                if (tableItem.getItemId() == player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_ARROWS).getId()) {
                    return tableItem;
                }
            }
        } else {
            for (RangedAmmo tableItem : RangedAmmo.values()) {
                if (tableItem.getItemId() == player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId()) {
                    return tableItem;
                }
            }
        }
        return null;
    }

    /**
     * Get the item id of this ammo.
     * 
     * @return the item id of this ammo.
     */
    public int getItemId() {
        return itemId;
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
     * Get the projectile data for of this ammo.
     * 
     * @return the curve
     */
    public int getCurve() {
        return curve;
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
