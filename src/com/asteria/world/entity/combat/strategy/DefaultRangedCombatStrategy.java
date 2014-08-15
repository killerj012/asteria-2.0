package com.asteria.world.entity.combat.strategy;

import com.asteria.util.Utility;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.Projectile;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.combat.CombatContainer;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.combat.range.CombatRangedAmmo;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.AssignWeaponInterface;
import com.asteria.world.entity.player.content.AssignWeaponInterface.FightStyle;
import com.asteria.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import com.asteria.world.item.Item;

/**
 * The default combat strategy assigned to an {@link Entity} during a ranged
 * based combat session.
 * 
 * @author lare96
 */
public class DefaultRangedCombatStrategy implements CombatStrategy {

    @Override
    public boolean canAttack(Entity entity, Entity victim) {

        // We do not need to check npcs.
        if (entity.type() == EntityType.NPC) {
            return true;
        }

        // Create the player instance.
        Player player = (Player) entity;

        // If we are using a crystal bow then we don't need to check for ammo.
        if (CombatFactory.crystalBow(player)) {
            return true;
        }

        // Check the ammo before proceeding.
        return checkAmmo(player);
    }

    @Override
    public CombatContainer attack(Entity entity, Entity victim) {
        if (entity.type() == EntityType.NPC) {
            Npc npc = (Npc) entity;
            CombatRangedAmmo ammo = CombatRangedAmmo.ADAMANT_ARROW;

            switch (npc.getNpcId()) {
            case 688:
                ammo = CombatRangedAmmo.BRONZE_ARROW;
                break;
            }

            entity.animation(new Animation(npc.getDefinition()
                    .getAttackAnimation()));
            entity.highGraphic(new Graphic(ammo.getGraphic()));
            new Projectile(entity, victim, ammo.getProjectile(),
                    ammo.getDelay(), ammo.getSpeed(), ammo.getStartHeight(),
                    ammo.getEndHeight(), 0).sendProjectile();

            return new CombatContainer(entity, victim, 1, CombatType.RANGED,
                    true);
        }

        // Create the player instance.
        Player player = (Player) entity;
        player.setRangedAmmo(null);
        player.setFireAmmo(0);

        // First start the animation.
        startAnimation(player);

        // Next we decrement the ammo.
        if (!CombatFactory.crystalBow(player)) {
            decrementAmmo(player);
        }

        // Then send the projectiles.
        CombatRangedAmmo ammo = CombatRangedAmmo.getPlayerAmmo(player);
        player.setRangedAmmo(ammo);

        if (!player.isSpecialActivated()) {
            player.highGraphic(new Graphic(ammo.getGraphic()));
            new Projectile(player, victim, ammo.getProjectile(),
                    ammo.getDelay(), ammo.getSpeed(), ammo.getStartHeight(),
                    ammo.getEndHeight(), 0).sendProjectile();
        }

        // And finally create the combat container.
        return new CombatContainer(entity, victim, 1, CombatType.RANGED, true);
    }

    @Override
    public int attackDelay(Entity entity) {

        // Get the attack speed implementation.
        return entity.getAttackSpeed();
    }

    @Override
    public int attackDistance(Entity entity) {

        // The default distance for all npcs using ranged is 6.
        if (entity.type() == EntityType.NPC) {
            return 6;
        }

        // Create the player instance.
        Player player = (Player) entity;

        // Set the distance for different ranged weapon types.
        int distance = 0;
        switch (player.getWeapon()) {
        case DART:
        case THROWNAXE:
            distance = 4;
            break;
        case KNIFE:
        case JAVELIN:
            distance = 5;
            break;
        case CROSSBOW:
        case LONGBOW:
            distance = 8;
            break;
        case SHORTBOW:
            distance = 7;
            break;
        default:
            throw new IllegalStateException();
        }

        return distance + (player.getFightType().getStyle() == FightStyle.DEFENSIVE ? 2
                : 0);
    }

