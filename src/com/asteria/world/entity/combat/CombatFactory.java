package com.asteria.world.entity.combat;

import com.asteria.Main;
import com.asteria.engine.task.TaskManager;
import com.asteria.util.Utility;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.MovementQueue;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect.PoisonType;
import com.asteria.world.entity.combat.effect.CombatSkullEffect;
import com.asteria.world.entity.combat.prayer.CombatPrayer;
import com.asteria.world.entity.combat.strategy.DefaultMagicCombatStrategy;
import com.asteria.world.entity.combat.strategy.DefaultMeleeCombatStrategy;
import com.asteria.world.entity.combat.strategy.DefaultRangedCombatStrategy;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.AssignWeaponInterface.FightStyle;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;
import com.asteria.world.map.Location;
import com.asteria.world.map.Position;

/**
 * A static factory class containing all miscellaneous methods related to, and
 * used for combat.
 * 
 * @author lare96
 * @author Scu11
 * @author Graham
 */
public final class CombatFactory {

    // TODO: KBD and dragon combat strategies, as an example of how to make
    // bosses.
    // TODO: When KBD is done, check if immunity is above 0. If not then deal
    // fire damage.

    /** The amount of time it takes for cached damage to timeout. */
    // Damage cached for currently 60 seconds will not be accounted for.
    public static final long DAMAGE_CACHE_TIMEOUT = 60000;

    /** The amount of damage that will be drained by combat protection prayer. */
    // Currently at .20 meaning 20% of damage drained when using the right
    // protection prayer.
    public static final double PRAYER_DAMAGE_REDUCTION = .20;

    /** The rate at which accuracy will be reduced by combat protection prayer. */
    // Currently at .255 meaning 25.5% percent chance of canceling damage when
    // using the right protection prayer.
    public static final double PRAYER_ACCURACY_REDUCTION = .255;

    /** The amount of hitpoints the redemption prayer will heal. */
    // Currently at .25 meaning hitpoints will be healed by 25% of the remaining
    // prayer points when using redemption.
    public static final double REDEMPTION_PRAYER_HEAL = .25;

    /** The maximum amount of damage inflicted by retribution. */
    // Damage between currently 0-10 will be inflicted if in the specified
    // radius when the retribution prayer effect is activated.
    public static final int MAXIMUM_RETRIBUTION_DAMAGE = 10;

    /** The radius that retribution will hit players in. */
    // All players within currently 3 squares will get hit by the retribution
    // effect.
    public static final int RETRIBUTION_RADIUS = 3;

    /**
     * A set of constants representing the three different types of combat that
     * can be used.
     * 
     * @author lare96
     */
    public enum CombatType {
        MELEE,
        RANGED,
        MAGIC
    }

    /**
     * Determines if the entity is wearing full veracs.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full veracs.
     */
    public static boolean fullVeracs(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Verac the Defiled") : ((Player) entity)
            .getEquipment().containsAll(4753, 4757, 4759, 4755);
    }

    /**
     * Determines if the entity is wearing full dharoks.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full dharoks.
     */
    public static boolean fullDharoks(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Dharok the Wretched") : ((Player) entity)
            .getEquipment().containsAll(4716, 4720, 4722, 4718);
    }

    /**
     * Determines if the entity is wearing full karils.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full karils.
     */
    public static boolean fullKarils(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Karil the Tainted") : ((Player) entity)
            .getEquipment().containsAll(4732, 4736, 4738, 4734);
    }

    /**
     * Determines if the entity is wearing full ahrims.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full ahrims.
     */
    public static boolean fullAhrims(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Ahrim the Blighted") : ((Player) entity)
            .getEquipment().containsAll(4708, 4712, 4714, 4710);
    }

    /**
     * Determines if the entity is wearing full torags.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full torags.
     */
    public static boolean fullTorags(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Torag the Corrupted") : ((Player) entity)
            .getEquipment().containsAll(4745, 4749, 4751, 4747);
    }

    /**
     * Determines if the entity is wearing full guthans.
     * 
     * @param entity
     *            the entity to determine this for.
     * @return true if the player is wearing full guthans.
     */
    public static boolean fullGuthans(Entity entity) {
        return entity.type() == EntityType.NPC ? ((Npc) entity).getDefinition()
            .getName().equals("Guthan the Infested") : ((Player) entity)
            .getEquipment().containsAll(4724, 4728, 4730, 4726);
    }

    /**
     * Determines if the player is wielding a crystal bow.
     * 
     * @param player
     *            the player to determine for.
     * @return true if the player is wielding a crystal bow.
     */
    public static boolean crystalBow(Player player) {
        Item item;
        if ((item = player.getEquipment().get(Utility.EQUIPMENT_SLOT_WEAPON)) == null) {
            return false;
        }

        return item.getDefinition().getItemName().toLowerCase().contains(
            "crystal bow");
    }

