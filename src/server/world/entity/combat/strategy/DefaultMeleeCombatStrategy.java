package server.world.entity.combat.strategy;

import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;

public class DefaultMeleeCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        return true;
    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            npc.animation(new Animation(npc.getDefinition().getAttackAnimation()));
        } else if (entity.isPlayer()) {
            Player player = (Player) entity;
            player.animation(new Animation(player.getFightType().getAnimation()));
        }

        if (CombatFactory.hitAccuracy(entity, victim, CombatType.MELEE, 1)) {
            return new CombatHit(new Hit[] { CombatFactory.getMeleeHit(entity) }, CombatType.MELEE);
        }
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {
        return entity.getAttackSpeed();
    }

    @Override
    public int getDistance(Entity entity) {
        if (entity.isNpc()) {
            return 1;
        }

        int distance = 1;
        Player player = (Player) entity;

        if (player.getWeapon() == WeaponInterface.HALBERD) {
            distance++;
        }
        return distance;
    }
}
