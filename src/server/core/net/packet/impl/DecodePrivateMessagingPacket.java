package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.player.Player;

/**
 * Sent when the player adds a friend/ignore, removes a friend/ignore or sends a
 * private message.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 188, 215, 133, 74, 126 })
public class DecodePrivateMessagingPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        switch (player.getSession().getPacketOpcode()) {
        case 188:
            long name = in.readLong();
            player.getPrivateMessage().addFriend(name);
            break;
        case 215:
            name = in.readLong();
            player.getPrivateMessage().removeFriend(name);
            break;
        case 133:
            name = in.readLong();
            player.getPrivateMessage().addIgnore(name);
            break;
        case 74:
            name = in.readLong();
            player.getPrivateMessage().removeIgnore(name);
            break;
        case 126:
            long to = in.readLong();
            int size = player.getSession().getPacketLength() - 8;
            byte[] message = in.readBytes(size);

            if (!player.getFriends().contains(to)) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You cannot send a message to a player not on your friends list!");
                return;
            }

            player.getPrivateMessage().sendPrivateMessage(player, to, message,
                    size);
            break;
        }
    }
}
