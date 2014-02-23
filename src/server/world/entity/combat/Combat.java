package server.world.entity.combat;

import server.core.Rs2Engine;
import server.core.worker.listener.EventListener;
import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;

/**
 * Contains static utility methods and fields that manage combat for entities.
 * 
 * @author lare96
 */
public class Combat {

    // TODO: poisoning and retreating

    /**
     * Constants representing the different types of combat.
     * 
     * @author lare96
     */
    public enum CombatType {
        MELEE, RANGE, MAGIC
    }

    /**
     * Checks if one entity is able to fight another entity.
     * 
     * @param attacker
     *        the attacker who wants to fight.
     * @param victim
     *        the victim who is being attacked.
     * @return true if the attacker is allowed to fight the victim.
     */
    public static boolean check(Entity attacker, Entity victim) {

        /** Check if either of the participants have died. */
        if (attacker.isHasDied() || victim.isHasDied()) {
            attacker.getCombatSession().resetCombat(victim);
            return false;

            /** Check if the participants are within distance of each other. */
        } else if (!attacker.getPosition().withinDistance(victim.getPosition(), 25)) {
            attacker.getCombatSession().resetCombat(victim);
            return false;

            /** Otherwise the attacker is allowed to attack. */
        } else {
            return true;
        }
    }

    /**
     * Determines what combat type this entity is using.
     * 
     * @param attacker
     *        the entity who's combat type we have to determine.
     */
    public static void combatTypeCheck(Entity attacker) {

        /** Determine the combat type we are using. */
        if (attacker instanceof Player) {
            Player player = (Player) attacker;
            attacker.setType(player.isAutocastMagic() || player.isUsingMagic() ? CombatType.MAGIC : player.getWeapon().getCombatType());
        } else if (attacker instanceof Npc) {
            Npc mob = (Npc) attacker;
            mob.setType(CombatType.MELEE);
            // TODO: Combat strategies for npcs. We don't need this for players
            // because players 'stratagize' their own combat.
            // TODO: To be honest we don't even need this until combat is fully
            // completed. This will be done last.
        }
    }

    /**
     * Prepares the entity for combat based on its current combat type.
     * 
     * @param attacker
     */
    public static void combatTypePrepare(Entity attacker) {

        /** Check for each combat type. */
        switch (attacker.getType()) {
            case MELEE:
                // TODO: Check for any other miscellaneous
                // things.
                // like fight types
                break;
            case RANGE:
                // TODO: Check for correct arrows and stuff as
                // well
                // as
                // any
                // other miscellaneous things.
                // like fight types
                break;
            case MAGIC:
                // TODO: Check for correct runes and stuff as
                // well
                // as
                // any
                // other miscellaneous things.
                // like fight types
                break;

        }
    }

    /**
     * Starts combat between two entities.
     * 
     * @param attacker
     *        the 'attacker' in this confrontation.
     * @param victim
     *        the 'victim' in this confrontation.
     */
    public static void fight(final Entity attacker, final Entity victim) {

        /** Obviously check if we can fight before continuing. */
        if (!check(attacker, victim)) {
            attacker.getCombatSession().resetCombat(victim);
            return;
        }

        // TODO: start following the entity here

        /**
         * A new listener that will start combat for us when/if certain
         * conditions are met.
         */
        Rs2Engine.getWorld().submit(new EventListener() {
            @Override
            public boolean listenForEvent() {

                /**
                 * Constantly check for the combat type in case the entity
                 * switches weapons while walking to attack the other entity.
                 */
                combatTypeCheck(attacker);

                /**
                 * Fire the logic below if we are unregistered, finished moving,
                 * or within the required distance to attack.
                 */
                return !(attacker.isUnregistered() || attacker.getMovementQueue().isMovementDone() || attacker.getPosition().withinDistance(victim.getPosition(), getDistanceRequired(attacker, attacker.getType())));
            }

            @Override
            public void run() {

                /** Make sure we are in the required distance. */
                if (attacker.getPosition().withinDistance(victim.getPosition(), getDistanceRequired(attacker, attacker.getType()))) {

                    /** Start the combat session. */
                    attacker.getCombatSession().attackEntity(victim);
                }
            }
        });
    }

    public static int getDistanceRequired(Entity attacker, CombatType type) {
        switch (type) {
            case MELEE:
                // TODO: halberds
                return 1;
            case RANGE:
                // TODO: darts, thrownaxe, knives, javelin, crossbow, shortbow,
                // longbow, crystal bow
                return 6;
            case MAGIC:
                return 8;
            default:
                return 1;
        }
    }

    // TODO: formulas... should start on prayer too
    public static Hit calculateHit(Entity entity, CombatType type) {
        if (entity instanceof Player) {
            switch (type) {
                case MELEE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case RANGE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case MAGIC:
                    return new Hit(Misc.getRandom().nextInt(5));
            }
        } else if (entity instanceof Npc) {
            switch (type) {
                case MELEE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case RANGE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case MAGIC:
                    return new Hit(Misc.getRandom().nextInt(5));
            }
        }
        return new Hit(0);
    }

    /**
     * Calculates the combat level difference for wilderness player vs. player
     * combat.
     * 
     * @param combatLevel
     *        the combat level of the first person.
     * @param otherCombatLevel
     *        the combat level of the other person.
     * @return the combat level difference.
     */
    public static int calculateCombatDifference(int combatLevel, int otherCombatLevel) {
        if (combatLevel > otherCombatLevel) {
            return (combatLevel - otherCombatLevel);
        } else if (otherCombatLevel > combatLevel) {
            return (otherCombatLevel - combatLevel);
        } else {
            return 0;
        }
    }
}
