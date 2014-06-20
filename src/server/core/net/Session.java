package server.core.net;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.WriteBuffer;
import server.core.net.packet.PacketEncoder;
import server.core.worker.TaskFactory;
import server.util.Misc;
import server.world.World;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.task.CombatPoisonTask;
import server.world.entity.combat.task.CombatSkullTask;
import server.world.entity.combat.task.CombatTeleblockTask;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponAnimation;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.content.RestoreEnergyWorker;
import server.world.entity.player.content.RestoreStatWorker;
import server.world.entity.player.file.ReadPlayerFileEvent;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager;

/**
 * The class behind a Player that handles all networking-related things.
 * 
 * @author blakeman8192
 * @author lare96
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 */
public final class Session {

    /** The regex pattern used to determine valid credentials. */
    private static final Pattern PATTERN = Pattern.compile("\\w(\\w| (?! )){2,10}\\w");

    /**
     * If RSA should be decoded in the login block (set this to false if you
     * don't have RSA enabled in your client and you don't know how to get RSA
     * working).
     */
    public static final boolean DECODE_RSA = false;

    /**
     * If this is set to true, any players that login but moderators or higher
     * will be moved to random places 200 squares within the home area.
     */
    private static final boolean SOCKET_FLOOD = false;

    /** The private RSA modulus and exponent key pairs. */
    private static final BigInteger RSA_MODULUS = new BigInteger("95938610921572746524650133814858151901913076652480429598183870656291246099349831798849348614985734300731049329237933048794504022897746723376579898629175025215880393800715209863314290417958725518169765091231358927530763716352174212961746574137578805287960782611757859202906381434888168466423570348398899194541"),
            RSA_EXPONENT = new BigInteger("5378312350669976818157141639620196989298085716789189287634886259536048921510158872529601703029702119732149400119324443005798370082950416736889917871791338756888938417005708590957237003926710452309501641625737520695929480769820807041774825159548922857357239208866414166598649761006651610675718558204518453657");

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(Session.class.getSimpleName());

    /** The selection key assigned for this session. */
    private SelectionKey key;

    /** The buffer for reading data. */
    private final ByteBuffer inData;

    /** The buffer for writing data. */
    private final ByteBuffer outData;

    /** The socket channel for this session. */
    private SocketChannel socketChannel;

    /** The login stage this session is currently in. */
    private Stage stage;

    /** The packet opcode for this session. */
    private int packetOpcode = -1;

    /** The packet length for this session. */
    private int packetLength = -1;

    /** The packet encryptor for this session. */
    private ISAACCipher encryptor;

    /** The packet decryptor for this session. */
    private ISAACCipher decryptor;

    /** The player created in this session. */
    private Player player;

    /** Builds raw outgoing packets and sends them to the {@link PacketEncoder}. */
    private PacketEncoder packetBuilder;

    /** The host address for this session. */
    private String host;

    /** If the player logging in is a bot. */
    private boolean botLogin = false;

    /**
     * The current connection stage of the session.
     * 
     * @author blakeman8192
     */
    public enum Stage {
        CONNECTED, LOGGING_IN, LOGGED_IN, LOGGED_OUT
    }

    /**
     * Create a new {@link Session}.
     * 
     * @param key
     *        the selection key assigned to this session.
     */
    public Session(SelectionKey key) {
        this.key = key;
        stage = Stage.CONNECTED;
        inData = ByteBuffer.allocateDirect(512);
        outData = ByteBuffer.allocateDirect(8192);

        if (key != null) {
            socketChannel = (SocketChannel) key.channel();
            host = socketChannel.socket().getInetAddress().getHostAddress();
            player = new Player(this);
            packetBuilder = new PacketEncoder(player);
        }
    }

    /**
     * Disconnects the player from this session.
     */
    public void disconnect() {
        try {
            if (player != null) {
                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        minigame.fireOnForcedLogout(player);
                    }
                }

                if (player.getUsername() != null) {
                    player.getPrivateMessage().sendPrivateMessageOnLogout();
                }

                World.savePlayer(player);
                TaskFactory.getFactory().cancelWorkers(player);
                player.getTradeSession().resetTrade(false);
                SkillEvent.fireSkillEvents(player);

                if (World.getPlayers().contains(player)) {
                    World.getPlayers().remove(player);
                }
            }

