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

package top.cmarco.systeminfo.commands.java;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.Collections;

public final class CommandJava extends SystemInfoCommand {
    /**
     * Constructs a new `SystemInfoCommand` with the provided information.
     *
     * @param systemInfo   The `SystemInfo` instance to use for system information.
     */
    public CommandJava(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "java",
                "shows information about your Java",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Print the information about the current Java environment to the CommandSender.
     * @param sender The CommandSender.
     */
    public static void printJava(@NotNull CommandSender sender) {
        final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        sender.sendMessage(Utils.color("&2«« &7Java Information&2»»"));
        sender.sendMessage(Utils.color("&7Version: &a" + runtimeMXBean.getSpecVersion()));
        sender.sendMessage(Utils.color("&7Vendor: &a" + runtimeMXBean.getVmVendor()));
        sender.sendMessage(Utils.color("&7Args: &a" + String.join(" ", runtimeMXBean.getInputArguments())));
        try {
            final Method getPidMethod = runtimeMXBean.getClass().getMethod("getPid");
            final Object methodReturnVal = getPidMethod.invoke(runtimeMXBean);
            sender.sendMessage(Utils.color("&7Process ID: &a" + methodReturnVal));
        } catch (Exception ignored) {}
    }

    /**
     * Executes the "/java" command, displaying a list of processes running on the system to the sender.
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
                printJava(sender);
                return true;
            } else {
                sender.sendMessage(Messages.OUT_OF_ARGS.value(true));
            }
        } else {
            sender.sendMessage(Messages.NO_PERMISSIONS.value(true));
        }
        return false;
    }
}
