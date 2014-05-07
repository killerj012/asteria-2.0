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
    BRONZE_ARROW(882, 7, 10, 19),
    IRON_ARROW(884, 10, 9, 18),
    STEEL_ARROW(886, 16, 11, 20),
    MITHRIL_ARROW(888, 22, 12, 21),
    ADAMANT_ARROW(890, 31, 13, 22),
    RUNE_ARROW(892, 49, 15, 24),

    /** A collection of bolts. */
    BOLTS(877, 10, 27, 28),
    BARBED_BOLTS(881, 12, 27, 28),
    OPAL_BOLTS(879, 14, 27, 28),
    PEARL_BOLTS(880, 48, 27, 28),
    BOLT_RACK(4740, 55, 27, 28),

    /** A collection of knives. */
    BRONZE_KNIFE(864, 3, 212, 219),
    IRON_KNIFE(863, 4, 213, 220),
    STEEL_KNIFE(865, 7, 214, 221),
    BLACK_KNIFE(869, 8, 215, 222),
    MITHRIL_KNIFE(866, 10, 216, 223),
    ADAMANT_KNIFE(867, 14, 217, 224),
    RUNE_KNIFE(868, 24, 218, 225),

    /** A collection of darts. */
    BRONZE_DART(806, 1, 226, 232),
    IRON_DART(807, 3, 227, 233),
    STEEL_DART(808, 4, 228, 234),
    BLACK_DART(3093, 6, 273, 273),
    MITHRIL_DART(809, 7, 229, 235),
    ADAMANT_DART(810, 10, 230, 236),
    RUNE_DART(811, 14, 231, 237),

    /** A collection of javelins. */
    BRONZE_JAVELIN(825, 6, 200, 206),
    IRON_JAVELIN(826, 10, 201, 207),
    STEEL_JAVELIN(827, 12, 202, 208),
    MITHRIL_JAVELIN(828, 18, 203, 209),
    ADAMANT_JAVELIN(829, 28, 204, 210),
    RUNE_JAVELIN(830, 42, 205, 211),

    /** A collection of throwing axes. */
    BRONZE_THROWNAXE(800, 5, 35, 43),
    IRON_THROWNAXE(801, 7, 36, 42),
    STEEL_THROWNAXE(802, 11, 37, 44),
    MITHRIL_THROWNAXE(803, 16, 38, 45),
    ADAMANT_THROWNAXE(804, 23, 39, 46),
    RUNE_THROWNAXE(805, 26, 41, 48),

    /** Other miscellaneous range ammo. */
    TOKTZ_XIL_UL(6522, -1, 49, -1);

    /** The item id of this ammo. */
    private int itemId;

    /** The strength of this ammo. */
    private int rangedStrength;

    /** The projectile id of this ammo. */
    private int projectileId;

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
     *        the projectile id of this ammo.
     * @param graphicId
     *        the graphic id of this ammo.
     */
    private RangedAmmo(int itemId, int rangedStrength, int projectileId, int graphicId) {
        this.itemId = itemId;
        this.rangedStrength = rangedStrength;
        this.projectileId = projectileId;
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
     * Gets the item id of this ammo.
     * 
     * @return the item id of this ammo.
     */
    public int getItemId() {
        return itemId;
    }

    /**
     * Gets the strength of this ammo.
     * 
     * @return the strength of this ammo.
     */
    public int getRangedStrength() {
        return rangedStrength;
    }

    /**
     * Gets the projectile id of this ammo.
     * 
     * @return the projectile id of this ammo.
     */
    public int getProjectileId() {
        return projectileId;
    }

    /**
     * Gets the graphic id of this ammo.
     * 
     * @return the graphic id of this ammo.
     */
    public int getGraphicId() {
        return graphicId;
    }
}
