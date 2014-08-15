package com.asteria.world.entity;

import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.PlayerMagicStaff;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;

/**
 * A parent class represented by any generic spell able to be cast by an
 * {@link Entity}.
 * 
 * @author lare96
 */
public abstract class Spell {

    /**
     * Determines if this spell is able to be cast by the argued {@link Player}.
     * We do not include {@link Npc}s here since no checks need to be made for
     * them when they cast a spell.
     * 
     * @param player
     *            the player casting the spell.
     * @return <code>true</code> if the spell can be cast by the player,
     *         <code>false</code> otherwise.
     */
    // TODO: Add support for combination runes.
    public boolean canCast(Player player) {

        // We first check the level required.
        if (player.getSkills()[Skills.MAGIC].getLevel() < levelRequired()) {
            player.getPacketBuilder().sendMessage(
                    "You need a Magic level of " + levelRequired()
                            + " to cast this spell.");
            player.getCombatBuilder().reset();
            return false;
        }

        // Then we check the items required.
        if (itemsRequired(player) != null) {

            // Suppress the runes based on the staff, we then use the new array
            // of items that don't include suppressed runes.
            Item[] items = PlayerMagicStaff.suppressRunes(player,
                    itemsRequired(player));

            // Now check if we have all of the runes.
            if (!player.getInventory().containsAll(items)) {

                // We don't, so we can't cast.
                player.getPacketBuilder()
                        .sendMessage(
                                "You do not have the required items to cast this spell.");
                resetPlayerSpell(player);
                player.getCombatBuilder().reset();
                return false;
            }

            // We've made it through the checks, so we have the items and can
            // remove them now.
            player.getInventory().remove(items);
        }

        // Finally, we check the equipment required.
        if (equipmentRequired(player) != null) {
            if (!player.getEquipment()
                    .containsAll(equipmentRequired(player))) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You do not have the required equipment to cast this spell.");
                resetPlayerSpell(player);
                player.getCombatBuilder().reset();
                return false;
            }
        }
        return true;
    }

    /**
     * Resets the argued player's autocasting if they're currently in combat.
     * 
     * @param player
     *            the player to reset.
     */
    // To prevent a bit of boilerplate code.
    private void resetPlayerSpell(Player player) {
        if (player.getCombatBuilder().isAttacking()
                || player.getCombatBuilder().isBeingAttacked()
                && player.isAutocast()) {
            player.setAutocastSpell(null);
            player.setAutocast(false);
            player.getPacketBuilder().sendConfig(108, 0);
            player.setCastSpell(null);
        }
    }

    /**
     * The level required to cast this spell.
     * 
     * @return the level required to cast this spell.
     */
    public abstract int levelRequired();

    /**
     * The base experience given when this spell is cast.
     * 
     * @return the base experience given when this spell is cast.
     */
    public abstract int baseExperience();

    /**
     * The items required to cast this spell.
     * 
     * @param player
     *            the player's inventory to check for these items.
     * 
     * @return the items required to cast this spell, or <code>null</code> if
     *         there are no items required.
     */
    public abstract Item[] itemsRequired(Player player);

    /**
     * The equipment required to cast this spell.
     * 
     * @param player
     *            the player's equipment to check for these items.
     * 
     * @return the equipment required to cast this spell, or <code>null</code>
     *         if there is no equipment required.
     */
    public abstract Item[] equipmentRequired(Player player);

    /**
     * The method invoked when the spell is cast.
     * 
     * @param cast
     *            the entity casting the spell.
     * @param castOn
     *            the target of the spell.
     */
    public abstract void startCast(Entity cast, Entity castOn);
}
