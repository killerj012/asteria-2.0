package server.world.entity;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.CombatBuilder;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoison;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Position;

/**
 * Holds fields and methods used by both players and NPCs.
 * 
 * @author lare96
 */
public abstract class Entity {

    /** The index of the entity. */
    private int slot = -1;

    /** If this entity is a player. */
    private boolean isPlayer;

    /** If this entity is an npc. */
    private boolean isNpc;

    /** The amount of poison hits left to take. */
    private int poisonHits;

    /** The strength of the poison. */
    private CombatPoison poisonStrength = CombatPoison.MILD;

    /** If this entity retaliates automatically. */
    private boolean isAutoRetaliate;

    /** If this entity has been unregistered. */
    private boolean unregistered;

    /** The position of the entity. */
    private Position position = new Position(3093, 3244);

    /** If this entity is is dead. */
    private boolean hasDied;

    /** If the wilderness interface has been updated. */
    private boolean wildernessInterface;

    /** If the multicombat interface has been updated. */
    private boolean multiCombatInterface;

    /** The combat builder. */
    private CombatBuilder combatBuilder = new CombatBuilder(this);

    /** Used for placing actions at the end of the walking queue. */
    private MovementQueueListener movementListener = new MovementQueueListener(this);

    /** Update flags for this entity. */
    private UpdateFlags flags = new UpdateFlags();

    /** The primary direction of the entity. */
    private int primaryDirection = -1;

    /** The secondary direction of the entity. */
    private int secondaryDirection = -1;

    /** If this entity needs placement. */
    private boolean needsPlacement;

    /** If the movement queue needs to be reset. */
    private boolean resetMovementQueue;

    /** The current animation you are performing. */
    private Animation animation = new Animation();

    /** The current gfx you are performing. */
    private Gfx gfx = new Gfx();

    /** The current text being forced. */
    private String forcedText;

    /** The current index you are facing. */
    private int faceIndex;

    /** The current coordinates you are facing. */
    private Position faceCoordinates = new Position();

    /** The current primary hit being dealt to you. */
    private Hit primaryHit;

    /** The current secondary hit being dealt to you. */
    private Hit secondaryHit;

    /** The death timer for controlled events. */
    private int deathTicks;

    /** Handles movement for the entity. */
    private MovementQueue movementQueue = new MovementQueue(this);

    /** The current region of the entity. */
    private Position currentRegion = new Position(0, 0, 0);

    /** If the player is following. */
    private boolean following;

    /** The entity you are following. */
    private Entity followingEntity;

    /** The follow worker. */
    private Worker followWorker = new Worker(-1, false) {
        @Override
        public void fire() {
        }
    }.terminateRun();

    /** The last time you were hit. */
    private Stopwatch lastCombat = new Stopwatch().headStart(10000);

    /**
     * Handles processing for this entity.
     */
    public abstract void pulse() throws Exception;

    /**
     * Handles death for this entity.
     */
    public abstract Worker death() throws Exception;

    /**
     * Gets the attack speed for the entity.
     */
    public abstract int getAttackSpeed();

    /**
     * Moves this entity to another position.
     * 
     * @param position
     *        the new position to move this entity on.
     */
    public abstract void move(Position position);

    /**
     * Gets the current health of the entity.
     * 
     * @return the current health of the entity.
     */
    public abstract int getCurrentHealth();

    /**
     * Resets this entity after updating.
     */
    public void reset() {
        setPrimaryDirection(-1);
        setSecondaryDirection(-1);
        flags.reset();
        setResetMovementQueue(false);
        setNeedsPlacement(false);
    }

    /**
     * Play an animation for this entity.
     * 
     * @param animation
     *        the animation to play.
     */
    public void animation(Animation animation) {
        this.getAnimation().setAs(animation);
        this.getFlags().flag(Flag.ANIMATION);
    }

    /**
     * Play a gfx for this entity.
     * 
     * @param gfx
     *        the gfx to play.
     */
    public void gfx(Gfx gfx) {
        this.getGfx().setAs(gfx);
        this.getFlags().flag(Flag.GRAPHICS);
    }

    /**
     * Force chat for this entity.
     * 
     * @param forcedText
     *        the text to force.
     */
    public void forceChat(String forcedText) {
        this.forcedText = forcedText;
        this.getFlags().flag(Flag.FORCED_CHAT);
    }

