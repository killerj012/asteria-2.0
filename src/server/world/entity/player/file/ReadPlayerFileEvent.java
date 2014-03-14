package server.world.entity.player.file;

import java.io.File;
import java.io.FileReader;
import java.util.logging.Logger;

import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerFileEvent;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.skill.Skill;
import server.world.entity.player.skill.SkillManager;
import server.world.item.Item;
import server.world.map.Position;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A implementation of a {@link PlayerFileEvent} that reads (loads) a character
 * file.
 * 
 * @author lare96
 */
public class ReadPlayerFileEvent extends PlayerFileEvent {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(ReadPlayerFileEvent.class.getName());

    /** Used to determine the client's response to the login request. */
    private int returnCode = Misc.LOGIN_RESPONSE_OK;

    /**
     * Create a new {@link ReadPlayerFileEvent}.
     * 
     * @param player
     *        the player who's character file will be read.
     */
    public ReadPlayerFileEvent(Player player) {
        super(player);

        if (!file().exists()) {
            SkillManager.login(player);
            logger.info(player + " is logging in for the first time!");
            returnCode = Misc.LOGIN_RESPONSE_OK;
        }
    }

    // XXX: If you're trying to load character data remember to do it twice!!
    // once in the cache if-block and one in the disk i/o if-block. You'll see
    // what I'm talking about below! Will think of a new design for this soon
    // where its only done once but for now do it twice.

