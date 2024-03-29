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

package top.cmarco.systeminfo.oshi;

import org.bukkit.Bukkit;
import org.bukkit.map.MinecraftFont;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import oshi.hardware.*;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.utils.Utils;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * The `SystemValues` class is responsible for retrieving and storing various system-related information using the OSHI library.
 * This information includes details about the operating system, hardware, CPU, memory, sensors, and more.
 */
public final class SystemValues {

    /**
     * Initializes a new instance of the `SystemValues` class with a provided logger.
     *
     * @param logger The logger to use for logging any errors or warnings during information retrieval.
     */
    public SystemValues(@NotNull Logger logger) {
        this.logger = logger;
    }

    private final Logger logger; // The logger for error and warning logging.

    // Variables to store system information
    private OperatingSystem operatingSystem;
    private HardwareAbstractionLayer hardwareAbstractionLayer;
    private CentralProcessor centralProcessor;
    private Sensors sensors;
    private GlobalMemory memory;
    private CentralProcessor.ProcessorIdentifier processorIdentifier;
    private VirtualMemory virtualMemory;
    private OperatingSystem.OSVersionInfo osVersionInfo;
    private NetworkIF serverNetworkInterface;
    private double lastCpuLoad = -1.00d;
    private long[] previousTicks;

    private static int CPU_CORES_CACHE = -1; // caching to improve performance.
    private static int CPU_THREADS_CACHE = -1; // caching to improve performance.
    private static long CPU_MAX_FREQ = -1L; // caching to improve performance.
    private static String CPU_STEPPING = null; // caching to improve performance.

    /**
     * A method to check for {@link SecurityException} and return object from supplier.
     * {@linkplain MinecraftFont#Font#getChar(char ch)}
     *
     * @param sup   The typed supplier.
     * @param error The error to print if SecurityException is thrown.
     * @param <T>   A generic type for the supplier.
     * @return The object provided by the supplier, or null if errors thrown.
     */
    private <T> T checkSecurityExc(@NotNull final Supplier<T> sup, @NotNull final String error) {
        try {
            return sup.get();
        } catch (final SecurityException exception) {
            logger.warning(error);
            logger.warning(exception.getLocalizedMessage());
        }
        return null;
    }

    /**
     * Updates the stored system information by querying various system-related data using the OSHI library.
     */
    public void updateValues() {
        oshi.SystemInfo systemInfo = checkSecurityExc(oshi.SystemInfo::new, "Could not create System Information instance. Plugin will not work properly!");

        if (systemInfo == null) {
            Bukkit.getPluginManager().disablePlugin(top.cmarco.systeminfo.plugin.SystemInfo.INSTANCE);
            return;
        }

        operatingSystem = checkSecurityExc(systemInfo::getOperatingSystem, "Could not obtain OS info");
        hardwareAbstractionLayer = checkSecurityExc(systemInfo::getHardware, "Could not obtain HAL info");

        if (hardwareAbstractionLayer == null) {
            logger.warning("The hardware abstraction layer could not be obtained, many features will not work properly!");
            return;
        }

        centralProcessor = checkSecurityExc(hardwareAbstractionLayer::getProcessor, "Could not obtain CPU info");
        sensors = checkSecurityExc(hardwareAbstractionLayer::getSensors, "Could not obtain sensors info");
        memory = checkSecurityExc(hardwareAbstractionLayer::getMemory, "Could not obtain memory info");
        processorIdentifier = checkSecurityExc(centralProcessor::getProcessorIdentifier, "Could not obtain processor identifier");
        virtualMemory = checkSecurityExc(memory::getVirtualMemory, "Could not obtain virtual memory info");
        osVersionInfo = checkSecurityExc(operatingSystem::getVersionInfo, "Could not obtain OS Version info");

        List<NetworkIF> loadedNetworkIFs = checkSecurityExc(hardwareAbstractionLayer::getNetworkIFs, "Could not obtain network interfaces.");

        if (loadedNetworkIFs == null) {
            return;
        }

        Optional<NetworkIF> firstInterfaceOptional = loadedNetworkIFs.stream().findFirst();
        serverNetworkInterface = firstInterfaceOptional.orElseThrow(RuntimeException::new);
    }

    /**
     * @return Returns the formatted value of the system current maximum memory
     */
    @NotNull
    public String getMaxMemory() {
        return Utils.formatData(memory.getTotal());
    }

    /**
     * @return Returns the value of the system current maximum memory in bytes.
     */
    public long getMaxMemory2() {
        return memory.getTotal();
    }

    /**
     * @return Returns the formatted value of the system currently available memory.
     */
    @NotNull
    public String getAvailableMemory() {
        return Utils.formatData(memory.getAvailable());
    }

