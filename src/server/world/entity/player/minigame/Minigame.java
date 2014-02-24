package server.world.entity.player.minigame;

import server.world.entity.Entity;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A basic free-form {@link Minigame} that can provide implementation for a wide
 * variety of different types of minigames.
 * 
 * @author lare96
 */
public abstract class Minigame {

    // TODO: Integrate this and the cycled minigame.

    /**
     * Fired when an {@link Player} enters the minigame (or waiting room).
     * 
     * @param player
     *        the player entering the minigame (or waiting room).
     */
    public abstract void fireOnEnter(Player player);

    /**
     * Fired when an {@link Player} logs in while in the minigame boundary.
     * 
     * @param player
     *        the player logging in.
     */
    public abstract void fireOnLogin(Player player);

    /**
     * Fired when an {@link Player} gets forcibly logged out.
     * 
     * @param player
     *        the player who got forcibly logged out.
     */
    public abstract void fireOnForcedLogout(Player player);

    /**
     * Determines if a {@link Player} is in the minigame.
     * 
     * @param player
     *        the player to determine for.
     * @return true if they are in the minigame.
     */
    public abstract boolean inMinigame(Player player);

    /**
     * The name of this minigame.
     * 
     * @return the name of this minigame.
     */
    public abstract String name();

    /**
     * Fired when a {@link Player} dies within the minigame.
     * 
     * @param player
     *        the player who died within the minigame.
     */
    public void fireOnDeath(Player player) {

    }

    /**
     * Fired when a {@link Entity} is killed within the minigame.
     * 
     * @param player
     *        the player who killed the entity.
     * @param other
     *        the entity that was killed.
     */
    public void fireOnKill(Player player, Entity other) {

    }

    /**
     * If a {@link Player} can equip items while in this minigame.
     * 
     * @param player
     *        the player trying to equip items.
     * @param item
     *        the item the player is trying to equip.
     * @param equipmentSlot
     *        the equipment slot the player is trying to equip to.
     * @return true by default.
     */
    public boolean canEquip(Player player, Item item, int equipmentSlot) {
        return true;
    }

    /**
     * If a {@link Player} can unequip items while in this minigame.
     * 
     * @param player
     *        the player trying to unequip items.
     * @param item
     *        the item the player is trying to unequip.
     * @param equipmentSlot
     *        the equipment slot the player is trying to unequip from.
     * @return true by default.
     */
    public boolean canUnequip(Player player, Item item, int equipmentSlot) {
        return true;
    }

    /**
     * If a {@link Player} can trade while in this minigame.
     * 
     * @param player
     *        the player offering to trade.
     * @param other
     *        the player being offered to trade.
     * @return false by default;
     */
    public boolean canTrade(Player player, Player other) {
        return false;
    }

    /**
     * If a {@link Player} can fight while in this minigame.
     * 
     * @param player
     *        the player trying to attack.
     * @param other
     *        the entity being attacked.
     * @return false by default.
     */
    public boolean canHit(Player player, Entity other) {
        return false;
    }

    /**
     * If a {@link Player} can logout formally (using the logout button) while
     * in this minigame.
     * 
     * @param player
     *        the player trying to logout.
     * @return false by default.
     */
    public boolean canFormalLogout(Player player) {
        return false;
    }

    /**
     * If a {@link Player} can teleport while in this minigame.
     * 
     * @param player
     *        the player trying to teleport.
     * @return false by default.
     */
    public boolean canTeleport(Player player) {
        return false;
    }

    /**
     * The position that a {@link Player} will be returned to once dead.
     * 
     * @param player
     *        the player we are returning to the position.
     * @return the death position by default.
     */
    public Position getDeathPosition(Player player) {
        return new Position(3093, 3244);
    }
}