    /**
     * Make this entity face another entity.
     * 
     * @param faceIndex
     *        the index of the entity to face.
     */
    public void faceEntity(int faceIndex) {
        this.faceIndex = faceIndex;
        this.getFlags().flag(Flag.FACE_ENTITY);
    }

    /**
     * Make this entity face the specified coordinates.
     * 
     * @param position
     *        the position to face.
     */
    public void facePosition(Position position) {
        this.getFaceCoordinates().setX(2 * position.getX() + 1);
        this.getFaceCoordinates().setY(2 * position.getY() + 1);
        this.getFlags().flag(Flag.FACE_COORDINATE);
    }

    /**
     * Deals one damage to this entity.
     * 
     * @param hit
     *        the damage to be dealt.
     */
    public void dealDamage(Hit hit) {
        int writeDamage = hit.getDamage();

        if (isPlayer()) {
            Player player = (Player) this;

            if (writeDamage > player.getSkills()[Misc.HITPOINTS].getLevel()) {
                writeDamage = player.getSkills()[Misc.HITPOINTS].getLevel();
            }

            player.getSkills()[Misc.HITPOINTS].decreaseLevel(writeDamage);
            SkillManager.refresh(player, SkillConstant.HITPOINTS);
        } else if (isNpc()) {
            Npc npc = (Npc) this;

            if (writeDamage > npc.getCurrentHP()) {
                writeDamage = npc.getCurrentHP();
            }

            npc.decreaseHealth(writeDamage);
        }

        this.primaryHit = new Hit(writeDamage, hit.getType());
        this.getFlags().flag(Flag.HIT);
    }

    /**
     * Deal secondary damage to this entity.
     * 
     * @param secondaryHit
     *        the damage to be dealt.
     */
    private void dealSecondaryDamage(Hit secondaryHit) {
        int writeDamage = secondaryHit.getDamage();

        if (isPlayer()) {
            Player player = (Player) this;

            if (writeDamage > player.getSkills()[Misc.HITPOINTS].getLevel()) {
                writeDamage = player.getSkills()[Misc.HITPOINTS].getLevel();
            }

            player.getSkills()[Misc.HITPOINTS].decreaseLevel(player.getSecondaryHit().getDamage());
            SkillManager.refresh(player, SkillConstant.HITPOINTS);
        } else if (isNpc()) {
            Npc npc = (Npc) this;

            if (writeDamage > npc.getCurrentHP()) {
                writeDamage = npc.getCurrentHP();
            }

            npc.decreaseHealth(npc.getSecondaryHit().getDamage());
        }

        this.secondaryHit = secondaryHit.clone();
        this.getFlags().flag(Flag.HIT_2);
    }

    /**
     * Deals two damage splats to this entity.
     * 
     * @param hit
     *        the first hit.
     * @param secondHit
     *        the second hit.
     */
    public void dealDoubleDamage(Hit hit, Hit secondHit) {
        dealDamage(hit);
        dealSecondaryDamage(secondHit);
    }

    /**
     * Deals three damage splats to this entity.
     * 
     * @param hit
     *        the first hit.
     * @param secondHit
     *        the second hit.
     * @param thirdHit
     *        the third hit.
     */
    public void dealTripleDamage(Hit hit, Hit secondHit, final Hit thirdHit) {
        dealDoubleDamage(hit, secondHit);

        TaskFactory.getFactory().submit(new Worker(1, false) {
            @Override
            public void fire() {
                dealDamage(thirdHit);
                this.cancel();
            }
        });
    }

    /**
     * Deals four damage splats to this entity.
     * 
     * @param hit
     *        the first hit.
     * @param secondHit
     *        the second hit.
     * @param thirdHit
     *        the third hit.
     * @param fourthHit
     *        the fourth hit.
     */
    public void dealQuadrupleDamage(Hit hit, Hit secondHit, final Hit thirdHit, final Hit fourthHit) {
        dealDoubleDamage(hit, secondHit);

        TaskFactory.getFactory().submit(new Worker(1, false) {
            @Override
            public void fire() {
                dealDoubleDamage(thirdHit, fourthHit);
                this.cancel();
            }
        });
    }

