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

package top.cmarco.systeminfo.commands.htop;

import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;
import org.bukkit.command.CommandSender;
import oshi.software.os.OSProcess;

import java.util.Collections;
import java.util.List;

/**
 * The `CommandHtop` class is a Spigot command that allows players with the appropriate permission to view a list of
 * processes running on the system using the "/htop" command.
 */
public final class CommandHtop extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandHtop` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandHtop(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "htop",
                "shows a list of the processes running on the system",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/htop" command, displaying a list of processes running on the system to the sender.
     *
     * @param sender The command sender.
     * @param name   The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(CommandSender sender, @NotNull String name, String[] args) {
        if (sender.isOp()) {
            if (args.length == 0) {
                printHtop(sender);
                return true;
            } else {
                sender.sendMessage(Messages.OUT_OF_ARGS.value(true));
            }
        } else {
            sender.sendMessage(Messages.NO_PERMISSIONS.value(true));
        }
        return false;
    }

    private static double getOsProcPercentage(@NotNull OSProcess osProcess) {
        return 100d * (osProcess.getKernelTime() + osProcess.getUserTime()) / osProcess.getUpTime();
    }

    /**
     * Displays a list of processes running on the system to the sender.
     *
     * @param sender The command sender.
     */
    private void printHtop(CommandSender sender) {
        sender.sendMessage(Utils.color("&2« &7Htop &2»"));
        sender.sendMessage(Utils.color("&7Processes: &a" + systemInfo.getSystemValues().getRunningProcesses() +
                " &7Threads: &a" + systemInfo.getSystemValues().getThreadCount()));
        sender.sendMessage(Utils.color("&7    PID  %CPU %MEM     VSZ            NAME"));
        List<OSProcess> processes = systemInfo.getSystemValues().getOSProcesses();
        processes.sort((proc1, proc2) -> (int) (getOsProcPercentage(proc2) - getOsProcPercentage(proc1)));

        for (int i = 0; i < processes.size() && i < 8; i++) {
            OSProcess osProcess = processes.get(i);
            sender.sendMessage(Utils.color(String.format(" &8%5d &7%5.1f %s %9s %9s &a%s",
                    osProcess.getProcessID(),
                    100d * (osProcess.getKernelTime() + osProcess.getUserTime()) / osProcess.getUpTime(),
                    Utils.formatData(100 * osProcess.getResidentSetSize() / systemInfo.getSystemValues().getMaxMemory2()),
                    Utils.formatData(osProcess.getVirtualSize()),
                    Utils.formatData(osProcess.getResidentSetSize()),
                    osProcess.getName())));
        }
    }
}
