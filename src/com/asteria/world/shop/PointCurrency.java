package com.asteria.world.shop;

import com.asteria.world.entity.player.Player;

/**
 * A {@link GenericCurrency} implementation that provides further functionality
 * for any currency that can be represented by points.
 * 
 * @author lare96
 */
public abstract class PointCurrency implements GenericCurrency {

    @Override
    public boolean inventoryFull(Player player) {
        return true;
    }
}