    /**
     * Determines if the player has arrows equipped.
     * 
     * @param player
     *            the player to determine for.
     * @return true if the player has arrows equipped.
     */
    public static boolean arrowsEquipped(Player player) {
        Item item;
        if ((item = player.getEquipment().get(Utility.EQUIPMENT_SLOT_ARROWS)) == null) {
            return false;
        }

        return !(!item.getDefinition().getItemName().endsWith("arrow") && !item
            .getDefinition().getItemName().endsWith("arrow(p)") && !item
            .getDefinition().getItemName().endsWith("arrow(p+)") && !item
            .getDefinition().getItemName().endsWith("arrow(p++)"));
    }

    /**
     * Determines if the player has bolts equipped.
     * 
     * @param player
     *            the player to determine for.
     * @return true if the player has bolts equipped.
     */
    public static boolean boltsEquipped(Player player) {
        Item item;
        if ((item = player.getEquipment().get(Utility.EQUIPMENT_SLOT_ARROWS)) == null) {
            return false;
        }

        return !(!item.getDefinition().getItemName().endsWith("bolts") && !item
            .getDefinition().getItemName().endsWith("bolts(p)") && !item
            .getDefinition().getItemName().endsWith("bolts(p+)") && !item
            .getDefinition().getItemName().endsWith("bolts(p++)"));
    }

    /**
     * Attempts to poison the argued {@link Entity} with the argued
     * {@link PoisonType}. This method will have no effect if the entity is
     * already poisoned.
     * 
     * @param entity
     *            the entity that will be poisoned, if not already.
     * @param poisonType
     *            the poison type that this entity is being inflicted with.
     */
    public static void poisonEntity(Entity entity, PoisonType poisonType) {

        // We are already poisoned or the poison type is invalid, do nothing.
        if (entity.isPoisoned() || poisonType == null) {
            return;
        }

        // If the entity is a player, we check for poison immunity. If they have
        // no immunity then we send them a message telling them that they are
        // poisoned.
        if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            if (player.getPoisonImmunity() > 0)
                return;
            player.getPacketBuilder().sendMessage("You have been poisoned!");
        }

