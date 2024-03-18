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

package top.cmarco.systeminfo.commands.neofetch;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.enums.Messages;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * A class that prints a server logo with basic information about hardware.
 * <br>
 * Inspired by the Linux neofetch command
 * <br>
 * <a href="https://github.com/dylanaraps/neofetch">GitHub - neofetch</a>
 */
public final class CommandNeofetch extends SystemInfoCommand {

    /**
     * Constructs a new `SystemInfoCommand` with the provided information.
     *
     * @param systemInfo   The `SystemInfo` instance to use for system information.
     */
    public CommandNeofetch(@NotNull SystemInfo systemInfo) {
        super(systemInfo, "neofetch", "prints the server logo and general information", "/<command>", Collections.emptyList());
    }

    private static Iterator<String> getNeofetchHardwareInfo(ServerLogo impl) {
        SystemInfo instance = SystemInfo.INSTANCE;
        SystemValues values = instance.getSystemValues();
        RuntimeMXBean javaMx = ManagementFactory.getRuntimeMXBean();
        Deque<String> strings = new ArrayDeque<>();
        strings.offerLast("&aOS&f: " + values.getOSFamily() + " " + values.getOSVersion());
        strings.offerLast("&aKernel&f: " + values.getKernelVersion());
        strings.offerLast("&aUptime&f: " + (1 + ChronoUnit.MINUTES.between(instance.getStartupTime(), LocalDateTime.now())) + "m");
        strings.offerLast("&aCPU&f: " + values.getCpuModelName() + " (" + values.getCpuCores() + ") @ " + values.getCpuMaxFrequency() + "GHz");
        strings.offerLast("&aGPU&f: " + values.getMainGPU().getVendor() + " " + values.getMainGPU().getName());
        strings.offerLast("&aMemory&f: " + values.getUsedMemory() + " / " + values.getMaxMemory());
        strings.offerLast("&aJava&f: " + javaMx.getVmVendor() + " " + javaMx.getVmVersion());
        strings.offerLast("&aMinecraft Version&f: " + Bukkit.getServer().getGameVersion());
        strings.offerLast("&aServer Implementation&f: " + impl.name().toLowerCase());
        return strings.iterator();
    }

    private static void printNeofetch(@NotNull CommandSender sender) {
        String version = Bukkit.getVersion().toLowerCase(Locale.ROOT);
        ServerLogo serverLogo = ServerLogo.SPIGOT;
        if (version.contains("bukkit")) {
            serverLogo = ServerLogo.BUKKIT;
        } else if (version.contains("spigot")) {
            serverLogo = ServerLogo.SPIGOT;
        } else if (version.contains("paper")) {
            serverLogo = ServerLogo.PAPERMC;
        } else if (version.contains("folia")) {
            serverLogo = ServerLogo.FOLIA;
        } else if (version.contains("purpur")) {
            serverLogo = ServerLogo.PURPUR;
        } else if (version.contains("pufferfish")) {
            serverLogo = ServerLogo.PUFFERFISH;
        } else if (version.contains("arclight")) {
            serverLogo = ServerLogo.ARCLIGHT;
        }

        String[] logoLines = serverLogo.getLogoLines();
        int midline = Math.floorDiv(logoLines.length, 2);
        int begin = midline - 4, end = midline + 4;
        Iterator<String> infoStrings = getNeofetchHardwareInfo(serverLogo);
        for (int l = 0; l < logoLines.length; l++) {
            StringBuilder stringBuilder = new StringBuilder(logoLines[l]);
            if (l >= begin && l <= end && infoStrings.hasNext()) {
                String append = infoStrings.next();
                stringBuilder.append("  ").append(append);
            }
            sender.sendMessage(Utils.color(stringBuilder.toString()));
        }
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (sender.isOp()) {

            if (sender instanceof HumanEntity) {
                sender.sendMessage(Messages.CONSOLE_ONLY.value(true));
                return false;
            }

            if (args.length == 0) {
                printNeofetch(sender);
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
