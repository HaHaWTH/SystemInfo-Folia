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

package top.cmarco.systeminfo.commands.devices;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import oshi.hardware.UsbDevice;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.Collections;
/**
 * The `CommandDevices` class is a Spigot command that allows players with the appropriate permission to retrieve a list
 * of system devices using the "/devices" command.
 */
public final class CommandDevices extends SystemInfoCommand {

    /**
     * Initializes a new instance of the `CommandDevices` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this command.
     */
    public CommandDevices(@NotNull SystemInfo systemInfo) {
        super(systemInfo,"devices",
                "get a list of system devices",
                "/<command>",
                Collections.emptyList());
    }

    /**
     * Executes the "/devices" command, displaying a list of system devices to the sender.
     *
     * @param sender The command sender.
     * @param name   The command name.
     * @param args   The command arguments (not used in this command).
     * @return True if the command was executed successfully; otherwise, false.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public boolean execute(CommandSender sender, String name, String[] args) {
        if (sender.hasPermission("systeminfo.commands.devices")) {
            if (args.length == 0) {
                printDevices(sender);
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
     * Displays a list of system devices to the sender.
     *
     * @param sender The command sender.
     */
    private void printDevices(CommandSender sender) {
        sender.sendMessage(Utils.color("&2» &7Attached devices &2«"));
        sender.sendMessage(Utils.color("&2» &7List:"));
        for (UsbDevice usb : systemInfo.getSystemValues().getUsbDevices()) {
            sender.sendMessage(Utils.color("&7- &a" + usb.getVendor() + " " + usb.getSerialNumber()));

            if (sender instanceof Player) {
                Player player = (Player) sender;
                player.spigot().sendMessage(Utils.builderHover(" &7Serial-ID &8[&a*&8]&r", usb.getSerialNumber()));
            } else {
                sender.sendMessage(Utils.color(String.format(" &7Serial-ID &8[&a%s&8]&r", usb.getSerialNumber())));
            }

            usb.getConnectedDevices();
            for (final UsbDevice subUsb : usb.getConnectedDevices()) {
                sender.sendMessage(Utils.color((" &7|- &a" + subUsb.getVendor() + " " + subUsb.getName())));
            }
        }
    }
}