package com.asteria.world.entity;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskFactory;
import com.asteria.util.Stopwatch;
import com.asteria.world.World;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.combat.CombatBuilder;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.combat.magic.CombatSpell;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Position;

/**
 * A parent class that can be represented by a {@link Player} or {@link Npc}.
 * Things common to both types should be placed in this class.
 * 
 * @author lare96
 */
public abstract class Entity {

    /** The index of the entity. */
    private int slot = -1;

    /** The poison damage for this entity. */
    private int poisonDamage;

    /** If this entity has been unregistered. */
    private boolean unregistered;

    /** The position of the entity. */
    private Position position = Player.STARTING_POSITION.clone();

    /** The combat builder. */
    private CombatBuilder combatBuilder = new CombatBuilder(this);

    /** Used for placing actions at the end of the walking queue. */
    private MovementQueueListener movementListener = new MovementQueueListener(
            this);

    /** The spell currently being casted. */
    private CombatSpell currentlyCasting;

    /** Update flags for this entity. */
    private UpdateFlags flags = new UpdateFlags();

    /** The primary direction of the entity. */
    private int primaryDirection = -1;

    /** The secondary direction of the entity. */
    private int secondaryDirection = -1;

    /** The last direction of the entity. */
    private int lastDirection = 0;

    /** If this entity needs placement. */
    private boolean needsPlacement;

    /** If the movement queue needs to be reset. */
    private boolean resetMovementQueue;

    /** The current animation you are performing. */
    private Animation animation = new Animation();

    /** The current graphic you are performing. */
    private Graphic graphic = new Graphic();

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

    /** Handles movement for the entity. */
    private MovementQueue movementQueue = new MovementQueue(this);

    /** The current region of the entity. */
    private Position currentRegion = new Position();

    /** The last time you were hit. */
    private Stopwatch lastCombat = new Stopwatch().headStart(10000);

    /** The freeze delay. */
    private long freezeDelay;

    /** The freeze timer. */
    private Stopwatch freezeTimer = new Stopwatch();

    /** If the entity should fight back when attacked. */
    private boolean autoRetaliate;

    /** If the entity is currently following someone. */
    private boolean following;

    /** The entity this entity is following. */
    private Entity followEntity;

    /** If this entity has been killed. */
    private boolean dead;

    /**
     * All of the possible types that can be assigned to an entity. We have this
     * to get around using <code>instanceof</code>.
     * 
     * @author lare96
     */
    public enum EntityType {
        PLAYER,
        NPC
    }

    /**
     * Handles processing for this entity.
     */
    public abstract void pulse() throws Exception;

    /**
     * Gets the attack speed for the entity.
     */
    public abstract int getAttackSpeed();

    /**
     * Moves this entity to another position.
     * 
     * @param position
     *            the new position to move this entity on.
     */
    public abstract void move(Position position);

    /**
     * Gets the current health of the entity.
     * 
     * @return the current health of the entity.
     */
    public abstract int getCurrentHealth();

    /**
     * Get the type of the entity.
     * 
     * @return the type of the entity.
     */
    public abstract EntityType type();

    /**
     * Decrements this entity's health.
     * 
     * @param hit
     *            the hit to decrement the entity's health by.
     * @return the new hit that will be dealt.
     */
    public abstract Hit decrementHealth(Hit hit);

    /**
     * Gets the calculated combat strategy.
     * 
     * @return the calculated combat strategy.
     */
    public abstract CombatStrategy determineStrategy();

    /**
     * Gets the base attack for the argued combat type.
     * 
     * @param type
     *            the argued combat type.
     * @return the base attack.
     */
    public abstract int getBaseAttack(CombatType type);

    /**
     * Gets the base defence for the argued combat type.
     * 
     * @param type
     *            the argued combat type.
     * @return the base defence.
     */
    public abstract int getBaseDefence(CombatType type);

    /**
     * Poisons the argued victim if applicable.
     * 
     * @param victim
     *            the victim to poison.
     * @param type
     *            the combat type being used.
     */
    public abstract void poisonVictim(Entity victim, CombatType type);

