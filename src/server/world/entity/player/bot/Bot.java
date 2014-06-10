package server.world.entity.player.bot;

import java.net.Socket;

import server.core.GenericTaskPool;
import server.core.task.impl.BotLoginTask;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;

/**
 * An entity that is presented as an actual player, but is really controlled by
 * the server. Bots <b>do not</b> currently support the use of RSA, so it will
 * have to be disabled for them to login.
 * 
 * @author lare96
 */
public class Bot {

    /** All of the possible names for bots that can be generated. */
    public static final String[] BOT_NAMES = { "NotABot", "ServerBot", "TestBot", "AutoBot", "WeirdBot", "CoolBot" };

    /**
     * A {@link GenericTaskPool} that asynchronously logs in bots. We need to
     * log them in on another thread because read operations from sockets are
     * blocking which means that if we did it on the game thread, the bot would
     * never be able to log in (because the network would never get a chance to
     * push a session task!).
     */
    private static GenericTaskPool loginBot = new GenericTaskPool("BotThread", 1, Thread.MIN_PRIORITY);

    /** The username of this bot. */
    private String username;

    /** The password of this bot. */
    private String password;

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
    public Bot(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Create a new {@link Bot} with a generated username and password.
     */
    public Bot() {
        this.username = Misc.randomElement(BOT_NAMES);
        this.password = "password";
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
            throw new IllegalStateException("Bot is offline! Task queued until bot logs in!");
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
        return World.getPlayer(username) != null;
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

        /** Push the login task. */
        loginBot.execute(new BotLoginTask(this));
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
}
