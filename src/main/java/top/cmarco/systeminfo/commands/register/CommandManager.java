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

package top.cmarco.systeminfo.commands.register;

import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.plugin.SystemInfo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages and registers system information commands in your Spigot plugin.
 * <p>
 * This class is responsible for creating instances of system information commands based on the provided
 * `CommandType` enum, and registering those commands with the Spigot command map for use within the plugin.
 *
 * @version 1.0
 * @since Date or Version
 */
public final class CommandManager {
    private final SystemInfo systemInfo;

    private final List<Command> commands = new ArrayList<>();

    /**
     * Constructs a new `CommandManager` with the given `CommandMap` and `SystemInfo`.
     *
     * @param systemInfo The `SystemInfo` instance for accessing system information.
     */
    public CommandManager(@NotNull SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    /**
     * Creates instances of system information commands based on the `CommandType` enum
     * and adds them to the list of registered commands.
     */
    public void createInstances() {
        for (CommandType value : CommandType.values()) {
            Class<? extends SystemInfoCommand> clazz = value.getClazz();
            try {
                final Constructor<? extends SystemInfoCommand> constructor = clazz.getDeclaredConstructor(SystemInfo.class);
                final SystemInfoCommand command = constructor.newInstance(this.systemInfo);
                commands.add(command);
            } catch (NoSuchMethodException | InvocationTargetException | RuntimeException | InstantiationException | IllegalAccessException exception) {
                systemInfo.getLogger().warning("Something went wrong while getting a constructor for " + value.getDisplayName());
                systemInfo.getLogger().warning(exception.getLocalizedMessage());
            }
        }
    }

    /**
     * Registers all created system information commands with the Spigot command map.
     */
    public void registerAll() {
        systemInfo.getServer().getCommandMap().registerAll("systeminfo", commands);
    }

}