        // Poison the entity as normal.
        entity.setPoisonDamage(poisonType.getDamage());
        TaskManager.submit(new CombatPoisonEffect(entity));
    }

    /**
     * Attempts to put the skull icon on the argued player, including the effect
     * where the player loses all item upon death. This method will have no
     * effect if the argued player is already skulled.
     * 
     * @param player
     *            the player to attempt to skull to.
     */
    public static void skullPlayer(Player player) {

        // We are already skulled, return.
        if (player.getSkullTimer() > 0) {
            return;
        }

        // Otherwise skull the player as normal.
        player.setSkullTimer(3000);
        player.setSkullIcon(0);
        player.getFlags().flag(Flag.APPEARANCE);
        TaskManager.submit(new CombatSkullEffect(player));
    }

    /**
     * Calculates the combat level difference for wilderness player vs. player
     * combat.
     * 
     * @param combatLevel
     *            the combat level of the first person.
     * @param otherCombatLevel
     *            the combat level of the other person.
     * @return the combat level difference.
     */
    public static int combatLevelDifference(int combatLevel,
        int otherCombatLevel) {
        if (combatLevel > otherCombatLevel) {
            return (combatLevel - otherCombatLevel);
        } else if (otherCombatLevel > combatLevel) {
            return (otherCombatLevel - combatLevel);
        } else {
            return 0;
        }
    }

    /**
     * Generates a random {@link Hit} based on the argued entity's stats.
     * 
     * @param entity
     *            the entity to generate the random hit for.
     * @param victim
     *            the victim being attacked.
     * @param type
     *            the combat type being used.
     * @return the melee hit.
     */
    public static Hit getHit(Entity entity, Entity victim, CombatType type) {
        switch (type) {
        case MELEE:
            return new Hit(Utility.inclusiveRandom(1, CombatFactory
                .calculateMaxMeleeHit(entity, victim)));
        case RANGED:
            return new Hit(Utility.inclusiveRandom(1, CombatFactory
                .calculateMaxRangedHit(entity, victim)));
        case MAGIC:
            if (Main.DEBUG && entity.type() == EntityType.PLAYER)
                ((Player) entity).getPacketBuilder().sendMessage(
                    "[DEBUG]: Maximum hit this turn is [" + entity
                        .getCurrentlyCasting().maximumHit() + "].");
            return new Hit(Utility.inclusiveRandom(0, entity
                .getCurrentlyCasting().maximumHit()));
        default:
            throw new IllegalArgumentException("Invalid combat type: " + type);
        }
    }

    /**
     * A flag that determines if the entity's attack will be successful based on
     * the argued attacker's and victim's stats.
     * 
     * @param attacker
     *            the attacker who's hit is being calculated for accuracy.
     * @param victim
     *            the victim who's awaiting to either be hit or dealt no damage.
     * @param type
     *            the type of combat being used to deal the hit.
     * @return true if the hit was successful, or in other words accurate.
     */
    public static boolean rollAccuracy(Entity attacker, Entity victim,
        CombatType type) {
        boolean veracEffect = false;

        if (type == CombatType.MELEE) {
            if (CombatFactory.fullVeracs(attacker)) {
                if (Utility.RANDOM.nextInt(8) == 3) {
                    veracEffect = true;
                }
            }
        }

        double prayerMod = 1;
        double equipmentBonus = 1;
        double specialBonus = 1;
        int styleBonus = 0;
        int bonusType = -1;
        if (attacker.type() == EntityType.PLAYER) {
            Player player = (Player) attacker;

            equipmentBonus = type == CombatType.MAGIC ? player.getBonus()[Utility.ATTACK_MAGIC]
                : player.getBonus()[player.getFightType().getBonusType()];
            bonusType = player.getFightType().getCorrespondingBonus();
            if (type == CombatType.MELEE) {
                if (CombatPrayer.isActivated(player,
                    CombatPrayer.CLARITY_OF_THOUGHT)) {
                    prayerMod = 1.05;
                } else if (CombatPrayer.isActivated(player,
                    CombatPrayer.IMPROVED_REFLEXES)) {
                    prayerMod = 1.10;
                } else if (CombatPrayer.isActivated(player,
                    CombatPrayer.INCREDIBLE_REFLEXES)) {
                    prayerMod = 1.15;
                }
            } else if (type == CombatType.RANGED) {
                // XXX: Ranged prayers here.

            } else if (type == CombatType.MAGIC) {
                // XXX: Magic prayers here.
            }

            if (player.getFightType().getStyle() == FightStyle.ACCURATE) {
                styleBonus = 3;
            } else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
                styleBonus = 1;
            }

            if (player.isSpecialActivated()) {
                specialBonus = player.getCombatSpecial().getAccuracyBonus();
            }
        }

        double attackCalc = Math.floor(equipmentBonus + attacker
            .getBaseAttack(type)) + 8;
        attackCalc *= prayerMod;
        attackCalc += styleBonus;

        if (equipmentBonus < -67) {
            attackCalc = Utility.exclusiveRandom(8) == 0 ? attackCalc : 0;
        }
        attackCalc *= specialBonus;

        equipmentBonus = 1;
        prayerMod = 1;
        styleBonus = 0;
        if (victim.type() == EntityType.PLAYER) {
            Player player = (Player) victim;

            if (bonusType == -1) {
                equipmentBonus = type == CombatType.MAGIC ? player.getBonus()[Utility.DEFENCE_MAGIC]
                    : player.getSkills()[Skills.DEFENCE].getLevel();
            } else {
                equipmentBonus = type == CombatType.MAGIC ? player.getBonus()[Utility.DEFENCE_MAGIC]
                    : player.getBonus()[bonusType];
            }

            if (CombatPrayer.isActivated(player, CombatPrayer.THICK_SKIN)) {
                prayerMod = 1.05;
            } else if (CombatPrayer.isActivated(player, CombatPrayer.ROCK_SKIN)) {
                prayerMod = 1.10;
            } else if (CombatPrayer
                .isActivated(player, CombatPrayer.STEEL_SKIN)) {
                prayerMod = 1.15;
            }

            if (player.getFightType().getStyle() == FightStyle.DEFENSIVE) {
                styleBonus = 3;
            } else if (player.getFightType().getStyle() == FightStyle.CONTROLLED) {
                styleBonus = 1;
            }
        }

        double defenceCalc = Math.floor(equipmentBonus + victim
            .getBaseDefence(type)) + 8;
        defenceCalc *= prayerMod;
        defenceCalc += styleBonus;

        if (equipmentBonus < -67) {
            defenceCalc = Utility.exclusiveRandom(8) == 0 ? defenceCalc : 0;
        }
        if (veracEffect) {
            defenceCalc = 0;
        }
        double A = Math.floor(attackCalc);
        double D = Math.floor(defenceCalc);
        double hitSucceed = A < D ? (A - 1.0) / (2.0 * D)
            : 1.0 - (D + 1.0) / (2.0 * A);
        hitSucceed = hitSucceed >= 1.0 ? 0.99 : hitSucceed <= 0.0 ? 0.01
            : hitSucceed;

        if (attacker.type() == EntityType.PLAYER && Main.DEBUG) {
            ((Player) attacker)
                .getPacketBuilder()
                .sendMessage(
                    "[DEBUG] Your roll [" + (Math.round(attackCalc * 1000.0) / 1000.0) + "] : Victim's roll [" + (Math
                        .round(defenceCalc * 1000.0) / 1000.0) + "] : Chance to hit [" + (100 * Math
                        .round(hitSucceed * 1000.0) / 1000.0) + "%]");
        }
        return hitSucceed >= Utility.RANDOM.nextDouble();
    }

    /**
     * Calculates the maximum melee hit for the argued {@link Entity} without
     * taking the victim into consideration.
     * 
     * @param entity
     *            the entity to calculate the maximum hit for.
     * @param victim
     *            the victim being attacked.
     * @return the maximum melee hit that this entity can deal.
     */
    @SuppressWarnings("incomplete-switch")
    protected static int calculateMaxMeleeHit(Entity entity, Entity victim) {
        int maxHit = 0;

        if (entity.type() == EntityType.NPC) {
            Npc npc = (Npc) entity;
            maxHit = npc.getDefinition().getMaxHit();
            if (npc.getStrengthWeakened()[0]) {
                maxHit -= (int) ((0.10) * (maxHit));
            } else if (npc.getStrengthWeakened()[1]) {
                maxHit -= (int) ((0.20) * (maxHit));
            } else if (npc.getStrengthWeakened()[2]) {
                maxHit -= (int) ((0.30) * (maxHit));
            }
            return maxHit;
        }

        Player player = (Player) entity;
        double specialMultiplier = 1;
        double prayerMultiplier = 1;
        // TODO: void melee = 1.2, slayer helm = 1.15, salve amulet = 1.15,
        // salve amulet(e) = 1.2
        double otherBonusMultiplier = 1;
        int strengthLevel = player.getSkills()[Skills.STRENGTH].getLevel();
        int attackLevel = player.getSkills()[Skills.ATTACK].getLevel();
        int combatStyleBonus = 0;

        if (CombatPrayer.isActivated(player, CombatPrayer.BURST_OF_STRENGTH)) {
            prayerMultiplier = 1.05;
        } else if (CombatPrayer.isActivated(player,
            CombatPrayer.SUPERHUMAN_STRENGTH)) {
            prayerMultiplier = 1.1;
        } else if (CombatPrayer.isActivated(player,
            CombatPrayer.ULTIMATE_STRENGTH)) {
            prayerMultiplier = 1.15;
        }
        // else if
        // (CombatPrayer.isPrayerActivated(player,CombatPrayer.CHIVALRY)) {
        // prayerMultiplier = 1.18;
        // } else if
        // (CombatPrayer.isPrayerActivated(player,CombatPrayer.PIETY)) {
        // prayerMultiplier = 1.23;
        // }

        switch (player.getFightType().getStyle()) {
        case AGGRESSIVE:
            combatStyleBonus = 3;
            break;
        case CONTROLLED:
            combatStyleBonus = 1;
            break;
        }

        // if (CombatFactory.fullVoid(player)) {
        // otherBonusMultiplier = 1.1;
        // }

        if (strengthLevel <= 10 || attackLevel <= 10) {
            otherBonusMultiplier = 1.8;
        }

        int effectiveStrengthDamage = (int) ((strengthLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
        double baseDamage = 1.3 + (effectiveStrengthDamage / 10) + (player
            .getBonus()[Utility.BONUS_STRENGTH] / 80) + ((effectiveStrengthDamage * player
            .getBonus()[Utility.BONUS_STRENGTH]) / 640);

        if (player.isSpecialActivated()) {
            specialMultiplier = player.getCombatSpecial().getStrengthBonus();
        }

        maxHit = (int) (baseDamage * specialMultiplier);

        if (CombatFactory.fullDharoks(player)) {
            maxHit += (player.getSkills()[Skills.HITPOINTS]
                .getLevelForExperience() - player.getSkills()[Skills.HITPOINTS]
                .getLevel()) * 0.35;
        }

        if (victim.type() == EntityType.NPC) {
            Npc npc = (Npc) victim;
            if (npc.getDefenceWeakened()[0]) {
                maxHit += (int) ((0.10) * (maxHit));
            } else if (npc.getDefenceWeakened()[1]) {
                maxHit += (int) ((0.20) * (maxHit));
            } else if (npc.getDefenceWeakened()[2]) {
                maxHit += (int) ((0.30) * (maxHit));
            }
        }
        if (Main.DEBUG)
            player.getPacketBuilder().sendMessage(
                "[DEBUG]: Maximum hit this turn is [" + maxHit + "].");
        return maxHit;

    }

    /**
     * Calculates the maximum ranged hit for the argued {@link Entity} without
     * taking the victim into consideration.
     * 
     * @param entity
     *            the entity to calculate the maximum hit for.
     * @param victim
     *            the victim being attacked.
     * @return the maximum ranged hit that this entity can deal.
     */
    @SuppressWarnings("incomplete-switch")
    protected static int calculateMaxRangedHit(Entity entity, Entity victim) {
        int maxHit = 0;
        if (entity.type() == EntityType.NPC) {
            Npc npc = (Npc) entity;
            maxHit = npc.getDefinition().getMaxHit();
            if (npc.getStrengthWeakened()[0]) {
                maxHit -= (int) ((0.10) * (maxHit));
            } else if (npc.getStrengthWeakened()[1]) {
                maxHit -= (int) ((0.20) * (maxHit));
            } else if (npc.getStrengthWeakened()[2]) {
                maxHit -= (int) ((0.30) * (maxHit));
            }
            return maxHit;
        }

        Player player = (Player) entity;

        double specialMultiplier = 1;
        double prayerMultiplier = 1;
        double otherBonusMultiplier = 1;
        int rangedStrength = player.getRangedAmmo().getStrength();
        int rangeLevel = player.getSkills()[Skills.RANGED].getLevel();
        int combatStyleBonus = 0;

        switch (player.getFightType().getStyle()) {
        case ACCURATE:
            combatStyleBonus = 3;
            break;
        }

        // if (fullVoidRange(mob)) {
        // otherBonusMultiplier = 1.1;
        // }

        int effectiveRangeDamage = (int) ((rangeLevel * prayerMultiplier * otherBonusMultiplier) + combatStyleBonus);
        double baseDamage = 1.3 + (effectiveRangeDamage / 10) + (rangedStrength / 80) + ((effectiveRangeDamage * rangedStrength) / 640);

        if (player.isSpecialActivated()) {
            specialMultiplier = player.getCombatSpecial().getStrengthBonus();
        }

        maxHit = (int) (baseDamage * specialMultiplier);

        if (victim.type() == EntityType.NPC) {
            Npc npc = (Npc) victim;
            if (npc.getDefenceWeakened()[0]) {
                maxHit += (int) ((0.10) * (maxHit));
            } else if (npc.getDefenceWeakened()[1]) {
                maxHit += (int) ((0.20) * (maxHit));
            } else if (npc.getDefenceWeakened()[2]) {
                maxHit += (int) ((0.30) * (maxHit));
            }
        }
        if (Main.DEBUG)
            player.getPacketBuilder().sendMessage(
                "[DEBUG]: Maximum hit this turn is [" + maxHit + "].");
        return maxHit;
    }

    // /**
    // * The percentage of the hit reducted by antifire.
    // */
    // protected static double dragonfireReduction(Mob mob) {
    // boolean dragonfireShield = mob.getEquipment() != null
    // && (mob.getEquipment().contains(1540)
    // || mob.getEquipment().contains(11283)
    // || mob.getEquipment().contains(11284) || mob
    // .getEquipment().contains(11285));
    // boolean dragonfirePotion = false;
    // boolean protectPrayer = mob.getCombatState().getPrayer(
    // CombatPrayer.PROTECT_FROM_MAGIC);
    // if (dragonfireShield && dragonfirePotion) {
    // if (mob.getActionSender() != null) {
    // mob.getActionSender().sendMessage(
    // "You shield absorbs most of the dragon fire!");
    // mob.getActionSender()
    // .sendMessage(
    // "Your potion protects you from the heat of the dragon's breath!");
    // }
    // return 1;
    // } else if (dragonfireShield) {
    // if (mob.getActionSender() != null) {
    // mob.getActionSender().sendMessage(
    // "You shield absorbs most of the dragon fire!");
    // }
    // return 0.8; // 80%
    // } else if (dragonfirePotion) {
    // if (mob.getActionSender() != null) {
    // mob.getActionSender()
    // .sendMessage(
    // "Your potion protects you from the heat of the dragon's breath!");
    // }
    // return 0.8; // 80%
    // } else if (protectPrayer) {
    // if (mob.getActionSender() != null) {
    // mob.getActionSender().sendMessage(
    // "Your prayers resist some of the dragon fire.");
    // }
    // return 0.6; // 60%
    // }
    // return /* mob.getEquipment() != null */0;
    // }s

    /**
     * A series of checks performed before the entity attacks the victim.
     * 
     * @param builder
     *            the builder to perform the checks with.
     * @return true if the entity passed the checks, false if they did not.
     */
    protected static boolean checkHook(CombatBuilder builder) {

        // Check if we need to reset the combat session.
        if (builder.getVictim().isUnregistered() || builder.getEntity()
            .isUnregistered() || builder.getEntity().isDead() || builder
            .getVictim().isDead()) {
            builder.reset();
            return false;
        }

        // Here we check if the victim has teleported away.
        if (builder.getVictim().type() == EntityType.PLAYER) {
            if (((Player) builder.getVictim()).getTeleportStage() > 0) {
                builder.cooldown = 10;
                return false;
            }
        }

        // Here we check if we are already in combat with another entity.
        if (!Location.inMultiCombat(builder.getEntity()) && builder
            .isBeingAttacked() && !builder.getVictim().equals(
            builder.getLastAttacker())) {

            if (builder.getEntity().type() == EntityType.PLAYER)
                ((Player) builder.getEntity()).getPacketBuilder().sendMessage(
                    "You are already under attack!");
            builder.reset();
            return false;
        }

        // Here we check if the entity we are attacking is already in
        // combat.
        if (!Location.inMultiCombat(builder.getEntity()) && builder.getVictim()
            .getCombatBuilder().isBeingAttacked() && !builder.getVictim()
            .getCombatBuilder().getLastAttacker().equals(builder.getEntity())) {
            if (builder.getEntity().type() == EntityType.PLAYER)
                ((Player) builder.getEntity()).getPacketBuilder().sendMessage(
                    "They are already under attack!");
            builder.reset();
            return false;
        }

        // Check if the victim is still in the wilderness, and check if the
        // player can attack the victim while they are in a minigame.
        if (builder.getEntity().type() == EntityType.PLAYER) {
            Player player = (Player) builder.getEntity();
            Minigame minigame = MinigameFactory.getMinigame(player);

            if (minigame != null) {
                if (!minigame.canHit(player, builder.getVictim())) {
                    return false;
                }
            } else if (builder.getVictim().type() == EntityType.PLAYER) {
                if (!Location.inWilderness(builder.getVictim())) {
                    player.getPacketBuilder().sendMessage(
                        "They are not in the wilderness!");
                    builder.reset();
                    return false;
                }
            }
        }

        // Check if the npc needs to retreat.
        if (builder.getEntity().type() == EntityType.NPC) {
            Npc npc = (Npc) builder.getEntity();

            if (builder.getVictim().getCombatBuilder().isCooldown() && !npc
                .getPosition().isViewableFrom(npc.getOriginalPosition()) || !builder
                .getVictim().getCombatBuilder().isBeingAttacked() && !npc
                .getPosition().isViewableFrom(npc.getOriginalPosition())) {
                builder.reset();
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the entity is close enough to attack.
     * 
     * @param builder
     *            the builder used to perform the check.
     * @return true if the entity is close enough to attack, false otherwise.
     */
    protected static boolean checkAttackDistance(CombatBuilder builder) {
        Position attacker = builder.getEntity().getPosition();
        Position victim = builder.getVictim().getPosition();
        int distance = builder.getStrategy()
            .attackDistance(builder.getEntity());
        MovementQueue movement = builder.getEntity().getMovementQueue();

        // We're moving so increase the distance.
        if (!movement.isMovementDone()) {
            distance += 1;

            // We're running so increase the distance even more.

            // XXX: Might have to change this back to 1 or even remove it, not
            // sure what it's like on actual runescape. Are you allowed to
            // attack when the entity is trying to run away from you?
            if (movement.isRunToggled()) {
                distance += 2;
            }
        }

        // Check if we're within the required distance.
        return attacker.withinDistance(victim, distance);
    }

    /**
     * Applies combat prayer effects to the calculated hits.
     * 
     * @param container
     *            the combat container that holds the hits.
     * @param builder
     *            the builder to apply prayer effects to.
     */
    protected static void applyPrayer(CombatContainer container,
        CombatBuilder builder) {

        // If we aren't checking the accuracy, then don't bother doing any of
        // this.
        if (!container.isCheckAccuracy()) {
            return;
        }

        // The attacker is an npc, and the victim is a player so we completely
        // cancel the hits if the right prayer is active.
        if (builder.getVictim().type() == EntityType.PLAYER && builder
            .getEntity().type() == EntityType.NPC) {
            Player victim = (Player) builder.getVictim();
            Npc attacker = (Npc) builder.getEntity();

            // Except for verac of course :)
            if (attacker.getDefinition().getName().equals("Verac the Defiled")) {
                return;
            }

            // It's not verac so we cancel all of the hits.
            if (CombatPrayer.isActivated(victim, CombatPrayer
                .getProtectingPrayer(container.getCombatType()))) {
                container.allHits(context -> context.setAccurate(false));
            }
            return;
        }

        // The attacker is an player, and the victim is a npc so we only reduce
        // the damage by 20% and the accuracy at random.
        if (builder.getVictim().type() == EntityType.PLAYER && builder
            .getEntity().type() == EntityType.PLAYER) {
            final Player attacker = (Player) builder.getEntity();
            Player victim = (Player) builder.getVictim();

            // If wearing veracs, the attacker will hit through prayer
            // protection.
            if (CombatFactory.fullVeracs(attacker)) {
                if (Main.DEBUG)
                    attacker
                        .getPacketBuilder()
                        .sendMessage(
                            "[DEBUG]: Chance of opponents prayer cancelling hit [0%:" + CombatFactory.PRAYER_ACCURACY_REDUCTION + "%]");
                return;
            }

            // They aren't wearing veracs so lets reduce the accuracy and hits.
            if (CombatPrayer.isActivated(victim, CombatPrayer
                .getProtectingPrayer(container.getCombatType()))) {

                container.allHits(context -> {

                    // First reduce the damage.
                    int hit = context.getHit().getDamage();
                    double mod = Math
                        .abs(1 - CombatFactory.PRAYER_DAMAGE_REDUCTION);
                    context.getHit().setDamage((int) (hit * mod));
                    if (Main.DEBUG)
                        attacker
                            .getPacketBuilder()
                            .sendMessage(
                                "[DEBUG]: Damage reduced by opponents prayer [" + (hit - context
                                    .getHit().getDamage()) + "]");

                    // Then reduce the accuracy.
                    mod = Math.round(Utility.RANDOM.nextDouble() * 100.0) / 100.0;
                    if (Main.DEBUG)
                        attacker
                            .getPacketBuilder()
                            .sendMessage(
                                "[DEBUG]: Chance of opponents prayer cancelling hit [" + mod + "/" + CombatFactory.PRAYER_ACCURACY_REDUCTION + "]");
                    if (mod <= CombatFactory.PRAYER_ACCURACY_REDUCTION) {
                        context.setAccurate(false);
                    }
                });
            }
            return;
        }
    }

    /**
     * Gives experience for the total amount of damage dealt in a combat hit.
     * 
     * @param builder
     *            the attacker's combat builder.
     * @param container
     *            the attacker's combat container.
     * @param damage
     *            the total amount of damage dealt.
     */
    protected static void giveExperience(CombatBuilder builder,
        CombatContainer container, int damage) {

        // This attack does not give any experience.
        if (container.getExperience().length == 0 && container.getCombatType() != CombatType.MAGIC) {
            return;
        }

        // Otherwise we give experience as normal.
        if (builder.getEntity().type() == EntityType.PLAYER) {
            Player player = (Player) builder.getEntity();
            int exp = 0;
            int hitpointsExp = 0;

            // Add the experience exclusively for magic if needed.
            if (container.getCombatType() == CombatType.MAGIC) {
                exp = (int) (damage * 4d) + builder.getEntity()
                    .getCurrentlyCasting().baseExperience();
                hitpointsExp = (int) (exp / 3d);

                Skills.experience(player, exp, Skills.MAGIC);
                Skills.experience(player, hitpointsExp, Skills.HITPOINTS);
                return;
            }

            // Calculate the experience amount that will be given.
            exp = (int) ((damage * 4d) / container.getExperience().length);
            hitpointsExp = (int) (exp / 3d);

            // Now give the experience.
            for (int i : container.getExperience()) {
                Skills.experience(player, exp, i);
            }
            Skills.experience(player, hitpointsExp, Skills.HITPOINTS);
        }
    }

    /**
     * Handles various armor effects for the attacker and victim.
     * 
     * @param builder
     *            the attacker's combat builder.
     * @param container
     *            the attacker's combat container.
     * @param damage
     *            the total amount of damage dealt.
     */
    // TODO: Use abstraction for this, will need it when more effects are added.
    protected static void handleArmorEffects(CombatBuilder builder,
        CombatContainer container, int damage) {

        // 25% chance of these barrows armor effects happening.
        if (Utility.exclusiveRandom(4) == 0) {

            // The guthans effect is here.
            if (CombatFactory.fullGuthans(builder.getEntity())) {
                builder.getVictim().graphic(new Graphic(398));
                builder.getEntity().heal(damage);
                return;
            }

            // The rest of the effects only apply to victims that are players.
            if (builder.getVictim().type() == EntityType.PLAYER) {
                Player victim = (Player) builder.getVictim();

                // The torags effect is here.
                if (CombatFactory.fullTorags(builder.getEntity())) {
                    victim.decrementRunEnergy(Utility.inclusiveRandom(1, 100));
                    victim.graphic(new Graphic(399));
                    return;
                }

                // The ahrims effect is here.
                if (CombatFactory.fullAhrims(builder.getEntity()) && victim
                    .getSkills()[Skills.STRENGTH].getLevel() >= victim
                    .getSkills()[Skills.STRENGTH].getLevelForExperience()) {
                    victim.getSkills()[Skills.STRENGTH].decreaseLevel(Utility
                        .inclusiveRandom(1, 10));
                    Skills.refresh(victim, Skills.STRENGTH);
                    victim.graphic(new Graphic(400));
                    return;
                }

                // The karils effect is here.
                if (CombatFactory.fullKarils(builder.getEntity()) && victim
                    .getSkills()[Skills.AGILITY].getLevel() >= victim
                    .getSkills()[Skills.AGILITY].getLevelForExperience()) {
                    victim.graphic(new Graphic(401));
                    victim.getSkills()[Skills.AGILITY].decreaseLevel(Utility
                        .inclusiveRandom(1, 10));
                    Skills.refresh(victim, Skills.AGILITY);
                    return;
                }
            }
        }
    }

    /**
     * Handles various prayer effects for the attacker and victim.
     * 
     * @param builder
     *            the attacker's combat builder.
     * @param container
     *            the attacker's combat container.
     * @param damage
     *            the total amount of damage dealt.
     */
    protected static void handlePrayerEffects(CombatBuilder builder,
        CombatContainer container, int damage) {

        // Prayer effects can only be done with victims that are players.
        if (builder.getVictim().type() == EntityType.PLAYER && container
            .getHits().length != 0) {
            Player victim = (Player) builder.getVictim();

            // The redemption prayer effect.
            if (CombatPrayer.isActivated(victim, CombatPrayer.REDEMPTION) && victim
                .getSkills()[Skills.HITPOINTS].getLevel() <= (victim
                .getSkills()[Skills.HITPOINTS].getLevelForExperience() / 10)) {
                victim.getSkills()[Skills.HITPOINTS]
                    .increaseLevel(Utility
                        .inclusiveRandom(
                            1,
                            (int) (victim.getSkills()[Skills.HITPOINTS]
                                .getLevelForExperience() * CombatFactory.REDEMPTION_PRAYER_HEAL)));
                victim.graphic(new Graphic(436));
                victim.getSkills()[Skills.PRAYER].setLevel(0, true);
                victim.getPacketBuilder().sendMessage(
                    "You've run out of prayer points!");
                CombatPrayer.deactivateAll(victim);
                Skills.refresh(victim, Skills.PRAYER);
                Skills.refresh(victim, Skills.HITPOINTS);
                return;
            }

            // These last prayers can only be done with player attackers.
            if (builder.getEntity().type() == EntityType.PLAYER) {

                // The retribution prayer effect.
                if (CombatPrayer.isActivated(victim, CombatPrayer.RETRIBUTION) && victim
                    .getSkills()[Skills.HITPOINTS].getLevel() < 1) {
                    victim.graphic(new Graphic(437));

                    if (Location.inWilderness(victim) || MinigameFactory
                        .inMinigame(victim) && !Location.inMultiCombat(victim)) {
                        if (builder.getEntity().getPosition().withinDistance(
                            victim.getPosition(),
                            CombatFactory.RETRIBUTION_RADIUS)) {
                            builder
                                .getEntity()
                                .dealDamage(
                                    new Hit(
                                        Utility
                                            .inclusiveRandom(CombatFactory.MAXIMUM_RETRIBUTION_DAMAGE)));
                        }
                    } else if (Location.inWilderness(victim) || MinigameFactory
                        .inMinigame(victim) && Location.inMultiCombat(victim)) {
                        for (Player player : victim.getLocalPlayers()) {
                            if (player == null) {
                                continue;
                            }

                            if (!player.equals(victim) && player.getPosition()
                                .withinDistance(victim.getPosition(),
                                    CombatFactory.RETRIBUTION_RADIUS)) {
                                player
                                    .dealDamage(new Hit(
                                        Utility
                                            .inclusiveRandom(CombatFactory.MAXIMUM_RETRIBUTION_DAMAGE)));
                            }
                        }
                    }
                }

                // The smite prayer effect.
                if (CombatPrayer.isActivated((Player) builder.getEntity(),
                    CombatPrayer.SMITE)) {
                    victim.getSkills()[Skills.PRAYER].decreaseLevel(damage / 4);
                    Skills.refresh(victim, Skills.PRAYER);
                }
            }
        }
    }

    /**
     * A static factory method that constructs the default melee
     * {@link CombatStrategy} implementation.
     * 
     * @return the default melee combat strategy implementation.
     */
    public static CombatStrategy newDefaultMeleeStrategy() {
        return new DefaultMeleeCombatStrategy();
    }

    /**
     * A static factory method that constructs the default magic
     * {@link CombatStrategy} implementation.
     * 
     * @return the default magic combat strategy implementation.
     */
    public static CombatStrategy newDefaultMagicStrategy() {
        return new DefaultMagicCombatStrategy();
    }

    /**
     * A static factory method that constructs the default ranged
     * {@link CombatStrategy} implementation.
     * 
     * @return the default ranged combat strategy implementation.
     */
    public static CombatStrategy newDefaultRangedStrategy() {
        return new DefaultRangedCombatStrategy();
    }

    private CombatFactory() {}
}
