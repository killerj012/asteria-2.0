package server.world.music;

import server.world.map.Location;

/**
 * Contains static data and utility methods for managing music played by
 * {@link Location}.
 * 
 * @author lare96
 */
public class Music {

    // XXX: Still a work in progress..

    /**
     * A map containing data for every single piece of regional music that can
     * be played in the 317 cache.
     */
    private static Music[] music = new Music[400];

    private static Music[] musicRegion = new Music[20000];

    // private static Map<Integer, Music> music = new HashMap<Integer, Music>();
    //
    // private static Map<Integer, Music> musicLocation = new HashMap<Integer,
    // Music>();

    /**
     * The name of this music.
     */
    private String name;

    /**
     * The id of this music.
     */
    private int songId;

    /**
     * The music tab line id (for unlocking).
     */
    private int musicTabLineId;

    /**
     * The music tab action button (for playing).
     */
    private int musicTabButtonId;

    private String unlockDescription;

    /**
     * The locations that this music is played in.
     */
    private int[] playedIn;

    /**
     * Create a new {@link Music} instance.
     * 
     * @param name
     *        the name of this music.
     * @param songId
     *        the id of this music.
     * @param musicTabLineId
     *        the music tab line id (for unlocking).
     * @param musicTabButtonId
     *        the music tab action button (for playing).
     * @param playedIn
     *        the locations that this music is played in.
     */
    public Music(String name, int songId, int musicTabLineId, int musicTabButtonId, String unlockDescription, int[] playedIn) {
        this.name = name;
        this.songId = songId;
        this.musicTabLineId = musicTabLineId;
        this.musicTabButtonId = musicTabButtonId;
        this.playedIn = playedIn;
        this.unlockDescription = unlockDescription;
    }

    /**
     * Gets an unmodifiable view of this map.
     * 
     * @return an unmodifiable view of this map.
     */
    public static Music[] getMusic() {
        return music;
    }

    /**
     * Gets the name of this music.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the id of this music.
     * 
     * @return the song id.
     */
    public int getSongId() {
        return songId;
    }

    /**
     * Gets the music tab line id (for unlocking).
     * 
     * @return the music tab line id.
     */
    public int getMusicTabLineId() {
        return musicTabLineId;
    }

    /**
     * Gets the music tab action button (for playing).
     * 
     * @return the music tab button id.
     */
    public int getMusicTabButtonId() {
        return musicTabButtonId;
    }

    /**
     * Gets the locations that this music is played in.
     * 
     * @return the played in.
     */
    public int[] getPlayedIn() {
        return playedIn;
    }

    /**
     * @return the musicLocation
     */
    public static Music[] getMusicRegion() {
        return musicRegion;
    }

    /**
     * @return the unlockDescription
     */
    public String getUnlockDescription() {
        return unlockDescription;
    }

}
