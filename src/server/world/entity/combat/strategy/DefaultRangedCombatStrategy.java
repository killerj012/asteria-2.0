package server.world.entity.combat.strategy;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.data.RangedAmmo;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.FightStyle;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.item.Item;

public class DefaultRangedCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            Item arrowItem = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_ARROWS);

            if (player.getWeapon() == WeaponInterface.SHORTBOW || player.getWeapon() == WeaponInterface.LONGBOW) {
                if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().getItemName().startsWith("Crystal bow") || player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().getItemName().endsWith("crystal bow")) {
                    return true;
                }

                if (arrowItem == null) {
                    player.getPacketBuilder().sendMessage("You do not have any ammo in your quiver.");
                    return false;
                }

                if (!arrowItem.getDefinition().getItemName().endsWith("arrow") && !arrowItem.getDefinition().getItemName().endsWith("arrow(p)") && !arrowItem.getDefinition().getItemName().endsWith("arrow(p+)") && !arrowItem.getDefinition().getItemName().endsWith("arrow(p++)")) {
                    player.getPacketBuilder().sendMessage("You need to use arrows with your bow.");
                    return false;
                }
            } else if (player.getWeapon() == WeaponInterface.CROSSBOW) {
                if (arrowItem == null) {
                    player.getPacketBuilder().sendMessage("You do not have any ammo in your quiver.");
                    return false;
                }

                if (!player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().getItemName().startsWith("Karils")) {
                    if (!arrowItem.getDefinition().getItemName().endsWith("bolts") && !arrowItem.getDefinition().getItemName().endsWith("bolts(p)") && !arrowItem.getDefinition().getItemName().endsWith("bolts(p+)") && !arrowItem.getDefinition().getItemName().endsWith("bolts(p++)")) {
                        player.getPacketBuilder().sendMessage("You need to use bolts with your crossbow.");
                        return false;
                    }
                } else {
                    if (!arrowItem.getDefinition().getItemName().endsWith("rack")) {
                        player.getPacketBuilder().sendMessage("You need to use bolt racks with this crossbow.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            player.animation(new Animation(player.getFightType().getAnimation()));

            if (player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON).getDefinition().getItemName().startsWith("Karils")) {
                player.animation(new Animation(2075));
            } else {
                player.animation(new Animation(player.getFightType().getAnimation()));
            }

            if (CombatFactory.hitAccuracy(entity, victim, CombatType.RANGE, 1)) {
                return new CombatHit(new Hit[] { CombatFactory.getRangeHit(entity, RangedAmmo.getAmmo(player)) }, CombatType.RANGE);
            }
        } else if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            RangedAmmo npcAmmo = RangedAmmo.MITHRIL_ARROW;

            npc.animation(new Animation(npc.getDefinition().getAttackAnimation()));

            if (CombatFactory.hitAccuracy(entity, victim, CombatType.RANGE, 1)) {
                return new CombatHit(new Hit[] { CombatFactory.getRangeHit(entity, npcAmmo) }, CombatType.RANGE);
            }
        }
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {
        return entity.getAttackSpeed();
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public int getDistance(Entity entity) {
        if (entity.isNpc()) {
            return 6;
        }

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

        if (player.getFightType().getStyle() == FightStyle.DEFENSIVE) {
            distance += 2;
        }

        return distance;
    }

}
