package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillEvent;

/**
 * Sent when the player sends another player some sort of request using the
 * <code>sendMessage</code> method.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 139 })
public class DecodeRequestPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int requestId = in.readShort(true, ByteOrder.LITTLE);
        Player request = World.getPlayers().get(requestId);

        /** Make sure the player to is real and close to us. */
        if (request == null || !request.getPosition().isViewableFrom(player.getPosition())) {
            return;
        }

        SkillEvent.fireSkillEvents(player);

        switch (player.getSession().getPacketOpcode()) {
            case 139:

                /** Check if we can trade based on the minigame we're in. */
                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        if (!minigame.canTrade(player, request)) {
                            return;
                        }
                    }
                }

                player.getTradeSession().request(request);
                break;

        }
    }
}
