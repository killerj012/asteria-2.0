package com.asteria.world.entity.player.content;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskManager;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.Spell;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.map.Position;

/**
 * A {@link Spell} implementation that represents any teleportation spell that
 * can be cast. Only {@link Player}s may cast teleportation spells.
 * 
 * @author lare96
 */
public abstract class TeleportSpell extends Spell {

    /**
     * The different types of teleportation methods.
     * 
     * @author lare96
     */
    public enum Teleport {
        NORMAL_SPELLBOOK_TELEPORT {
            @Override
            public void fire(final Player player, final TeleportSpell spell) {
                player.animation(new Animation(714));

                TaskManager.submit(new Task(1, false) {
                    @Override
                    public void execute() {
                        if (player.getTeleportStage() == 1) {
                            player.graphic(new Graphic(308));
                            player.setTeleportStage(2);
                        } else if (player.getTeleportStage() == 2) {
                            player.setTeleportStage(3);
                        } else if (player.getTeleportStage() == 3) {
                            player.move(spell.teleportTo());
                            player.animation(new Animation(715));
                            player.setTeleportStage(0);
                            this.cancel();
                        }
                    }
                }.bind(this));
            }
        },
        ANCIENTS_SPELLBOOK_TELEPORT {
            @Override
            public void fire(final Player player, final TeleportSpell spell) {
                player.animation(new Animation(1979));

                TaskManager.submit(new Task(1, false) {
                    @Override
                    public void execute() {
                        if (player.getTeleportStage() == 1) {
                            player.graphic(new Graphic(392));
                            player.setTeleportStage(2);
                        } else if (player.getTeleportStage() == 2) {
                            player.setTeleportStage(3);
                        } else if (player.getTeleportStage() == 3) {
                            player.setTeleportStage(4);
                        } else if (player.getTeleportStage() == 4) {
                            player.move(spell.teleportTo());
                            player.setTeleportStage(0);
                            this.cancel();
                        }
                    }
                }.bind(this));
            }
        };

        /**
         * A dynamic method fired when the argued {@link Player} is teleported
         * using this teleport type.
         * 
         * @param player
         *            the player being teleported.
         * @param spell
         *            the teleport spell being used to teleport this player.
         */
        public abstract void fire(Player player, TeleportSpell spell);
    }

    /**
     * The position this teleport spell takes the {@link Player} to.
     * 
     * @return the position this spell takes the player to.
     */
    public abstract Position teleportTo();

    /**
     * The teleport method this spell uses.
     * 
     * @return the teleport method this spell uses.
     */
    public abstract Teleport type();

    @Override
    public Item[] equipmentRequired(Player player) {

        // Teleport spells never require any equipment.
        return null;
    }

    @Override
    public void startCast(Entity cast, Entity castOn) {}
}