    /**
     * Restores the amount of argued damage.
     * 
     * @param damage
     *            the amount of damage to restore.
     */
    public abstract void heal(int damage);

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Entity)) {
            return false;
        }

        Entity e = (Entity) o;

        if (e.type() != type()) {
            return false;
        }
        return e.slot == slot;
    }

    @Override
    public String toString() {
        if (type() == EntityType.NPC) {
            return World.getNpcs().get(slot).toString();
        }
        return World.getPlayers().get(slot).toString();
    }

    /**
     * Resets this entity after updating.
     */
    public void reset() {
        primaryDirection = -1;
        secondaryDirection = -1;
        flags.reset();
        resetMovementQueue = false;
        needsPlacement = false;
    }

    /**
     * Play an animation for this entity.
     * 
     * @param animation
     *            the animation to play.
     */
    public void animation(Animation animation) {
        this.animation = animation.clone();
        flags.flag(Flag.ANIMATION);
    }

    /**
     * Play a graphic for this entity.
     * 
     * @param graphic
     *            the graphic to play.
     */
    public void graphic(Graphic graphic) {
        this.graphic = graphic.clone();
        flags.flag(Flag.GRAPHICS);
    }

    /**
     * Play a high graphic for this entity.
     * 
     * @param graphic
     *            the graphic to play.
     */
    public void highGraphic(Graphic graphic) {
        this.graphic = graphic.clone();
        this.graphic.setHeight(6553600);
        flags.flag(Flag.GRAPHICS);
    }

    /**
     * Force chat for this entity.
     * 
     * @param forcedText
     *            the text to force.
     */
    public void forceChat(String forcedText) {
        this.forcedText = forcedText;
        flags.flag(Flag.FORCED_CHAT);
    }

    /**
     * Make this entity face another entity.
     * 
     * @param entity
     *            the entity to face.
     */
    public void faceEntity(Entity entity) {
        if (entity == null) {
            this.faceIndex = 65535;
            flags.flag(Flag.FACE_ENTITY);
            return;
        }
        this.faceIndex = entity.type() == EntityType.PLAYER ? entity.slot + 32768
                : entity.slot;
        flags.flag(Flag.FACE_ENTITY);
    }

    /**
     * Make this entity face the specified coordinates.
     * 
     * @param position
     *            the position to face.
     */
    public void facePosition(Position position) {
        faceCoordinates.setX(2 * position.getX() + 1);
        faceCoordinates.setY(2 * position.getY() + 1);
        flags.flag(Flag.FACE_COORDINATE);
    }

    /**
     * Deals one damage to this entity.
     * 
     * @param hit
     *            the damage to be dealt.
     */
    public void dealDamage(Hit hit) {
        primaryHit = decrementHealth(hit);
        flags.flag(Flag.HIT);
    }

    /**
     * Deal secondary damage to this entity.
     * 
     * @param hit
     *            the damage to be dealt.
     */
    private void dealSecondaryDamage(Hit hit) {
        secondaryHit = decrementHealth(hit);
        flags.flag(Flag.HIT_2);
    }

    /**
     * Deals two damage splats to this entity.
     * 
     * @param hit
     *            the first hit.
     * @param secondHit
     *            the second hit.
     */
    public void dealDoubleDamage(Hit hit, Hit secondHit) {
        dealDamage(hit);
        dealSecondaryDamage(secondHit);
    }

    /**
     * Deals three damage splats to this entity.
     * 
     * @param hit
     *            the first hit.
     * @param secondHit
     *            the second hit.
     * @param thirdHit
     *            the third hit.
     */
    public void dealTripleDamage(Hit hit, Hit secondHit, final Hit thirdHit) {
        dealDoubleDamage(hit, secondHit);

        TaskFactory.submit(new Task(1, false) {
            @Override
            public void fire() {
                if (unregistered) {
                    this.cancel();
                    return;
                }
                dealDamage(thirdHit);
                this.cancel();
            }
        });
    }

    /**
     * Deals four damage splats to this entity.
     * 
     * @param hit
     *            the first hit.
     * @param secondHit
     *            the second hit.
     * @param thirdHit
     *            the third hit.
     * @param fourthHit
     *            the fourth hit.
     */
    public void dealQuadrupleDamage(Hit hit, Hit secondHit, final Hit thirdHit,
            final Hit fourthHit) {
        dealDoubleDamage(hit, secondHit);

        TaskFactory.submit(new Task(1, false) {
            @Override
            public void fire() {
                if (unregistered) {
                    this.cancel();
                    return;
                }
                dealDoubleDamage(thirdHit, fourthHit);
                this.cancel();
            }
        });
    }

    /**
     * Prepares to cast the argued spell on the argued victim.
     * 
     * @param spell
     *            the spell to cast.
     * @param victim
     *            the victim to cast the spell on.
     */
    public void prepareSpell(CombatSpell spell, Entity victim) {
        currentlyCasting = spell;
        currentlyCasting.startCast(this, victim);
    }

    /**
     * Set the slot for the entity.
     * 
     * @param slot
     *            the slot.
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
     *            the primary direction to set.
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
     *            the secondary direction to set.
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
     *            true if this entity needs placement.
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
     *            true if the entity is reset movement queue.
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
    public Graphic getGfx() {
        return graphic;
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
     *            the unregistered to set.
     */
    public void setUnregistered(boolean unregistered) {
        this.unregistered = unregistered;
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
     * @return the lastCombat
     */
    public Stopwatch getLastCombat() {
        return lastCombat;
    }

    public int getAndDecrementPoisonDamage() {
        return poisonDamage--;
    }

    public int getPoisonDamage() {
        return poisonDamage;
    }

    public void setPoisonDamage(int poisonDamage) {
        this.poisonDamage = poisonDamage;
    }

    public boolean isPoisoned() {
        return poisonDamage != 0;
    }

    /**
     * @return the lastDirection
     */
    public int getLastDirection() {
        return lastDirection;
    }

    /**
     * @param lastDirection
     *            the lastDirection to set
     */
    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }

    public boolean isAutoRetaliate() {
        return autoRetaliate;
    }

    public void setAutoRetaliate(boolean autoRetaliate) {
        this.autoRetaliate = autoRetaliate;
    }

    public boolean isFollowing() {
        return following;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public Stopwatch getFreezeTimer() {
        return freezeTimer;
    }

    public Entity getFollowEntity() {
        return followEntity;
    }

    public void setFollowEntity(Entity followEntity) {
        this.followEntity = followEntity;
    }

    public long getFreezeDelay() {
        return freezeDelay;
    }

    public void setFreezeDelay(long freezeDelay) {
        this.freezeDelay = freezeDelay;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isFrozen() {
        return freezeTimer.elapsed() < freezeDelay;
    }

    public CombatSpell getCurrentlyCasting() {
        return currentlyCasting;
    }

    public void setCurrentlyCasting(CombatSpell currentlyCasting) {
        this.currentlyCasting = currentlyCasting;
    }
}
