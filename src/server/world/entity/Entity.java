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
     * Deal primary damage to this entity.
     * 
     * @param primaryHit
     *        the damage and hit-type.
     */
    public void dealDamage(Hit primaryHit) {
        this.primaryHit = primaryHit.clone();
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
     * @param secondaryHit
     *        the damage and hit-type.
     */
    public void dealSecondaryDamage(Hit secondaryHit) {
        this.secondaryHit = secondaryHit.clone();
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
     * Gets the combat type.
     * 
     * @return the type.
     */
    public CombatType getType() {
        return type;
    }

    /**
     * Sets the combat type.
     * 
     * @param type
     *        the type to set.
     */
    public void setType(CombatType type) {
        this.type = type;
    }

    /**
     * Gets the combat session.
     * 
     * @return the combat session.
     */
    public CombatSession getCombatSession() {
        return combatSession;
    }
}
