package server.world.entity.combat;

import server.core.worker.TaskFactory;
import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.range.RangedAmmo;
import server.world.entity.combat.strategy.DefaultMagicCombatStrategy;
import server.world.entity.combat.strategy.DefaultMeleeCombatStrategy;
import server.world.entity.combat.strategy.DefaultRangedCombatStrategy;
import server.world.entity.combat.task.CombatAccuracyHitTask;
import server.world.entity.combat.task.CombatHitTask;
import server.world.entity.combat.task.CombatPoisonTask;
import server.world.entity.combat.task.CombatSkullTask;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoison;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.FightStyle;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;

/**
 * A class containing static factory fields and methods used for combat.
 * 
 * @author lare96
 */
public class CombatFactory {

    /**
     * So this class cannot be instantiated.
     */
    private CombatFactory() {
    }

    /**
     * Poisons the designated entity.
     * 
     * @param entity
     *        the entity being poisoned.
     */
    public static void poisonEntity(Entity entity, CombatPoison poisonType) {
        if (entity.getPoisonHits() > 0) {
            return;
        }

        if (entity.isPlayer()) {
            ((Player) entity).getPacketBuilder().sendMessage("You have been " + poisonType.name().toLowerCase() + "ly poisoned!");
        }

        entity.setPoisonHits(poisonType.getHitAmount());
        entity.setPoisonStrength(poisonType);
        TaskFactory.getFactory().submit(new CombatPoisonTask(entity));
    }

    /**
     * Assigns a skull to a player.
     * 
     * @param player
     *        the player to assign a skull to.
     */
    public static void skullPlayer(final Player player) {
        if (player.getSkullTimer() > 0) {
            return;
        }

        player.setSkullTimer(3000);
        player.setSkullIcon(0);
        player.getFlags().flag(Flag.APPEARANCE);
        TaskFactory.getFactory().submit(new CombatSkullTask(player));
    }

