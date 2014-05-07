package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.world.entity.Entity;
import server.world.entity.FlyingProjectile;
import server.world.entity.Projectile;

public class CombatProjectileTask extends Worker {

    private Entity entity;
    private Projectile startProjectile;
    private FlyingProjectile flyingProjectile;
    private Projectile endProjectile;

    public CombatProjectileTask(Entity entity, int delay, Projectile startProjectile, FlyingProjectile flyingProjectile, Projectile endProjectile) {
        super(delay, startProjectile != null);
    }

    @Override
    public void fire() {
        if (this.isInitialRun()) {
            // fire starting projectile
            return;
        }

        // fire flying projectile
        // fire ending projectile
    }
}
