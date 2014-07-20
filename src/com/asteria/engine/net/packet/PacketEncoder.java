package com.asteria.engine.net.packet;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ground.GroundItem;
import com.asteria.world.map.Palette;
import com.asteria.world.map.Palette.PaletteTile;
import com.asteria.world.map.Position;
import com.asteria.world.object.WorldObject;
import com.asteria.world.object.WorldObject.Rotation;

/**
 * A collection of packets that are constructed and sent to a socket channel.
 * 
 * @author lare96
 */
public final class PacketEncoder {

    /** The player sending these packets. */
    private Player player;

    /**
     * Create a new {@link PacketEncoder}.
     * 
     * @param player
     *            the player sending these packets.
     */
    public PacketEncoder(Player player) {
        this.player = player;
    }

    /**
     * Shows or hides a layer on an interface.
     * 
     * @param interfaceIndex
     *            the layer to show or hide.
     * @param hidden
     *            true if the layer should be hidden.
     * @return this packet encoder.
     */
    public PacketEncoder sendHideInterfaceLayer(int interfaceIndex,
            boolean hidden) {
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(171, player.getSession());
        out.writeByte(hidden ? 1 : 0);
        out.writeShort(interfaceIndex);
        out.sendPacket();
        return this;
    }

    /**
     * Updates the special bar meter.
     * 
     * @param amount
     *            the amount to update it with.
     * @param id
     *            the id of the bar.
     * @return this packet encoder.
     */
    public PacketEncoder updateSpecialBar(int amount, int id) {
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(70, player.getSession());
        out.writeShort(amount);
        out.writeShort(0, ProtocolBuffer.ByteOrder.LITTLE);
        out.writeShort(id, ProtocolBuffer.ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Displays a string on an empty chatbox interface.
     * 
     * @param s
     *            the string to display.
     * @return this packet encoder.
     */
    public PacketEncoder sendEmptyChatbox(String s) {
        sendString(s, 357);
        sendString("Click here to continue", 358);
        sendChatInterface(356);
        return this;
    }

    /**
     * Plays an animation for this object.
     * 
     * @param position
     *            the position of the object.
     * @param animation
     *            the animation to play.
     * @param type
     *            the type of object.
     * @param orientation
     *            the orientation of this object.
     * @return this packet encoder.
     */
    public PacketEncoder sendObjectAnimation(Position position, int animation,
            int type, int orientation) {
        sendCoordinates(position);
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(160, player.getSession());

        // 0 has to be sent for the position or else it won't work.
        out.writeByte(((0 & 7) << 4) + (0 & 7), ValueType.S);
        out.writeByte((type << 2) + (orientation & 3), ValueType.S);
        out.writeShort(animation, ValueType.A);
        out.sendPacket();
        return this;
    }

    /**
     * Plays an animation for this object visible to everyone.
     * 
     * @param position
     *            the position of the object.
     * @param animation
     *            the animation to play.
     * @param type
     *            the type of object.
     * @param orientation
     *            the orientation of this object.
     * @return this packet encoder.
     */
    public PacketEncoder sendAllObjectAnimation(Position position,
            int animation, int type, int orientation) {
        for (Player player : this.player.getLocalPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().isViewableFrom(position)) {
                player.getPacketBuilder().sendObjectAnimation(position,
                        animation, type, orientation);
            }
        }
        return this;
    }

    /**
     * Creates a graphic for a single player.
     * 
     * @param id
     *            the id of the graphic.
     * @param position
     *            the position of the graphic.
     * @param level
     *            the level (how high) the graphic is.
     * @return this packet encoder.
     */
    public PacketEncoder sendGraphic(int id, Position position, int level) {
        sendCoordinates(position);
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(4, player.getSession()).writeByte(0).writeShort(id)
                .writeByte(level).writeShort(0);
        out.sendPacket();
        return this;
    }

    /**
     * Creates a graphic for everyone within viewing distance.
     * 
     * @param id
     *            the id of the graphic.
     * @param position
     *            the position of the graphic.
     * @param level
     *            the level (how high) the graphic is.
     * @return this packet encoder.
     */
    public PacketEncoder sendAllGraphic(int id, Position position, int level) {
        for (Player player : this.player.getLocalPlayers()) {
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
     *            the id of the sound.
     * @param type
     *            the type of sound.
     * @param delay
     *            the delay before the sound plays.
     * @return this packet encoder.
     */
    public PacketEncoder sendSound(int id, int type, int delay) {
        ProtocolBuffer out = new ProtocolBuffer(8);
        out.build(174, player.getSession()).writeShort(id).writeByte(type)
                .writeShort(delay);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the welcome interface used upon login.
     * 
     * @param recoveryChange
     *            the last time you set your recovery questions.
     * @param memberWarning
     *            if you should be warned about your membership running out.
     * @param messages
     *            the amount of messages you have.
     * @param lastLoginIP
     *            the last IP you logged in from.
     * @param lastLogin
     *            the last time you logged in.
     * @return this packet encoder.
     */
    public PacketEncoder sendWelcomeInterface(int recoveryChange,
            boolean memberWarning, int messages, int lastLoginIP, int lastLogin) {
        ProtocolBuffer out = new ProtocolBuffer(20);
        out.build(176, player.getSession())
                .writeByte(recoveryChange, ValueType.C)
                .writeShort(messages, ValueType.A)
                .writeByte(memberWarning ? 1 : 0)
                .writeInt(lastLoginIP, ByteOrder.INVERSE_MIDDLE)
                .writeShort(lastLogin);
        out.sendPacket();
        return this;
    }

    /**
     * Plays an interface animation.
     * 
     * @param interfaceId
     *            the interface to play the animation on.
     * @param animation
     *            the animation to play.
     * @return this packet encoder.
     */
    public PacketEncoder interfaceAnimation(int interfaceId, int animation) {
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(200, player.getSession()).writeShort(interfaceId)
                .writeShort(animation);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the multicombat icon.
     * 
     * @param state
     *            the state of the multicombat icon.
     * @return this packet encoder.
     */
    public PacketEncoder sendMultiCombatInterface(int state) {

        // All possible states.
        // Off - 0
        // On - 1
        ProtocolBuffer out = new ProtocolBuffer(2);
        out.build(61, player.getSession()).writeByte(state);
        out.sendPacket();
        return this;
    }

    /**
     * Sends items to the selected slot on the interface.
     * 
     * @param id
     *            the interface to display the items on.
     * @param item
     *            the item to display on the interface.
     * @param slot
     *            the slot to display the items on.
     * @return this packet encoder.
     */
    public PacketEncoder sendItemOnInterfaceSlot(int id, Item item, int slot) {
        ProtocolBuffer out = new ProtocolBuffer(32);
        out.buildVarShort(34, player.getSession()).writeShort(id)
                .writeByte(slot).writeShort(item.getId() + 1);

        if (item.getAmount() > 254) {
            out.writeByte(255).writeShort(item.getAmount());
        } else {
            out.writeByte(item.getAmount());
        }

        out.endVarShort();
        out.sendPacket();
        return this;
    }

    /**
     * Sends the head model of a mob to an interface.
     * 
     * @param id
     *            the id of the head model.
     * @param size
     *            the size of the head model.
     * @return this packet encoder.
     */
    public PacketEncoder sendMobHeadModel(int id, int size) {
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(75, player.getSession())
                .writeShort(id, ValueType.A, ByteOrder.LITTLE)
                .writeShort(size, ValueType.A, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Creates a custom map region made up tiles from anywhere in the game
     * world.
     * 
     * @param palette
     *            the instance of the region to create.
     * @return this packet builder.
     */
    public PacketEncoder sendCustomMapRegion(Palette palette) {
        sendMapRegion();
        ProtocolBuffer out = new ProtocolBuffer(100);
        out.buildVarShort(241, player.getSession());
        out.writeShort(player.getPosition().getRegionY() + 6, ValueType.A);
        out.startBitAccess();

        for (int z = 0; z < 4; z++) {
            for (int x = 0; x < 13; x++) {
                for (int y = 0; y < 13; y++) {
                    PaletteTile tile = palette.getTile(x, y, z);
                    out.writeBits(1, tile != null ? 1 : 0);
                    if (tile != null) {
                        out.writeBits(
                                26,
                                tile.getX() << 14 | tile.getY() << 3 | tile
                                        .getZ() << 24 | tile.getRotation() << 1);
                    }
                }
            }
        }
        out.finishBitAccess();
        out.writeShort(player.getPosition().getRegionX() + 6);
        out.endVarShort();
        out.sendPacket();
        return this;
    }

    /**
     * Sends the head model of a player to an interface.
     * 
     * @param i
     *            the size of the head model.
     * @return this packet encoder.
     */
    public PacketEncoder sendPlayerHeadModel(int size) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(185, player.getSession()).writeShort(size, ValueType.A,
                ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Flashes the selected sidebar.
     * 
     * @param id
     *            the id of the sidebar to flash.
     * @return this packet encoder.
     */
    public PacketEncoder flashSelectedSidebar(int id) {

        // For some reason it seems as if all of the id's are negative.
        // -1, -2, -3, -4, -5, etc.
        ProtocolBuffer out = new ProtocolBuffer(2);
        out.build(24, player.getSession()).writeByte(id, ValueType.A);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the enter name interface.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder enterName() {

        // TODO: Do the packet decoding part of this.
        ProtocolBuffer out = new ProtocolBuffer(1);
        out.build(187, player.getSession());
        out.sendPacket();
        return this;
    }

    /**
     * Changes the state of the minimap.
     * 
     * @param state
     *            the new state of the minimap.
     * @return this packet encoder.
     */
    public PacketEncoder sendMapState(int state) {

        // All possible states.
        // Normal - 0
        // Normal, but unclickable - 1
        // Blacked out - 2
        ProtocolBuffer out = new ProtocolBuffer(2);
        out.build(99, player.getSession()).writeByte(state);
        out.sendPacket();
        return this;
    }

    /**
     * Resets the cameras rotation.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendResetCameraRotation() {
        ProtocolBuffer out = new ProtocolBuffer(1);
        out.build(108, player.getSession());
        out.sendPacket();
        return this;
    }

    /**
     * Spins the camera.
     * 
     * @param x
     *            the x coordinate within the loaded map.
     * @param y
     *            the y coordinate within the loaded map.
     * @param height
     *            the height of the camera.
     * @param speed
     *            the speed of the camera.
     * @param angle
     *            the angle of the camera.
     * @return this packet encoder.
     */
    public PacketEncoder sendCameraSpin(int x, int y, int height, int speed,
            int angle) {

        // TODO: Document the argued.
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(177, player.getSession()).writeByte(x / 64).writeByte(y / 64)
                .writeShort(height).writeByte(speed).writeByte(angle);
        out.sendPacket();
        return this;
    }

    /**
     * Moves the camera.
     * 
     * @param x
     *            the x coordinate within the loaded map.
     * @param y
     *            the y coordinate within the loaded map.
     * @param height
     *            the height of the camera.
     * @param speed
     *            the speed of the camera.
     * @param angle
     *            the angle of the camera.
     * @return this packet encoder.
     */
    public PacketEncoder sendCameraMovement(int x, int y, int height,
            int speed, int angle) {

        // TODO: Document the argued.
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(166, player.getSession()).writeByte(x / 64).writeByte(y / 64)
                .writeShort(height).writeByte(speed).writeByte(angle);
        out.sendPacket();
        return this;
    }

    /**
     * Shakes the screen.
     * 
     * @param intensity
     *            the intensity of the shake.
     * @return this packet encoder.
     */
    public PacketEncoder sendScreenShake(int intensity) {

        // Any intensity above 4 freezes the client as far as I know.
        if (intensity > 4) {
            throw new IllegalArgumentException("Intensity must be below 5!");
        }

        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(35, player.getSession()).writeByte(intensity)
                .writeByte(intensity).writeByte(intensity).writeByte(intensity);
        out.sendPacket();
        return this;
    }

    /**
     * Resets the position of the camera.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendResetCamera() {
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(107, player.getSession());
        out.sendPacket();
        return this;
    }

    /**
     * Plays music from the cache.
     * 
     * @param id
     *            the id of the music to play.
     * @return this packet encoder.
     */
    public PacketEncoder sendMusic(int id) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(74, player.getSession()).writeShort(id, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the system update time.
     * 
     * @param time
     *            the amount of time to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendSystemUpdate(int time) {

        // TODO: Document the argued.
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(114, player.getSession()).writeShort(time, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the energy percentage.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendEnergy() {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(110, player.getSession());
        out.writeByte(player.getRunEnergy());
        out.sendPacket();
        return this;
    }

    /**
     * Changes the line color on an interface.
     * 
     * @param line
     *            the line.
     * @param color
     *            the new color.
     * @return this packer builder.
     */
    public PacketEncoder sendLineColor(int line, int color) {

        // Used for changing the color on things like the quest interface. All
        // of the colors:
        // Red = 0x6000
        // Yellow = 0x33FF66
        // Green = 0x3366;
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(122, player.getSession())
                .writeShort(line, ValueType.A, ByteOrder.LITTLE)
                .writeShort(color, ValueType.A, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Sends an item to an interface.
     * 
     * @param id
     *            the id of the item.
     * @param zoom
     *            the zoom of the item.
     * @param model
     *            the model of the item.
     * @return this packet encoder.
     */
    public PacketEncoder sendItemOnInterface(int id, int zoom, int model) {
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(246, player.getSession())
                .writeShort(id, ProtocolBuffer.ByteOrder.LITTLE)
                .writeShort(zoom).writeShort(model);
        out.sendPacket();
        return this;
    }

    /**
     * Creates a projectile for the specified player.
     * 
     * @param position
     *            the position of the projectile.
     * @param offset
     *            the offset position of the projectile.
     * @param angle
     *            the angle of the projectile.
     * @param speed
     *            the speed of the projectile.
     * @param gfxMoving
     *            the rate that projectile gfx moves in.
     * @param startHeight
     *            the starting height of the projectile.
     * @param endHeight
     *            the ending height of the projectile.
     * @param lockon
     *            the lockon value of this projectile.
     * @param time
     *            the time it takes for this projectile to hit its desired
     *            position.
     * @return this packet encoder.
     */
    public PacketEncoder sendProjectile(Position position, Position offset,
            int angle, int speed, int gfxMoving, int startHeight,
            int endHeight, int lockon, int time) {
        this.sendCoordinates(position);
        ProtocolBuffer out = new ProtocolBuffer(16);
        out.build(117, player.getSession()).writeByte(angle)
                .writeByte(offset.getY()).writeByte(offset.getX())
                .writeShort(lockon).writeShort(gfxMoving)
                .writeByte(startHeight).writeByte(endHeight).writeShort(time)
                .writeShort(speed).writeByte(16).writeByte(64);
        out.sendPacket();
        return this;
    }

    /**
     * Creates a global projectile.
     * 
     * @param position
     *            the position of the projectile.
     * @param offset
     *            the offset position of the projectile.
     * @param angle
     *            the angle of the projectile.
     * @param speed
     *            the speed of the projectile.
     * @param gfxMoving
     *            the rate that projectile gfx moves in.
     * @param startHeight
     *            the starting height of the projectile.
     * @param endHeight
     *            the ending height of the projectile.
     * @param lockon
     *            the lockon value of this projectile.
     * @param time
     *            the time it takes for this projectile to hit its desired
     *            position.
     * @return this packet encoder.
     */
    public void sendAllProjectile(Position position, Position offset,
            int angle, int speed, int gfxMoving, int startHeight,
            int endHeight, int lockon, int time) {
        for (Player all : player.getLocalPlayers()) {
            if (all == null) {
                continue;
            }

            if (all.getPosition().isViewableFrom(position)) {
                all.getPacketBuilder().sendProjectile(position, offset, angle,
                        speed, gfxMoving, startHeight, endHeight, lockon, time);
            }
        }
    }

    /**
     * Sends a client config.
     * 
     * @param id
     *            the id of the config.
     * @param state
     *            the state to put this config in.
     * @return this packet encoder.
     */
    public PacketEncoder sendConfig(int id, int state) {
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(36, player.getSession());
        out.writeShort(id, ByteOrder.LITTLE).writeByte(state);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the image of an object to the world.
     * 
     * @param object
     *            the object to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(151, player.getSession())
                .writeByte(0, ValueType.S)
                .writeShort(object.getId(), ByteOrder.LITTLE)
                .writeByte(
                        (object.getType() << 2) + (object.getRotation()
                                .ordinal() & 3), ValueType.S);
        out.sendPacket();
        return this;
    }

    /**
     * Removes the image of an object from the world.
     * 
     * @param object
     *            the object to remove.
     * @return this packet encoder.
     */
    public PacketEncoder sendRemoveObject(WorldObject object) {
        sendCoordinates(object.getPosition());
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(101, player.getSession())
                .writeByte(
                        (object.getType() << 2) + (object.getRotation()
                                .ordinal() & 3), ValueType.C).writeByte(0);
        out.sendPacket();
        return this;
    }

    /**
     * Replaces the image of an object with a new one.
     * 
     * @param position
     *            the position of the old object.
     * @param object
     *            the new object to take its place.
     * @return this packet encoder.
     */
    public PacketEncoder sendReplaceObject(Position position, int object) {
        sendRemoveObject(new WorldObject(0, position, Rotation.SOUTH, 10));
        sendObject(new WorldObject(object, position, Rotation.SOUTH, 10));
        return this;
    }

    /**
     * Sends the players skills to the client.
     * 
     * @param skillID
     *            the id of the skill being sent.
     * @param level
     *            the level of the skill being sent.
     * @param exp
     *            the experience of the skill being sent.
     * @return this packet encoder.
     */
    public PacketEncoder sendSkill(int skillID, int level, int exp) {
        ProtocolBuffer out = new ProtocolBuffer(8);
        out.build(134, player.getSession()).writeByte(skillID)
                .writeInt(exp, ByteOrder.MIDDLE).writeByte(level);
        out.sendPacket();
        return this;
    }

    /**
     * Closes any interfaces this player has open.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendCloseWindows() {
        ProtocolBuffer out = new ProtocolBuffer(1);
        out.build(219, player.getSession());
        out.sendPacket();
        return this;
    }

    /**
     * Sends the list of people you have on your friends and ignores list.
     * 
     * @param i
     *            the world you're in? Not completely sure what this is.
     * @return this packet encoder.
     */
    public PacketEncoder sendPrivateMessagingList(int i) {
        ProtocolBuffer out = new ProtocolBuffer(2);
        out.build(221, player.getSession()).writeByte(i);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the chat options.
     * 
     * @param publicChat
     *            the public chat option.
     * @param privateChat
     *            the private chat option.
     * @param tradeBlock
     *            the trade/challenge option.
     * @return this packet encoder.
     */
    public PacketEncoder sendChatOptions(int publicChat, int privateChat,
            int tradeBlock) {
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(206, player.getSession()).writeByte(publicChat)
                .writeByte(privateChat).writeByte(tradeBlock);
        out.sendPacket();
        return this;
    }

    /**
     * Loads a player in your friends list.
     * 
     * @param playerName
     *            the player's name.
     * @param world
     *            the world they are on.
     * @return this packet encoder.
     */
    public PacketEncoder loadPrivateMessage(long playerName, int world) {
        if (world != 0) {
            world += 9;
        }

        ProtocolBuffer out = new ProtocolBuffer(10);
        out.build(50, player.getSession()).writeLong(playerName)
                .writeByte(world);
        out.sendPacket();
        return this;
    }

    /**
     * Sends a hint arrow on the specified object.
     * 
     * @param object
     *            the position of the object to send the arrow on.
     * @param position
     *            the position of the arrow on the object.
     * @return this packet encoder.
     */
    public PacketEncoder sendObjectHintArrow(Position object, int position) {

        // Sends the hint arrow on a certain tile. The positions the hint arrow
        // can be in:
        // Middle - 2
        // West - 3
        // East - 4
        // South - 5
        // North - 6
        ProtocolBuffer out = new ProtocolBuffer(7);
        out.build(254, player.getSession()).writeByte(position)
                .writeShort(object.getX()).writeShort(object.getY())
                .writeByte(object.getZ());
        out.sendPacket();
        return this;
    }

    /**
     * Send a private message to another player.
     * 
     * @param name
     *            the name of the player you are sending the message to.
     * @param rights
     *            your player rights.
     * @param chatMessage
     *            the message.
     * @param messageSize
     *            the message size.
     * @return this packet encoder.
     */
    public PacketEncoder sendPrivateMessage(long name, int rights,
            byte[] chatMessage, int messageSize) {
        ProtocolBuffer out = new ProtocolBuffer(messageSize + 15);
        out.buildVar(196, player.getSession()).writeLong(name)
                .writeInt(player.getPrivateMessage().getLastId())
                .writeByte(rights).writeBytes(chatMessage, messageSize)
                .endVar();
        out.sendPacket();
        return this;
    }

    /**
     * Sends a hint arrow on an entity.
     * 
     * @param type
     *            the type of entity.
     * @param id
     *            the id of the entity.
     * @return this packet encoder.
     */
    public PacketEncoder sendEntityHintArrow(int type, int id) {

        // The types are:
        // Npc - 1
        // Player - 10
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(254, player.getSession()).writeByte(type).writeShort(id)
                .writeByte(0);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the players current coordinates to the client.
     * 
     * @param position
     *            the coordinates.
     * @return this packet encoder.
     */
    public PacketEncoder sendCoordinates(Position position) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(85, player.getSession())
                .writeByte(
                        position.getY() - (player.getCurrentRegion()
                                .getRegionY() * 8), ValueType.C)
                .writeByte(
                        position.getX() - (player.getCurrentRegion()
                                .getRegionX() * 8), ValueType.C);
        out.sendPacket();
        return this;
    }

    /**
     * Opens a walkable interface for this player.
     * 
     * @param id
     *            the walkable interface to open.
     * @return this packet encoder.
     */
    public PacketEncoder sendWalkable(int id) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(208, player.getSession()).writeShort(id, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the image of a ground item to the world.
     * 
     * @param item
     *            the item to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendGroundItem(GroundItem item) {
        sendCoordinates(item.getPosition());
        ProtocolBuffer out = new ProtocolBuffer(6);
        out.build(44, player.getSession())
                .writeShort(item.getItem().getId(), ValueType.A,
                        ByteOrder.LITTLE)
                .writeShort(item.getItem().getAmount()).writeByte(0);
        out.sendPacket();
        return this;
    }

    /**
     * Removes the image of a ground item from the world.
     * 
     * @param item
     *            the item to remove.
     * @return this packet encoder.
     */
    public PacketEncoder sendRemoveGroundItem(GroundItem item) {
        sendCoordinates(item.getPosition());
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(156, player.getSession()).writeByte(0, ValueType.S)
                .writeShort(item.getItem().getId());
        out.sendPacket();
        return this;
    }

    /**
     * Sends player context menus.
     * 
     * @param option
     *            the option.
     * @param slot
     *            the slot for the option to be placed in.
     * @return this packet encoder.
     */
    public PacketEncoder sendContextMenu(String option, int slot) {
        ProtocolBuffer out = new ProtocolBuffer(option.length() + 6);
        out.buildVar(104, player.getSession())
                .writeByte(slot, ProtocolBuffer.ValueType.C)
                .writeByte(0, ProtocolBuffer.ValueType.A).writeString(option)
                .endVar();
        out.sendPacket();
        return this;
    }

    /**
     * Sends a string to an interface.
     * 
     * @param text
     *            the string to send.
     * @param id
     *            where the string should be sent.
     * @return this packet encoder.
     */
    public PacketEncoder sendString(String text, int id) {
        ProtocolBuffer out = new ProtocolBuffer(text.length() + 6);
        out.buildVarShort(126, player.getSession()).writeString(text)
                .writeShort(id, ValueType.A).endVarShort();
        out.sendPacket();
        return this;
    }

    /**
     * Updates an array of items on an interface.
     * 
     * @param interfaceId
     *            the interface to send the items on.
     * @param items
     *            the items to send.
     * @param length
     *            the length of the items.
     * @return this packet encoder.
     */
    public PacketEncoder sendUpdateItems(int interfaceId, Item[] items,
            int length) {
        ProtocolBuffer out = new ProtocolBuffer(500);
        out.buildVarShort(53, player.getSession()).writeShort(interfaceId);
        if (items == null) {
            out.writeShort(0)
                    .writeByte(0)
                    .writeShort(0, ProtocolBuffer.ValueType.A,
                            ProtocolBuffer.ByteOrder.LITTLE).endVarShort();
            out.sendPacket();
            return this;
        }
        out.writeShort(length);
        for (Item item : items) {
            if (item != null) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(),
                            ProtocolBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }
                out.writeShort(item.getId() + 1, ProtocolBuffer.ValueType.A,
                        ProtocolBuffer.ByteOrder.LITTLE);
            } else {
                out.writeByte(0);
                out.writeShort(0, ProtocolBuffer.ValueType.A,
                        ProtocolBuffer.ByteOrder.LITTLE);
            }
        }
        out.endVarShort();
        out.sendPacket();
        return this;
    }

    /**
     * Updates an array of items on an interface.
     * 
     * @param interfaceId
     *            the interface to send the items on.
     * @param items
     *            the items to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendUpdateItems(int interfaceId, Item[] items) {
        return sendUpdateItems(interfaceId, items, items.length);
    }

    /**
     * Sends an interface to your inventory.
     * 
     * @param interfaceId
     *            the interface to send.
     * @param inventoryId
     *            the inventory to send on.
     * @return this packet encoder.
     */
    public PacketEncoder sendInventoryInterface(int interfaceId, int inventoryId) {
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(248, player.getSession());
        out.writeShort(interfaceId, ProtocolBuffer.ValueType.A);
        out.writeShort(inventoryId);
        out.sendPacket();
        return this;
    }

    /**
     * Opens an interface for this player.
     * 
     * @param interfaceId
     *            the interface to open for this player.
     * @return this packet encoder.
     */
    public PacketEncoder sendInterface(int interfaceId) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(97, player.getSession());
        out.writeShort(interfaceId);
        out.sendPacket();
        return this;
    }

    /**
     * Sends the player a message to the chatbox.
     * 
     * @param message
     *            the message to send.
     * @return this packet encoder.
     */
    public PacketEncoder sendMessage(String message) {
        ProtocolBuffer out = new ProtocolBuffer(message.length() + 3);
        out.buildVar(253, player.getSession());
        out.writeString(message);
        out.endVar();
        out.sendPacket();
        return this;
    }

    /**
     * Sends a sidebar interface.
     * 
     * @param menuId
     *            the sidebar to send the interface on.
     * @param form
     *            the interface to send on the sidebar.
     * @return this packet encoder.
     */
    public PacketEncoder sendSidebarInterface(int menuId, int form) {
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(71, player.getSession());
        out.writeShort(form);
        out.writeByte(menuId, ProtocolBuffer.ValueType.A);
        out.sendPacket();
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
        player.setUpdateRegion(true);
        ProtocolBuffer out = new ProtocolBuffer(5);
        out.build(73, player.getSession());
        out.writeShort(player.getPosition().getRegionX() + 6,
                ProtocolBuffer.ValueType.A);
        out.writeShort(player.getPosition().getRegionY() + 6);
        out.sendPacket();
        return this;
    }

    /**
     * Disconnects the player.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder sendLogout() {
        ProtocolBuffer out = new ProtocolBuffer(1);
        out.build(109, player.getSession());
        out.sendPacket();
        return this;
    }

    /**
     * Sends the slot to the client. Used for player following and other thing
     * that require the slot.
     * 
     * @return this packet builder.
     */
    public PacketEncoder sendDetails() {
        ProtocolBuffer out = new ProtocolBuffer(4);
        out.build(249, player.getSession());
        out.writeByte(1, ValueType.A);
        out.writeShort(player.getSlot(), ValueType.A, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Sends an interface to your chatbox.
     * 
     * @param frame
     *            the interface to send to the chatbox.
     * @return this packet encoder.
     */
    public PacketEncoder sendChatInterface(int frame) {
        ProtocolBuffer out = new ProtocolBuffer(3);
        out.build(164, player.getSession());
        out.writeShort(frame, ByteOrder.LITTLE);
        out.sendPacket();
        return this;
    }

    /**
     * Resets this players animation.
     * 
     * @return this packet encoder.
     */
    public PacketEncoder resetAnimation() {
        ProtocolBuffer out = new ProtocolBuffer(1);
        out.build(1, player.getSession());
        out.sendPacket();
        return this;
    }
}
