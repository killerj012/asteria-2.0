package server.world.entity.player.skill.impl;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;

public class Herblore extends SkillEvent {

    public enum Herb {
        GUAM,
        MARRENTILL,
        TARROMIN,
        HARRALANDER,
        RANARR,
        IRIT,
        AVANTOE,
        KWUARM,
        CADANTINE,
        DWARF_WEEF,
        TORSTOL,
        LANTADYME,
        TOADFLAX,
        SNAPDRAGON,
        SPIRIT_WEED,
        WERGALI

        // Grimy herbs:
        //
        // 199 - Grimy guam
        // 201 - Grimy marrentill
        // 203 - Grimy tarromin
        // 205 - Grimy harralander
        // 207 - Grimy ranarr
        // 209 - Grimy irit
        // 211 - Grimy avantoe
        // 213 - Grimy kwuarm
        // 215 - Grimy cadantine
        // 217 - Grimy dwarf weed
        // 219 - Grimy torstol
        // 2485 - Grimy lantadyme
        // 3049 - Grimy toadflax
        // 3051 - Grimy snapdragon
        // 12174 - Grimy spirit weed
        // 14836 - Grimy wergali
        //
        // Clean herbs:
        //
        // 249 - Clean guam
        // 251 - Clean marrentill
        // 253 - Clean tarromin
        // 255 - Clean harralander
        // 257 - Clean ranarr
        // 259 - Clean irit
        // 261 - Clean avantoe
        // 263 - Clean kwuarm
        // 265 - Clean cadantine
        // 267 - Clean dwarf weed
        // 269 - Clean torstol
        // 2481 - Clean lantadyme
        // 2998 - Clean toadflax
        // 3000 - Clean snapdragon
        // 12172 - Clean spirit weed
        // 14854 - Clean wergali
    }

    public enum Potion {

    }

    @Override
    public void fireResetEvent(Player player) {

    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.HERBLORE;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.HERBLORE;
    }
}
