package server.world.entity.combat.strategy;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.EntityType;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.Projectile;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.range.CombatRangedAmmo;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.content.AssignWeaponInterface.FightStyle;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.item.Item;

/**
 * The default combat strategy assigned to an entity during a ranged based
 * combat session. NPCs with ranged attacks should not be assigned this combat
 * strategy but instead have an individualized combat strategy dedicated to
 * them.
 * 
 * @author lare96
 */
public class DefaultRangedCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {

        /** If the entity is a player we need to check for ammo. */
        if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            Item arrowItem = player.getEquipment().getContainer()
                    .getItem(Misc.EQUIPMENT_SLOT_ARROWS);

            if (player.getWeapon() == WeaponInterface.SHORTBOW
                    || player.getWeapon() == WeaponInterface.LONGBOW) {
                if (player.getEquipment().getContainer()
                        .getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition()
                        .getItemName().startsWith("Crystal bow")
                        || player.getEquipment().getContainer()
                                .getItem(Misc.EQUIPMENT_SLOT_WEAPON)
                                .getDefinition().getItemName()
                                .endsWith("crystal bow")) {
                    return true;
                }

                if (arrowItem == null) {
                    player.getPacketBuilder().sendMessage(
                            "You do not have any ammo in your quiver.");
                    player.getCombatBuilder().reset();
                    player.faceEntity(65535);
                    player.getFollowWorker().cancel();
                    player.setFollowing(false);
                    player.setFollowingEntity(null);
                    return false;
                }

                if (!arrowItem.getDefinition().getItemName().endsWith("arrow")
                        && !arrowItem.getDefinition().getItemName()
                                .endsWith("arrow(p)")
                        && !arrowItem.getDefinition().getItemName()
                                .endsWith("arrow(p+)")
                        && !arrowItem.getDefinition().getItemName()
                                .endsWith("arrow(p++)")) {
                    player.getPacketBuilder().sendMessage(
                            "You need to use arrows with your bow.");
                    player.getCombatBuilder().reset();
                    player.faceEntity(65535);
                    player.getFollowWorker().cancel();
                    player.setFollowing(false);
                    player.setFollowingEntity(null);
                    return false;
                }
            } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
                if (arrowItem == null) {
                    player.getPacketBuilder().sendMessage(
                            "You do not have any ammo in your quiver.");
                    player.getCombatBuilder().reset();
                    player.faceEntity(65535);
                    player.getFollowWorker().cancel();
                    player.setFollowing(false);
                    player.setFollowingEntity(null);
                    return false;
                }

