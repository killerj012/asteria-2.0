package server.world.entity.combat.special;

import server.world.entity.Entity;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatType;
import server.world.entity.player.Player;

/**
 * A dynamic table used to determine how a player will act when using a special
 * attack.
 * 
 * @author lare96
 */
public interface CombatSpecialStrategy {

    /**
     * Fired when the player activates the special attack bar.
     * 
     * @param player
     *        the player activating the special attack bar.
     * @param target
     *        the player's current target.
     */
    public void onActivation(Player player, Entity target);

    /**
     * Fired on the player's combat hook.
     * 
     * @param player
     *        the player currently fighting.
     * @param target
     *        the player's current target.
     * @return the combat hit for this combat hook.
     */
    public CombatHitContainer calculateHit(Player player, Entity target);

    /**
     * Determines what type of combat this hook will be.
     * 
     * @return the type of combat that the special attack is.
     */
    public CombatType combatType();
}
