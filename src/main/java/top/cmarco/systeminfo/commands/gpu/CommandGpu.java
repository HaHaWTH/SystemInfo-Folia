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

package top.cmarco.systeminfo.commands.gpu;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import oshi.hardware.GraphicsCard;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;
import java.util.List;

/**
 * This class represents a Spigot command to
 * get information about the available GPU.
 */
public class CommandGpu extends SystemInfoCommand {

    /**
     * Constructs a new `SystemInfoCommand` with the provided information.
     * @param systemInfo   The `SystemInfo` instance to use for system information.
    */
    public CommandGpu(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "gpu", "get information about the system GPUs", "/<command>", Collections.singletonList("nvidia-smi"));
    }

    /**
     * Method to print all information about loaded GPUs.
     * Supports at the same time console and player chat.
     * @param sender The sender of the command.
     */
    private void printGPUs(@NotNull CommandSender sender) {
        SystemValues systemValues = super.systemInfo.getSystemValues();

        sender.sendMessage(Utils.color("&2«« &7GPUs info &2»»"));

        List<GraphicsCard> gpus = systemValues.getGPUs();
        for (int i = 0; i < gpus.size(); i++) {
            final GraphicsCard gpu = gpus.get(i);
            sender.sendMessage(Utils.color("&aGPU &l" + (i+1) + "&r&7:"));
            sender.sendMessage(Utils.color(" &a| &7GPU Vendor: &a" + gpu.getVendor()));
            sender.sendMessage(Utils.color(" &a| &7GPU Name: &a" + gpu.getName()));
            sender.sendMessage(Utils.color(" &a| &7GPU Version: &a" + gpu.getVersionInfo()));
            sender.sendMessage(Utils.color(" &a| &7GPU VRAM: &a" + Utils.formatDataBits(gpu.getVRam())));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.isOp()) {
            if (args.length == 0) {
                printGPUs(sender);
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
