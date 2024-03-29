package top.cmarco.systeminfo.commands.network;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.protocol.BukkitNetworkingManager;
import top.cmarco.systeminfo.utils.Utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Method;
import java.util.Collections;

public class CommandNetwork extends SystemInfoCommand {
    /**
     * Constructs a new `SystemInfoCommand` with the provided information.
     *
     * @param systemInfo   The `SystemInfo` instance to use for system information.
     */
    public CommandNetwork(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "network",
                "shows information about your Network",
                "/<command>",
                Collections.emptyList());
    }

    public static void printNetwork(@NotNull CommandSender sender) {
        sender.sendMessage(Utils.color("&2«« &7Network Info &2»»"));
        sender.sendMessage(Utils.color("&7Packet received: &a" + SystemInfo.networkingManager.getTotalReceivedPackets()));
        sender.sendMessage(Utils.color("&7Packet sent: &a" + SystemInfo.networkingManager.getTotalSentPackets()));
        sender.sendMessage(Utils.color("&7Bytes received: &a" + Utils.formatData(SystemInfo.networkingManager.getTotalReceivedBytes())));
        sender.sendMessage(Utils.color("&7Bytes sent: &a" + Utils.formatData(SystemInfo.networkingManager.getTotalSentBytes())));
    }


    @Override
    public boolean execute(CommandSender sender, @NotNull String name, String[] args) {
        if (sender.hasPermission("systeminfo.commands.network")) {
            if (args.length == 0) {
                printNetwork(sender);
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
