package server.world.entity.player.content;

import server.world.item.ItemDefinition;

public class AssignSkillRequirement {

    /** A map of items and the appearance animations. */
    private static SkillRequirement[] skillRequirements = new SkillRequirement[7956];

    /** Loads all of the items and appearance animations. */
    static {

        /** Iterate through the item database. */
        for (ItemDefinition def : ItemDefinition.getDefinitions()) {

            /** Filter items. */
            if (def == null || def.isNoted() || def.getEquipmentSlot() != -1) {
                continue;
            }

            /** Load the map with the correct data. */
            if (def.getItemName().startsWith("Bronze")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                }
            } else if (def.getItemName().startsWith("Iron")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Steel")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Black")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Mithril")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Adamant")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Rune")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Dragon")) {
                if (def.getItemName().endsWith("scimitar") || def.getItemName().endsWith("battleaxe") || def.getItemName().endsWith("2h sword") || def.getItemName().endsWith("warhammer") || def.getItemName().endsWith("dagger") || def.getItemName().endsWith("dagger(p)") || def.getItemName().endsWith("dagger(p+)") || def.getItemName().endsWith("dagger(p++)") || def.getItemName().endsWith("sword") || def.getItemName().endsWith("mace") || def.getItemName().endsWith("spear") || def.getItemName().endsWith("pickaxe") || def.getItemName().endsWith("claws") || def.getItemName().endsWith("halberd")) {

                } else if (def.getItemName().endsWith("full helm") || def.getItemName().endsWith("med helm") || def.getItemName().endsWith("platebody") || def.getItemName().endsWith("chainbody") || def.getItemName().endsWith("platelegs") || def.getItemName().endsWith("plateskirt") || def.getItemName().endsWith("sq shield") || def.getItemName().endsWith("kiteshield")) {

                } else if (def.getItemName().endsWith("knife") || def.getItemName().endsWith("thrownaxe") || def.getItemName().endsWith("dart") || def.getItemName().endsWith("javelin")) {

                }
            } else if (def.getItemName().startsWith("Abyssal")) {

            } else if (def.getItemName().endsWith("shortbow") || def.getItemName().equals("longbow")) {
                if (def.getItemName().startsWith("Oak")) {

                } else if (def.getItemName().startsWith("Willow")) {

                } else if (def.getItemName().startsWith("Maple")) {

                } else if (def.getItemName().startsWith("Yew")) {

                } else if (def.getItemName().startsWith("Magic")) {

                }
            }
        }
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

        public SkillRequirement(int requiredLevel, int skillId) {
            this.requiredLevel = requiredLevel;
            this.skillId = skillId;
        }

        /**
         * @return the requiredLevel
         */
        public int getRequiredLevel() {
            return requiredLevel;
        }

        /**
         * @return the skillId
         */
        public int getSkillId() {
            return skillId;
        }
    }
}
