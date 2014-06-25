package server.world.entity.npc;

import server.core.worker.Worker;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.EntityType;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Position;

/**
 * A non-player-character that extends Entity so that we can share the many
 * similar attributes.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Npc extends Entity {

    /** The npc ID. */
    private int npcId;

    /** Whether or not the npc is visible. */
    private boolean isVisible = true;

    /** The npcs max health. */
    private int maxHealth;

    /** The npcs current health. */
    private int currentHealth;

    /** If this npc respawns or not. */
    private boolean respawn;

    /** Determines if npcs have been weakened. */
    private boolean[] statsWeakened = new boolean[3],
            statsBadlyWeakened = new boolean[3];

    /** The movement coordinator for this npc. */
    private NpcMovementCoordinator movementCoordinator = new NpcMovementCoordinator(this);

    /**
     * The npcs position from the moment of conception. This position never
     * changes.
     */
    private Position originalPosition = new Position();

    /** If this npc was originally random walking. */
    private boolean originalRandomWalk;

    /** The respawn ticks. */
    private int respawnTicks;

    /**
     * Creates a new {@link Npc}.
     * 
     * @param npcId
     *        the npc ID.
     * @param position
     *        the npcs position.
     */
    public Npc(int npcId, Position position) {
        this.npcId = npcId;
        this.getPosition().setAs(position.clone());
        this.originalPosition.setAs(position.clone());
        this.maxHealth = getDefinition().getHitpoints();
        this.setCurrentHealth(getDefinition().getHitpoints());
        this.setAutoRetaliate(true);
        this.getFlags().flag(Flag.APPEARANCE);
    }

    @Override
    public void pulse() throws Exception {
        // XXX: Equal to the "process()" method, the only thing that should be
        // in here is movement... nothing else! Use workers for delayed actions!

        movementCoordinator.coordinate();
        getMovementQueue().execute();
    }

    @Override
    public Worker death() throws Exception {
        return new Worker(1, false) {

            @Override
            public void fire() {

                /** After two ticks play the death animation for this npc. */
                if (getDeathTicks() == 1) {
                    animation(new Animation(getDefinition().getDeathAnimation()));

                    /** After 7 ticks remove the npc and begin respawning. */
                } else if (getDeathTicks() == 6) {

                    /**
                     * Drop the items on death and remove the npc from the area.
                     */
                    if (respawnTicks == 0) {
                        Player killer = getCombatBuilder().getKiller();
                        dropDeathItems(killer);
                        move(new Position(1, 1));

                        if (!isRespawn()) {
                            this.cancel();
                        }
                    }

                    /** Respawn the npc when a set amount of time has elapsed. */
                    if (respawnTicks == getRespawnTime()) {
                        Npc npc = new Npc(npcId, getOriginalPosition());
                        npc.setRespawn(true);
                        World.getNpcs().add(npc);
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
        getPosition().setAs(new Position(1, 1));
        getFlags().flag(Flag.APPEARANCE);
        World.getNpcs().remove(this);
    }

    @Override
    public int getAttackSpeed() {
        return this.getDefinition().getAttackSpeed();
    }

    @Override
    public int getCurrentHealth() {
        return this.getCurrentHP();
    }

    @Override
    public String toString() {
        return "NPC[slot= " + getSlot() + ", name=" + getDefinition().getName() + "]";
    }

    @Override
    public EntityType type() {
        return EntityType.NPC;
    }

    /**
     * Drops items for the player that killed this npc.
     * 
     * @param killer
     *        the killer for this npc.
     */
    public void dropDeathItems(Player killer) {

        /** Validate the killer recieved. */
        if (killer == null) {
            return;
        }

        /** Get the drop table for this npc. */
        NpcDropTable table = NpcDropTable.getAllDrops().get(npcId);

        /** Validate the drop table recieved. */
        if (table == null) {
            return;
        }

        /** Drop the items for the player. */
        Item[] dropItems = table.calculateDrops(killer);

        for (Item drop : dropItems) {
            if (drop == null) {
                continue;
            }

            World.getGroundItems().register(new GroundItem(drop, new Position(getPosition().getX(), getPosition().getY(), getPosition().getZ()), killer));
        }

        for (Minigame minigame : MinigameFactory.getMinigames().values()) {
            if (minigame.inMinigame(killer)) {
                minigame.fireOnKill(killer, this);
            }
        }
    }

    /**
     * Gets the respawn time in ticks.
     * 
     * @return the respawn time in ticks.
     */
    public int getRespawnTime() {
        return (getDefinition().getRespawnTime() == 0 ? 1 : getDefinition().getRespawnTime());
    }

    /**
     * Increases this npcs health.
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
     * Decreases this npcs health.
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
     * Gets the npc id.
     * 
     * @return the npc id.
     */
    public int getNpcId() {
        return npcId;
    }

    /**
     * Gets if this npc is visible or not.
     * 
     * @return true if this npc is visible.
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Set this npc's visibility.
     * 
     * @param isVisible
     *        if this npc should be visible or invisible.
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Gets the max health of this npc.
     * 
     * @return the max health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets this npc's current health.
     * 
     * @return the current health.
     */
    public int getCurrentHP() {
        return currentHealth;
    }

    /**
     * Sets this npc's current health.
     * 
     * @param currentHealth
     *        the new health value to set.
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    /**
     * Gets the original position of this npc (from the moment of conception).
     * 
     * @return the original position.
     */
    public Position getOriginalPosition() {
        return originalPosition;
    }

    /**
     * Gets a npc definition.
     * 
     * @param id
     *        the npc definition to get.
     * @return the definition.
     */
    public NpcDefinition getDefinition() {
        return NpcDefinition.getNpcDefinition()[npcId];
    }

    /**
     * Gets if this npc was originally walking.
     * 
     * @return the original random walk.
     */
    public boolean isOriginalRandomWalk() {
        return originalRandomWalk;
    }

    /**
     * Sets if this npc was originally walking.
     * 
     * @param originalRandomWalk
     *        the original random walk to set.
     */
    public void setOriginalRandomWalk(boolean originalRandomWalk) {
        this.originalRandomWalk = originalRandomWalk;
    }

    /**
     * Sets if this npc should respawn on death.
     * 
     * @param respawn
     *        the respawn to set.
     */
    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    /**
     * Gets if this npc will respawn on death.
     * 
     * @return the respawn.
     */
    public boolean isRespawn() {
        return respawn;
    }

    /**
     * Get the movement coordinator.
     * 
     * @return the movement coordinator.
     */
    public NpcMovementCoordinator getMovementCoordinator() {
        return movementCoordinator;
    }

    /**
     * @return the statsWeakened
     */
    public boolean[] getStatsWeakened() {
        return statsWeakened;
    }

    /**
     * @return the statsBadlyWeakened
     */
    public boolean[] getStatsBadlyWeakened() {
        return statsBadlyWeakened;
    }
}
