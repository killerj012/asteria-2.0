package server.world.entity.player.bot;

import java.net.Socket;

import server.core.ThreadProvider;
import server.core.task.impl.BotLoginTask;
import server.core.worker.Worker;
import server.world.World;
import server.world.entity.player.Player;
import server.world.map.Position;

/**
 * A fake player that is controlled by the server. Bots can be used to do
 * anything a normal player can do by sending data through its socket instance,
 * using update masks, and applying various functions meant for a normal player.
 * 
 * @author lare96
 */
public class Bot {

    /** Provides threads that will login bots. */
    private static ThreadProvider provider = new ThreadProvider("BotThread", Thread.MIN_PRIORITY, true, false);

    /** The username of this bot. */
    private String username;

    /** The password of this bot. */
    private String password;

    /** The position this bot will be moved to. */
    private Position position;

    /** The instance of the player created when this bot logged in. */
    private Player player;

    /**
     * The current task this bot is assigned to, a value of <code>null</code>
     * means this bot is idle.
     */
    private BotTask task;

    /** The task that will be performed as soon as the bot logs in. */
    private BotTask queuedTask;

    /** The socket used to connect by this bot. */
    private Socket socket;

    /** The worker that will be used to handle tasks assigned to this bot. */
    private Worker botWorker;

    /**
     * Create a new {@link Bot}.
     * 
     * @param username
     *        the username of this bot.
     * @param password
     *        the password of this bot.
     */
    public Bot(String username, String password, Position position) {
        this.username = username;
        this.password = password;
        this.position = position;
    }

    /**
     * Assigns a task to this bot.
     * 
     * @param newTask
     *        the task to assign to this bot.
     */
    public void assignTask(BotTask newTask) {

        /** This bot is offline. */
        if (!isOnline()) {
            queuedTask = newTask;
            return;
        }

        /** Stop any current tasks. */
        if (task != null) {
            task.stopTask(this);
            task = null;
        }

        /** Dispose of the queued task. */
        queuedTask = null;

        /** And start the new task! */
        task = newTask;
        task.fireTask(this);
    }

    /**
     * Stop any current tasks this bot is performing.
     */
    public void stopTask() {

        /** This bot is offline. */
        if (!isOnline()) {
            throw new IllegalStateException("This bot is not online!");
        }

        /** We don't have any task to stop. */
        if (task == null) {
            throw new IllegalStateException("Bot does not have any tasks to stop!");
        }

        /** And stop the task! */
        task.stopTask(this);
        task = null;
        queuedTask = null;
    }

    /**
     * Gets if this bot is online or not.
     * 
     * @return true if this bot is online.
     */
    public boolean isOnline() {
        if (player == null) {
            return false;
        }

        return World.getPlayers().contains(player);
    }

    @Override
    public String toString() {
        return "BOT(" + username + ":" + password + ")";
    }

    /**
     * Logs in this bot using the <code>loginBot</code> executor implementation.
     * 
     * @return this bot for chaining.
     */
    public Bot loginBot() {

        /** The bot has already online. */
        if (isOnline()) {
            throw new IllegalStateException("This bot is already online!");
        }

        /** Create the thread and push the login task. */
        provider.newThread(new BotLoginTask(this)).start();
        return this;
    }

    /**
     * Disposes of the bot by closing its socket.
     * 
     * @return this bot for chaining.
     * @throws Exception
     *         if any errors occur while trying to destroy the bot.
     */
    public Bot disposeBot() throws Exception {

        /** This bot is offline. */
        if (!isOnline()) {
            throw new IllegalStateException("This bot is not online!");
        }

        /** Stop its task. */
        if (task != null) {
            task.stopTask(this);
            task = null;
        }

        /** Dispose the bot. */
        socket.close();
        player = null;
        return this;
    }

    /**
     * Gets the username of this bot.
     * 
     * @return the username of this bot.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the password of this bot.
     * 
     * @return the password of this bot.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Gets the instance of the player created when this bot logged in.
     * 
     * @return the instance of the player created when this bot logged in.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the instance of the player created when this bot logged in.
     * 
     * @param player
     *        the instance of the player created when this bot logged in.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the socket used to connect by this bot.
     * 
     * @return the socket used to connect by this bot.
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Sets the socket used to connect by this bot.
     * 
     * @param socket
     *        the socket used to connect by this bot.
     */
    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    /**
     * Gets the worker that will be used to handle tasks assigned to this bot.
     * 
     * @return the worker that will be used to handle tasks assigned to this
     *         bot.
     */
    public Worker getBotWorker() {
        return botWorker;
    }

    /**
     * Sets the worker that will be used to handle tasks assigned to this bot.
     * 
     * @param botWorker
     *        the worker that will be used to handle tasks assigned to this bot.
     */
    public void setBotWorker(Worker botWorker) {
        this.botWorker = botWorker;
    }

    /**
     * Gets the task that will be performed as soon as the bot logs in.
     * 
     * @return the task that will be performed as soon as the bot logs in.
     */
    public BotTask getQueuedTask() {
        return queuedTask;
    }

    /**
     * Gets the position this bot will be moved to.
     * 
     * @return the position this bot will be moved to.
     */
    public Position getPosition() {
        return position;
    }
}