            key.attach(null);
            key.cancel();
            stage = Stage.LOGGED_OUT;
            socketChannel.close();
            HostGateway.exit(host);
            logger.info(player + " has logged out.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a buffer to the socket.
     * 
     * @param buffer
     *        the buffer to send.
     */
    public void send(ByteBuffer buffer) {
        if (!socketChannel.isOpen())
            return;

        /** Prepare the buffer for writing. */
        buffer.flip();

        try {
            /** ...and write it! */
            socketChannel.write(buffer);

            /** If not all the data was sent. */
            if (buffer.hasRemaining()) {

                /** Queue it. */
                synchronized (outData) {
                    outData.put(buffer);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            disconnect();
        }
    }

    /**
     * Encodes and sends a packet to the socket.
     * 
     * @param buffer
     *        the packet to encode and send.
     */
    public void encode(WriteBuffer buffer) {

        /** Encode the header for this packet. */
        buffer.getBuffer().put(0, (byte) (buffer.getBuffer().array()[0] + encryptor.getKey()));

        /** And send the packet! */
        send(buffer.getBuffer());
    }

    /**
     * Handles the login process for this session.
     */
    public void handleLogin() throws Exception {
        switch (getStage()) {
            case CONNECTED:
                if (inData.remaining() < 2) {
                    inData.compact();
                    return;
                }

                /** Validate the request. */
                int request = inData.get() & 0xff;
                inData.get();

                if (request != 14) {
                    logger.info("Invalid login request: " + request);
                    disconnect();
                    return;
                }

                /** Write the response. */
                PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(17);
                out.writeLong(0); // First 8 bytes are ignored by the client.
                out.writeByte(0); // The response opcode, 0 for logging in.
                out.writeLong(new SecureRandom().nextLong()); // SSK.
                send(out.getBuffer());

                stage = Stage.LOGGING_IN;
                break;
            case LOGGING_IN:
                if (inData.remaining() < 2) {
                    inData.compact();
                    return;
                }

                /** Validate the login type. */
                int loginType = inData.get();

                if (loginType != 16 && loginType != 18) {
                    logger.info("Invalid login type: " + loginType);
                    disconnect();
                    return;
                }

                /** Ensure that we can read all of the login block. */
                int blockLength = inData.get() & 0xff;
                int loginEncryptPacketSize = blockLength - (36 + 1 + 1 + 2);

                if (loginEncryptPacketSize <= 0) {
                    logger.info("Zero RSA packet size");
                    disconnect();
                    return;
                }

                if (inData.remaining() < blockLength) {
                    inData.flip();
                    inData.compact();
                    return;
                }

                /** Read the login block. */
                PacketBuffer.ReadBuffer in = PacketBuffer.newReadBuffer(inData);

                int magicId = in.readByte();

                if (magicId == 1) {
                    botLogin = true;
                }

                /** Validate the client version. */
                int clientVersion = in.readShort();

                if (clientVersion != 317) {
                    logger.info("Invalid client version: " + clientVersion);
                    disconnect();
                    return;
                }

                in.readByte(); // Skip the high/low memory version.

                for (int i = 0; i < 9; i++) { // Skip the CRC keys.
                    in.readInt();
                }
                loginEncryptPacketSize--;
                in.readByte();

                String username = null;
                String password = null;

                /** Either decode RSA or ignore it depending on the settings. */
                if (DECODE_RSA && !botLogin) {
                    byte[] encryptionBytes = new byte[loginEncryptPacketSize];
                    in.getBuffer().get(encryptionBytes);

                    ByteBuffer rsaBuffer = ByteBuffer.wrap(new BigInteger(encryptionBytes).modPow(RSA_EXPONENT, RSA_MODULUS).toByteArray());

                    int rsaOpcode = rsaBuffer.get();

                    if (rsaOpcode != 10) {
                        logger.info("Unable to decode RSA block properly!");
                        disconnect();
                        return;
                    }

                    /** Set up the ISAAC ciphers. */
                    long clientHalf = rsaBuffer.getLong();
                    long serverHalf = rsaBuffer.getLong();

                    int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };

                    decryptor = new ISAACCipher(isaacSeed);

                    for (int i = 0; i < isaacSeed.length; i++) {
                        isaacSeed[i] += 50;

                    }

                    encryptor = new ISAACCipher(isaacSeed);

                    /** Read the user authentication. */
                    // int uid = rsaBuffer.getInt();
                    rsaBuffer.getInt();

                    ReadBuffer readStr = PacketBuffer.newReadBuffer(rsaBuffer);
                    username = readStr.readString();
                    password = readStr.readString();
                } else {
                    in.getBuffer().get();

                    /** Set up the ISAAC ciphers. */
                    long clientHalf = in.getBuffer().getLong();
                    long serverHalf = in.getBuffer().getLong();

                    int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf, (int) (serverHalf >> 32), (int) serverHalf };

                    decryptor = new ISAACCipher(isaacSeed);

                    for (int i = 0; i < isaacSeed.length; i++) {
                        isaacSeed[i] += 50;

                    }

                    encryptor = new ISAACCipher(isaacSeed);

                    /** Read the user authentication. */
                    in.getBuffer().getInt(); // Skip the user ID.
                    username = in.readString();
                    password = in.readString();
                }

                /** lowercase the username for accurate compare results. */
                username = username.toLowerCase();

                /** Set the username and password. */
                player.setUsername(username);
                player.setPassword(password);
                player.setUsernameHash(Misc.nameToLong(username));

                int response = Misc.LOGIN_RESPONSE_OK;

                /** Check if the player is already logged in. */
                if (World.getPlayer(player.getUsernameHash()) != null) {
                    response = Misc.LOGIN_RESPONSE_ACCOUNT_ONLINE;
                }

                /** Load saved data. */
                if (response == 2) {
                    ReadPlayerFileEvent read = new ReadPlayerFileEvent(player);
                    read.run();
                    response = read.getReturnCode();
                }

                if (player.isBanned()) {
                    response = Misc.LOGIN_RESPONSE_ACCOUNT_DISABLED;
                }

                /** Load player rights and the client response code. */
                PacketBuffer.WriteBuffer resp = PacketBuffer.newWriteBuffer(3);
                resp.writeByte(badCredentials(username, password) ? Misc.LOGIN_RESPONSE_INVALID_CREDENTIALS : response);

                if (player.getStaffRights() == 3) {
                    resp.writeByte(2);
                } else {
                    resp.writeByte(player.getStaffRights());
                }

                resp.writeByte(0);
                send(resp.getBuffer());

                if (response != 2) {
                    disconnect();
                    return;
                }

                /** Register this player for processing. */
                World.getPlayers().add(player);

                /** Update their appearance. */
                packetBuilder.sendMapRegion();
                packetBuilder.sendDetails();
                player.getFlags().flag(Flag.APPEARANCE);

                /** Load sidebar interfaces. */
                packetBuilder.sendSidebarInterface(1, 3917);
                packetBuilder.sendSidebarInterface(2, 638);
                packetBuilder.sendSidebarInterface(3, 3213);
                packetBuilder.sendSidebarInterface(4, 1644);
                packetBuilder.sendSidebarInterface(5, 5608);
                packetBuilder.sendSidebarInterface(6, player.getSpellbook().getSidebarInterface());
                packetBuilder.sendSidebarInterface(8, 5065);
                packetBuilder.sendSidebarInterface(9, 5715);
                packetBuilder.sendSidebarInterface(10, 2449);
                packetBuilder.sendSidebarInterface(11, 904);
                packetBuilder.sendSidebarInterface(12, 147);
                packetBuilder.sendSidebarInterface(13, 962);
                packetBuilder.sendSidebarInterface(0, 2423);

                /** Teleport the player to the saved position. */
                if (SOCKET_FLOOD) {
                    if (player.getStaffRights() > 0) {
                        player.move(player.getPosition());
                    } else {
                        player.move(player.getPosition().move(Misc.random(200), Misc.random(200)));
                    }
                } else if (!SOCKET_FLOOD) {
                    player.move(player.getPosition());
                }

                /** Refresh skills. */
                SkillManager.refreshAll(player);

                /** Refresh equipment. */
                player.getEquipment().refresh();

                /** Refresh inventory. */
                player.getInventory().refresh();

                /** Send the bonuses. */
                player.writeBonus();

                /** Send skills to the client. */
                for (int i = 0; i < player.getSkills().length; i++) {
                    packetBuilder.sendSkill(i, player.getSkills()[i].getLevel(), player.getSkills()[i].getExperience());
                }

                /** Update private messages on login. */
                player.getPrivateMessage().sendPrivateMessageOnLogin();

                /** Update interface text. */
                player.loadText();

                /** Update context menus. */
                packetBuilder.sendPlayerMenu("Trade with", 4);
                packetBuilder.sendPlayerMenu("Follow", 5);

                /** Starter package and makeover mage. */
                if (player.isNewPlayer()) {
                    player.getInventory().addItemSet(Player.STARTER_PACKAGE);
                    player.getPacketBuilder().sendInterface(3559);
                    player.setNewPlayer(false);
                }

                /** Schedule various workers. */
                TaskFactory.getFactory().submit(new RestoreEnergyWorker(player));
                TaskFactory.getFactory().submit(new RestoreStatWorker(player));

                if (player.getPoisonHits() > 0) {
                    TaskFactory.getFactory().submit(new CombatPoisonTask(player));
                }

                /** Send the welcome message. */
                packetBuilder.sendMessage(Player.WELCOME_MESSAGE);

                /** Do minigame stuff. */
                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        minigame.fireOnLogin(player);
                    }
                }

                /** Send the weapon interface. */
                AssignWeaponInterface.reset(player);
                AssignWeaponInterface.assignInterface(player, player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON));

