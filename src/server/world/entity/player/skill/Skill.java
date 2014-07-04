package server.world.entity.player.skill;

import server.world.entity.player.Player;

/**
 * A single skill that can be trained by a {@link Player}.
 * 
 * @author lare96
 */
public class Skill {

    /** The level of this skill. */
    private int level;

    /** The experience for this skill. */
    private int experience;

    /** The real level for this skill. */
    // XXX: The real level is now cached so we only have to do calculations when
    // absolutely needed!
    private int realLevel;

    /**
     * Create a new {@link Skill}.
     */
    public Skill() {
        this.realLevel = 1;
        this.level = 1;
        this.experience = 0;
    }

    /**
     * Gets a level based on how much experience you have.
     * 
     * @return the level based on how much experience you have.
     */
    public int getLevelForExperience() {
        return realLevel;
    }

    /**
     * Calculates the level based on how much experience you have.
     * 
     * @return the level based on how much experience you have.
     */
    public int calculateLevelForExperience() {
        int points = 0;
        int output = 0;

        if (realLevel >= 99) {
            return 99;
        }

        for (int lvl = 1; lvl <= 99; lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            output = (int) Math.floor(points / 4);
            if (output >= this.getExperience()) {
                realLevel = lvl;
                return lvl;
            }
        }
        realLevel = 99;
        return 99;
    }

    /**
     * Gets the amount of experience needed for the next level.
     * 
     * @return the amount of experience needed for the next level.
     */
    public int getExperienceForNextLevel() {
        int points = 0;
        int output = 0;

        for (int lvl = 1; lvl <= (this.getLevel() + 1); lvl++) {
            points += Math.floor(lvl + 300.0 * Math.pow(2.0, lvl / 7.0));
            if (lvl >= (this.getLevelForExperience() + 1))
                return output;
            output = (int) Math.floor(points / 4);
        }

        return 0;
    }

    /**
     * Gets if your level is equal to or above the number provided.
     * 
     * @param level
     *            the level to compare to yours.
     * @return true if your level is equal to or above the level provided.
     */
    public boolean reqLevel(int level) {
        return this.getLevel() >= level ? true : false;
    }

    /**
     * Increments this level by the speicified amount.
     * 
     * @param amount
     *            the amount to increase this level by.
     */
    public void increaseLevel(int amount) {
        if ((level + amount) > 120) {
            level = 120;
            return;
        }

        level += amount;
    }

    /**
     * Increments this level by the speicified amount to the maximum amount.
     * 
     * @param amount
     *            the amount to increase this level by.
     * @param maximum
     *            the maximum level to increase this to.
     */
    public void increaseLevel(int amount, int maximum) {
        if ((level + amount) > maximum) {
            level = maximum;
            return;
        }

        level += amount;
    }

    /**
     * Decrements this level by the speicified amount.
     * 
     * @param amount
     *            the amount to decrease this level by.
     */
    public void decreaseLevel(int amount) {
        if ((level - amount) < 1) {
            level = 0;
            return;
        }

        level -= amount;
    }

    /**
     * Gets the experience for this skill.
     * 
     * @return the experience.
     */
    public int getExperience() {
        return experience;
    }

    /**
     * Gets the level for this skill.
     * 
     * @return the level.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Set a new level for this skill.
     * 
     * @param level
     *            the new level to set.
     */
    public void setLevel(int level) {
        this.level = level;

        if (this.level < 0) {
            this.level = 0;
        } else if (this.level > 120) {
            this.level = 120;
        }
    }

    /**
     * Set a new real level for this skill.
     * 
     * @param realLevel
     *            the new level to set.
     */
    protected void setRealLevel(int realLevel) {
        this.realLevel = realLevel;
    }

    /**
     * Set a new experience amount for this skill.
     * 
     * @param experience
     *            the new amount of experience to set.
     */
    public void setExperience(int experience) {
        this.experience = experience;
    }
}
