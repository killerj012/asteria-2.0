package server.world.entity.player.skill.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import server.core.Rs2Engine;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.item.ground.RegisterableWorldItem;
import server.world.item.ground.StaticWorldItem;
import server.world.map.Position;
import server.world.object.RegisterableWorldObject;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * Handles the firemaking skill. This has support for walking to the west when
 * lit, fires burning out, and the inability to light fires on top of another
 * fire or item.
 * 
 * @author lare96
 */
public class Firemaking extends SkillEvent {

    /**
     * The {@link Firemaking} singleton instance.
     */
    private static Firemaking singleton;

    /**
     * A list which contains the positions all the fires lit. We need this so we
     * can keep track of which spots in the world have fires on them.
     */
    private static List<Position> fireDatabase = new ArrayList<Position>();

    /**
     * All of the logs we are able to light with a tinderbox.
     * 
     * @author lare96
     */
    public enum Log {
        NORMAL(1511, 2, 1, 40, 30),
        OAK(1521, 2, 15, 60, 50),
        WILLOW(1519, 3, 30, 90, 80),
        MAPLE(1517, 4, 45, 135, 100),
        YEW(1515, 5, 60, 202, 150),
        MAGIC(1513, 6, 75, 303, 200);

        /** The item id of the log. */
        private int logId;

        /** The speed it takes to light this log. */
        private int lightSpeed;

        /** The level needed to light this log. */
        private int level;

        /** The experience given when burned. */
        private int experience;

        /** The time it takes to finish burning and turn into ashes (in seconds). */
        private int burnTime;

        /** A map that allows us to retrieve a {@link Log} by its item id. */
        private static Map<Integer, Log> logMap = new HashMap<Integer, Log>();

        /** Fill the map with the correct data. */
        static {
            for (Log l : Log.values()) {
                logMap.put(l.getLogId(), l);
            }
        }

        /**
         * Create a new {@link Log}.
         * 
         * @param logId
         *        the item id of the log.
         * @param lightSpeed
         *        how long it takes to light this log.
         * @param level
         *        the level needed to light this log.
         * @param experience
         *        the experienced gained from lighting this log.
         * @param burnTime
         *        the time before this log turns into ashes.
         */
        Log(int logId, int lightSpeed, int level, int experience, int burnTime) {
            this.logId = logId;
            this.lightSpeed = lightSpeed;
            this.level = level;
            this.experience = experience;
            this.burnTime = burnTime;
        }

        /**
         * Gets the item id of the log.
         * 
         * @return the item id.
         */
        public int getLogId() {
            return logId;
        }

        /**
         * Gets how long it takes to light this log.
         * 
         * @return the light speed.
         */
        public int getLightSpeed() {
            return lightSpeed;
        }

        /**
         * Gets the level needed to light this log.
         * 
         * @return the level needed.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the experienced gained from lighting this log.
         * 
         * @return the experience gained.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Gets the time before this log turns into ashes.
         * 
         * @return the burn time.
         */
        public int getBurnTime() {
            return burnTime;
        }

        /**
         * Gets an {@link Log} instance by the item id.
         * 
         * @param id
         *        the id to get the instance of.
         * @return the {@link Log} instance.
         */
        public static Log getLog(int id) {
            return logMap.get(id);
        }
    }

    /**
     * Starts the firemaking process.
     * 
     * @param player
     *        the player lighting this log.
     * @param log
     *        the type of log being lit.
     */
    public void lightLog(final Player player, final Log log) {

        /** Block if this is a malformed log. */
        if (log == null) {
            return;
        }

        /** Block if we are already lighting a fire. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            player.getPacketBuilder().sendMessage("You are already lighting a fire!");
            return;
        }

        /** Block if we aren't a high enough level to light this log. */
        if (!player.getSkills()[Misc.FIREMAKING].reqLevel(log.getLevel())) {
            player.getPacketBuilder().sendMessage("You need a firemaking level of " + log.getLevel() + " to light " + log.name().toLowerCase().replaceAll("_", " ") + " logs.");
            return;
        }

        /** Block if an item or fire is on this position. */
        if (RegisterableWorldItem.getSingleton().searchDatabasePosition(player.getPosition()) || fireDatabase.contains(player.getPosition())) {
            player.getPacketBuilder().sendMessage("You cannot light a fire here!");
            return;
        }

        /** Prepare for firemaking. */
        player.getMovementQueue().reset();
        player.getSkillEvent()[eventFireIndex()] = true;

        /** Start firemaking. */
        Rs2Engine.getWorld().submit(new Worker(Misc.getRandom().nextInt(getLightTime(player, log)) + 1, false, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {

                /** Block if we are no longer firemaking. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Light the log and start a task to handle the ashes. */
                player.getMovementQueue().walk(-1, 0);
                player.getInventory().deleteItem(new Item(log.getLogId()));
                exp(player, log.getExperience());
                burnLog(player, log);
                fireResetEvent(player);
                player.getSkillEvent()[eventFireIndex()] = false;
                this.cancel();
            }
        }.attach(player));

        /**
         * A separate task that plays the animation because it runs on a strict
         * time of 3 ticks.
         */
        Rs2Engine.getWorld().submit(new Worker(3, true) {
            @Override
            public void fire() {

                /** Block if we are no longer firemaking. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Play the animation. */
                player.animation(new Animation(733));
            }
        }.attach(player));
    }

    /**
     * Starts a task that will turn the lit log into ashes.
     * 
     * @param player
     *        the player who lit this log.
     * @param log
     *        the log that needs to be turned into ashes.
     */
    private void burnLog(Player player, Log log) {
        final Position logPosition = new Position(player.getPosition().getX(), player.getPosition().getY(), player.getPosition().getZ());
        final WorldObject fire = new WorldObject(2732, logPosition, Rotation.SOUTH, 10);

        /** Make and register the fire. */
        fireDatabase.add(logPosition);
        RegisterableWorldObject.getSingleton().register(fire);

        /** Unregister the fire and replace it with ashes after the delay */
        Rs2Engine.getWorld().submit(new Worker(log.getBurnTime(), false, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {
                fireDatabase.remove(logPosition);
                RegisterableWorldObject.getSingleton().unregister(fire);
                RegisterableWorldItem.getSingleton().register(new StaticWorldItem(new Item(592, 1), logPosition, true, false));
                this.cancel();
            }
        });
    }

    /**
     * Get the time it takes to light the log based on the log you're lighting
     * and your firemaking level.
     * 
     * @param player
     *        the player lighting this log.
     * @param log
     *        the type of log being lit.
     * @return the time it will take to light the log.
     */
    private int getLightTime(Player player, Log log) {
        if (player.getSkills()[Misc.FIREMAKING].getLevel() <= 45) {
            return log.getLightSpeed() * 3;
        } else if (player.getSkills()[Misc.FIREMAKING].getLevel() > 45 && player.getSkills()[Misc.FIREMAKING].getLevel() <= 85) {
            return log.getLightSpeed() * 2;
        } else if (player.getSkills()[Misc.FIREMAKING].getLevel() > 85) {
            return log.getLightSpeed();
        }

        return log.getLightSpeed() * 3;
    }

    /**
     * Gets the {@link Firemaking} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Firemaking getSingleton() {
        if (singleton == null) {
            singleton = new Firemaking();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.FIREMAKING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.FIREMAKING;
    }
}