    /**
     * @return Returns the formatted value of the system currently used memory.
     */
    @NotNull
    public String getUsedMemory() {
        return Utils.formatData(memory.getTotal() - memory.getAvailable());
    }

    /**
     * @return Returns the formatted value of the system current maximum swap memory.
     */
    @NotNull
    public String getTotalSwap() {
        return Utils.formatData(virtualMemory.getSwapTotal());
    }

    /**
     * @return Returns the formatted value of the system currently used swap memory.
     */
    @NotNull
    public String getUsedSwap() {
        return Utils.formatData(virtualMemory.getSwapUsed());
    }

    /**
     * @return Returns the cpu voltage with unit of measure if available, else return "Unavailable".
     */
    @NotNull
    public String getCpuVoltage() {
        return sensors.getCpuVoltage() != 0 ? sensors.getCpuVoltage() + "V" : "Unavailable";
    }

    /**
     * @return Returns the cpu temperature with unit of measure if available, else return "Unavailable".
     */
    @NotNull
    public String getCpuTemperature() {
        return sensors.getCpuTemperature() != 0f ? String.format("%.1f", sensors.getCpuTemperature()) + "C°" : "Unavailable";
    }

    /**
     * @return Returns the cpu temperature with unit of measure and status, else return "Unavailable"
     * 0.0-50.0 is "Idle" status
     * 50.0-75.0 is "Load" status
     * 75.0-90.0 is "Overload" status
     * else is "Not available"
     */
    @NotNull
    public String getCpuTemperatureStatus() {
        double degrees = sensors.getCpuTemperature();
        if (degrees > 0.0) {
            if (degrees <= 50.0) {
                return (String.format("&a%.1fC° &2Idle", degrees));
            } else if (degrees <= 75.0) {
                return (String.format("&6%.1fC° &6Load", degrees));
            } else if (degrees <= 90.0) {
                return (String.format("&c%.1fC° &cOverload", degrees));
            }
        }
        return "&cNot available";
    }

    /**
     * Get the kernel version of this operating system.
     *
     * @return The kernel version.
     */
    @NotNull
    public String getKernelVersion() {
        return operatingSystem.getVersionInfo().getBuildNumber();
    }

    /**
     * @return Returns fans speed in RPM separated by a blank space.
     */
    @NotNull
    public String getFansRPM() {
        StringBuilder rpm = new StringBuilder();
        int[] speeds = sensors.getFanSpeeds();
        for (int speed : speeds)
            rpm.append(speed).append(" ");
        return rpm.toString();
    }

    /**
     * @return Get the current operating system family name.
     */
    @NotNull
    public String getOSFamily() {
        return operatingSystem.getFamily();
    }

    /**
     * @return Get the current operating system manufacturer name.
     */
    @NotNull
    public String getOSManufacturer() {
        return operatingSystem.getManufacturer();
    }

    /**
     * @return Get the current operating system version name.
     */
    @NotNull
    public String getOSVersion() {
        return osVersionInfo.getVersion();
    }

    /**
     * @return Get the name of the current cpu vendor.
     */
    @NotNull
    public String getCpuVendor() {
        return processorIdentifier.getVendor();
    }

    /**
     * @return Get the model of the current cpu.
     */
    @NotNull
    public String getCpuModel() {
        return processorIdentifier.getModel();
    }

    /**
     * @return Get the model name of the current cpu.
     */
    @NotNull
    public String getCpuModelName() {
        return processorIdentifier.getName();
    }

    /**
     * @return Get the max standard frequency of the current CPU with 2 decimals in GHz.
     */
    @NotNull
    public String getCpuMaxFrequency() {
        if (CPU_MAX_FREQ == -1L) {
            CPU_MAX_FREQ = centralProcessor.getMaxFreq();
        }

        return String.format("%.2f", CPU_MAX_FREQ / 1E9);
    }

    /**
     * @return The CPU stepping name.
     */
    @NotNull
    public String getCpuStepping() {
        if (CPU_STEPPING == null) {
            CPU_STEPPING = processorIdentifier.getStepping();
        }

        return CPU_STEPPING;
    }

    /**
     * @return Get the amount of physical cores in the CPU
     */
    @NotNull
    public String getCpuCores() {

        if (CPU_CORES_CACHE == -1) {
            CPU_CORES_CACHE = centralProcessor.getPhysicalProcessorCount();
        }

        return String.valueOf(CPU_CORES_CACHE);
    }

    /**
     * @return Get the amount of logical cores in the CPU
     */
    @NotNull
    public String getCpuThreads() {

        if (CPU_THREADS_CACHE == -1) {
            CPU_THREADS_CACHE = centralProcessor.getLogicalProcessorCount();
        }

        return String.valueOf(CPU_THREADS_CACHE);
    }

    /**
     * @return returns an array of processes
     */
    @NotNull
    public List<OSProcess> getOSProcesses() {
        return operatingSystem.getProcesses();
    }

