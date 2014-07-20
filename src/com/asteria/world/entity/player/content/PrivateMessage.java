package com.asteria.world.entity.player.content;

import com.asteria.world.World;
import com.asteria.world.entity.player.Player;

/**
 * Manages friend/ignore lists as well as the sending of private messages.
 * 
 * @author lare96
 */
public class PrivateMessage {

    /** The player instance. */
    private Player player;

    /** The last private message ID. */
    private int lastId = 1;

    /**
     * Create a new {@link PrivateMessage}.
     * 
     * @param player
     *            the player instance.
     */
    public PrivateMessage(Player player) {
        this.player = player;
    }

    /**
     * Updates this player's friend list with all of the friends they have
     * added.
     */
    public void updateThisList() {

        // Loop through all of your friends and update their statuses.
        for (long name : player.getFriends()) {
            if (name == 0) {
                continue;
            }
            Player load = World.getPlayerByHash(name);
            player.getPacketBuilder().loadPrivateMessage(name,
                    load == null ? 0 : 1);
        }

    }

    /**
     * Updates everyone else's friends list with whether this player is online
     * or offline.
     * 
     * @param online
     *            <code>true</code> if the player should be updated on their
     *            lists with an online status, or false if the player should be
     *            updated with an <code>offline</code>.
     */
    public void updateOtherList(boolean online) {

        // Loop through everyone and update them with your status.
        for (Player players : World.getPlayers()) {
            if (players == null) {
                continue;
            }

            if (players.getFriends().contains(player.getUsernameHash())) {
                players.getPacketBuilder().loadPrivateMessage(
                        player.getUsernameHash(), !online ? 0 : 1);
            }
        }
    }

    /**
     * Adds someone to your friends list.
     * 
     * @param name
     *            the name hash of the person to add.
     */
    public void addFriend(long name) {

        // Return if the friends list is full.
        if (player.getFriends().size() >= 200) {
            player.getPacketBuilder().sendMessage("Your friends list is full.");
            return;
        }

        // Return if the person you are trying to add is already on your friends
        // list.
        if (player.getFriends().contains(name)) {
            player.getPacketBuilder().sendMessage(
                    "They are already on your friends list.");
            return;
        }

        // Add the name to your friends list and update it with online or
        // offline.
        Player load = World.getPlayerByHash(name);

        player.getFriends().add(name);
        player.getPacketBuilder()
                .loadPrivateMessage(name, load == null ? 0 : 1);
    }

    /**
     * Adds someone to your ignores list.
     * 
     * @param name
     *            the name hash of the person to add.
     */
    public void addIgnore(long name) {

        // Return if the ignores list is full.
        if (player.getIgnores().size() >= 100) {
            player.getPacketBuilder().sendMessage("Your ignores list is full.");
            return;
        }

        // Return if the person you are trying to add is already on your ignores
        // list.
        if (player.getIgnores().contains(name)) {
            player.getPacketBuilder().sendMessage(
                    "They are already on your ignores list.");
            return;
        }

        // Add the name to your ignores list.
        player.getIgnores().add(name);
    }

    /**
     * Removes someone from your friends list.
     * 
     * @param name
     *            the name hash of the person to remove.
     */
    public void removeFriend(long name) {

        // Remove the person from your friends list.
        if (player.getFriends().contains(name)) {
            player.getFriends().remove(name);
        } else {
            player.getPacketBuilder().sendMessage(
                    "They are not on your friends list.");
        }
    }

    /**
     * Removes someone from your ignores list.
     * 
     * @param name
     *            the name hash of the person to remove.
     */
    public void removeIgnore(long name) {

        // Remove the person from your ignores list.
        if (player.getIgnores().contains(name)) {
            player.getIgnores().remove(name);
        } else {
            player.getPacketBuilder().sendMessage(
                    "They are not on your ignores list.");
        }
    }

    /**
     * Sends a private message to another player.
     * 
     * @param sendingFrom
     *            the player sending the message.
     * @param sendingTo
     *            the player being sent the message.
     * @param message
     *            the message packed in an array.
     * @param messageSize
     *            the total size of the message.
     */
    public void sendPrivateMessage(Player sendingFrom, long sendingTo,
            byte[] message, int messageSize) {
        Player send = World.getPlayerByHash(sendingTo);

        if (send != null) {
            send.getPacketBuilder().sendPrivateMessage(
                    sendingFrom.getUsernameHash(),
                    sendingFrom.getRights().getProtocolValue(), message,
                    messageSize);
        }
    }

    /**
     * Gets and increments your last private messaging ID.
     * 
     * @return your last private messaging ID, will increase by 1 every time
     *         this method is called.
     */
    public int getLastId() {
        return lastId++;
    }
}
