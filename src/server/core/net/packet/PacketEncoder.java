package server.core.net.packet;

import java.util.concurrent.ConcurrentLinkedQueue;

import server.core.Rs2Engine;
import server.core.net.Session;
import server.core.net.buffer.PacketBuffer;
import server.core.net.buffer.PacketBuffer.AccessType;
import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.buffer.PacketBuffer.WriteBuffer;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ground.WorldItem;
import server.world.map.MapRegion;
import server.world.map.MapRegionTile;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * Queues all outgoing packets raw without the cipher key attached to the header
 * then encodes them one by one and writes them to the appropriate socket. This
 * procedure is crucial to ensure that the <code>encryptor</code> stays in
 * sync.
 * 
 * @author lare96
 */
public final class PacketEncoder {

    /**
     * A queue of raw outgoing packets that need to be encoded and sent to
     * sessions.
     */
    private static ConcurrentLinkedQueue<QueuedEncode> packets;

    /**
     * Configures the {@link PacketEncoder}.
     */
    public void configure() {
        packets = new ConcurrentLinkedQueue<QueuedEncode>();
    }

    /**
     * Terminates the {@link PacketEncoder}.
     */
    public void terminate() {
        packets.clear();
    }

    /**
     * Adds a raw outgoing packet to the queue.
     * 
     * @param packet
     *        the raw packet to encode.
     * @param session
     *        the session to encode this packet for.
     */
    public void encode(WriteBuffer packet, Session session) {
        packets.add(new QueuedEncode(packet, session));
    }

