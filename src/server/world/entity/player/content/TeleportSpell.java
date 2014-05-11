package server.world.entity.player.content;

import server.world.entity.Entity;
import server.world.entity.Spell;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * Represents any teleportation spell that can be cast in game.
 * 
 * @author lare96
 */
public abstract class TeleportSpell extends Spell {

    /**
     * The different types of teleport methods.
     * 
     * @author lare96
     */
    public enum Teleport {
        NORMAL_SPELLBOOK_TELEPORT, ANCIENTS_SPELLBOOK_TELEPORT
    }

    /**
     * The position to teleport whomever.
     */
    public abstract Position teleportTo();

    /**
     * The teleport type of this spell.
     */
    public abstract Teleport type();

    @Override
    public int spellId() {
        /** Teleportation spells do not have an 'id'. */
        return -1;
    }

    @Override
    public Item[] equipmentRequired(Player player) {
        return null;
    }

    @Override
    public boolean prepareCast(Entity cast, Entity castOn) {
        return true;
    }

    @Override
    public void castSpell(Entity cast, Entity castOn) {

    }
}