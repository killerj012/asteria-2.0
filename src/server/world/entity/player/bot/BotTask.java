package server.world.entity.player.bot;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.map.Position;

/**
 * An enumeration that holds all of the tasks that can be assigned to a bot.
 * 
 * @author lare96
 */
public enum BotTask {

    /** The bot will just walk around the home area. */
    WALK_AROUND() {
        @Override
        public void fireTask(final Bot bot) {
            bot.getPlayer().forceChat("I'm going to walk around a bit.");
            final Position startPosition = bot.getPlayer().getPosition().clone();

            bot.setBotWorker(new Worker(3, false, WorkRate.APPROXIMATE_SECOND) {

                /** If this bot should navigate back to its starting position. */
                private boolean navigateBack;

                @Override
                public void fire() {
                    if (navigateBack) {

                        /** Walk back to the starting position. */
                        bot.getPlayer().getMovementQueue().walk(startPosition);
                        navigateBack = false;
                    } else if (!navigateBack) {
                        int x = 0, y = 0;

                        /** Determine the direction. */
                        switch (Misc.getRandom().nextInt(2)) {
                            case 0:
                                x = Misc.getRandom().nextInt(2);
                                y = Misc.getRandom().nextInt(2);

                                break;
                            case 1:
                                x = -Misc.getRandom().nextInt(2);
                                y = -Misc.getRandom().nextInt(2);
                                break;
                        }

                        /** Walk in that direction. */
                        bot.getPlayer().getMovementQueue().walk(x, y);
                        navigateBack = true;
                    }
                }
            }.attach(bot.getPlayer()));

            TaskFactory.getFactory().submit(bot.getBotWorker());
        }

        @Override
        public void stopTask(Bot bot) {
            System.out.println("??");
            bot.getPlayer().forceChat("I don't feel like walking anymore.");
            bot.getPlayer().getMovementQueue().reset();
            bot.getBotWorker().cancel();
        }
    };

    /**
     * Fired when the task is assigned.
     * 
     * @param bot
     *        the bot that this task is being fired for.
     */
    public abstract void fireTask(Bot bot);

    /**
     * Fired when the task is stopped.
     * 
     * @param bot
     *        the bot that this task is being stopped for.
     */
    public abstract void stopTask(Bot bot);
}