    /**
     * Encodes and sends all of the raw packets in the queue.
     */
    public void encodePackets() {
        QueuedEncode encode;

        /** Poll all of the queued packets. */
        while ((encode = packets.poll()) != null) {
            if (encode.getPacketBuffer() == null || encode.getSession().getEncryptor() == null) {
                continue;
            }

            try {
                /** Encode the header for this packet. */
                encode.getPacketBuffer().getBuffer().put(0, (byte) (encode.getPacketBuffer().getBuffer().array()[0] + encode.getSession().getEncryptor().getKey()));

                /** And send the packet! */
                encode.getSession().send(encode.getPacketBuffer().getBuffer());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A raw queued outgoing packet waiting to be encoded and written to a
     * socket.
     * 
     * @author lare96
     */
    private static final class QueuedEncode {

        /** The buffer for the packet we are encoding. */
        private WriteBuffer packetBuffer;

        /** The session we are encoding this packet for. */
        private Session session;

        /**
         * Create a new {@link QueuedEncode}.
         * 
         * @param packetBuffer
         *        the buffer for the packet we are encoding.
         * @param session
         *        the session we are encoding this packet for.
         */
        public QueuedEncode(WriteBuffer packetBuffer, Session session) {
            this.packetBuffer = packetBuffer;
            this.session = session;
        }

        /**
         * Gets the buffer for the packet we are encoding and writing.
         * 
         * @return the packet buffer.
         */
        public WriteBuffer getPacketBuffer() {
            return packetBuffer;
        }

        /**
         * Gets the session we are encoding this packet for.
         * 
         * @return the session.
         */
        public Session getSession() {
            return session;
        }
    }

    /**
     * A collection of packets sent by the server that will be read by the
     * client.
     * 
     * @author lare96
     */
    public static final class PacketBuilder {

        /**
         * The player sending these packets.
         */
        private Player player;

        /**
         * Construct a new packet builder.
         * 
         * @param player
         *        the player sending these packets.
         */
        public PacketBuilder(Player player) {
            this.player = player;
        }

        /**
         * Creates a graphic for a single player.
         * 
         * @param id
         *        the id of the graphic.
         * @param position
         *        the position of the graphic.
         * @param level
         *        the level (how high) the graphic is.
         * @return this packet builder.
         */
        public PacketBuilder sendGraphic(int id, Position position, int level) {
            sendCoordinates(position);
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(4).writeByte(0).writeShort(id).writeByte(level).writeShort(0);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Creates a graphic for everyone within viewing distance.
         * 
         * @param id
         *        the id of the graphic.
         * @param position
         *        the position of the graphic.
         * @param level
         *        the level (how high) the graphic is.
         * @return this packet builder.
         */
        public PacketBuilder sendViewableGraphic(int id, Position position, int level) {
            for (Player player : Rs2Engine.getWorld().getPlayers()) {
                if (player == null) {
                    continue;
                }

                if (position.isViewableFrom(player.getPosition())) {
                    player.getPacketBuilder().sendGraphic(id, position, level);
                }
            }
            return this;
        }

        /**
         * Plays a sound from the cache.
         * 
         * @param id
         *        the id of the sound.
         * @param type
         *        the type of sound.
         * @param duration
         *        the curation of the sound.
         * @return this packet builder.
         */
        public PacketBuilder sendSound(int id, int type, int duration) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(8);
            out.writeHeader(174).writeShort(id).writeByte(type).writeShort(duration);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the welcome interface used upon login.
         * 
         * @param recoveryChange
         *        the last time you set your recovery questions.
         * @param memberWarning
         *        if you should be warned about your membership running out.
         * @param messages
         *        the amount of messages you have.
         * @param lastLoginIP
         *        the last IP you logged in from.
         * @param lastLogin
         *        the last time you logged in.
         * @return this packet builder.
         */
        public PacketBuilder sendWelcomeInterface(int recoveryChange, boolean memberWarning, int messages, int lastLoginIP, int lastLogin) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(20);
            out.writeHeader(176).writeByte(recoveryChange, ValueType.C).writeShort(messages, ValueType.A).writeByte(memberWarning ? 1 : 0).writeInt(lastLoginIP, ByteOrder.INVERSE_MIDDLE).writeShort(lastLogin);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Plays an interface animation.
         * 
         * @param interfaceId
         *        the interface to play the animation on.
         * @param animation
         *        the animation to play.
         * @return this packet builder.
         */
        public PacketBuilder interfaceAnimation(int interfaceId, int animation) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(200).writeShort(interfaceId).writeShort(animation);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the multicombat icon.
         * 
         * @param state
         *        the state of the multicombat icon (0 = off/1 = on).
         * @return this packet builder.
         */
        public PacketBuilder sendMultiCombatInterface(int state) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2);
            out.writeHeader(61).writeByte(state);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends items to the selected slot on the interface.
         * 
         * @param frame
         *        the frame to display the items on.
         * @param item
         *        the item to display on the interface.
         * @param slot
         *        the slot to display the items on.
         * @return this packet builder.
         */
        public PacketBuilder sendItemOnInterfaceSlot(int frame, Item item, int slot) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(32);
            out.writeVariableShortPacketHeader(34).writeShort(frame).writeByte(slot).writeShort(item.getId() + 1);

            if (item.getAmount() > 254) {
                out.writeByte(255).writeShort(item.getAmount());
            } else {
                out.writeByte(item.getAmount());
            }

            out.finishVariableShortPacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the head model of a mob to an interface.
         * 
         * @param id
         *        the id of the head model.
         * @param size
         *        the size of the head model.
         * @return this packet builder.
         */
        public PacketBuilder sendMobHeadModel(int id, int size) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(75).writeShort(id, ValueType.A, ByteOrder.LITTLE).writeShort(size, ValueType.A, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a custom map region.
         * 
         * @param region
         *        the map region to send.
         * @return this packet builder.
         */
        public PacketBuilder sendCustomMapRegion(MapRegion region) {
            this.sendMapRegion();

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(50);
            out.writeVariableShortPacketHeader(241).writeShort(player.getPosition().getRegionY() + 6, ValueType.A).setAccessType(AccessType.BIT_ACCESS);
            for (int z = 0; z < MapRegion.SIZE_LENGTH_Z; z++) {
                for (int x = 0; x < MapRegion.SIZE_LENGTH_X; x++) {
                    for (int y = 0; y < MapRegion.SIZE_LENGTH_Y; y++) {
                        MapRegionTile tile = region.getTile(x, y, z);

                        out.writeBit(tile != null);

                        if (tile != null) {
                            out.writeBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
                        }
                    }
                }
            }
            out.setAccessType(AccessType.BYTE_ACCESS);
            out.writeShort(player.getPosition().getRegionX() + 6).finishVariableShortPacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the head model of a player to an interface.
         * 
         * @param i
         *        the size of the head model.
         * @return this packet builder.
         */
        public PacketBuilder sendPlayerHeadModel(int size) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(185).writeShort(size, ValueType.A, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Flashes the selected sidebar.
         * 
         * @param id
         *        the id of the sidebar to flash.
         * @return this packet builder.
         */
        public PacketBuilder flashSelectedSidebar(int id) {
            // XXX: does not work, you have to fix the packet client sided.

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2);
            out.writeHeader(24).writeByte(id, ValueType.A);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the enter name interface.
         * 
         * @return this packet builder.
         */
        public PacketBuilder enterName() {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(1);
            out.writeHeader(187);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Changes the state of the minimap.
         * 
         * @param state
         *        the new state of the minimap.
         * @return this packet builder.
         */
        public PacketBuilder sendMapState(int state) {
            // States:
            // 0 - Active: Clickable and viewable
            // 1 - Locked: viewable but not clickable
            // 2 - Blacked-out: Minimap is replaced with black background
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2);
            out.writeHeader(99).writeByte(state);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Resets the cameras rotation.
         * 
         * @return this packet builder.
         */
        public PacketBuilder sendResetCameraRotation() {
            // XXX: disconnects the player when used?

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(1);
            out.writeHeader(108);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Spins the camera.
         * 
         * @param x
         *        the x coordinate within the loaded map.
         * @param y
         *        the y coordinate within the loaded map.
         * @param height
         *        the height of the camera.
         * @param speed
         *        the speed of the camera.
         * @param angle
         *        the angle of the camera.
         * @return this packet builder.
         */
        public PacketBuilder sendCameraSpin(int x, int y, int height, int speed, int angle) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(177).writeByte(x / 64).writeByte(y / 64).writeShort(height).writeByte(speed).writeByte(angle);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Moves the camera.
         * 
         * @param x
         *        the x coordinate within the loaded map.
         * @param y
         *        the y coordinate within the loaded map.
         * @param height
         *        the height of the camera.
         * @param speed
         *        the speed of the camera.
         * @param angle
         *        the angle of the camera.
         * @return this packet builder.
         */
        public PacketBuilder sendCameraMovement(int x, int y, int height, int speed, int angle) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(166).writeByte(x / 64).writeByte(y / 64).writeShort(height).writeByte(speed).writeByte(angle);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Shakes the screen.
         * 
         * @param intensity
         *        the intensity of the shake.
         * @return this packet builder.
         */
        public PacketBuilder sendScreenShake(int intensity) {
            if (intensity > 4) {
                throw new IllegalArgumentException("Intensity must be below 5!");
            }

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(35).writeByte(intensity).writeByte(intensity).writeByte(intensity).writeByte(intensity);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Resets the position of the camera.
         * 
         * @return this packet builder.
         */
        public PacketBuilder sendResetCamera() {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(107);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Plays music from the cache.
         * 
         * @param id
         *        the id of the music to play.
         * @return this packet builder.
         */
        public PacketBuilder sendMusic(int id) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(74).writeShort(id, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the system update time.
         * 
         * @param time
         *        the amount of time to send.
         * @return this packet builder.
         */
        public PacketBuilder systemUpdate(int time) {
            // XXX: 101 = 1:00? 201 = 2:00? 50 = 0:29? Figure it out.
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(114).writeShort(time, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Changes the color on an interface.
         * 
         * @param interfaceId
         *        the interface.
         * @param color
         *        the new color.
         * @return this packer builder.
         */
        public PacketBuilder changeColorOnInterface(int interfaceId, int color) {
            // XXX: afaik, doesn't work but I have no clue what this packet is
            // for
            // and I might have been trying to use it wrong.

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(122).writeShort(interfaceId, ValueType.A, ByteOrder.LITTLE).writeShort(color, ValueType.A, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends an item to an interface.
         * 
         * @param id
         *        the id of the item.
         * @param zoom
         *        the zoom of the item.
         * @param model
         *        the model of the item.
         * @return this packet builder.
         */
        public PacketBuilder sendItemOnInterface(int id, int zoom, int model) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(246).writeShort(id, PacketBuffer.ByteOrder.LITTLE).writeShort(zoom).writeShort(model);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Creates a projectile for the specified player.
         * 
         * @param position
         *        the position of the projectile.
         * @param offset
         *        the offset position of the projectile.
         * @param angle
         *        the angle of the projectile.
         * @param speed
         *        the speed of the projectile.
         * @param gfxMoving
         *        the rate that projectile gfx moves in.
         * @param startHeight
         *        the starting height of the projectile.
         * @param endHeight
         *        the ending height of the projectile.
         * @param lockon
         *        the lockon value of this projectile.
         * @param time
         *        the time it takes for this projectile to hit its desired
         *        position.
         * @return this packet builder.
         */
        public PacketBuilder createProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time) {
            this.sendCoordinates(position);
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(16);
            out.writeHeader(117).writeByte(angle).writeByte(offset.getY()).writeByte(offset.getX()).writeShort(lockon).writeShort(gfxMoving).writeByte(startHeight).writeByte(endHeight).writeShort(time).writeShort(speed).writeByte(16).writeByte(64);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a client config.
         * 
         * @param id
         *        the id of the config.
         * @param state
         *        the state to put this config in.
         * @return this packet builder.
         */
        public PacketBuilder sendConfig(int id, int state) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(4);
            out.writeHeader(36);
            out.writeShort(id, ByteOrder.LITTLE).writeByte(state);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the image of an object to the world.
         * 
         * @param object
         *        the object to send.
         * @return this packet builder.
         */
        public PacketBuilder sendObject(WorldObject object) {
            sendCoordinates(object.getPosition());
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(151).writeByte(0, ValueType.S).writeShort(object.getId(), ByteOrder.LITTLE).writeByte((object.getType() << 2) + (object.getFace().getFaceId() & 3), ValueType.S);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Removes the image of an object from the world.
         * 
         * @param object
         *        the object to remove.
         * @return this packet builder.
         */
        public PacketBuilder removeObject(WorldObject object) {
            sendCoordinates(object.getPosition());
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(101).writeByte((object.getType() << 2) + (object.getFace().getFaceId() & 3), ValueType.C).writeByte(0);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Replaces the image of an object with a new one.
         * 
         * @param position
         *        the position of the old object.
         * @param object
         *        the new object to take its place.
         * @return this packet builder.
         */
        public PacketBuilder replaceObject(Position position, int object) {
            removeObject(new WorldObject(0, position, Rotation.SOUTH, 10));
            sendObject(new WorldObject(object, position, Rotation.SOUTH, 10));
            return this;
        }

        /**
         * Sends the players skills to the client.
         * 
         * @param skillID
         *        the id of the skill being sent.
         * @param level
         *        the level of the skill being sent.
         * @param exp
         *        the experience of the skill being sent.
         * @return this packet builder.
         */
        public PacketBuilder sendSkill(int skillID, int level, int exp) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(8);
            out.writeHeader(134).writeByte(skillID).writeInt(exp, ByteOrder.MIDDLE).writeByte(level);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Closes any interfaces this player has open.
         * 
         * @return this packet builder.
         */
        public PacketBuilder closeWindows() {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(1);
            out.writeHeader(219);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the list of people you have on your friends and ignores list.
         * 
         * @param i
         *        the world you're in? Not completely sure what this is.
         * @return this packet builder.
         */
        public PacketBuilder sendPrivateMessagingList(int i) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2);
            out.writeHeader(221).writeByte(i);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the chat options.
         * 
         * @param publicChat
         *        the public chat option.
         * @param privateChat
         *        the private chat option.
         * @param tradeBlock
         *        the trade/challenge option.
         * @return this packet builder.
         */
        public PacketBuilder sendChatOptions(int publicChat, int privateChat, int tradeBlock) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(4);
            out.writeHeader(206).writeByte(publicChat).writeByte(privateChat).writeByte(tradeBlock);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Loads a player in your friends list.
         * 
         * @param playerName
         *        the player's name.
         * @param world
         *        the world they are on.
         * @return this packet builder.
         */
        public PacketBuilder loadPrivateMessage(long playerName, int world) {
            if (world != 0) {
                world += 9;
            }

            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(10);
            out.writeHeader(50).writeLong(playerName).writeByte(world);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a hint arrow on the specified coordinates.
         * 
         * @param coordinates
         *        the coordinates to send the arrow on.
         * @param position
         *        the position of the arrow on the coordinates.
         * @return this packet builder.
         */
        public PacketBuilder sendPositionHintArrow(Position coordinates, int position) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(7);
            out.writeHeader(254).writeByte(position).writeShort(coordinates.getX()).writeShort(coordinates.getY()).writeByte(coordinates.getZ());
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Send a private message to another player.
         * 
         * @param name
         *        the name of the player you are sending the message to.
         * @param rights
         *        your player rights.
         * @param chatMessage
         *        the message.
         * @param messageSize
         *        the message size.
         * @return this packet builder.
         */
        public PacketBuilder sendPrivateMessage(long name, int rights, byte[] chatMessage, int messageSize) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(messageSize + 15);
            out.writeVariablePacketHeader(196).writeLong(name).writeInt(player.getPrivateMessage().getLastPrivateMessageId()).writeByte(rights).writeBytes(chatMessage, messageSize).finishVariablePacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a hint arrow on an entity.
         * 
         * @param type
         *        the type of entity.
         * @param id
         *        the id of the entity.
         * @return this packet builder.
         */
        public PacketBuilder sendEntityHintArrow(int type, int id) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(254).writeByte(type).writeShort(id).writeByte(0);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the players current coordinates to the client.
         * 
         * @param position
         *        the coordinates.
         * @return this packet builder.
         */
        public PacketBuilder sendCoordinates(Position position) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(85).writeByte(position.getY() - (player.getCurrentRegion().getRegionY() * 8), ValueType.C).writeByte(position.getX() - (player.getCurrentRegion().getRegionX() * 8), ValueType.C);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Opens a walkable interface for this player.
         * 
         * @param id
         *        the walkable interface to open.
         * @return this packet builder.
         */
        public PacketBuilder walkableInterface(int id) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(208).writeShort(id, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the image of a ground item to the world.
         * 
         * @param item
         *        the item to send.
         * @return this packet builder.
         */
        public PacketBuilder sendGroundItem(WorldItem item) {
            sendCoordinates(item.getPosition());
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(6);
            out.writeHeader(44).writeShort(item.getItem().getId(), ValueType.A, ByteOrder.LITTLE).writeShort(item.getItem().getAmount()).writeByte(0);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Removes the image of a ground item from the world.
         * 
         * @param item
         *        the item to remove.
         * @return this packet builder.
         */
        public PacketBuilder removeGroundItem(WorldItem item) {
            sendCoordinates(item.getPosition());
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(4);
            out.writeHeader(156).writeByte(0, ValueType.S).writeShort(item.getItem().getId());
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends player context menus.
         * 
         * @param option
         *        the option.
         * @param slot
         *        the slot for the option to be placed in.
         * @return this packet builder.
         */
        public PacketBuilder sendPlayerMenu(String option, int slot) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(option.length() + 6);
            out.writeVariablePacketHeader(104).writeByte(slot, PacketBuffer.ValueType.C).writeByte(0, PacketBuffer.ValueType.A).writeString(option).finishVariablePacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a string to an interface.
         * 
         * @param text
         *        the string to send.
         * @param id
         *        where the string should be sent.
         * @return this packet builder.
         */
        public PacketBuilder sendString(String text, int id) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(text.length() + 6);
            out.writeVariableShortPacketHeader(126).writeString(text).writeShort(id, ValueType.A).finishVariableShortPacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the equipment you current have on to the client.
         * 
         * @param slot
         *        the equipment slot.
         * @param itemID
         *        the item id.
         * @param itemAmount
         *        the item amount.
         * @return this packet builder.
         */
        public PacketBuilder sendEquipment(int slot, int itemID, int itemAmount) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(32);
            out.writeVariableShortPacketHeader(34).writeShort(1688).writeByte(slot).writeShort(itemID + 1);

            if (itemAmount > 254) {
                out.writeByte(255).writeShort(itemAmount);
            } else {
                out.writeByte(itemAmount);
            }

            out.finishVariableShortPacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Updates an array of items on an interface.
         * 
         * @param interfaceId
         *        the interface to send the items on.
         * @param items
         *        the items to send.
         * @return this packet builder.
         */
        public PacketBuilder sendUpdateItems(int interfaceId, Item[] items) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2048);
            out.writeVariableShortPacketHeader(53).writeShort(interfaceId);
            if (items == null) {
                out.writeShort(0).writeByte(0).writeShort(0, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE).finishVariableShortPacketHeader();
                Rs2Engine.getEncoder().encode(out, player.getSession());
                return this;
            }
            out.writeShort(items.length);
            for (Item item : items) {
                if (item != null) {
                    if (item.getAmount() > 254) {
                        out.writeByte(255);
                        out.writeInt(item.getAmount(), PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                    } else {
                        out.writeByte(item.getAmount());
                    }
                    out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                } else {
                    out.writeByte(0);
                    out.writeShort(0, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
                }
            }
            out.finishVariableShortPacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends an interface to your inventory.
         * 
         * @param interfaceId
         *        the interface to send.
         * @param inventoryId
         *        the inventory to send on.
         * @return this packet builder.
         */
        public PacketBuilder sendInventoryInterface(int interfaceId, int inventoryId) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(248);
            out.writeShort(interfaceId, PacketBuffer.ValueType.A);
            out.writeShort(inventoryId);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Opens an interface for this player.
         * 
         * @param interfaceId
         *        the interface to open for this player.
         * @return this packet builder.
         */
        public PacketBuilder sendInterface(int interfaceId) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(97);
            out.writeShort(interfaceId);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends the player a message to the chatbox.
         * 
         * @param message
         *        the message to send.
         * @return this packet builder.
         */
        public PacketBuilder sendMessage(String message) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(message.length() + 3);
            out.writeVariablePacketHeader(253);
            out.writeString(message);
            out.finishVariablePacketHeader();
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends a sidebar interface.
         * 
         * @param menuId
         *        the sidebar to send the interface on.
         * @param form
         *        the interface to send on the sidebar.
         * @return this packet builder.
         */
        public PacketBuilder sendSidebarInterface(int menuId, int form) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(4);
            out.writeHeader(71);
            out.writeShort(form);
            out.writeByte(menuId, PacketBuffer.ValueType.A);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Refreshes the map region.
         * 
         * @return this packet builder.
         */
        public PacketBuilder sendMapRegion() {
            player.getCurrentRegion().setAs(player.getPosition());
            player.setNeedsPlacement(true);
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(5);
            out.writeHeader(73);
            out.writeShort(player.getPosition().getRegionX() + 6, PacketBuffer.ValueType.A);
            out.writeShort(player.getPosition().getRegionY() + 6);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Disconnects the player.
         * 
         * @return this packet builder.
         */
        public PacketBuilder sendLogout() {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(1);
            out.writeHeader(109);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Sends an interface to your chatbox.
         * 
         * @param frame
         *        the interface to send to the chatbox.
         * @return this packet builder.
         */
        public PacketBuilder sendChatInterface(int frame) {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(3);
            out.writeHeader(164);
            out.writeShort(frame, ByteOrder.LITTLE);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }

        /**
         * Resets this players animation.
         * 
         * @return this packet builder.
         */
        public PacketBuilder resetAnimation() {
            PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(1);
            out.writeHeader(1);
            Rs2Engine.getEncoder().encode(out, player.getSession());
            return this;
        }
    }

}
