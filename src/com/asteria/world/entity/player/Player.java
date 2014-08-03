package com.asteria.world.entity.player;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.asteria.Main;
import com.asteria.engine.net.Session;
import com.asteria.engine.net.packet.PacketEncoder;
import com.asteria.engine.task.Task;
import com.asteria.util.Stopwatch;
import com.asteria.util.Utility;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.asteria.world.entity.combat.magic.CombatSpell;
import com.asteria.world.entity.combat.prayer.CombatPrayerTask;
import com.asteria.world.entity.combat.range.CombatRangedAmmo;
import com.asteria.world.entity.combat.special.CombatSpecial;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.aggression.NpcAggression;
import com.asteria.world.entity.npc.dialogue.Dialogue;
import com.asteria.world.entity.player.content.AssignWeaponAnimation.WeaponAnimationIndex;
import com.asteria.world.entity.player.content.AssignWeaponInterface.FightType;
import com.asteria.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import com.asteria.world.entity.player.content.PrivateMessage;
import com.asteria.world.entity.player.content.Spellbook;
import com.asteria.world.entity.player.content.TeleportSpell;
import com.asteria.world.entity.player.content.TradeSession;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.entity.player.skill.Skill;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;
import com.asteria.world.item.container.BankContainer;
import com.asteria.world.item.container.EquipmentContainer;
import com.asteria.world.item.container.InventoryContainer;
import com.asteria.world.map.Location;
import com.asteria.world.map.Position;

