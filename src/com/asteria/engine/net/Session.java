package com.asteria.engine.net;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.security.SecureRandom;
import java.util.logging.Logger;

import com.asteria.engine.net.packet.PacketEncoder;
import com.asteria.engine.task.TaskManager;
import com.asteria.util.Stopwatch;
import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect;
import com.asteria.world.entity.combat.effect.CombatSkullEffect;
import com.asteria.world.entity.combat.effect.CombatTeleblockEffect;
import com.asteria.world.entity.combat.prayer.CombatPrayer;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.PlayerFileTask.ReadPlayerFileTask;
import com.asteria.world.entity.player.content.AssignWeaponAnimation;
import com.asteria.world.entity.player.content.AssignWeaponInterface;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.entity.player.skill.Skills;

/**
 * The class behind a Player that handles all networking-related things.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class Session {

    /** If RSA should be decoded in the login block. */
    public static final boolean DECODE_RSA = true;

    /**
     * Players that don't have a username equal to
     * <code>SOCKET_FLOOD_USERNAME</code> are moved within 200 squares of the
     * home area on login.
     */
    public static final boolean SOCKET_FLOOD = false;

    /**
     * Players that don't have this username are moved within 200 squares of the
     * home area on login, if the <code>SOCKET_FLOOD</code> boolean is flagged.
     */
    public static final String SOCKET_FLOOD_USERNAME = "lare96";

    /** The private RSA modulus and exponent key pairs. */
    public static final BigInteger RSA_MODULUS = new BigInteger(
            "95938610921572746524650133814858151901913076652480429598183870656291246099349831798849348614985734300731049329237933048794504022897746723376579898629175025215880393800715209863314290417958725518169765091231358927530763716352174212961746574137578805287960782611757859202906381434888168466423570348398899194541"),
            RSA_EXPONENT = new BigInteger(
                    "5378312350669976818157141639620196989298085716789189287634886259536048921510158872529601703029702119732149400119324443005798370082950416736889917871791338756888938417005708590957237003926710452309501641625737520695929480769820807041774825159548922857357239208866414166598649761006651610675718558204518453657");

    /** A logger for printing debugging info. */
    private static Logger logger = Logger.getLogger(Session.class
            .getSimpleName());

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

    /** Builds outgoing packets and sends them to the socket channel. */
    private PacketEncoder packetBuilder;

    /** The host address for this session. */
    private String host;

    /** The packet timeout timer for this player. */
    private Stopwatch timeout = new Stopwatch();

    /**
     * The current connection stage of the session.
     * 
     * @author blakeman8192
     */
    public enum Stage {
        CONNECTED,
        LOGGING_IN,
        LOGGED_IN,
        LOGGED_OUT
    }

    /**
     * Create a new {@link Session}.
     * 
     * @param key
     *            the selection key assigned to this session.
     */
    public Session(SelectionKey key) {
        this.key = key;
        stage = Stage.CONNECTED;
        inData = ByteBuffer.allocateDirect(512);
        outData = ByteBuffer.allocateDirect(8192);

        if (key != null) {
            socketChannel = (SocketChannel) key.channel();
            host = socketChannel.socket().getInetAddress().getHostAddress()
                    .toLowerCase();
            player = new Player(this);
            packetBuilder = new PacketEncoder(player);
        }
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

            // Validate the request.
            int request = inData.get() & 0xff;
            inData.get();

            if (request != 14) {
                logger.warning("Invalid login request: " + request);
                disconnect();
                return;
            }

            // Write the response and send it.
            ProtocolBuffer out = new ProtocolBuffer(17);
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

            // Validate the login type.
            int loginType = inData.get();

            if (loginType != 16 && loginType != 18) {
                logger.warning("Invalid login type: " + loginType);
                disconnect();
                return;
            }

            // Ensure that we can read all of the login block.
            int blockLength = inData.get() & 0xff;
            int loginEncryptPacketSize = blockLength - (36 + 1 + 1 + 2);

            if (loginEncryptPacketSize <= 0) {
                logger.warning("Zero RSA packet size");
                disconnect();
                return;
            }

            if (inData.remaining() < blockLength) {
                inData.flip();
                inData.compact();
                return;
            }

            // Read the login block.
            ProtocolBuffer in = new ProtocolBuffer(inData);
            in.readByte(); // Ignore the magic ID.

            // Validate the client version.
            int clientVersion = in.readShort();

            if (clientVersion != 317) {
                logger.warning("Invalid client version: " + clientVersion);
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

            // Either decode RSA or ignore it depending on the settings.
            if (DECODE_RSA) {

                // Create the RSA buffer.
                byte[] encryptionBytes = new byte[loginEncryptPacketSize];
                in.getBuffer().get(encryptionBytes);

                ByteBuffer rsaBuffer = ByteBuffer.wrap(new BigInteger(
                        encryptionBytes).modPow(RSA_EXPONENT, RSA_MODULUS)
                        .toByteArray());

                // Check if RSA block can be decoded.
                int rsaOpcode = rsaBuffer.get();

                if (rsaOpcode != 10) {
                    logger.warning("Unable to decode RSA block properly!");
                    disconnect();
                    return;
                }

                // Set up the ISAAC ciphers.
                long clientHalf = rsaBuffer.getLong();
                long serverHalf = rsaBuffer.getLong();

                int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf,
                        (int) (serverHalf >> 32), (int) serverHalf };

                decryptor = new ISAACCipher(isaacSeed);

                for (int i = 0; i < isaacSeed.length; i++) {
                    isaacSeed[i] += 50;

                }

                encryptor = new ISAACCipher(isaacSeed);

                // Read the user authentication.
                rsaBuffer.getInt(); // Skip the user ID.
                ProtocolBuffer readStr = new ProtocolBuffer(rsaBuffer);
                username = readStr.readString();
                password = readStr.readString();
            } else {
                in.getBuffer().get();

                // Set up the ISAAC ciphers.
                long clientHalf = in.getBuffer().getLong();
                long serverHalf = in.getBuffer().getLong();

                int[] isaacSeed = { (int) (clientHalf >> 32), (int) clientHalf,
                        (int) (serverHalf >> 32), (int) serverHalf };

                decryptor = new ISAACCipher(isaacSeed);

                for (int i = 0; i < isaacSeed.length; i++) {
                    isaacSeed[i] += 50;

                }

                encryptor = new ISAACCipher(isaacSeed);

                // Read the user authentication.
                in.getBuffer().getInt(); // Skip the user ID.
                username = in.readString();
                password = in.readString();
            }

            // Edit the username and password for security purposes.
            username = username.toLowerCase().replaceAll("_", " ").trim();
            password = password.toLowerCase();

            // Make sure the account credentials are valid.
            boolean invalidCredentials = !username
                    .matches("^[a-zA-Z0-9_ ]{1,12}$") || password.isEmpty() || password
                    .length() > 20;

            // Create the initial response code.
            int response = invalidCredentials ? Utility.LOGIN_RESPONSE_INVALID_CREDENTIALS
                    : Utility.LOGIN_RESPONSE_OK;

            // Edit it for banned hosts.
            response = HostGateway.getBannedHosts().contains(host) ? Utility.LOGIN_RESPONSE_ACCOUNT_DISABLED
                    : response;

            // Do not load the character file if the response is invalid.
            if (response == Utility.LOGIN_RESPONSE_OK) {

                // Set the username and password.
                player.setUsername(username);
                player.setPassword(password);

                // Cache the username hash.
                player.setUsernameHash(Utility.nameToHash(username));

                // Check if the player is already logged in.
                if (World.getPlayerByHash(player.getUsernameHash()) != null) {
                    response = Utility.LOGIN_RESPONSE_ACCOUNT_ONLINE;
                }

                // Load the character.
                if (response == 2) {
                    ReadPlayerFileTask read = new ReadPlayerFileTask(player);
                    response = read.call();
                }
            }

            // Check if we even have enough space for the player.
            if (World.getPlayers().getSize() >= World.getPlayers()
                    .getCapacity()) {
                response = Utility.LOGIN_RESPONSE_WORLD_FULL;
            }

            // Write the rights and the client response code.
            ProtocolBuffer resp = new ProtocolBuffer(3);
            resp.writeByte(response);
            resp.writeByte(player.getRights().getProtocolValue());
            resp.writeByte(0);
            send(resp.getBuffer());

            // Disconnect the player if the response is not two.
            if (response != Utility.LOGIN_RESPONSE_OK) {
                disconnect();
                return;
            }

            // Add the player to the entity container.
            World.getPlayers().add(player);

            // Send the map region, slot, and update appearance.
            packetBuilder.sendMapRegion();
            packetBuilder.sendDetails();
            player.getFlags().flag(Flag.APPEARANCE);

            // Send all of the sidebar interfaces.
            packetBuilder.sendSidebarInterface(1, 3917);
            packetBuilder.sendSidebarInterface(2, 638);
            packetBuilder.sendSidebarInterface(3, 3213);
            packetBuilder.sendSidebarInterface(4, 1644);
            packetBuilder.sendSidebarInterface(5, 5608);
            packetBuilder.sendSidebarInterface(6, player.getSpellbook()
                    .getSidebarInterface());
            packetBuilder.sendSidebarInterface(8, 5065);
            packetBuilder.sendSidebarInterface(9, 5715);
            packetBuilder.sendSidebarInterface(10, 2449);
            packetBuilder.sendSidebarInterface(11, 904);
            packetBuilder.sendSidebarInterface(12, 147);
            packetBuilder.sendSidebarInterface(13, 962);
            packetBuilder.sendSidebarInterface(0, 2423);

            // Teleport the player to the saved position.
            if (SOCKET_FLOOD) {
                if (player.getUsername().equals(SOCKET_FLOOD_USERNAME)) {
                    player.move(player.getPosition());
                } else {
                    player.move(player.getPosition().move(200));
                }
            } else if (!SOCKET_FLOOD) {
                player.move(player.getPosition());
            }

            // Refresh skills, equipment, and the inventory.
            Skills.refreshAll(player);
            player.getEquipment().refresh();
            player.getInventory().refresh();
            player.writeBonus();

            // Update private messages on login.
            player.getPacketBuilder().sendPrivateMessagingList(2);
            player.getPrivateMessage().updateThisList();
            player.getPrivateMessage().updateOtherList(true);

            // Send the context menus.
            packetBuilder.sendContextMenu("Trade with", 4);
            packetBuilder.sendContextMenu("Follow", 5);

            // Send the starter package and makeover mage interface if this
            // player is new.
            if (player.isNewPlayer()) {
                player.getInventory().addItemSet(Player.STARTER_PACKAGE);
                packetBuilder.sendInterface(3559);
                player.setNewPlayer(false);
            }

            // Schedule various tasks.
            if (player.isPoisoned()) {
                TaskManager.submit(new CombatPoisonEffect(player));
            }
            if (player.getTeleblockTimer() > 0) {
                TaskManager.submit(new CombatTeleblockEffect(player));
            }
            if (player.getSkullTimer() > 0) {
                player.setSkullIcon(0);
                TaskManager.submit(new CombatSkullEffect(player));
            }

            // Send the welcome message.
            packetBuilder.sendMessage(Player.WELCOME_MESSAGE);

            // Check dynamic minigame actions.
            for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                if (minigame.inMinigame(player)) {
                    minigame.fireOnLogin(player);
                }
            }

            // Send the weapon interface and animation.
            AssignWeaponInterface.assignInterface(player, player.getEquipment()
                    .getContainer().getItem(Utility.EQUIPMENT_SLOT_WEAPON));
            AssignWeaponAnimation.assignAnimation(player, player.getEquipment()
                    .getContainer().getItem(Utility.EQUIPMENT_SLOT_WEAPON));

            // Last but not least, send client configurations.
            packetBuilder.sendConfig(173, player.getMovementQueue()
                    .isRunToggled() ? 1 : 0);
            packetBuilder.sendConfig(172, player.isAutoRetaliate() ? 0 : 1);
            packetBuilder.sendConfig(player.getFightType().getParentId(),
                    player.getFightType().getChildId());
            packetBuilder.sendConfig(427, player.isAcceptAid() ? 1 : 0);
            packetBuilder.sendConfig(108, 0);
            packetBuilder.sendConfig(301, 0);
            packetBuilder.sendString(player.getRunEnergy() + "%", 149);
            CombatPrayer.resetAllGlows(player);

            // The player is now online!
            logger.info(player + " has logged in.");
            stage = Stage.LOGGED_IN;
            timeout.reset();
            break;
        case LOGGED_OUT:
        case LOGGED_IN:
            disconnect();
            break;
        }
    }

    /**
     * Disconnects the player from this session.
     */
    public void disconnect() {
        try {
            if (player != null && stage == Stage.LOGGED_IN) {
                for (Minigame minigame : MinigameFactory.getMinigames()
                        .values()) {
                    if (minigame.inMinigame(player)) {
                        minigame.fireOnForcedLogout(player);
                    }
                }

                World.savePlayer(player);
                TaskManager.cancelTasks(player);
                player.getTradeSession().reset(false);
                player.getPrivateMessage().updateOtherList(false);
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

            if (player != null) {
                logger.info(player + " has logged out.");
            } else {
                logger.info(this + " has logged out.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Sends a buffer to the socket.
     * 
     * @param buffer
     *            the buffer to send.
     */
    public void send(ByteBuffer buffer) {
        if (!socketChannel.isOpen())
            return;

        buffer.flip();

        try {
            socketChannel.write(buffer);

            if (buffer.hasRemaining()) {
                outData.put(buffer);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            disconnect();
        }
    }

    /**
     * Sends a protocol buffer to the socket.
     * 
     * @param buffer
     *            the buffer to send.
     */
    public void send(ProtocolBuffer buffer) {
        send(buffer.getBuffer());
    }

    @Override
    public String toString() {
        return "SESSION[host= " + host + ", stage= " + stage.name() + "]";
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
     *            the packet opcode to set.
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
     *            the packet length to set.
     */
    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * Gets the buffer for reading data.
     * 
     * @return the buffer for reading data.
     */
    public ByteBuffer getInData() {
        return inData;
    }

    /**
     * Gets the buffer for writing data.
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

    /**
     * Gets the packet timeout timer.
     * 
     * @return the packet timeout timer.
     */
    public Stopwatch getTimeout() {
        return timeout;
    }
}