    /**
     * Starts the animation for the argued {@link Player} in the current combat
     * hook.
     * 
     * @param player
     *            the player to start the animation for.
     */
    private void startAnimation(Player player) {
        if (player.getEquipment()
                .get(Utility.EQUIPMENT_SLOT_WEAPON).getDefinition()
                .getItemName().startsWith("Karils")) {
            player.animation(new Animation(2075));
        } else {
            player.animation(new Animation(player.getFightType().getAnimation()));
        }
    }

    /**
     * Checks the ammo to make sure the argued {@link Player} has the right type
     * and amount before attacking.
     * 
     * @param player
     *            the player's ammo to check.
     * @return <code>true</code> if the player has the right ammo,
     *         <code>false</code> otherwise.
     */
    private boolean checkAmmo(Player player) {

        // Get the item in the arrows slot.
        Item item = player.getEquipment()
                .get(Utility.EQUIPMENT_SLOT_ARROWS);

        // Check if we have an item in the arrows slot.
        if (item == null) {
            player.getPacketBuilder().sendMessage(
                    "You do not have any ammo in your quiver.");
            player.getCombatBuilder().reset();
            return false;
        }

        // Check the arrows for each type of ranged weapon.
        if (player.getWeapon() == WeaponInterface.SHORTBOW || player
                .getWeapon() == WeaponInterface.LONGBOW) {
            if (!CombatFactory.arrowsEquipped(player)) {
                player.getPacketBuilder().sendMessage(
                        "You need to use arrows with your bow.");
                player.getCombatBuilder().reset();
                return false;
            }
        } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
            if (player.getEquipment()
                    .get(Utility.EQUIPMENT_SLOT_WEAPON).getDefinition()
                    .getItemName().startsWith("Karils") && !item
                    .getDefinition().getItemName().endsWith("rack")) {
                player.getPacketBuilder().sendMessage(
                        "You need to use bolt racks with this crossbow.");
                player.getCombatBuilder().reset();
                return false;
            } else if (!player.getEquipment()
                    .get(Utility.EQUIPMENT_SLOT_WEAPON).getDefinition()
                    .getItemName().startsWith("Karils") && !CombatFactory
                    .boltsEquipped(player)) {
                player.getPacketBuilder().sendMessage(
                        "You need to use bolts with your crossbow.");
                player.getCombatBuilder().reset();
                return false;
            }
        }
        return true;
    }

    /**
     * Decrements the amount ammo the {@link Player} currently has equipped.
     * 
     * @param player
     *            the player to decrement ammo for.
     */
    private void decrementAmmo(Player player) {

        // Determine which slot we are decrementing ammo from.
        int slot = player.getWeapon() == WeaponInterface.SHORTBOW || player
                .getWeapon() == WeaponInterface.LONGBOW || player.getWeapon() == WeaponInterface.CROSSBOW ? Utility.EQUIPMENT_SLOT_ARROWS
                : Utility.EQUIPMENT_SLOT_WEAPON;

        // Set the ammo we are currently using.
        player.setFireAmmo(player.getEquipment().get(slot)
                .getId());

        // Decrement the ammo in the selected slot.
        player.getEquipment().get(slot).decrementAmount();

        if (slot == Utility.EQUIPMENT_SLOT_WEAPON) {
            player.getFlags().flag(Flag.APPEARANCE);
        }

        // If we are at 0 ammo remove the item from the equipment completely.
        if (player.getEquipment().get(slot).getAmount() == 0) {
            player.getPacketBuilder().sendMessage(
                    "That was your last piece of ammo!");
            player.getEquipment().set(slot, null);

            if (slot == Utility.EQUIPMENT_SLOT_WEAPON) {
                AssignWeaponInterface.assignInterface(player, null);
                AssignWeaponInterface.changeFightType(player);
            }
        }

        // Refresh the equipment interface.
        player.getEquipment().refresh();
    }
}
