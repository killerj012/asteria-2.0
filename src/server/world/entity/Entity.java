package server.world.entity;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.combat.CombatSession;
import server.world.entity.combat.Hit;
import server.world.entity.combat.Combat.CombatType;
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

    /** The combat type this entity is using. */
    private CombatType type;

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

    /** The combat session. */
    private CombatSession combatSession = new CombatSession(this);

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

    /**
     * Handles processing for this entity.
     */
    public abstract void pulse() throws Exception;

    /**
     * Handles death for this entity.
     */
    public abstract Worker death() throws Exception;

    /**
     * Moves this entity to another position.
     * 
     * @param position
     *        the new position to move this entity on.
     */
    public abstract void move(Position position);

    /**
     * Registers this entity for processing.
     */
    public abstract void register();

    /**
     * Unregisters this entity from processing.
     */
    public abstract void unregister();

    /**
     * Prompts this entity to start following another entity.
     * 
     * @param entity
     *        the entity to follow.
     */
    public abstract void follow(Entity follow);

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
     * @param text
     *        the text to force.
     */
    public void forceChat(String text) {
        this.setForcedText(text);
        this.getFlags().flag(Flag.FORCED_CHAT);
    }

    /**
     * Make this entity face another entity.
     * 
     * @param index
     *        the index of the entity to face.
     */
    public void faceEntity(int index) {
        this.setFaceIndex(index);
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
     * Deal primary damage to this entity.
     * 
     * @param hit
     *        the damage and hit-type.
     */
    public void primaryHit(Hit hit) {
        this.setPrimaryHit(hit);
        this.getFlags().flag(Flag.HIT);

        if (this instanceof Player) {
            Player player = (Player) this;

            player.getSkills()[Misc.HITPOINTS].decreaseLevel(player.getPrimaryHit().getDamage());
            SkillManager.refresh(player, SkillConstant.HITPOINTS);
        }
    }

    /**
     * Deal secondary damage to this entity.
     * 
     * @param hit
     *        the damage and hit-type.
     */
    public void secondaryHit(Hit hit) {
        this.setSecondaryHit(hit);
        this.getFlags().flag(Flag.HIT_2);

        if (this instanceof Player) {
            Player player = (Player) this;

            player.getSkills()[Misc.HITPOINTS].decreaseLevel(player.getPrimaryHit().getDamage());
            SkillManager.refresh(player, SkillConstant.HITPOINTS);
        }
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
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @return the primaryDirection.
     */
    public int getPrimaryDirection() {
        return primaryDirection;
    }

    /**
     * @param primaryDirection
     *        the primaryDirection to set.
     */
    public void setPrimaryDirection(int primaryDirection) {
        this.primaryDirection = primaryDirection;
    }

    /**
     * @return the secondaryDirection.
     */
    public int getSecondaryDirection() {
        return secondaryDirection;
    }

    /**
     * @param secondaryDirection
     *        the secondaryDirection to set.
     */
    public void setSecondaryDirection(int secondaryDirection) {
        this.secondaryDirection = secondaryDirection;
    }

    /**
     * @return the needsPlacement.
     */
    public boolean isNeedsPlacement() {
        return needsPlacement;
    }

    /**
     * @param needsPlacement
     *        the needsPlacement to set.
     */
    public void setNeedsPlacement(boolean needsPlacement) {
        this.needsPlacement = needsPlacement;
    }

    /**
     * @return the resetMovementQueue.
     */
    public boolean isResetMovementQueue() {
        return resetMovementQueue;
    }

    /**
     * @param resetMovementQueue
     *        the resetMovementQueue to set.
     */
    public void setResetMovementQueue(boolean resetMovementQueue) {
        this.resetMovementQueue = resetMovementQueue;
    }

    /**
     * @return the movementHandler.
     */
    public MovementQueue getMovementQueue() {
        return movementQueue;
    }

    /**
     * @return the currentRegion.
     */
    public Position getCurrentRegion() {
        return currentRegion;
    }

    /**
     * @return the flags.
     */
    public UpdateFlags getFlags() {
        return flags;
    }

    /**
     * @return the forcedText.
     */
    public String getForcedText() {
        return forcedText;
    }

    /**
     * @param forcedText
     *        the forcedText to set.
     */
    private void setForcedText(String forcedText) {
        this.forcedText = forcedText;
    }

    /**
     * @return the faceIndex.
     */
    public int getFaceIndex() {
        return faceIndex;
    }

    /**
     * @param faceIndex
     *        the faceIndex to set.
     */
    private void setFaceIndex(int faceIndex) {
        this.faceIndex = faceIndex;
    }

    /**
     * @return the faceCoordinates.
     */
    public Position getFaceCoordinates() {
        return faceCoordinates;
    }

    /**
     * @return the primaryHit.
     */
    public Hit getPrimaryHit() {
        return primaryHit;
    }

    /**
     * @param primaryHit
     *        the primaryHit to set.
     */
    private void setPrimaryHit(Hit primaryHit) {
        this.primaryHit = primaryHit;
    }

    /**
     * @return the secondaryHit.
     */
    public Hit getSecondaryHit() {
        return secondaryHit;
    }

    /**
     * @param secondaryHit
     *        the secondaryHit to set.
     */
    private void setSecondaryHit(Hit secondaryHit) {
        this.secondaryHit = secondaryHit;
    }

    /**
     * @return the hasDied.
     */
    public boolean isHasDied() {
        return hasDied;
    }

    /**
     * @param hasDied
     *        the hasDied to set.
     */
    public void setHasDied(boolean hasDied) {
        this.hasDied = hasDied;
    }

    /**
     * @return the deathTicks.
     */
    public int getDeathTicks() {
        return deathTicks;
    }

    /**
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
     * @return the animation.
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * @return the gfx.
     */
    public Gfx getGfx() {
        return gfx;
    }

    /**
     * @return the isAutoRetaliate.
     */
    public boolean isAutoRetaliate() {
        return isAutoRetaliate;
    }

    /**
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
     * @return the unregistered.
     */
    public boolean isUnregistered() {
        return unregistered;
    }

    /**
     * @param unregistered
     *        the unregistered to set.
     */
    public void setUnregistered(boolean unregistered) {
        this.unregistered = unregistered;
    }

    /**
     * @return the wildernessInterface.
     */
    public boolean isWildernessInterface() {
        return wildernessInterface;
    }

    /**
     * @param wildernessInterface
     *        the wildernessInterface to set.
     */
    public void setWildernessInterface(boolean wildernessInterface) {
        this.wildernessInterface = wildernessInterface;
    }

    /**
     * @return the multiCombatInterface.
     */
    public boolean isMultiCombatInterface() {
        return multiCombatInterface;
    }

    /**
     * @param multiCombatInterface
     *        the multiCombatInterface to set.
     */
    public void setMultiCombatInterface(boolean multiCombatInterface) {
        this.multiCombatInterface = multiCombatInterface;
    }

    /**
     * @return the type
     */
    public CombatType getType() {
        return type;
    }

    /**
     * @param type
     *        the type to set
     */
    public void setType(CombatType type) {
        this.type = type;
    }

    /**
     * @return the combatSession
     */
    public CombatSession getCombatSession() {
        return combatSession;
    }
}
