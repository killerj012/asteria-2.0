package server.world.music;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.world.map.Location;

/**
 * Contains static data and utility methods for managing music played by
 * {@link Location}.
 * 
 * @author lare96
 */
public class Music {

    /**
     * A map containing data for every single piece of regional music that can
     * be played in the 317 cache.
     */
    private static Map<Integer, Music> music = new HashMap<Integer, Music>();

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

    /**
     * The locations that this music is played in.
     */
    private Location[] playedIn;

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
    public Music(String name, int songId, int musicTabLineId, int musicTabButtonId, Location[] playedIn) {
        this.name = name;
        this.songId = songId;
        this.musicTabLineId = musicTabLineId;
        this.musicTabButtonId = musicTabButtonId;
        this.playedIn = playedIn;
    }

    /**
     * Gets a modifiable list of locations that this song is played in.
     * 
     * @param name
     *        the name of this song.
     * @return a modifiable list of locations that this song is played in.
     */
    public static List<Location> getLocationPlayedIn(String name) {

        /** Get the music instance by name. */
        Music music = getMusic(name);

        /** Check if this is a valid song. */
        if (music == null) {
            throw new IllegalArgumentException("Invalid song requested!");
        }

        /** Return the array of areas as a list. */
        return music.getLocationList();
    }

    /**
     * Gets a music instance by its name (case sensitive).
     * 
     * @param name
     *        the name of the music you want to retrieve an instance of.
     * @return the instance of the music.
     */
    public static Music getMusic(String name) {
        for (Music m : music.values()) {
            if (m.getName().equalsIgnoreCase(name)) {
                return m;
            }
        }
        return null;
    }

    /**
     * Gets an unmodifiable view of this map.
     * 
     * @return an unmodifiable view of this map.
     */
    public static Map<Integer, Music> getMusic() {
        return music;
    }

    /**
     * Gets a modifiable list of locations that this song is played in.
     * 
     * @return a modifiable list of locations that this song is played in.
     */
    public List<Location> getLocationList() {

        /** Return the array of areas as a list. */
        return Arrays.asList(playedIn);
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
    public Location[] getPlayedIn() {
        return playedIn;
    }
}
