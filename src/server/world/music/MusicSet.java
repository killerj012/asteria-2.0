package server.world.music;

import java.util.HashSet;
import java.util.Set;

import server.world.entity.player.Player;

/**
 * Manages locked and unlocked music for each {@link Player}.
 * 
 * @author lare96
 */
public class MusicSet {

    /**
     * An array of music id's that are automatically unlocked upon character
     * creation.
     */
    public static final int[] AUTOMATICALLY_UNLOCKED = {};

    /**
     * An instance of this player.
     */
    private Player player;

    /**
     * The music currently being played.
     */
    private int currentlyPlaying;

    /**
     * The next song.
     */
    private int nextSong;

    /**
     * A set of music that the player has already unlocked.
     */
    private Set<Integer> unlocked = new HashSet<Integer>();

    /**
     * Create a new {@link MusicSet}.
     * 
     * @param player
     *        the player to create a music set for.
     */
    public MusicSet(Player player) {
        this.player = player;

        /** Prepare the list with already unlocked music. */
        for (int i : AUTOMATICALLY_UNLOCKED) {
            unlocked.add(i);
        }
    }

    /**
     * Update the music tab with all of the unlocked music on login.
     */
    public void loginMusicTabUpdate() {
        for (int i : unlocked) {
            Music music = Music.getMusic().get(i);

            player.getPacketBuilder().sendString("", 4439);
            player.getPacketBuilder().sendString("@gre@" + music.getName(), music.getMusicTabLineId());
        }
    }

    /**
     * Unlocks a new song for this player if needed.
     * 
     * @param songId
     *        the song to try and unlock.
     */
    public void unlock(int songId) {
        if (!unlocked.contains(songId)) {
            Music music = Music.getMusic().get(songId);
            player.getPacketBuilder().sendMessage("@red@You have unlocked " + music.getName() + "!");
            player.getPacketBuilder().sendString("@gre@" + music.getName(), music.getMusicTabLineId());
            unlocked.add(songId);
        }
    }

    /**
     * Gets music currently being played.
     * 
     * @return the currently playing.
     */
    public int getCurrentlyPlaying() {
        return currentlyPlaying;
    }

    /**
     * Sets new music currently being played.
     * 
     * @param currentlyPlaying
     *        the new music to play.
     */
    public void setCurrentlyPlaying(int currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }

    /**
     * Gets the next song to play.
     * 
     * @return the next song to play.
     */
    public int getNextSong() {
        return nextSong;
    }

    /**
     * Sets the next song to play.
     * 
     * @param nextSong
     *        the next song to play.
     */
    public void setNextSong(int nextSong) {
        this.nextSong = nextSong;
    }

    /**
     * A set of music id's that the player has already unlocked.
     * 
     * @return the unlocked.
     */
    public Set<Integer> getUnlocked() {
        return unlocked;
    }
}
