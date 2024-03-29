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

package top.cmarco.systeminfo.commands.disks;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import oshi.hardware.HWDiskStore;
import oshi.hardware.HWPartition;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;
/**
 * The `CommandDisks` class is a Spigot command that allows players with the appropriate permission to retrieve a list
 * of system disks and their details using the "/disks" command.
 */
public final class CommandDisks extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandDisks` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandDisks(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "disks",
                "get your system disks list",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/disks" command, displaying a list of system disks and their details to the sender.
     *
     * @param sender The command sender.
     * @param name   The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @Override
    public boolean execute(CommandSender sender, @NotNull String name, String[] args) {
        if (sender.hasPermission("systeminfo.commands.disks")) {
            if (args.length == 0) {
                try {
                    printDisks(sender);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage("&c» Could not obtain info from this system.");
                }
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
     * Displays a list of system disks and their details to the sender.
     *
     * @param sender The command sender.
     */
    private void printDisks(CommandSender sender) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            for (HWDiskStore disk : systemInfo.getSystemValues().getDiskStores()) {
                player.spigot().sendMessage(Utils.builderHover("&7[" + disk.getName() + " " + disk.getModel(),
                        "&7Serial: &a" + disk.getSerial()
                                + "\n&7Disk Read: &a" + Utils.formatData(disk.getReadBytes())
                                + "\n&7Disk Written: &a" + Utils.formatData(disk.getWriteBytes())));
                for (HWPartition part : disk.getPartitions()) {
                    player.spigot().sendMessage(Utils.builderHover("  &7|-- &a" + part.getIdentification() + " " + part.getType() + " &7Size: &a" + Utils.formatData(part.getSize()),
                            "&7Mount Point: &a" + part.getMountPoint() + " &7Uuid: &a" + part.getUuid()));
                }
            }
        } else {
            for (HWDiskStore disk : systemInfo.getSystemValues().getDiskStores()) {
                final String diskStr = "&7[" + disk.getName() + " " + disk.getModel() +
                        "&7Serial: &a" + disk.getSerial()
                        + "\n&7Disk Read: &a" + Utils.formatData(disk.getReadBytes())
                        + "\n&7Disk Written: &a" + Utils.formatData(disk.getWriteBytes());

                sender.sendMessage(Utils.color(diskStr));

                for (HWPartition part : disk.getPartitions()) {
                    final String diskPart = "  &7|-- &a" + part.getIdentification() + " " + part.getType() + " &7Size: &a" + Utils.formatData(part.getSize()) +
                    "&7Mount Point: &a" + part.getMountPoint() + " &7Uuid: &a" + part.getUuid();

                    sender.sendMessage(Utils.color(diskStr));
                }
            }
        }
    }
}
