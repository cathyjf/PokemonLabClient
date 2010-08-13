/* PokemonMove.java
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

import java.util.List;

/**
 *
 * @author ben
 */
public class PokemonMove {
    public String name;
    public String type;
    public int id;
    public String damageClass;
    public int power;
    public int accuracy;
    public int pp;
    public int maxPp;
    public String target;

    public static String getNameFromId(List<PokemonMove> moves, int id) {
        for (PokemonMove i : moves) {
            if (id == i.id) {
                return i.name;
            }
        }
        return null;
    }

    public static int getIdFromName(List<PokemonMove> moves, String name) {
        for (PokemonMove i : moves) {
            if (i.name.equalsIgnoreCase(name)) {
                return i.id;
            }
        }
        return -1;
    }

    public static int calculateHiddenPowerPower(int[] ivs) {
        int pow = 0;
        for (int i = 0; i < Pokemon.STAT_COUNT; i++) {
            int r = ivs[i] % 4;
            if ((r == 2) || (r == 3)) {
                pow += 1 << i;
            }
        }
        return pow * 40 / 63 + 30;
    }

    public static String getHiddenPowerType(int[] ivs) {
        int idx = 0;
        for (int i = 0; i < Pokemon.STAT_COUNT; i++) {
            if ((ivs[i] % 2) != 0) {
                idx += 1 << i;
            }
        }
        idx = idx * 15 / 63;
        return new String[] {"Fighting", "Flying", "Poison", "Ground", "Rock",
            "Bug", "Ghost", "Steel", "Fire", "Water", "Grass", "Electric", "Psychic",
            "Ice", "Dragon", "Dark"}[idx];
    }
}
