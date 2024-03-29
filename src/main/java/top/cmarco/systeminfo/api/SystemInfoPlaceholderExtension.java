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

package top.cmarco.systeminfo.api;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.plugin.SystemInfo;
import top.cmarco.systeminfo.protocol.BukkitNetworkingManager;
import top.cmarco.systeminfo.utils.Utils;

/**
 * The `SystemInfoPlaceholderExtension` class is an extension for PlaceholderAPI that provides placeholders
 * related to system information. These placeholders can be used in plugins to dynamically display system
 * information to players.
 */
public final class SystemInfoPlaceholderExtension extends PlaceholderExpansion {

    private final SystemInfo systemInfo;

    /**
     * Initializes a new instance of the `SystemInfoPlaceholderExtension` class.
     *
     * @param systemInfo The `SystemInfo` instance associated with this extension.
     */
    public SystemInfoPlaceholderExtension(@NotNull SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
    }

    /**
     * Indicates whether this extension can be registered with PlaceholderAPI.
     *
     * @return True if the extension can be registered; otherwise, false.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * Indicates whether the placeholders provided by this extension should persist across server restarts.
     *
     * @return True if placeholders should persist; otherwise, false.
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Gets the identifier for this extension, which is used to reference its placeholders.
     *
     * @return The identifier for this extension.
     */
    @Override
    public @NotNull String getIdentifier() {
        return "systeminfo";
    }

    /**
     * Gets the author of this extension.
     *
     * @return The author of this extension.
     */
    @Override
    public @NotNull String getAuthor() {
        return "TheViperShow";
    }

    /**
     * Gets the version of this extension.
     *
     * @return The version of this extension, retrieved from the associated `SystemInfo` instance.
     */
    @Override
    public @NotNull String getVersion() {
        return this.systemInfo.getDescription().getVersion();
    }

    /**
     * Handles requests for specific placeholders and returns the corresponding system information.
     *
     * @param p      The offline player for whom the placeholder is being requested (not used in this context).
     * @param params The name of the requested placeholder.
     * @return The value of the requested placeholder, or null if the placeholder name is not recognized.
     */
    @Override
    public String onRequest(@NotNull final OfflinePlayer p, @NotNull final String params) {
        final SystemValues values = systemInfo.getSystemValues();
        final BukkitNetworkingManager networkingManager = systemInfo.getNetworkingManager();
        final boolean networkingEnabled = networkingManager != null;

        switch (params) {
            case "packet_count_sent_total":
                return networkingEnabled ? Long.toString(networkingManager.getTotalSentPackets()) : "Unavailable";
            case "packet_count_recv_total":
                return networkingEnabled ? Long.toString(networkingManager.getTotalReceivedPackets()) : "Unavailable";
            case "packet_data_sent_total":
                return networkingEnabled ? Utils.formatData(networkingManager.getTotalSentBytes()) : "Unavailable";
            case "packet_data_recv_total":
                return networkingEnabled ? Utils.formatData(networkingManager.getTotalReceivedBytes()) : "Unavailable";
            case "packet_count_sent_now":
                return networkingEnabled ? Long.toString(networkingManager.getLastSentPackets()) + "/s" : "Unavailable";
            case "packet_count_recv_now":
                return networkingEnabled ? Long.toString(networkingManager.getLastReceivedPackets()) + "/s" : "Unavailable";
            case "packet_data_sent_now":
                return networkingEnabled ? Utils.formatData(networkingManager.getLastSentBytes()) + "/s" : "Unavailable";
            case "packet_data_recv_now":
                return networkingEnabled ? Utils.formatData(networkingManager.getLastReceivedBytes()) + "/s" : "Unavailable";
            case "cpu-model":
                return String.format("%s %s", values.getCpuModel(), values.getCpuModelName());
            case "cpu-frequency":
                return values.getCpuMaxFrequency();
            case "cpu-temperature":
                return values.getCpuTemperature();
            case "cpu-load":
                return String.format("%.2f",values.getLastCpuLoad()) + "%";
            case "swap-max":
                return values.getTotalSwap();
            case "swap-used":
                return values.getUsedSwap();
            case "memory-max":
                return values.getMaxMemory();
            case "memory-available":
                return values.getAvailableMemory();
            case "memory-used":
                return values.getUsedMemory();
            case "processes":
                return Integer.toString(values.getRunningProcesses());
            default:
                return null;
        }
    }
}
