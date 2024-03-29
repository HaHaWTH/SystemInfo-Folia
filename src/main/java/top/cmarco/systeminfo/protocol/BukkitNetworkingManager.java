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

package top.cmarco.systeminfo.protocol;

import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import org.jetbrains.annotations.NotNull;
import top.cmarco.systeminfo.plugin.SystemInfo;

public final class BukkitNetworkingManager {

    private final SystemInfo plugin;
    private final EventManager eventManager;
    long lastSentPackets = -1L, lastReceivedPackets = -1L, lastSentBytes = -1L, lastReceivedBytes = -1L;
    long totalSentPackets = -1L, totalReceivedPackets = -1L, totalSentBytes = -1L, totalReceivedBytes = -1L;
    private long lastReset = -1L;

    /**
     * Constructs a new instance of BukkitNetworkingManager with the provided SystemInfo plugin.
     *
     * @param plugin The SystemInfo plugin instance.
     */
    public BukkitNetworkingManager(@NotNull final SystemInfo plugin) {
        this.plugin = plugin;
        this.eventManager = PacketEvents.getAPI().getEventManager();
    }

    /**
     * Starts the scheduler to reset packet count periodically.
     */
    private void startPacketCountResetScheduler() {
        final TaskScheduler scheduler = SystemInfo.getScheduler();
        scheduler.runTaskTimerAsynchronously(() -> {
            this.lastSentPackets = 0L;
            this.lastReceivedPackets = 0L;
            this.lastSentBytes = 0L;
            this.lastReceivedBytes = 0L;
            this.lastReset = System.currentTimeMillis();
        }, 20L, 20L);
    }

    /**
     * Loads packet listeners for both server and client packets.
     */
    public void loadPacketListeners() {
        lastSentPackets = 0L;
        lastReceivedPackets = 0L;
        totalSentPackets = 0L;
        totalReceivedPackets = 0L;
        this.startPacketCountResetScheduler();

        try {
            this.eventManager.registerListener(new StatsPacketListener(this), PacketListenerPriority.LOWEST);
        } catch (Exception illegalAccessException) {
            plugin.getLogger().warning("Error registering listener!");
            plugin.getLogger().warning(illegalAccessException.getLocalizedMessage());
        }
    }

    /* ---------------------- */

    /**
     * Get the most recent amount of sent packets in the interval of time.
     *
     * @return The most recent amount of sent packets.
     */
    public long getLastSentPackets() {
        return lastSentPackets;
    }

    /**
     * Get the most recent amount of received packets in the interval of time.
     *
     * @return The most recent amount of received packets.
     */
    public long getLastReceivedPackets() {
        return lastReceivedPackets;
    }

    /**
     * Get the total sent packets since the server has been start up.
     *
     * @return The total amount of sent packets.
     */
    public long getTotalSentPackets() {
        return totalSentPackets;
    }

    /**
     * Get the total received packets since the server has been start up.
     *
     * @return The total amount of received packets.
     */
    public long getTotalReceivedPackets() {
        return totalReceivedPackets;
    }

    /**
     * Returns the number of bytes last sent.
     *
     * @return The number of bytes last sent.
     */
    public long getLastSentBytes() {
        return lastSentBytes;
    }

    /**
     * Returns the number of bytes last received.
     *
     * @return The number of bytes last received.
     */
    public long getLastReceivedBytes() {
        return lastReceivedBytes;
    }

    /**
     * Returns the total number of bytes sent.
     *
     * @return The total number of bytes sent.
     */
    public long getTotalSentBytes() {
        return totalSentBytes;
    }

    /**
     * Returns the total number of bytes received.
     *
     * @return The total number of bytes received.
     */
    public long getTotalReceivedBytes() {
        return totalReceivedBytes;
    }

    /**
     * Get the last UNIX time the last sent & received packets counter has been reset.
     *
     * @return The last reset time.
     */
    public long getLastReset() {
        return lastReset;
    }

    /**
     * Get the NetworkStatsData currently generated by the Minecraft server
     * since the moment it started running.
     *
     * @return The available NetworkStatsData.
     */
    @NotNull
    public NetworkStatsData getNetworkStats() {
        return new NetworkStatsData.Builder()
                .lastReceivedBytes(getLastReceivedBytes())
                .lastSentBytes(getLastSentBytes())
                .totalReceivedBytes(getTotalReceivedBytes())
                .totalSentBytes(getTotalSentBytes())
                .lastSentPackets(getLastSentPackets())
                .lastReceivedPackets(getLastReceivedPackets())
                .totalReceivedPackets(getTotalReceivedPackets())
                .totalSentPackets(getTotalSentPackets())
                .lastReset(getLastReset())
                .build();
    }

    /**
     * Resets the current network stats of the server.
     *
     * @return The previous NetworkStatsData.
     */
    @NotNull
    public NetworkStatsData resetNetworkStats() {
        NetworkStatsData lastAvailable = this.getNetworkStats();
        this.lastReceivedBytes = 0;
        this.lastSentBytes = 0;
        this.lastReceivedPackets = 0;
        this.lastSentPackets = 0;
        this.totalReceivedBytes = 0;
        this.totalSentBytes = 0;
        this.totalReceivedPackets = 0;
        this.totalSentPackets = 0;
        this.lastReset = 0;
        return lastAvailable;
    }
}
