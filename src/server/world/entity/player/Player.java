package server.world.entity.player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import server.core.net.Session;
import server.core.net.packet.PacketEncoder;
import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.npc.Npc;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.container.BankContainer;
import server.world.entity.player.container.EquipmentContainer;
import server.world.entity.player.container.InventoryContainer;
import server.world.entity.player.content.PrivateMessage;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.content.TeleportSpell;
import server.world.entity.player.content.TradeSession;
import server.world.entity.player.content.AssignWeaponAnimation.WeaponAnimationIndex;
import server.world.entity.player.content.AssignWeaponInterface.FightType;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.Skill;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.map.Location;
import server.world.map.Position;

/**
 * Represents a logged-in player that is able to receive and send packets and
 * interact with entities and world models.
 * 
 * @author blakeman8192
 * @author lare96
 */
@SuppressWarnings("all")
public class Player extends Entity {

    /** The items recieved when the player logs in for the first time. */
    public static final Item[] STARTER_PACKAGE = { new Item(995, 10000) };

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(Player.class.getSimpleName());

    /** The network for this player. */
    private final Session session;

    /** The current fight type selected. */
    private FightType fightType = FightType.UNARMED_PUNCH;

    /** If this player has magic selected or is autocasting. */
    private boolean autocastMagic, usingMagic;

    /** The weapon animation for this player. */
    private WeaponAnimationIndex equipmentAnimation = new WeaponAnimationIndex();

    /** An array of all the trainable skills. */
    private Skill[] trainable = new Skill[21];

    /** The current players combat level. */
    private double combatLevel;

    /** The wilderness level for this player. */
    private int wildernessLevel;

    /** The weapon interface the player currently has open. */
    private WeaponInterface weapon;

    /** The players's current teleport stage. */
    private int teleportStage;

    /** The position this entity is current teleporting on. */
    private Position teleport = new Position();

    /** Animation played when this player dies. */
    private static final Animation DEATH = new Animation(0x900);

    /** If this player is visible. */
    private boolean isVisible = true;

    /** The shop you currently have open. */
    private int openShopId;

    /** The amount of food you are cooking. */
    private int cookAmount;

    /** Amount of logs the tree you're cutting holds. */
    private int woodcuttingLogAmount;

    /** Skill event firing flags. */
    private boolean[] skillEvent = new boolean[15];

    /** If this player is banned. */
    private boolean isBanned;

    /** Options used for npc dialogues, */
    private int option;

    /** The players head icon. */
    private int headIcon = -1;

    /** The players skull icon. */
    private int skullIcon = -1;

    /** The player's run energy. */
    private int runEnergy = 100;

    /** The npc teleporting the player. */
    private Npc runecraftingNpc;

    /** Handles a trading session with another player. */
    private TradeSession tradeSession = new TradeSession(this);

    /** A collection of anti-massing timers. */
    private Stopwatch eatingTimer = new Stopwatch().reset(),
            buryTimer = new Stopwatch().reset(),
            altarTimer = new Stopwatch().reset(),
            npcTheftTimer = new Stopwatch().reset(),
            objectTheftTimer = new Stopwatch().reset();

    /** The last delay when stealing. */
    private long lastTheftDelay;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** If this player is new. */
    private boolean newPlayer = true;

    /** Options for banking. */
    private boolean insertItem, withdrawAsNote;

    /** The conversation id the character is in. */
    private int npcDialogue;

    /** The stage in the conversation the player is in. */
    private int conversationStage;

    /** If this is the first packet recieved. Used for global items. */
    private boolean firstPacket;

    /** A list of local players. */
    private final List<Player> players = new LinkedList<Player>();

    /** A list of local npcs. */
    private final List<Npc> npcs = new LinkedList<Npc>();

    /** The players rights. */
    private int staffRights = 0;

    /** The players current spellbook. */
    private Spellbook spellbook = Spellbook.NORMAL;

    /** Variables for public chatting. */
    private int chatColor, chatEffects;

    /** The chat message in bytes. */
    private byte[] chatText;

    /** The gender. */
    private int gender = Misc.GENDER_MALE;

    /** The appearance. */
    private int[] appearance = new int[7], colors = new int[5];

