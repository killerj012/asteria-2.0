package server.world.entity.combat.special;

import server.world.entity.Entity;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatType;
import server.world.entity.player.Player;

public interface CombatSpecialStrategy {

    public void onActivation(Player player, Entity target);

    public CombatHit calculateHit(Player player, Entity target);

    public void onHit(Player player, Entity target);

    public CombatType combatType();

}
