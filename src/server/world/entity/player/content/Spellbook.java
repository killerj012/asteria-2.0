package server.world.entity.player.content;

import server.world.entity.player.Player;
import server.world.entity.player.content.TeleportSpell.Teleport;

/**
 * All of the possible spellbooks that can be used.
 * 
 * @author lare96
 */
public enum Spellbook {

    /** The normal spellbook. */
    NORMAL(1151, Teleport.NORMAL_SPELLBOOK_TELEPORT),

    /** The ancient spellbook. */
    ANCIENT(12855, Teleport.ANCIENTS_SPELLBOOK_TELEPORT);

    /**
     * The interface of the spellbook that will be displayed in the magic tab
     * sidebar.
     */
    private int sidebarInterface;

    /** The teleport type this spellbook uses. */
    private Teleport teleport;

    /**
     * Create a new {@link SpellBook}.
     * 
     * @param sidebarInterface
     *            the sidebar interface for this spellbook.
     * @param teleport
     *            the teleport type this spellbook uses.
     */
    Spellbook(int sidebarInterface, Teleport teleport) {
        this.sidebarInterface = sidebarInterface;
        this.teleport = teleport;
    }

    /**
     * Converts the player to a different magic type.
     * 
     * @param player
     *            the player converting.
     * @param book
     *            the book to convert to.
     */
    public static void convert(Player player, Spellbook book) {
        player.getPacketBuilder().sendSidebarInterface(6,
                book.getSidebarInterface());
        player.setSpellbook(book);
        player.getPacketBuilder().sendMessage(
                "You convert to "
                        + book.name().toLowerCase().replaceAll("_", " ")
                        + " magicks!");
    }

    /**
     * Gets the the interface of the spellbook.
     * 
     * @return the sidebar interface.
     */
    public int getSidebarInterface() {
        return sidebarInterface;
    }

    /**
     * Gets the teleport type this spellbook uses.
     * 
     * @return the teleport type.
     */
    public Teleport getTeleport() {
        return teleport;
    }
}
