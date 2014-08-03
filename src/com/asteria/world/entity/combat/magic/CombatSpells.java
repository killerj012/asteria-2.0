package com.asteria.world.entity.combat.magic;

import com.asteria.engine.task.TaskManager;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.Projectile;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect.PoisonType;
import com.asteria.world.entity.combat.effect.CombatTeleblockEffect;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;

/**
 * Holds all of the {@link CombatSpell}s that can be cast by an {@link Entity}.
 * 
 * @author lare96
 */
public enum CombatSpells {

    // TODO: Find another way to do this. This would be hard to load externally
    // because certain effects for spells are done in here.

    /** Normal spellbook spells. */
    WIND_STRIKE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 91, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(92);
        }

        @Override
        public int maximumHit() {
            return 2;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(90, 6553600);
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
    }),
    CONFUSE(new CombatEffectSpell() {
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.05 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getDefenceWeakened()[0] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(104);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(102, 6553600);
        }

        @Override
        public int baseExperience() {
            return 13;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 2),
                    new Item(559) };
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
    WATER_STRIKE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 94, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(95);
        }

        @Override
        public int maximumHit() {
            return 4;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(93, 6553600);
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
    EARTH_STRIKE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 97, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(98);
        }

        @Override
        public int maximumHit() {
            return 6;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(96, 6553600);
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
            return new Item[] { new Item(556, 1), new Item(558, 1),
                    new Item(557, 2) };
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.STRENGTH].getLevel() < player
                        .getSkills()[Skills.STRENGTH].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.STRENGTH]
                        .decreaseLevel((int) (0.05 * (player.getSkills()[Skills.STRENGTH]
                                .getLevel())));
                Skills.refresh(player, Skills.STRENGTH);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[1] || npc.getStrengthWeakened()[1]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getDefenceWeakened()[1] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(107);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(105, 6553600);
        }

        @Override
        public int baseExperience() {
            return 21;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 2),
                    new Item(559, 1) };
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
    FIRE_STRIKE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 100, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(101);
        }

        @Override
        public int maximumHit() {
            return 8;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(99, 6553600);
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
            return new Item[] { new Item(556, 1), new Item(558, 1),
                    new Item(554, 3) };
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
    WIND_BOLT(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 118, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(119);
        }

        @Override
        public int maximumHit() {
            return 9;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(117, 6553600);
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.DEFENCE].getLevel() < player
                        .getSkills()[Skills.DEFENCE].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player has already been weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.DEFENCE]
                        .decreaseLevel((int) (0.05 * (player.getSkills()[Skills.DEFENCE]
                                .getLevel())));
                Skills.refresh(player, Skills.DEFENCE);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC has already been weakened.");
                    }
                    return;
                }

                npc.getDefenceWeakened()[2] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(110);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(108, 6553600);
        }

        @Override
        public int baseExperience() {
            return 29;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 2), new Item(557, 3),
                    new Item(559, 1) };
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
                if (cast.type() == EntityType.PLAYER) {
                    ((Player) cast)
                            .getPacketBuilder()
                            .sendMessage(
                                    "The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().freeze(5000);

            if (castOn.type() == EntityType.PLAYER) {
                ((Player) castOn).getPacketBuilder().sendMessage(
                        "You have been frozen by magic!");
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(181, 6553600);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 3),
                    new Item(561, 2) };
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
    WATER_BOLT(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 121, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(122);
        }

        @Override
        public int maximumHit() {
            return 10;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(120, 6553600);
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
            return new Item[] { new Item(556, 2), new Item(562, 1),
                    new Item(555, 2) };
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
    EARTH_BOLT(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 124, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(125);
        }

        @Override
        public int maximumHit() {
            return 11;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(123, 6553600);
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
            return new Item[] { new Item(556, 2), new Item(562, 1),
                    new Item(557, 3) };
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
    FIRE_BOLT(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 127, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(128);
        }

        @Override
        public int maximumHit() {
            return 12;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(126, 6553600);
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
            return new Item[] { new Item(556, 3), new Item(562, 1),
                    new Item(554, 4) };
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
    CRUMBLE_UNDEAD(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(724);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 146, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(147);
        }

        @Override
        public int maximumHit() {
            return 15;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(145, 6553600);
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
            return new Item[] { new Item(556, 2), new Item(562, 1),
                    new Item(557, 2) };
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
    WIND_BLAST(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 133, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(134);
        }

        @Override
        public int maximumHit() {
            return 13;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(132, 6553600);
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
    WATER_BLAST(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 136, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(137);
        }

        @Override
        public int maximumHit() {
            return 14;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(135, 6553600);
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
            return new Item[] { new Item(555, 3), new Item(556, 3),
                    new Item(560, 1) };
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
    IBAN_BLAST(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(708);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 88, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(89);
        }

        @Override
        public int maximumHit() {
            return 25;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(87, 6553600);
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
                if (cast.type() == EntityType.PLAYER) {
                    ((Player) cast)
                            .getPacketBuilder()
                            .sendMessage(
                                    "The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().freeze(10000);

            if (castOn.type() == EntityType.PLAYER) {
                ((Player) castOn).getPacketBuilder().sendMessage(
                        "You have been frozen by magic!");
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(180, 6553600);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 60;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(557, 4),
                    new Item(561, 3) };
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
    MAGIC_DART(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(1576);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 328, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(329);
        }

        @Override
        public int maximumHit() {
            return 19;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(327, 6553600);
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
    EARTH_BLAST(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 139, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(140);
        }

        @Override
        public int maximumHit() {
            return 15;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(138, 6553600);
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
            return new Item[] { new Item(556, 3), new Item(560, 1),
                    new Item(557, 4) };
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
    FIRE_BLAST(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 130, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(131);
        }

        @Override
        public int maximumHit() {
            return 16;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(129, 6553600);
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
            return new Item[] { new Item(556, 4), new Item(560, 1),
                    new Item(554, 5) };
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
    SARADOMIN_STRIKE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(76);
        }

        @Override
        public int maximumHit() {
            return 20;
        }

        @Override
        public Graphic startGraphic() {
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
            return new Item[] { new Item(556, 4), new Item(565, 2),
                    new Item(554, 2) };
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
    CLAWS_OF_GUTHIX(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(77);
        }

        @Override
        public int maximumHit() {
            return 20;
        }

        @Override
        public Graphic startGraphic() {
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
            return new Item[] { new Item(556, 4), new Item(565, 2),
                    new Item(554, 2) };
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
    FLAMES_OF_ZAMORAK(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(811);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(78);
        }

        @Override
        public int maximumHit() {
            return 20;
        }

        @Override
        public Graphic startGraphic() {
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
            return new Item[] { new Item(556, 4), new Item(565, 2),
                    new Item(554, 2) };
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
    WIND_WAVE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 159, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(160);
        }

        @Override
        public int maximumHit() {
            return 17;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(158, 6553600);
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
    WATER_WAVE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 162, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(163);
        }

        @Override
        public int maximumHit() {
            return 18;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(161, 6553600);
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
            return new Item[] { new Item(556, 5), new Item(565, 1),
                    new Item(555, 7) };
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.DEFENCE].getLevel() < player
                        .getSkills()[Skills.DEFENCE].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.DEFENCE]
                        .decreaseLevel((int) (0.10 * (player.getSkills()[Skills.DEFENCE]
                                .getLevel())));
                Skills.refresh(player, Skills.DEFENCE);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[2] || npc.getStrengthWeakened()[2]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStrengthWeakened()[2] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(169);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(167, 6553600);
        }

        @Override
        public int baseExperience() {
            return 76;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 5), new Item(555, 5),
                    new Item(566, 1) };
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
    EARTH_WAVE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 165, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(166);
        }

        @Override
        public int maximumHit() {
            return 19;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(164, 6553600);
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
            return new Item[] { new Item(556, 5), new Item(565, 1),
                    new Item(557, 7) };
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.STRENGTH].getLevel() < player
                        .getSkills()[Skills.STRENGTH].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.STRENGTH]
                        .decreaseLevel((int) (0.10 * (player.getSkills()[Skills.STRENGTH]
                                .getLevel())));
                Skills.refresh(player, Skills.STRENGTH);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[1] || npc.getStrengthWeakened()[1]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStrengthWeakened()[1] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(172);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(170, 6553600);
        }

        @Override
        public int baseExperience() {
            return 83;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 8), new Item(555, 8),
                    new Item(566, 1) };
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
    FIRE_WAVE(new CombatNormalSpell() {
        @Override
        public Animation castAnimation() {
            return new Animation(711);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 156, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(157);
        }

        @Override
        public int maximumHit() {
            return 20;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(155, 6553600);
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
            return new Item[] { new Item(556, 5), new Item(565, 1),
                    new Item(554, 7) };
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
                if (cast.type() == EntityType.PLAYER) {
                    ((Player) cast)
                            .getPacketBuilder()
                            .sendMessage(
                                    "The spell has no effect because they are already frozen.");
                }
                return;
            }

            castOn.getMovementQueue().freeze(12000);

            if (castOn.type() == EntityType.PLAYER) {
                ((Player) castOn).getPacketBuilder().sendMessage(
                        "You have been frozen by magic!");
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(179, 6553600);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(177, 6553600);
        }

        @Override
        public int baseExperience() {
            return 91;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 5), new Item(557, 5),
                    new Item(561, 4) };
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player is already weakened.");
                    }
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.10 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
                player.getPacketBuilder().sendMessage(
                        "You feel slightly weakened.");
            } else if (castOn.type() == EntityType.NPC) {
                Npc npc = (Npc) castOn;

                if (npc.getDefenceWeakened()[0] || npc.getStrengthWeakened()[0]) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the NPC is already weakened.");
                    }
                    return;
                }

                npc.getStrengthWeakened()[0] = true;
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(107);
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(173, 6553600);
        }

        @Override
        public int baseExperience() {
            return 90;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(557, 12), new Item(555, 12),
                    new Item(556, 1) };
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
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getTeleblockTimer() > 0) {
                    if (cast.type() == EntityType.PLAYER) {
                        ((Player) cast)
                                .getPacketBuilder()
                                .sendMessage(
                                        "The spell has no effect because the player is already teleblocked.");
                    }
                    return;
                }

                player.setTeleblockTimer(3000);
                TaskManager.submit(new CombatTeleblockEffect(player));
                player.getPacketBuilder().sendMessage(
                        "You have just been teleblocked!");
            } else if (castOn.type() == EntityType.NPC) {
                if (cast.type() == EntityType.PLAYER) {
                    ((Player) cast)
                            .getPacketBuilder()
                            .sendMessage(
                                    "All NPCs are completely immune to this particular spell.");
                }
            }
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(345);
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 65;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(563, 1), new Item(562, 1),
                    new Item(560, 1) };
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
    SMOKE_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            CombatFactory.poisonEntity(castOn, PoisonType.MILD);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 384, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(385);
        }

        @Override
        public int maximumHit() {
            return 13;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 30;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 1), new Item(554, 1),
                    new Item(562, 2), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 50;
        }

        @Override
        public int spellId() {
            return 12939;
        }
    }),
    SHADOW_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.1 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 378, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(379);
        }

        @Override
        public int maximumHit() {
            return 14;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 31;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 1), new Item(566, 1),
                    new Item(562, 2), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 52;
        }

        @Override
        public int spellId() {
            return 12987;
        }
    }),
    BLOOD_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (damage < 1) {
                return;
            }

            if (cast.type() == EntityType.PLAYER) {
                Player player = (Player) cast;
                player.heal((int) (damage * 0.25));
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 372, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(373);
        }

        @Override
        public int maximumHit() {
            return 15;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 33;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(565, 1), new Item(562, 2),
                    new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 56;
        }

        @Override
        public int spellId() {
            return 12901;
        }
    }),
    ICE_RUSH(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            castOn.getMovementQueue().freeze(7000);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 360, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(361);
        }

        @Override
        public int maximumHit() {
            return 18;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 34;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 2), new Item(562, 2),
                    new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 58;
        }

        @Override
        public int spellId() {
            return 12861;
        }
    }),
    SMOKE_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            CombatFactory.poisonEntity(castOn, PoisonType.MILD);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(389);
        }

        @Override
        public int maximumHit() {
            return 13;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 36;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(554, 2),
                    new Item(562, 4), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 62;
        }

        @Override
        public int spellId() {
            return 12963;
        }
    }),
    SHADOW_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.1 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(382);
        }

        @Override
        public int maximumHit() {
            return 18;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 37;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 1), new Item(566, 2),
                    new Item(562, 4), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 64;
        }

        @Override
        public int spellId() {
            return 13011;
        }
    }),
    BLOOD_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (damage < 1) {
                return;
            }

            if (cast.type() == EntityType.PLAYER) {
                Player player = (Player) cast;
                player.heal((int) (damage * 0.25));
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(376);
        }

        @Override
        public int maximumHit() {
            return 21;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 39;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(565, 2), new Item(562, 4),
                    new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 68;
        }

        @Override
        public int spellId() {
            return 12919;
        }
    }),
    ICE_BURST(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            castOn.getMovementQueue().freeze(9000);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(363);
        }

        @Override
        public int maximumHit() {
            return 22;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 40;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 4), new Item(562, 4),
                    new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 70;
        }

        @Override
        public int spellId() {
            return 12881;
        }
    }),
    SMOKE_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            CombatFactory.poisonEntity(castOn, PoisonType.EXTRA);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 386, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(387);
        }

        @Override
        public int maximumHit() {
            return 23;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 42;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(554, 2),
                    new Item(565, 2), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 74;
        }

        @Override
        public int spellId() {
            return 12951;
        }
    }),
    SHADOW_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.15 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 380, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(381);
        }

        @Override
        public int maximumHit() {
            return 24;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 43;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 2), new Item(566, 2),
                    new Item(565, 2), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 76;
        }

        @Override
        public int spellId() {
            return 12999;
        }
    }),
    BLOOD_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (damage < 1) {
                return;
            }

            if (cast.type() == EntityType.PLAYER) {
                Player player = (Player) cast;
                player.heal((int) (damage * 0.25));
            }
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return new Projectile(cast, castOn, 374, 44, 3, 43, 31, 0);
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(375);
        }

        @Override
        public int maximumHit() {
            return 25;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 45;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(565, 4), new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 80;
        }

        @Override
        public int spellId() {
            return 12911;
        }
    }),
    ICE_BLITZ(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            castOn.getMovementQueue().freeze(10000);
        }

        @Override
        public int spellRadius() {
            return 0;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1978);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(367);
        }

        @Override
        public int maximumHit() {
            return 26;
        }

        @Override
        public Graphic startGraphic() {
            return new Graphic(366, 6553600);
        }

        @Override
        public int baseExperience() {
            return 46;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 3), new Item(565, 2),
                    new Item(560, 2) };
        }

        @Override
        public int levelRequired() {
            return 82;
        }

        @Override
        public int spellId() {
            return 12871;
        }
    }),
    SMOKE_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            CombatFactory.poisonEntity(castOn, PoisonType.SUPER);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(391);
        }

        @Override
        public int maximumHit() {
            return 27;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 48;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(554, 4),
                    new Item(565, 2), new Item(560, 4) };
        }

        @Override
        public int levelRequired() {
            return 86;
        }

        @Override
        public int spellId() {
            return 12975;
        }
    }),
    SHADOW_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (castOn.type() == EntityType.PLAYER) {
                Player player = (Player) castOn;

                if (player.getSkills()[Skills.ATTACK].getLevel() < player
                        .getSkills()[Skills.ATTACK].getLevelForExperience()) {
                    return;
                }

                player.getSkills()[Skills.ATTACK]
                        .decreaseLevel((int) (0.15 * (player.getSkills()[Skills.ATTACK]
                                .getLevel())));
                Skills.refresh(player, Skills.ATTACK);
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(383);
        }

        @Override
        public int maximumHit() {
            return 28;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 49;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(556, 4), new Item(566, 3),
                    new Item(565, 2), new Item(560, 4) };
        }

        @Override
        public int levelRequired() {
            return 88;
        }

        @Override
        public int spellId() {
            return 13023;
        }
    }),
    BLOOD_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            if (damage < 1) {
                return;
            }

            if (cast.type() == EntityType.PLAYER) {
                Player player = (Player) cast;
                player.heal((int) (damage * 0.25));
            }
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(377);
        }

        @Override
        public int maximumHit() {
            return 29;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 51;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(560, 4), new Item(566, 1),
                    new Item(565, 4) };
        }

        @Override
        public int levelRequired() {
            return 92;
        }

        @Override
        public int spellId() {
            return 12929;
        }
    }),
    ICE_BARRAGE(new CombatAncientSpell() {
        @Override
        public void spellEffect(Entity cast, Entity castOn, int damage) {
            castOn.getMovementQueue().freeze(15000);
        }

        @Override
        public int spellRadius() {
            return 1;
        }

        @Override
        public Animation castAnimation() {
            return new Animation(1979);
        }

        @Override
        public Projectile castProjectile(Entity cast, Entity castOn) {
            return null;
        }

        @Override
        public Graphic endGraphic() {
            return new Graphic(369);
        }

        @Override
        public int maximumHit() {
            return 30;
        }

        @Override
        public Graphic startGraphic() {
            return null;
        }

        @Override
        public int baseExperience() {
            return 52;
        }

        @Override
        public Item[] itemsRequired(Player player) {
            return new Item[] { new Item(555, 6), new Item(565, 2),
                    new Item(560, 4) };
        }

        @Override
        public int levelRequired() {
            return 94;
        }

        @Override
        public int spellId() {
            return 12891;
        }
    });

    /** The combat spell that can be casted. */
    private CombatSpell spell;

    /**
     * Create a new {@link CombatSpells}.
     * 
     * @param spell
     *            the combat spell that can be casted.
     */
    private CombatSpells(CombatSpell spell) {
        this.spell = spell;
    }

    /**
     * Gets the combat spell that can be casted.
     * 
     * @return the combat spell that can be casted.
     */
    public CombatSpell getSpell() {
        return spell;
    }

    /**
     * Gets the spell constant by its spell id.
     * 
     * @param spellId
     *            the spell to retrieve.
     * @return the spell constant with that spell id.
     */
    public static CombatSpells getSpell(int spellId) {
        for (CombatSpells spell : CombatSpells.values()) {
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
