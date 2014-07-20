package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player adds a friend/ignore, removes a friend/ignore or sends a
 * private message.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 188, 215, 133, 74, 126 })
public class DecodePrivateMessagingPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        switch (player.getSession().getPacketOpcode()) {
        case 188:
            long name = buf.readLong();

            if (name < 0) {
                return;
            }

            player.getPrivateMessage().addFriend(name);
            break;
        case 215:
            name = buf.readLong();

            if (name < 0) {
                return;
            }

            player.getPrivateMessage().removeFriend(name);
            break;
        case 133:
            name = buf.readLong();

            if (name < 0) {
                return;
            }

            player.getPrivateMessage().addIgnore(name);
            break;
        case 74:
            name = buf.readLong();

            if (name < 0) {
                return;
            }

            player.getPrivateMessage().removeIgnore(name);
            break;
        case 126:
            long to = buf.readLong();
            int size = player.getSession().getPacketLength() - 8;
            byte[] message = buf.readBytes(size);

            if (to < 0 || size < 0 || message == null) {
                return;
            }

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
