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

    public static int getIdFromName(List<PokemonMove> moves, String name) {
        for (PokemonMove i : moves) {
            if (i.name.equals(name)) {
                return i.id;
            }
        }
        return -1;
    }

    public PokemonMove() {

    }
}
