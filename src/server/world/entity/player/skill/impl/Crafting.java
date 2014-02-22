package server.world.entity.player.skill.impl;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;

public class Crafting extends SkillEvent {

    @Override
    public int eventFireIndex() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void fireResetEvent(Player player) {
        // TODO Auto-generated method stub

    }

    @Override
    public SkillConstant skillConstant() {
        // TODO Auto-generated method stub
        return null;
    }

}
