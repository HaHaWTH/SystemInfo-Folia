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

package top.cmarco.systeminfo.plugin;

import com.github.Anon8281.universalScheduler.UniversalScheduler;
import com.github.Anon8281.universalScheduler.scheduling.schedulers.TaskScheduler;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import top.cmarco.systeminfo.api.SystemInfoPlaceholderExtension;
import top.cmarco.systeminfo.commands.register.CommandManager;
import top.cmarco.systeminfo.config.SystemInfoConfig;
import top.cmarco.systeminfo.gui.GuiClickListener;
import top.cmarco.systeminfo.gui.SystemInfoGui;
import top.cmarco.systeminfo.libraries.LibraryManager;
import top.cmarco.systeminfo.oshi.SystemValues;
import top.cmarco.systeminfo.protocol.BukkitNetworkingManager;
import top.cmarco.systeminfo.utils.Utils;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * The main class of the SystemInfo Spigot plugin, responsible for initializing and managing the plugin's features.
 * This plugin provides information about the server's system and hardware.
 */
public final class SystemInfo extends JavaPlugin {

    public static SystemInfo INSTANCE = null;
    private final LocalDateTime startupTime = LocalDateTime.now(); // Timestamp when the plugin was loaded.
    private final PluginManager pluginManager = Bukkit.getPluginManager(); // The Spigot PluginManager instance.
    private CommandManager commandManager; // The custom CommandManager for handling plugin commands.
    private SystemInfoPlaceholderExtension systemInfoExtension; // PlaceholderAPI extension for custom placeholders.
    private SystemValues systemValues; // Manager for system information values.
    private SystemInfoGui systemInfoGui; // Graphical User Interface for the plugin.
    private LibraryManager libraryManager; // Download and load dependencies.
    private SystemInfoConfig systemInfoConfig; // YAML configuration manager.
    public static BukkitNetworkingManager networkingManager;
    private static TaskScheduler taskScheduler;
    public static TaskScheduler getScheduler() {
        return taskScheduler;
    }
    /**
     * Checks if the bukkit version contains OSHI.
     *
     * @param plugin The Plugin.
     * @return True if at least Bukkit 1.17.
     */
    private static boolean checkVersion(@NotNull Plugin plugin) {
        final String[] hasVersionOshi = {"1.17", "1.18", "1.19", "1.20", "1.21"};
        final String currentVer = plugin.getServer().getBukkitVersion();
        return Arrays.stream(hasVersionOshi).noneMatch(currentVer::contains);
    }

    /**
     * Called when the plugin is enabled. It initializes various components and registers listeners.
     */
    @Override
    public void onEnable() {
        INSTANCE = this;
        taskScheduler = UniversalScheduler.getScheduler(this);
        setupConfig();
        loadDependencies();
        setupPacketEvents();
        loadValues();
        loadCommands();
        loadGui();
        loadAPI();
        registerListener();
        setupConfig();
        setupMetrics();
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().getSettings()
                .reEncodeByDefault(true)
                .checkForUpdates(false)
                .bStats(false);
        PacketEvents.getAPI().load();
    }

    @Override
    public void onDisable() {
        final boolean hasPacketEvents = Bukkit.getPluginManager().getPlugin("packetevents") != null;
        if (!hasPacketEvents) {
            return;
        }

        PacketEvents.getAPI().terminate();
    }

    /**
     * Set up behaviour when ProtocolLib is present.
     * The method will do nothing is the soft-dependency is absent.
     */
    private void setupPacketEvents() {
        networkingManager = new BukkitNetworkingManager(this);
        networkingManager.loadPacketListeners();
    }

    private void setupMetrics() {
        Metrics metrics = new Metrics(this, 5610);
    }

    private void setupConfig() {
        this.systemInfoConfig = new SystemInfoConfig(this);
    }

    /**
     * Loads the dependencies necessary for this plugin at runtime.
     * Loads: oshi-core and jspeedtest.
     */
    private void loadDependencies() {
        this.libraryManager = new LibraryManager(this);
        this.libraryManager.loadSpeedtestLibraries();

        if (checkVersion(this)) {
            this.libraryManager.loadOshiLibraries();
        }
    }

    /**
     * Initializes and loads the SystemInfoGui.
     */
    private void loadGui() {
        this.systemInfoGui = new SystemInfoGui(this);
    }

    /**
     * Initializes and updates the SystemValues.
     */
    private void loadValues() {
        systemValues = new SystemValues(getLogger());
        systemValues.updateValues();
        systemValues.startUpdateCpuLoadTask();
    }

    /**
     * Initializes and registers the PlaceholderAPI extension if PlaceholderAPI is available.
     * Otherwise, logs a warning message.
     */
    private void loadAPI() {
        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            systemInfoExtension = new SystemInfoPlaceholderExtension(this);
            systemInfoExtension.register();
            this.getLogger().info("Successfully Registered SystemInfo with PlaceholderAPI.");
        } else {
            this.getLogger().info("Could not find PlaceholderAPI, placeholders won't be available.");
        }
    }

    /**
     * Initializes and registers custom commands using the CommandManager.
     * If there's an issue with accessing the command map, the plugin will be disabled.
     */
    private void loadCommands() {
        try {
            commandManager = new CommandManager(Utils.getCommandMap(), this);
            commandManager.createInstances();
            commandManager.registerAll();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            this.getLogger().warning("Could not get command map for SystemInfo!");
            this.getLogger().warning("Disabling plugin . . .");
            pluginManager.disablePlugin(this);
        }
    }

    /**
     * Registers the GuiClickListener as a listener to handle GUI interactions.
     */
    private void registerListener() {
        pluginManager.registerEvents(new GuiClickListener(), this);
    }

    // Getters for class members

    /**
     * @return Gets the timestamp this plugin was loaded at.
     */
    @NotNull
    public LocalDateTime getStartupTime() {
        return startupTime;
    }

    /**
     * @return Gets the server plugin manager.
     */
    @NotNull
    public PluginManager getPluginManager() {
        return pluginManager;
    }

    /**
     * @return Gets the server command manager.
     */
    @NotNull
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * @return Gets the PlaceholderAPI extension.
     */
    @NotNull
    public SystemInfoPlaceholderExtension getSystemInfoExtension() {
        return systemInfoExtension;
    }

    /**
     * @return Gets the system values class.
     */
    @NotNull
    public SystemValues getSystemValues() {
        return systemValues;
    }

    /**
     * @return Gets the system info gui class.
     */
    @NotNull
    public SystemInfoGui getSystemInfoGui() {
        return systemInfoGui;
    }

    /**
     * @return Gets the mail config used by system info.
     */
    @NotNull
    public SystemInfoConfig getSystemInfoConfig() {
        return systemInfoConfig;
    }

    /**
     * Get the Bukkit server networking manager class of this plugin.
     * Can be null when ProtocolLib dependency is missing.
     *
     * @return The Bukkit networking manager.
     */
    @Nullable
    public BukkitNetworkingManager getNetworkingManager() {
        return networkingManager;
    }
}
