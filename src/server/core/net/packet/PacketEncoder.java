package server.core.net.packet;

import java.nio.channels.SocketChannel;

import server.core.net.Session;
import server.core.net.buffer.PacketBuffer;
import server.core.net.buffer.PacketBuffer.AccessType;
import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.buffer.PacketBuffer.WriteBuffer;
import server.world.World;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Position;
import server.world.map.RegionBuilder;
import server.world.map.RegionTileBuilder;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A collection of packets that are encoded and written to a
 * {@link SocketChannel} for a certain {@link Session}.
 * 
 * @author lare96
 */
public final class PacketEncoder {

    /** The player sending these packets. */
    private Player player;

    /**
     * Construct a new {@link PacketEncoder}.
     * 
     * @param player
     *        the player sending these packets.
     */
    public PacketEncoder(Player player) {
        this.player = player;
    }

    /**
     * Displays a string on an empty chatbox interface.
     * 
     * @param s
     *        the string to display.
     * @return this packet encoder.
     */
    public PacketEncoder sendChatboxString(String s) {
        sendString(s, 357);
        sendString("Click here to continue", 358);
        sendChatInterface(356);
        return this;
    }

    /**
     * Plays an animation for this object.
     * 
     * @param position
     *        the position of the object.
     * @param animation
     *        the animation to play.
     * @param type
     *        the type of object.
     * @param orientation
     *        the orientation of this object.
     * @return this packet encoder.
     */
    public PacketEncoder sendObjectAnimation(Position position, int animation, int type, int orientation) {
        sendCoordinates(position);
        WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(160);
        out.writeByte(((0 & 7) << 4) + (0 & 7), ValueType.S);
        out.writeByte((type << 2) + (orientation & 3), ValueType.S);
        out.writeShort(animation, ValueType.A);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Plays an animation for this object visible to everyone.
     * 
     * @param position
     *        the position of the object.
     * @param animation
     *        the animation to play.
     * @param type
     *        the type of object.
     * @param orientation
     *        the orientation of this object.
     * @return this packet encoder.
     */
    public PacketEncoder sendGlobalObjectAnimation(Position position, int animation, int type, int orientation) {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().isViewableFrom(position)) {
                player.getPacketBuilder().sendObjectAnimation(position, animation, type, orientation);
            }
        }
        return this;
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
     * @return this packet encoder.
     */
    public PacketEncoder sendGraphic(int id, Position position, int level) {
        sendCoordinates(position);
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(4).writeByte(0).writeShort(id).writeByte(level).writeShort(0);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendViewableGraphic(int id, Position position, int level) {
        for (Player player : World.getPlayers()) {
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
     * @param delay
     *        the delay before the sound plays.
     * @return this packet encoder.
     */
    public PacketEncoder sendSound(int id, int type, int delay) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(8);
        out.writeHeader(174).writeShort(id).writeByte(type).writeShort(delay);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendWelcomeInterface(int recoveryChange, boolean memberWarning, int messages, int lastLoginIP, int lastLogin) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(20);
        out.writeHeader(176).writeByte(recoveryChange, ValueType.C).writeShort(messages, ValueType.A).writeByte(memberWarning ? 1 : 0).writeInt(lastLoginIP, ByteOrder.INVERSE_MIDDLE).writeShort(lastLogin);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Plays an interface animation.
     * 
     * @param interfaceId
     *        the interface to play the animation on.
     * @param animation
     *        the animation to play.
     * @return this packet encoder.
     */
    public PacketEncoder interfaceAnimation(int interfaceId, int animation) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(200).writeShort(interfaceId).writeShort(animation);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the multicombat icon.
     * 
     * @param state
     *        the state of the multicombat icon (0 = off/1 = on).
     * @return this packet encoder.
     */
    public PacketEncoder sendMultiCombatInterface(int state) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2);
        out.writeHeader(61).writeByte(state);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendItemOnInterfaceSlot(int frame, Item item, int slot) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(32);
        out.writeVariableShortPacketHeader(34).writeShort(frame).writeByte(slot).writeShort(item.getId() + 1);

        if (item.getAmount() > 254) {
            out.writeByte(255).writeShort(item.getAmount());
        } else {
            out.writeByte(item.getAmount());
        }

        out.finishVariableShortPacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the head model of a mob to an interface.
     * 
     * @param id
     *        the id of the head model.
     * @param size
     *        the size of the head model.
     * @return this packet encoder.
     */
    public PacketEncoder sendMobHeadModel(int id, int size) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(75).writeShort(id, ValueType.A, ByteOrder.LITTLE).writeShort(size, ValueType.A, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends a custom map region.
     * 
     * @param region
     *        the map region to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendCustomMapRegion(RegionBuilder region) {
        this.sendMapRegion();

        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(50);
        out.writeVariableShortPacketHeader(241).writeShort(player.getPosition().getRegionY() + 6, ValueType.A).setAccessType(AccessType.BIT_ACCESS);
        for (int z = 0; z < RegionBuilder.SIZE_LENGTH_Z; z++) {
            for (int x = 0; x < RegionBuilder.SIZE_LENGTH_X; x++) {
                for (int y = 0; y < RegionBuilder.SIZE_LENGTH_Y; y++) {
                    RegionTileBuilder tile = region.getTile(x, y, z);

                    out.writeBit(tile != null);

                    if (tile != null) {
                        out.writeBits(26, tile.getX() << 14 | tile.getY() << 3 | tile.getZ() << 24 | tile.getRotation() << 1);
                    }
                }
            }
        }
        out.setAccessType(AccessType.BYTE_ACCESS);
        out.writeShort(player.getPosition().getRegionX() + 6).finishVariableShortPacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the head model of a player to an interface.
     * 
     * @param i
     *        the size of the head model.
     * @return this packet encoder.
     */
    public PacketEncoder sendPlayerHeadModel(int size) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(185).writeShort(size, ValueType.A, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Flashes the selected sidebar.
     * 
     * @param id
     *        the id of the sidebar to flash.
     * @return this packet encoder.
     */
    public PacketEncoder flashSelectedSidebar(int id) {
        // XXX: try negative values

        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2);
        out.writeHeader(24).writeByte(id, ValueType.A);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the enter name interface.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder enterName() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(1);
        out.writeHeader(187);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Changes the state of the minimap.
     * 
     * @param state
     *        the new state of the minimap.
     * @return this packet encoder.
     */
    public PacketEncoder sendMapState(int state) {
        // States:
        // 0 - Active: Clickable and viewable
        // 1 - Locked: viewable but not clickable
        // 2 - Blacked-out: Minimap is replaced with black background
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2);
        out.writeHeader(99).writeByte(state);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Resets the cameras rotation.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendResetCameraRotation() {
        // XXX: disconnects the player when used?

        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(1);
        out.writeHeader(108);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendCameraSpin(int x, int y, int height, int speed, int angle) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(177).writeByte(x / 64).writeByte(y / 64).writeShort(height).writeByte(speed).writeByte(angle);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendCameraMovement(int x, int y, int height, int speed, int angle) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(166).writeByte(x / 64).writeByte(y / 64).writeShort(height).writeByte(speed).writeByte(angle);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Shakes the screen.
     * 
     * @param intensity
     *        the intensity of the shake.
     * @return this packet encoder.
     */
    public PacketEncoder sendScreenShake(int intensity) {
        if (intensity > 4) {
            throw new IllegalArgumentException("Intensity must be below 5!");
        }

        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(35).writeByte(intensity).writeByte(intensity).writeByte(intensity).writeByte(intensity);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Resets the position of the camera.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendResetCamera() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(107);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Plays music from the cache.
     * 
     * @param id
     *        the id of the music to play.
     * @return this packet encoder.
     */
    public PacketEncoder sendMusic(int id) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(74).writeShort(id, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the system update time.
     * 
     * @param time
     *        the amount of time to send.
     * @return this packet encoder.
     */
    public PacketEncoder systemUpdate(int time) {
        // XXX: 101 = 1:00? 201 = 2:00? 50 = 0:29? Figure it out.
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(114).writeShort(time, ByteOrder.LITTLE);
        player.getSession().encode(out);
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
    public PacketEncoder changeColorOnInterface(int interfaceId, int color) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(122).writeShort(interfaceId, ValueType.A, ByteOrder.LITTLE).writeShort(color, ValueType.A, ByteOrder.LITTLE);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendItemOnInterface(int id, int zoom, int model) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(246).writeShort(id, PacketBuffer.ByteOrder.LITTLE).writeShort(zoom).writeShort(model);
        player.getSession().encode(out);
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
     *        the time it takes for this projectile to hit its desired position.
     * @return this packet encoder.
     */
    public PacketEncoder sendProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time) {
        this.sendCoordinates(position);
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(16);
        out.writeHeader(117).writeByte(angle).writeByte(offset.getY()).writeByte(offset.getX()).writeShort(lockon).writeShort(gfxMoving).writeByte(startHeight).writeByte(endHeight).writeShort(time).writeShort(speed).writeByte(16).writeByte(64);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Creates a global projectile.
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
     *        the time it takes for this projectile to hit its desired position.
     * @return this packet encoder.
     */
    public PacketEncoder sendGlobalProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time) {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().isViewableFrom(position)) {
                player.getPacketBuilder().sendProjectile(position, offset, angle, speed, gfxMoving, startHeight, endHeight, lockon, time);
            }
        }
        return this;
    }

    /**
     * Sends a client config.
     * 
     * @param id
     *        the id of the config.
     * @param state
     *        the state to put this config in.
     * @return this packet encoder.
     */
    public PacketEncoder sendConfig(int id, int state) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(4);
        out.writeHeader(36);
        out.writeShort(id, ByteOrder.LITTLE).writeByte(state);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the image of an object to the world.
     * 
     * @param object
     *        the object to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(151).writeByte(0, ValueType.S).writeShort(object.getId(), ByteOrder.LITTLE).writeByte((object.getType() << 2) + (object.getRotation().getFaceId() & 3), ValueType.S);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Removes the image of an object from the world.
     * 
     * @param object
     *        the object to remove.
     * @return this packet encoder.
     */
    public PacketEncoder removeObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(101).writeByte((object.getType() << 2) + (object.getRotation().getFaceId() & 3), ValueType.C).writeByte(0);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Replaces the image of an object with a new one.
     * 
     * @param position
     *        the position of the old object.
     * @param object
     *        the new object to take its place.
     * @return this packet encoder.
     */
    public PacketEncoder replaceObject(Position position, int object) {
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
     * @return this packet encoder.
     */
    public PacketEncoder sendSkill(int skillID, int level, int exp) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(8);
        out.writeHeader(134).writeByte(skillID).writeInt(exp, ByteOrder.MIDDLE).writeByte(level);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Closes any interfaces this player has open.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder closeWindows() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(1);
        out.writeHeader(219);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the list of people you have on your friends and ignores list.
     * 
     * @param i
     *        the world you're in? Not completely sure what this is.
     * @return this packet encoder.
     */
    public PacketEncoder sendPrivateMessagingList(int i) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2);
        out.writeHeader(221).writeByte(i);
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendChatOptions(int publicChat, int privateChat, int tradeBlock) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(4);
        out.writeHeader(206).writeByte(publicChat).writeByte(privateChat).writeByte(tradeBlock);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Loads a player in your friends list.
     * 
     * @param playerName
     *        the player's name.
     * @param world
     *        the world they are on.
     * @return this packet encoder.
     */
    public PacketEncoder loadPrivateMessage(long playerName, int world) {
        if (world != 0) {
            world += 9;
        }

        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(10);
        out.writeHeader(50).writeLong(playerName).writeByte(world);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends a hint arrow on the specified coordinates.
     * 
     * @param coordinates
     *        the coordinates to send the arrow on.
     * @param position
     *        the position of the arrow on the coordinates.
     * @return this packet encoder.
     */
    public PacketEncoder sendPositionHintArrow(Position coordinates, int position) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(7);
        out.writeHeader(254).writeByte(position).writeShort(coordinates.getX()).writeShort(coordinates.getY()).writeByte(coordinates.getZ());
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendPrivateMessage(long name, int rights, byte[] chatMessage, int messageSize) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(messageSize + 15);
        out.writeVariablePacketHeader(196).writeLong(name).writeInt(player.getPrivateMessage().getLastPrivateMessageId()).writeByte(rights).writeBytes(chatMessage, messageSize).finishVariablePacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends a hint arrow on an entity.
     * 
     * @param type
     *        the type of entity.
     * @param id
     *        the id of the entity.
     * @return this packet encoder.
     */
    public PacketEncoder sendEntityHintArrow(int type, int id) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(254).writeByte(type).writeShort(id).writeByte(0);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the players current coordinates to the client.
     * 
     * @param position
     *        the coordinates.
     * @return this packet encoder.
     */
    public PacketEncoder sendCoordinates(Position position) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(85).writeByte(position.getY() - (player.getCurrentRegion().getRegionY() * 8), ValueType.C).writeByte(position.getX() - (player.getCurrentRegion().getRegionX() * 8), ValueType.C);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Opens a walkable interface for this player.
     * 
     * @param id
     *        the walkable interface to open.
     * @return this packet encoder.
     */
    public PacketEncoder walkableInterface(int id) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(208).writeShort(id, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the image of a ground item to the world.
     * 
     * @param item
     *        the item to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendGroundItem(GroundItem item) {
        sendCoordinates(item.getPosition());
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(6);
        out.writeHeader(44).writeShort(item.getItem().getId(), ValueType.A, ByteOrder.LITTLE).writeShort(item.getItem().getAmount()).writeByte(0);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Removes the image of a ground item from the world.
     * 
     * @param item
     *        the item to remove.
     * @return this packet encoder.
     */
    public PacketEncoder removeGroundItem(GroundItem item) {
        sendCoordinates(item.getPosition());
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(4);
        out.writeHeader(156).writeByte(0, ValueType.S).writeShort(item.getItem().getId());
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends player context menus.
     * 
     * @param option
     *        the option.
     * @param slot
     *        the slot for the option to be placed in.
     * @return this packet encoder.
     */
    public PacketEncoder sendPlayerMenu(String option, int slot) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(option.length() + 6);
        out.writeVariablePacketHeader(104).writeByte(slot, PacketBuffer.ValueType.C).writeByte(0, PacketBuffer.ValueType.A).writeString(option).finishVariablePacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends a string to an interface.
     * 
     * @param text
     *        the string to send.
     * @param id
     *        where the string should be sent.
     * @return this packet encoder.
     */
    public PacketEncoder sendString(String text, int id) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(text.length() + 6);
        out.writeVariableShortPacketHeader(126).writeString(text).writeShort(id, ValueType.A).finishVariableShortPacketHeader();
        player.getSession().encode(out);
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
     * @return this packet encoder.
     */
    public PacketEncoder sendEquipment(int slot, int itemID, int itemAmount) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(32);
        out.writeVariableShortPacketHeader(34).writeShort(1688).writeByte(slot).writeShort(itemID + 1);

        if (itemAmount > 254) {
            out.writeByte(255).writeShort(itemAmount);
        } else {
            out.writeByte(itemAmount);
        }

        out.finishVariableShortPacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Updates an array of items on an interface.
     * 
     * @param interfaceId
     *        the interface to send the items on.
     * @param items
     *        the items to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendUpdateItems(int interfaceId, Item[] items) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2048);
        out.writeVariableShortPacketHeader(53).writeShort(interfaceId);
        if (items == null) {
            out.writeShort(0).writeByte(0).writeShort(0, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE).finishVariableShortPacketHeader();
            player.getSession().encode(out);
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
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends an interface to your inventory.
     * 
     * @param interfaceId
     *        the interface to send.
     * @param inventoryId
     *        the inventory to send on.
     * @return this packet encoder.
     */
    public PacketEncoder sendInventoryInterface(int interfaceId, int inventoryId) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(248);
        out.writeShort(interfaceId, PacketBuffer.ValueType.A);
        out.writeShort(inventoryId);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Opens an interface for this player.
     * 
     * @param interfaceId
     *        the interface to open for this player.
     * @return this packet encoder.
     */
    public PacketEncoder sendInterface(int interfaceId) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(97);
        out.writeShort(interfaceId);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the player a message to the chatbox.
     * 
     * @param message
     *        the message to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendMessage(String message) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(message.length() + 3);
        out.writeVariablePacketHeader(253);
        out.writeString(message);
        out.finishVariablePacketHeader();
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends a sidebar interface.
     * 
     * @param menuId
     *        the sidebar to send the interface on.
     * @param form
     *        the interface to send on the sidebar.
     * @return this packet encoder.
     */
    public PacketEncoder sendSidebarInterface(int menuId, int form) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(4);
        out.writeHeader(71);
        out.writeShort(form);
        out.writeByte(menuId, PacketBuffer.ValueType.A);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Refreshes the map region.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendMapRegion() {
        player.getCurrentRegion().setAs(player.getPosition());
        player.setNeedsPlacement(true);
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(5);
        out.writeHeader(73);
        out.writeShort(player.getPosition().getRegionX() + 6, PacketBuffer.ValueType.A);
        out.writeShort(player.getPosition().getRegionY() + 6);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Disconnects the player.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendLogout() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(1);
        out.writeHeader(109);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends the slot to the client. Used for
     * 
     * @return this packet builder.
     */
    public PacketEncoder sendDetails() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(4);
        out.writeHeader(249);
        out.writeByte(1, ValueType.A);
        out.writeShort(player.getSlot(), ValueType.A, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Sends an interface to your chatbox.
     * 
     * @param frame
     *        the interface to send to the chatbox.
     * @return this packet encoder.
     */
    public PacketEncoder sendChatInterface(int frame) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(3);
        out.writeHeader(164);
        out.writeShort(frame, ByteOrder.LITTLE);
        player.getSession().encode(out);
        return this;
    }

    /**
     * Resets this players animation.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder resetAnimation() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(1);
        out.writeHeader(1);
        player.getSession().encode(out);
        return this;
    }
}