    @Override
    public void run() {

        /** If we have the player cached no need for disk i/o. */
        if (World.getCachedPlayers().containsKey(getPlayer().getUsername())) {
            Player cachedData = World.getCachedPlayers().get(getPlayer().getUsername());

            getPlayer().setUsername(cachedData.getUsername());

            if (!getPlayer().getPassword().equals(cachedData.getPassword())) {
                getPlayer().setIncorrectPassword(true);
                returnCode = Misc.LOGIN_RESPONSE_INVALID_CREDENTIALS;
                return;
            }

            getPlayer().setPassword(cachedData.getPassword());
            getPlayer().getPosition().setAs(cachedData.getPosition());
            getPlayer().setStaffRights(cachedData.getStaffRights());
            getPlayer().setGender(cachedData.getGender());
            getPlayer().setAppearance(cachedData.getAppearance());
            getPlayer().setColors(cachedData.getColors());
            getPlayer().getMovementQueue().setRunToggled(cachedData.getMovementQueue().isRunToggled());
            getPlayer().setNewPlayer(cachedData.isNewPlayer());
            getPlayer().getInventory().getContainer().setItems(cachedData.getInventory().getContainer().toArray());
            getPlayer().getBank().getContainer().setItems(cachedData.getBank().getContainer().toArray());
            getPlayer().getEquipment().getContainer().setItems(cachedData.getEquipment().getContainer().toArray());
            getPlayer().setTrainable(cachedData.getSkills());
            getPlayer().setRunEnergy(cachedData.getRunEnergy());
            getPlayer().setBanned(cachedData.isBanned());
            getPlayer().setAutoRetaliate(cachedData.isAutoRetaliate());

            if (cachedData.getSpellbook() == null) {
                getPlayer().setSpellbook(Spellbook.NORMAL);
            } else {
                getPlayer().setSpellbook(cachedData.getSpellbook());
            }

            for (Long l : cachedData.getFriends()) {
                getPlayer().getFriends().add(l);
            }

            for (Long l : cachedData.getIgnores()) {
                getPlayer().getIgnores().add(l);
            }

            for (int i : cachedData.getMusicSet().getUnlocked()) {
                getPlayer().getMusicSet().getUnlocked().add(i);
            }

            World.getCachedPlayers().remove(cachedData.getUsername());
            return;
        }

        /** If we don't perform disk i/o as normal. */
        try {
            final JsonParser fileParser = new JsonParser();
            final Gson builder = new GsonBuilder().create();
            final Object object = fileParser.parse(new FileReader(file()));
            final JsonObject reader = (JsonObject) object;

            final String username = reader.get("username").getAsString();
            final String password = reader.get("password").getAsString();
            final Position position = new Position(reader.get("x").getAsInt(), reader.get("y").getAsInt(), reader.get("z").getAsInt());
            final int staffRights = reader.get("staff-rights").getAsInt();
            final int gender = reader.get("gender").getAsInt();
            final int[] appearance = builder.fromJson(reader.get("appearance").getAsJsonArray(), int[].class);
            final int[] colors = builder.fromJson(reader.get("colors").getAsJsonArray(), int[].class);
            final boolean runToggled = reader.get("run-toggled").getAsBoolean();
            final boolean newPlayer = reader.get("new-player").getAsBoolean();
            final Item[] inventory = builder.fromJson(reader.get("inventory").getAsJsonArray(), Item[].class);
            final Item[] bank = builder.fromJson(reader.get("bank").getAsJsonArray(), Item[].class);
            final Item[] equipment = builder.fromJson(reader.get("equipment").getAsJsonArray(), Item[].class);
            final Skill[] skills = builder.fromJson(reader.get("skills").getAsJsonArray(), Skill[].class);
            final Long[] friends = builder.fromJson(reader.get("friends").getAsJsonArray(), Long[].class);
            final Long[] ignores = builder.fromJson(reader.get("ignores").getAsJsonArray(), Long[].class);
            final int runEnergy = reader.get("run-energy").getAsInt();
            final Spellbook book = Spellbook.valueOf(reader.get("spell-book").getAsString());
            final boolean banned = reader.get("is-banned").getAsBoolean();
            final boolean retaliate = reader.get("auto-retaliate").getAsBoolean();
            final int[] unlockedMusic = builder.fromJson(reader.get("unlocked-music").getAsJsonArray(), int[].class);

            getPlayer().setUsername(username);

            if (!getPlayer().getPassword().equals(password)) {
                getPlayer().setIncorrectPassword(true);
                returnCode = Misc.LOGIN_RESPONSE_INVALID_CREDENTIALS;
                return;
            }

            getPlayer().setPassword(password);
            getPlayer().getPosition().setAs(position);
            getPlayer().setStaffRights(staffRights);
            getPlayer().setGender(gender);
            getPlayer().setAppearance(appearance);
            getPlayer().setColors(colors);
            getPlayer().getMovementQueue().setRunToggled(runToggled);
            getPlayer().setNewPlayer(newPlayer);
            getPlayer().getInventory().getContainer().setItems(inventory);
            getPlayer().getBank().getContainer().setItems(bank);
            getPlayer().getEquipment().getContainer().setItems(equipment);
            getPlayer().setTrainable(skills);
            getPlayer().setRunEnergy(runEnergy);
            getPlayer().setBanned(banned);
            getPlayer().setAutoRetaliate(retaliate);

            if (book == null) {
                getPlayer().setSpellbook(Spellbook.NORMAL);
            } else {
                getPlayer().setSpellbook(book);
            }

            for (Long l : friends) {
                getPlayer().getFriends().add(l);
            }

            for (Long l : ignores) {
                getPlayer().getIgnores().add(l);
            }

            for (int i : unlockedMusic) {
                getPlayer().getMusicSet().getUnlocked().add(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Error while reading data for " + getPlayer());
            returnCode = Misc.LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN;
        }
    }

    @Override
    public File file() {
        return new File("./data/players/" + getPlayer().getUsername() + ".json");
    }

    /**
     * Gets the return code that will be used to determine the client's response
     * to the login request.
     * 
     * @return the return code.
     */
    public int getReturnCode() {
        return returnCode;
    }

    // /**
    // * A container that contains all of the player credentials for logging in.
    // *
    // * @author lare96
    // */
    // private static class Credentials {
    //
    // /** All of the credentials. */
    // private String username, password;
    // private Position position;
    // private int staffRights, gender, runEnergy;
    // private int[] appearance, colors, unlockedMusic;
    // private boolean runToggled, newPlayer, banned, retaliate;
    // private Item[] inventory, bank, equipment;
    // private Skill[] skills;
    // private Long[] friends, ignores;
    // private Spellbook book;
    // }
}
