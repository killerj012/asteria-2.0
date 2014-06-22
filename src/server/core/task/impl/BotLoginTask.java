package server.core.task.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.security.SecureRandom;

import server.Main;
import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.WriteBuffer;
import server.core.task.SequentialTask;
import server.util.Misc;
import server.world.World;
import server.world.entity.player.bot.Bot;
import server.world.entity.player.bot.BotLoginException;

/**
 * An asynchronous task that logs in a server-sided bot. This has to be done on
 * another thread because its a blocking operation.
 * 
 * @author lare96
 */
public class BotLoginTask extends SequentialTask {

    /** The bot to login asynchronously. */
    private Bot bot;

    /**
     * Create a new {@link BotLoginTask}.
     * 
     * @param bot
     *        the bot to login.
     */
    public BotLoginTask(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void run() {
        try {
            /** Initialize the connection. */
            bot.setSocket(new Socket());
            bot.getSocket().connect(new InetSocketAddress("127.0.0.1", Main.PORT), 5000);
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
            out.write(outBuffer.getBuffer().array(), 0, outBuffer.getBuffer().position());

            /** Read the initial response. **/
            ReadBuffer inBuffer = PacketBuffer.newReadBuffer(80);
            in.read(inBuffer.getBuffer().array(), 0, 17);
            inBuffer.readLong();

            int opcode = inBuffer.readByte();

            if (opcode != 0) {
                throw new BotLoginException(bot, "invalid response opcode: " + opcode);
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
            out.write(outBuffer.getBuffer().array(), 0, outBuffer.getBuffer().position());
            out.flush();

            /** Receive the final response. */
            inBuffer = PacketBuffer.newReadBuffer(ByteBuffer.allocate(90));
            in.read(inBuffer.getBuffer().array(), 0, 3);
            opcode = inBuffer.readByte();

            if (opcode != 2) {
                throw new BotLoginException(bot, "login rejected from server, opcode: " + opcode);
            }

            /** Set the player instance. */
            bot.setPlayer(World.getPlayer(Misc.nameToLong(bot.getUsername())));
            bot.getPlayer().move(bot.getPosition());

            /** Start the queued task if we have any. */
            if (bot.getQueuedTask() != null) {
                bot.assignTask(bot.getQueuedTask());
            }
        } catch (IOException e) {

            /** Print the error and discard connection if it fails. */
            e.printStackTrace();
        }
    }
}