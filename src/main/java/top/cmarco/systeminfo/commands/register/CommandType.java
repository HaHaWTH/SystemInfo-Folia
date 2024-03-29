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

package top.cmarco.systeminfo.commands.register;

import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.commands.SystemInfoCommand;
import top.cmarco.systeminfo.commands.cpuload.CommandCpuLoad;
import top.cmarco.systeminfo.commands.devices.CommandDevices;
import top.cmarco.systeminfo.commands.disks.CommandDisks;
import top.cmarco.systeminfo.commands.gpu.CommandGpu;
import top.cmarco.systeminfo.commands.htop.CommandHtop;
import top.cmarco.systeminfo.commands.java.CommandJava;
import top.cmarco.systeminfo.commands.lscpu.CommandLscpu;
import top.cmarco.systeminfo.commands.neofetch.CommandNeofetch;
import top.cmarco.systeminfo.commands.network.CommandNetwork;
import top.cmarco.systeminfo.commands.sensors.CommandSensors;
import top.cmarco.systeminfo.commands.speedtest.CommandSpeedtest;
import top.cmarco.systeminfo.commands.systeminfo.CommandSystemInfo;
import top.cmarco.systeminfo.commands.uptime.CommandUptime;
import top.cmarco.systeminfo.commands.vmstat.CommandVmstat;

/**
 * An enum that represents different types of system information commands in your Spigot plugin.
 * <p></p>
 * Each enum constant represents a specific command type along with its display name and associated command class.
 * These command types are used to categorize and identify different system information commands.
 *
 * @version 1.0
 * @since Date or Version
 */
public enum CommandType {
    CPU_LOAD("CPU Load", CommandCpuLoad.class),
    DEVICES("Devices", CommandDevices.class),
    DISKS("Disks", CommandDisks.class),
    HTOP("Htop", CommandHtop.class),
    NETWORK("Network", CommandNetwork.class),
    LSCPU("LsCPU", CommandLscpu.class),
    SENSORS("Sensors", CommandSensors.class),
    SPEEDTEST("Speedtest", CommandSpeedtest.class),
    SYSTEM_INFO("System Info", CommandSystemInfo.class),
    UPTIME("Uptime", CommandUptime.class),
    VMSTAT("VmStat", CommandVmstat.class),
    JAVA("Java", CommandJava.class),
    GPU("Gpu", CommandGpu.class),
    NEOFETCH("Neofetch", CommandNeofetch.class),
    ;

    private final String displayName;
    private final Class<? extends SystemInfoCommand> clazz;

    /**
     * Constructs a `CommandType` with the specified display name and associated command class.
     *
     * @param displayName The display name of the command type.
     * @param clazz       The class representing the associated system information command.
     */
    CommandType(@NotNull String displayName, @NotNull Class<? extends SystemInfoCommand> clazz) {
        this.displayName = displayName;
        this.clazz = clazz;
    }

    /**
     * Get the display name of the command type.
     *
     * @return The display name.
     */
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the class representing the associated system information command.
     *
     * @return The command class.
     */
    @NotNull
    public Class<? extends SystemInfoCommand> getClazz() {
        return clazz;
    }
}