    /**
     * Set the slot for the entity.
     * 
     * @param slot
     *        the slot.
     */
    public void setSlot(int slot) {
        this.slot = slot;
    }

    /**
     * Gets the entity's slot.
     * 
     * @return the slot.
     */
    public int getSlot() {
        return slot;
    }

    /**
     * Gets this entity's position.
     * 
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the primary direction for this entity.
     * 
     * @return the primary direction.
     */
    public int getPrimaryDirection() {
        return primaryDirection;
    }

    /**
     * Sets the primary direction for this entity.
     * 
     * @param primaryDirection
     *        the primary direction to set.
     */
    public void setPrimaryDirection(int primaryDirection) {
        this.primaryDirection = primaryDirection;
    }

    /**
     * Gets the secondary direction for this entity.
     * 
     * @return the secondary direction.
     */
    public int getSecondaryDirection() {
        return secondaryDirection;
    }

    /**
     * Sets the secondary direction for this entity.
     * 
     * @param secondaryDirection
     *        the secondary direction to set.
     */
    public void setSecondaryDirection(int secondaryDirection) {
        this.secondaryDirection = secondaryDirection;
    }

    /**
     * Gets if this entity needs placement.
     * 
     * @return true if this entity needs placement.
     */
    public boolean isNeedsPlacement() {
        return needsPlacement;
    }

    /**
     * Sets if this entity needs placement.
     * 
     * @param needsPlacement
     *        true if this entity needs placement.
     */
    public void setNeedsPlacement(boolean needsPlacement) {
        this.needsPlacement = needsPlacement;
    }

    /**
     * Gets if this entity is reset movement queue.
     * 
     * @return true if the entity is reset movement queue.
     */
    public boolean isResetMovementQueue() {
        return resetMovementQueue;
    }

    /**
     * Sets if this entity is reset movement queue.
     * 
     * @param resetMovementQueue
     *        true if the entity is reset movement queue.
     */
    public void setResetMovementQueue(boolean resetMovementQueue) {
        this.resetMovementQueue = resetMovementQueue;
    }

    /**
     * Gets the movement queue.
     * 
     * @return the movement queue.
     */
    public MovementQueue getMovementQueue() {
        return movementQueue;
    }

    /**
     * Gets the current region.
     * 
     * @return the current region.
     */
    public Position getCurrentRegion() {
        return currentRegion;
    }

    /**
     * Gets the update flags.
     * 
     * @return the flags.
     */
    public UpdateFlags getFlags() {
        return flags;
    }

    /**
     * Gets the forced text.
     * 
     * @return the forced text.
     */
    public String getForcedText() {
        return forcedText;
    }

    /**
     * Gets the face index.
     * 
     * @return the face index.
     */
    public int getFaceIndex() {
        return faceIndex;
    }

    /**
     * Get the face coordinates for this entity.
     * 
     * @return the faceCoordinates.
     */
    public Position getFaceCoordinates() {
        return faceCoordinates;
    }

    /**
     * Get the primary hit for this entity.
     * 
     * @return the primaryHit.
     */
    public Hit getPrimaryHit() {
        return primaryHit;
    }

    /**
     * Get the secondary hit for this entity.
     * 
     * @return the secondaryHit.
     */
    public Hit getSecondaryHit() {
        return secondaryHit;
    }

    /**
     * Get if this entity has died.
     * 
     * @return the hasDied.
     */
    public boolean isHasDied() {
        return hasDied;
    }

    /**
     * Set if this entity has died.
     * 
     * @param hasDied
     *        the hasDied to set.
     */
    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    /**
     * Gets the death ticks.
     * 
     * @return the deathTicks.
     */
    public int getDeathTicks() {
        return deathTicks;
    }

    /**
     * Sets the death ticks.
     * 
     * @param deathTicks
     *        the deathTicks to set.
     */
    public void setDeathTicks(int deathTicks) {
        this.deathTicks = deathTicks;
    }

    /**
     * Increments the death ticks.
     */
    public void incrementDeathTicks() {
        this.deathTicks++;
    }

    /**
     * Gets the animation.
     * 
     * @return the animation.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Gets the gfx.
     * 
     * @return the gfx.
     */
    public Gfx getGfx() {
        return gfx;
    }