    /** The player's bonuses. */
    private int[] playerBonus = new int[12];

    /** The friends list. */
    private List<Long> friends = new ArrayList<Long>(200);

    /** The ignores list. */
    private List<Long> ignores = new ArrayList<Long>(100);

    /** Flag that determines if this player has entered an incorrect password. */
    private boolean incorrectPassword;

    /** An instance of this player. */
    private Player player = this;

    /** For player npcs (pnpc). */
    private int npcAppearanceId = -1;

    /** The players inventory. */
    private InventoryContainer inventory = new InventoryContainer(this);

    /** The players bank. */
    private BankContainer bank = new BankContainer(this);

    /** The players equipment. */
    private EquipmentContainer equipment = new EquipmentContainer(this);

    /** Private messaging for this player. */
    private PrivateMessage privateMessage = new PrivateMessage(this);

    /** Field that determines if you are using a stove for cooking. */
    private boolean usingStove;

    /**
     * Creates a new {@link Player}.
     * 
     * @param session
     *        the session to create the player from.
     */
    public Player(Session session) {
        this.session = session;

        /** Set the default appearance. */
        getAppearance()[Misc.APPEARANCE_SLOT_CHEST] = 18;
        getAppearance()[Misc.APPEARANCE_SLOT_ARMS] = 26;
        getAppearance()[Misc.APPEARANCE_SLOT_LEGS] = 36;
        getAppearance()[Misc.APPEARANCE_SLOT_HEAD] = 0;
        getAppearance()[Misc.APPEARANCE_SLOT_HANDS] = 33;
        getAppearance()[Misc.APPEARANCE_SLOT_FEET] = 42;
        getAppearance()[Misc.APPEARANCE_SLOT_BEARD] = 10;

        /** Set the default colors. */
        getColors()[0] = 7;
        getColors()[1] = 8;
        getColors()[2] = 9;
        getColors()[3] = 5;
        getColors()[4] = 0;
    }

    @Override
    public void pulse() throws Exception {
        getMovementQueue().execute();
    }

    @Override
    public Worker death() throws Exception {
        return new Worker(1, true) {
            @Override
            public void fire() {
                if (getDeathTicks() == 0) {
                    // and stop combat
                    getMovementQueue().reset();
                } else if (getDeathTicks() == 1) {
                    animation(DEATH);
                    SkillEvent.fireSkillEvents(player);
                    player.getTradeSession().resetTrade(false);

                    // duel, whatever

                } else if (getDeathTicks() == 5) {
                    // send message to whoever killed this player and do
                    // whatever
                    // decide what items/equipment to keep, drop all other
                    // items/equipment, take into account the protect item
                    // prayer

                    Entity killer = null;
                    Minigame minigame = MinigameFactory.getMinigame(player);

                    if (minigame != null) {
                        minigame.fireOnDeath(player);

                        if (killer instanceof Player) {
                            minigame.fireOnKill((Player) killer, player);
                        }

                        move(minigame.getDeathPosition(player));
                    } else {
                        move(new Position(3093, 3244));
                    }
                } else if (getDeathTicks() == 6) {
                    // if the player is skulled
                    // unskull the player,
                    getPacketBuilder().resetAnimation();
                    getPacketBuilder().sendMessage("Oh dear, you're dead!");
                    getPacketBuilder().walkableInterface(65535);
                    // Prayer.getSingleton().stopAllCombatPrayer(player);
                    heal(player.getSkills()[Misc.HITPOINTS].getLevelForExperience());
                    setHasDied(false);
                    setDeathTicks(0);
                    this.cancel();
                    return;
                }

                incrementDeathTicks();
            }
        };
    }

