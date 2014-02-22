package server.core.net.packet.impl;

import server.Main;
import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.util.Misc;
import server.world.entity.Gfx;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;
import server.world.entity.player.content.TradeSession.TradeStage;
import server.world.entity.player.skill.impl.Cooking;
import server.world.entity.player.skill.impl.Runecrafting;
import server.world.entity.player.skill.impl.Runecrafting.Altar;
import server.world.map.Position;

/**
 * Sent when the player clicks an action button.
 * 
 * @author lare96
 */
public class DecodeClickButtonPacket extends PacketDecoder {

    @Override
    public void decode(Player player, PacketBuffer.ReadBuffer in) {
        int buttonId = Misc.hexToInt(in.readBytes(2));

        switch (buttonId) {
            case 3146:
                player.setTwoClickMouse(false);
                break;
            case 3145:
                player.setTwoClickMouse(true);
                break;

            /** Teleports. */
            case 50235:
            case 4140:
                player.teleport(new Position(3094, 3243));
                break;
            case 50245:
            case 4143:
                NpcDialogue.fiveOptions(player, "Cat Pits", "Barrows", "Fight Pits", "Pest Control", "More");
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
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.THICK_SKIN);
                break;
            case 21234:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.BURST_OF_STRENGTH);
                break;
            case 21235:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.CLARITY_OF_THOUGHT);
                break;
            case 21236:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.ROCK_SKIN);
                break;
            case 21237:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.SUPERHUMAN_STRENGTH);
                break;
            case 21238:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.IMPROVED_REFLEXES);
                break;
            case 21239:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RAPID_RESTORE);
                break;
            case 21240:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RAPID_HEAL);
                break;
            case 21241:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_ITEM);
                break;
            case 21242:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.STEEL_SKIN);
                break;
            case 21243:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.ULTIMATE_STRENGTH);
                break;
            case 21244:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.INCREDIBLE_REFLEXES);
                break;
            case 21245:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MAGIC);
                break;
            case 21246:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MISSILES);
                break;
            case 21247:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.PROTECT_FROM_MELEE);
                break;
            case 2171:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.RETRIBUTION);
                break;
            case 2172:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.REDEMPTION);
                break;
            case 2173:
                // Prayer.getSingleton().activatePrayer(player,
                // CombatPrayer.SMITE);
                break;
            /** End of Prayers */

            case 150:
                player.setAutoRetaliate(true);
                player.getPacketBuilder().sendMessage("Auto retaliate has been turned on!");
                break;
            case 151:
                player.setAutoRetaliate(false);
                player.getPacketBuilder().sendMessage("Auto retaliate has been turned off!");
                break;
            case 56109:
                switch (player.getOption()) {
                    case 1:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Runecrafting.RUNE_ESSENCE_MINE);
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 6:
                        player.getPacketBuilder().closeWindows();
                        player.heal(99);
                        player.getPacketBuilder().sendMessage("You feel a magical aura pass through your body.");
                        player.gfx(new Gfx(436));
                        break;
                    case 7:
                        // start security tapes
                        break;
                }
                break;
            case 56110:
                switch (player.getOption()) {
                    case 1:
                        NpcDialogue.fiveOptions(player, "Air", "Mind", "Water", "Earth", "Next");
                        player.setOption(2);
                        break;
                    case 6:
                    case 7:
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;

            case 32017:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.DEATH.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32018:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.BLOOD.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32019:
                switch (player.getOption()) {
                    case 5:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.SOUL.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32020:
                switch (player.getOption()) {
                    case 5:
                        NpcDialogue.fiveOptions(player, "Chaos", "Nature", "Law", "Next", "Previous");
                        player.setOption(4);
                        break;
                }
                break;

            case 32029:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.AIR.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.FIRE.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.CHAOS.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32030:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.MIND.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.BODY.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.NATURE.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32031:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.WATER.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 3:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.COSMIC.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 4:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.LAW.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                }
                break;
            case 32032:
                switch (player.getOption()) {
                    case 2:
                        Runecrafting.getSingleton().teleport(player, player.getRunecraftingMob(), Altar.EARTH.getTeleport());
                        player.getPacketBuilder().closeWindows();
                        break;
                    case 3:
                        NpcDialogue.fiveOptions(player, "Chaos", "Nature", "Law", "Next", "Previous");
                        player.setOption(4);
                        break;
                    case 4:
                        NpcDialogue.fourOptions(player, "Death", "Blood", "Soul", "Previous");
                        player.setOption(5);
                        break;
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
                player.getSession().disconnect();
                break;
            case 153:
                player.getMovementQueue().setRunToggled(true);
                break;
            case 152:
                player.getMovementQueue().setRunToggled(false);
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
            case 53152:
                Cooking.getSingleton().cookFish(player, player.getCook(), 1);
                break;
            case 53151:
                Cooking.getSingleton().cookFish(player, player.getCook(), 5);
                break;
            case 53149:
                int i = player.getInventory().getContainer().getCount(player.getCook().getRawFishId());

                Cooking.getSingleton().cookFish(player, player.getCook(), i);
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
            default:
                Main.getLogger().info("Unhandled button: " + buttonId);
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 185 };
    }
}