    /**
     * Calculates the max melee hit.
     * 
     * @param entity
     *        the entity to calculate for.
     * @return the max melee hit.
     */
    public static int calculateMaxMeleeHit(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            double strengthLevel = player.getSkills()[Misc.STRENGTH].getLevel();

            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.BURST_OF_STRENGTH)) {
                strengthLevel *= 1.05;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.SUPERHUMAN_STRENGTH)) {
                strengthLevel *= 1.1;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.ULTIMATE_STRENGTH)) {
                strengthLevel *= 1.15;
            }

            int styleBonus = 0;

            if (player.getFightType().getStyle() == FightStyle.AGGRESSIVE) {
                styleBonus = 3;
            } else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
                styleBonus = 1;
            }

            int effectiveStrengthDamage = (int) (strengthLevel + styleBonus);
            double baseDamage = 5 + (effectiveStrengthDamage + 8) * (player.getPlayerBonus()[Misc.BONUS_STRENGTH] + 64) / 64;
            int maxHit = (int) Math.floor(baseDamage);
            maxHit = (int) Math.floor(maxHit / 10);

            if (CombatFactory.isWearingFullDharoks(player)) {
                maxHit += (player.getSkills()[Misc.HITPOINTS].getLevelForExperience() - player.getSkills()[Misc.HITPOINTS].getLevel()) / 10;
            }

            return maxHit;
        } else if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            int maxHit = npc.getDefinition().getMaxHit();

            if (npc.getStatsWeakened()[1]) {
                maxHit -= (int) ((0.10) * (maxHit));
            } else if (npc.getStatsBadlyWeakened()[1]) {
                maxHit -= (int) ((0.20) * (maxHit));
            }

            if (maxHit < 1) {
                maxHit = 1;
            }

            return maxHit;
        }
        return 0;
    }

    /**
     * Calculates the max range hit.
     * 
     * @param entity
     *        the entity to calculate for.
     * @param table
     *        the ammo being used.
     * @return the max range hit.
     */
    public static int calculateMaxRangeHit(Entity entity, RangedAmmo table) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            int rangedLevel = player.getSkills()[Misc.RANGED].getLevel();

            double styleBonus = 0;

            if (player.getFightType().getStyle() == FightStyle.ACCURATE)
                styleBonus = 3;
            else if (player.getFightType().getStyle() == FightStyle.AGGRESSIVE)
                styleBonus = 1;
            rangedLevel += styleBonus;

            int rangedStrength = table.getRangedStrength();
            double maxHit = (rangedLevel + rangedStrength / 8 + rangedLevel * rangedStrength * Math.pow(64, -1) + 14) / 10;
            return (int) Math.floor(maxHit);
        } else if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            return npc.getDefinition().getMaxHit();
        }
        return 0;
    }

    /**
     * Gets the melee hit for this turn.
     * 
     * @param entity
     *        the entity to get the hit for.
     * @return the melee hit.
     */
    public static Hit getMeleeHit(Entity entity) {
        if (entity.isPlayer()) {
            ((Player) entity).getPacketBuilder().sendMessage("Maximum hit possible this turn: " + CombatFactory.calculateMaxMeleeHit(entity));
        }

        return new Hit(Misc.getRandom().nextInt(CombatFactory.calculateMaxMeleeHit(entity)));
    }

    /**
     * Gets the range hit for this turn.
     * 
     * @param entity
     *        the entity to get the hit for.
     * @param table
     *        the ammo being used.
     * @return the range hit.
     */
    public static Hit getRangeHit(Entity entity, RangedAmmo table) {
        if (entity.isPlayer()) {
            ((Player) entity).getPacketBuilder().sendMessage("Maximum hit possible this turn: " + CombatFactory.calculateMaxRangeHit(entity, table));
        }

        return new Hit(Misc.getRandom().nextInt(CombatFactory.calculateMaxRangeHit(entity, table)));
    }

    /**
     * Calculates the effective accuracy level.
     * 
     * @param entity
     *        the entity to calculate for.
     * @return the effective accuracy level.
     */
    private static double getEffectiveAccuracy(Entity entity) {
        double attackBonus = 0;
        double baseAttack = 0;

        if (entity.isPlayer()) {
            Player player = (Player) entity;
            baseAttack = player.getSkills()[Misc.ATTACK].getLevel();
            attackBonus = player.getPlayerBonus()[player.getFightType().getBonusType()];

            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.CLARITY_OF_THOUGHT)) {
                baseAttack *= 1.05;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.IMPROVED_REFLEXES)) {
                baseAttack *= 1.1;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.INCREDIBLE_REFLEXES)) {
                baseAttack *= 1.15;
            }
        } else if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            baseAttack = npc.getDefinition().getAttackBonus();

            if (npc.getStatsWeakened()[0]) {
                baseAttack -= (int) ((0.10) * (baseAttack));
            } else if (npc.getStatsBadlyWeakened()[0]) {
                baseAttack -= (int) ((0.20) * (baseAttack));
            }

            if (baseAttack < 1) {
                baseAttack = 1;
            }
        }

        return Math.floor(baseAttack + attackBonus) + 8;
    }

    /**
     * Calculates the effective defence level.
     * 
     * @param victim
     *        the victim to calculate for.
     * @param type
     *        the type of combat being used.
     * @return the effective defence.
     */
    private static double getEffectiveDefence(Entity victim, CombatType type) {
        double baseDefence = 0;

        if (victim.isPlayer()) {
            Player player = (Player) victim;

            switch (type) {
                case RANGE:
                case MELEE:
                    baseDefence = player.getSkills()[Misc.DEFENCE].getLevel();

                    if (CombatPrayer.isPrayerActivated(player, CombatPrayer.THICK_SKIN)) {
                        baseDefence *= 1.05;
                    } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.ROCK_SKIN)) {
                        baseDefence *= 1.1;
                    } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.STEEL_SKIN)) {
                        baseDefence *= 1.15;
                    }
                    break;
                case MAGIC:
                    baseDefence = player.getSkills()[Misc.DEFENCE].getLevel();
                    break;
            }
        } else if (victim.isNpc()) {
            Npc npc = (Npc) victim;
            switch (type) {
                case RANGE:
                    baseDefence = npc.getDefinition().getDefenceRange();
                    break;
                case MELEE:
                    baseDefence = npc.getDefinition().getDefenceMelee();
                    break;
                case MAGIC:
                    baseDefence = npc.getDefinition().getDefenceMage();
                    break;
            }

            if (npc.getStatsWeakened()[2]) {
                baseDefence -= (int) ((0.10) * (baseDefence));
            } else if (npc.getStatsBadlyWeakened()[2]) {
                baseDefence -= (int) ((0.20) * (baseDefence));
            }

            if (baseDefence < 0) {
                baseDefence = 0;
            }
        }
        return Math.floor(baseDefence) + 8;
    }

    /**
     * Calculates the attack roll for the attacker.
     * 
     * @param attacker
     *        the entity to calculate for.
     * @return the attack roll.
     */
    private static double getAttackRoll(Entity attacker) {

        double specAccuracy = 1;
        double effectiveAccuracy = getEffectiveAccuracy(attacker);

        int styleBonusAttack = 0;

        if (attacker.isPlayer()) {
            Player player = (Player) attacker;

            if (player.getFightType().getStyle() == FightStyle.ACCURATE) {
                styleBonusAttack = 3;
            } else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
                styleBonusAttack = 1;
            }
        }

        effectiveAccuracy *= (1 + (styleBonusAttack) / 64);
        return (int) (effectiveAccuracy * specAccuracy);
    }

    /**
     * Calculates the defence roll for the victim.
     * 
     * @param victim
     *        the victim to calculate for.
     * @return the defence roll.
     */
    private static double getDefenceRoll(Entity victim, CombatType type) {
        double effectiveDefence = getEffectiveDefence(victim, type);
        int styleBonusDefence = 0;

        if (victim.isPlayer()) {
            Player player = (Player) victim;

            if (type == CombatType.MAGIC) {
                effectiveDefence += player.getPlayerBonus()[Misc.DEFENCE_MAGIC];
            } else if (type == CombatType.RANGE) {
                effectiveDefence += player.getPlayerBonus()[Misc.DEFENCE_RANGE];
            } else {
                effectiveDefence += ((player.getPlayerBonus()[Misc.DEFENCE_CRUSH] + player.getPlayerBonus()[Misc.DEFENCE_SLASH] + player.getPlayerBonus()[Misc.DEFENCE_STAB]) / 3);
            }

            if (type == CombatType.MAGIC) {
                int level = player.getSkills()[Misc.MAGIC].getLevel();
                effectiveDefence = (int) (Math.floor(level * 0.7) + Math.floor(effectiveDefence * 0.3));
            } else {
                if (player.getFightType().getStyle() == FightStyle.DEFENSIVE) {
                    styleBonusDefence += 3;
                } else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
                    styleBonusDefence += 1;
                }
            }
        }
        effectiveDefence *= (1 + (styleBonusDefence) / 64);

        if (victim.isPlayer()) {
            Player player = (Player) victim;

            if (CombatFactory.isWearingFullVeracs(player)) {
                effectiveDefence *= 0.75;
            }
        } else if (victim.isNpc()) {
            Npc npc = (Npc) victim;

            if (npc.getNpcId() == 2030) {
                effectiveDefence *= 0.75;
            }
        }
        return effectiveDefence;
    }

    /**
     * Gets the chance of attacking.
     * 
     * @param attack
     *        the attack that will be calculated.
     * @param defence
     *        the defence that will be calculated.
     * @return the chance of attacking.
     */
    private static double getChance(double attack, double defence) {
        double A = Math.floor(attack);
        double D = Math.floor(defence);
        double chance = A < D ? (A - 1.0) / (2.0 * D) : 1.0 - (D + 1.0) / (2.0 * A);
        chance = chance > 0.9999 ? 0.9999 : chance < 0.0001 ? 0.0001 : chance;
        return chance;
    }

    /**
     * Determines if the player will hit or not and any miscellaneous weapon and
     * armor effects are activated here.
     * 
     * @param attacker
     *        the entity attacking.
     * @param victim
     *        the entity being attacked.
     * @param type
     *        the type of combat being used.
     * @param hitCount
     *        the amount of hits being dealt.
     * @return if the hit was successful.
     */
    public static boolean hitAccuracy(Entity attacker, Entity victim, CombatType type, int hitCount) {
        if (hitCount > 4) {
            throw new IllegalArgumentException("Illegal number of hits! Maximum: 4");
        }

        double defence = CombatFactory.getDefenceRoll(victim, type);
        double accuracy = CombatFactory.getAttackRoll(attacker);
        double chance = CombatFactory.getChance(accuracy, defence);
        boolean accurate = CombatFactory.isAccurateHit(chance);

        if (attacker.isPlayer()) {
            ((Player) attacker).getPacketBuilder().sendMessage("Chance to hit: " + (int) (chance * 100) + "%");
        }
        if (victim.isPlayer() && attacker.isNpc()) {
            ((Player) victim).getPacketBuilder().sendMessage("Chance of npc hitting you: " + (int) (chance * 100) + "%");
        }

        if (type == CombatType.MAGIC) {
            if (attacker.getCurrentlyCasting().maximumStrength() == -1) {
                if (!accurate) {
                    TaskFactory.getFactory().submit(new CombatAccuracyHitTask(attacker, victim, type, hitCount, 3, false));
                } else {
                    TaskFactory.getFactory().submit(new CombatHitTask(attacker, victim, null, type, 0, 3, false));
                }
                return accurate;
            }
        }

        if (!accurate) {
            switch (type) {
                case MELEE:
                    TaskFactory.getFactory().submit(new CombatAccuracyHitTask(attacker, victim, type, hitCount, 1, true));
                    break;
                case RANGE:
                    TaskFactory.getFactory().submit(new CombatAccuracyHitTask(attacker, victim, type, hitCount, 2, false));
                    break;
                case MAGIC:
                    TaskFactory.getFactory().submit(new CombatAccuracyHitTask(attacker, victim, type, hitCount, 3, false));
                    break;
            }
        }
        return accurate;
    }

    /**
     * Determines the combat strategy for players.
     * 
     * @param player
     *        the player to determine the combat strategy for.
     */
    public static void determinePlayerStrategy(Player player) {
        if (player.getCastSpell() != null) {
            player.getCombatBuilder().setCurrentStrategy(CombatFactory.newDefaultMagicStrategy());
        } else if (player.getWeapon() == WeaponInterface.SHORTBOW || player.getWeapon() == WeaponInterface.LONGBOW || player.getWeapon() == WeaponInterface.CROSSBOW || player.getWeapon() == WeaponInterface.DART || player.getWeapon() == WeaponInterface.JAVELIN || player.getWeapon() == WeaponInterface.THROWNAXE || player.getWeapon() == WeaponInterface.KNIFE) {
            player.getCombatBuilder().setCurrentStrategy(CombatFactory.newDefaultRangedStrategy());
        } else {
            player.getCombatBuilder().setCurrentStrategy(CombatFactory.newDefaultMeleeStrategy());
        }
    }

    /**
     * Determines the combat strategy for npcs.
     * 
     * @param npc
     *        the npc to determine the combat strategy for.
     */
    protected static void determineNpcStrategy(Npc npc) {
        npc.getCombatBuilder().setCurrentStrategy(CombatFactory.newDefaultMeleeStrategy());
    }

    /**
     * Determines if the player is wearing full veracs.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full veracs.
     */
    public static boolean isWearingFullVeracs(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4753 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4757 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4759 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4755;
    }

    /**
     * Determines if the player is wearing full dharoks.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full dharoks.
     */
    private static boolean isWearingFullDharoks(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4716 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4720 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4722 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4718;
    }

    /**
     * Determines if the player is wearing full karils.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full karils.
     */
    public static boolean isWearingFullKarils(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4732 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4736 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4738 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4734;
    }

    /**
     * Determines if the player is wearing full ahrims.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full ahrims.
     */
    public static boolean isWearingFullAhrims(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4708 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4712 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4714 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4710;
    }

    /**
     * Determines if the player is wearing full torags.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full torags.
     */
    public static boolean isWearingFullTorags(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4745 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4749 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4751 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4747;
    }

    /**
     * Determines if the player is wearing full guthans.
     * 
     * @param player
     *        the player to determine for.
     * @return true if the player is wearing full guthans.
     */
    public static boolean isWearingFullGuthans(Player player) {
        if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS) == null || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON) == null) {
            return false;
        }

        return player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_HEAD).getId() == 4724 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_CHEST).getId() == 4728 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_LEGS).getId() == 4730 && player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId() == 4726;
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

    /**
     * Determines if the given chance is a successful hit.
     * 
     * @param chance
     *        the chance to determine.
     * @return true if the hit was successful.
     */
    private static boolean isAccurateHit(double chance) {
        return Misc.getRandom().nextDouble() <= chance;
    }

    /**
     * Constructs the default melee combat strategy.
     * 
     * @return the default melee combat strategy.
     */
    public static CombatStrategy newDefaultMeleeStrategy() {
        return new DefaultMeleeCombatStrategy();
    }

    /**
     * Constructs the default magic combat strategy.
     * 
     * @return the default magic combat strategy.
     */
    public static CombatStrategy newDefaultMagicStrategy() {
        return new DefaultMagicCombatStrategy();
    }

    /**
     * Constructs the default ranged combat strategy.
     * 
     * @return the default ranged combat strategy.
     */
    public static CombatStrategy newDefaultRangedStrategy() {
        return new DefaultRangedCombatStrategy();
    }
}
