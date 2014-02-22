package server.world.entity.npc;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.UpdateFlags.Flag;
import server.world.map.Position;

/**
 * A non-player-character that extends Entity so that we can share the many
 * similar attributes.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Npc extends Entity {

    /**
     * The mob ID.
     */
    private int mobId;

    /**
     * Whether or not the mob is visible.
     */
    private boolean isVisible = true;

    /**
     * The mobs max health.
     */
    private int maxHealth;

    /**
     * The mobs current health.
     */
    private int currentHealth;

    /**
     * If this mob respawns or not.
     */
    private boolean respawn = true;

    /**
     * The mobs position from the moment of conception. This position never
     * changes.
     */
    private Position originalPosition = new Position();

    /**
     * If this mob was originally random walking.
     */
    private boolean originalRandomWalk;

    /**
     * The respawn ticks.
     */
    private int respawnTicks;

    /**
     * Handles random walking for this mob.
     */
    private NpcMovement randomWalking = new NpcMovement(this);

    /**
     * Creates a new {@link Npc}.
     * 
     * @param mobId
     *        the mob ID.
     * @param position
     *        the mobs position.
     */
    public Npc(int mobId, Position position) {
        this.mobId = mobId;
        this.getPosition().setAs(position);
        this.originalPosition.setAs(position);
        this.maxHealth = getDefinition().getHitpoints();
        this.setCurrentHealth(getDefinition().getHitpoints());
        this.setAutoRetaliate(true);
        this.getFlags().flag(Flag.APPEARANCE);
    }

    @Override
    public void pulse() throws Exception {
        this.getRandomWalking().walk();
        this.getMovementQueue().execute();
    }

    @Override
    public Worker death() throws Exception {
        return new Worker(1, false) {

            @Override
            public void fire() {

                /** After two ticks play the death animation for this mob. */
                if (getDeathTicks() == 1) {
                    animation(new Animation(getDefinition().getDeathAnimation()));

                    /** After 7 ticks remove the mob and begin respawning. */
                } else if (getDeathTicks() == 6) {

                    /** Drop the items on death and remove the mob from the area. */
                    if (respawnTicks == 0) {
                        // XXX: The mob would drop items here! Example...
                        // new WorldItem(new Item(526), new
                        // Position(getPosition().getX(), getPosition().getY()),
                        // World.getPlayer("lare96")).register();

                        move(new Position(1, 1));

                        if (!isRespawn()) {
                            this.cancel();
                        }
                    }

                    /** Respawn the mob when a set amount of time has elapsed. */
                    if (respawnTicks == getRespawnTime()) {
                        getPosition().setAs(getOriginalPosition());
                        register();
                        this.cancel();
                    } else {
                        respawnTicks++;
                    }
                    return;
                }

                incrementDeathTicks();
            }
        };
    }

    @Override
    public void move(Position position) {
        getMovementQueue().reset();
        getPosition().setAs(position);
        getFlags().flag(Flag.APPEARANCE);
        unregister();
    }

    @Override
    public void register() {
        for (int i = 1; i < Rs2Engine.getWorld().getNpcs().length; i++) {
            if (Rs2Engine.getWorld().getNpcs()[i] == null) {
                Rs2Engine.getWorld().getNpcs()[i] = this;
                this.setSlot(i);
                return;
            }
        }
        throw new IllegalStateException("Server is full!");
    }

    @Override
    public void unregister() {
        if (this.getSlot() == -1) {
            return;
        }

        Rs2Engine.getWorld().getNpcs()[this.getSlot()] = null;
        this.setUnregistered(true);
    }

    @Override
    public void follow(final Entity entity) {

    }

    @Override
    public String toString() {
        return "Mob(" + getSlot() + ":" + getDefinition().getName() + ")";
    }

    /**
     * Gets the respawn time in ticks.
     * 
     * @return the respawn time in ticks.
     */
    public int getRespawnTime() {
        return (getDefinition().getRespawnTime() == 0 ? 1 : getDefinition().getRespawnTime()) * 2;
    }

    /**
     * Increases this mobs health.
     * 
     * @param amount
     *        the amount to increase by.
     */
    public void increaseHealth(int amount) {
        if ((currentHealth + amount) > maxHealth) {
            currentHealth = maxHealth;
            return;
        }

        currentHealth += amount;
    }

    /**
     * Decreases this mobs health.
     * 
     * @param amount
     *        the amount to decrease by.
     */
    public void decreaseHealth(int amount) {
        if ((currentHealth - amount) < 0) {
            currentHealth = 0;
            return;
        }

        currentHealth -= amount;
    }

    /**
     * Gets the mob id.
     * 
     * @return the mob id.
     */
    public int getNpcId() {
        return mobId;
    }

    /**
     * Gets if this mob is visible or not.
     * 
     * @return true if this mob is visible.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set this mob's visibility.
     * 
     * @param isVisible
     *        if this mob should be visible or invisible.
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Gets the max health of this mob.
     * 
     * @return the max health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets this mob's current health.
     * 
     * @return the current health.
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * Sets this mob's current health.
     * 
     * @param currentHealth
     *        the new health value to set.
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    /**
     * Gets the original position of this mob (from the moment of conception).
     * 
     * @return the original position.
     */
    public Position getOriginalPosition() {
        return originalPosition;
    }

    /**
     * Gets the class that handles random walking for this mob.
     * 
     * @return the random walking.
     */
    public NpcMovement getRandomWalking() {
        return randomWalking;
    }

    /**
     * Gets a mob definition.
     * 
     * @param id
     *        the mob definition to get.
     * @return the definition.
     */
    public NpcDefinition getDefinition() {
        return NpcDefinition.getNpcDefinition()[mobId];
    }

    /**
     * Gets if this mob was originally walking.
     * 
     * @return the original random walk.
     */
    public boolean isOriginalRandomWalk() {
        return originalRandomWalk;
    }

    /**
     * Sets if this mob was originally walking.
     * 
     * @param originalRandomWalk
     *        the original random walk to set.
     */
    public void setOriginalRandomWalk(boolean originalRandomWalk) {
        this.originalRandomWalk = originalRandomWalk;
    }

    /**
     * Sets if this mob should respawn on death.
     * 
     * @param respawn
     *        the respawn to set.
     */
    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    /**
     * Gets if this mob will respawn on death.
     * 
     * @return the respawn.
     */
    public boolean isRespawn() {
        return respawn;
    }
}
