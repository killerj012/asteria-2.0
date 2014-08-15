package com.asteria.world.entity.player;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.asteria.engine.GameEngine;
import com.asteria.util.Utility;
import com.asteria.world.entity.player.content.AssignWeaponInterface.FightType;
import com.asteria.world.entity.player.content.Spellbook;
import com.asteria.world.entity.player.skill.Skill;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;
import com.asteria.world.map.Position;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * A class that will hold our two nested classes for reading data from and
 * writing data to character files.
 * 
 * @author lare96
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class PlayerFileTask {

    /** A {@code String} representation of our players directory. */
    public static final String DIR = "data/players";

    /** A {@link Logger} for printing debugging info. */
    private static final Logger logger = Logger.getLogger(PlayerFileTask.class
            .getSimpleName());

    private PlayerFileTask() {}

    /**
     * A task executed by the {@link GameEngine}'s sequential thread pool that
     * will save the player's character file.
     * 
     * @author lare96
     */
    public static class WritePlayerFileTask implements Runnable {

        /** The player who's file will be saved. */
        private Player player;

        /**
         * Create a new {@link WritePlayerFileTask}.
         * 
         * @param player
         *            the player who's file will be saved.
         */
        public WritePlayerFileTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {

            // Put a concurrent lock on the player just in case they are
            // still being modified.
            synchronized (player) {

                // Create the path and file objects.
                Path path = Paths.get(DIR, player.getUsername() + ".json");
                File file = path.toFile();

                try (FileWriter writer = new FileWriter(file)) {

                    // Check if the file exists before saving it.
                    if (!file.exists()) {
                        if (!file.createNewFile()) {
                            logger.severe("Unable to create save file for player: " + player);
                            return;
                        }
                    }

                    // Now add the properties to the json parser.
                    Gson builder = new GsonBuilder().setPrettyPrinting()
                            .create();
                    JsonObject object = new JsonObject();

                    object.addProperty("username", player.getUsername().trim());
                    object.addProperty("password", player.getPassword().trim());
                    object.add("position",
                            builder.toJsonTree(player.getPosition()));
                    object.addProperty("staff-rights", player.getRights()
                            .name());
                    object.addProperty("gender",
                            new Integer(player.getGender()));
                    object.add("appearance",
                            builder.toJsonTree(player.getAppearance()));
                    object.add("colors", builder.toJsonTree(player.getColors()));
                    object.addProperty("run-toggled", new Boolean(player
                            .getMovementQueue().isRunToggled()));
                    object.addProperty("new-player",
                            new Boolean(player.isNewPlayer()));
                    object.add(
                            "inventory",
                            builder.toJsonTree(player.getInventory()
.toArray()));
                    object.add(
                            "bank",
                            builder.toJsonTree(player.getBank()
                                    .toArray()));
                    object.add(
                            "equipment",
                            builder.toJsonTree(player.getEquipment()
.toArray()));
                    object.add("skills", builder.toJsonTree(player.getSkills()));
                    object.add("friends",
                            builder.toJsonTree(player.getFriends().toArray()));
                    object.add("ignores",
                            builder.toJsonTree(player.getIgnores().toArray()));
                    object.addProperty("run-energy",
                            new Integer(player.getRunEnergy()));
                    object.addProperty("spell-book", player.getSpellbook()
                            .name());
                    object.addProperty("is-banned",
                            new Boolean(player.isBanned()));
                    object.addProperty("auto-retaliate",
                            new Boolean(player.isAutoRetaliate()));
                    object.addProperty("fight-type", player.getFightType()
                            .name());
                    object.addProperty("skull-timer",
                            new Integer(player.getSkullTimer()));
                    object.addProperty("accept-aid",
                            new Boolean(player.isAcceptAid()));
                    object.addProperty("poison-damage",
                            new Integer(player.getPoisonDamage()));
                    object.addProperty("teleblock-timer",
                            new Integer(player.getTeleblockTimer()));
                    object.addProperty("special-amount",
                            new Integer(player.getSpecialPercentage()));

                    // And write the data to the character file!
                    writer.write(builder.toJson(object));

                    // And print an indication that we've saved it.
                    logger.info(player + " game successfully saved!");
                } catch (Exception e) {

                    // An error happened while saving.
                    logger.log(Level.WARNING,
                            "Error while saving character file!", e);
                }
            }
        }
    }

    /**
     * A result-bearing task executed on the main game thread that will load the
     * player's character file.
     * 
     * @author lare96
     */
    public static class ReadPlayerFileTask implements Callable<Integer> {

        /** The player who's file will be written to. */
        private Player player;

        /**
         * Create a new {@link ReadPlayerFileTask}.
         * 
         * @param player
         *            the player who's file will be written to.
         */
        public ReadPlayerFileTask(Player player) {
            this.player = player;
        }

        @Override
        public Integer call() {

            try {

                // Create the path and file objects.
                Path path = Paths.get(DIR, player.getUsername() + ".json");
                File file = path.toFile();

                // If the file doesn't exist, we're logging in for the first
                // time and can skip all of this.
                if (!file.exists()) {
                    Skills.create(player);
                    logger.info(player + " is logging in for the first time!");
                    return Utility.LOGIN_RESPONSE_OK;
                }

                // Now read the properties from the json parser.
                JsonParser fileParser = new JsonParser();
                Gson builder = new GsonBuilder().create();
                JsonObject reader = (JsonObject) fileParser
                        .parse(new FileReader(file));

                if (reader.has("username")) {
                    player.setUsername(reader.get("username").getAsString());
                }
                if (reader.has("password")) {
                    String password = reader.get("password").getAsString();
                    if (!player.getPassword().equals(password)) {
                        return Utility.LOGIN_RESPONSE_INVALID_CREDENTIALS;
                    }

                    player.setPassword(password);
                }
                if (reader.has("position")) {
                    player.getPosition().setAs(
                            builder.fromJson(reader.get("position"),
                                    Position.class));
                }
                if (reader.has("staff-rights")) {
                    player.setRights(PlayerRights.valueOf(reader.get(
                            "staff-rights").getAsString()));
                }
                if (reader.has("gender")) {
                    player.setGender(reader.get("gender").getAsInt());
                }
                if (reader.has("appearance")) {
                    player.setAppearance(builder.fromJson(
                            reader.get("appearance").getAsJsonArray(),
                            int[].class));
                }
                if (reader.has("colors")) {
                    player.setColors(builder.fromJson(reader.get("colors")
                            .getAsJsonArray(), int[].class));
                }
                if (reader.has("run-toggled")) {
                    player.getMovementQueue().setRunToggled(
                            reader.get("run-toggled").getAsBoolean());
                }
                if (reader.has("new-player")) {
                    player.setNewPlayer(reader.get("new-player").getAsBoolean());
                }
                if (reader.has("inventory")) {
                    player.getInventory()

                            .setItems(
                                    builder.fromJson(reader.get("inventory")
                                            .getAsJsonArray(), Item[].class));

                }
                if (reader.has("bank")) {
                    player.getBank()

                            .setItems(
                                    builder.fromJson(reader.get("bank")
                                            .getAsJsonArray(), Item[].class));
                }
                if (reader.has("equipment")) {
                    player.getEquipment()

                            .setItems(
                                    builder.fromJson(reader.get("equipment")
                                            .getAsJsonArray(), Item[].class));
                }
                if (reader.has("skills")) {
                    player.setSkills(builder.fromJson(reader.get("skills")
                            .getAsJsonArray(), Skill[].class));
                }
                if (reader.has("friends")) {
                    long[] friends = builder.fromJson(reader.get("friends")
                            .getAsJsonArray(), long[].class);

                    for (long l : friends) {
                        player.getFriends().add(l);
                    }
                }
                if (reader.has("ignores")) {
                    long[] ignores = builder.fromJson(reader.get("ignores")
                            .getAsJsonArray(), long[].class);

                    for (long l : ignores) {
                        player.getIgnores().add(l);
                    }
                }
                if (reader.has("run-energy")) {
                    player.setRunEnergy(reader.get("run-energy").getAsInt());
                }
                if (reader.has("spell-book")) {
                    player.setSpellbook(Spellbook.valueOf(reader.get(
                            "spell-book").getAsString()));
                }
                if (reader.has("is-banned")) {
                    boolean banned = reader.get("is-banned").getAsBoolean();

                    if (banned) {
                        return Utility.LOGIN_RESPONSE_ACCOUNT_DISABLED;
                    }
                    player.setBanned(banned);
                }
                if (reader.has("auto-retaliate")) {
                    player.setAutoRetaliate(reader.get("auto-retaliate")
                            .getAsBoolean());
                }
                if (reader.has("fight-type")) {
                    player.setFightType(FightType.valueOf(reader.get(
                            "fight-type").getAsString()));
                }
                if (reader.has("skull-timer")) {
                    player.setSkullTimer(reader.get("skull-timer").getAsInt());
                }
                if (reader.has("accept-aid")) {
                    player.setAcceptAid(reader.get("accept-aid").getAsBoolean());
                }
                if (reader.has("poison-damage")) {
                    player.setPoisonDamage(reader.get("poison-damage")
                            .getAsInt());
                }
                if (reader.has("teleblock-timer")) {
                    player.setTeleblockTimer(reader.get("teleblock-timer")
                            .getAsInt());
                }
                if (reader.has("special-amount")) {
                    player.setSpecialPercentage(reader.get("special-amount")
                            .getAsInt());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Utility.LOGIN_RESPONSE_COULD_NOT_COMPLETE_LOGIN;
            }
            return Utility.LOGIN_RESPONSE_OK;
        }
    }
}
