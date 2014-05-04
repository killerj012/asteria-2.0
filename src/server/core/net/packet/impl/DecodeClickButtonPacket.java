package server.core.net.packet.impl;

import java.util.logging.Logger;

import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.FightType;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.entity.player.content.TradeSession.TradeStage;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.map.Position;

/**
 * Sent when the player clicks an action button.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 185 })
public class DecodeClickButtonPacket extends PacketDecoder {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(DecodeClickButtonPacket.class.getSimpleName());

    @Override
    public void decode(Player player, PacketBuffer.ReadBuffer in) {
        int buttonId = Misc.hexToInt(in.readBytes(2));

        switch (buttonId) {

            /** Teleports. */
            case 50235:
            case 4140:
                player.teleport(new Position(3094, 3243));
                break;
            // case 4146: // falador
            // player.teleport(new Position(2964, 3378));
            // break;
            // case 4150: // camelot
            // player.teleport(new Position(2757, 3477));
            // break;
            // case 6004: // ardougne
            // player.teleport(new Position(2529, 3307));
            // break;
            // case 6005: // watchtower
            // player.teleport(new Position(2951, 3087));
            // break;
            // case 29031: // trollheim
            // player.teleport(new Position(2892, 3670));
            // break;
            // case 72038: // ape atoll
            // player.teleport(new Position(2755, 2784));
            // break;
            // case 50253: // kharyrll
            //
            // break;
            // case 51005: // lassar
            //
            // break;
            // case 51013: // dareeyak
            //
            // break;
            // case 51023: // carrallangar
            //
            // break;
            // case 51031: // annakarl
            //
            // break;
            // case 51039: // ghorrock
            //
            // break;
            /** Prayers */
            case 21233:
                CombatPrayer.THICK_SKIN.activatePrayer(player, true);
                break;
            case 21234:
                CombatPrayer.BURST_OF_STRENGTH.activatePrayer(player, true);
                break;
            case 21235:
                CombatPrayer.CLARITY_OF_THOUGHT.activatePrayer(player, true);
                break;
            case 21236:
                CombatPrayer.ROCK_SKIN.activatePrayer(player, true);
                break;
            case 21237:
                CombatPrayer.SUPERHUMAN_STRENGTH.activatePrayer(player, true);
                break;
            case 21238:
                CombatPrayer.IMPROVED_REFLEXES.activatePrayer(player, true);
                break;
            case 21239:
                CombatPrayer.RAPID_RESTORE.activatePrayer(player, true);
                break;
            case 21240:
                CombatPrayer.RAPID_HEAL.activatePrayer(player, true);
                break;
            case 21241:
                CombatPrayer.PROTECT_ITEM.activatePrayer(player, true);
                break;
            case 21242:
                CombatPrayer.STEEL_SKIN.activatePrayer(player, true);
                break;
            case 21243:
                CombatPrayer.ULTIMATE_STRENGTH.activatePrayer(player, true);
                break;
            case 21244:
                CombatPrayer.INCREDIBLE_REFLEXES.activatePrayer(player, true);
                break;
            case 21245:
                CombatPrayer.PROTECT_FROM_MAGIC.activatePrayer(player, true);
                break;
            case 21246:
                CombatPrayer.PROTECT_FROM_MISSILES.activatePrayer(player, true);
                break;
            case 21247:
                CombatPrayer.PROTECT_FROM_MELEE.activatePrayer(player, true);
                break;
            case 2171:
                CombatPrayer.RETRIBUTION.activatePrayer(player, true);
                break;
            case 2172:
                CombatPrayer.REDEMPTION.activatePrayer(player, true);
                break;
            case 2173:
                CombatPrayer.SMITE.activatePrayer(player, true);
                break;
            /** End of Prayers */

            case 150:
                if (!player.isAutoRetaliate()) {
                    player.setAutoRetaliate(true);
                    player.getPacketBuilder().sendMessage("Auto retaliate has been turned on!");
                }
                break;
            case 151:
                if (player.isAutoRetaliate()) {
                    player.setAutoRetaliate(false);
                    player.getPacketBuilder().sendMessage("Auto retaliate has been turned off!");
                }
                break;
            case 56109:
                switch (player.getOption()) {

                }
                break;
            case 56110:
                switch (player.getOption()) {

                }
                break;

            case 32017:
                switch (player.getOption()) {

                }
                break;
            case 32018:
                switch (player.getOption()) {

                }
                break;
            case 32019:
                switch (player.getOption()) {

                }
                break;
            case 32020:
                switch (player.getOption()) {

                }
                break;

            case 32029:
                switch (player.getOption()) {

                }
                break;
            case 32030:
                switch (player.getOption()) {

                }
                break;
            case 32031:
                switch (player.getOption()) {

                }
                break;
            case 32032:
                switch (player.getOption()) {

                }
                break;
            case 32033:
                switch (player.getOption()) {
                    case 2:
                        NpcDialogue.fiveOptions(player, "Fire", "Body", "Cosmic", "Next", "Previous");
                        player.setOption(3);
                        break;
                    case 3:
                        NpcDialogue.fiveOptions(player, "Air", "Mind", "Water", "Earth", "Next");
                        player.setOption(2);
                        break;
                    case 4:
                        NpcDialogue.fiveOptions(player, "Fire", "Body", "Cosmic", "Next", "Previous");
                        player.setOption(3);
                        break;
                }
                break;

            case 9154:
                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        if (!minigame.canFormalLogout(player)) {
                            return;
                        }
                    }
                }

                if (player.getLastCombat().elapsed() <= 10000) {
                    player.getPacketBuilder().sendMessage("You must wait 10 seconds after combat before logging out.");
                    return;
                }

                player.getSession().disconnect();
                break;
            case 153:
                if (player.getRunEnergy() == 0) {
                    return;
                }

                player.getMovementQueue().setRunToggled(true);
                player.getPacketBuilder().sendConfig(173, 1);
                break;
            case 152:
                player.getMovementQueue().setRunToggled(false);
                player.getPacketBuilder().sendConfig(173, 0);
                break;
            case 21011:
                player.setWithdrawAsNote(false);
                break;
            case 21010:
                player.setWithdrawAsNote(true);
                break;
            case 31195:
                player.setInsertItem(true);
                break;
            case 31194:
                player.setInsertItem(false);
                break;
            case 13092:
                Player partner = player.getTradeSession().getPartner();

                if (partner.getInventory().getContainer().freeSlots() < player.getTradeSession().getOffering().size()) {
                    player.getPacketBuilder().sendMessage(partner.getUsername() + " does not have enough free slots for this many items.");
                    return;
                }

                player.getTradeSession().setAcceptInitialOffer(true);
                player.getPacketBuilder().sendString("Waiting for other player...", 3431);
                partner.getPacketBuilder().sendString("Other player has accepted", 3431);

                if (player.getTradeSession().isAcceptInitialOffer() && partner.getTradeSession().isAcceptInitialOffer()) {
                    player.getTradeSession().setStage(TradeStage.CONFIRM_OFFER);
                    partner.getTradeSession().setStage(TradeStage.CONFIRM_OFFER);

                    player.getTradeSession().confirmTrade();
                    partner.getTradeSession().confirmTrade();
                }
                break;
            case 13218:
                partner = player.getTradeSession().getPartner();

                player.getTradeSession().setAcceptConfirmOffer(true);
                partner.getPacketBuilder().sendString("Other player has accepted.", 3535);
                player.getPacketBuilder().sendString("Waiting for other player...", 3535);

                if (player.getTradeSession().isAcceptConfirmOffer() && partner.getTradeSession().isAcceptConfirmOffer()) {
                    player.getTradeSession().finishTrade();
                }
                break;

            /** Lots of fight types! */
            case 1080: // staff
                player.setFightType(FightType.STAFF_BASH);
                break;
            case 1079:
                player.setFightType(FightType.STAFF_POUND);
                break;
            case 1078:
                player.setFightType(FightType.STAFF_FOCUS);
                break;
            case 1177: // warhammer
                player.setFightType(FightType.WARHAMMER_POUND);
                break;
            case 1176:
                player.setFightType(FightType.WARHAMMER_PUMMEL);
                break;
            case 1175:
                player.setFightType(FightType.WARHAMMER_BLOCK);
                break;
            case 3014: // scythe
                player.setFightType(FightType.SCYTHE_REAP);
                break;
            case 3017:
                player.setFightType(FightType.SCYTHE_CHOP);
                break;
            case 3016:
                player.setFightType(FightType.SCYTHE_JAB);
                break;
            case 3015:
                player.setFightType(FightType.SCYTHE_BLOCK);
                break;
            case 6168: // battle axe
                player.setFightType(FightType.BATTLEAXE_CHOP);
                break;
            case 6171:
                player.setFightType(FightType.BATTLEAXE_HACK);
                break;
            case 6170:
                player.setFightType(FightType.BATTLEAXE_SMASH);
                break;
            case 6169:
                player.setFightType(FightType.BATTLEAXE_BLOCK);
                break;
            case 6221: // crossbow
                player.setFightType(FightType.CROSSBOW_ACCURATE);
                break;
            case 6220:
                player.setFightType(FightType.CROSSBOW_RAPID);
                break;
            case 6219:
                player.setFightType(FightType.CROSSBOW_LONGRANGE);
                break;
            case 6236: // shortbow & longbow
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_ACCURATE);
                }
                break;
            case 6235:
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_RAPID);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_RAPID);
                }
                break;
            case 6234:
                if (player.getWeapon() == WeaponInterface.SHORTBOW) {
                    player.setFightType(FightType.SHORTBOW_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.LONGBOW) {
                    player.setFightType(FightType.LONGBOW_LONGRANGE);
                }
                break;
            case 8234: // dagger & sword
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_STAB);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_STAB);
                }
                break;
            case 8237:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_LUNGE);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_LUNGE);
                }
                break;
            case 8236:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_SLASH);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_SLASH);
                }
                break;
            case 8235:
                if (player.getWeapon() == WeaponInterface.DAGGER) {
                    player.setFightType(FightType.DAGGER_BLOCK);
                } else if (player.getWeapon() == WeaponInterface.SWORD) {
                    player.setFightType(FightType.SWORD_BLOCK);
                }
                break;
            case 9125: // scimitar & longsword
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_CHOP);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_CHOP);
                }
                break;
            case 9128:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_SLASH);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_SLASH);
                }
                break;
            case 9127:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_LUNGE);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_LUNGE);
                }
                break;
            case 9126:
                if (player.getWeapon() == WeaponInterface.SCIMITAR) {
                    player.setFightType(FightType.SCIMITAR_BLOCK);
                } else if (player.getWeapon() == WeaponInterface.LONGSWORD) {
                    player.setFightType(FightType.LONGSWORD_BLOCK);
                }
                break;
            case 14218: // mace
                player.setFightType(FightType.MACE_POUND);
                break;
            case 14221:
                player.setFightType(FightType.MACE_PUMMEL);
                break;
            case 14220:
                player.setFightType(FightType.MACE_SPIKE);
                break;
            case 14219:
                player.setFightType(FightType.MACE_BLOCK);
                break;
            case 17102: // knife, thrownaxe, dart & javelin
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_ACCURATE);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_ACCURATE);
                }
                break;
            case 17101:
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_RAPID);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_RAPID);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_RAPID);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_RAPID);
                }
                break;
            case 17100:
                if (player.getWeapon() == WeaponInterface.KNIFE) {
                    player.setFightType(FightType.KNIFE_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.THROWNAXE) {
                    player.setFightType(FightType.THROWNAXE_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.DART) {
                    player.setFightType(FightType.DART_LONGRANGE);
                } else if (player.getWeapon() == WeaponInterface.JAVELIN) {
                    player.setFightType(FightType.JAVELIN_LONGRANGE);
                }
                break;
            case 18077: // spear
                player.setFightType(FightType.SPEAR_LUNGE);
                break;
            case 18080:
                player.setFightType(FightType.SPEAR_SWIPE);
                break;
            case 18079:
                player.setFightType(FightType.SPEAR_POUND);
                break;
            case 18078:
                player.setFightType(FightType.SPEAR_BLOCK);
                break;
            case 18103: // 2h sword
                player.setFightType(FightType.TWOHANDEDSWORD_CHOP);
                break;
            case 15106:
                player.setFightType(FightType.TWOHANDEDSWORD_SLASH);
                break;
            case 18105:
                player.setFightType(FightType.TWOHANDEDSWORD_SMASH);
                break;
            case 18104:
                player.setFightType(FightType.TWOHANDEDSWORD_BLOCK);
                break;
            case 21200: // pickaxe
                player.setFightType(FightType.PICKAXE_SPIKE);
                break;
            case 21203:
                player.setFightType(FightType.PICKAXE_IMPALE);
                break;
            case 21202:
                player.setFightType(FightType.PICKAXE_SMASH);
                break;
            case 21201:
                player.setFightType(FightType.PICKAXE_BLOCK);
                break;
            case 30088: // claws
                player.setFightType(FightType.CLAWS_CHOP);
                break;
            case 30091:
                player.setFightType(FightType.CLAWS_SLASH);
                break;
            case 30090:
                player.setFightType(FightType.CLAWS_LUNGE);
                break;
            case 30089:
                player.setFightType(FightType.CLAWS_BLOCK);
                break;
            case 33018: // halberd
                player.setFightType(FightType.HALBERD_JAB);
                break;
            case 33017:
                player.setFightType(FightType.HALBERD_SWIPE);
                break;
            case 33016:
                player.setFightType(FightType.HALBERD_FEND);
                break;
            case 22228: // unarmed
                player.setFightType(FightType.UNARMED_PUNCH);
                break;
            case 22227:
                player.setFightType(FightType.UNARMED_KICK);
                break;
            case 22226:
                player.setFightType(FightType.UNARMED_BLOCK);
                break;
            case 48010: // whip
                player.setFightType(FightType.WHIP_FLICK);
                break;
            case 48009:
                player.setFightType(FightType.WHIP_LASH);
                break;
            case 48008:
                player.setFightType(FightType.WHIP_DEFLECT);
                break;

            default:
                logger.info("Unhandled button: " + buttonId);
                break;
        }
    }
}