    /**
     * Gets if this entity is in retaliate mode.
     * 
     * @return the isAutoRetaliate.
     */
    public boolean isAutoRetaliate() {
        return isAutoRetaliate;
    }

    /**
     * Sets if this entity is in retaliate mode.
     * 
     * @param isAutoRetaliate
     *        the isAutoRetaliate to set.
     */
    public void setAutoRetaliate(boolean isAutoRetaliate) {
        this.isAutoRetaliate = isAutoRetaliate;
    }

    /**
     * Gets the movement queue listener.
     * 
     * @return the listener.
     */
    public MovementQueueListener getMovementQueueListener() {
        return movementListener;
    }

    /**
     * Gets if this entity is registered.
     * 
     * @return the unregistered.
     */
    public boolean isUnregistered() {
        return unregistered;
    }

    /**
     * Sets if this entity is registered,
     * 
     * @param unregistered
     *        the unregistered to set.
     */
    public void setUnregistered(boolean unregistered) {
        this.unregistered = unregistered;
    }

    /**
     * Gets the wilderness interface.
     * 
     * @return the wildernessInterface.
     */
    public boolean isWildernessInterface() {
        return wildernessInterface;
    }

    /**
     * Sets the wilderness interface.
     * 
     * @param wildernessInterface
     *        the wildernessInterface to set.
     */
    public void setWildernessInterface(boolean wildernessInterface) {
        this.wildernessInterface = wildernessInterface;
    }

    /**
     * Gets the multicombat interface.
     * 
     * @return the multiCombatInterface.
     */
    public boolean isMultiCombatInterface() {
        return multiCombatInterface;
    }

    /**
     * Sets the multicombat interface.
     * 
     * @param multiCombatInterface
     *        the multiCombatInterface to set.
     */
    public void setMultiCombatInterface(boolean multiCombatInterface) {
        this.multiCombatInterface = multiCombatInterface;
    }

    /**
     * Gets the combat session.
     * 
     * @return the combat session.
     */
    public CombatBuilder getCombatBuilder() {
        return combatBuilder;
    }

    /**
     * @return the isPlayer
     */
    public boolean isPlayer() {
        return isPlayer;
    }

    /**
     * @param isPlayer
     *        the isPlayer to set
     */
    public void setPlayer(boolean isPlayer) {
        this.isPlayer = isPlayer;
    }

    /**
     * @return the isNpc
     */
    public boolean isNpc() {
        return isNpc;
    }

    /**
     * @param isNpc
     *        the isNpc to set
     */
    public void setNpc(boolean isNpc) {
        this.isNpc = isNpc;
    }

    /**
     * @return the following
     */
    public boolean isFollowing() {
        return following;
    }

    /**
     * @param following
     *        the following to set
     */
    public void setFollowing(boolean following) {
        this.following = following;
    }

    /**
     * @return the followWorker
     */
    public Worker getFollowWorker() {
        return followWorker;
    }

    /**
     * @param followWorker
     *        the followWorker to set
     */
    public void setFollowWorker(Worker followWorker) {
        this.followWorker = followWorker;
    }

    /**
     * @return the followingEntity
     */
    public Entity getFollowingEntity() {
        return followingEntity;
    }

    /**
     * @param followingEntity
     *        the followingEntity to set
     */
    public void setFollowingEntity(Entity followingEntity) {
        this.followingEntity = followingEntity;
    }

    /**
     * @return the lastCombat
     */
    public Stopwatch getLastCombat() {
        return lastCombat;
    }

    /**
     * @return the poisonHits
     */
    public int getPoisonHits() {
        return poisonHits;
    }

    /**
     * @param poisonHits
     *        the poisonHits to set
     */
    public void setPoisonHits(int poisonHits) {
        this.poisonHits = poisonHits;
    }

    /**
     * @return the poisonStrength
     */
    public CombatPoison getPoisonStrength() {
        return poisonStrength;
    }

    /**
     * @param poisonStrength
     *        the poisonStrength to set
     */
    public void setPoisonStrength(CombatPoison poisonStrength) {
        this.poisonStrength = poisonStrength;
    }

    public void decrementPoisonHits() {
        this.poisonHits--;
    }
}
