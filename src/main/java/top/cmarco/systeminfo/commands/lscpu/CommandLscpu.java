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

package top.cmarco.systeminfo.commands.lscpu;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;


/**
 * The `CommandLscpu` class is a Spigot command that allows players with the appropriate permission to retrieve
 * information about the system processor(s) using the "/lscpu" command.
 */
public final class CommandLscpu extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandLscpu` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandLscpu(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "lscpu",
                "get information about the system processor(s)",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/lscpu" command, providing information about the system processor(s) to the sender.
     *
     * @param sender The command sender.
     * @param name   The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(CommandSender sender, String name, String[] args) {
        if (sender.hasPermission("systeminfo.commands.lscpu")) {
            if (args.length == 0) {
                printLscpu(sender);
                return true;
            } else {
                sender.sendMessage(Messages.OUT_OF_ARGS.value(true));
            }
        } else {
            sender.sendMessage(Messages.NO_PERMISSIONS.value(true));
        }
        return false;
    }

    /**
     * Prints information about the system processor(s) to the sender.
     *
     * @param sender The command sender.
     */
    private void printLscpu(CommandSender sender) {
        SystemValues values = systemInfo.getSystemValues();
        sender.sendMessage(Utils.color("&2«« &7Cpu info &2»»"));
        sender.sendMessage(Utils.color("&7Operating System: &a" + values.getOSFamily() + " " + values.getOSManufacturer() + " " + values.getOSVersion()));
        sender.sendMessage(Utils.color("&7Cpu Vendor: &a" + values.getCpuVendor()));
        sender.sendMessage(Utils.color("&7Cpu Model: &a" + values.getCpuModel() + " " + values.getCpuModelName()));
        sender.sendMessage(Utils.color("&7Cpu Clock Rate: &a" + values.getCpuMaxFrequency()));
        sender.sendMessage(Utils.color("&7Cpu Stepping: &a" + values.getCpuStepping()));
        sender.sendMessage(Utils.color("&7Physical Cores: &a" + values.getCpuCores()));
        sender.sendMessage(Utils.color("&7Logical Cores: &a" + values.getCpuThreads()));
    }
}
