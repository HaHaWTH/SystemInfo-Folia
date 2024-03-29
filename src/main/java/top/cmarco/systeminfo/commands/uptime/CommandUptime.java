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

package top.cmarco.systeminfo.commands.uptime;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

/**
 * The `CommandUptime` class is a Spigot command that allows players with the appropriate permission to retrieve
 * information about the JVM uptime using the "/uptime" command.
 */
public final class CommandUptime extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandUptime` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandUptime(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "uptime",
                "get the JVM uptime",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/uptime" command, providing information about JVM uptime to the sender.
     *
     * @param sender The command sender.
     * @param name   The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String name, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission("systeminfo.commands.uptime")) {
                uptime(sender);
                return true;
            } else {
                sender.sendMessage(Messages.NO_PERMISSIONS.value(true));
            }
        } else {
            sender.sendMessage(Messages.OUT_OF_ARGS.value(true));
        }
        return false;
    }

    /**
     * Sends information about JVM uptime to the specified command sender.
     *
     * @param sender The command sender.
     */
    private void uptime(CommandSender sender) {
        final long uptime = ChronoUnit.MINUTES.between(systemInfo.getStartupTime(), LocalDateTime.now());
        sender.sendMessage(Utils.color("&2»» &7Machine uptime &2««"));
        sender.sendMessage(Utils.color("&2» &7JVM Uptime: &a" + uptime + " min."));
    }
}

