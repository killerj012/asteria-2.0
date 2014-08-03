package com.asteria;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.asteria.engine.GameEngine;
import com.asteria.engine.net.HostGateway;
import com.asteria.engine.net.ServerEngine;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.util.Stopwatch;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect.CombatPoisonData;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcDefinition;
import com.asteria.world.entity.npc.NpcDropTable;
import com.asteria.world.entity.player.content.AssignSkillRequirement;
import com.asteria.world.entity.player.content.AssignWeaponAnimation;
import com.asteria.world.entity.player.content.AssignWeaponInterface;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.item.ItemDefinition;
import com.asteria.world.item.ground.GroundItemManager;
import com.asteria.world.object.WorldObjectManager;
import com.asteria.world.shop.Shop;

/**
 * The 'origin' class which contains the first method executed when this
 * application is ran.
 * 
 * @author lare96
 */
public final class Main {

    /** The logger for printing information. */
    private static final Logger logger = Logger.getLogger(Main.class
            .getSimpleName());

    /** The name of this server. */
    public static final String NAME = "Asteria 2.0";

    /**
     * The first method invoked when the server is ran.
     * 
     * @param args
     *            the array of runtime arguments.
     */
    // XXX: Loading in parallel?
    public static void main(String[] args) {
        try {
            // The stopwatch for timing how long all this takes.
            Stopwatch timer = new Stopwatch().reset();

            // Load all of the json stuff.
            NpcDropTable.parseDrops().load();
            ItemDefinition.parseItems().load();
            WorldObjectManager.parseObjects().load();
            NpcDefinition.parseNpcs().load();
            Shop.parseShops().load();
            GroundItemManager.parseItems().load();
            Npc.parseNpcs().load();

            // Load all of the IP banned hosts.
            HostGateway.loadBannedHosts();

            // Load all of the various skills.
            SkillEvent.loadSkills();

            // Load all of the packets.
            PacketDecoder.loadDecoders();

            // Load all of the minigames.
            MinigameFactory.loadMinigames();

            // Load all of the weapon animations.
            AssignWeaponAnimation.loadWeaponAnimations();

            // Load all of the weapon interfaces.
            AssignWeaponInterface.loadWeaponInterfaces();

            // Load all of the skill requirements.
            AssignSkillRequirement.loadSkillRequirements();

            // Load all of the poison data.
            CombatPoisonData.loadPoisonData();
            logger.info("Sucessfully loaded all utilities!");

            // Initialize and start the reactor.
            ServerEngine.init();
            logger.info("The reactor is now running!");

            // Initialize and start the engine.
            GameEngine.init();
            logger.info("The engine is now running!");

            // Asteria is now online!
            logger.info(NAME + " is now online! ".concat("[took " + timer
                    .elapsed() + " ms]"));
        } catch (Exception e) {

            // An error occurred, print it.
            logger.log(Level.SEVERE,
                    "An error occured while starting " + Main.NAME + "!", e);
        }
    }

    private Main() {}
}
