package server.world.entity.player.skill.impl;

import java.util.HashMap;
import java.util.Map;

import server.world.GenericAction;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
 * Handles the agility skill. Currently has support for two full agility
 * courses.
 * 
 * @author lare96
 */
public class Agility extends SkillEvent {

    // TODO: finish this.

    /**
     * The singleton instance.
     */
    private static Agility singleton;

    /**
     * All of the agility courses.
     * 
     * @author lare96
     */
    public enum AgilityCourse {
        GNOME_AGILITY_COURSE(null),

        WILDERNESS_AGILITY_COURSE(new AgilityObstacle(2288, new GenericAction<Player>() {
            @Override
            public void fireAction(Player param) {
                // TODO Auto-generated method stub

            }
        }), new AgilityObstacle(2288, new GenericAction<Player>() {
            @Override
            public void fireAction(Player param) {
                // TODO Auto-generated method stub

            }
        }), new AgilityObstacle(2288, new GenericAction<Player>() {
            @Override
            public void fireAction(Player param) {
                // TODO Auto-generated method stub

            }
        }), new AgilityObstacle(2288, new GenericAction<Player>() {
            @Override
            public void fireAction(Player param) {
                // TODO Auto-generated method stub

            }
        }));

        private AgilityObstacle[] agilityObstacles;

        AgilityCourse(AgilityObstacle... agilityObstacles) {
            this.agilityObstacles = agilityObstacles;
        }

        /**
         * A map wrapper containing the course and another map with the agility
         * obstacles.
         */
        private static Map<AgilityCourse, HashMap<Integer, AgilityObstacle>> obstacles = new HashMap<AgilityCourse, HashMap<Integer, AgilityObstacle>>();

        static {
            for (AgilityCourse c : AgilityCourse.values()) {
                obstacles.put(c, new HashMap<Integer, AgilityObstacle>());

                for (AgilityObstacle a : c.getAgilityObstacles()) {
                    obstacles.get(c).put(a.getObject(), a);
                }
            }
        }

        public AgilityObstacle getAgilityObstacle(AgilityCourse course, int objectId) {
            return obstacles.get(course).get(objectId);
        }

        /**
         * @return the agilityObstacles
         */
        public AgilityObstacle[] getAgilityObstacles() {
            return agilityObstacles;
        }
    }

    public void startObstacle(AgilityObstacle obstacle) {

    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.AGILITY;
    }

    @Override
    public void fireResetEvent(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.AGILITY;
    }

    public static class AgilityObstacle {

        private int level;

        private int experience;

        private int object;

        private GenericAction<Player> action;

        AgilityObstacle(int object, GenericAction<Player> action) {
            this.object = object;
            this.action = action;
        }

        /**
         * @return the object
         */
        public int getObject() {
            return object;
        }

        /**
         * @return the action
         */
        public GenericAction<Player> getAction() {
            return action;
        }
    }
}
