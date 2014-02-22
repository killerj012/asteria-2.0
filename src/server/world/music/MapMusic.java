package server.world.music;

import server.world.entity.player.Player;
import server.world.map.Location;

/**
 * Plays the appropriate music for a player when a new region is entered.
 * 
 * @author lare96
 */
public class MapMusic {

    /**
     * Loads a new song every time you enter a region.
     * 
     * @param player
     *        the player to load a new song for.
     */
    public static void loadMusic(Player player) {
        /** Check if we are using the music player manually. */

        /** If not check if there is any new music to be played. */
        player.getMusicSet().setNextSong(0);

        for (Music m : Music.getMusic().values()) {
            for (Location l : m.getLocationList()) {
                if (player.getPosition().inLocation(l)) {
                    player.getMusicSet().setNextSong(m.getSongId());
                }
            }
        }

        /** And play the new music! */
        if (player.getMusicSet().getNextSong() != 0 && player.getMusicSet().getCurrentlyPlaying() != player.getMusicSet().getNextSong()) {
            Music music = Music.getMusic().get(player.getMusicSet().getNextSong());

            player.getPacketBuilder().sendMusic(music.getSongId());
            player.getMusicSet().setCurrentlyPlaying(music.getSongId());
            player.getMusicSet().unlock(music.getSongId());
            player.getPacketBuilder().sendString(music.getName(), 4439);
        }
    }
}
