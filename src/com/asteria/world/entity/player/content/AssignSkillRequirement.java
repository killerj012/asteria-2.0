package com.asteria.world.entity.player.content;

import java.util.HashMap;
import java.util.Map;

import com.asteria.util.Utility;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.entity.player.skill.Skills.SkillData;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemDefinition;

/**
 * Makes sure that a player has the required skill levels to wear certain items.
 * 
 * @author lare96
 */
public final class AssignSkillRequirement {

    // TODO: Load all of this through json file, not going to touch this until
    // that's done.

    /** A map of the skill requirements. */
    private static Map<Integer, SkillRequirement[]> skillRequirements = new HashMap<>(
            75);

    /** Loads all of the items and appearance animations. */
    public static void loadSkillRequirements() {
        for (ItemDefinition def : ItemDefinition.getDefinitions()) {
            if (def == null || def.isNoted() || def.getEquipmentSlot() == -1) {
                continue;
            }

            if (def.getItemName().startsWith("Steel")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("warhammer") || def
                        .getItemName().endsWith("dagger") || def.getItemName()
                        .endsWith("dagger(p)") || def.getItemName().endsWith(
                        "dagger(p+)") || def.getItemName().endsWith(
                        "dagger(p++)") || def.getItemName().endsWith("sword") || def
                        .getItemName().endsWith("mace") || def.getItemName()
                        .endsWith("spear") || def.getItemName().endsWith(
                        "pickaxe") || def.getItemName().endsWith("claws")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(5,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(5,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("knife") || def
                        .getItemName().endsWith("knife(p)") || def
                        .getItemName().endsWith("knife(p+)") || def
                        .getItemName().endsWith("knife(p++)") || def
                        .getItemName().endsWith("thrownaxe") || def
                        .getItemName().endsWith("dart") || def.getItemName()
                        .endsWith("dart(p)") || def.getItemName().endsWith(
                        "dart(p+)") || def.getItemName().endsWith("dart(p++)") || def
                        .getItemName().endsWith("javelin") || def.getItemName()
                        .endsWith("javelin(p)") || def.getItemName().endsWith(
                        "javelin(p+)") || def.getItemName().endsWith(
                        "javelin(p++)")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(5,
                                    Skills.RANGED) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(5,
                                    Skills.ATTACK) });
                }
            } else if (def.getItemName().startsWith("Black")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("warhammer") || def
                        .getItemName().endsWith("dagger") || def.getItemName()
                        .endsWith("dagger(p)") || def.getItemName().endsWith(
                        "dagger(p+)") || def.getItemName().endsWith(
                        "dagger(p++)") || def.getItemName().endsWith("sword") || def
                        .getItemName().endsWith("mace") || def.getItemName()
                        .endsWith("spear") || def.getItemName().endsWith(
                        "pickaxe") || def.getItemName().endsWith("claws")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(10,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(10,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("knife") || def
                        .getItemName().endsWith("knife(p)") || def
                        .getItemName().endsWith("knife(p+)") || def
                        .getItemName().endsWith("knife(p++)") || def
                        .getItemName().endsWith("thrownaxe") || def
                        .getItemName().endsWith("dart") || def.getItemName()
                        .endsWith("dart(p)") || def.getItemName().endsWith(
                        "dart(p+)") || def.getItemName().endsWith("dart(p++)") || def
                        .getItemName().endsWith("javelin") || def.getItemName()
                        .endsWith("javelin(p)") || def.getItemName().endsWith(
                        "javelin(p+)") || def.getItemName().endsWith(
                        "javelin(p++)")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(10,
                                    Skills.RANGED) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements
                            .put(def.getItemId(), new SkillRequirement[] {
                                    new SkillRequirement(10, Skills.ATTACK),
                                    new SkillRequirement(5, Skills.STRENGTH) });
                }
            } else if (def.getItemName().startsWith("Mithril")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("warhammer") || def
                        .getItemName().endsWith("dagger") || def.getItemName()
                        .endsWith("dagger(p)") || def.getItemName().endsWith(
                        "dagger(p+)") || def.getItemName().endsWith(
                        "dagger(p++)") || def.getItemName().endsWith("sword") || def
                        .getItemName().endsWith("mace") || def.getItemName()
                        .endsWith("spear") || def.getItemName().endsWith(
                        "pickaxe") || def.getItemName().endsWith("claws")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(20,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(20,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("knife") || def
                        .getItemName().endsWith("knife(p)") || def
                        .getItemName().endsWith("knife(p+)") || def
                        .getItemName().endsWith("knife(p++)") || def
                        .getItemName().endsWith("thrownaxe") || def
                        .getItemName().endsWith("dart") || def.getItemName()
                        .endsWith("dart(p)") || def.getItemName().endsWith(
                        "dart(p+)") || def.getItemName().endsWith("dart(p++)") || def
                        .getItemName().endsWith("javelin") || def.getItemName()
                        .endsWith("javelin(p)") || def.getItemName().endsWith(
                        "javelin(p+)") || def.getItemName().endsWith(
                        "javelin(p++)")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(20,
                                    Skills.RANGED) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(20,
 Skills.ATTACK),
                                            new SkillRequirement(10,
 Skills.STRENGTH) });
                }
            } else if (def.getItemName().startsWith("Adamant")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("warhammer") || def
                        .getItemName().endsWith("dagger") || def.getItemName()
                        .endsWith("dagger(p)") || def.getItemName().endsWith(
                        "dagger(p+)") || def.getItemName().endsWith(
                        "dagger(p++)") || def.getItemName().endsWith("sword") || def
                        .getItemName().endsWith("mace") || def.getItemName()
                        .endsWith("spear") || def.getItemName().endsWith(
                        "pickaxe") || def.getItemName().endsWith("claws")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(30,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(30,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("knife") || def
                        .getItemName().endsWith("knife(p)") || def
                        .getItemName().endsWith("knife(p+)") || def
                        .getItemName().endsWith("knife(p++)") || def
                        .getItemName().endsWith("thrownaxe") || def
                        .getItemName().endsWith("dart") || def.getItemName()
                        .endsWith("dart(p)") || def.getItemName().endsWith(
                        "dart(p+)") || def.getItemName().endsWith("dart(p++)") || def
                        .getItemName().endsWith("javelin") || def.getItemName()
                        .endsWith("javelin(p)") || def.getItemName().endsWith(
                        "javelin(p+)") || def.getItemName().endsWith(
                        "javelin(p++)")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(30,
                                    Skills.RANGED) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(30,
 Skills.ATTACK),
                                            new SkillRequirement(15,
 Skills.STRENGTH) });
                }
            } else if (def.getItemName().startsWith("Rune")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("warhammer") || def
                        .getItemName().endsWith("dagger") || def.getItemName()
                        .endsWith("dagger(p)") || def.getItemName().endsWith(
                        "dagger(p+)") || def.getItemName().endsWith(
                        "dagger(p++)") || def.getItemName().endsWith("sword") || def
                        .getItemName().endsWith("mace") || def.getItemName()
                        .endsWith("spear") || def.getItemName().endsWith(
                        "pickaxe") || def.getItemName().endsWith("claws")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("knife") || def
                        .getItemName().endsWith("knife(p)") || def
                        .getItemName().endsWith("knife(p+)") || def
                        .getItemName().endsWith("knife(p++)") || def
                        .getItemName().endsWith("thrownaxe") || def
                        .getItemName().endsWith("dart") || def.getItemName()
                        .endsWith("dart(p)") || def.getItemName().endsWith(
                        "dart(p+)") || def.getItemName().endsWith("dart(p++)") || def
                        .getItemName().endsWith("javelin") || def.getItemName()
                        .endsWith("javelin(p)") || def.getItemName().endsWith(
                        "javelin(p+)") || def.getItemName().endsWith(
                        "javelin(p++)")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.RANGED) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(40,
 Skills.ATTACK),
                                            new SkillRequirement(20,
 Skills.STRENGTH) });
                }
            } else if (def.getItemName().startsWith("Dragon")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName()
                        .endsWith("battleaxe") || def.getItemName().endsWith(
                        "2h sword") || def.getItemName().endsWith("dagger") || def
                        .getItemName().endsWith("dagger(p)") || def
                        .getItemName().endsWith("dagger(p+)") || def
                        .getItemName().endsWith("dagger(p++)") || def
                        .getItemName().endsWith("sword") || def.getItemName()
                        .endsWith("mace") || def.getItemName()
                        .endsWith("spear")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.ATTACK) });
                } else if (def.getItemName().endsWith("med helm") || def
                        .getItemName().endsWith("chainbody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("sq shield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("halberd")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(60,
 Skills.ATTACK),
                                            new SkillRequirement(30,
 Skills.STRENGTH) });
                }
            } else if (def.getItemName().startsWith("Zamorak")) {
                if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("staff")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.MAGIC) });
                }
            } else if (def.getItemName().startsWith("Guthix")) {
                if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("staff")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.MAGIC) });
                }
            } else if (def.getItemName().startsWith("Saradomin")) {
                if (def.getItemName().endsWith("full helm") || def
                        .getItemName().endsWith("platebody") || def
                        .getItemName().endsWith("platelegs") || def
                        .getItemName().endsWith("plateskirt") || def
                        .getItemName().endsWith("kiteshield")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.DEFENCE) });
                } else if (def.getItemName().endsWith("staff")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.MAGIC) });
                }
            } else if (def.getItemName().endsWith("Abyssal whip")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(70,
                                Skills.ATTACK) });
            } else if (def.getItemName().equals("Granite maul")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(50, Skills.ATTACK),
                        new SkillRequirement(50, Skills.STRENGTH) });
            } else if (def.getItemName().equals("Tzhaar-ket-om")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(60,
                                Skills.STRENGTH) });
            } else if (def.getItemName().startsWith("Dharoks")) {
                if (def.getItemName().endsWith("greataxe")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(70,
 Skills.ATTACK),
                                            new SkillRequirement(70,
 Skills.STRENGTH) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.DEFENCE) });
                }
            } else if (def.getItemName().startsWith("Guthans")) {
                if (def.getItemName().endsWith("warspear")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.ATTACK) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.DEFENCE) });
                }
            } else if (def.getItemName().startsWith("Veracs")) {
                if (def.getItemName().endsWith("flail")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.ATTACK) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.DEFENCE) });
                }
            } else if (def.getItemName().startsWith("Karils")) {
                if (def.getItemName().endsWith("crossbow")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(70, Skills.DEFENCE),
                                    new SkillRequirement(70, Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Ahrims")) {
                if (def.getItemName().endsWith("staff")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(70, Skills.ATTACK),
                                    new SkillRequirement(70, Skills.MAGIC) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(70, Skills.DEFENCE),
                                    new SkillRequirement(70, Skills.MAGIC) });
                }
            } else if (def.getItemName().startsWith("Torags")) {
                if (def.getItemName().endsWith("hammers")) {
                    skillRequirements
                            .put(def.getItemId(),
                                    new SkillRequirement[] {
                                            new SkillRequirement(70,
 Skills.ATTACK),
                                            new SkillRequirement(70,
 Skills.STRENGTH) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.DEFENCE) });
                }
            } else if (def.getItemName().startsWith("Hardleather body")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(10,
                                Skills.DEFENCE) });
            } else if (def.getItemName().startsWith("Studded")) {
                if (def.getItemName().endsWith("body")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(20, Skills.DEFENCE),
                                    new SkillRequirement(20, Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(20,
                                    Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Green d'hide")) {
                if (def.getItemName().endsWith("body")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(40, Skills.DEFENCE),
                                    new SkillRequirement(40, Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Blue d'hide")) {
                if (def.getItemName().endsWith("body")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(40, Skills.DEFENCE),
                                    new SkillRequirement(50, Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(50,
                                    Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Red d'hide")) {
                if (def.getItemName().endsWith("body")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(40, Skills.DEFENCE),
                                    new SkillRequirement(60, Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(60,
                                    Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Black d'hide")) {
                if (def.getItemName().endsWith("body")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] {
                                    new SkillRequirement(40, Skills.DEFENCE),
                                    new SkillRequirement(70, Skills.RANGED) });
                } else {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(70,
                                    Skills.RANGED) });
                }
            } else if (def.getItemName().startsWith("Mystic")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(20, Skills.DEFENCE),
                        new SkillRequirement(40, Skills.MAGIC) });
            } else if (def.getItemName().startsWith("Enchanted")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(20, Skills.DEFENCE),
                        new SkillRequirement(40, Skills.MAGIC) });
            } else if (def.getItemName().startsWith("Splitbark")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(40, Skills.DEFENCE),
                        new SkillRequirement(40, Skills.MAGIC) });
            } else if (def.getItemName().startsWith("Infinity")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(25, Skills.DEFENCE),
                        new SkillRequirement(50, Skills.MAGIC) });
            } else if (def.getItemName().equals("Mage's book")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(60,
                                Skills.MAGIC) });
            } else if (def.getItemName().equals("Beginner wand")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(45,
                                Skills.MAGIC) });
            } else if (def.getItemName().equals("Apprentice wand")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(50,
                                Skills.MAGIC) });
            } else if (def.getItemName().equals("Teacher wand")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(55,
                                Skills.MAGIC) });
            } else if (def.getItemName().equals("Master wand")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(60,
                                Skills.MAGIC) });
            } else if (def.getItemName().equals("Ancient staff")) {
                skillRequirements.put(def.getItemId(), new SkillRequirement[] {
                        new SkillRequirement(50, Skills.MAGIC),
                        new SkillRequirement(50, Skills.ATTACK) });
            } else if (def.getItemName().equals("Iban's staff")) {
                skillRequirements.put(def.getItemId(),
                        new SkillRequirement[] { new SkillRequirement(50,
                                Skills.ATTACK) });
            } else if (def.getItemName().endsWith("shortbow") || def
                    .getItemName().equals("longbow")) {
                if (def.getItemName().startsWith("Oak")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(5,
                                    Skills.RANGED) });
                } else if (def.getItemName().startsWith("Willow")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(20,
                                    Skills.RANGED) });
                } else if (def.getItemName().startsWith("Maple")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(30,
                                    Skills.RANGED) });
                } else if (def.getItemName().startsWith("Yew")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(40,
                                    Skills.RANGED) });
                } else if (def.getItemName().startsWith("Magic")) {
                    skillRequirements.put(def.getItemId(),
                            new SkillRequirement[] { new SkillRequirement(50,
                                    Skills.RANGED) });
                }
            }
        }
    }

    /**
     * Checks if the player is allowed to equip the item.
     * 
     * @param player
     *            the player trying to equip the item.
     * @param equipItem
     *            the item being equipped by the player.
     * @return true if the player can equip the item.
     */
    public static boolean checkRequirement(Player player, Item equipItem) {
        if (equipItem == null) {
            return false;
        }

        SkillRequirement[] skillReqs = skillRequirements.get(equipItem.getId());

        if (skillReqs == null) {
            return true;
        }

        for (SkillRequirement skillReq : skillReqs) {
            if (player.getSkills()[skillReq.getSkillId()]
                    .getLevelForExperience() < skillReq
                    .getRequiredLevel()) {
                String skillName = Utility.capitalize(SkillData
                        .getSkill(skillReq.getSkillId()).name().toLowerCase());
                player.getPacketBuilder().sendMessage(
                        "You need a " + skillName + " level of " + skillReq
                                .getRequiredLevel() + " to wear this item.");
                return false;
            }
        }
        return true;
    }

    /**
     * A skill requirement that is used to prevent players from equipping items
     * that are too high of a level for them.
     * 
     * @author lare96
     */
    private static class SkillRequirement {

        /** The level required to equip the item. */
        private int requiredLevel;

        /** The skill that corresponds to the level. */
        private int skillId;

        /**
         * Create a new {@link SkillRequirement}.
         * 
         * @param requiredLevel
         *            the level required to equip the item.
         * @param skillId
         *            the skill that corresponds to the level.
         */
        public SkillRequirement(int requiredLevel, int skillId) {
            this.requiredLevel = requiredLevel;
            this.skillId = skillId;
        }

        /**
         * Gets the level required to equip the item.
         * 
         * @return the level required to equip the item.
         */
        public int getRequiredLevel() {
            return requiredLevel;
        }

        /**
         * Gets the skill that corresponds to the level.
         * 
         * @return the skill that corresponds to the level.
         */
        public int getSkillId() {
            return skillId;
        }
    }

    private AssignSkillRequirement() {}
}
