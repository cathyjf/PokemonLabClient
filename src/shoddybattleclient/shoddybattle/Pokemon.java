/* PokemonMoves.java
 *
 * Created April 7, 2009
 *
 * This file is a part of Shoddy Battle.
 * Copyright (C) 2009  Catherine Fitzpatrick and Benjamin Gwin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, visit the Free Software Foundation, Inc.
 * online at http://gnu.org.
 */

package shoddybattleclient.shoddybattle;

/**
 *
 * @author ben
 */
public class Pokemon {

    public static enum Gender {
        GENDER_MALE,
        GENDER_FEMALE,
        GENDER_NONE,
        GENDER_BOTH
    }

    public static final int MOVE_COUNT = 4;
    public static final int STAT_COUNT = 6;

    public int speciesId;
    public String nickname;
    public boolean shiny;
    public Gender gender;
    public int level;
    public int item;
    public int ability;
    public int nature;
    public int[] moves = new int[MOVE_COUNT];
    public int[] ppUps = new int[MOVE_COUNT];
    public int[] ivs = new int[STAT_COUNT];
    public int[] evs = new int[STAT_COUNT];

    public Pokemon(int speciesId, String nickname, boolean shiny, Gender gender,
            int level, int item, int ability, int nature, int[] moves, int[] ppUps,
            int[] ivs, int[] evs) {

        this.speciesId = speciesId;
        this.nickname = nickname;
        this.shiny = shiny;
        this.gender = gender;
        this.level = level;
        this.item = item;
        this.ability = ability;
        this.nature = nature;
        this.moves = moves;
        this.ppUps = ppUps;
        this.ivs = ivs;
        this.evs = evs;
    }
}