                if (!player.getEquipment().getContainer()
                        .getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition()
                        .getItemName().startsWith("Karils")) {
                    if (!arrowItem.getDefinition().getItemName()
                            .endsWith("bolts")
                            && !arrowItem.getDefinition().getItemName()
                                    .endsWith("bolts(p)")
                            && !arrowItem.getDefinition().getItemName()
                                    .endsWith("bolts(p+)")
                            && !arrowItem.getDefinition().getItemName()
                                    .endsWith("bolts(p++)")) {
                        player.getPacketBuilder().sendMessage(
                                "You need to use bolts with your crossbow.");
                        player.getCombatBuilder().reset();
                        player.faceEntity(65535);
                        player.getFollowWorker().cancel();
                        player.setFollowing(false);
                        player.setFollowingEntity(null);
                        return false;
                    }
                } else {
                    if (!arrowItem.getDefinition().getItemName()
                            .endsWith("rack")) {
                        player.getPacketBuilder()
                                .sendMessage(
                                        "You need to use bolt racks with this crossbow.");
                        player.getCombatBuilder().reset();
                        player.faceEntity(65535);
                        player.getFollowWorker().cancel();
                        player.setFollowing(false);
                        player.setFollowingEntity(null);
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public CombatHitContainer attack(Entity entity, Entity victim) {

        /**
         * If the entity is a player we need to decrement and fire projectiles
         * and gfx's based on the current ammo they have equipped.
         */
        if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            player.animation(new Animation(player.getFightType().getAnimation()));
            CombatRangedAmmo ammo = CombatRangedAmmo.getAmmo(player);

            if (player.getEquipment().getContainer()
                    .getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition()
                    .getItemName().startsWith("Karils")) {
                player.animation(new Animation(2075));
            } else {
                player.animation(new Animation(player.getFightType()
                        .getAnimation()));
            }

            if (player.getWeapon() == WeaponInterface.SHORTBOW
                    || player.getWeapon() == WeaponInterface.LONGBOW
                    || player.getWeapon() == WeaponInterface.CROSSBOW) {
                if (player.getEquipment().getContainer()
                        .getItem(Misc.EQUIPMENT_SLOT_ARROWS).getAmount() == 1) {
                    player.getPacketBuilder().sendMessage(
                            "That was your last piece of ammo!");
                    player.getEquipment().getContainer()
                            .set(Misc.EQUIPMENT_SLOT_ARROWS, null);
                } else {
                    player.setFireAmmo(player.getEquipment().getContainer()
                            .getItem(Misc.EQUIPMENT_SLOT_ARROWS).getId());
                    player.getEquipment().getContainer()
                            .getItem(Misc.EQUIPMENT_SLOT_ARROWS)
                            .decrementAmount();
                }
            } else {
                if (player.getEquipment().getContainer()
                        .getItem(Misc.EQUIPMENT_SLOT_WEAPON).getAmount() == 1) {
                    player.getPacketBuilder().sendMessage(
                            "That was your last piece of ammo!");
                    player.getEquipment().getContainer()
                            .set(Misc.EQUIPMENT_SLOT_WEAPON, null);
                    AssignWeaponInterface.reset(player);
                    AssignWeaponInterface.changeFightType(player);
                    player.getFlags().flag(Flag.APPEARANCE);
                } else {
                    player.setFireAmmo(player.getEquipment().getContainer()
                            .getItem(Misc.EQUIPMENT_SLOT_WEAPON).getId());
                    player.getEquipment().getContainer()
                            .getItem(Misc.EQUIPMENT_SLOT_WEAPON)
                            .decrementAmount();
                }
            }

            player.getEquipment().refresh();
            player.setRangedAmmo(ammo);
            if (!player.isSpecialActivated()) {
                player.gfx(new Gfx(ammo.getGraphicId(), 6553600));
                new Projectile(player, victim, ammo.getProjectileId(),
                        ammo.getDelay(), ammo.getSpeed(),
                        ammo.getStartHeight(), ammo.getEndHeight(), 0)
                        .sendProjectile();
                return new CombatHitContainer(
                        new Hit[] { CombatFactory.getRangeHit(entity, ammo) },
                        CombatType.RANGE, true) {
                    @Override
                    public void onHit(Entity attacker, Entity victim,
                            int damage, boolean accurate) {
                    }
                };
            }
            return player.getCombatSpecial().getSpecialStrategy()
                    .calculateHit(player, victim);
        }
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {

        /** Get the attack speed implementation. */
        return entity.getAttackSpeed();
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public int getDistance(Entity entity) {

        /**
         * If the entity is player then get the appropriate distance based on
         * the weapon being used.
         */
        if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            int distance = 0;

            switch (player.getWeapon()) {
            case DART:
                distance = 4;
                break;
            case THROWNAXE:
                distance = 4;
                break;
            case KNIFE:
                distance = 5;
                break;
            case JAVELIN:
                distance = 5;
                break;
            case CROSSBOW:
                distance = 8;
                break;
            case SHORTBOW:
                distance = 7;
                break;
            case LONGBOW:
                distance = 8;
                break;
            }

            /** Increase the distance when using longranged. */
            if (player.getFightType().getStyle() == FightStyle.DEFENSIVE) {
                distance += 2;
            }

            return distance;
        }
        return 6;
    }
}
