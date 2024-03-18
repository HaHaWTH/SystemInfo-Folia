/*
 *     SystemInfo - The Master of Server Hardware
 *     Copyright © 2024 CMarco
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package top.cmarco.systeminfo.utils;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import top.cmarco.systeminfo.plugin.SystemInfo;

public class Utils {

    private static Base64.Decoder decoder = null;

    public static String normalize(final String encodedText) {
        if (decoder == null) {
            decoder = Base64.getDecoder();
        }

        final byte[] bytes =  decoder.decode(encodedText);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static Character cachedColor = null;
    public static Character cachedDarkColor = null;
    public static final Map<Character, Character> LIGHT_DARK_CHARACTER_MAP = new HashMap<>();


    static {
        LIGHT_DARK_CHARACTER_MAP.put('a', '2');
        LIGHT_DARK_CHARACTER_MAP.put('b', '3');
        LIGHT_DARK_CHARACTER_MAP.put('c', '4');
        LIGHT_DARK_CHARACTER_MAP.put('d', '5');
        LIGHT_DARK_CHARACTER_MAP.put('e', '6');
        LIGHT_DARK_CHARACTER_MAP.put('f', '7');
        LIGHT_DARK_CHARACTER_MAP.put('7', '8');
        LIGHT_DARK_CHARACTER_MAP.put('9', '1');
    }

    /**
     * This method takes a String as input and replaces & with color codes.
     *
     * @param input a non-null String
     * @return returns the string with colors which can be displayed inside Minecraft chat/console
     */
    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String formatData(long bytes) {
        final String[] units = {"B", "kB", "MB", "GB", "TB", "PB", "EB"};

        if (bytes == Long.MIN_VALUE) {
            bytes = Long.MAX_VALUE;
        }

        int unitIndex = 0;
        double size = Math.abs(bytes);

        while (size >= 1000 && unitIndex < units.length - 1) {
            size /= 1000;
            unitIndex++;
        }

        return String.format("%s%.1f %s", bytes < 0 ? "-" : "", size, units[unitIndex]);
    }


    /**
     * The amounts of bits in a byte. Used for conversions.
     */
    public final static int BITS_IN_BYTE = 0x08;

    /**
     * Display the appropriate data format for the amount of bytes given.
     * @param bytes The total bytes.
     * @return formatted output.
     */
    public static String formatDataBits(final long bytes) {
        final long bits = Math.abs(BITS_IN_BYTE * bytes);

        if (bits / 10E3 <= 99) {
            return String.format("%.1f KiB", bits / 10E2);
        } else if (bits / 10E6 <= 99) {
            return String.format("%.1f MiB", bits / 10E5);
        } else if (bits / 10E9 <= 99) {
            return String.format("%.2f GiB", bits / 10E8);
        } else {
            return String.format("%.3f TiB", bits / 10E11);
        }
    }

    /**
     * This method counts the entities in every world with a given environment
     * if more worlds are found, multiple values will be returned in the same string
     * separated by a blank space.
     *
     * @param environment This is the environment target {@link org.bukkit.World.Environment}
     * @return returns a String with the number of total entities in every targeted world.
     * example: "255 327 455 " will be the output if three worlds with same environment are found.
     */
    public static String countEntitiesInWorlds(World.Environment environment) {
        StringBuilder entitiesInWorlds = new StringBuilder();
        Bukkit.getWorlds().stream().filter(world -> world.getEnvironment() == environment).forEach(world -> entitiesInWorlds.append(world.getEntities().size()).append(" "));
        if (entitiesInWorlds.length() == 0) {
            return Utils.color("&cUnloaded");
        }
        return entitiesInWorlds.toString();
    }

    /**
     * This method counts the loaded chunks in every world with a given environment
     * if more worlds are found, multiple values will be returned in the same string
     * separated by a blank space.
     *
     * @param environment This is the environment target {@link org.bukkit.World.Environment}
     * @return returns a String with the number of loaded chunks in every targeted world.
     * example: "36 122 " will be the output if two worlds with same environment are found.
     */
    public static String loadedChunksInWorlds(World.Environment environment) {
        StringBuilder loadedChunksInWorlds = new StringBuilder();
        Bukkit.getWorlds().stream()
                .filter(world -> world.getEnvironment() == environment)
                .forEach(world -> loadedChunksInWorlds.append(world.getLoadedChunks().length).append(" "));
        if (loadedChunksInWorlds.length() == 0) {
            return Utils.color("&cUnloaded");
        }
        return loadedChunksInWorlds.toString();
    }

}
