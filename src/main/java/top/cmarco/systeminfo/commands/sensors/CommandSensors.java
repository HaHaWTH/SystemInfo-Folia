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

package top.cmarco.systeminfo.commands.sensors;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;


/**
 * The `CommandSensors` class is a Spigot command that allows players with the appropriate permission to retrieve
 * information from system sensors using the "/sensors" command.
 */
public final class CommandSensors extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandSensors` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandSensors(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "sensors",
                "get information from the system sensors",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/sensors" command, providing information from system sensors to the sender.
     *
     * @param sender The command sender.
     * @param s      The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.hasPermission("systeminfo.commands.sensors")) {
            if (args.length == 0) {
                sender.sendMessage(Utils.color("&7Fans RPM: &a" + systemInfo.getSystemValues().getFansRPM()));
                sender.sendMessage(Utils.color("&7Cpu Voltage: &a" + systemInfo.getSystemValues().getCpuVoltage()));
                sender.sendMessage(Utils.color("&7Cpu Temperature: " + systemInfo.getSystemValues().getCpuTemperatureStatus()));
                return true;
            }
        } else {
            sender.sendMessage(Messages.NO_PERMISSIONS.value(true));
        }
        return false;
    }
}