    /**
     * @return get the amount of total active processes
     */
    public int getRunningProcesses() {
        return operatingSystem.getProcessCount();
    }

    /**
     * @return get the amount of total threads running
     */
    public int getThreadCount() {
        return operatingSystem.getThreadCount();
    }

    /**
     * @return get an array of HWDiskStore which represents a NVM
     *
     * @see <a href=https://en.wikipedia.org/wiki/Non-volatile_memory>"Wikipedia Non-volatile memory</a>
     */
    @NotNull
    public List<HWDiskStore> getDiskStores() {
        return hardwareAbstractionLayer.getDiskStores();
    }

    /**
     * @return get an array of USB devices attached to the machine
     * @see <a href=https://en.wikipedia.org/wiki/USB>Wikipedia USB</a>
     */
    @NotNull
    public List<UsbDevice> getUsbDevices() {
        return hardwareAbstractionLayer.getUsbDevices(true);
    }

    /**
     * Get all the GPUs currently available in this machine.
     *
     * @return A list with all the available GPUs, represented by {@link GraphicsCard}.
     *
     * @see <a href=https://en.wikipedia.org/wiki/GPU>Wikipedia GPU</a>
     */
    @NotNull
    public List<GraphicsCard> getGPUs() {
        return hardwareAbstractionLayer.getGraphicsCards();
    }

    /**
     * Gets the main GPU used by this system.
     *
     * @return The system main GPU.
     */
    @NotNull
    public GraphicsCard getMainGPU() {
        return hardwareAbstractionLayer.getGraphicsCards().get(0x00);
    }

    /**
     * @return Get the system cpu load ticks
     */
    public long[] getSystemCpuLoadTicks() {
        return centralProcessor.getSystemCpuLoadTicks();
    }

    /**
     * @return get the processor cpu load ticks
     */
    public long[][] getProcessorCpuLoadTicks() {
        return centralProcessor.getProcessorCpuLoadTicks();
    }

    /**
     * @param oldTicks this is the previous value which will be compared to the current
     * @return this will return a double value representing the current average processor load
     */
    public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
        return centralProcessor.getSystemCpuLoadBetweenTicks(oldTicks);
    }

    /**
     * @param oldTicks this is the previous value which will be compared to the current
     * @return returns an array of values which represent a load for each processing unit of the CPU.
     */
    public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
        return centralProcessor.getProcessorCpuLoadBetweenTicks(oldTicks);
    }

    /**
     * Set the last CPU load.
     *
     * @param lastCpuLoad The CPU Load.
     */
    public void setLastCpuLoad(@Range(from = 0, to = Integer.MAX_VALUE) final double lastCpuLoad) {
        this.lastCpuLoad = lastCpuLoad;
    }

    /**
     * Get the most recent CPU load.
     *
     * @return The CPU load.
     */
    public double getLastCpuLoad() {
        return lastCpuLoad;
    }

    /**
     * Start a task that automatically updates the global CPU load.
     */
    public void startUpdateCpuLoadTask() {
        if (top.cmarco.systeminfo.plugin.SystemInfo.INSTANCE == null) {
            return;
        }

        SystemInfo.getScheduler()
                .runTaskTimerAsynchronously(() -> {
                    if (previousTicks != null) {
                        lastCpuLoad = SystemInfo.INSTANCE.getSystemValues().getSystemCpuLoadBetweenTicks(previousTicks) * 100;
                    }
                    previousTicks = SystemInfo.INSTANCE.getSystemValues().getSystemCpuLoadTicks();
                }, 20L, 20L);
    }

    /**
     * Get the Network Interface bound to the TCP port used by the CraftBukkit server.
     *
     * @return The Network Interface
     */
    @NotNull
    public NetworkIF getServerConnection() {
        return serverNetworkInterface;
    }

    /**
     * Get the received queue for the minecraft server.
     *
     * @return The receive queue.
     */
    public long getBytesRecv() {
        return serverNetworkInterface.getBytesRecv();
    }

    /**
     * Get the transmit queue for the minecraft server.
     *
     * @return The transmit queue.
     */
    public long getBytesSent() {
        return serverNetworkInterface.getBytesSent();
    }

    /**
     * Get the current speed of this network interface.
     *
     * @return The speed.
     */
    public long getSpeed() {
        return serverNetworkInterface.getSpeed();
    }

    /**
     * Get the IPv4 address of the current network interface.
     *
     * @return Get the address.
     */
    public String getIPv4Address() {
        return String.join(".", serverNetworkInterface.getIPv4addr());
    }

    /**
     * Get the name of the network interface.
     *
     * @return The name of the network interface.
     */
    public String getNetworkInterfaceName() {
        return serverNetworkInterface.getName();
    }
}
