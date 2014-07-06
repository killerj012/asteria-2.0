package server.world.entity.player.bot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import server.Main;
import server.core.ThreadProvider;
import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.WriteBuffer;
import server.core.worker.Worker;
import server.util.Misc;
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
    private static ThreadProvider provider = new ThreadProvider("BotThread",
            Thread.MIN_PRIORITY, true, false);

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
     *            the username of this bot.
     * @param password
     *            the password of this bot.
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
     *            the task to assign to this bot.
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
            throw new IllegalStateException(
                    "Bot does not have any tasks to stop!");
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
        provider.newThread(new Runnable() {
            @Override
            public void run() {
                try {
                    /** Initialize the connection. */
                    Bot bot = Bot.this;
                    bot.setSocket(new Socket());
                    bot.getSocket()
                            .connect(
                                    new InetSocketAddress("127.0.0.1",
                                            Main.PORT), 5000);
                    bot.getSocket().setSoTimeout(5000);
                    bot.getSocket().setTcpNoDelay(true);
                    SecureRandom rand = new SecureRandom();

                    /** Get the IO streams so we can write/read data. */
                    OutputStream out = bot.getSocket().getOutputStream();
                    InputStream in = bot.getSocket().getInputStream();

                    /** Send the initial request. */
                    WriteBuffer outBuffer = PacketBuffer.newWriteBuffer();
                    outBuffer.writeByte(14);
                    outBuffer.writeByte(rand.nextInt());
                    out.write(outBuffer.getBuffer().array(), 0, outBuffer
                            .getBuffer().position());

                    /** Read the initial response. **/
                    ReadBuffer inBuffer = PacketBuffer.newReadBuffer(80);
                    in.read(inBuffer.getBuffer().array(), 0, 17);
                    inBuffer.readLong();

                    int opcode = inBuffer.readByte();

                    if (opcode != 0) {
                        throw new BotLoginException(bot,
                                "invalid response opcode: " + opcode);
                    }

                    /** Initialize the ISAAC seed. **/
                    int[] seed = new int[4];
                    seed[0] = rand.nextInt();
                    seed[1] = rand.nextInt();
                    seed[2] = inBuffer.readInt();
                    seed[3] = inBuffer.readInt();

                    /** Prepare the secure block. **/
                    WriteBuffer block = PacketBuffer.newWriteBuffer();
                    block.writeByte(10); // RSA opcode.
                    block.writeInt(seed[0]);
                    block.writeInt(seed[1]);
                    block.writeInt(seed[2]);
                    block.writeInt(seed[3]);
                    block.writeInt(rand.nextInt()); // Random UID
                    block.writeString(bot.getUsername());
                    block.writeString(bot.getPassword());

                    /** Write the client information. */
                    outBuffer = PacketBuffer.newWriteBuffer();
                    outBuffer.writeByte(16);
                    outBuffer.writeByte(block.getBuffer().position() + 40);
                    outBuffer.writeByte(1);
                    outBuffer.writeShort(317);
                    outBuffer.writeByte(0);

                    for (int x = 0; x < 9; x++)
                        outBuffer.writeInt(rand.nextInt());

                    /** And append the secure block to the packet. */
                    outBuffer.writeByte(block.getBuffer().position() - 1);
                    outBuffer.writeBytes(block.getBuffer());

                    /** And ship the packet out to the server. */
                    out.write(outBuffer.getBuffer().array(), 0, outBuffer
                            .getBuffer().position());
                    out.flush();

                    /** Receive the final response. */
                    inBuffer = PacketBuffer.newReadBuffer(ByteBuffer
                            .allocate(90));
                    in.read(inBuffer.getBuffer().array(), 0, 3);
                    opcode = inBuffer.readByte();

                    if (opcode != 2) {
                        throw new BotLoginException(bot,
                                "login rejected from server, opcode: " + opcode);
                    }

                    /** Set the player instance. */
                    bot.setPlayer(World.getPlayer(Misc.nameToLong(bot
                            .getUsername())));
                    bot.getPlayer().move(bot.getPosition());

                    /** Start the queued task if we have any. */
                    if (bot.getQueuedTask() != null) {
                        bot.assignTask(bot.getQueuedTask());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return this;
    }

    /**
     * Disposes of the bot by closing its socket.
     * 
     * @return this bot for chaining.
     * @throws Exception
     *             if any errors occur while trying to destroy the bot.
     */
    public Bot disposeBot() throws Exception {

        /** This bot is offline. */
        if (!isOnline()) {
            throw new IllegalStateException("This bot is not online!");
        }

        /** Stop its task. */
        if (task != null) {
            task.stopTask(this);
            queuedTask = null;
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
     *            the instance of the player created when this bot logged in.
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
     *            the socket used to connect by this bot.
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
     *            the worker that will be used to handle tasks assigned to this
     *            bot.
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