                /** Assign the new animation based on the weapon. */
                AssignWeaponAnimation.assignAnimation(player, player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON));

                /** Check if the player is skulled. */
                if (player.getSkullTimer() > 0) {
                    player.setSkullIcon(0);
                    player.getFlags().flag(Flag.APPEARANCE);
                    TaskFactory.getFactory().submit(new CombatSkullTask(player));
                }

                /** Check if the player is teleblocked. */
                if (player.getTeleblockTimer() > 0) {
                    TaskFactory.getFactory().submit(new CombatTeleblockTask(player));
                }

                /** Load the configs. */
                player.loadConfigs();

                logger.info(player + " has logged in.");
                stage = Stage.LOGGED_IN;
                break;
            case LOGGED_OUT:
                disconnect();
                break;
            case LOGGED_IN:
                disconnect();
                break;
        }
    }

    /**
     * Returns a flag to determine if a username and password are bad.
     * 
     * @param username
     *        The username to check.
     * @param password
     *        The password to check.
     * @return {@code true} if and only if the credentials are bad, otherwise
     *         {@code falase}.
     */
    private boolean badCredentials(String username, String password) {
        Matcher usernameMatcher = PATTERN.matcher(username);
        Matcher passwordMatcher = PATTERN.matcher(password);

        if (usernameMatcher.matches() && passwordMatcher.matches()) {
            return false;
        }

        return true;
    }

    /**
     * Gets the remote host of the client.
     * 
     * @return the host.
     */
    public String getHost() {
        return host;
    }

    /**
     * Gets the encryptor.
     * 
     * @return the encryptor.
     */
    public ISAACCipher getEncryptor() {
        return encryptor;
    }

    /**
     * Gets the decryptor.
     * 
     * @return the decryptor.
     */
    public ISAACCipher getDecryptor() {
        return decryptor;
    }

    /**
     * Gets the player.
     * 
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the socket channel.
     * 
     * @return the socket channel.
     */
    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /**
     * Gets the login stage of this session.
     * 
     * @return the stage.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Gets the opcode for the current packet.
     * 
     * @return the packet opcode.
     */
    public int getPacketOpcode() {
        return packetOpcode;
    }

    /**
     * Sets the opcode for the current packet.
     * 
     * @param packetOpcode
     *        the packet opcode to set.
     */
    public void setPacketOpcode(int packetOpcode) {
        this.packetOpcode = packetOpcode;
    }

    /**
     * Gets the length for the current packet.
     * 
     * @return the packet length.
     */
    public int getPacketLength() {
        return packetLength;
    }

    /**
     * Sets the length for the current packet.
     * 
     * @param packetLength
     *        the packet length to set.
     */
    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * Gets the {@link ByteBuffer} for reading data.
     * 
     * @return the buffer for reading data.
     */
    public ByteBuffer getInData() {
        return inData;
    }

    /**
     * Gets the {@link ByteBuffer} for writing data.
     * 
     * @return the buffer for writing data.
     */
    public ByteBuffer getOutData() {
        return outData;
    }

    /**
     * Gets the packet builder
     * 
     * @return the packet builder.
     */
    public PacketEncoder getServerPacketBuilder() {
        return packetBuilder;
    }

    /**
     * Gets the selection key.
     * 
     * @return the selection key.
     */
    public SelectionKey getKey() {
        return key;
    }
}