/**
 * A logged in player that is able to receive and send packets and interact with
 * entities and world models.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Player extends Entity {

    /** The starting position. */
    public static final Position STARTING_POSITION = new Position(3093, 3244);

    /** The welcome message. */
    public static final String WELCOME_MESSAGE = "Welcome to " + Main.NAME + "!";

    /** The items received when the player logs in for the first time. */
    public static final Item[] STARTER_PACKAGE = { new Item(995, 10000) };

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

    /** A few player-bound tasks. */
    private Task prayerDrain, restoreRun;

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
    private int skullIcon = -1, skullTimer;

    /** Teleblock stuff. */
    private int teleblockTimer;

    /** The player's run energy. */
    private int runEnergy = 100;

    /** Handles a trading session with another player. */
    private TradeSession tradeSession = new TradeSession(this);

    /** A collection of timers. */
    private final Stopwatch eatingTimer = new Stopwatch().reset(),
            potionTimer = new Stopwatch().reset(), tolerance = new Stopwatch(),
            lastEnergy = new Stopwatch().reset();

    /** The multicombat and wilderness flags. */
    private boolean wildernessInterface, multicombatInterface;

    /** The amount of ticks this player is immune to dragon fire */
    private int dragonFireImmunity;

    /** The amount of ticks this player is immune to poison. */
    private int poisonImmunity;

    /** The username. */
    private String username;

    /** The password. */
    private String password;

    /** The points for the example currency. */
    private int examplePoints = 10;

    /** If this player is new. */
    private boolean newPlayer = true;

    /** Options for banking. */
    private boolean insertItem, withdrawAsNote;

    /** A list of local players. */
    private final Set<Player> players = new LinkedHashSet<Player>();

    /** A list of local npcs. */
    private final Set<Npc> npcs = new LinkedHashSet<Npc>();

    /** The players rights. */
    private PlayerRights rights;

    /** The players current spellbook. */
    private Spellbook spellbook = Spellbook.NORMAL;

    /** Variables for public chatting. */
    private int chatColor, chatEffects;

    /** The chat message in bytes. */
    private byte[] chatText;

    /** The gender. */
    private int gender = Utility.GENDER_MALE;

    /** The appearance. */
    private int[] appearance = new int[7], colors = new int[5];

    /** The player's bonuses. */
    private int[] playerBonus = new int[12];

    /** The friends list. */
    private List<Long> friends = new ArrayList<Long>(200);

    /** The ignores list. */
    private List<Long> ignores = new ArrayList<Long>(100);

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

    /** If the region has been updated. */
    private boolean updateRegion;

    /**
     * Creates a new {@link Player}.
     * 
     * @param session
     *            the session to create the player from.
     */
    public Player(Session session) {
        this.session = session;

        // Set the player's rights, if we're connecting locally we automatically
        // get developer status.
        rights = session.getHost().equals("127.0.0.1") || session.getHost()
                .equals("localhost") ? PlayerRights.DEVELOPER
                : PlayerRights.PLAYER;

        // Set the default appearance.
        getAppearance()[Utility.APPEARANCE_SLOT_CHEST] = 18;
        getAppearance()[Utility.APPEARANCE_SLOT_ARMS] = 26;
        getAppearance()[Utility.APPEARANCE_SLOT_LEGS] = 36;
        getAppearance()[Utility.APPEARANCE_SLOT_HEAD] = 0;
        getAppearance()[Utility.APPEARANCE_SLOT_HANDS] = 33;
        getAppearance()[Utility.APPEARANCE_SLOT_FEET] = 42;
        getAppearance()[Utility.APPEARANCE_SLOT_BEARD] = 10;

        // Set the default colors.
        getColors()[0] = 7;
        getColors()[1] = 8;
        getColors()[2] = 9;
        getColors()[3] = 5;
        getColors()[4] = 0;
    }

    @Override
    public Hit decrementHealth(Hit hit) {
        if (hit.getDamage() > skills[Skills.HITPOINTS].getLevel()) {
            hit.setDamage(skills[Skills.HITPOINTS].getLevel());
        }

        skills[Skills.HITPOINTS].decreaseLevel(hit.getDamage());
        Skills.refresh(this, Skills.HITPOINTS);
        getPacketBuilder().sendCloseWindows();
        return hit;
    }

    @Override
    public void pulse() throws Exception {
        if (session.getTimeout().elapsed() > 5000) {
            session.disconnect();
            return;
        }

        getMovementQueue().execute();
        NpcAggression.targetPlayer(this);
    }

    @Override
    public EntityType type() {
        return EntityType.PLAYER;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Player)) {
            return false;
        }

        Player p = (Player) o;
        return p.getSlot() == getSlot() || p.getUsername().equals(username);
    }

    @Override
    public CombatStrategy determineStrategy() {
        if (specialActivated) {
            if (combatSpecial.getCombatType() == CombatType.MELEE) {
                return CombatFactory.newDefaultMeleeStrategy();
            } else if (combatSpecial.getCombatType() == CombatType.RANGED) {
                return CombatFactory.newDefaultRangedStrategy();
            } else if (combatSpecial.getCombatType() == CombatType.MAGIC) {
                return CombatFactory.newDefaultMagicStrategy();
            }
        }

        if (castSpell != null || autocastSpell != null) {
            if (autocast) {
                castSpell = autocastSpell;
            }
            return CombatFactory.newDefaultMagicStrategy();
        }

        if (weapon == WeaponInterface.SHORTBOW || weapon == WeaponInterface.LONGBOW || weapon == WeaponInterface.CROSSBOW || weapon == WeaponInterface.DART || weapon == WeaponInterface.JAVELIN || weapon == WeaponInterface.THROWNAXE || weapon == WeaponInterface.KNIFE) {
            return CombatFactory.newDefaultRangedStrategy();
        }
        return CombatFactory.newDefaultMeleeStrategy();
    }

    @Override
    public void poisonVictim(Entity victim, CombatType type) {
        if (type == CombatType.MELEE || weapon == WeaponInterface.DART || weapon == WeaponInterface.KNIFE || weapon == WeaponInterface.THROWNAXE || weapon == WeaponInterface.JAVELIN) {
            CombatFactory.poisonEntity(victim, CombatPoisonData
                    .getPoisonType(equipment.getContainer().getItem(
                            Utility.EQUIPMENT_SLOT_WEAPON)));
        } else if (type == CombatType.RANGED) {
            CombatFactory.poisonEntity(victim, CombatPoisonData
                    .getPoisonType(equipment.getContainer().getItem(
                            Utility.EQUIPMENT_SLOT_ARROWS)));
        }
    }

    /**
     * An advanced teleport method that teleports the player to a position based
     * on the teleport spell.
     * 
     * @param position
     *            the position to teleport to.
     */
    public void teleport(final TeleportSpell spell) {

        if (teleportStage > 0) {
            return;
        }
        Player player = Player.this;

        if (wildernessLevel >= 20) {
            player.getPacketBuilder().sendMessage(
                    "You must be below level 20 wilderness to teleport!");
            return;
        }

        if (teleblockTimer > 0) {
            if ((teleblockTimer * 600) >= 1000 && (teleblockTimer * 600) <= 60000) {
                getPacketBuilder()
                        .sendMessage(
                                "You must wait approximately " + ((teleblockTimer * 600) / 1000) + " seconds in order to teleport!");
                return;
            } else if ((teleblockTimer * 600) > 60000) {
                getPacketBuilder()
                        .sendMessage(
                                "You must wait approximately " + ((teleblockTimer * 600) / 60000) + " minutes in order to teleport!");
                return;
            }
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(player)) {
                if (!minigame.canTeleport(player)) {
                    return;
                }
            }
        }

        if (!spell.canCast(player)) {
            return;
        }

        teleportStage = 1;
        getCombatBuilder().reset();
        faceEntity(null);
        setFollowing(false);
        setFollowEntity(null);
        getPacketBuilder().sendCloseWindows();
        Skills.experience(this, spell.baseExperience(), Skills.MAGIC);
        spell.type().fire(player, spell);
    }

    /**
     * The default teleport method that teleports the player to a position based
     * on the spellbook they have open.
     * 
     * @param position
     *            the position to teleport to.
     */
    public void teleport(final Position position) {
        teleport(new TeleportSpell() {
            @Override
            public Position teleportTo() {
                return position;
            }

            @Override
            public Teleport type() {
                Player player = Player.this;
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
        dialogueStage = 0;
        getMovementQueue().reset();
        getPacketBuilder().sendCloseWindows();
        getPosition().setAs(position);
        setResetMovementQueue(true);
        setNeedsPlacement(true);
        getPacketBuilder().sendMapRegion();
    }

    @Override
    public int getAttackSpeed() {
        int speed = weapon.getSpeed();

        if (fightType == FightType.CROSSBOW_RAPID || fightType == FightType.SHORTBOW_RAPID || fightType == FightType.LONGBOW_RAPID || fightType == FightType.DART_RAPID || fightType == FightType.KNIFE_RAPID || fightType == FightType.THROWNAXE_RAPID || fightType == FightType.JAVELIN_RAPID) {
            speed--;
        } else if (fightType == FightType.CROSSBOW_LONGRANGE || fightType == FightType.SHORTBOW_LONGRANGE || fightType == FightType.LONGBOW_LONGRANGE || fightType == FightType.DART_LONGRANGE || fightType == FightType.KNIFE_LONGRANGE || fightType == FightType.THROWNAXE_LONGRANGE || fightType == FightType.JAVELIN_LONGRANGE) {
            speed++;
        }

        return speed;
    }

    @Override
    public int getCurrentHealth() {
        return skills[Skills.HITPOINTS].getLevel();
    }

    @Override
    public String toString() {
        return getUsername() == null ? "SESSION[host= " + session.getHost() + ", stage= " + session
                .getStage().name() + "]"
                : "PLAYER[username= " + getUsername() + ", host= " + session
                        .getHost() + ", rights= " + rights + "]";
    }

    @Override
    public int getBaseAttack(CombatType type) {
        if (type == CombatType.RANGED)
            return skills[Skills.RANGED].getLevel();
        else if (type == CombatType.MAGIC)
            return skills[Skills.MAGIC].getLevel();
        return skills[Skills.ATTACK].getLevel();
    }

    @Override
    public int getBaseDefence(CombatType type) {
        if (type == CombatType.MAGIC)
            return skills[Skills.MAGIC].getLevel();
        return skills[Skills.DEFENCE].getLevel();
    }

    @Override
    public void heal(int amount) {
        int level = getSkills()[Skills.HITPOINTS].getLevelForExperience();

        if ((getSkills()[Skills.HITPOINTS].getLevel() + amount) >= level) {
            getSkills()[Skills.HITPOINTS].setLevel(level, true);
        } else {
            getSkills()[Skills.HITPOINTS].increaseLevel(amount);
        }

        Skills.refresh(this, Skills.HITPOINTS);
    }

    /**
     * Logs the player out.
     */
    public void logout() {
        Player.this.getPacketBuilder().sendLogout();
        session.disconnect();
    }

    /**
     * Starts a dialogue.
     * 
     * @param d
     *            the dialogue to start.
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
        Player player = Player.this;
        if (dialogue == null) {
            player.getPacketBuilder().sendCloseWindows();
            stopDialogue();
            return;
        }

        if ((dialogueStage + 1) > dialogue.getDialogues().length) {
            player.getPacketBuilder().sendCloseWindows();
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
        int magLvl = skills[Skills.MAGIC].getLevelForExperience();
        int ranLvl = skills[Skills.RANGED].getLevelForExperience();
        int attLvl = skills[Skills.ATTACK].getLevelForExperience();
        int strLvl = skills[Skills.STRENGTH].getLevelForExperience();
        int defLvl = skills[Skills.DEFENCE].getLevelForExperience();
        int hitLvl = skills[Skills.HITPOINTS].getLevelForExperience();
        int prayLvl = skills[Skills.PRAYER].getLevelForExperience();
        double mag = magLvl * 1.5;
        double ran = ranLvl * 1.5;
        double attstr = attLvl + strLvl;

        combatLevel = 0;

        if (ran > attstr && ran > mag) { // player is ranged class
            combatLevel = ((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((ranLvl) * 0.4875);
        } else if (mag > attstr) { // player is mage class
            combatLevel = (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((magLvl) * 0.4875));
        } else {
            combatLevel = (((defLvl) * 0.25) + ((hitLvl) * 0.25) + ((prayLvl / 2) * 0.25) + ((attLvl) * 0.325) + ((strLvl) * 0.325));
        }

        return (int) combatLevel;
    }

    /**
     * Displays the wilderness and multicombat interfaces for players when
     * needed.
     */
    public void displayInterfaces() {

        // Update the wilderness info.
        if (Location.inWilderness(this)) {
            int calculateY = this.getPosition().getY() > 6400 ? this
                    .getPosition().getY() - 6400 : this.getPosition().getY();
            wildernessLevel = (((calculateY - 3520) / 8) + 1);

            if (!wildernessInterface) {
                this.getPacketBuilder().sendWalkable(197);
                this.getPacketBuilder().sendContextMenu("Attack", 3);
                wildernessInterface = true;
            }

            this.getPacketBuilder().sendString(
                    "@yel@Level: " + wildernessLevel, 199);
        } else {
            this.getPacketBuilder().sendContextMenu("Attack", 3);
            this.getPacketBuilder().sendWalkable(-1);
            wildernessInterface = false;
            wildernessLevel = 0;
        }

        // Update the multicombat info.
        if (Location.inMultiCombat(this)) {
            if (!multicombatInterface) {
                this.getPacketBuilder().sendMultiCombatInterface(1);
                multicombatInterface = true;
            }
        } else {
            this.getPacketBuilder().sendMultiCombatInterface(0);
            multicombatInterface = false;
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
                send = Utility.BONUS_NAMES[i] + ": +" + playerBonus[i];
            } else {
                send = Utility.BONUS_NAMES[i] + ": -" + Math
                        .abs(playerBonus[i]);
            }

            if (i == 10) {
                offset = 1;
            }

            getPacketBuilder().sendString(send, (1675 + i + offset));
        }
    }

    /**
     * Sets the username.
     * 
     * @param username
     *            the username
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

    public String getCapitalizedUsername() {
        return Utility.capitalize(username);
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the password
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

    public Set<Player> getLocalPlayers() {
        return players;
    }

    public Set<Npc> getLocalNpcs() {
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
     *            the newPlayer to set
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
     *            the withdrawAsNote to set
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
     *            the insertItem to set
     */
    public void setInsertItem(boolean insertItem) {
        this.insertItem = insertItem;
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
     *            the friends to set
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
     *            the ignores to set
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
     *            the runEnergy to set
     */
    public void decrementRunEnergy() {
        this.runEnergy -= 1;
        decrementRunEnergy(1);
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
     */
    public void decrementRunEnergy(int amount) {
        if ((runEnergy - amount) < 0) {
            amount = 0;
        }

        this.runEnergy -= amount;
        getPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    public void restoreRunEnergy() {
        if (lastEnergy.elapsed() > 3500 && runEnergy < 100) {
            incrementRunEnergy(1);
            lastEnergy.reset();
        }
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
     */
    public void incrementRunEnergy(int amount) {
        this.runEnergy += amount;
        getPacketBuilder().sendString(getRunEnergy() + "%", 149);
    }

    /**
     * @param runEnergy
     *            the runEnergy to set
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
     *            the option to set
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
     *            the headIcon to set
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
     *            the openShopId to set
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
     *            the skullIcon to set
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
     *            the spellbook to set
     */
    public void setSpellbook(Spellbook spellbook) {
        this.spellbook = spellbook;
    }

    /**
     * @return the playerBonus
     */
    public int[] getBonus() {
        return playerBonus;
    }

    /**
     * @param playerBonus
     *            the playerBonus to set
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

    // XXX: The implementation of an attribute system is looking vital!
    public boolean isImmuneToDragonFire() {
        return dragonFireImmunity > 0;
    }

    public int getDragonFireImmunity() {
        return dragonFireImmunity;
    }

    public void setDragonFireImmunity(int dragonFireImmunity) {
        this.dragonFireImmunity = dragonFireImmunity;
    }

    public void incrementDragonFireImmunity(int amount) {
        dragonFireImmunity += amount;
    }

    public void decrementDragonFireImmunity(int amount) {
        dragonFireImmunity -= amount;
    }

    public int getPoisonImmunity() {
        return poisonImmunity;
    }

    public void setPoisonImmunity(int poisonImmunity) {
        this.poisonImmunity = poisonImmunity;
    }

    public void incrementPoisonImmunity(int amount) {
        poisonImmunity += amount;
    }

    public void decrementPoisonImmunity(int amount) {
        poisonImmunity -= amount;
    }

    /**
     * @return the isBanned
     */
    public boolean isBanned() {
        return isBanned;
    }

    /**
     * @param isBanned
     *            the isBanned to set
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
     *            the weapon to set.
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
     *            the trainable to set.
     */
    public void setSkills(Skill[] trainable) {
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
     *            the fightType to set
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
    public Task getPrayerDrain() {
        return prayerDrain;
    }

    /**
     * @param prayerDrain
     *            the prayerDrain to set
     */
    public void setPrayerDrain(CombatPrayerTask prayerDrain) {
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
     *            the teleportStage to set
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
     *            the skullTimer to set
     */
    public void setSkullTimer(int skullTimer) {
        this.skullTimer = skullTimer;
    }

    public void decrementSkullTimer() {
        skullTimer -= 50;
    }

    /**
     * @return the acceptAid
     */
    public boolean isAcceptAid() {
        return acceptAid;
    }

    /**
     * @param acceptAid
     *            the acceptAid to set
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
     *            the castSpell to set
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
     *            the autocast to set
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
     *            the teleblockTimer to set
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
     *            the autocastSpell to set
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
     *            the specialPercentage to set
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
     *            the fireAmmo to set
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
     *            the combatSpecial to set
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
     *            the specialActivated to set
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
     *            the rangedAmmo to set
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
     *            the cached update block.
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
     *            the usernameHash to set
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
     *            the examplePoints to set
     */
    public void setExamplePoints(int examplePoints) {
        this.examplePoints = examplePoints;
    }

    /**
     * @return the tolerance
     */
    public Stopwatch getTolerance() {
        return tolerance;
    }

    public Task getRestoreRun() {
        return restoreRun;
    }

    public void setRestoreRun(Task restoreRun) {
        this.restoreRun = restoreRun;
    }

    public boolean isUpdateRegion() {
        return updateRegion;
    }

    public void setUpdateRegion(boolean updateRegion) {
        this.updateRegion = updateRegion;
    }
}
