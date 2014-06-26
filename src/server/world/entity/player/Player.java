package server.world.entity.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import server.Main;
import server.core.net.Session;
import server.core.net.packet.PacketEncoder;
import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.EntityType;
import server.world.entity.Gfx;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.magic.CombatSpell;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.prayer.CombatPrayerWorker;
import server.world.entity.combat.range.CombatRangedAmmo;
import server.world.entity.combat.special.CombatSpecial;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoison;
import server.world.entity.npc.Npc;
import server.world.entity.npc.dialogue.Dialogue;
import server.world.entity.player.content.AssignWeaponAnimation.WeaponAnimationIndex;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.content.AssignWeaponInterface.FightType;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.entity.player.content.PrivateMessage;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.content.TeleportSpell;
import server.world.entity.player.content.TradeSession;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.Skill;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager;
import server.world.item.Item;
import server.world.item.Item.NormalPriceItemComparator;
import server.world.item.container.BankContainer;
import server.world.item.container.EquipmentContainer;
import server.world.item.container.InventoryContainer;
import server.world.item.ground.GroundItem;
import server.world.item.ground.StaticGroundItem;
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

    /** The welcome message. */
    public static final String WELCOME_MESSAGE = "Welcome to "
            + Main.NAME
            + "!";

    /** A message sent to a player that has killed another player. */
    public static final String[] DEATH_MESSAGES = { "You have killed -player-!" };

    /** The items recieved when the player logs in for the first time. */
    public static final Item[] STARTER_PACKAGE = { new Item(995, 10000) };

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(Player.class.getSimpleName());

    /** The network for this player. */
    private final Session session;

    /** The spell currently selected. */
    private CombatSpell castSpell;

    /** The range ammo being used. */
    private CombatRangedAmmo rangedAmmo;

    /** If the player is autocasting. */
    private boolean autocast;

    /** What the player is autocasting. */
    private CombatSpell autocastSpell;

    /** The current special attack the player has set. */
    private CombatSpecial combatSpecial;

    /** If the player data needs to be read or not. */
    private boolean needsRead = true;

    /** The special bar percentage. */
    private int specialPercentage = 100;

    /** The ammo that was just fired with. */
    private int fireAmmo;

    /** If the special is activated. */
    private boolean specialActivated;

    /** The current fight type selected. */
    private FightType fightType = FightType.UNARMED_PUNCH;

    /** The weapon animation for this player. */
    private WeaponAnimationIndex equipmentAnimation = new WeaponAnimationIndex();

    /** An array of all the trainable skills. */
    private Skill[] skills = new Skill[21];

    /** The prayer worker. */
    private Worker prayerDrain = new CombatPrayerWorker(this).terminateRun();

    /** The prayer statuses. */
    private boolean[] prayerActive = new boolean[18];

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

    /** Skill event firing flags. */
    private boolean[] skillEvent = new boolean[15];

    /** If this player is banned. */
    private boolean isBanned;

    /** If the player has accept aid on. */
    private boolean acceptAid = true;

    /** Options used for npc dialogues, */
    private int option;

    /** The players head icon. */
    private int headIcon = -1;

    /** The players skull stuff. */
    private int skullIcon = -1,
            skullTimer;

    /** Teleblock stuff. */
    private int teleblockTimer;

    /** The player's run energy. */
    private int runEnergy = 100;

    /** Handles a trading session with another player. */
    private TradeSession tradeSession = new TradeSession(this);

    /** A collection of anti-massing timers. */
    private final Stopwatch eatingTimer = new Stopwatch().reset();
    private final Stopwatch potionTimer = new Stopwatch().reset();

    /** The amount of ticks this player is immune to dragon fire */
    private int dragonFireImmunity;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The points for the example currency. */
    private int examplePoints = 10;

    /** If this player is new. */
    private boolean newPlayer = true;

    /** Options for banking. */
    private boolean insertItem,
            withdrawAsNote;

    /** The conversation id the character is in. */
    private int npcDialogue;

    /** The stage in the conversation the player is in. */
    private int conversationStage;

    /** A list of local players. */
    private final Set<Player> players = new LinkedHashSet<Player>();

    /** A list of local npcs. */
    private final Set<Npc> npcs = new LinkedHashSet<Npc>();

    /** The players rights. */
    private PlayerRights rights = PlayerRights.PLAYER;

    /** The players current spellbook. */
    private Spellbook spellbook = Spellbook.NORMAL;

    /** Variables for public chatting. */
    private int chatColor, chatEffects;

    /** The chat message in bytes. */
    private byte[] chatText;

    /** The gender. */
    private int gender = Misc.GENDER_MALE;

    /** The appearance. */
    private int[] appearance = new int[7],
            colors = new int[5];

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

    /** The cached update block. */
    private ByteBuffer cachedUpdateBlock;

    /** The player's username hash. */
    private long usernameHash;

    /** The current dialogue we are in. */
    private Dialogue dialogue;

    /** The current dialogue stage we are in. */
    private int dialogueStage;

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
        // XXX: Equal to the "process()" method, the only thing that should be
        // in here is movement... nothing else! Use workers for delayed actions!

        getMovementQueue().execute();
    }

    @Override
    public Worker death() throws Exception {
        return new Worker(1, true) {

            @Override
            public void fire() {

                if (getDeathTicks() == 0) {
                    getMovementQueue().reset();
                    setPoisonHits(0);
                    setPoisonStrength(CombatPoison.MILD);
                } else if (getDeathTicks() == 1) {
                    animation(DEATH);
                    SkillEvent.fireSkillEvents(player);
                    player.getTradeSession().resetTrade(false);
                } else if (getDeathTicks() == 5) {

                    Player killer = getCombatBuilder().getKiller();
                    Minigame minigame = MinigameFactory.getMinigame(player);

                    if (minigame != null) {
                        minigame.fireOnDeath(player);

                        if (!minigame.canKeepItems()) {
                            if (player.getRights().lessThan(PlayerRights.ADMINISTRATOR)) {
                                dropDeathItems(killer);
                            }
                        }

                        if (killer != null
                                && killer.type() == EntityType.PLAYER) {
                            minigame.fireOnKill(killer, player);
                        }

                        move(minigame.getDeathPosition(player));
                    } else {
                        if (player.getRights().lessThan(PlayerRights.ADMINISTRATOR)) {
                            deathHook(killer);
                            move(new Position(3093, 3244));
                        } else {
                            if (killer != null) {
                                killer.getPacketBuilder().sendMessage("Sorry, but you cannot kill administrators.");
                            }
                        }
                    }
                    player.getCombatBuilder().reset();
                } else if (getDeathTicks() == 6) {
                    skullTimer = 0;
                    skullIcon = -1;
                    teleblockTimer = 0;
                    animation(new Animation(65535));

                    if (rights.lessThan(PlayerRights.ADMINISTRATOR)) {
                        AssignWeaponInterface.reset(player);
                        AssignWeaponInterface.changeFightType(player);
                    }

                    getCombatBuilder().resetDamage();
                    getPacketBuilder().sendMessage("Oh dear, you're dead!");
                    getPacketBuilder().walkableInterface(65535);
                    player.getSkills()[Misc.PRAYER].setLevel(player.getSkills()[Misc.PRAYER].getLevelForExperience());
                    CombatPrayer.deactivateAllPrayer(player);
                    SkillManager.refresh(player, Misc.PRAYER);
                    heal(player.getSkills()[Misc.HITPOINTS].getLevelForExperience());
                    setHasDied(false);
                    setDeathTicks(0);
                    getFlags().flag(Flag.APPEARANCE);
                    this.cancel();
                    return;
                }

                incrementDeathTicks();
            }
        };
    }

    @Override
    public EntityType type() {
        return EntityType.PLAYER;
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

        if (wildernessLevel >= 20) {
            player.getPacketBuilder().sendMessage("You must be below level 20 wilderness to teleport!");
            return;
        }

        if (teleblockTimer > 0) {
            if ((teleblockTimer * 600) == 600) {
                getPacketBuilder().sendMessage("You'll be able to teleport in a second! Just wait!");
                return;
            } else if ((teleblockTimer * 600) >= 1000
                    && (teleblockTimer * 600) <= 60000) {
                getPacketBuilder().sendMessage("You must wait approximately "
                        + ((teleblockTimer * 600) / 1000)
                        + " seconds in order to teleport!");
                return;
            } else if ((teleblockTimer * 600) > 60000) {
                getPacketBuilder().sendMessage("You must wait approximately "
                        + ((teleblockTimer * 600) / 60000)
                        + " minutes in order to teleport!");
                return;
            }
        }

        if (!getSkills()[Misc.MAGIC].reqLevel(spell.levelRequired())) {
            getPacketBuilder().sendMessage("You need a magic level of "
                    + spell.levelRequired()
                    + " to teleport here!");
            return;
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canTeleport(player)) {
                    return;
                }
            }
        }

        if (spell.itemsRequired(this) != null) {
            for (Item item : spell.itemsRequired(this)) {
                if (item == null) {
                    continue;
                }

                if (!getInventory().getContainer().contains(item)) {
                    getPacketBuilder().sendMessage("You need "
                            + item.getAmount()
                            + " "
                            + item.getDefinition().getItemName()
                            + " to teleport here!");
                    return;
                }

                player.getInventory().deleteItem(item);
            }
        }

        teleportStage = 1;
        getCombatBuilder().reset();
        faceEntity(65535);
        getFollowWorker().cancel();
        setFollowing(false);
        setFollowingEntity(null);
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
            public Item[] itemsRequired(Player player) {
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
    }

    @Override
    public int getAttackSpeed() {
        int speed = weapon.getSpeed();

        if (fightType == FightType.CROSSBOW_RAPID
                || fightType == FightType.SHORTBOW_RAPID
                || fightType == FightType.LONGBOW_RAPID
                || fightType == FightType.DART_RAPID
                || fightType == FightType.KNIFE_RAPID
                || fightType == FightType.THROWNAXE_RAPID
                || fightType == FightType.JAVELIN_RAPID) {
            speed--;
        } else if (fightType == FightType.CROSSBOW_LONGRANGE
                || fightType == FightType.SHORTBOW_LONGRANGE
                || fightType == FightType.LONGBOW_LONGRANGE
                || fightType == FightType.DART_LONGRANGE
                || fightType == FightType.KNIFE_LONGRANGE
                || fightType == FightType.THROWNAXE_LONGRANGE
                || fightType == FightType.JAVELIN_LONGRANGE) {
            speed++;
        }

        return speed;
    }

    @Override
    public int getCurrentHealth() {
        return skills[Misc.HITPOINTS].getLevel();
    }

    @Override
    public String toString() {
        return getUsername() == null ? "SESSION[host= "
                + session.getHost()
                + ", stage= "
                + session.getStage().name()
                + "]" : "PLAYER[username= "
                + getUsername()
                + ", host= "
                + session.getHost()
                + ", rights= "
                + rights
                + "]";
    }

    /**
     * Logs the player out.
     */
    public void logout() {
        player.getPacketBuilder().sendLogout();
        session.disconnect();
    }

    /**
     * The hook fired when this player is killed.
     * 
     * @param killer
     *        the player who killed this player (if any).
     */
    public void deathHook(Player killer) {
        if (killer == null) {
            dropDeathItems(null);
            return;
        }
        killer.getPacketBuilder().sendMessage(Misc.randomElement(DEATH_MESSAGES).replaceAll("-player-", username));
        dropDeathItems(killer);
    }

    /**
     * Drops everything except the 3 (or 4) most valuable items on death.
     */
    public void dropDeathItems(Player killer) {

        /** All of the player's inventory and equipment. */
        Item[] equipment = player.getEquipment().getContainer().toArray();
        Item[] inventory = player.getInventory().getContainer().toArray();
        LinkedList<Item> dropItems = new LinkedList<Item>();
        dropItems.addAll(Arrays.asList(equipment));
        dropItems.addAll(Arrays.asList(inventory));

        /** Remove all of the player's inventory and equipment. */
        player.getEquipment().getContainer().clear();
        player.getInventory().getContainer().clear();
        player.getEquipment().refresh();
        player.getInventory().refresh();
        player.getFlags().flag(Flag.APPEARANCE);

        /** The player is skulled so drop everything. */
        if (player.getSkullTimer() > 0) {
            for (Item item : dropItems) {
                if (item == null) {
                    continue;
                }

                World.getGroundItems().register(killer == null ? new StaticGroundItem(item, getPosition(), true, false) : new GroundItem(item, getPosition(), killer));
            }
            return;
        }

        /** Create an array of items to keep. */
        Item[] keepItems = new Item[3];

        /** Expand that array if we have the protect item prayer activated. */
        if (CombatPrayer.isPrayerActivated(this, CombatPrayer.PROTECT_ITEM)) {
            keepItems = new Item[4];
        }

        /** Fill the array with the most valuable items. */
        Collections.sort(dropItems, new NormalPriceItemComparator());
        Collections.reverse(dropItems);
        int slot = 0;

        for (Iterator<Item> it = dropItems.iterator(); it.hasNext();) {
            Item next = it.next();

            if (next == null) {
                continue;
            } else if (slot == keepItems.length) {

                /** We've filled the array, stop searching. */
                break;
            }

            keepItems[slot++] = next;
            it.remove();

        }

        /** Keep the saved items. */
        player.getInventory().addItemSet(keepItems);

        /** Drop the other items. */
        for (Item item : dropItems) {
            if (item == null) {
                continue;
            }

            World.getGroundItems().register(killer == null ? new StaticGroundItem(item, getPosition(), true, false) : new GroundItem(item, getPosition(), killer));
        }
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
        move(new Position(player.getPosition().getX()
                + addX, player.getPosition().getY()
                + addY));
    }

    /**
     * Heals this player.
     * 
     * @param amount
     *        the amount of heal this player by.
     */
    public void heal(int amount) {
        int level = getSkills()[Misc.HITPOINTS].getLevelForExperience();

        if ((getSkills()[Misc.HITPOINTS].getLevel() + amount) >= level) {
            getSkills()[Misc.HITPOINTS].setLevel(level);
        } else {
            getSkills()[Misc.HITPOINTS].increaseLevel(amount);
        }

        SkillManager.refresh(this, Misc.HITPOINTS);
    }

    /**
     * Loads client configurations.
     */
    public void loadConfigs() {
        getPacketBuilder().sendConfig(173, getMovementQueue().isRunToggled() ? 1 : 0);
        getPacketBuilder().sendConfig(172, isAutoRetaliate() ? 0 : 1);
        getPacketBuilder().sendConfig(fightType.getParentId(), fightType.getChildId());
        getPacketBuilder().sendConfig(427, isAcceptAid() ? 1 : 0);
        getPacketBuilder().sendConfig(108, 0);
        getPacketBuilder().sendConfig(301, 0);
        CombatPrayer.resetPrayerGlows(this);
    }

    /**
     * Starts a dialogue.
     * 
     * @param d
     *        the dialogue to start.
     */
    public void sendDialogue(Dialogue d) {
        if (d.getDialogues().length == 0) {
            throw new IllegalArgumentException("Cannot send empty dialogue!");
        }

        this.dialogue = d;
        this.dialogue.getDialogues()[dialogueStage++].fire(dialogue);
    }

    /**
     * Stops the current dialogue.
     */
    public void stopDialogue() {
        dialogue = null;
        dialogueStage = 0;
    }

    /**
     * Advances the current dialogue by one stage.
     */
    public void advanceDialogue() {
        if (dialogue == null) {
            player.getPacketBuilder().closeWindows();
            stopDialogue();
            return;
        }

        if ((dialogueStage + 1) > dialogue.getDialogues().length) {
            player.getPacketBuilder().closeWindows();
            stopDialogue();
        } else {
            dialogue.getDialogues()[dialogueStage++].fire(dialogue);
        }
    }

    /**
     * Determines if you are in a dialogue or not.
     * 
     * @return true if you are in a dialogue.
     */
    public boolean inDialogue() {
        return dialogue != null;
    }

    /**
     * Calculates this players combat level.
     * 
     * @return the players combat level.
     */
    public int getCombatLevel() {
        int magLvl = skills[Misc.MAGIC].getLevelForExperience();
        int ranLvl = skills[Misc.RANGED].getLevelForExperience();
        int attLvl = skills[Misc.ATTACK].getLevelForExperience();
        int strLvl = skills[Misc.STRENGTH].getLevelForExperience();
        int defLvl = skills[Misc.DEFENCE].getLevelForExperience();
        int hitLvl = skills[Misc.HITPOINTS].getLevelForExperience();
        int prayLvl = skills[Misc.PRAYER].getLevelForExperience();
        double mag = magLvl * 1.5;
        double ran = ranLvl * 1.5;
        double attstr = attLvl + strLvl;

        combatLevel = 0;

        if (ran > attstr) {
            combatLevel = ((defLvl) * 0.25)
                    + ((hitLvl) * 0.25)
                    + ((prayLvl) * 0.125)
                    + ((ranLvl) * 0.4875);
        } else if (mag > attstr) {
            combatLevel = (((defLvl) * 0.25)
                    + ((hitLvl) * 0.25)
                    + ((ranLvl) * 0.125) + ((magLvl) * 0.4875));
        } else {
            combatLevel = (((defLvl) * 0.25)
                    + ((hitLvl) * 0.25)
                    + ((prayLvl) * 0.125)
                    + ((attLvl) * 0.325) + ((strLvl) * 0.325));
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

            this.getPacketBuilder().sendString("@yel@Level: "
                    + wildernessLevel, 199);
        } else {
            this.getPacketBuilder().sendPlayerMenu("Attack", 3);
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
            this.setMultiCombatInterface(false);
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
            if (item == null
                    || item.getId() < 1
                    || item.getAmount() < 1) {
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
                send = Misc.BONUS_NAMES[i]
                        + ": +"
                        + playerBonus[i];
            } else {
                send = Misc.BONUS_NAMES[i]
                        + ": -"
                        + Math.abs(playerBonus[i]);
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

    public Set<Player> getPlayers() {
        return players;
    }

    public Set<Npc> getNpcs() {
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
        getPacketBuilder().sendString(getRunEnergy()
                + "%", 149);
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void decrementRunEnergy(int amount) {
        if ((runEnergy - amount) < 1) {
            runEnergy = 0;
            getPacketBuilder().sendString(getRunEnergy()
                    + "%", 149);
            return;
        }

        this.runEnergy -= amount;
        getPacketBuilder().sendString(getRunEnergy()
                + "%", 149);
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void incrementRunEnergy() {
        this.runEnergy += 1;
        getPacketBuilder().sendString(getRunEnergy()
                + "%", 149);
    }

    /**
     * @param runEnergy
     *        the runEnergy to set
     */
    public void incrementRunEnergy(int amount) {
        this.runEnergy += amount;
        getPacketBuilder().sendString(getRunEnergy()
                + "%", 149);
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

    public Stopwatch getPotionTimer() {
        return potionTimer;
    }

    public PacketEncoder getPacketBuilder() {
        return session.getServerPacketBuilder();
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

    public Session getSession() {
        return session;
    }

    public void setRights(PlayerRights rights) {
        this.rights = rights;
    }

    public PlayerRights getRights() {
        return rights;
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

    // XXX: The implementation of an attribute system is looking vital!
    public boolean isImmuneToDragonFire() {
        return dragonFireImmunity > 0;
    }

    public int getDragonFireImmunity() {
        return dragonFireImmunity;
    }

    public void setDragonFireImmunity(int drgonFireImmuity) {
        this.dragonFireImmunity = dragonFireImmunity;
    }

    public void incrementDragonFireImmunity(int amount) {
        dragonFireImmunity += amount;
    }

    public void decrementDragonFireImmunity(int amount) {
        dragonFireImmunity -= amount;
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
        return skills;
    }

    /**
     * @param trainable
     *        the trainable to set.
     */
    public void setTrainable(Skill[] trainable) {
        this.skills = trainable;
    }

    /**
     * @return the equipmentAnimation
     */
    public WeaponAnimationIndex getUpdateAnimation() {
        return equipmentAnimation;
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

    /**
     * @return the prayerActive
     */
    public boolean[] getPrayerActive() {
        return prayerActive;
    }

    /**
     * @return the prayerDrain
     */
    public Worker getPrayerDrain() {
        return prayerDrain;
    }

    /**
     * @param prayerDrain
     *        the prayerDrain to set
     */
    public void setPrayerDrain(CombatPrayerWorker prayerDrain) {
        this.prayerDrain = prayerDrain;
    }

    /**
     * @return the teleportStage
     */
    public int getTeleportStage() {
        return teleportStage;
    }

    /**
     * @param teleportStage
     *        the teleportStage to set
     */
    public void setTeleportStage(int teleportStage) {
        this.teleportStage = teleportStage;
    }

    /**
     * @return the skullTimer
     */
    public int getSkullTimer() {
        return skullTimer;
    }

    /**
     * @param skullTimer
     *        the skullTimer to set
     */
    public void setSkullTimer(int skullTimer) {
        this.skullTimer = skullTimer;
    }

    public void decrementSkullTimer() {
        skullTimer--;
    }

    /**
     * @return the acceptAid
     */
    public boolean isAcceptAid() {
        return acceptAid;
    }

    /**
     * @param acceptAid
     *        the acceptAid to set
     */
    public void setAcceptAid(boolean acceptAid) {
        this.acceptAid = acceptAid;
    }

    /**
     * @return the castSpell
     */
    public CombatSpell getCastSpell() {
        return castSpell;
    }

    /**
     * @param castSpell
     *        the castSpell to set
     */
    public void setCastSpell(CombatSpell castSpell) {
        this.castSpell = castSpell;
    }

    /**
     * @return the autocast
     */
    public boolean isAutocast() {
        return autocast;
    }

    /**
     * @param autocast
     *        the autocast to set
     */
    public void setAutocast(boolean autocast) {
        this.autocast = autocast;
    }

    /**
     * @return the teleblockTimer
     */
    public int getTeleblockTimer() {
        return teleblockTimer;
    }

    /**
     * @param teleblockTimer
     *        the teleblockTimer to set
     */
    public void setTeleblockTimer(int teleblockTimer) {
        this.teleblockTimer = teleblockTimer;
    }

    public void decrementTeleblockTimer() {
        teleblockTimer--;
    }

    /**
     * @return the autocastSpell
     */
    public CombatSpell getAutocastSpell() {
        return autocastSpell;
    }

    /**
     * @param autocastSpell
     *        the autocastSpell to set
     */
    public void setAutocastSpell(CombatSpell autocastSpell) {
        this.autocastSpell = autocastSpell;
    }

    /**
     * @return the specialPercentage
     */
    public int getSpecialPercentage() {
        return specialPercentage;
    }

    /**
     * @param specialPercentage
     *        the specialPercentage to set
     */
    public void setSpecialPercentage(int specialPercentage) {
        this.specialPercentage = specialPercentage;
    }

    /**
     * @return the fireAmmo
     */
    public int getFireAmmo() {
        return fireAmmo;
    }

    /**
     * @param fireAmmo
     *        the fireAmmo to set
     */
    public void setFireAmmo(int fireAmmo) {
        this.fireAmmo = fireAmmo;
    }

    /**
     * @return the combatSpecial
     */
    public CombatSpecial getCombatSpecial() {
        return combatSpecial;
    }

    /**
     * @param combatSpecial
     *        the combatSpecial to set
     */
    public void setCombatSpecial(CombatSpecial combatSpecial) {
        this.combatSpecial = combatSpecial;
    }

    /**
     * @return the specialActivated
     */
    public boolean isSpecialActivated() {
        return specialActivated;
    }

    /**
     * @param specialActivated
     *        the specialActivated to set
     */
    public void setSpecialActivated(boolean specialActivated) {
        this.specialActivated = specialActivated;
    }

    public void decrementSpecialPercentage(int drainAmount) {
        this.specialPercentage -= drainAmount;

        if (specialPercentage < 0) {
            specialPercentage = 0;
        }
    }

    public void incrementSpecialPercentage(int gainAmount) {
        this.specialPercentage += gainAmount;

        if (specialPercentage > 100) {
            specialPercentage = 100;
        }
    }

    /**
     * @return the rangedAmmo
     */
    public CombatRangedAmmo getRangedAmmo() {
        return rangedAmmo;
    }

    /**
     * @param rangedAmmo
     *        the rangedAmmo to set
     */
    public void setRangedAmmo(CombatRangedAmmo rangedAmmo) {
        this.rangedAmmo = rangedAmmo;
    }

    /**
     * Gets the cached update block.
     * 
     * @return the cached update block.
     */
    public ByteBuffer getCachedUpdateBlock() {
        return cachedUpdateBlock;
    }

    /**
     * Sets the cached update block.
     * 
     * @param cachedUpdateBlock
     *        the cached update block.
     */
    public void setCachedUpdateBlock(ByteBuffer cachedUpdateBlock) {
        this.cachedUpdateBlock = cachedUpdateBlock;
    }

    /**
     * @return the usernameHash
     */
    public long getUsernameHash() {
        return usernameHash;
    }

    /**
     * @param usernameHash
     *        the usernameHash to set
     */
    public void setUsernameHash(long usernameHash) {
        this.usernameHash = usernameHash;
    }

    /**
     * @return the examplePoints
     */
    public int getExamplePoints() {
        return examplePoints;
    }

    /**
     * @param examplePoints
     *        the examplePoints to set
     */
    public void setExamplePoints(int examplePoints) {
        this.examplePoints = examplePoints;
    }
}
