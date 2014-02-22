package server.world.entity.player.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import server.Main;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerFileEvent;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

/**
 * A implementation of {@link PlayerFileEvent} that writes (saves) a character
 * file.
 * 
 * @author lare96
 */
public class WritePlayerFileEvent extends PlayerFileEvent {

    /**
     * Create a new {@link WritePlayerFileEvent}.
     * 
     * @param player
     *        the player who's character file will be written to.
     */
    public WritePlayerFileEvent(Player player) {
        super(player);

        if (!file().exists()) {
            try {
                file().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (file().exists() && !this.getPlayer().isIncorrectPassword()) {
            try {
                final Gson builder = new GsonBuilder().setPrettyPrinting().create();
                final JsonObject object = new JsonObject();

                object.addProperty("username", getPlayer().getUsername().trim());
                object.addProperty("password", getPlayer().getPassword().trim());
                object.addProperty("x", new Integer(getPlayer().getPosition().getX()));
                object.addProperty("y", new Integer(getPlayer().getPosition().getY()));
                object.addProperty("z", new Integer(getPlayer().getPosition().getZ()));
                object.addProperty("staff-rights", new Integer(getPlayer().getStaffRights()));
                object.addProperty("gender", new Integer(getPlayer().getGender()));
                object.add("appearance", builder.toJsonTree(getPlayer().getAppearance()));
                object.add("colors", builder.toJsonTree(getPlayer().getColors()));
                object.addProperty("run-toggled", new Boolean(getPlayer().getMovementQueue().isRunToggled()));
                object.addProperty("new-player", new Boolean(getPlayer().isNewPlayer()));
                object.add("inventory", builder.toJsonTree(getPlayer().getInventory().getContainer().toArray()));
                object.add("bank", builder.toJsonTree(getPlayer().getBank().getContainer().toArray()));
                object.add("equipment", builder.toJsonTree(getPlayer().getEquipment().getContainer().toArray()));
                object.add("skills", builder.toJsonTree(getPlayer().getSkills()));
                object.add("friends", builder.toJsonTree(getPlayer().getFriends().toArray()));
                object.add("ignores", builder.toJsonTree(getPlayer().getIgnores().toArray()));
                object.addProperty("run-energy", new Integer(getPlayer().getRunEnergy()));
                object.addProperty("spell-book", getPlayer().getSpellbook().name());
                object.addProperty("is-banned", new Boolean(getPlayer().isBanned()));
                object.addProperty("auto-retaliate", new Boolean(getPlayer().isAutoRetaliate()));
                object.add("unlocked-music", builder.toJsonTree(getPlayer().getMusicSet().getUnlocked().toArray()));

                FileWriter fileWriter = new FileWriter(file());
                fileWriter.write(builder.toJson(object));
                fileWriter.flush();
                fileWriter.close();
            } catch (Exception e) {
                e.printStackTrace();
                Main.getLogger().info("Error while writing data for " + getPlayer());
            }
        }
    }

    @Override
    public File file() {
        return new File("./data/players/" + getPlayer().getUsername() + ".json");
    }
}
