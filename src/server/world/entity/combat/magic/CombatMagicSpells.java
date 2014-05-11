package server.world.entity.combat.magic;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Projectile;
import server.world.entity.Spell;
import server.world.entity.combat.task.CombatTeleblockTask;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;

/**
 * Every single spell that can be cast in game.
 * 
 * @author lare96
 */
public enum CombatMagicSpells {

    /** Normal spellbook spells. */
    WIND_STRIKE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 91, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(92);
        }

        @Override
        public int maximumStrength() {
            return 2;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(90, 6553600);
        }

        @Override
        public int baseExperience() {
            return 5;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556), new Item(558) };
        }

        @Override
        public int levelRequired() {
            return 1;
        }

        @Override
        public int spellId() {
            return 1152;
        }
    }), CONFUSE(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(716);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 103, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.ATTACK].getLevel() < player.getSkills()[Misc.ATTACK].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.ATTACK].decreaseLevel((int) (0.05 * (player.getSkills()[Misc.ATTACK].getLevel())));
                SkillManager.refresh(player, SkillConstant.ATTACK);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[0] || npc.getStatsBadlyWeakened()[0]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getStatsWeakened()[0] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(104);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(102, 6553600);
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 2), new Item(559) };
        }

        @Override
        public int levelRequired() {
            return 3;
        }

        @Override
        public int spellId() {
            return 1153;
        }
    }),
    WATER_STRIKE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 94, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(95);
        }

        @Override
        public int maximumStrength() {
            return 4;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(93, 6553600);
        }

        @Override
        public int baseExperience() {
            return 7;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555), new Item(556), new Item(558) };
        }

        @Override
        public int levelRequired() {
            return 5;
        }

        @Override
        public int spellId() {
            return 1154;
        }
    }),
    EARTH_STRIKE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 97, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(98);
        }

        @Override
        public int maximumStrength() {
            return 6;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(96, 6553600);
        }

        @Override
        public int baseExperience() {
            return 9;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 1), new Item(558, 1), new Item(557, 2) };
        }

        @Override
        public int levelRequired() {
            return 9;
        }

        @Override
        public int spellId() {
            return 1156;
        }
    }),
    WEAKEN(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(716);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 106, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.STRENGTH].getLevel() < player.getSkills()[Misc.STRENGTH].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.STRENGTH].decreaseLevel((int) (0.05 * (player.getSkills()[Misc.STRENGTH].getLevel())));
                SkillManager.refresh(player, SkillConstant.STRENGTH);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[1] || npc.getStatsBadlyWeakened()[1]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getStatsWeakened()[1] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(107);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(105, 6553600);
        }

        @Override
        public int baseExperience() {
            return 21;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 2), new Item(559, 1) };
        }

        @Override
        public int levelRequired() {
            return 11;
        }

        @Override
        public int spellId() {
            return 1157;
        }
    }),
    FIRE_STRIKE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 100, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(101);
        }

        @Override
        public int maximumStrength() {
            return 8;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(99, 6553600);
        }

        @Override
        public int baseExperience() {
            return 11;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 1), new Item(558, 1), new Item(554, 3) };
        }

        @Override
        public int levelRequired() {
            return 13;
        }

        @Override
        public int spellId() {
            return 1158;
        }
    }),
    WIND_BOLT(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 118, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(119);
        }

        @Override
        public int maximumStrength() {
            return 9;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(117, 6553600);
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(562, 1) };
        }

        @Override
        public int levelRequired() {
            return 17;
        }

        @Override
        public int spellId() {
            return 1160;
        }
    }),
    CURSE(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(710);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 109, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.DEFENCE].getLevel() < player.getSkills()[Misc.DEFENCE].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.DEFENCE].decreaseLevel((int) (0.05 * (player.getSkills()[Misc.DEFENCE].getLevel())));
                SkillManager.refresh(player, SkillConstant.DEFENCE);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[2] || npc.getStatsBadlyWeakened()[2]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getStatsWeakened()[2] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(110);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(108, 6553600);
        }

        @Override
        public int baseExperience() {
            return 29;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 2), new Item(557, 3), new Item(559, 1) };
        }

        @Override
        public int levelRequired() {
            return 19;
        }

        @Override
        public int spellId() {
            return 1161;
        }
    }),
    BIND(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(710);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 178, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.getMovementQueue().isLockMovement()) {
                if (cast.isPlayer()) {
                    ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().lockMovementFor(9, WorkRate.DEFAULT);

            if (castOn.isPlayer()) {
                ((Player) castOn).getPacketBuilder().sendMessage("You have been frozen by magic!");
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(181);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 3), new Item(561, 2) };
        }

        @Override
        public int levelRequired() {
            return 20;
        }

        @Override
        public int spellId() {
            return 1572;
        }
    }),
    WATER_BOLT(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 121, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(122);
        }

        @Override
        public int maximumStrength() {
            return 10;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(120, 6553600);
        }

        @Override
        public int baseExperience() {
            return 16;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(562, 1), new Item(555, 2) };
        }

        @Override
        public int levelRequired() {
            return 23;
        }

        @Override
        public int spellId() {
            return 1163;
        }
    }),
    EARTH_BOLT(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 124, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(125);
        }

        @Override
        public int maximumStrength() {
            return 11;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(123, 6553600);
        }

        @Override
        public int baseExperience() {
            return 19;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(562, 1), new Item(557, 3) };
        }

        @Override
        public int levelRequired() {
            return 29;
        }

        @Override
        public int spellId() {
            return 1166;
        }
    }),
    FIRE_BOLT(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 127, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(128);
        }

        @Override
        public int maximumStrength() {
            return 12;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(126, 6553600);
        }

        @Override
        public int baseExperience() {
            return 22;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 3), new Item(562, 1), new Item(554, 4) };
        }

        @Override
        public int levelRequired() {
            return 35;
        }

        @Override
        public int spellId() {
            return 1169;
        }
    }),
    CRUMBLE_UNDEAD(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(724);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 146, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(147);
        }

        @Override
        public int maximumStrength() {
            return 15;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(145, 6553600);
        }

        @Override
        public int baseExperience() {
            return 24;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(562, 1), new Item(557, 2) };
        }

        @Override
        public int levelRequired() {
            return 39;
        }

        @Override
        public int spellId() {
            return 1171;
        }
    }),
    WIND_BLAST(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 133, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(134);
        }

        @Override
        public int maximumStrength() {
            return 13;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(132, 6553600);
        }

        @Override
        public int baseExperience() {
            return 25;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 3), new Item(560, 1) };
        }

        @Override
        public int levelRequired() {
            return 41;
        }

        @Override
        public int spellId() {
            return 1172;
        }
    }),
    WATER_BLAST(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 136, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(137);
        }

        @Override
        public int maximumStrength() {
            return 14;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(135, 6553600);
        }

        @Override
        public int baseExperience() {
            return 28;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(556, 3), new Item(560, 1) };
        }

        @Override
        public int levelRequired() {
            return 47;
        }

        @Override
        public int spellId() {
            return 1175;
        }
    }),
    IBAN_BLAST(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(708);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 88, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(89);
        }

        @Override
        public int maximumStrength() {
            return 25;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(87, 6553600);
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return new Item[] { new Item(1409) };
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(560, 1), new Item(554, 5) };
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1539;
        }
    }),
    SNARE(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(710);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 178, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.getMovementQueue().isLockMovement()) {
                if (cast.isPlayer()) {
                    ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().lockMovementFor(10, WorkRate.APPROXIMATE_SECOND);

            if (castOn.isPlayer()) {
                ((Player) castOn).getPacketBuilder().sendMessage("You have been frozen by magic!");
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(180);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 60;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 4), new Item(561, 3) };
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 1582;
        }
    }),
    MAGIC_DART(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(1576);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 328, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(329);
        }

        @Override
        public int maximumStrength() {
            return 19;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(327, 6553600);
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return new Item[] { new Item(4170) };
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(558, 4), new Item(560, 1) };
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12037;
        }
    }),
    EARTH_BLAST(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 139, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(140);
        }

        @Override
        public int maximumStrength() {
            return 15;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(138, 6553600);
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 3), new Item(560, 1), new Item(557, 4) };
        }

        @Override
        public int levelRequired() {
            return 53;
        }

        @Override
        public int spellId() {
            return 1177;
        }
    }),
    FIRE_BLAST(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 130, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(131);
        }

        @Override
        public int maximumStrength() {
            return 16;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(129, 6553600);
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(560, 1), new Item(554, 5) };
        }

        @Override
        public int levelRequired() {
            return 59;
        }

        @Override
        public int spellId() {
            return 1181;
        }
    }),
    SARADOMIN_STRIKE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(76);
        }

        @Override
        public int maximumStrength() {
            return 20;
        }

        @Override
        public Gfx startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return new Item[] { new Item(2415) };
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) };
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1190;
        }
    }),
    CLAWS_OF_GUTHIX(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(77);
        }

        @Override
        public int maximumStrength() {
            return 20;
        }

        @Override
        public Gfx startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return new Item[] { new Item(2416) };
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) };
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1191;
        }
    }),
    FLAMES_OF_ZAMORAK(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(78);
        }

        @Override
        public int maximumStrength() {
            return 20;
        }

        @Override
        public Gfx startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 35;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return new Item[] { new Item(2417) };
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(565, 2), new Item(554, 2) };
        }

        @Override
        public int levelRequired() {
            return 60;
        }

        @Override
        public int spellId() {
            return 1192;
        }
    }),
    WIND_WAVE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 159, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(160);
        }

        @Override
        public int maximumStrength() {
            return 17;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(158, 6553600);
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 5), new Item(565, 1) };
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 1183;
        }
    }),
    WATER_WAVE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 162, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(163);
        }

        @Override
        public int maximumStrength() {
            return 18;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(161, 6553600);
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 5), new Item(565, 1), new Item(555, 7) };
        }

        @Override
        public int levelRequired() {
            return 65;
        }

        @Override
        public int spellId() {
            return 1185;
        }
    }),
    VULNERABILITY(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(729);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 168, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.DEFENCE].getLevel() < player.getSkills()[Misc.DEFENCE].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.DEFENCE].decreaseLevel((int) (0.10 * (player.getSkills()[Misc.DEFENCE].getLevel())));
                SkillManager.refresh(player, SkillConstant.DEFENCE);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[2] || npc.getStatsBadlyWeakened()[2]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStatsBadlyWeakened()[2] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(169);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(167, 6553600);
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 5), new Item(555, 5), new Item(566, 1) };
        }

        @Override
        public int levelRequired() {
            return 66;
        }

        @Override
        public int spellId() {
            return 1542;
        }
    }),
    EARTH_WAVE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 165, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(166);
        }

        @Override
        public int maximumStrength() {
            return 19;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(164, 6553600);
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 5), new Item(565, 1), new Item(557, 7) };
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 1188;
        }
    }),
    ENFEEBLE(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(729);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 171, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.STRENGTH].getLevel() < player.getSkills()[Misc.STRENGTH].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.STRENGTH].decreaseLevel((int) (0.10 * (player.getSkills()[Misc.STRENGTH].getLevel())));
                SkillManager.refresh(player, SkillConstant.STRENGTH);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[1] || npc.getStatsBadlyWeakened()[1]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStatsBadlyWeakened()[1] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(172);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(170, 6553600);
        }

        @Override
        public int baseExperience() {
            return 83;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 8), new Item(555, 8), new Item(566, 1) };
        }

        @Override
        public int levelRequired() {
            return 73;
        }

        @Override
        public int spellId() {
            return 1543;
        }
    }),
    FIRE_WAVE(new CombatFightSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 156, 44, 3, 43, 31, 0);
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(157);
        }

        @Override
        public int maximumStrength() {
            return 20;
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(155, 6553600);
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 5), new Item(565, 1), new Item(554, 7) };
        }

        @Override
        public int levelRequired() {
            return 75;
        }

        @Override
        public int spellId() {
            return 1189;
        }
    }),
    ENTANGLE(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(710);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 178, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.getMovementQueue().isLockMovement()) {
                if (cast.isPlayer()) {
                    ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().lockMovementFor(15, WorkRate.APPROXIMATE_SECOND);

            if (castOn.isPlayer()) {
                ((Player) castOn).getPacketBuilder().sendMessage("You have been frozen by magic!");
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(179);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 91;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 5), new Item(557, 5), new Item(561, 4) };
        }

        @Override
        public int levelRequired() {
            return 79;
        }

        @Override
        public int spellId() {
            return 1592;
        }
    }),
    STUN(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(729);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 174, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getSkills()[Misc.ATTACK].getLevel() < player.getSkills()[Misc.ATTACK].getLevelForExperience()) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Misc.ATTACK].decreaseLevel((int) (0.10 * (player.getSkills()[Misc.ATTACK].getLevel())));
                SkillManager.refresh(player, SkillConstant.ATTACK);
                player.getPacketBuilder().sendMessage("You feel slightly weakened.");
            } else if (castOn.isNpc()) {
                Npc npc = (Npc) castOn;

                if (npc.getStatsWeakened()[0] || npc.getStatsBadlyWeakened()[0]) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStatsBadlyWeakened()[0] = true;
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(107);
        }

        @Override
        public Gfx startGfx() {
            return new Gfx(173, 6553600);
        }

        @Override
        public int baseExperience() {
            return 90;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 12), new Item(555, 12), new Item(556, 1) };
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 1562;
        }
    }),
    TELEBLOCK(new CombatEffectSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(1819);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 344, 44, 3, 43, 31, 0);
        }

        @Override
        public void spellEffect(Entity cast, Entity castOn) {
            if (castOn.isPlayer()) {
                Player player = (Player) castOn;

                if (player.getTeleblockTimer() > 0) {
                    if (cast.isPlayer()) {
                        ((Player) cast).getPacketBuilder().sendMessage("The spell has no effect because the player is already teleblocked.");
                    }
                    return;
                }

                player.setTeleblockTimer(3000);
                TaskFactory.getFactory().submit(new CombatTeleblockTask(player));
                player.getPacketBuilder().sendMessage("You have just been teleblocked!");
            } else if (castOn.isNpc()) {
                if (cast.isPlayer()) {
                    ((Player) cast).getPacketBuilder().sendMessage("All NPCs are completely immune to this particular spell.");
                }
            }
        }

        @Override
        public Gfx endGfx() {
            return new Gfx(345);
        }

        @Override
        public Gfx startGfx() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Item[] equipmentRequired(Player player) {
            return null;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(563, 1), new Item(562, 1), new Item(560, 1) };
        }

        @Override
        public int levelRequired() {
            return 85;
        }

        @Override
        public int spellId() {
            return 12445;
        }
    }),

    /** Ancient spellbook spells. */
    SMOKE_RUSH(null),
    SHADOW_RUSH(null),
    BLOOD_RUSH(null),
    ICE_RUSH(null),
    SMOKE_BURST(null),
    SHADOW_BURST(null),
    BLOOD_BURST(null),
    ICE_BURST(null),
    SMOKE_BLITZ(null),
    SHADOW_BLITZ(null),
    BLOOD_BLITZ(null),
    ICE_BLITZ(null),
    SMOKE_BARRAGE(null),
    SHADOW_BARRAGE(null),
    BLOOD_BARRAGE(null),
    ICE_BARRAGE(null);

    private Spell spell;

    private CombatMagicSpells(Spell spell) {
        this.spell = spell;
    }

    public Spell getSpell() {
        return spell;
    }

    public static CombatMagicSpells getSpell(int spellId) {
        for (CombatMagicSpells spell : CombatMagicSpells.values()) {
            if (spell.getSpell() == null) {
                continue;
            }

            if (spell.getSpell().spellId() == spellId) {
                return spell;
            }
        }
        return null;
    }
}