    /**
     * An advanced teleport method that teleports the player to a position based
     * on the teleport spell.
     * 
     * @param position
     *        the position to teleport to.
     */
    public void teleport(final TeleportSpell spell) {
        if (teleportStage > 0) {
            return;
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canTeleport(player)) {
                    return;
                }
            }
        }

        if (spell.itemsRequired() != null) {
            for (Item item : spell.itemsRequired()) {
                if (item == null) {
                    continue;
                }

                if (!getInventory().getContainer().contains(item)) {
                    getPacketBuilder().sendMessage("You need " + item.getAmount() + " " + item.getDefinition().getItemName() + " to teleport here!");
                    return;
                }

                player.getInventory().deleteItem(item);
            }
        }

        if (!getSkills()[Misc.MAGIC].reqLevel(spell.levelRequired())) {
            getPacketBuilder().sendMessage("You need a magic level of " + spell.levelRequired() + " to teleport here!");
            return;
        }

        teleportStage = 1;
        getPacketBuilder().closeWindows();
        SkillManager.addExperience(this, spell.baseExperience(), Misc.MAGIC);

        switch (spell.type()) {
            case NORMAL_SPELLBOOK_TELEPORT:
                animation(new Animation(714));

                TaskFactory.getFactory().submit(new Worker(1, false) {
                    @Override
                    public void fire() {
                        if (teleportStage == 1) {
                            gfx(new Gfx(308));
                            teleportStage = 2;
                        } else if (teleportStage == 2) {
                            teleportStage = 3;
                        } else if (teleportStage == 3) {
                            move(spell.teleportTo());
                            animation(new Animation(715));
                            teleportStage = 0;
                            this.cancel();
                        }
                    }
                }.attach(this));
                break;
            case ANCIENTS_SPELLBOOK_TELEPORT:
                animation(new Animation(1979));

                TaskFactory.getFactory().submit(new Worker(1, false) {
                    @Override
                    public void fire() {
                        if (teleportStage == 1) {
                            gfx(new Gfx(392));
                            teleportStage = 2;
                        } else if (teleportStage == 2) {
                            teleportStage = 3;
                        } else if (teleportStage == 3) {
                            teleportStage = 4;
                        } else if (teleportStage == 4) {
                            move(spell.teleportTo());
                            teleportStage = 0;
                            this.cancel();
                        }
                    }
                }.attach(this));
                break;
        }
    }

    /**
     * The default teleport method that teleports the player to a position based
     * on the spellbook they have open.
     * 
     * @param position
     *        the position to teleport to.
     */
    public void teleport(final Position position) {
        teleport(new TeleportSpell() {
            @Override
            public Position teleportTo() {
                return position;
            }

            @Override
            public Teleport type() {
                return player.getSpellbook().getTeleport();
            }

            @Override
            public int baseExperience() {
                return 0;
            }

            @Override
            public Item[] itemsRequired() {
                return null;
            }

            @Override
            public int levelRequired() {
                return 1;
            }
        });
    }

    @Override
    public void move(Position position) {
        getMovementQueue().reset();
        getPacketBuilder().closeWindows();
        getPosition().setAs(position);
        setResetMovementQueue(true);
        setNeedsPlacement(true);
        getPacketBuilder().sendMapRegion();

        if (position.getZ() != 0) {
            World.getGroundItems().searchDatabaseHeightChange(this);
            World.getObjects().removeOnHeight(this);
        }
    }

    @Override
    public void follow(final Entity entity) {

    }

    @Override
    public int getAttackSpeed() {
        int speed = weapon.getSpeed();

        if (fightType == FightType.CROSSBOW_RAPID || fightType == FightType.SHORTBOW_RAPID || fightType == FightType.LONGBOW_RAPID || fightType == FightType.DART_RAPID || fightType == FightType.KNIFE_RAPID || fightType == FightType.THROWNAXE_RAPID || fightType == FightType.JAVELIN_RAPID) {
            speed--;
        }

        return speed;
    }

    @Override
    public String toString() {
        return getUsername() == null ? "SESSION(" + session.getHost() + ")" : "PLAYER(" + getUsername() + ":" + session.getHost() + ")";
    }

    /**
     * Logs the player out.
     */
    public void logout() throws Exception {
        World.getPlayers().remove(this);

        if (!session.isPacketDisconnect()) {
            getPacketBuilder().sendLogout();
        }

        if (username != null) {
            World.savePlayer(this);
        }

        logger.info(this + " has logged out.");
    }

    /**
     * Move the player based on their current position.
     * 
     * @param addX
     *        the x offset.
     * @param addY
     *        the y offset.
     */
    public void move(int addX, int addY) {
        move(new Position(player.getPosition().getX() + addX, player.getPosition().getY() + addY));
    }

    /**
     * Heals this player.
     * 
     * @param amount
     *        the amount of heal this player by.
     */
    public void heal(int amount) {
        if (getSkills()[Misc.HITPOINTS].getLevel() + amount >= getSkills()[Misc.HITPOINTS].getLevelForExperience()) {
            getSkills()[Misc.HITPOINTS].setLevel(getSkills()[Misc.HITPOINTS].getLevelForExperience());
        } else {
            getSkills()[Misc.HITPOINTS].increaseLevel(amount);
        }

        SkillManager.refresh(this, SkillConstant.HITPOINTS);
    }

    /**
     * Loads client configurations.
     */
    public void loadConfigs() {
        getPacketBuilder().sendConfig(173, getMovementQueue().isRunToggled() ? 1 : 0);
        getPacketBuilder().sendConfig(172, isAutoRetaliate() ? 1 : 0);
        getPacketBuilder().sendConfig(fightType.getParentId(), fightType.getChildId());
    }

    /**
     * Starts a dialogue.
     * 
     * @param id
     *        the dialogue to start.
     */
    public void dialogue(int id) {
        if (NpcDialogue.getDialogueMap().containsKey(id)) {
            npcDialogue = id;
            NpcDialogue.getDialogueMap().get(npcDialogue).dialogue(this);
        }
    }

    /**
     * Calculates this players combat level.
     * 
     * @return the players combat level.
     */
    public int getCombatLevel() {
        double mag = trainable[SkillConstant.MAGIC.ordinal()].getLevelForExperience() * 1.5;
        double ran = trainable[SkillConstant.RANGED.ordinal()].getLevelForExperience() * 1.5;
        double attstr = trainable[SkillConstant.ATTACK.ordinal()].getLevelForExperience() + trainable[SkillConstant.STRENGTH.ordinal()].getLevelForExperience();

        combatLevel = 0;

        if (ran > attstr) {
            combatLevel = ((trainable[SkillConstant.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.PRAYER.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[SkillConstant.RANGED.ordinal()].getLevelForExperience()) * 0.4875);
        } else if (mag > attstr) {
            combatLevel = (((trainable[SkillConstant.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.RANGED.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[SkillConstant.MAGIC.ordinal()].getLevelForExperience()) * 0.4875));
        } else {
            combatLevel = (((trainable[SkillConstant.DEFENCE.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.HITPOINTS.ordinal()].getLevelForExperience()) * 0.25) + ((trainable[SkillConstant.PRAYER.ordinal()].getLevelForExperience()) * 0.125) + ((trainable[SkillConstant.ATTACK.ordinal()].getLevelForExperience()) * 0.325) + ((trainable[SkillConstant.STRENGTH.ordinal()].getLevelForExperience()) * 0.325));
        }

        return (int) combatLevel;
    }

    /**
     * Displays the wilderness and multicombat interfaces for players when
     * needed.
     */
    public void displayInterfaces() {

        /** Update the wilderness info. */
        if (Location.inWilderness(this)) {
            int calculateY = this.getPosition().getY() > 6400 ? this.getPosition().getY() - 6400 : this.getPosition().getY();
            wildernessLevel = (((calculateY - 3520) / 8) + 1);

            if (!this.isWildernessInterface()) {
                this.getPacketBuilder().walkableInterface(197);
                this.getPacketBuilder().sendPlayerMenu("Attack", 3);
                this.setWildernessInterface(true);
            }

            this.getPacketBuilder().sendString("@yel@Level: " + wildernessLevel, 199);
        } else {
            this.getPacketBuilder().sendPlayerMenu("null", 3);
            this.getPacketBuilder().walkableInterface(-1);
            this.setWildernessInterface(false);
            wildernessLevel = 0;
        }

        /** Update the multicombat info. */
        if (Location.inMultiCombat(this)) {
            if (!this.isMultiCombatInterface()) {
                this.getPacketBuilder().sendMultiCombatInterface(1);
                this.setMultiCombatInterface(true);
            }
        } else {
            this.getPacketBuilder().sendMultiCombatInterface(0);
            this.setWildernessInterface(false);
        }
    }

    /**
     * Calculates and writes the players bonuses.
     */
    public void writeBonus() {
        for (int i = 0; i < playerBonus.length; i++) {
            playerBonus[i] = 0;
        }

        for (Item item : this.getEquipment().getContainer().toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            for (int i = 0; i < playerBonus.length; i++) {
                playerBonus[i] += item.getDefinition().getBonus()[i];
            }
        }

        int offset = 0;
        String send = "";

        for (int i = 0; i < playerBonus.length; i++) {
            if (playerBonus[i] >= 0) {
                send = Misc.BONUS_NAMES[i] + ": +" + playerBonus[i];
            } else {
                send = Misc.BONUS_NAMES[i] + ": -" + Math.abs(playerBonus[i]);
            }

            if (i == 10) {
                offset = 1;
            }

            getPacketBuilder().sendString(send, (1675 + i + offset));
        }
    }

    /**
     * Loads interface text on login.
     */
    public void loadText() {
        getPacketBuilder().sendString("Teleport Home", 1300);
        getPacketBuilder().sendString("Teleports you to Draynor Village.", 1301);
        getPacketBuilder().sendString("Teleport Home", 13037);
        getPacketBuilder().sendString("Teleports you to Draynor Village.", 13038);
        getPacketBuilder().sendString("Minigame Teleport", 1325);
        getPacketBuilder().sendString("A selection of teleports.", 1326);
        getPacketBuilder().sendString("Minigame Teleport", 13047);
        getPacketBuilder().sendString("A selection of teleports.", 13048);
    }

    /**
     * Sets the username.
     * 
     * @param username
     *        the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *        the password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Npc> getNpcs() {
        return npcs;
    }

    public void setNpcAppearanceId(int npcAppearanceId) {
        this.npcAppearanceId = npcAppearanceId;
    }

    public int getNpcAppearanceId() {
        return npcAppearanceId;
    }

    public InventoryContainer getInventory() {
        return inventory;
    }

    public BankContainer getBank() {
        return bank;
    }

    public EquipmentContainer getEquipment() {
        return equipment;
    }

    /**
     * @return the newPlayer
     */
    public boolean isNewPlayer() {
        return newPlayer;
    }

    /**
     * @param newPlayer
     *        the newPlayer to set
     */
    public void setNewPlayer(boolean newPlayer) {
        this.newPlayer = newPlayer;
    }

    /**
     * @return the withdrawAsNote
     */
    public boolean isWithdrawAsNote() {
        return withdrawAsNote;
    }

    /**
     * @param withdrawAsNote
     *        the withdrawAsNote to set
     */
    public void setWithdrawAsNote(boolean withdrawAsNote) {
        this.withdrawAsNote = withdrawAsNote;
    }

    /**
     * @return the insertItem
     */
    public boolean isInsertItem() {
        return insertItem;
    }

    /**
     * @param insertItem
     *        the insertItem to set
     */
    public void setInsertItem(boolean insertItem) {
        this.insertItem = insertItem;
    }

    /**
     * @return the incorrectPassword
     */
    public boolean isIncorrectPassword() {
        return incorrectPassword;
    }

    /**
     * @param incorrectPassword
     *        the incorrectPassword to set
     */
    public void setIncorrectPassword(boolean incorrectPassword) {
        this.incorrectPassword = incorrectPassword;
    }

    /**
     * @return the firstPacket
     */
    public boolean isFirstPacket() {
        return firstPacket;
    }

    /**
     * @param firstPacket
     *        the firstPacket to set
     */
    public void setFirstPacket(boolean firstPacket) {
        this.firstPacket = firstPacket;
    }

    /**
     * @return the privateMessage
     */
    public PrivateMessage getPrivateMessage() {
        return privateMessage;
    }

    /**
     * @return the friends
     */
    public List<Long> getFriends() {
        return friends;
    }

    /**
     * @param friends
     *        the friends to set
     */
    public void setFriends(List<Long> friends) {
        this.friends = friends;
    }

    /**
     * @return the ignores
     */
    public List<Long> getIgnores() {
        return ignores;
    }

    /**
     * @param ignores
     *        the ignores to set
     */
    public void setIgnores(List<Long> ignores) {
        this.ignores = ignores;
    }

    /**
     * @return the skillingAction
     */
    public boolean[] getSkillEvent() {
        return skillEvent;
    }

    /**
     * @return the usingStove
     */
    public boolean isUsingStove() {
        return usingStove;
    }

    /**
     * @param usingStove
     *        the usingStove to set
     */
    public void setUsingStove(boolean usingStove) {
        this.usingStove = usingStove;
    }

    /**
     * @return the trading
     */
    public TradeSession getTradeSession() {
        return tradeSession;
    }

    /**
     * @return the runEnergy
     */
    public int getRunEnergy() {
        return runEnergy;
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void decrementRunEnergy() {
        this.runEnergy -= 1;
        getPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void incrementRunEnergy() {
        this.runEnergy += 1;
        getPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void setRunEnergy(int runEnergy) {
        this.runEnergy = runEnergy;
    }

    /**
     * @return the eatingTimer
     */
    public Stopwatch getEatingTimer() {
        return eatingTimer;
    }

    /**
     * @param eatingTimer
     *        the eatingTimer to set
     */
    public void setEatingTimer(Stopwatch eatingTimer) {
        this.eatingTimer = eatingTimer;
    }

    public PacketEncoder getPacketBuilder() {
        return session.getServerPacketBuilder();
    }

    /**
     * @return the npcDialogue
     */
    public int getNpcDialogue() {
        return npcDialogue;
    }

    /**
     * @param npcDialogue
     *        the npcDialogue to set
     */
    public void setNpcDialogue(int npcDialogue) {
        this.npcDialogue = npcDialogue;
    }

    /**
     * @return the buryTimer
     */
    public Stopwatch getBuryTimer() {
        return buryTimer;
    }

    /**
     * @return the option
     */
    public int getOption() {
        return option;
    }

    /**
     * @param option
     *        the option to set
     */
    public void setOption(int option) {
        this.option = option;
    }

    /**
     * @return the runecraftingNpc
     */
    public Npc getRunecraftingNpc() {
        return runecraftingNpc;
    }

    /**
     * @param runecraftingNpc
     *        the runecraftingNpc to set
     */
    public void setRunecraftingNpc(Npc runecraftingNpc) {
        this.runecraftingNpc = runecraftingNpc;
    }

    /**
     * @return the cookAmount
     */
    public int getCookAmount() {
        return cookAmount;
    }

    /**
     * @param cookAmount
     *        the cookAmount to set
     */
    public void setCookAmount(int cookAmount) {
        this.cookAmount = cookAmount;
    }

    public void addCookAmount() {
        cookAmount++;
    }

    /**
     * @return the headIcon
     */
    public int getHeadIcon() {
        return headIcon;
    }

    /**
     * @param headIcon
     *        the headIcon to set
     */
    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    /**
     * @return the openShopId
     */
    public int getOpenShopId() {
        return openShopId;
    }

    /**
     * @param openShopId
     *        the openShopId to set
     */
    public void setOpenShopId(int openShopId) {
        this.openShopId = openShopId;
    }

    /**
     * @return the skullIcon
     */
    public int getSkullIcon() {
        return skullIcon;
    }

    /**
     * @param skullIcon
     *        the skullIcon to set
     */
    public void setSkullIcon(int skullIcon) {
        this.skullIcon = skullIcon;
    }

    /**
     * @return the altarTimer
     */
    public Stopwatch getAltarTimer() {
        return altarTimer;
    }

    /**
     * @return the spellbook
     */
    public Spellbook getSpellbook() {
        return spellbook;
    }

    /**
     * @param spellbook
     *        the spellbook to set
     */
    public void setSpellbook(Spellbook spellbook) {
        this.spellbook = spellbook;
    }

    /**
     * @return the woodcuttingLogAmount
     */
    public int getWoodcuttingLogAmount() {
        return woodcuttingLogAmount;
    }

    /**
     * @param woodcuttingLogAmount
     *        the woodcuttingLogAmount to set
     */
    public void setWoodcuttingLogAmount(int woodcuttingLogAmount) {
        this.woodcuttingLogAmount = woodcuttingLogAmount;
    }

    public void decrementWoodcuttingLogAmount() {
        this.woodcuttingLogAmount--;
    }

    /**
     * @return the playerBonus
     */
    public int[] getPlayerBonus() {
        return playerBonus;
    }

    /**
     * @param playerBonus
     *        the playerBonus to set
     */
    public void setPlayerBonus(int[] playerBonus) {
        this.playerBonus = playerBonus;
    }

    /**
     * @return the conversationStage
     */
    public int getConversationStage() {
        return conversationStage;
    }

    /**
     * @param conversationStage
     *        the conversationStage to set
     */
    public void setConversationStage(int conversationStage) {
        this.conversationStage = conversationStage;
    }

    public Session getSession() {
        return session;
    }

    public void setStaffRights(int staffRights) {
        this.staffRights = staffRights;
    }

    public int getStaffRights() {
        return staffRights;
    }

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }

    public int getChatColor() {
        return chatColor;
    }

    public void setChatEffects(int chatEffects) {
        this.chatEffects = chatEffects;
    }

    public int getChatEffects() {
        return chatEffects;
    }

    public void setChatText(byte[] chatText) {
        this.chatText = chatText;
    }

    public byte[] getChatText() {
        return chatText;
    }

    public int[] getAppearance() {
        return appearance;
    }

    public void setAppearance(int[] appearance) {
        this.appearance = appearance;
    }

    public int[] getColors() {
        return colors;
    }

    public void setColors(int[] colors) {
        this.colors = colors;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getGender() {
        return gender;
    }

    /**
     * @return the isVisible
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * @param isVisible
     *        the isVisible to set
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * @return the lastTheftDelay
     */
    public long getLastTheftDelay() {
        return lastTheftDelay;
    }

    /**
     * @param lastTheftDelay
     *        the lastTheftDelay to set
     */
    public void setLastTheftDelay(long lastTheftDelay) {
        this.lastTheftDelay = lastTheftDelay;
    }

    /**
     * @return the theftTimer
     */
    public Stopwatch getNpcTheftTimer() {
        return npcTheftTimer;
    }

    /**
     * @return the isBanned
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     * @param isBanned
     *        the isBanned to set
     */
    public void setBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }

    /**
     * @return the objectTheftTimer
     */
    public Stopwatch getObjectTheftTimer() {
        return objectTheftTimer;
    }

    /**
     * @return the weapon.
     */
    public WeaponInterface getWeapon() {
        return weapon;
    }

    /**
     * @param weapon
     *        the weapon to set.
     */
    public void setWeapon(WeaponInterface weapon) {
        this.weapon = weapon;
    }

    /**
     * @return the wildernessLevel
     */
    public int getWildernessLevel() {
        return wildernessLevel;
    }

    /**
     * @return the trainable skills.
     */
    public Skill[] getSkills() {
        return trainable;
    }

    /**
     * @param trainable
     *        the trainable to set.
     */
    public void setTrainable(Skill[] trainable) {
        this.trainable = trainable;
    }

    /**
     * @return the equipmentAnimation
     */
    public WeaponAnimationIndex getUpdateAnimation() {
        return equipmentAnimation;
    }

    /**
     * @return the autocastMagic
     */
    public boolean isAutocastMagic() {
        return autocastMagic;
    }

    /**
     * @param autocastMagic
     *        the autocastMagic to set
     */
    public void setAutocastMagic(boolean autocastMagic) {
        this.autocastMagic = autocastMagic;
    }

    /**
     * @return the usingMagic
     */
    public boolean isUsingMagic() {
        return usingMagic;
    }

    /**
     * @param usingMagic
     *        the usingMagic to set
     */
    public void setUsingMagic(boolean usingMagic) {
        this.usingMagic = usingMagic;
    }

    /**
     * @return the fightType
     */
    public FightType getFightType() {
        return fightType;
    }

    /**
     * @param fightType
     *        the fightType to set
     */
    public void setFightType(FightType fightType) {
        this.fightType = fightType;
    }
}
